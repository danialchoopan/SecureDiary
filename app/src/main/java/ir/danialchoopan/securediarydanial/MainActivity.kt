package ir.danialchoopan.securediarydanial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ir.danialchoopan.securediarydanial.ui.navigation.NavGraph
import ir.danialchoopan.securediarydanial.ui.navigation.GenericViewModelFactory
import ir.danialchoopan.securediarydanial.ui.settings.SettingsViewModel
import ir.danialchoopan.securediarydanial.ui.theme.SecureDiaryDanialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val appContainer = (application as DiaryApplication).container

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = GenericViewModelFactory {
                SettingsViewModel(appContainer.settingsRepository, appContainer.encryptionRepository)
            })
            val theme by settingsViewModel.theme.collectAsState()
            val language by settingsViewModel.language.collectAsState()
            val navController = rememberNavController()

            SecureDiaryDanialTheme(
                darkTheme = theme == "dark",
                language = language
            ) {
                NavGraph(
                    navController = navController,
                    language = language,
                    onLanguageChange = { settingsViewModel.setLanguage(it) }
                )
            }
        }
    }
}
