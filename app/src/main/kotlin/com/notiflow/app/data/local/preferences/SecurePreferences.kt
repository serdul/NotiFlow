package com.notiflow.app.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val prefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            "notiflow_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveApiKey(provider: String, key: String) {
        prefs.edit().putString("api_key_${provider.lowercase()}", key).apply()
    }

    fun getApiKey(provider: String): String? {
        return prefs.getString("api_key_${provider.lowercase()}", null)
    }

    fun clearApiKey(provider: String) {
        prefs.edit().remove("api_key_${provider.lowercase()}").apply()
    }

    fun hasApiKey(provider: String): Boolean {
        return !getApiKey(provider).isNullOrBlank()
    }
}
