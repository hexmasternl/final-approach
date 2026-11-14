package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import nl.hexmaster.finalapproach.ui.components.FaDialogTone
import nl.hexmaster.finalapproach.ui.components.FaIconButton
import nl.hexmaster.finalapproach.ui.components.FaScoreCounter
import nl.hexmaster.finalapproach.ui.components.touchInset
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens

internal object HudText {
    const val BEST = "Best"
    const val SCORE = "Score"
    const val OPTIONS = "Options"
    const val PAUSED_EYEBROW = "Tower · holding"
    const val PAUSED_TITLE = "Paused"
    const val PAUSED_BODY = "The tower is holding traffic."
    const val CONTINUE = "Continue"
    const val RESTART = "Restart"
    const val QUIT = "Quit"
}

/**
 * How far the touch target of a HUD control reaches beyond what is drawn of it: the controls are
 * drawn `strip-height` high in the middle of a full touch target.
 */
private val hudTouchOverhang: Dp = touchInset(FaDimens.stripHeight)

/** Distance from the top inset to the top of the HUD rows: what is drawn stands `space-4` from the edge. */
internal val hudRowTopPadding: Dp = FaDimens.space4 - hudTouchOverhang

/** Height of the HUD rows: a full touch target, with the strips and the controls in its middle. */
internal val hudRowHeight: Dp = FaDimens.touchMin

/**
 * The overlay of a running level, laid out as the design system's Hud Bar: one row with the best
 * score (with a star) and the current score as flight strips at the top left, the cog wheel at the
 * top right. The strips are flat readouts; the cog wheel is the only thing on the bar with a lip,
 * next to Skip tutorial while the tutorial runs. It only reads what it is given.
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
            // Ignoring bar visibility: the strips keep their place when the bars hide or show briefly.
            .windowInsetsPadding(WindowInsets.systemBarsIgnoringVisibility.union(WindowInsets.displayCutout))
            // What is drawn stands `space-4` from the edges; the touch targets reach a little further out.
            .padding(
                start = FaDimens.space4,
                top = hudRowTopPadding,
                end = FaDimens.space4 - hudTouchOverhang,
            ),
    ) {
        Row(
            Modifier.align(Alignment.TopStart).height(hudRowHeight),
            horizontalArrangement = Arrangement.spacedBy(FaDimens.space3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FaScoreCounter(HudText.BEST, best, icon = FaIcon.Star)
            FaScoreCounter(HudText.SCORE, score, accent = true, popOnRise = true)
        }
        Row(
            Modifier.align(Alignment.TopEnd).height(hudRowHeight),
            horizontalArrangement = Arrangement.spacedBy(FaDimens.space3 - hudTouchOverhang),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Only while the first-time tutorial runs.
            if (onSkipTutorial != null) {
                FaButton(TutorialText.SKIP, onSkipTutorial, variant = FaButtonVariant.Ghost, size = FaButtonSize.Small, onField = true)
            }
            FaIconButton(FaIcon.Settings, HudText.OPTIONS, onOptions, compact = true)
        }
    }
}

/**
 * Distance from the top of the screen to the bottom of the HUD when the system keeps [topInset]
 * clear at the top: `space-4` inset, one `strip-height` tall row, and under it the larger of the
 * contact shadow and the touch overhang of the compact icon button. The touch target reaches
 * further down than the shadow, so this covers everything the HUD draws and everything of it that
 * takes a touch.
 */
fun hudBottom(topInset: Dp): Dp =
    topInset + FaDimens.space4 + FaDimens.stripHeight + maxOf(FaDimens.contactOffsetField, hudTouchOverhang)

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
        eyebrow = HudText.PAUSED_EYEBROW,
        tone = FaDialogTone.Neutral,
    )
}
