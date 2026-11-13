package nl.hexmaster.finalapproach.ui.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.sin
import nl.hexmaster.finalapproach.engine.GameEngine
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion
import nl.hexmaster.finalapproach.ui.theme.FaOpacity

/** How a path looks this frame. */
internal data class PathLook(
    val selected: Boolean,
    /** Seconds since a landing zone cut the path off, or null when it has not. */
    val sinceLocked: Double?,
    val reduceMotion: Boolean,
    val timeSeconds: Double,
)

/** The livery colour of livery index [index]: `path1` to `path6`. */
internal fun FaColors.livery(index: Int): Color = when (index.mod(LIVERY_COUNT)) {
    0 -> path1
    1 -> path2
    2 -> path3
    3 -> path4
    4 -> path5
    else -> path6
}

internal const val LIVERY_COUNT = GameEngine.LIVERY_COUNT

private const val MS_PER_SECOND = 1000.0

/**
 * Draws a flight path from its aircraft ([points] start at the aircraft, in pixels): an outline
 * casing and a livery dash, both dashed, ending in a dot. A path cut off by a landing zone
 * flashes white once ([FaColors.fuselage] over `dur-pop`); with reduced motion it turns solid
 * white instead and stays that way until the aircraft lands.
 */
internal fun DrawScope.drawFlightPath(points: List<Offset>, liveryColor: Color, look: PathLook, colors: FaColors) {
    if (points.size < 2) return
    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
    }
    val dash = FaDimens.pathDash.toPx()
    val gap = FaDimens.pathGap.toPx()
    val locked = look.sinceLocked != null
    val steady = locked && look.reduceMotion
    val marching = look.selected && !look.reduceMotion && !locked
    val phase = if (marching) {
        -((look.timeSeconds * MS_PER_SECOND / FaMotion.durMarchMs) % 1.0).toFloat() * (dash + gap)
    } else {
        0f
    }
    val effect = if (steady) null else PathEffect.dashPathEffect(floatArrayOf(dash, gap), phase)
    val flash = look.sinceLocked
        ?.takeIf { !look.reduceMotion }
        ?.let { it * MS_PER_SECOND / FaMotion.durPopMs }
        ?.takeIf { it < 1.0 }
        ?.let { sin(it * PI).toFloat() }
        ?: 0f
    val color = when {
        steady -> colors.fuselage
        else -> lerp(liveryColor, colors.fuselage, flash)
    }
    val alpha = if (look.selected || locked) 1f else FaOpacity.pathIdle

    fun stroke(width: Float) = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = effect)
    drawPath(path, colors.outline, alpha = alpha, style = stroke(FaDimens.pathCasing.toPx()))
    drawPath(path, color, alpha = alpha, style = stroke(FaDimens.pathStroke.toPx()))

    val end = points.last()
    val dotRadius = FaDimens.pathDot.toPx() / 2f
    drawCircle(color, dotRadius, end, alpha = alpha)
    drawCircle(colors.outline, dotRadius, end, alpha = alpha, style = Stroke(FaDimens.line2.toPx()))
}
