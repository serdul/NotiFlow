package com.notiflow.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.notiflow.app.data.local.database.NotiFlowDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: NotiFlowDatabase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val reminderId = inputData.getLong("reminderId", -1L)
        if (reminderId == -1L) return Result.failure()

        return try {
            val reminder = database.reminderDao().getById(reminderId)
                ?: return Result.failure()
            val task = reminder.taskId?.let { database.taskDao().getById(it) }

            showReminderNotification(task?.title ?: "Task Reminder")
            database.reminderDao().update(reminder.copy(isDelivered = true))
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Failed to deliver reminder $reminderId")
            Result.failure()
        }
    }

    private fun showReminderNotification(title: String) {
        val channelId = "notiflow_reminders"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val channel = NotificationChannel(
            channelId, "Reminders", NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Task and event reminders" }
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Reminder: $title")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
