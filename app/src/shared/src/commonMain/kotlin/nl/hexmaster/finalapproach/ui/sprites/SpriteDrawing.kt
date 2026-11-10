package nl.hexmaster.finalapproach.ui.sprites

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaMotion

/** Stroke widths on the 96-unit sprite square, as the design system draws them. */
private const val OUTLINE_UNITS = 4f
private const val GLASS_OUTLINE_UNITS = 3f
private const val SHINE_UNITS = 2.5f
private const val LINE_UNITS = 4.5f

/** The rotor disc is the prop-blur colour at a little under half strength. */
private const val ROTOR_DISC_ALPHA = 0.45f

/** A propeller seen from above flicks between full width and this fraction of it. */
private const val PROPELLER_MIN_SCALE = 0.18f

/** Stroke of an icon on its 24-unit grid. */
private const val ICON_UNITS = 24f
private const val ICON_STROKE_UNITS = 3f

internal fun parsePath(pathData: String): Path = PathParser().parsePathString(pathData).toPath()

private class ParsedPart(val part: SpritePart, val path: Path)

private val parsedSprites: Map<AircraftType, List<ParsedPart>> by lazy {
    aircraftSprites.mapValues { (_, parts) -> parts.map { ParsedPart(it, parsePath(it.pathData)) } }
}

private val parsedSideViewLightProp: List<ParsedPart> by lazy {
    sideViewLightProp.map { ParsedPart(it, parsePath(it.pathData)) }
}

private fun roundStroke(width: Float) = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)

/**
 * Draws everything in [block] into an offscreen layer and composites it at [alpha], so shapes
 * that overlap inside the layer do not show through each other.
 */
internal inline fun DrawScope.alphaLayer(alpha: Float, block: DrawScope.() -> Unit) {
    drawIntoCanvas { canvas ->
        canvas.saveLayer(Rect(Offset.Zero, size), Paint().apply { this.alpha = alpha })
        block()
        canvas.restore()
    }
}

/**
 * Draws an aircraft sprite with its ground shadow.
 *
 * @param center where the middle of the sprite goes, in pixels
 * @param sizePx side length of the sprite square in pixels
 * @param headingDegrees clockwise from straight up
 * @param timeSeconds drives the propellers and the rotor; pass a constant to hold them still
 * @param shadowOffset where the ground shadow falls, in pixels; always down-right
 */
internal fun DrawScope.drawAircraft(
    type: AircraftType,
    livery: Color,
    center: Offset,
    sizePx: Float,
    headingDegrees: Float,
    timeSeconds: Float,
    colors: FaColors,
    shadowOffset: Offset,
) {
    val parts = parsedSprites.getValue(type)
    val shadow = colors.shadowGround
    alphaLayer(shadow.alpha) {
        drawSprite(parts, center + shadowOffset, sizePx, headingDegrees, timeSeconds) { part, path ->
            val solid = shadow.copy(alpha = 1f)
            if (part.paint != SpritePaint.Shine && part.paint != SpritePaint.Blur) {
                drawPath(path, solid)
                drawPath(path, solid, style = roundStroke(OUTLINE_UNITS))
            }
        }
    }
    drawSprite(parts, center, sizePx, headingDegrees, timeSeconds) { part, path ->
        paintPart(part, path, livery, colors)
    }
}

/**
 * Draws the side-view light prop, nose to the right, with no ground shadow: it is seen against
 * the sky.
 *
 * @param center where the middle of the sprite goes, in pixels
 * @param lengthPx nose-to-tail length in pixels
 */
internal fun DrawScope.drawSideViewLightProp(
    livery: Color,
    center: Offset,
    lengthPx: Float,
    timeSeconds: Float,
    colors: FaColors,
) {
    drawSprite(
        parts = parsedSideViewLightProp,
        center = center,
        sizePx = lengthPx,
        headingDegrees = 0f,
        timeSeconds = timeSeconds,
        gridWidth = SIDE_SPRITE_WIDTH,
        gridHeight = SIDE_SPRITE_HEIGHT,
    ) { part, path ->
        paintPart(part, path, livery, colors)
    }
}

private fun DrawScope.paintPart(part: SpritePart, path: Path, livery: Color, colors: FaColors) {
    val outline = colors.outline
    when (part.paint) {
        SpritePaint.Body -> fillAndOutline(path, colors.fuselage, outline, OUTLINE_UNITS)
        SpritePaint.Livery -> fillAndOutline(path, livery, outline, OUTLINE_UNITS)
        SpritePaint.Shade -> fillAndOutline(path, colors.fuselageShade, outline, OUTLINE_UNITS)
        SpritePaint.Glass -> fillAndOutline(path, colors.glass, outline, GLASS_OUTLINE_UNITS)
        SpritePaint.Shine -> drawPath(path, colors.glassShine, style = roundStroke(SHINE_UNITS))
        SpritePaint.Dark -> drawPath(path, outline)
        SpritePaint.Blur -> drawPath(path, colors.propBlur, alpha = ROTOR_DISC_ALPHA)
        SpritePaint.Line -> drawPath(path, outline, style = roundStroke(LINE_UNITS))
    }
}

private fun DrawScope.fillAndOutline(path: Path, fill: Color, outline: Color, width: Float) {
    drawPath(path, fill)
    drawPath(path, outline, style = roundStroke(width))
}

private inline fun DrawScope.drawSprite(
    parts: List<ParsedPart>,
    center: Offset,
    sizePx: Float,
    headingDegrees: Float,
    timeSeconds: Float,
    gridWidth: Float = SPRITE_UNITS,
    gridHeight: Float = SPRITE_UNITS,
    crossinline drawPart: DrawScope.(SpritePart, Path) -> Unit,
) {
    val scale = sizePx / gridWidth
    val turns = timeSeconds * 1000f / FaMotion.durRotorMs
    // Full width, narrow, full width: once per turn.
    val flick = PROPELLER_MIN_SCALE + (1f - PROPELLER_MIN_SCALE) * abs(cos(PI * turns)).toFloat()
    withTransform({
        translate(center.x, center.y)
        rotate(headingDegrees, Offset.Zero)
        scale(scale, scale, Offset.Zero)
        translate(-gridWidth / 2f, -gridHeight / 2f)
    }) {
        for (parsed in parts) {
            val part = parsed.part
            val pivot = Offset(part.pivotX, part.pivotY)
            when (part.motion) {
                SpriteMotion.None -> drawPart(part, parsed.path)
                // Seen from above a propeller changes width; seen from the side, height.
                SpriteMotion.Propeller ->
                    withTransform({ scale(flick, 1f, pivot) }) { drawPart(part, parsed.path) }

                SpriteMotion.PropellerSide ->
                    withTransform({ scale(1f, flick, pivot) }) { drawPart(part, parsed.path) }

                SpriteMotion.Rotor -> withTransform({ rotate((turns * 360f) % 360f, pivot) }) {
                    drawPart(part, parsed.path)
                }
            }
        }
    }
}

private class ParsedIcon(val fills: List<Pair<Path, Float>>, val stroke: Path?)

private val parsedIcons: Map<FaIcon, ParsedIcon> by lazy {
    FaIcon.entries.associateWith { icon ->
        ParsedIcon(
            fills = icon.def.fills.map { parsePath(it.pathData) to it.strokeWidth },
            stroke = icon.def.stroke?.let(::parsePath),
        )
    }
}

/** Draws [icon] in [color], filling the whole draw area (a square). */
internal fun DrawScope.drawIcon(icon: FaIcon, color: Color) {
    val parsed = parsedIcons.getValue(icon)
    val scale = size.minDimension / ICON_UNITS
    withTransform({ scale(scale, scale, Offset.Zero) }) {
        for ((path, strokeWidth) in parsed.fills) {
            drawPath(path, color)
            if (strokeWidth > 0f) drawPath(path, color, style = roundStroke(strokeWidth))
        }
        parsed.stroke?.let { drawPath(it, color, style = roundStroke(ICON_STROKE_UNITS)) }
    }
}
