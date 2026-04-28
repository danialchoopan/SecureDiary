package ir.danialchoopan.securediarydanial.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import ir.danialchoopan.securediarydanial.ui.theme.SecureDiaryDanialTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LockContent(
    language: String,
    error: String? = null,
    isBiometricEnabled: Boolean = false,
    onAuthenticate: (String) -> Unit,
    onBiometricClick: () -> Unit = {}
) {
    var pin by remember { mutableStateOf("") }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = if (language == "fa") "خوش آمدید" else "Welcome Back",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = if (language == "fa") "لطفاً برای ادامه پین کد خود را وارد کنید" else "Please enter your PIN to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            OutlinedTextField(
                value = pin,
                onValueChange = { 
                    if (it.length <= 6) {
                        pin = it
                    }
                },
                placeholder = { 
                    Text(
                        if (language == "fa") "پین کد" else "PIN Code",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    ) 
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(200.dp),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    letterSpacing = 8.sp
                ),
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                singleLine = true
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onAuthenticate(pin) },
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp),
                shape = CircleShape
            ) {
                Text(if (language == "fa") "ورود" else "Unlock")
            }

            if (isBiometricEnabled) {
                Spacer(modifier = Modifier.height(48.dp))
                IconButton(
                    onClick = onBiometricClick,
                    modifier = Modifier
                        .size(72.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biometric",
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun LockScreen(
    viewModel: AuthViewModel,
    onAuthenticated: () -> Unit,
    language: String
) {
    val context = LocalContext.current
    var error by remember { mutableStateOf<String?>(null) }
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState(initial = false)

    LaunchedEffect(key1 = true) {
        viewModel.isAuthenticated.collectLatest { authenticated ->
            if (authenticated) {
                onAuthenticated()
            } else {
                error = if (language == "fa") "پین کد اشتباه است" else "Incorrect PIN"
            }
        }
    }

    // Auto-trigger biometric if enabled
    LaunchedEffect(isBiometricEnabled) {
        if (isBiometricEnabled && context is FragmentActivity) {
            viewModel.showBiometricPrompt(context)
        }
    }

    LockContent(
        language = language,
        error = error,
        isBiometricEnabled = isBiometricEnabled,
        onAuthenticate = { pin ->
            if (pin.length >= 4) {
                viewModel.authenticate(pin)
            }
        },
        onBiometricClick = {
            if (context is FragmentActivity) {
                viewModel.showBiometricPrompt(context)
            }
        }
    )
}

@Preview(showBackground = true, name = "Modern Lock EN")
@Composable
fun LockPreviewLight() {
    SecureDiaryDanialTheme(darkTheme = false, language = "en") {
        LockContent(language = "en", isBiometricEnabled = true, onAuthenticate = {})
    }
}
