package com.notiflow.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId"), Index("startTime")]
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = null,
    val startTime: Long,
    val endTime: Long? = null,
    val isAllDay: Boolean = false,
    val location: String? = null,
    val isRecurring: Boolean = false,
    val recurrenceDays: String? = null,
    val categoryId: Long? = null,
    val sourceTaskId: Long? = null,
    val icsExported: Boolean = false,
    val conflictDetected: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
