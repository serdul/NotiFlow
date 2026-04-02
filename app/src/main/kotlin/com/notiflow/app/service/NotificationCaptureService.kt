package com.notiflow.app.service

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.work.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.notiflow.app.data.local.database.NotiFlowDatabase
import com.notiflow.app.data.local.entities.CapturedNotificationEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class NotificationCaptureService : NotificationListenerService() {

    @Inject lateinit var database: NotiFlowDatabase

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val pendingQueue = mutableListOf<CapturedNotificationEntity>()
    private var batchJob: Job? = null
    private val gson = Gson()

    private var batchIntervalMs = 90_000L
    private var batchMaxSize = 10
    private var monitoredPackages: Set<String> = setOf(
        "com.whatsapp", "com.whatsapp.w4b", "org.telegram.messenger",
        "com.google.android.gm", "com.facebook.orca",
        "com.microsoft.teams", "com.microsoft.office.outlook"
    )
    private var monitoredGroups: Set<String> = emptySet()

    override fun onCreate() {
        super.onCreate()
        Timber.d("NotificationCaptureService created")
        loadSettings()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Timber.d("NotificationCaptureService destroyed")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        if (packageName == "com.notiflow.app") return
        if (packageName !in monitoredPackages) return

        val extras = sbn.notification?.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()
        val messageText = bigText?.takeIf { it.isNotBlank() } ?: text

        if (title.isBlank() && messageText.isBlank()) return

        val isGroupMessage = !subText.isNullOrBlank()
        val groupName = if (isGroupMessage) subText else null
        val isMonitored = !isGroupMessage || (groupName != null && groupName in monitoredGroups) || monitoredGroups.isEmpty()

        val appLabel = runCatching {
            packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(packageName, 0)
            ).toString()
        }.getOrDefault(packageName)

        val entity = CapturedNotificationEntity(
            packageName = packageName,
            appLabel = appLabel,
            title = title,
            messageText = messageText,
            groupName = groupName,
            isGroupMessage = isGroupMessage,
            isMonitored = isMonitored,
            timestamp = System.currentTimeMillis(),
            processingStatus = "PENDING"
        )

        serviceScope.launch {
            synchronized(pendingQueue) { pendingQueue.add(entity) }
            scheduleBatch()
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Capture already happened in onNotificationPosted — nothing to do here.
    }

    private fun scheduleBatch() {
        val queueSize = synchronized(pendingQueue) { pendingQueue.size }
        if (queueSize >= batchMaxSize) {
            batchJob?.cancel()
            batchJob = serviceScope.launch { processBatch() }
            return
        }
        batchJob?.cancel()
        batchJob = serviceScope.launch {
            delay(batchIntervalMs)
            processBatch()
        }
    }

    private suspend fun processBatch() {
        val batch: List<CapturedNotificationEntity>
        synchronized(pendingQueue) {
            if (pendingQueue.isEmpty()) return
            batch = pendingQueue.toList()
            pendingQueue.clear()
        }

        val batchId = UUID.randomUUID().toString()
        Timber.d("Processing batch $batchId with ${batch.size} notifications")

        val dao = database.capturedNotificationDao()
        batch.forEach { entity ->
            val id = dao.insert(entity.copy(batchId = batchId, processingStatus = "PENDING"))
            Timber.d("Saved notification id=$id to batch $batchId")
        }

        val workRequest = OneTimeWorkRequestBuilder<NotificationBatchWorker>()
            .setInputData(workDataOf("batchId" to batchId))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(applicationContext).enqueue(workRequest)
        Timber.d("Enqueued NotificationBatchWorker for batch $batchId")
    }

    private fun loadSettings() {
        serviceScope.launch {
            try {
                val settings = database.userSettingsDao().getOnce()
                if (settings != null) {
                    batchIntervalMs = settings.batchIntervalSeconds * 1000L
                    batchMaxSize = settings.batchMaxSize
                    val type = object : TypeToken<List<String>>() {}.type
                    monitoredPackages = gson.fromJson<List<String>>(settings.monitoredApps, type).toSet()
                    monitoredGroups = gson.fromJson<List<String>>(settings.monitoredGroups, type).toSet()
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load settings")
            }
        }
    }

    companion object {
        fun isNotificationServiceEnabled(context: Context): Boolean {
            val flat = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            ) ?: return false
            return flat.contains(context.packageName)
        }

        fun getNotificationAccessIntent(): Intent =
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
    }
}
