package nl.hexmaster.finalapproach.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.platform.GatedSoundPlayer
import nl.hexmaster.finalapproach.platform.SettingsController
import nl.hexmaster.finalapproach.ui.game.GameScreen
import nl.hexmaster.finalapproach.ui.game.LevelSelectPage
import nl.hexmaster.finalapproach.ui.menu.MainPage
import nl.hexmaster.finalapproach.ui.settings.SettingsPage
import nl.hexmaster.finalapproach.ui.splash.SplashPage
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** The screens of the app. It opens on the splash page; after that the main page is home. */
enum class Screen { Splash, Main, LevelSelect, Game, Settings }

/** The whole app. The host supplies everything platform-specific through [host]. */
@Composable
fun App(host: AppHost) {
    val settingsController = remember(host) { SettingsController(host.settings) }
    val settings by settingsController.settings.collectAsState()
    // Every sound effect passes this one gate; the game never reads the setting itself.
    val sounds = remember(host, settingsController) {
        GatedSoundPlayer(host.sounds) { settingsController.settings.value.soundEffects }
    }
    FaTheme(reduceMotion = settings.reduceMotion.resolve(host.reduceMotion)) {
        var screen by rememberSaveable { mutableStateOf(Screen.Splash) }
        var levelId by rememberSaveable { mutableStateOf(Levels.meadowField.id) }
        // Have an ad ready by the time the player starts a level.
        LaunchedEffect(Unit) { host.ads.preload() }
        when (screen) {
            Screen.Splash -> SplashPage(host = host, onFinished = { screen = Screen.Main })

            Screen.Main -> MainPage(
                host = host,
                onTakeOff = { screen = Screen.LevelSelect },
                onSettings = { screen = Screen.Settings },
            )

            Screen.LevelSelect -> LevelSelectPage(
                host = host,
                onStart = { id ->
                    levelId = id
                    screen = Screen.Game
                },
                onBack = { screen = Screen.Main },
            )

            // Quit, the game-ended "Main page" action and back on that dialog all lead to the main page.
            Screen.Game -> GameScreen(
                host = host,
                sounds = sounds,
                onQuit = { screen = Screen.Main },
                level = Levels.byId(levelId) ?: Levels.meadowField,
            )

            Screen.Settings -> SettingsPage(
                host = host,
                settings = settings,
                onSoundEffects = { on -> settingsController.update { it.copy(soundEffects = on) } },
                onKeepScreenOn = { on -> settingsController.update { it.copy(keepScreenOn = on) } },
                onReduceMotion = { choice -> settingsController.update { it.copy(reduceMotion = choice) } },
                onBack = { screen = Screen.Main },
            )
        }
    }
}
