package com.notiflow.app.domain.model

data class VoiceCapture(
    val id: Long = 0,
    val audioFilePath: String,
    val transcription: String? = null,
    val durationSeconds: Float = 0f,
    val associatedTaskId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
