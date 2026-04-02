package com.notiflow.app.domain.repository

import com.notiflow.app.domain.model.CapturedNotification
import com.notiflow.app.domain.model.ProcessingStatus
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getPendingNotifications(): Flow<List<CapturedNotification>>
    suspend fun getNotificationsByBatchId(batchId: String): List<CapturedNotification>
    suspend fun insertNotification(notification: CapturedNotification): Long
    suspend fun updateProcessingStatus(id: Long, status: ProcessingStatus, batchId: String? = null)
    suspend fun deleteOlderThan(timestampMs: Long)
    fun getAllNotifications(): Flow<List<CapturedNotification>>
}
