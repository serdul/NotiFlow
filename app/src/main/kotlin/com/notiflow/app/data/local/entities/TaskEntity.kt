package com.notiflow.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId"), Index("sourceNotificationId")]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = null,
    val dueDate: Long? = null,
    val dueTime: Long? = null,
    val priority: String = "NORMAL",
    val categoryId: Long? = null,
    val assignedPerson: String? = null,
    val status: String = "PENDING",
    val isRecurring: Boolean = false,
    val recurrenceDays: String? = null,
    val sourceNotificationId: Long? = null,
    val sourceAppLabel: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isAiFlagged: Boolean = false,
    val flagReason: String? = null,
    val voiceNoteUri: String? = null
)
