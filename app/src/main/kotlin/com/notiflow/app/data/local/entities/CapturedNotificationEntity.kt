package com.notiflow.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "captured_notifications")
data class CapturedNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appLabel: String,
    val title: String,
    val messageText: String,
    val groupName: String? = null,
    val isGroupMessage: Boolean = false,
    val isMonitored: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
    val processingStatus: String = "PENDING",
    val batchId: String? = null
)
