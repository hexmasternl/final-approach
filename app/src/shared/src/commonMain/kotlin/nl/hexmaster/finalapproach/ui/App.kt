package nl.hexmaster.finalapproach.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.ui.menu.MainPage
import nl.hexmaster.finalapproach.ui.splash.SplashPage
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** The screens of the app. It opens on the splash page; after that the main page is home. */
enum class Screen { Splash, Main, Game, Settings }

private object AppText {
    const val GAME_TITLE = "Cleared for take off"
    const val GAME_BODY = "The airfield is still being built. Check back soon."
    const val SETTINGS_TITLE = "Settings"
    const val SETTINGS_BODY = "Sound and music switches will live here."
}

/** The whole app. The host supplies everything platform-specific through [host]. */
@Composable
fun App(host: AppHost) {
    FaTheme(reduceMotion = host.reduceMotion) {
        var screen by rememberSaveable { mutableStateOf(Screen.Splash) }
        when (screen) {
            Screen.Splash -> SplashPage(host = host, onFinished = { screen = Screen.Main })

            Screen.Main -> MainPage(
                host = host,
                onTakeOff = { screen = Screen.Game },
                onSettings = { screen = Screen.Settings },
            )

            // The game and the settings page have their own issues. Until they are built,
            // placeholders keep the flow from and back to the main page working.
            Screen.Game -> PlaceholderPage(
                host = host,
                title = AppText.GAME_TITLE,
                body = AppText.GAME_BODY,
                ground = PlaceholderGround.Lawn,
                onBack = { screen = Screen.Main },
            )

            Screen.Settings -> PlaceholderPage(
                host = host,
                title = AppText.SETTINGS_TITLE,
                body = AppText.SETTINGS_BODY,
                ground = PlaceholderGround.Sky,
                onBack = { screen = Screen.Main },
            )
        }
    }
}
