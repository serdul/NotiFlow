package com.notiflow.app.data.remote.whisper

import com.notiflow.app.data.local.preferences.SecurePreferences
import com.notiflow.app.data.remote.ai.WhisperResponse
import com.notiflow.app.data.remote.ai.WhisperResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

interface WhisperApiService {
    @Multipart
    @POST("audio/transcriptions")
    suspend fun transcribe(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("model") model: okhttp3.RequestBody,
        @Part("language") language: okhttp3.RequestBody
    ): WhisperResponse
}

@Singleton
class WhisperClient @Inject constructor(
    private val securePreferences: SecurePreferences
) {
    private val baseUrl = "https://api.groq.com/openai/v1/"
    private val model = "whisper-large-v3-turbo"
    private val maxDurationSeconds = 300 // 5 minutes

    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.HEADERS }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val service by lazy { retrofit.create(WhisperApiService::class.java) }

    suspend fun transcribe(audioFile: File): Result<WhisperResult> {
        return runCatching {
            val apiKey = securePreferences.getApiKey("groq")
                ?: throw IllegalStateException("Groq API key not configured for Whisper transcription")

            // Validate file exists and has content
            require(audioFile.exists() && audioFile.length() > 0) { "Audio file is empty or does not exist" }

            Timber.d("Transcribing audio file: ${audioFile.name}, size: ${audioFile.length()} bytes")

            val requestFile = audioFile.asRequestBody("audio/m4a".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", audioFile.name, requestFile)
            val modelBody = model.toRequestBody("text/plain".toMediaTypeOrNull())
            val languageBody = "en".toRequestBody("text/plain".toMediaTypeOrNull())

            val response = service.transcribe("Bearer $apiKey", filePart, modelBody, languageBody)
            WhisperResult(transcription = response.text)
        }
    }
}
