package nl.hexmaster.finalapproach.ui.sprites

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * The strokes and corner scenery is drawn with, in pixels: the [outline] (`line-2`), the [fine]
 * detail line (`line-fine`) and the [corner] radius of flat scenery (`radius-sm`).
 */
internal class SceneryLines(val outline: Float, val fine: Float, val corner: Float)

/**
 * The scenery lines for a world drawn at [pxPerWorldUnit]. Scenery belongs to the airfield, so
 * its lines are world lengths, [SCENERY_UNITS_PER_DP] world units per dp of their token, and
 * shrink with the field; they are never wider on screen than their token. That keeps scenery
 * lighter than an aircraft, whose outline does not shrink with the world zoom.
 */
internal fun DrawScope.sceneryLines(pxPerWorldUnit: Float): SceneryLines {
    val pxPerUnit = pxPerWorldUnit * SCENERY_UNITS_PER_DP
    fun shrinking(token: Dp): Float = min(token.toPx(), token.value * pxPerUnit)
    return SceneryLines(
        outline = shrinking(FaDimens.line2),
        fine = shrinking(FaDimens.lineFine),
        corner = shrinking(FaDimens.radiusSm),
    )
}

/** World units a scenery line is wide per dp of its token. */
private const val SCENERY_UNITS_PER_DP = 0.8f

private class ParsedSceneryPart(val part: SceneryPart, val path: Path)

private val parsedScenery: Map<ScenerySprite, List<ParsedSceneryPart>> by lazy {
    ScenerySprite.entries.associateWith { sprite -> sprite.parts.map { ParsedSceneryPart(it, parsePath(it.pathData)) } }
}

private fun sceneryStroke(width: Float) = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)

/**
 * Draws a scenery sprite: no shadow, flat fills from the theme, [lines] for its strokes.
 *
 * @param center where the middle of the sprite goes, in pixels
 * @param widthPx room across the sprite, in pixels
 * @param lengthPx room along the sprite, front to back, in pixels; the sprite keeps its
 *   proportions and is fitted into this room
 * @param rotationDegrees where its front points, clockwise from straight up
 */
internal fun DrawScope.drawScenerySprite(
    sprite: ScenerySprite,
    center: Offset,
    widthPx: Float,
    lengthPx: Float,
    rotationDegrees: Float,
    colors: FaColors,
    lines: SceneryLines,
) {
    val scale = min(widthPx / sprite.width, lengthPx / sprite.height)
    if (scale <= 0f) return
    withTransform({
        translate(center.x, center.y)
        rotate(rotationDegrees, Offset.Zero)
        scale(scale, scale, Offset.Zero)
        translate(-sprite.width / 2f, -sprite.height / 2f)
    }) {
        for (parsed in parsedScenery.getValue(sprite)) {
            val part = parsed.part
            part.fill?.let { drawPath(parsed.path, colors.scenery(it)) }
            // The canvas is scaled to the sprite's grid, so a stroke of so many pixels is that
            // many pixels divided by the scale.
            val width = when (part.stroke) {
                SceneryStroke.None -> null
                SceneryStroke.Outline -> lines.outline
                SceneryStroke.Fine -> lines.fine
            }
            if (width != null) drawPath(parsed.path, colors.scenery(part.strokePaint), style = sceneryStroke(width / scale))
        }
    }
}

/**
 * Draws [sprite] piece after piece along a strip: a hedge, a fence, a forest edge. The pieces
 * are spread evenly over the length, so the row starts and ends on a whole piece.
 *
 * @param center middle of the strip, in pixels
 * @param widthPx width of the strip; one piece is this wide, less twice the [stagger]
 * @param lengthPx length of the strip along [rotationDegrees]
 * @param overlap how far a piece overlaps the one before it, as a fraction of its length
 * @param stagger how far pieces step aside, to the left and to the right in turn, as a fraction
 *   of the strip's width
 */
internal fun DrawScope.drawSceneryRow(
    sprite: ScenerySprite,
    center: Offset,
    widthPx: Float,
    lengthPx: Float,
    rotationDegrees: Float,
    colors: FaColors,
    lines: SceneryLines,
    overlap: Float = 0f,
    stagger: Float = 0f,
) {
    val pieceWidth = widthPx * (1f - 2f * stagger)
    val pieceLength = pieceWidth * sprite.height / sprite.width
    val pitch = pieceLength * (1f - overlap)
    if (pitch <= 0f) return
    val count = max(1, ((lengthPx - pieceLength) / pitch).roundToInt() + 1)
    val step = if (count > 1) (lengthPx - pieceLength) / (count - 1) else 0f
    val first = -(count - 1) * step / 2f
    withTransform({
        translate(center.x, center.y)
        rotate(rotationDegrees, Offset.Zero)
    }) {
        // From the far end towards the front, so each piece lies over the one behind it.
        for (i in count - 1 downTo 0) {
            val aside = if (stagger == 0f) 0f else widthPx * stagger * (if (i % 2 == 0) -1f else 1f)
            drawScenerySprite(sprite, Offset(aside, first + i * step), pieceWidth, pieceLength, 0f, colors, lines)
        }
    }
}
