package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import nl.hexmaster.finalapproach.ui.components.FaButton
import nl.hexmaster.finalapproach.ui.components.FaButtonSize
import nl.hexmaster.finalapproach.ui.components.FaButtonVariant
import nl.hexmaster.finalapproach.ui.components.FaDialog
import nl.hexmaster.finalapproach.ui.components.FaDialogAction
import nl.hexmaster.finalapproach.ui.components.FaIconButton
import nl.hexmaster.finalapproach.ui.components.FaScoreCounter
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaShadows
import nl.hexmaster.finalapproach.ui.theme.FaTheme

internal object HudText {
    const val BEST = "Best"
    const val SCORE = "Score"
    const val OPTIONS = "Options"
    const val PAUSED_TITLE = "Paused"
    const val PAUSED_BODY = "The tower is holding traffic."
    const val CONTINUE = "Continue"
    const val RESTART = "Restart"
    const val QUIT = "Quit"
}

/**
 * The overlay of a running level, laid out as the design system's Hud Bar: one row with the best
 * score (compact, with a star) and the current score at the top left, the cog wheel at the top
 * right. It only reads what it is given.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Hud(
    best: Int,
    score: Int,
    onOptions: () -> Unit,
    modifier: Modifier = Modifier,
    onSkipTutorial: (() -> Unit)? = null,
) {
    Box(
        modifier
            .fillMaxSize()
            // Ignoring bar visibility: the chips keep their place when the bars hide or show briefly.
            .windowInsetsPadding(WindowInsets.systemBarsIgnoringVisibility.union(WindowInsets.displayCutout))
            .padding(FaDimens.space4),
    ) {
        Row(
            Modifier.align(Alignment.TopStart),
            horizontalArrangement = Arrangement.spacedBy(FaDimens.space3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FaScoreCounter(HudText.BEST, best, compact = true, icon = FaIcon.Star, iconColor = FaTheme.colors.sunDeep)
            FaScoreCounter(HudText.SCORE, score, popOnRise = true)
        }
        Row(
            Modifier.align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(FaDimens.space3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Only while the first-time tutorial runs.
            if (onSkipTutorial != null) {
                FaButton(TutorialText.SKIP, onSkipTutorial, variant = FaButtonVariant.Ghost, size = FaButtonSize.Small)
            }
            FaIconButton(FaIcon.Settings, HudText.OPTIONS, onOptions)
        }
    }
}

/**
 * Distance from the top of the screen to the bottom of the HUD, lip included, when the system
 * keeps [topInset] clear at the top: `space-4` inset, then one `hud-chip` tall row and its lip.
 */
fun hudBottom(topInset: Dp): Dp = topInset + FaDimens.space4 + FaDimens.hudChip + FaShadows.lip

/** The options panel: Continue, Restart and Quit over a frozen game. */
@Composable
fun OptionsPanel(onContinue: () -> Unit, onRestart: () -> Unit, onQuit: () -> Unit) {
    FaDialog(
        HudText.PAUSED_TITLE,
        HudText.PAUSED_BODY,
        actions = listOf(
            FaDialogAction(HudText.CONTINUE, onContinue, FaButtonVariant.Primary),
            FaDialogAction(HudText.RESTART, onRestart, FaButtonVariant.Secondary, FaIcon.Restart),
            FaDialogAction(HudText.QUIT, onQuit, FaButtonVariant.Danger),
        ),
    )
}
