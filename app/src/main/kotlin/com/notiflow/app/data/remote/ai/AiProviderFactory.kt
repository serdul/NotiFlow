package com.notiflow.app.data.remote.ai

import com.notiflow.app.data.local.preferences.SecurePreferences
import com.notiflow.app.domain.model.AiProvider as AiProviderEnum
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiProviderFactory @Inject constructor(
    private val openAiClient: OpenAiClient,
    private val geminiClient: GeminiClient,
    private val groqClient: GroqClient,
    private val securePreferences: SecurePreferences
) {
    fun getProvider(preference: AiProviderEnum): AiProvider {
        return when (preference) {
            AiProviderEnum.OPENAI -> {
                if (securePreferences.hasApiKey("openai")) {
                    Timber.d("Using OpenAI provider")
                    openAiClient
                } else {
                    Timber.w("OpenAI key not set, falling back to Groq")
                    groqClient
                }
            }
            AiProviderEnum.GEMINI -> {
                if (securePreferences.hasApiKey("gemini")) {
                    Timber.d("Using Gemini provider")
                    geminiClient
                } else {
                    Timber.w("Gemini key not set, falling back to Groq")
                    groqClient
                }
            }
            AiProviderEnum.GROQ -> {
                Timber.d("Using Groq provider")
                groqClient
            }
        }
    }
}
