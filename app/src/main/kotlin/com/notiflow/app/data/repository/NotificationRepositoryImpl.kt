package com.notiflow.app.data.repository

import com.notiflow.app.data.local.dao.CapturedNotificationDao
import com.notiflow.app.data.mappers.toDomain
import com.notiflow.app.data.mappers.toEntity
import com.notiflow.app.domain.model.CapturedNotification
import com.notiflow.app.domain.model.ProcessingStatus
import com.notiflow.app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: CapturedNotificationDao
) : NotificationRepository {
    override fun getPendingNotifications(): Flow<List<CapturedNotification>> =
        notificationDao.getPending().map { list -> list.map { it.toDomain() } }

    override suspend fun getNotificationsByBatchId(batchId: String): List<CapturedNotification> =
        notificationDao.getByBatchId(batchId).map { it.toDomain() }

    override suspend fun insertNotification(notification: CapturedNotification): Long =
        notificationDao.insert(notification.toEntity())

    override suspend fun updateProcessingStatus(id: Long, status: ProcessingStatus, batchId: String?) =
        notificationDao.updateStatus(id, status.name, batchId)

    override suspend fun deleteOlderThan(timestampMs: Long) =
        notificationDao.deleteOlderThan(timestampMs)

    override fun getAllNotifications(): Flow<List<CapturedNotification>> =
        notificationDao.getAll().map { list -> list.map { it.toDomain() } }
}
