package ir.danialchoopan.securediarydanial.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.danialchoopan.securediarydanial.ui.theme.SecureDiaryDanialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    theme: String,
    language: String,
    isBiometricSupported: Boolean,
    isBiometricEnabled: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit,
    onBiometricChange: (Boolean) -> Unit,
    onAboutMeClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (language == "fa") "تنظیمات" else "Settings") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (language == "fa") "تم تاریک" else "Dark Theme")
                Switch(
                    checked = theme == "dark",
                    onCheckedChange = onThemeChange
                )
            }

            HorizontalDivider()

            if (isBiometricSupported) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (language == "fa") "استفاده از اثر انگشت" else "Use Biometric")
                    Switch(
                        checked = isBiometricEnabled,
                        onCheckedChange = onBiometricChange
                    )
                }
                HorizontalDivider()
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(if (language == "fa") "زبان" else "Language")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = language == "en",
                        onClick = { onLanguageChange("en") },
                        label = { Text("English") }
                    )
                    FilterChip(
                        selected = language == "fa",
                        onClick = { onLanguageChange("fa") },
                        label = { Text("فارسی") }
                    )
                }
            }

            HorizontalDivider()

            // About Me Item
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAboutMeClick)
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(if (language == "fa") "درباره من" else "About Me")
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onAboutMeClick: () -> Unit
) {
    val theme by viewModel.theme.collectAsState()
    val language by viewModel.language.collectAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()

    SettingsContent(
        theme = theme,
        language = language,
        isBiometricSupported = viewModel.isBiometricSupported,
        isBiometricEnabled = isBiometricEnabled,
        onThemeChange = { isDark -> viewModel.setTheme(if (isDark) "dark" else "light") },
        onLanguageChange = { viewModel.setLanguage(it) },
        onBiometricChange = { viewModel.setBiometricEnabled(it) },
        onAboutMeClick = onAboutMeClick
    )
}

@Preview(showBackground = true, name = "Light Mode EN")
@Composable
fun SettingsPreviewLight() {
    SecureDiaryDanialTheme(darkTheme = false, language = "en") {
        SettingsContent("light", "en", true, false, {}, {}, {}, {})
    }
}
