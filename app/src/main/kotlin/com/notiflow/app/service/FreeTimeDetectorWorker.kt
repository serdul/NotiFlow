package com.notiflow.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.notiflow.app.data.local.database.NotiFlowDatabase
import com.notiflow.app.domain.usecase.DetectFreeTimeSlotsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.time.LocalDate

@HiltWorker
class FreeTimeDetectorWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val detectFreeTimeSlotsUseCase: DetectFreeTimeSlotsUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val today = LocalDate.now()
            val slots = detectFreeTimeSlotsUseCase(today)
            Timber.d("Detected ${slots.size} free time slots for today")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Failed to detect free time slots")
            Result.failure()
        }
    }
}
