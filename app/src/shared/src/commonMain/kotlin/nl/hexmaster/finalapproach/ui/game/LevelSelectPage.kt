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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.ui.BackHandler
import nl.hexmaster.finalapproach.ui.components.FaMoulded
import nl.hexmaster.finalapproach.ui.components.tonePaper
import nl.hexmaster.finalapproach.ui.sprites.drawSunburst
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

private object LevelSelectText {
    const val TITLE = "Choose a level"
}

/** The page after Take off: one square tile per level; tapping a tile starts that level. */
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
                    levels.forEach { level -> LevelTile(level) { onStart(level.id) } }
                }
            }
        }
    }
}

/**
 * One level as a moulded card: a `level-tile` square, lip included, with the miniature and the
 * name on its paper face. It is pressed like a button.
 */
@Composable
internal fun LevelTile(level: LevelConfig, onClick: () -> Unit) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    val measurer = rememberTextMeasurer()
    FaMoulded(
        onClick = onClick,
        tone = colors.tonePaper,
        radius = FaDimens.radiusPanel,
        lip = FaDimens.lipPanel,
        modifier = Modifier.size(FaDimens.levelTile),
    ) {
        Column(
            Modifier.fillMaxSize().padding(start = FaDimens.space2, top = FaDimens.space2, end = FaDimens.space2, bottom = FaDimens.space1),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // The miniature is the level's own field, in the palette of the level's own theme.
            Box(Modifier.size(FaDimens.levelTileArt).levelMiniature(level, levelPalette(level), measurer, type.titleMd))
            BasicText(level.name, style = type.titleSm.copy(color = colors.ink, textAlign = TextAlign.Center))
        }
    }
}
