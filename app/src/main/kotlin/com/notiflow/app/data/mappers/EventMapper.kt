package com.notiflow.app.data.mappers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.notiflow.app.data.local.entities.EventEntity
import com.notiflow.app.domain.model.Event
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

private val eventGson = Gson()
private val eventZone = ZoneId.systemDefault()

fun EventEntity.toDomain(): Event {
    val startInstant = Instant.ofEpochMilli(startTime)
    val startZoned = startInstant.atZone(eventZone)
    return Event(
        id = id,
        title = title,
        description = description,
        startDate = startZoned.toLocalDate(),
        startTime = if (isAllDay) null else startZoned.toLocalTime(),
        endTime = endTime?.let {
            Instant.ofEpochMilli(it).atZone(eventZone).toLocalTime()
        },
        isAllDay = isAllDay,
        location = location,
        isRecurring = isRecurring,
        recurrenceDays = recurrenceDays?.let {
            val type = object : TypeToken<List<Int>>() {}.type
            eventGson.fromJson(it, type)
        } ?: emptyList(),
        categoryId = categoryId,
        sourceTaskId = sourceTaskId,
        icsExported = icsExported,
        conflictDetected = conflictDetected,
        createdAt = createdAt
    )
}

fun Event.toEntity(): EventEntity {
    val startMs = startDate.atTime(startTime ?: LocalTime.MIDNIGHT)
        .atZone(eventZone).toInstant().toEpochMilli()
    val endMs = endTime?.let {
        startDate.atTime(it).atZone(eventZone).toInstant().toEpochMilli()
    }
    return EventEntity(
        id = id,
        title = title,
        description = description,
        startTime = startMs,
        endTime = endMs,
        isAllDay = isAllDay,
        location = location,
        isRecurring = isRecurring,
        recurrenceDays = if (recurrenceDays.isEmpty()) null else eventGson.toJson(recurrenceDays),
        categoryId = categoryId,
        sourceTaskId = sourceTaskId,
        icsExported = icsExported,
        conflictDetected = conflictDetected,
        createdAt = createdAt
    )
}
