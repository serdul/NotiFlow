package com.notiflow.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Timber.d("Device booted — scheduling periodic workers")
            val periodicWork = PeriodicWorkRequestBuilder<FreeTimeDetectorWorker>(
                1, TimeUnit.HOURS
            ).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "free_time_detector",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWork
            )
        }
    }
}
