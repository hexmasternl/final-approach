package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.LevelScenery
import nl.hexmaster.finalapproach.domain.LevelTheme
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.ui.BackHandler
import nl.hexmaster.finalapproach.ui.components.faBlock
import nl.hexmaster.finalapproach.ui.sprites.drawSunburst
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaShadows
import nl.hexmaster.finalapproach.ui.theme.FaTheme

private object LevelSelectText {
    const val TITLE = "Choose a level"
}

private object LevelSelectDefaults {
    /** Side of a square level tile. */
    val tile = 168.dp

    /** Side of the miniature inside a tile. */
    val miniature = 120.dp
    const val DAWN_ALPHA = 0.35f
    const val RUNWAY_STROKE_FRACTION = 0.7f
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
            BasicText(LevelSelectText.TITLE, style = type.displayMd.copy(color = colors.ink, textAlign = TextAlign.Center))
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

@Composable
private fun LevelTile(level: LevelConfig, onClick: () -> Unit) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    Column(
        Modifier
            .size(LevelSelectDefaults.tile)
            .faBlock(colors.surface, colors.outline, FaDimens.line3, FaDimens.radiusLg, FaShadows.lipLg)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(FaDimens.space3),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(Modifier.size(LevelSelectDefaults.miniature)) { drawLevelMiniature(level, colors) }
        BasicText(level.name, style = type.titleSm.copy(color = colors.ink, textAlign = TextAlign.Center))
    }
}

/** Draws the layout of [level] scaled into the square draw area. */
private fun DrawScope.drawLevelMiniature(level: LevelConfig, colors: FaColors) {
    val world = level.world
    val scale = minOf(size.width / world.width.toFloat(), size.height / world.height.toFloat())
    val originX = (size.width - world.width.toFloat() * scale) / 2f
    val originY = (size.height - world.height.toFloat() * scale) / 2f
    fun at(x: Double, y: Double) = Offset(originX + x.toFloat() * scale, originY + y.toFloat() * scale)

    val ground = when (level.landscape) {
        LevelScenery.Island -> colors.sky300
        else -> colors.grass500
    }
    drawRect(ground, topLeft = Offset(originX, originY), size = Size(world.width.toFloat() * scale, world.height.toFloat() * scale))
    level.island?.let { island ->
        drawOval(
            colors.grass300,
            topLeft = at(island.center.x - island.radiusX, island.center.y - island.radiusY),
            size = Size(island.radiusX.toFloat() * 2f * scale, island.radiusY.toFloat() * 2f * scale),
        )
    }
    if (level.theme == LevelTheme.Dawn) {
        drawRect(
            colors.sky700.copy(alpha = LevelSelectDefaults.DAWN_ALPHA),
            topLeft = Offset(originX, originY),
            size = Size(world.width.toFloat() * scale, world.height.toFloat() * scale),
        )
    }
    level.runways.forEach { runway ->
        val half = runway.length / 2.0
        val dx = kotlin.math.sin(runway.orientation)
        val dy = -kotlin.math.cos(runway.orientation)
        drawLine(
            colors.asphalt500,
            at(runway.position.x - dx * half, runway.position.y - dy * half),
            at(runway.position.x + dx * half, runway.position.y + dy * half),
            strokeWidth = (runway.width * LevelSelectDefaults.RUNWAY_STROKE_FRACTION).toFloat() * scale * 2f,
            cap = StrokeCap.Butt,
        )
    }
    level.helipads.forEach { pad ->
        drawCircle(colors.asphalt300, pad.radius.toFloat() * scale, at(pad.position.x, pad.position.y))
    }
}
