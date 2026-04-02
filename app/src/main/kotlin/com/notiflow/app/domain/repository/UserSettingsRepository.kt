package com.notiflow.app.domain.repository

import com.notiflow.app.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    fun getUserSettings(): Flow<UserSettings>
    suspend fun updateUserSettings(settings: UserSettings)
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setPinHash(hash: String?)
    suspend fun setAiProvider(provider: String)
    suspend fun setTheme(theme: String)
    suspend fun setMonitoredApps(packageNames: List<String>)
    suspend fun setMonitoredGroups(groupNames: List<String>)
}
