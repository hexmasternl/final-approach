package nl.hexmaster.finalapproach.ui.game

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import nl.hexmaster.finalapproach.domain.HelipadConfig
import nl.hexmaster.finalapproach.domain.LevelScenery
import nl.hexmaster.finalapproach.domain.RunwayConfig
import nl.hexmaster.finalapproach.domain.SceneryDefaults
import nl.hexmaster.finalapproach.domain.SceneryItem
import nl.hexmaster.finalapproach.domain.SceneryKind
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.sprites.GroundMetrics
import nl.hexmaster.finalapproach.ui.sprites.GroundMetrics.Helipad
import nl.hexmaster.finalapproach.ui.sprites.GroundMetrics.Runway
import nl.hexmaster.finalapproach.ui.sprites.ScenerySprite
import nl.hexmaster.finalapproach.ui.sprites.SceneryTier
import nl.hexmaster.finalapproach.ui.sprites.drawSceneryRow
import nl.hexmaster.finalapproach.ui.sprites.drawSceneryRowShadows
import nl.hexmaster.finalapproach.ui.sprites.drawSceneryShadowAt
import nl.hexmaster.finalapproach.ui.sprites.drawScenerySprite
import nl.hexmaster.finalapproach.ui.theme.FaColors
import kotlin.math.PI
import kotlin.math.cos

/** Screen position of world point [world]. */
internal fun WorldViewport.offsetOf(world: Vec2): Offset = toScreen(world).let { Offset(it.x.toFloat(), it.y.toFloat()) }

/** World point under screen position [screen]. */
internal fun WorldViewport.worldOf(screen: Offset): Vec2 = toWorld(Vec2(screen.x.toDouble(), screen.y.toDouble()))

internal fun WorldViewport.px(worldLength: Double): Float = toScreenLength(worldLength).toFloat()

internal fun degreesOf(radians: Double): Float = (radians * DEGREES_PER_RADIAN).toFloat()

private const val DEGREES_PER_RADIAN = 180.0 / PI

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

/**
 * Runs [block] in the frame of a runway: origin at its middle, x along its axis, y across it, and
 * one unit of the frame one world unit. Sizes of the design system's ground metrics, and their
 * line widths, can be used as they are.
 */
internal fun DrawScope.inRunwayFrame(runway: RunwayConfig, viewport: WorldViewport, block: DrawScope.() -> Unit) {
    val center = viewport.offsetOf(runway.position)
    val unit = viewport.px(WORLD_UNIT)
    withTransform({
        translate(center.x, center.y)
        rotate(degreesOf(runway.orientation) - QUARTER_TURN_DEGREES, Offset.Zero)
        scale(unit, unit, Offset.Zero)
    }, block)
}

private const val WORLD_UNIT = 1.0
private const val QUARTER_TURN_DEGREES = 90f

private fun roundStroke(width: Float, cap: StrokeCap = StrokeCap.Round) = Stroke(width, cap = cap, join = StrokeJoin.Round)

private fun PlanRect.topLeft() = Offset(x, y)

private fun PlanRect.size() = Size(width, height)

/** The end of [runway] an airplane landing on [heading] touches down at. */
private fun RunwayConfig.endFor(heading: Double): RunwayEnd = if (cos(heading - orientation) >= 0.0) RunwayEnd.Low else RunwayEnd.High

/**
 * Draws a runway as the design system builds it (`Runway.md`): a rough-grass fringe, a concrete
 * shoulder, the outlined asphalt at the level's footprint, then the markings and lights of its
 * [runwayPlan]: an edge line, six threshold bars, the number, aiming blocks and touchdown marks,
 * the centre line (dashes, or chevrons on a one-way runway), tyre rubber, and edge, threshold and
 * approach lights. Sizes come from [GroundMetrics], colours from [colors].
 */
internal fun DrawScope.drawRunway(
    runway: RunwayConfig,
    viewport: WorldViewport,
    colors: FaColors,
    measurer: TextMeasurer,
    numberStyle: TextStyle,
) {
    val m = GroundMetrics.Runway
    val across = runway.width.toFloat() / m.width
    val landingEnds = runway.landingHeadings.map { runway.endFor(it) }.toSet()
    val plan = runwayPlan(runway.length.toFloat(), landingEnds, across)
    val half = runway.length.toFloat() / 2f
    val asphaltHalf = m.halfWidth * across
    fun band(extraAlong: Float, extraAcross: Float) =
        Rect(-half - extraAlong, -asphaltHalf - extraAcross, half + extraAlong, asphaltHalf + extraAcross)
    inRunwayFrame(runway, viewport) {
        val fringe = band(m.fringe, m.fringeHalf * across - asphaltHalf)
        drawRoundRect(colors.grass700, fringe.topLeft, fringe.size, CornerRadius(m.fringeRx))
        val shoulder = band(m.shoulder, m.shoulderHalf * across - asphaltHalf)
        drawRoundRect(colors.concrete500, shoulder.topLeft, shoulder.size, CornerRadius(m.shoulderRx))
        drawRoundRect(colors.concrete700, shoulder.topLeft, shoulder.size, CornerRadius(m.shoulderRx), style = roundStroke(Runway.Lines.shoulder))
        val asphalt = band(0f, 0f)
        drawRoundRect(colors.asphalt500, asphalt.topLeft, asphalt.size, CornerRadius(m.rx))
        drawRoundRect(colors.outline, asphalt.topLeft, asphalt.size, CornerRadius(m.rx), style = roundStroke(Runway.Lines.outline))
        plan.ends.forEach { drawRunwayEnd(it, colors) }
        plan.edgeLines.forEach { drawLine(colors.marking, Offset(it.x0, it.y0), Offset(it.x1, it.y1), Runway.Lines.edge, StrokeCap.Round) }
        drawCentreLine(plan, colors)
        plan.ends.forEach { drawRunwayNumber(runway, it, colors, measurer, numberStyle) }
        plan.edgeLights.forEach { drawLight(colors.beacon, colors.concrete700, it, Runway.Lights.Edge.r) }
        plan.approachLights.forEach {
            val box = it.box
            val radius = CornerRadius(APPROACH_LIGHT_RX)
            drawRoundRect(if (it.red) colors.cherry500 else colors.foam, box.topLeft(), box.size(), radius)
            drawRoundRect(colors.outline, box.topLeft(), box.size(), radius, style = roundStroke(Runway.Lines.light))
        }
    }
}

/** The corner radius of an approach light's box, in world units (the design system's `rx="1"`). */
private const val APPROACH_LIGHT_RX = 1f

private fun DrawScope.drawLight(fill: Color, edge: Color, at: PlanPoint, radius: Float) {
    drawCircle(fill, radius, Offset(at.x, at.y))
    drawCircle(edge, radius, Offset(at.x, at.y), style = Stroke(Runway.Lines.light))
}

/** The marks of one landing end: tyre rubber, threshold bars, aiming blocks, touchdown marks and threshold lights. */
private fun DrawScope.drawRunwayEnd(end: RunwayEndPlan, colors: FaColors) {
    val m = GroundMetrics.Runway
    end.rubber.forEach { drawLine(colors.asphalt700, Offset(it.x0, it.y0), Offset(it.x1, it.y1), Runway.Lines.rubber, StrokeCap.Round) }
    end.bars.forEach { drawRoundRect(colors.marking, it.topLeft(), it.size(), CornerRadius(Runway.Bars.rx)) }
    end.aiming.forEach { drawRoundRect(colors.marking, it.topLeft(), it.size(), CornerRadius(Runway.Aiming.rx)) }
    end.touchdown.forEach { drawLine(colors.marking, Offset(it.x0, it.y0), Offset(it.x1, it.y1), Runway.Lines.touchdown, StrokeCap.Butt) }
    end.thresholdLights.forEach { drawLight(colors.mint500, colors.concrete700, it, Runway.Lights.Threshold.r) }
}

/** The centre line: dashes on a two-way runway, chevrons pointing the way airplanes land on a one-way one. */
private fun DrawScope.drawCentreLine(plan: RunwayPlan, colors: FaColors) {
    val m = GroundMetrics.Runway
    plan.centreDashes?.let {
        val dashes = PathEffect.dashPathEffect(m.dash.toFloatArray())
        drawLine(colors.marking, Offset(it.x0, it.y0), Offset(it.x1, it.y1), Runway.Lines.centre, StrokeCap.Butt, dashes)
    }
    val chevron = roundStroke(Runway.Lines.chevron)
    plan.chevrons.forEach {
        val path = Path().apply {
            moveTo(it.armA.x, it.armA.y)
            lineTo(it.tip.x, it.tip.y)
            lineTo(it.armB.x, it.armB.y)
        }
        drawPath(path, colors.marking, style = chevron)
    }
}

/** The runway number of [end], read from its own approach end: the top of the text points along the landing heading. */
private fun DrawScope.drawRunwayNumber(
    runway: RunwayConfig,
    end: RunwayEndPlan,
    colors: FaColors,
    measurer: TextMeasurer,
    numberStyle: TextStyle,
) {
    val heading = runway.landingHeadings.first { runway.endFor(it) == end.end }
    val size = GroundMetrics.Runway.numberSize.toSp()
    val layout = measurer.measure(runway.numberFor(heading), numberStyle.copy(fontSize = size, lineHeight = size))
    val at = Offset(end.numberAt.x, end.numberAt.y)
    // The frame's x axis points along the runway: text that reads up along +x is turned a quarter, along -x three quarters.
    rotate(end.end.sign * QUARTER_TURN_DEGREES * -1f, at) {
        drawText(layout, color = colors.marking, topLeft = at - Offset(layout.size.width / 2f, layout.size.height / 2f))
    }
}

/**
 * Draws a helipad as the design system builds it (`Helipad.md`): a rough-grass fringe, a concrete
 * pad with slab joints and a light in each corner, and on it the outlined asphalt disc at the
 * level's radius with a worn ring, the warning ring and the H. Sizes come from [GroundMetrics].
 */
internal fun DrawScope.drawHelipad(
    pad: HelipadConfig,
    viewport: WorldViewport,
    colors: FaColors,
) {
    val m = GroundMetrics.Helipad
    val center = viewport.offsetOf(pad.position)
    // The design system draws the disc 56 units in radius; the level's radius scales the whole pad.
    val unit = viewport.px(pad.radius) / m.disc
    withTransform({
        translate(center.x, center.y)
        scale(unit, unit, Offset.Zero)
    }) {
        val fringe = Helipad.Fringe.half
        drawRoundRect(colors.grass700, Offset(-fringe, -fringe), Size(fringe * 2f, fringe * 2f), CornerRadius(Helipad.Fringe.rx))
        val slab = Helipad.Pad.half
        val slabTopLeft = Offset(-slab, -slab)
        val slabSize = Size(slab * 2f, slab * 2f)
        drawRoundRect(colors.concrete300, slabTopLeft, slabSize, CornerRadius(Helipad.Pad.rx))
        drawRoundRect(colors.concrete700, slabTopLeft, slabSize, CornerRadius(Helipad.Pad.rx), style = roundStroke(Helipad.Lines.pad))
        val joints = roundStroke(Helipad.Lines.joint)
        drawPath(
            Path().apply {
                moveTo(-slab, 0f)
                lineTo(slab, 0f)
                moveTo(0f, -slab)
                lineTo(0f, slab)
            },
            colors.concrete500,
            style = joints,
        )
        drawCircle(colors.asphalt500, m.disc, Offset.Zero)
        drawCircle(colors.outline, m.disc, Offset.Zero, style = roundStroke(Helipad.Lines.outline))
        drawCircle(colors.asphalt700, m.wornRing, Offset.Zero, alpha = m.wornOpacity, style = roundStroke(Helipad.Lines.wornRing))
        drawCircle(colors.markingWarn, m.warnRing, Offset.Zero, style = roundStroke(Helipad.Lines.warnRing))
        val h = Helipad.H
        drawPath(
            Path().apply {
                moveTo(-h.x, -h.y)
                lineTo(-h.x, h.y)
                moveTo(h.x, -h.y)
                lineTo(h.x, h.y)
                moveTo(-h.x, 0f)
                lineTo(h.x, 0f)
            },
            colors.marking,
            style = roundStroke(Helipad.Lines.h),
        )
        for (sx in listOf(-1f, 1f)) for (sy in listOf(-1f, 1f)) {
            val at = Offset(sx * Helipad.CornerLight.at, sy * Helipad.CornerLight.at)
            drawCircle(colors.beacon, Helipad.CornerLight.r, at)
            drawCircle(colors.concrete700, Helipad.CornerLight.r, at, style = Stroke(Helipad.Lines.cornerLight))
        }
    }
}

/** The sprite of every kind of scenery that is drawn as one sprite, fitted into the item. */
internal val scenerySprites: Map<SceneryKind, ScenerySprite> = mapOf(
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

/** How a row kind is laid out: the sprite of one piece, how far pieces overlap, how far they stagger. */
private class RowSpec(val sprite: ScenerySprite, val overlap: Float = 0f, val stagger: Float = 0f)

private val sceneryRows: Map<SceneryKind, RowSpec> = mapOf(
    SceneryKind.Hedge to RowSpec(ScenerySprite.Hedge),
    SceneryKind.Fence to RowSpec(ScenerySprite.Fence),
    SceneryKind.ForestEdge to RowSpec(
        ScenerySprite.Tree,
        overlap = SceneryDefaults.FOREST_OVERLAP.toFloat(),
        stagger = SceneryDefaults.FOREST_STAGGER.toFloat(),
    ),
)

/**
 * True for scenery that lies flat and is drawn in the ground pass: a sprite of the flat tier, or
 * a kind without a sprite that the domain marks as ground (road, field, water, slabs). Everything
 * else has height: it casts a shadow and is drawn after the shadow layer, from north to south.
 */
internal val SceneryItem.drawsOnGround: Boolean
    get() = scenerySprites[kind]?.let { it.tier == SceneryTier.Flat } ?: kind.ground

/** Draws the shadow of one piece of scenery with height, opaque in [color], for the shadow layer. */
internal fun DrawScope.drawSceneryItemShadow(item: SceneryItem, viewport: WorldViewport, color: Color) {
    val center = viewport.offsetOf(item.position)
    val widthPx = viewport.px(item.width)
    val lengthPx = viewport.px(item.height)
    val degrees = degreesOf(item.rotation)
    scenerySprites[item.kind]?.let { drawSceneryShadowAt(it, center, widthPx, lengthPx, degrees, color) }
    sceneryRows[item.kind]?.let {
        drawSceneryRowShadows(it.sprite, center, widthPx, lengthPx, degrees, color, it.overlap, it.stagger)
    }
}

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
    lines: GroundLines,
) {
    val center = viewport.offsetOf(item.position)
    val widthPx = viewport.px(item.width)
    val lengthPx = viewport.px(item.height)
    val degrees = degreesOf(item.rotation)
    val sprite = scenerySprites[item.kind]
    if (sprite != null) {
        drawScenerySprite(sprite, center, widthPx, lengthPx, degrees, colors)
        return
    }
    val row = sceneryRows[item.kind]
    if (row != null) {
        drawSceneryRow(row.sprite, center, widthPx, lengthPx, degrees, colors, row.overlap, row.stagger)
        return
    }
    when (item.kind) {
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
internal fun DrawScope.drawRoadEdge(item: SceneryItem, landscape: LevelScenery, viewport: WorldViewport, colors: FaColors, lines: GroundLines) {
    inFrame(viewport.offsetOf(item.position), item.rotation, viewport.px(item.width), viewport.px(item.height)) { rect ->
        drawRect(roadColor(landscape, colors), rect.topLeft, rect.size)
        drawRect(colors.outline, rect.topLeft, rect.size, style = sceneryStroke(lines.outline))
    }
}

/** A dirt road on the savanna, asphalt everywhere else. */
private fun roadColor(landscape: LevelScenery, colors: FaColors): Color =
    if (landscape == LevelScenery.Savanna) colors.sand300 else colors.asphalt300

/** The surface of a road inside its edge: a dashed centre line, or the two ruts of a dirt road. */
private fun DrawScope.drawRoadSurface(rect: Rect, landscape: LevelScenery, viewport: WorldViewport, colors: FaColors, lines: GroundLines) {
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
private fun DrawScope.slab(rect: Rect, fill: Color, colors: FaColors, lines: GroundLines) {
    val radius = CornerRadius(lines.corner)
    drawRoundRect(fill, rect.topLeft, rect.size, radius)
    drawRoundRect(colors.outline, rect.topLeft, rect.size, radius, style = sceneryStroke(lines.outline))
}

/** A crop field: soil with furrows and crop rows along its length, like the crop-field tile. */
private fun DrawScope.drawCropField(rect: Rect, viewport: WorldViewport, colors: FaColors, lines: GroundLines) {
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
private fun DrawScope.drawWater(rect: Rect, bank: Float, viewport: WorldViewport, colors: FaColors, lines: GroundLines) {
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
        drawScenerySprite(ScenerySprite.Wave, at, wave, waveLength, 0f, colors)
    }
}

/** Ripples lie this fraction of the water's size from its middle: one over this number. */
private const val RIPPLE_SPREAD = 6f
