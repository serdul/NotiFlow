package com.notiflow.app.domain.model

enum class AiProvider { OPENAI, GEMINI, GROQ }
enum class ThemePreference { SYSTEM, LIGHT, DARK }

data class UserSettings(
    val onboardingCompleted: Boolean = false,
    val profession: String? = null,
    val notificationRetentionDays: Int = 30,
    val aiProviderPreference: AiProvider = AiProvider.GROQ,
    val batchIntervalSeconds: Int = 90,
    val batchMaxSize: Int = 10,
    val doNotDisturbEnabled: Boolean = false,
    val doNotDisturbStart: Int = 1320,
    val doNotDisturbEnd: Int = 420,
    val pinHash: String? = null,
    val defaultReminderMinutes: Int = 5,
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val monitoredApps: List<String> = listOf(
        "com.whatsapp",
        "com.whatsapp.w4b",
        "org.telegram.messenger",
        "com.google.android.gm",
        "com.facebook.orca",
        "com.microsoft.teams",
        "com.microsoft.office.outlook"
    ),
    val monitoredGroups: List<String> = emptyList()
)
