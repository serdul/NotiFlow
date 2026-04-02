package com.notiflow.app.data.remote.ai

import com.notiflow.app.domain.model.CapturedNotification
import com.notiflow.app.domain.model.Category
import com.notiflow.app.domain.model.Task

interface AiProvider {
    suspend fun processNotificationBatch(
        notifications: List<CapturedNotification>,
        userContext: UserContext,
        existingCategories: List<Category>
    ): AiProcessingResult

    suspend fun completeTaskContext(
        flaggedTask: Task,
        voiceTranscription: String,
        userContext: UserContext
    ): Task
}
