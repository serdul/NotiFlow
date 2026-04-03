package com.notiflow.app.data.remote.ai

import com.google.gson.annotations.SerializedName

// ─── User Context ────────────────────────────────────────────────────────────
data class UserContext(
    val profession: String?,
    val categories: List<String>,
    val currentDateTime: String,
    val timezone: String
)

// ─── AI Provider Result ───────────────────────────────────────────────────────
data class AiProcessingResult(
    val tasks: List<ExtractedTask> = emptyList(),
    val events: List<ExtractedEvent> = emptyList(),
    val flaggedItems: List<FlaggedItem> = emptyList(),
    val suggestedCategories: List<SuggestedCategory> = emptyList()
)

data class ExtractedTask(
    val title: String,
    val description: String?,
    val dueDate: String?,
    val dueTime: String?,
    val priority: String = "NORMAL",
    val categoryName: String?,
    val categoryIcon: String?,
    val assignedPerson: String?,
    val subtasks: List<String> = emptyList(),
    val isRecurring: Boolean = false,
    val sourceNotificationIndex: Int = 0
)

data class ExtractedEvent(
    val title: String,
    val description: String?,
    val startDate: String,
    val startTime: String?,
    val endTime: String?,
    val location: String?,
    val isAllDay: Boolean = false,
    val isRecurring: Boolean = false,
    val sourceNotificationIndex: Int = 0
)

data class FlaggedItem(
    val sourceNotificationIndex: Int,
    val partialTitle: String,
    val flagReason: String,
    val suggestedPrompt: String
)

data class SuggestedCategory(
    val name: String,
    val iconName: String,
    val colorHex: String
)

// ─── OpenAI / Groq (OpenAI-compatible) request/response ──────────────────────
data class OpenAiRequest(
    val model: String,
    val messages: List<OpenAiMessage>,
    val temperature: Double = 0.3,
    @SerializedName("max_tokens") val maxTokens: Int = 4096
)

data class OpenAiMessage(
    val role: String, // "system", "user", "assistant"
    val content: String
)

data class OpenAiResponse(
    val choices: List<OpenAiChoice>
)

data class OpenAiChoice(
    val message: OpenAiMessage
)

// ─── Gemini request/response ──────────────────────────────────────────────────
data class GeminiRequest(
    val contents: List<GeminiContent>,
    @SerializedName("generationConfig") val generationConfig: GeminiGenerationConfig = GeminiGenerationConfig()
)

data class GeminiContent(
    val role: String = "user",
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

data class GeminiGenerationConfig(
    val temperature: Double = 0.3,
    @SerializedName("maxOutputTokens") val maxOutputTokens: Int = 4096
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

data class GeminiCandidate(
    val content: GeminiContent
)

// ─── Whisper (audio transcription) ───────────────────────────────────────────
data class WhisperResult(
    val transcription: String,
    val duration: Float = 0f
)

data class WhisperResponse(
    val text: String
)
