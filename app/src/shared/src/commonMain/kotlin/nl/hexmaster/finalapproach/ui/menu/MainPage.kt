package nl.hexmaster.finalapproach.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.ui.BackHandler
import nl.hexmaster.finalapproach.ui.components.FaButton
import nl.hexmaster.finalapproach.ui.components.FaButtonSize
import nl.hexmaster.finalapproach.ui.components.FaButtonVariant
import nl.hexmaster.finalapproach.ui.components.FaDialog
import nl.hexmaster.finalapproach.ui.components.FaDialogAction
import nl.hexmaster.finalapproach.ui.components.FaWordmark
import nl.hexmaster.finalapproach.ui.components.FaWordmarkSize
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
    /** The label style is written in capitals. */
    fun version(name: String) = "V ${name.uppercase()}"
}

private object MainPageDefaults {
    /** Width of the column that holds the two buttons. */
    val menuWidth = 280.dp

    /** Longest step the backdrop takes in one frame, so a stalled frame does not make it jump. */
    const val MAX_FRAME_SECONDS = 0.1f
}

/**
 * The home screen: the Final Approach wordmark and the Take off and Settings buttons in front of
 * the animated sky, the app version in the bottom-right corner, and quitting through the back
 * button with a confirmation. The regular wordmark sits above the menu; when that does not fit
 * the height inside the system bars, the small wordmark sits to the left of it.
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
    if (!FaTheme.reduceMotion) {
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

        // While the dialog is open, the page behind it is hidden from screen readers.
        val behindDialog = if (quitOpen) Modifier.clearAndSetSemantics { } else Modifier
        Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).then(behindDialog)) {
            MainMenuLayout(
                versionLabel = {
                    BasicText(
                        MainPageText.version(host.versionName),
                        style = FaTheme.type.label.copy(color = FaTheme.colors.onSkyMuted),
                    )
                },
                wordmark = { FaWordmark(size = it) },
                menu = {
                    Column(
                        Modifier.width(MainPageDefaults.menuWidth),
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
                },
            )
        }

        if (quitOpen) {
            FaDialog(
                title = MainPageText.QUIT_TITLE,
                body = MainPageText.QUIT_BODY,
                actions = listOf(
                    FaDialogAction(MainPageText.QUIT, host::quit, FaButtonVariant.Danger),
                    FaDialogAction(MainPageText.CANCEL, { quitOpen = false }, FaButtonVariant.Ghost),
                ),
            )
        }
    }
}

private enum class MainMenuSlot { Version, Menu, Regular, Small }

/**
 * Places the wordmark and the menu as one centred group and the version label bottom-right. The
 * arrangement is chosen from measured sizes, not from orientation.
 */
@Composable
private fun MainMenuLayout(
    versionLabel: @Composable () -> Unit,
    wordmark: @Composable (FaWordmarkSize) -> Unit,
    menu: @Composable () -> Unit,
) {
    val edge = FaDimens.space4
    val gap = FaDimens.space7
    SubcomposeLayout(Modifier.fillMaxSize()) { constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val edgePx = edge.roundToPx()
        val gapPx = gap.roundToPx()
        val version = subcompose(MainMenuSlot.Version, versionLabel).map { it.measure(loose) }.first()
        val menuBox = subcompose(MainMenuSlot.Menu, menu).map { it.measure(loose) }.first()
        val regular = subcompose(MainMenuSlot.Regular) { wordmark(FaWordmarkSize.Regular) }.map { it.measure(loose) }.first()

        val reserved = 2 * (version.height + edgePx)
        val available: Dp = (constraints.maxHeight - reserved).coerceAtLeast(0).toDp()
        val stacked: Dp = (regular.height + gapPx + menuBox.height).toDp()
        val arrangement = chooseMainMenuArrangement(available, stacked)

        val small = if (arrangement == MainMenuArrangement.Beside) {
            subcompose(MainMenuSlot.Small) { wordmark(FaWordmarkSize.Small) }.map { it.measure(loose) }.first()
        } else {
            null
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            if (small == null) {
                val top = (constraints.maxHeight - regular.height - gapPx - menuBox.height) / 2
                regular.place((constraints.maxWidth - regular.width) / 2, top)
                menuBox.place((constraints.maxWidth - menuBox.width) / 2, top + regular.height + gapPx)
            } else {
                val left = (constraints.maxWidth - small.width - gapPx - menuBox.width) / 2
                small.place(left, (constraints.maxHeight - small.height) / 2)
                menuBox.place(left + small.width + gapPx, (constraints.maxHeight - menuBox.height) / 2)
            }
            version.place(constraints.maxWidth - version.width - edgePx, constraints.maxHeight - version.height - edgePx)
        }
    }
}
