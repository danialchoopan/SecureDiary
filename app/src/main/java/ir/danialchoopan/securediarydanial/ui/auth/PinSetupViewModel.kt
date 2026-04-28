package ir.danialchoopan.securediarydanial.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.danialchoopan.securediarydanial.domain.repository.EncryptionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class PinSetupViewModel(private val encryptionRepository: EncryptionRepository) : ViewModel() {

    private val _setupComplete = MutableSharedFlow<Unit>()
    val setupComplete = _setupComplete.asSharedFlow()

    val isBiometricSupported = encryptionRepository.isBiometricSupported()

    fun saveSetup(pin: String, biometricEnabled: Boolean) {
        viewModelScope.launch {
            encryptionRepository.savePin(pin)
            encryptionRepository.setBiometricEnabled(biometricEnabled)
            _setupComplete.emit(Unit)
        }
    }
}
