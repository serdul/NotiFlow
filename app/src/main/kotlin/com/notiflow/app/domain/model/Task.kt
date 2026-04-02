package com.notiflow.app.domain.model

import java.time.LocalDate
import java.time.LocalTime

enum class Priority { URGENT, HIGH, NORMAL, LOW }
enum class TaskStatus { PENDING, IN_PROGRESS, COMPLETED, OVERDUE, DISMISSED }

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val priority: Priority = Priority.NORMAL,
    val categoryId: Long? = null,
    val assignedPerson: String? = null,
    val status: TaskStatus = TaskStatus.PENDING,
    val isRecurring: Boolean = false,
    val recurrenceDays: List<Int> = emptyList(),
    val subtasks: List<SubTask> = emptyList(),
    val sourceNotificationId: Long? = null,
    val sourceAppLabel: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isAiFlagged: Boolean = false,
    val flagReason: String? = null,
    val voiceNoteUri: String? = null
)

data class SubTask(
    val id: Long = 0,
    val parentTaskId: Long = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0
)
