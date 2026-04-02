package com.notiflow.app.domain.usecase

import com.notiflow.app.domain.model.Event
import com.notiflow.app.domain.repository.EventRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ExportToICSUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")

    suspend operator fun invoke(events: List<Event>): String {
        val sb = StringBuilder()
        sb.appendLine("BEGIN:VCALENDAR")
        sb.appendLine("VERSION:2.0")
        sb.appendLine("PRODID:-//NotiFlow//NotiFlow App//EN")
        sb.appendLine("CALSCALE:GREGORIAN")

        for (event in events) {
            sb.appendLine("BEGIN:VEVENT")
            sb.appendLine("UID:notiflow-${event.id}@notiflow.app")
            if (event.isAllDay) {
                sb.appendLine("DTSTART;VALUE=DATE:${event.startDate.format(dateFormatter)}")
                sb.appendLine("DTEND;VALUE=DATE:${event.startDate.plusDays(1).format(dateFormatter)}")
            } else {
                val zone = ZoneId.systemDefault()
                val startDateTime = event.startDate.atTime(event.startTime ?: java.time.LocalTime.of(0, 0))
                    .atZone(zone).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime()
                sb.appendLine("DTSTART:${startDateTime.format(dateTimeFormatter)}")
                if (event.endTime != null) {
                    val endDateTime = event.startDate.atTime(event.endTime)
                        .atZone(zone).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime()
                    sb.appendLine("DTEND:${endDateTime.format(dateTimeFormatter)}")
                }
            }
            sb.appendLine("SUMMARY:${event.title.escapeIcs()}")
            event.description?.let { sb.appendLine("DESCRIPTION:${it.escapeIcs()}") }
            event.location?.let { sb.appendLine("LOCATION:${it.escapeIcs()}") }
            if (event.isRecurring && event.recurrenceDays.isNotEmpty()) {
                val days = event.recurrenceDays.joinToString(",") { dayIndex ->
                    listOf("SU", "MO", "TU", "WE", "TH", "FR", "SA")[dayIndex % 7]
                }
                sb.appendLine("RRULE:FREQ=WEEKLY;BYDAY=$days")
            }
            sb.appendLine("END:VEVENT")
        }

        sb.appendLine("END:VCALENDAR")
        return sb.toString()
    }

    private fun String.escapeIcs() = replace("\\", "\\\\").replace(";", "\\;")
        .replace(",", "\\,").replace("\n", "\\n")
}
