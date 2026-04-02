package com.notiflow.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val onboardingCompleted: Boolean = false,
    val profession: String? = null,
    val notificationRetentionDays: Int = 30,
    val aiProviderPreference: String = "GROQ",
    val batchIntervalSeconds: Int = 90,
    val batchMaxSize: Int = 10,
    val doNotDisturbEnabled: Boolean = false,
    val doNotDisturbStart: Int = 1320,
    val doNotDisturbEnd: Int = 420,
    val pinHash: String? = null,
    val defaultReminderMinutes: Int = 5,
    val themePreference: String = "SYSTEM",
    val monitoredApps: String = """["com.whatsapp","com.whatsapp.w4b","org.telegram.messenger","com.google.android.gm","com.facebook.orca","com.microsoft.teams","com.microsoft.office.outlook"]""",
    val monitoredGroups: String = "[]"
)
