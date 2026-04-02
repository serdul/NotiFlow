package com.notiflow.app.domain.model

enum class ProcessingStatus { PENDING, PROCESSING, PROCESSED, IGNORED, FLAGGED_INCOMPLETE }

data class CapturedNotification(
    val id: Long = 0,
    val packageName: String,
    val appLabel: String,
    val title: String,
    val messageText: String,
    val groupName: String? = null,
    val isGroupMessage: Boolean = false,
    val isMonitored: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
    val processingStatus: ProcessingStatus = ProcessingStatus.PENDING,
    val batchId: String? = null
)
