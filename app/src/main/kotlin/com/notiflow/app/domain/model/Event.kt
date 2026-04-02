package com.notiflow.app.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class Event(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val startDate: LocalDate,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val isAllDay: Boolean = false,
    val location: String? = null,
    val isRecurring: Boolean = false,
    val recurrenceDays: List<Int> = emptyList(),
    val categoryId: Long? = null,
    val sourceTaskId: Long? = null,
    val icsExported: Boolean = false,
    val conflictDetected: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
