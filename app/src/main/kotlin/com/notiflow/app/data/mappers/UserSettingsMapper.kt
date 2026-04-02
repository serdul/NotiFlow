package com.notiflow.app.data.mappers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.notiflow.app.data.local.entities.UserSettingsEntity
import com.notiflow.app.domain.model.AiProvider
import com.notiflow.app.domain.model.ThemePreference
import com.notiflow.app.domain.model.UserSettings

private val settingsGson = Gson()

fun UserSettingsEntity.toDomain(): UserSettings {
    val type = object : TypeToken<List<String>>() {}.type
    return UserSettings(
        onboardingCompleted = onboardingCompleted,
        profession = profession,
        notificationRetentionDays = notificationRetentionDays,
        aiProviderPreference = runCatching { AiProvider.valueOf(aiProviderPreference) }
            .getOrDefault(AiProvider.GROQ),
        batchIntervalSeconds = batchIntervalSeconds,
        batchMaxSize = batchMaxSize,
        doNotDisturbEnabled = doNotDisturbEnabled,
        doNotDisturbStart = doNotDisturbStart,
        doNotDisturbEnd = doNotDisturbEnd,
        pinHash = pinHash,
        defaultReminderMinutes = defaultReminderMinutes,
        themePreference = runCatching { ThemePreference.valueOf(themePreference) }
            .getOrDefault(ThemePreference.SYSTEM),
        monitoredApps = settingsGson.fromJson(monitoredApps, type),
        monitoredGroups = settingsGson.fromJson(monitoredGroups, type)
    )
}

fun UserSettings.toEntity(): UserSettingsEntity = UserSettingsEntity(
    onboardingCompleted = onboardingCompleted,
    profession = profession,
    notificationRetentionDays = notificationRetentionDays,
    aiProviderPreference = aiProviderPreference.name,
    batchIntervalSeconds = batchIntervalSeconds,
    batchMaxSize = batchMaxSize,
    doNotDisturbEnabled = doNotDisturbEnabled,
    doNotDisturbStart = doNotDisturbStart,
    doNotDisturbEnd = doNotDisturbEnd,
    pinHash = pinHash,
    defaultReminderMinutes = defaultReminderMinutes,
    themePreference = themePreference.name,
    monitoredApps = settingsGson.toJson(monitoredApps),
    monitoredGroups = settingsGson.toJson(monitoredGroups)
)
