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
import retrofit2.http.Header
import retrofit2.http.POST
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

interface OpenAiApiService {
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: OpenAiRequest
    ): OpenAiResponse
}

@Singleton
class OpenAiClient @Inject constructor(
    private val securePreferences: SecurePreferences
) : AiProvider {
    private val gson = Gson()
    private var model = "gpt-4o-mini"
    private val baseUrl = "https://api.openai.com/v1/"

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

    private val service by lazy { retrofit.create(OpenAiApiService::class.java) }

    fun setModel(modelName: String) { model = modelName }

    override suspend fun processNotificationBatch(
        notifications: List<CapturedNotification>,
        userContext: UserContext,
        existingCategories: List<Category>
    ): AiProcessingResult {
        val apiKey = securePreferences.getApiKey("openai")
            ?: throw IllegalStateException("OpenAI API key not configured")

        val userPrompt = PromptBuilder.buildUserPrompt(notifications, userContext, existingCategories)
        val request = OpenAiRequest(
            model = model,
            messages = listOf(
                OpenAiMessage("system", PromptBuilder.SYSTEM_PROMPT),
                OpenAiMessage("user", userPrompt)
            )
        )

        return retryWithBackoff(3) {
            val response = service.chatCompletion("Bearer $apiKey", request)
            val jsonContent = response.choices.firstOrNull()?.message?.content
                ?: return@retryWithBackoff AiProcessingResult()
            parseAiResult(jsonContent)
        }
    }

    override suspend fun completeTaskContext(
        flaggedTask: Task,
        voiceTranscription: String,
        userContext: UserContext
    ): Task {
        val apiKey = securePreferences.getApiKey("openai")
            ?: throw IllegalStateException("OpenAI API key not configured")

        val prompt = PromptBuilder.buildCompletionPrompt(
            flaggedTask.title, flaggedTask.flagReason ?: "", voiceTranscription, userContext
        )
        val request = OpenAiRequest(
            model = model,
            messages = listOf(
                OpenAiMessage("system", PromptBuilder.SYSTEM_PROMPT),
                OpenAiMessage("user", prompt)
            )
        )
        val response = service.chatCompletion("Bearer $apiKey", request)
        val jsonContent = response.choices.firstOrNull()?.message?.content ?: return flaggedTask
        return parseTaskJson(jsonContent, flaggedTask)
    }

    private fun parseAiResult(json: String): AiProcessingResult {
        return try {
            gson.fromJson(json, AiProcessingResult::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse AI response")
            AiProcessingResult()
        }
    }

    private fun parseTaskJson(json: String, fallback: Task): Task {
        return try {
            val extracted = gson.fromJson(json, ExtractedTask::class.java)
            fallback.copy(
                title = extracted.title,
                description = extracted.description,
                priority = runCatching { Priority.valueOf(extracted.priority) }.getOrDefault(Priority.NORMAL),
                isAiFlagged = false,
                flagReason = null
            )
        } catch (e: Exception) {
            fallback.copy(isAiFlagged = false)
        }
    }

    private suspend fun <T> retryWithBackoff(times: Int, block: suspend () -> T): T {
        repeat(times - 1) { attempt ->
            try { return block() } catch (e: Exception) {
                Timber.w(e, "Attempt ${attempt + 1} failed, retrying...")
                kotlinx.coroutines.delay((1000L * (attempt + 1) * 2))
            }
        }
        return block()
    }
}
