package nl.hexmaster.finalapproach.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.random.Random
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.ui.BackHandler
import nl.hexmaster.finalapproach.ui.components.FaButton
import nl.hexmaster.finalapproach.ui.components.FaButtonSize
import nl.hexmaster.finalapproach.ui.components.FaButtonVariant
import nl.hexmaster.finalapproach.ui.components.FaDialog
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

private object MainPageText {
    const val TAKE_OFF = "Take off"
    const val SETTINGS = "Settings"
    const val QUIT_TITLE = "Quit?"
    const val QUIT_BODY = "Leave the tower for now?"
    const val QUIT = "Quit"
    const val CANCEL = "Cancel"
    fun version(name: String) = "V $name"
}

private object MainPageDefaults {
    /** Width of the column that holds the two buttons. */
    val menuWidth = 280.dp

    /** Longest step the backdrop takes in one frame, so a stalled frame does not make it jump. */
    const val MAX_FRAME_SECONDS = 0.1f
}

/**
 * The home screen: Take off and Settings in front of the animated sky, the app version in the
 * bottom-right corner, and quitting through the back button with a confirmation.
 */
@Composable
fun MainPage(
    host: AppHost,
    onTakeOff: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
    config: MenuBackdropConfig = remember { MenuBackdropConfig() },
) {
    val simulation = remember(config) { MenuBackdropSimulation(config, Random.Default) }
    var backdrop by remember(config) { mutableStateOf(MenuBackdropState.initial(config)) }
    var screen by remember { mutableStateOf(IntSize.Zero) }
    var quitOpen by rememberSaveable { mutableStateOf(false) }
    val density = LocalDensity.current.density

    // With reduced motion the backdrop is never advanced: stripes and clouds stand still and
    // the aircraft slot stays empty.
    if (!host.reduceMotion) {
        LaunchedEffect(simulation) {
            var last = withFrameNanos { it }
            while (true) {
                val now = withFrameNanos { it }
                val delta = ((now - last) / 1_000_000_000f).coerceAtMost(MainPageDefaults.MAX_FRAME_SECONDS)
                last = now
                backdrop = simulation.step(backdrop, delta, screen.width / density, screen.height / density)
            }
        }
    }

    // Back opens the quit confirmation; with the confirmation open it closes it again.
    BackHandler(host) { quitOpen = !quitOpen }

    Box(modifier.fillMaxSize().onSizeChanged { screen = it }) {
        MenuBackdrop(backdrop, simulation, config, Modifier.fillMaxSize())

        Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            Column(
                Modifier.align(Alignment.Center).width(MainPageDefaults.menuWidth),
                verticalArrangement = Arrangement.spacedBy(FaDimens.space4),
            ) {
                FaButton(
                    text = MainPageText.TAKE_OFF,
                    onClick = onTakeOff,
                    modifier = Modifier.fillMaxWidth(),
                    size = FaButtonSize.Large,
                    icon = FaIcon.Play,
                )
                FaButton(
                    text = MainPageText.SETTINGS,
                    onClick = onSettings,
                    modifier = Modifier.fillMaxWidth(),
                    variant = FaButtonVariant.Ghost,
                    icon = FaIcon.Settings,
                )
            }

            BasicText(
                MainPageText.version(host.versionName),
                modifier = Modifier.align(Alignment.BottomEnd).padding(FaDimens.space4),
                style = FaTheme.type.label.copy(color = FaTheme.colors.onSkyMuted),
            )
        }

        if (quitOpen) {
            FaDialog(
                title = MainPageText.QUIT_TITLE,
                body = MainPageText.QUIT_BODY,
                animate = !host.reduceMotion,
            ) {
                FaButton(
                    text = MainPageText.QUIT,
                    onClick = host::quit,
                    modifier = Modifier.fillMaxWidth(),
                    variant = FaButtonVariant.Danger,
                )
                FaButton(
                    text = MainPageText.CANCEL,
                    onClick = { quitOpen = false },
                    modifier = Modifier.fillMaxWidth(),
                    variant = FaButtonVariant.Ghost,
                )
            }
        }
    }
}
