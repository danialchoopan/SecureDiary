package ir.danialchoopan.securediarydanial.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ir.danialchoopan.securediarydanial.DiaryApplication
import ir.danialchoopan.securediarydanial.ui.auth.AuthViewModel
import ir.danialchoopan.securediarydanial.ui.auth.LockScreen
import ir.danialchoopan.securediarydanial.ui.auth.PinSetupScreen
import ir.danialchoopan.securediarydanial.ui.auth.PinSetupViewModel
import ir.danialchoopan.securediarydanial.ui.splash.SplashScreen
import ir.danialchoopan.securediarydanial.ui.splash.SplashViewModel
import ir.danialchoopan.securediarydanial.ui.settings.SettingsScreen
import ir.danialchoopan.securediarydanial.ui.settings.SettingsViewModel
import ir.danialchoopan.securediarydanial.ui.settings.AboutMeScreen
import androidx.compose.ui.platform.LocalContext
import ir.danialchoopan.securediarydanial.ui.diary.AddEditEntryScreen
import ir.danialchoopan.securediarydanial.ui.diary.DetailScreen
import ir.danialchoopan.securediarydanial.ui.diary.DiaryViewModel
import ir.danialchoopan.securediarydanial.ui.diary.HomeScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    language: String,
    onLanguageChange: (String) -> Unit
) {
    val context = LocalContext.current
    val appContainer = (context.applicationContext as DiaryApplication).container

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            val splashViewModel: SplashViewModel = viewModel(factory = GenericViewModelFactory {
                SplashViewModel(appContainer.encryptionRepository)
            })
            SplashScreen(viewModel = splashViewModel) { destination ->
                navController.navigate(destination) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }

        composable(Screen.PinSetup.route) {
            val pinSetupViewModel: PinSetupViewModel = viewModel(factory = GenericViewModelFactory {
                PinSetupViewModel(appContainer.encryptionRepository)
            })
            PinSetupScreen(
                viewModel = pinSetupViewModel,
                onSetupComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.PinSetup.route) { inclusive = true }
                    }
                },
                language = language,
                onLanguageChange = onLanguageChange
            )
        }

        composable(Screen.Lock.route) {
            val authViewModel: AuthViewModel = viewModel(factory = GenericViewModelFactory {
                AuthViewModel(appContainer.encryptionRepository)
            })
            LockScreen(
                viewModel = authViewModel,
                onAuthenticated = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Lock.route) { inclusive = true }
                    }
                },
                language = language
            )
        }

        composable(Screen.Home.route) {
            val diaryViewModel: DiaryViewModel = viewModel(factory = GenericViewModelFactory {
                DiaryViewModel(appContainer.diaryRepository)
            })
            HomeScreen(
                viewModel = diaryViewModel,
                onEntryClick = { entryId ->
                    navController.navigate(Screen.Detail.passId(entryId))
                },
                onAddClick = {
                    navController.navigate(Screen.AddEditEntry.passId())
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                language = language
            )
        }

        composable(
            route = Screen.AddEditEntry.route,
            arguments = listOf(navArgument("entryId") { type = NavType.LongType; defaultValue = -1L })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: -1L
            val diaryViewModel: DiaryViewModel = viewModel(factory = GenericViewModelFactory {
                DiaryViewModel(appContainer.diaryRepository)
            })
            AddEditEntryScreen(
                entryId = entryId,
                viewModel = diaryViewModel,
                onBack = { navController.popBackStack() },
                language = language
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("entryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: -1L
            val diaryViewModel: DiaryViewModel = viewModel(factory = GenericViewModelFactory {
                DiaryViewModel(appContainer.diaryRepository)
            })
            DetailScreen(
                entryId = entryId,
                viewModel = diaryViewModel,
                onEditClick = { id ->
                    navController.navigate(Screen.AddEditEntry.passId(id))
                },
                onBack = { navController.popBackStack() },
                language = language
            )
        }

        composable(Screen.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel(factory = GenericViewModelFactory {
                SettingsViewModel(appContainer.settingsRepository, appContainer.encryptionRepository)
            })
            SettingsScreen(
                viewModel = settingsViewModel,
                onAboutMeClick = {
                    navController.navigate(Screen.AboutMe.route)
                }
            )
        }

        composable(Screen.AboutMe.route) {
            AboutMeScreen(
                onBack = { navController.popBackStack() },
                language = language
            )
        }
    }
}

class GenericViewModelFactory<T : ViewModel>(private val creator: () -> T) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return creator() as T
    }
}
