package com.notiflow.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    // All use cases use @Inject constructor — Hilt auto-provides them.
}
