package ir.danialchoopan.securediarydanial.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.danialchoopan.securediarydanial.domain.repository.SettingsRepository
import ir.danialchoopan.securediarydanial.domain.repository.EncryptionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val encryptionRepository: EncryptionRepository
) : ViewModel() {

    val theme: StateFlow<String> = settingsRepository.getTheme()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "dark")

    val language: StateFlow<String> = settingsRepository.getLanguage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val isBiometricEnabled: StateFlow<Boolean> = settingsRepository.isBiometricEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), encryptionRepository.isBiometricEnabled())

    val isBiometricSupported = encryptionRepository.isBiometricSupported()

    fun setTheme(theme: String) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setBiometricEnabled(enabled)
            encryptionRepository.setBiometricEnabled(enabled)
        }
    }
}
