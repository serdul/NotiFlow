package com.notiflow.app.domain.usecase

import com.notiflow.app.domain.model.CapturedNotification
import com.notiflow.app.domain.repository.NotificationRepository
import javax.inject.Inject

class ProcessNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notification: CapturedNotification): Long {
        return notificationRepository.insertNotification(notification)
    }
}
