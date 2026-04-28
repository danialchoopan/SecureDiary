package ir.danialchoopan.securediarydanial.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import ir.danialchoopan.securediarydanial.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "settings_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun getTheme(): Flow<String> = callbackFlow {
        val listener = { _: android.content.SharedPreferences, key: String? ->
            if (key == "theme") {
                trySend(sharedPreferences.getString("theme", "dark") ?: "dark")
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        trySend(sharedPreferences.getString("theme", "dark") ?: "dark")
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun setTheme(theme: String) {
        sharedPreferences.edit().putString("theme", theme).apply()
    }

    override fun getLanguage(): Flow<String> = callbackFlow {
        val listener = { _: android.content.SharedPreferences, key: String? ->
            if (key == "language") {
                trySend(sharedPreferences.getString("language", "en") ?: "en")
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        trySend(sharedPreferences.getString("language", "en") ?: "en")
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun setLanguage(language: String) {
        sharedPreferences.edit().putString("language", language).apply()
    }

    override fun isSetupComplete(): Flow<Boolean> = callbackFlow {
        val listener = { _: android.content.SharedPreferences, key: String? ->
            if (key == "setup_complete") {
                trySend(sharedPreferences.getBoolean("setup_complete", false))
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        trySend(sharedPreferences.getBoolean("setup_complete", false))
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun setSetupComplete(complete: Boolean) {
        sharedPreferences.edit().putBoolean("setup_complete", complete).apply()
    }

    override suspend fun setPassword(password: String) {
        sharedPreferences.edit().putString("password", password).apply()
    }

    override fun getPassword(): String? {
        return sharedPreferences.getString("password", null)
    }

    override fun isBiometricEnabled(): Flow<Boolean> = callbackFlow {
        val listener = { _: android.content.SharedPreferences, key: String? ->
            if (key == "biometric_enabled") {
                trySend(sharedPreferences.getBoolean("biometric_enabled", false))
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        trySend(sharedPreferences.getBoolean("biometric_enabled", false))
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("biometric_enabled", enabled).apply()
    }
}
