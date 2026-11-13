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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isSpecified
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.HelipadConfig
import nl.hexmaster.finalapproach.domain.LevelScenery
import nl.hexmaster.finalapproach.domain.RunwayConfig
import nl.hexmaster.finalapproach.domain.SceneryDefaults
import nl.hexmaster.finalapproach.domain.SceneryItem
import nl.hexmaster.finalapproach.domain.SceneryKind
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.sprites.SceneryLines
import nl.hexmaster.finalapproach.ui.sprites.ScenerySprite
import nl.hexmaster.finalapproach.ui.sprites.drawSceneryRow
import nl.hexmaster.finalapproach.ui.sprites.drawScenerySprite
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

/**
 * The markings of runways and helipads belong to the airfield and shrink with it: a token is
 * drawn as that many world units, the scale of the design system's 64-unit runway and 112-unit
 * helipad, so the markings always fit their runway or pad.
 */
private class FieldScale(viewport: WorldViewport, private val density: Float) {
    private val pxPerUnit = viewport.px(WORLD_UNIT)

    fun px(token: Dp): Float = token.value * pxPerUnit

    /** [style] with its text sized like the markings. */
    fun text(style: TextStyle): TextStyle {
        val factor = pxPerUnit / density
        return style.copy(
            fontSize = if (style.fontSize.isSpecified) style.fontSize * factor else style.fontSize,
            lineHeight = if (style.lineHeight.isSpecified) style.lineHeight * factor else style.lineHeight,
        )
    }

    private companion object {
        const val WORLD_UNIT = 1.0
    }
}

/** The outline of runways and helipads: `line-3`, the stroke of everything that is part of play. */
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
    val field = FieldScale(viewport, density)
    inFrame(center, runway.orientation, widthPx, lengthPx) { rect ->
        outlinedRect(rect, colors.asphalt500, colors, field.px(FaDimens.radiusSm))
        val inset = field.px(FaDimens.space4)
        val barLength = field.px(FaDimens.space5)
        val barWidth = field.px(FaDimens.line4)
        val patchLength = field.px(FaDimens.space8)
        val patchWidth = rect.width / 2f
        val numberRoom = field.px(FaDimens.space7)
        // Touchdown patches past the numbers, then four threshold bars at each end.
        listOf(rect.top + inset + barLength + numberRoom, rect.bottom - inset - barLength - numberRoom - patchLength)
            .forEach { y -> drawRect(colors.asphalt700, Offset(rect.center.x - patchWidth / 2f, y), Size(patchWidth, patchLength)) }
        val barSpacing = (rect.width - inset * 2f) / THRESHOLD_BARS
        repeat(THRESHOLD_BARS) { i ->
            val x = rect.left + inset + barSpacing * (i + 0.5f) - barWidth / 2f
            drawRect(colors.marking, Offset(x, rect.top + inset), Size(barWidth, barLength))
            drawRect(colors.marking, Offset(x, rect.bottom - inset - barLength), Size(barWidth, barLength))
        }
        val dash = field.px(FaDimens.space5)
        val gap = field.px(FaDimens.space4)
        val clear = inset + barLength + numberRoom
        var y = rect.top + clear
        while (y + dash < rect.bottom - clear) {
            drawRect(colors.marking, Offset(rect.center.x - barWidth / 2f, y), Size(barWidth, dash))
            y += dash + gap
        }
    }
    val numberInset = field.px(FaDimens.space4) + field.px(FaDimens.space5) + field.px(FaDimens.space7) / 2f
    val fieldNumberStyle = field.text(numberStyle)
    for (heading in runway.landingHeadings) {
        val direction = Angles.direction(heading)
        val threshold = center - Offset(direction.x.toFloat(), direction.y.toFloat()) * (lengthPx / 2f)
        val at = threshold + Offset(direction.x.toFloat(), direction.y.toFloat()) * numberInset
        val layout = measurer.measure(runway.numberFor(heading), fieldNumberStyle)
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
    val field = FieldScale(viewport, density)
    drawCircle(colors.asphalt500, radius, center)
    drawCircle(colors.outline, radius, center, style = outlineStroke())
    drawCircle(
        colors.markingWarn,
        radius - field.px(FaDimens.space3),
        center,
        style = Stroke(width = field.px(FaDimens.line4)),
    )
    val layout = measurer.measure(HELIPAD_LETTER, field.text(letterStyle))
    drawText(layout, color = colors.marking, topLeft = center - Offset(layout.size.width / 2f, layout.size.height / 2f))
}

private const val HELIPAD_LETTER = "H"
private const val THRESHOLD_BARS = 4

/** The sprite of every kind of scenery that is drawn as one sprite, fitted into the item. */
private val SPRITES: Map<SceneryKind, ScenerySprite> = mapOf(
    SceneryKind.Jetty to ScenerySprite.Jetty,
    SceneryKind.Terminal to ScenerySprite.Terminal,
    SceneryKind.Tower to ScenerySprite.Tower,
    SceneryKind.Hangar to ScenerySprite.Hangar,
    SceneryKind.Windsock to ScenerySprite.Windsock,
    SceneryKind.Car to ScenerySprite.Car,
    SceneryKind.FuelTruck to ScenerySprite.FuelTruck,
    SceneryKind.BaggageCart to ScenerySprite.BaggageCart,
    SceneryKind.ParkedAircraft to ScenerySprite.ParkedAircraft,
    SceneryKind.Tree to ScenerySprite.Tree,
    SceneryKind.Farmhouse to ScenerySprite.Farmhouse,
    SceneryKind.Barn to ScenerySprite.Barn,
    SceneryKind.Palm to ScenerySprite.Palm,
    SceneryKind.Acacia to ScenerySprite.Acacia,
    SceneryKind.Bush to ScenerySprite.Bush,
    SceneryKind.Rock to ScenerySprite.Rock,
    SceneryKind.Zebra to ScenerySprite.Zebra,
    SceneryKind.Elephant to ScenerySprite.Elephant,
)

private fun sceneryStroke(width: Float) = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)

/**
 * Draws one piece of decoration in the cartoon style: flat fills, the scenery outline, fine
 * details. Sprites come from the design system; fields, roads, water and slabs are drawn here
 * from tokens and the sizes in [SceneryDefaults]. [landscape] decides what a road is made of.
 */
internal fun DrawScope.drawScenery(
    item: SceneryItem,
    landscape: LevelScenery,
    viewport: WorldViewport,
    colors: FaColors,
    lines: SceneryLines,
) {
    val center = viewport.offsetOf(item.position)
    val widthPx = viewport.px(item.width)
    val lengthPx = viewport.px(item.height)
    val degrees = degreesOf(item.rotation)
    val sprite = SPRITES[item.kind]
    if (sprite != null) {
        drawScenerySprite(sprite, center, widthPx, lengthPx, degrees, colors, lines)
        return
    }
    when (item.kind) {
        SceneryKind.Hedge -> drawSceneryRow(ScenerySprite.Hedge, center, widthPx, lengthPx, degrees, colors, lines)
        SceneryKind.Fence -> drawSceneryRow(ScenerySprite.Fence, center, widthPx, lengthPx, degrees, colors, lines)
        SceneryKind.ForestEdge -> drawSceneryRow(
            ScenerySprite.Tree,
            center,
            widthPx,
            lengthPx,
            degrees,
            colors,
            lines,
            overlap = SceneryDefaults.FOREST_OVERLAP.toFloat(),
            stagger = SceneryDefaults.FOREST_STAGGER.toFloat(),
        )

        else -> inFrame(center, item.rotation, widthPx, lengthPx) { rect ->
            when (item.kind) {
                SceneryKind.Taxiway -> slab(rect, colors.asphalt500, colors, lines)
                SceneryKind.Apron -> {
                    slab(rect, colors.asphalt300, colors, lines)
                    val line = rect.deflate(viewport.px(SceneryDefaults.APRON_LINE_INSET))
                    if (line.width > 0f && line.height > 0f) {
                        drawRoundRect(colors.marking, line.topLeft, line.size, CornerRadius(lines.corner), style = sceneryStroke(lines.fine))
                    }
                }

                SceneryKind.Field -> drawCropField(rect, viewport, colors, lines)
                SceneryKind.Road -> drawRoadSurface(rect, landscape, viewport, colors, lines)
                SceneryKind.Pond -> drawWater(rect, bank = 0f, viewport, colors, lines)
                SceneryKind.Waterhole -> drawWater(rect, viewport.px(SceneryDefaults.WATERHOLE_BANK), viewport, colors, lines)
                else -> Unit
            }
        }
    }
}

/**
 * The edge of a road: its surface with an outline around it. Every road's edge is drawn before
 * any road's surface, so where two roads meet no outline crosses the junction. A road's ends are
 * left open by its surface: end a road under an apron, inside another road or at the world's edge.
 */
internal fun DrawScope.drawRoadEdge(item: SceneryItem, landscape: LevelScenery, viewport: WorldViewport, colors: FaColors, lines: SceneryLines) {
    inFrame(viewport.offsetOf(item.position), item.rotation, viewport.px(item.width), viewport.px(item.height)) { rect ->
        drawRect(roadColor(landscape, colors), rect.topLeft, rect.size)
        drawRect(colors.outline, rect.topLeft, rect.size, style = sceneryStroke(lines.outline))
    }
}

/** A dirt road on the savanna, asphalt everywhere else. */
private fun roadColor(landscape: LevelScenery, colors: FaColors): Color =
    if (landscape == LevelScenery.Savanna) colors.sand300 else colors.asphalt300

/** The surface of a road inside its edge: a dashed centre line, or the two ruts of a dirt road. */
private fun DrawScope.drawRoadSurface(rect: Rect, landscape: LevelScenery, viewport: WorldViewport, colors: FaColors, lines: SceneryLines) {
    val inside = rect.deflate(lines.outline / 2f)
    drawRect(roadColor(landscape, colors), Offset(inside.left, rect.top), Size(inside.width, rect.height))
    if (landscape == LevelScenery.Savanna) {
        val rut = rect.width * SceneryDefaults.ROAD_RUT_FRACTION.toFloat()
        for (x in listOf(rect.center.x - rut, rect.center.x + rut)) {
            drawLine(colors.soil700, Offset(x, rect.top), Offset(x, rect.bottom), lines.fine, StrokeCap.Round)
        }
    } else {
        val dash = viewport.px(SceneryDefaults.ROAD_DASH)
        val gap = viewport.px(SceneryDefaults.ROAD_GAP)
        var y = rect.top + gap
        while (y + dash < rect.bottom) {
            drawLine(colors.marking, Offset(rect.center.x, y), Offset(rect.center.x, y + dash), lines.fine, StrokeCap.Round)
            y += dash + gap
        }
    }
}

/** A flat slab of asphalt with the scenery outline. */
private fun DrawScope.slab(rect: Rect, fill: Color, colors: FaColors, lines: SceneryLines) {
    val radius = CornerRadius(lines.corner)
    drawRoundRect(fill, rect.topLeft, rect.size, radius)
    drawRoundRect(colors.outline, rect.topLeft, rect.size, radius, style = sceneryStroke(lines.outline))
}

/** A crop field: soil with furrows and crop rows along its length, like the crop-field tile. */
private fun DrawScope.drawCropField(rect: Rect, viewport: WorldViewport, colors: FaColors, lines: SceneryLines) {
    val radius = CornerRadius(lines.corner)
    drawRoundRect(colors.soil500, rect.topLeft, rect.size, radius)
    val spacing = viewport.px(SceneryDefaults.FIELD_ROW_SPACING)
    val furrows = Path()
    val crops = Path()
    // A furrow on every spacing, a crop row half way between two furrows; rows stop short of the edge.
    val rows = (rect.width / spacing).toInt()
    val first = rect.center.x - rows * spacing / 2f
    for (i in 0..rows) {
        val x = first + i * spacing
        if (i in 1 until rows) {
            furrows.moveTo(x, rect.top)
            furrows.lineTo(x, rect.bottom)
        }
        if (i < rows) {
            crops.moveTo(x + spacing / 2f, rect.top + spacing / 2f)
            crops.lineTo(x + spacing / 2f, rect.bottom - spacing / 2f)
        }
    }
    drawPath(furrows, colors.soil700, style = sceneryStroke(lines.fine))
    drawPath(crops, colors.grass300, style = sceneryStroke(lines.outline))
    drawRoundRect(colors.outline, rect.topLeft, rect.size, radius, style = sceneryStroke(lines.outline))
}

/**
 * A pond or waterhole filling [rect] as an oval: water with two ripples, inside a sand [bank]
 * that many pixels wide when it has one.
 */
private fun DrawScope.drawWater(rect: Rect, bank: Float, viewport: WorldViewport, colors: FaColors, lines: SceneryLines) {
    val water = rect.deflate(bank)
    if (bank > 0f) {
        drawOval(colors.sand300, rect.topLeft, rect.size)
        drawOval(colors.outline, rect.topLeft, rect.size, style = sceneryStroke(lines.outline))
    }
    drawOval(colors.sky500, water.topLeft, water.size)
    drawOval(colors.outline, water.topLeft, water.size, style = sceneryStroke(if (bank > 0f) lines.fine else lines.outline))
    val wave = viewport.px(SceneryDefaults.WAVE_WIDTH)
    val waveLength = wave * ScenerySprite.Wave.height / ScenerySprite.Wave.width
    // One ripple in the upper left of the water, one in the lower right.
    for (side in listOf(-1f, 1f)) {
        val at = water.center + Offset(side * water.width / RIPPLE_SPREAD, side * water.height / RIPPLE_SPREAD)
        drawScenerySprite(ScenerySprite.Wave, at, wave, waveLength, 0f, colors, lines)
    }
}

/** Ripples lie this fraction of the water's size from its middle: one over this number. */
private const val RIPPLE_SPREAD = 6f
