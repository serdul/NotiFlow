package com.notiflow.app.di

import com.notiflow.app.data.remote.ai.*
import com.notiflow.app.data.remote.whisper.WhisperClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    // OpenAiClient, GeminiClient, GroqClient, WhisperClient, and AiProviderFactory
    // are all @Singleton and @Inject constructors — Hilt auto-provides them.
    // This module is a placeholder for any future custom binding needs.
}
