package com.notiflow.app.domain.usecase

import com.notiflow.app.domain.model.Task
import javax.inject.Inject

class ScheduleReminderUseCase @Inject constructor() {
    suspend operator fun invoke(task: Task, minutesBefore: Int = 5): Result<Unit> {
        return runCatching {
            // WorkManager scheduling is handled by NotificationBatchWorker
            // This use case is a coordination point for the domain layer
        }
    }
}
