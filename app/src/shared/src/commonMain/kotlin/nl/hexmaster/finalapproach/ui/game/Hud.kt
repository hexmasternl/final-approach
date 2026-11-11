package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.ui.components.FaButton
import nl.hexmaster.finalapproach.ui.components.FaButtonVariant
import nl.hexmaster.finalapproach.ui.components.FaDialog
import nl.hexmaster.finalapproach.ui.components.FaIconButton
import nl.hexmaster.finalapproach.ui.components.FaSpawnCounter
import nl.hexmaster.finalapproach.ui.components.FaStatChip
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens

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
 * The overlay of a running level: best and current score and a spawn counter per type at the
 * top left, the cog wheel at the top right. It only reads what it is given.
 */
@Composable
fun Hud(
    best: Int,
    score: Int,
    types: List<AircraftType>,
    spawned: Map<AircraftType, Int>,
    onOptions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(FaDimens.space4),
    ) {
        Column(Modifier.align(Alignment.TopStart), verticalArrangement = Arrangement.spacedBy(FaDimens.space2)) {
            Row(horizontalArrangement = Arrangement.spacedBy(FaDimens.space2)) {
                FaStatChip(HudText.BEST, best)
                FaStatChip(HudText.SCORE, score, popOnRise = true)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(FaDimens.space2)) {
                types.forEach { type -> FaSpawnCounter(type, spawned[type] ?: 0) }
            }
        }
        FaIconButton(FaIcon.Settings, HudText.OPTIONS, onOptions, Modifier.align(Alignment.TopEnd))
    }
}

/** The options panel: Continue, Restart and Quit over a frozen game. */
@Composable
fun OptionsPanel(onContinue: () -> Unit, onRestart: () -> Unit, onQuit: () -> Unit) {
    FaDialog(HudText.PAUSED_TITLE, HudText.PAUSED_BODY) {
        FaButton(HudText.CONTINUE, onContinue, Modifier.fillMaxWidth(), FaButtonVariant.Primary)
        FaButton(HudText.RESTART, onRestart, Modifier.fillMaxWidth(), FaButtonVariant.Secondary, icon = FaIcon.Restart)
        FaButton(HudText.QUIT, onQuit, Modifier.fillMaxWidth(), FaButtonVariant.Danger)
    }
}
