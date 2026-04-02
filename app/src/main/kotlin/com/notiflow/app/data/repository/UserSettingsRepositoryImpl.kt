package com.notiflow.app.data.repository

import com.notiflow.app.data.local.dao.UserSettingsDao
import com.notiflow.app.data.local.entities.UserSettingsEntity
import com.notiflow.app.data.mappers.toDomain
import com.notiflow.app.data.mappers.toEntity
import com.notiflow.app.domain.model.UserSettings
import com.notiflow.app.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingsRepositoryImpl @Inject constructor(
    private val userSettingsDao: UserSettingsDao
) : UserSettingsRepository {
    private val defaultSettings = UserSettings()

    override fun getUserSettings(): Flow<UserSettings> =
        userSettingsDao.get().map { it?.toDomain() ?: defaultSettings }

    override suspend fun updateUserSettings(settings: UserSettings) =
        userSettingsDao.upsert(settings.toEntity())

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        val current = userSettingsDao.getOnce()?.toDomain() ?: defaultSettings
        userSettingsDao.upsert(current.copy(onboardingCompleted = completed).toEntity())
    }

    override suspend fun setPinHash(hash: String?) {
        val current = userSettingsDao.getOnce()?.toDomain() ?: defaultSettings
        userSettingsDao.upsert(current.copy(pinHash = hash).toEntity())
    }

    override suspend fun setAiProvider(provider: String) {
        val current = userSettingsDao.getOnce()?.toDomain() ?: defaultSettings
        val entity = current.toEntity().copy(aiProviderPreference = provider)
        userSettingsDao.upsert(entity)
    }

    override suspend fun setTheme(theme: String) {
        val current = userSettingsDao.getOnce()?.toDomain() ?: defaultSettings
        val entity = current.toEntity().copy(themePreference = theme)
        userSettingsDao.upsert(entity)
    }

    override suspend fun setMonitoredApps(packageNames: List<String>) {
        val current = userSettingsDao.getOnce()?.toDomain() ?: defaultSettings
        userSettingsDao.upsert(current.copy(monitoredApps = packageNames).toEntity())
    }

    override suspend fun setMonitoredGroups(groupNames: List<String>) {
        val current = userSettingsDao.getOnce()?.toDomain() ?: defaultSettings
        userSettingsDao.upsert(current.copy(monitoredGroups = groupNames).toEntity())
    }
}
