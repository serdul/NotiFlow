package com.notiflow.app.data.remote.ai

import com.google.gson.Gson
import com.notiflow.app.data.local.preferences.SecurePreferences
import com.notiflow.app.domain.model.CapturedNotification
import com.notiflow.app.domain.model.Category
import com.notiflow.app.domain.model.Priority
import com.notiflow.app.domain.model.Task
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

interface GeminiApiService {
    @POST("models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

@Singleton
class GeminiClient @Inject constructor(
    private val securePreferences: SecurePreferences
) : AiProvider {
    private val gson = Gson()
    private var model = "gemini-1.5-flash"
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/"

    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val service by lazy { retrofit.create(GeminiApiService::class.java) }

    fun setModel(modelName: String) { model = modelName }

    override suspend fun processNotificationBatch(
        notifications: List<CapturedNotification>,
        userContext: UserContext,
        existingCategories: List<Category>
    ): AiProcessingResult {
        val apiKey = securePreferences.getApiKey("gemini")
            ?: throw IllegalStateException("Gemini API key not configured")

        val fullPrompt = PromptBuilder.SYSTEM_PROMPT + "\n\n" +
                PromptBuilder.buildUserPrompt(notifications, userContext, existingCategories)
        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(fullPrompt))))
        )

        val response = service.generateContent(model, apiKey, request)
        val jsonContent = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: return AiProcessingResult()

        return try {
            gson.fromJson(jsonContent.trim(), AiProcessingResult::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse Gemini response")
            AiProcessingResult()
        }
    }

    override suspend fun completeTaskContext(
        flaggedTask: Task,
        voiceTranscription: String,
        userContext: UserContext
    ): Task {
        val apiKey = securePreferences.getApiKey("gemini")
            ?: throw IllegalStateException("Gemini API key not configured")

        val prompt = PromptBuilder.buildCompletionPrompt(
            flaggedTask.title, flaggedTask.flagReason ?: "", voiceTranscription, userContext
        )
        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(prompt))))
        )
        val response = service.generateContent(model, apiKey, request)
        val jsonContent = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: return flaggedTask
        return try {
            val extracted = gson.fromJson(jsonContent.trim(), ExtractedTask::class.java)
            flaggedTask.copy(
                title = extracted.title,
                description = extracted.description,
                priority = runCatching { Priority.valueOf(extracted.priority) }.getOrDefault(Priority.NORMAL),
                isAiFlagged = false
            )
        } catch (e: Exception) {
            flaggedTask.copy(isAiFlagged = false)
        }
    }
}
