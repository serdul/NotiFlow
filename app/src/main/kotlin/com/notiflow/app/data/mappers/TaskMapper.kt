package com.notiflow.app.data.mappers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.notiflow.app.data.local.dao.TaskWithSubtasks
import com.notiflow.app.data.local.entities.SubTaskEntity
import com.notiflow.app.data.local.entities.TaskEntity
import com.notiflow.app.domain.model.Priority
import com.notiflow.app.domain.model.SubTask
import com.notiflow.app.domain.model.Task
import com.notiflow.app.domain.model.TaskStatus
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

private val gson = Gson()
private val zone = ZoneId.systemDefault()

fun TaskWithSubtasks.toDomain(): Task {
    return task.toDomain().copy(subtasks = subtasks.map { it.toDomain() })
}

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        dueDate = dueDate?.let { LocalDate.ofInstant(Instant.ofEpochMilli(it), zone) },
        dueTime = dueTime?.let {
            val totalMinutes = (it / 60000).toInt()
            LocalTime.of(totalMinutes / 60, totalMinutes % 60)
        },
        priority = runCatching { Priority.valueOf(priority) }.getOrDefault(Priority.NORMAL),
        categoryId = categoryId,
        assignedPerson = assignedPerson,
        status = runCatching { TaskStatus.valueOf(status) }.getOrDefault(TaskStatus.PENDING),
        isRecurring = isRecurring,
        recurrenceDays = recurrenceDays?.let {
            val type = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(it, type)
        } ?: emptyList(),
        sourceNotificationId = sourceNotificationId,
        sourceAppLabel = sourceAppLabel,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isAiFlagged = isAiFlagged,
        flagReason = flagReason,
        voiceNoteUri = voiceNoteUri
    )
}

fun SubTaskEntity.toDomain(): SubTask = SubTask(
    id = id,
    parentTaskId = parentTaskId,
    title = title,
    isCompleted = isCompleted,
    orderIndex = orderIndex
)

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        dueDate = dueDate?.atStartOfDay(zone)?.toInstant()?.toEpochMilli(),
        dueTime = dueTime?.let {
            ((it.hour * 60 + it.minute) * 60000).toLong()
        },
        priority = priority.name,
        categoryId = categoryId,
        assignedPerson = assignedPerson,
        status = status.name,
        isRecurring = isRecurring,
        recurrenceDays = if (recurrenceDays.isEmpty()) null else gson.toJson(recurrenceDays),
        sourceNotificationId = sourceNotificationId,
        sourceAppLabel = sourceAppLabel,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isAiFlagged = isAiFlagged,
        flagReason = flagReason,
        voiceNoteUri = voiceNoteUri
    )
}

fun SubTask.toEntity(): SubTaskEntity = SubTaskEntity(
    id = id,
    parentTaskId = parentTaskId,
    title = title,
    isCompleted = isCompleted,
    orderIndex = orderIndex
)
