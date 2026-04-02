package com.notiflow.app.domain.usecase

import com.notiflow.app.domain.model.CapturedNotification
import com.notiflow.app.domain.repository.NotificationRepository
import javax.inject.Inject

class ExtractTasksFromBatchUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(batchId: String): List<CapturedNotification> {
        return notificationRepository.getNotificationsByBatchId(batchId)
            .filter { it.isMonitored }
    }
}
