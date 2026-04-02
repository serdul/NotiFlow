package com.notiflow.app.data.repository

import com.notiflow.app.data.local.dao.EventDao
import com.notiflow.app.data.mappers.toDomain
import com.notiflow.app.data.mappers.toEntity
import com.notiflow.app.domain.model.Event
import com.notiflow.app.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao
) : EventRepository {
    override fun getAllEvents(): Flow<List<Event>> =
        eventDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getEventsByDateRange(startMs: Long, endMs: Long): Flow<List<Event>> =
        eventDao.getByDateRange(startMs, endMs).map { list -> list.map { it.toDomain() } }

    override suspend fun insertEvent(event: Event): Long = eventDao.insert(event.toEntity())

    override suspend fun updateEvent(event: Event) = eventDao.update(event.toEntity())

    override suspend fun deleteEvent(event: Event) = eventDao.delete(event.toEntity())

    override fun getConflictingEvents(): Flow<List<Event>> =
        eventDao.getConflicts().map { list -> list.map { it.toDomain() } }
}
