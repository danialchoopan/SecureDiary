package ir.danialchoopan.securediarydanial.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getTheme(): Flow<String> // "light", "dark", "system"
    suspend fun setTheme(theme: String)
    fun getLanguage(): Flow<String> // "en", "fa"
    suspend fun setLanguage(language: String)
    
    // Security settings
    fun isSetupComplete(): Flow<Boolean>
    suspend fun setSetupComplete(complete: Boolean)
    
    suspend fun setPassword(password: String)
    fun getPassword(): String?
    
    fun isBiometricEnabled(): Flow<Boolean>
    suspend fun setBiometricEnabled(enabled: Boolean)
}
