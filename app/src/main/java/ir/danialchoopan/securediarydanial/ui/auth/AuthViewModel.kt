package ir.danialchoopan.securediarydanial.ui.auth

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.danialchoopan.securediarydanial.domain.repository.EncryptionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class AuthViewModel(private val encryptionRepository: EncryptionRepository) : ViewModel() {

    private val _isAuthenticated = MutableSharedFlow<Boolean>()
    val isAuthenticated = _isAuthenticated.asSharedFlow()

    val isBiometricEnabled = flow {
        emit(encryptionRepository.isBiometricEnabled())
    }

    fun authenticate(pin: String) {
        viewModelScope.launch {
            if (encryptionRepository.isPinCorrect(pin)) {
                _isAuthenticated.emit(true)
            } else {
                _isAuthenticated.emit(false)
            }
        }
    }

    fun showBiometricPrompt(activity: FragmentActivity) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    viewModelScope.launch {
                        _isAuthenticated.emit(true)
                    }
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use PIN")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
