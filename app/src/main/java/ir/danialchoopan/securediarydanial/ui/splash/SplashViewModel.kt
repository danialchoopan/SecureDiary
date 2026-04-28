package ir.danialchoopan.securediarydanial.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.danialchoopan.securediarydanial.domain.repository.EncryptionRepository
import ir.danialchoopan.securediarydanial.ui.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashViewModel(private val encryptionRepository: EncryptionRepository) : ViewModel() {

    // Using replay = 1 ensures that if the UI starts collecting after the event is sent, 
    // it will still receive the last destination.
    private val _startDestination = MutableSharedFlow<String>(replay = 1)
    val startDestination = _startDestination.asSharedFlow()

    init {
        checkDestination()
    }

    private fun checkDestination() {
        viewModelScope.launch {
            // Add a small delay so the splash screen is visible for a moment 
            // and the UI has time to set up the collector.
            delay(1500) 

            if (encryptionRepository.isPinSet()) {
                _startDestination.emit(Screen.Lock.route)
            } else {
                _startDestination.emit(Screen.PinSetup.route)
            }
        }
    }
}
