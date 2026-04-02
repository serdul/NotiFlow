package com.notiflow.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.notiflow.app.data.local.database.NotiFlowDatabase
import com.notiflow.app.data.local.entities.CategoryEntity
import com.notiflow.app.data.local.entities.EventEntity
import com.notiflow.app.data.local.entities.ReminderEntity
import com.notiflow.app.data.local.entities.TaskEntity
import com.notiflow.app.data.mappers.toDomain
import com.notiflow.app.data.remote.ai.AiProviderFactory
import com.notiflow.app.data.remote.ai.UserContext
import com.notiflow.app.domain.model.AiProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.TimeZone

@HiltWorker
class NotificationBatchWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: NotiFlowDatabase,
    private val aiProviderFactory: AiProviderFactory
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val batchId = inputData.getString("batchId")
            ?: return Result.failure()

        Timber.d("NotificationBatchWorker started for batch: $batchId")

        return try {
            val notifications = database.capturedNotificationDao().getByBatchId(batchId)
            val monitoredNotifications = notifications.filter { it.isMonitored }

            if (monitoredNotifications.isEmpty()) {
                Timber.d("No monitored notifications in batch $batchId")
                notifications.forEach {
                    database.capturedNotificationDao().updateStatus(it.id, "IGNORED", batchId)
                }
                return Result.success()
            }

            val settings = database.userSettingsDao().getOnce()
            val categoriesList = database.categoryDao().getAll().first()

            val zone = TimeZone.getDefault()
            val userContext = UserContext(
                profession = settings?.profession,
                categories = categoriesList.map { it.name },
                currentDateTime = java.time.LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                timezone = zone.id
            )

            val provider = aiProviderFactory.getProvider(
                runCatching { AiProvider.valueOf(settings?.aiProviderPreference ?: "GROQ") }
                    .getOrDefault(AiProvider.GROQ)
            )

            val domainNotifications = monitoredNotifications.map { it.toDomain() }
            val domainCategories = categoriesList.map { it.toDomain() }
            val result = provider.processNotificationBatch(domainNotifications, userContext, domainCategories)

            val zoneId = ZoneId.systemDefault()
            val now = System.currentTimeMillis()

            // Save tasks
            for (extractedTask in result.tasks) {
                val dueMs = extractedTask.dueDate?.let { parseDate(it, zoneId) }
                val dueTimeMs = extractedTask.dueTime?.let { parseTime(it) }

                val categoryId = extractedTask.categoryName?.let { catName ->
                    categoriesList.find { it.name.equals(catName, ignoreCase = true) }?.id
                        ?: run {
                            val newCat = CategoryEntity(
                                name = catName,
                                iconName = extractedTask.categoryIcon ?: "label",
                                isAiGenerated = true
                            )
                            database.categoryDao().insert(newCat)
                        }
                }

                val taskId = database.taskDao().insert(
                    TaskEntity(
                        title = extractedTask.title,
                        description = extractedTask.description,
                        dueDate = dueMs,
                        dueTime = dueTimeMs,
                        priority = extractedTask.priority,
                        categoryId = categoryId,
                        assignedPerson = extractedTask.assignedPerson,
                        status = "PENDING",
                        isRecurring = extractedTask.isRecurring,
                        sourceNotificationId = monitoredNotifications.getOrNull(extractedTask.sourceNotificationIndex)?.id,
                        createdAt = now,
                        updatedAt = now
                    )
                )

                if (dueMs != null) {
                    val reminderTime = dueMs - ((settings?.defaultReminderMinutes ?: 5) * 60_000L)
                    if (reminderTime > now) {
                        database.reminderDao().insert(
                            ReminderEntity(
                                taskId = taskId,
                                triggerTime = reminderTime,
                                minutesBefore = settings?.defaultReminderMinutes ?: 5
                            )
                        )
                    }
                }
            }

            // Save events
            for (extractedEvent in result.events) {
                val startMs = parseDate(extractedEvent.startDate, zoneId) ?: now
                val endMs = extractedEvent.endTime?.let { combineDateTime(extractedEvent.startDate, it, zoneId) }
                database.eventDao().insert(
                    EventEntity(
                        title = extractedEvent.title,
                        description = extractedEvent.description,
                        startTime = startMs,
                        endTime = endMs,
                        isAllDay = extractedEvent.isAllDay,
                        location = extractedEvent.location,
                        isRecurring = extractedEvent.isRecurring,
                        createdAt = now
                    )
                )
            }

            // Save flagged items as tasks
            for (flagged in result.flaggedItems) {
                database.taskDao().insert(
                    TaskEntity(
                        title = flagged.partialTitle,
                        status = "PENDING",
                        isAiFlagged = true,
                        flagReason = flagged.flagReason,
                        sourceNotificationId = monitoredNotifications.getOrNull(flagged.sourceNotificationIndex)?.id,
                        createdAt = now,
                        updatedAt = now
                    )
                )
            }

            // Save new categories from suggestions
            for (suggested in result.suggestedCategories) {
                val existing = database.categoryDao().getByName(suggested.name)
                if (existing == null) {
                    database.categoryDao().insert(
                        CategoryEntity(
                            name = suggested.name,
                            iconName = suggested.iconName,
                            colorHex = suggested.colorHex,
                            isAiGenerated = true
                        )
                    )
                }
            }

            // Mark all notifications as processed
            notifications.forEach {
                database.capturedNotificationDao().updateStatus(it.id, "PROCESSED", batchId)
            }

            Timber.d("Batch $batchId processed: ${result.tasks.size} tasks, ${result.events.size} events, ${result.flaggedItems.size} flagged")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Failed to process batch $batchId")
            Result.retry()
        }
    }

    private fun parseDate(dateStr: String, zoneId: ZoneId): Long? {
        return try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
                .atStartOfDay(zoneId).toInstant().toEpochMilli()
        } catch (e: DateTimeParseException) {
            null
        }
    }

    private fun combineDateTime(dateStr: String, timeStr: String, zoneId: ZoneId): Long? {
        return try {
            val date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
            val parts = timeStr.split(":")
            val hours = parts[0].toInt()
            val minutes = parts.getOrNull(1)?.toInt() ?: 0
            date.atTime(hours, minutes).atZone(zoneId).toInstant().toEpochMilli()
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse datetime: date=$dateStr time=$timeStr")
            null
        }
    }
}
