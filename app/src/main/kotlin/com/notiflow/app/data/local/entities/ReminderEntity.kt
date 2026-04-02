package com.notiflow.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long? = null,
    val eventId: Long? = null,
    val triggerTime: Long,
    val minutesBefore: Int = 5,
    val isDelivered: Boolean = false,
    val workManagerRequestId: String? = null
)
