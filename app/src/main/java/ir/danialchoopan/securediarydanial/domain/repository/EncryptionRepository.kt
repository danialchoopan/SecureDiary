package ir.danialchoopan.securediarydanial.domain.repository

import java.io.InputStream
import java.io.OutputStream

interface EncryptionRepository {
    fun encryptText(text: String): String
    fun decryptText(encryptedText: String): String
    fun encryptStream(input: InputStream, output: OutputStream)
    fun decryptStream(input: InputStream): InputStream
    fun savePin(pin: String)
    fun isPinCorrect(pin: String): Boolean
    fun isPinSet(): Boolean
    
    // Biometric methods
    fun setBiometricEnabled(enabled: Boolean)
    fun isBiometricEnabled(): Boolean
    fun isBiometricSupported(): Boolean
}
