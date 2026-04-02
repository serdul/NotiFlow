package com.notiflow.app.domain.repository

import com.notiflow.app.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getAllEvents(): Flow<List<Event>>
    fun getEventsByDateRange(startMs: Long, endMs: Long): Flow<List<Event>>
    suspend fun insertEvent(event: Event): Long
    suspend fun updateEvent(event: Event)
    suspend fun deleteEvent(event: Event)
    fun getConflictingEvents(): Flow<List<Event>>
}
