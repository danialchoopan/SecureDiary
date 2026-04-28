package ir.danialchoopan.securediarydanial.data.repository

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import ir.danialchoopan.securediarydanial.domain.repository.EncryptionRepository
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL

class EncryptionRepositoryImpl(private val context: Context) : EncryptionRepository {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private val ALIAS = "diary_image_key"
    private val TRANSFORMATION = "AES/CBC/PKCS7Padding"

    init {
        if (!keyStore.containsAlias(ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyGenerator.init(
                KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build()
            )
            keyGenerator.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey = (keyStore.getEntry(ALIAS, null) as KeyStore.SecretKeyEntry).secretKey

    override fun encryptText(text: String): String {
        return Base64.encodeToString(text.toByteArray(), Base64.DEFAULT) 
    }

    override fun decryptText(encryptedText: String): String {
        return String(Base64.decode(encryptedText, Base64.DEFAULT))
    }

    override fun encryptStream(input: InputStream, output: OutputStream) {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        output.write(cipher.iv)
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(cipher.update(buffer, 0, bytesRead))
        }
        output.write(cipher.doFinal())
    }

    override fun decryptStream(input: InputStream): InputStream {
        val iv = ByteArray(16)
        input.read(iv)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), IvParameterSpec(iv))
        return javax.crypto.CipherInputStream(input, cipher)
    }

    override fun savePin(pin: String) {
        sharedPreferences.edit().putString("pin_hash", pin).apply()
    }

    override fun isPinCorrect(pin: String): Boolean {
        return sharedPreferences.getString("pin_hash", null) == pin
    }

    override fun isPinSet(): Boolean {
        return sharedPreferences.contains("pin_hash")
    }

    override fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("biometric_enabled", enabled).apply()
    }

    override fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean("biometric_enabled", false)
    }

    override fun isBiometricSupported(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }
}
