package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.engine.LevelUnlockRules
import nl.hexmaster.finalapproach.engine.LevelUnlocks
import nl.hexmaster.finalapproach.ui.BackHandler
import nl.hexmaster.finalapproach.ui.sprites.drawSunburst
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

private object LevelSelectText {
    const val TITLE = "Choose a level"
}

/** The page after Take off: one card per level; tapping an unlocked card starts that level. */
@Composable
fun LevelSelectPage(
    host: AppHost,
    onStart: (levelId: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    levels: List<LevelConfig> = Levels.all,
) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    BackHandler(host, onBack)
    // The page is recreated when the player comes back from a level, so what a run earned shows at once.
    val unlocks = remember { LevelUnlocks() }
    val open = remember(host) { unlocks.settle(host.levelProgress, host.highScores) }

    Box(modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) { drawSunburst(center, 0f, colors) }
        Column(
            Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(FaDimens.space5),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BasicText(LevelSelectText.TITLE, style = type.displayMd.copy(color = colors.onSky, textAlign = TextAlign.Center))
            Box(Modifier.padding(top = FaDimens.space5).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Row(
                    Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = FaDimens.space2, vertical = FaDimens.space3),
                    horizontalArrangement = Arrangement.spacedBy(FaDimens.space4),
                ) {
                    levels.forEach { level ->
                        val locked = lockedInfoFor(level, levels, open, host)
                        LevelCard(level, levelCardState(level, host.highScores.best(level.id), locked), onClick = { onStart(level.id) })
                    }
                }
            }
        }
    }
}

/** The locked state of [level]'s card, or null when [level] is open. */
private fun lockedInfoFor(level: LevelConfig, levels: List<LevelConfig>, open: Set<String>, host: AppHost): LockedInfo? {
    if (level.id in open) return null
    val rule = LevelUnlockRules.Default.ruleFor(level.id) ?: return null
    val required = levels.firstOrNull { it.id == rule.requiredLevelId } ?: return null
    return lockedInfo(level, rule, required, host.levelProgress.totals(required.id))
}
