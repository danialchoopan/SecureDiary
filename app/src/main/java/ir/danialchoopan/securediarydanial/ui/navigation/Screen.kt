package ir.danialchoopan.securediarydanial.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object PinSetup : Screen("pin_setup")
    object Lock : Screen("lock")
    object Home : Screen("home")
    object AddEditEntry : Screen("add_edit_entry?entryId={entryId}") {
        fun passId(entryId: Long = -1L): String {
            return "add_edit_entry?entryId=$entryId"
        }
    }
    object Detail : Screen("detail/{entryId}") {
        fun passId(entryId: Long): String {
            return "detail/$entryId"
        }
    }
    object Settings : Screen("settings")
    object AboutMe : Screen("about_me")
}
