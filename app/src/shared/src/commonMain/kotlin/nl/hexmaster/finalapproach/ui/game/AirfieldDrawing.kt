package nl.hexmaster.finalapproach.ui.game

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.HelipadConfig
import nl.hexmaster.finalapproach.domain.RunwayConfig
import nl.hexmaster.finalapproach.domain.SceneryItem
import nl.hexmaster.finalapproach.domain.SceneryKind
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import kotlin.math.PI

/** Screen position of world point [world]. */
internal fun WorldViewport.offsetOf(world: Vec2): Offset = toScreen(world).let { Offset(it.x.toFloat(), it.y.toFloat()) }

/** World point under screen position [screen]. */
internal fun WorldViewport.worldOf(screen: Offset): Vec2 = toWorld(Vec2(screen.x.toDouble(), screen.y.toDouble()))

internal fun WorldViewport.px(worldLength: Double): Float = toScreenLength(worldLength).toFloat()

internal fun degreesOf(radians: Double): Float = (radians * DEGREES_PER_RADIAN).toFloat()

private const val DEGREES_PER_RADIAN = 180.0 / PI

private fun DrawScope.outlineStroke() =
    Stroke(width = FaDimens.line3.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

/** Fills and outlines a rectangle, the shape most airfield parts are made of. */
private fun DrawScope.outlinedRect(rect: Rect, fill: Color, colors: FaColors, corner: Float) {
    val radius = CornerRadius(corner, corner)
    drawRoundRect(fill, rect.topLeft, rect.size, radius)
    drawRoundRect(colors.outline, rect.topLeft, rect.size, radius, style = outlineStroke())
}

/**
 * Runs [block] in a frame centred on [center] and turned by [heading], where up is along the
 * heading; [block] gets the rectangle of [lengthPx] along and [widthPx] across, in that frame.
 */
private inline fun DrawScope.inFrame(
    center: Offset,
    heading: Double,
    widthPx: Float,
    lengthPx: Float,
    block: DrawScope.(Rect) -> Unit,
) {
    rotate(degreesOf(heading), center) {
        block(Rect(Offset(center.x - widthPx / 2f, center.y - lengthPx / 2f), Size(widthPx, lengthPx)))
    }
}

/** Draws a runway with its centre line, threshold bars and a number at each landing end. */
internal fun DrawScope.drawRunway(
    runway: RunwayConfig,
    viewport: WorldViewport,
    colors: FaColors,
    measurer: TextMeasurer,
    numberStyle: TextStyle,
) {
    val center = viewport.offsetOf(runway.position)
    val lengthPx = viewport.px(runway.length)
    val widthPx = viewport.px(runway.width)
    inFrame(center, runway.orientation, widthPx, lengthPx) { rect ->
        outlinedRect(rect, colors.asphalt500, colors, FaDimens.radiusSm.toPx())
        val inset = FaDimens.space4.toPx()
        val bar = FaDimens.line3.toPx()
        listOf(rect.top + inset, rect.bottom - inset - bar).forEach { y ->
            drawRect(colors.marking, Offset(rect.left + inset, y), Size(rect.width - inset * 2f, bar))
        }
        val dash = FaDimens.space5.toPx()
        val gap = FaDimens.space4.toPx()
        val clear = inset + FaDimens.space8.toPx()
        var y = rect.top + clear
        while (y + dash < rect.bottom - clear) {
            drawRect(colors.marking, Offset(center.x - bar / 2f, y), Size(bar, dash))
            y += dash + gap
        }
    }
    val numberInset = FaDimens.space4.toPx() + FaDimens.space6.toPx()
    for (heading in runway.landingHeadings) {
        val direction = Angles.direction(heading)
        val threshold = center - Offset(direction.x.toFloat(), direction.y.toFloat()) * (lengthPx / 2f)
        val at = threshold + Offset(direction.x.toFloat(), direction.y.toFloat()) * numberInset
        val layout = measurer.measure(runway.numberFor(heading), numberStyle)
        rotate(degreesOf(heading), at) {
            drawText(
                layout,
                color = colors.marking,
                topLeft = at - Offset(layout.size.width / 2f, layout.size.height / 2f),
            )
        }
    }
}

/** Draws a helipad: an outlined asphalt disc with a ring and an H. */
internal fun DrawScope.drawHelipad(
    pad: HelipadConfig,
    viewport: WorldViewport,
    colors: FaColors,
    measurer: TextMeasurer,
    letterStyle: TextStyle,
) {
    val center = viewport.offsetOf(pad.position)
    val radius = viewport.px(pad.radius)
    drawCircle(colors.asphalt500, radius, center)
    drawCircle(colors.outline, radius, center, style = outlineStroke())
    drawCircle(
        colors.marking,
        radius - FaDimens.space3.toPx(),
        center,
        style = Stroke(width = FaDimens.line2.toPx()),
    )
    val layout = measurer.measure(HELIPAD_LETTER, letterStyle)
    drawText(layout, color = colors.marking, topLeft = center - Offset(layout.size.width / 2f, layout.size.height / 2f))
}

private const val HELIPAD_LETTER = "H"

/** True for scenery that lies flat on the ground and is drawn under the runway. */
internal val SceneryItem.isGround: Boolean
    get() = kind == SceneryKind.Taxiway || kind == SceneryKind.Apron

/** Draws one piece of decoration in the cartoon style: flat fill, thick outline. */
internal fun DrawScope.drawScenery(item: SceneryItem, viewport: WorldViewport, colors: FaColors) {
    val center = viewport.offsetOf(item.position)
    val widthPx = viewport.px(item.width)
    val lengthPx = viewport.px(item.height)
    val corner = FaDimens.radiusSm.toPx()
    inFrame(center, item.rotation, widthPx, lengthPx) { rect ->
        when (item.kind) {
            SceneryKind.Taxiway -> outlinedRect(rect, colors.asphalt500, colors, corner)
            SceneryKind.Apron -> outlinedRect(rect, colors.asphalt300, colors, corner)
            SceneryKind.Terminal -> {
                outlinedRect(rect, colors.surfaceRaised, colors, corner)
                val roof = rect.deflate(FaDimens.space2.toPx())
                drawRoundRect(colors.sky300, roof.topLeft, Size(roof.width, roof.height / 2f), CornerRadius(corner, corner))
            }
            SceneryKind.Tower -> {
                outlinedRect(rect, colors.surfaceRaised, colors, corner)
                val cab = rect.width / 2f - FaDimens.space1.toPx()
                drawCircle(colors.cherry, cab, rect.center)
                drawCircle(colors.outline, cab, rect.center, style = outlineStroke())
            }
            SceneryKind.Hangar -> {
                outlinedRect(rect, colors.sky500, colors, corner)
                val ridge = FaDimens.line2.toPx()
                drawLine(colors.outline, Offset(rect.left, rect.center.y), Offset(rect.right, rect.center.y), ridge)
            }
            SceneryKind.Windsock -> {
                drawCircle(colors.outline, FaDimens.line3.toPx(), rect.center)
                val sock = Path().apply {
                    moveTo(rect.center.x, rect.top)
                    lineTo(rect.right, rect.center.y)
                    lineTo(rect.center.x, rect.bottom)
                    close()
                }
                drawPath(sock, colors.tangerine)
                drawPath(sock, colors.outline, style = outlineStroke())
            }
            SceneryKind.Tree -> {
                val radius = rect.minDimension / 2f
                drawCircle(colors.grass300, radius, rect.center)
                drawCircle(colors.outline, radius, rect.center, style = outlineStroke())
            }
        }
    }
}
