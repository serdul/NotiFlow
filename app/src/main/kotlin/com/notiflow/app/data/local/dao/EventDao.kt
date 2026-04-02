package com.notiflow.app.data.local.dao

import androidx.room.*
import com.notiflow.app.data.local.entities.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY startTime ASC")
    fun getAll(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE startTime >= :startMs AND startTime <= :endMs ORDER BY startTime ASC")
    fun getByDateRange(startMs: Long, endMs: Long): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE conflictDetected = 1")
    fun getConflicts(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getById(id: Long): EventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity): Long

    @Update
    suspend fun update(event: EventEntity)

    @Delete
    suspend fun delete(event: EventEntity)

    @Query("UPDATE events SET icsExported = 1 WHERE id IN (:ids)")
    suspend fun markAsExported(ids: List<Long>)
}
