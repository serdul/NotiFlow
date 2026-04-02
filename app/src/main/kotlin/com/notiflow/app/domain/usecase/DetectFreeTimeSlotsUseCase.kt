package com.notiflow.app.domain.usecase

import com.notiflow.app.domain.model.Event
import com.notiflow.app.domain.repository.EventRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

data class FreeTimeSlot(
    val start: LocalTime,
    val end: LocalTime,
    val durationMinutes: Int
)

class DetectFreeTimeSlotsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(date: LocalDate): List<FreeTimeSlot> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.atTime(23, 59)
        val startMs = startOfDay.toInstant(java.time.ZoneOffset.UTC).toEpochMilli()
        val endMs = endOfDay.toInstant(java.time.ZoneOffset.UTC).toEpochMilli()

        val events = eventRepository.getEventsByDateRange(startMs, endMs).first()
        return calculateFreeSlots(events)
    }

    private fun calculateFreeSlots(events: List<Event>): List<FreeTimeSlot> {
        val workStart = LocalTime.of(8, 0)
        val workEnd = LocalTime.of(22, 0)
        if (events.isEmpty()) {
            val duration = (workEnd.toSecondOfDay() - workStart.toSecondOfDay()) / 60
            return listOf(FreeTimeSlot(workStart, workEnd, duration))
        }

        val sortedEvents = events
            .filter { it.startTime != null }
            .sortedBy { it.startTime }

        val slots = mutableListOf<FreeTimeSlot>()
        var cursor = workStart

        for (event in sortedEvents) {
            val eventStart = event.startTime ?: continue
            val eventEnd = event.endTime ?: eventStart.plusHours(1)
            if (cursor.isBefore(eventStart)) {
                val duration = (eventStart.toSecondOfDay() - cursor.toSecondOfDay()) / 60
                if (duration >= 30) slots.add(FreeTimeSlot(cursor, eventStart, duration))
            }
            if (eventEnd.isAfter(cursor)) cursor = eventEnd
        }

        if (cursor.isBefore(workEnd)) {
            val duration = (workEnd.toSecondOfDay() - cursor.toSecondOfDay()) / 60
            if (duration >= 30) slots.add(FreeTimeSlot(cursor, workEnd, duration))
        }

        return slots
    }
}
