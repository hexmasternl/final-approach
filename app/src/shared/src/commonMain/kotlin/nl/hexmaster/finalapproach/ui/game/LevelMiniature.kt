package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import nl.hexmaster.finalapproach.domain.Bounds
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.LevelTheme
import nl.hexmaster.finalapproach.domain.OrientedRect
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.sprites.GroundMetrics
import nl.hexmaster.finalapproach.ui.theme.DawnColors
import nl.hexmaster.finalapproach.ui.theme.DayColors
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.NightColors
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** Tunable values of the level tile's miniature, in world units. */
internal object MiniatureDefaults {
    /** Room kept around every runway and helipad, with its fringe, so none touches the edge of the tile. */
    const val MARGIN = 40.0
}

/** The square of the world a level card's art shows: its top-left corner and its side, in world units. */
internal class MiniatureCrop(val left: Double, val top: Double, val side: Double) {
    val right: Double get() = left + side
    val bottom: Double get() = top + side

    fun contains(point: Vec2): Boolean = point.x in left..right && point.y in top..bottom
}

/** The corners of the rough-grass fringe of every runway and helipad of [level]: the outermost thing each draws. */
internal fun landingZoneCorners(level: LevelConfig): List<Vec2> {
    val runway = GroundMetrics.Runway
    val runways = level.runways.flatMap {
        val across = it.width / runway.width
        OrientedRect(it.position, it.orientation, it.length + 2.0 * runway.fringe, 2.0 * runway.fringeHalf * across).corners
    }
    val helipads = level.helipads.flatMap {
        val half = GroundMetrics.Helipad.Fringe.half * it.radius / GroundMetrics.Helipad.disc
        listOf(
            Vec2(it.position.x - half, it.position.y - half), Vec2(it.position.x + half, it.position.y - half),
            Vec2(it.position.x - half, it.position.y + half), Vec2(it.position.x + half, it.position.y + half),
        )
    }
    return runways + helipads
}

/**
 * The part of the world the level tile shows: the smallest square around every runway and
 * helipad with its fringe, [MiniatureDefaults.MARGIN] of room around them, kept inside the world.
 * The square is as large as the shortest side of the world at most, so a level whose landing
 * zones spread wider than that shows as much as fits.
 */
internal fun miniatureCrop(level: LevelConfig): MiniatureCrop {
    val corners = landingZoneCorners(level)
    val world = Bounds(0.0, 0.0, level.world.width, level.world.height)
    val shapes = Bounds(corners.minOf { it.x }, corners.minOf { it.y }, corners.maxOf { it.x }, corners.maxOf { it.y })
    val side = min(max(shapes.width, shapes.height) + 2.0 * MiniatureDefaults.MARGIN, min(world.width, world.height))
    val centreX = (shapes.left + shapes.right) / 2.0
    val centreY = (shapes.top + shapes.bottom) / 2.0
    val left = (centreX - side / 2.0).coerceIn(world.left, world.right - side)
    val top = (centreY - side / 2.0).coerceIn(world.top, world.bottom - side)
    return MiniatureCrop(left, top, side)
}

/**
 * How a crop is laid onto a tile of [tilePx] pixels: [pxPerUnit] pixels per world unit, and the
 * tile position of the world origin, so the crop's top-left corner lands on pixel 0,0 and its
 * side on the whole tile.
 */
internal class MiniatureView(val pxPerUnit: Float, val originX: Float, val originY: Float) {
    fun toTile(point: Vec2): Offset = Offset(originX + point.x.toFloat() * pxPerUnit, originY + point.y.toFloat() * pxPerUnit)
}

/**
 * How a crop is laid onto an art area of [widthPx] by [heightPx] pixels: the whole crop square fits
 * the shorter side, and the longer side shows more of the world around it, centred on the crop and
 * kept inside the world of [worldWidth] by [worldHeight] units where it can be.
 */
internal fun miniatureView(crop: MiniatureCrop, widthPx: Float, heightPx: Float, worldWidth: Double, worldHeight: Double): MiniatureView {
    val pxPerUnit = min(widthPx, heightPx) / crop.side.toFloat()
    val visibleWidth = widthPx.toDouble() / pxPerUnit
    val visibleHeight = heightPx.toDouble() / pxPerUnit
    val left = (crop.left + crop.side / 2.0 - visibleWidth / 2.0).coerceIn(0.0, max(0.0, worldWidth - visibleWidth))
    val top = (crop.top + crop.side / 2.0 - visibleHeight / 2.0).coerceIn(0.0, max(0.0, worldHeight - visibleHeight))
    return MiniatureView(pxPerUnit, (-left).toFloat() * pxPerUnit, (-top).toFloat() * pxPerUnit)
}

/**
 * [level]'s own field, cropped to [miniatureCrop], in the art area of a level card: the same code the game draws
 * with ([drawFieldStatic]), in [colors], the palette of the level's own theme. Only the crop is
 * drawn, once, into a bitmap the size of the tile. The bitmap is rebuilt when the size changes or
 * when the level, palette, text styles or ground art change, and not on any other recomposition.
 */
@Composable
internal fun LevelMiniature(
    level: LevelConfig,
    colors: FaColors,
    measurer: TextMeasurer,
    numberStyle: TextStyle,
    art: GroundArt,
    modifier: Modifier = Modifier,
) {
    val record = remember(level, colors, measurer, numberStyle, art) { miniatureRecorder(level, colors, measurer, numberStyle, art) }
    Spacer(modifier.drawWithCache(record))
}

private fun miniatureRecorder(
    level: LevelConfig,
    colors: FaColors,
    measurer: TextMeasurer,
    numberStyle: TextStyle,
    art: GroundArt,
): CacheDrawScope.() -> DrawResult = {
    val widthPx = max(1, size.width.roundToInt())
    val heightPx = max(1, size.height.roundToInt())
    val view = miniatureView(miniatureCrop(level), widthPx.toFloat(), heightPx.toFloat(), level.world.width, level.world.height)
    val bitmap = ImageBitmap(widthPx, heightPx)
    CanvasDrawScope().draw(this, layoutDirection, Canvas(bitmap), Size(widthPx.toFloat(), heightPx.toFloat())) {
        val worldPx = Size(level.world.width.toFloat() * view.pxPerUnit, level.world.height.toFloat() * view.pxPerUnit)
        val viewport = WorldViewport(level.world.width, level.world.height, worldPx.width.toDouble(), worldPx.height.toDouble())
        translate(view.originX, view.originY) {
            drawFieldStatic(level, viewport, colors, measurer, numberStyle, art)
        }
    }
    onDrawBehind {
        drawImage(bitmap, dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()))
    }
}

/** The palette a level is drawn in: its own theme's, whatever palette the page around it uses. */
internal fun levelPalette(level: LevelConfig): FaColors = when (level.theme) {
    LevelTheme.Day -> DayColors
    LevelTheme.Night -> NightColors
    LevelTheme.Dawn -> DawnColors
}
