package com.notiflow.app.data.local.dao

import androidx.room.*
import com.notiflow.app.data.local.entities.CapturedNotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CapturedNotificationDao {
    @Query("SELECT * FROM captured_notifications WHERE processingStatus = 'PENDING' ORDER BY timestamp ASC")
    fun getPending(): Flow<List<CapturedNotificationEntity>>

    @Query("SELECT * FROM captured_notifications ORDER BY timestamp DESC")
    fun getAll(): Flow<List<CapturedNotificationEntity>>

    @Query("SELECT * FROM captured_notifications WHERE batchId = :batchId")
    suspend fun getByBatchId(batchId: String): List<CapturedNotificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: CapturedNotificationEntity): Long

    @Query("UPDATE captured_notifications SET processingStatus = :status, batchId = :batchId WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, batchId: String? = null)

    @Query("DELETE FROM captured_notifications WHERE timestamp < :timestampMs")
    suspend fun deleteOlderThan(timestampMs: Long)
}
