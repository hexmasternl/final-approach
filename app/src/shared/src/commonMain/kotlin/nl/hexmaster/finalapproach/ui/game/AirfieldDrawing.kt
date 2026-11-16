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

internal const val WORLD_UNIT = 1.0
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

/**
 * Draws one piece of scenery that is a sprite or a row of sprites: the sprites come from the
 * design system. Ground pieces (roads, fields, water, slabs) are drawn by `GroundDrawing.kt`.
 */
internal fun DrawScope.drawScenery(
    item: SceneryItem,
    viewport: WorldViewport,
    colors: FaColors,
) {
    val center = viewport.offsetOf(item.position)
    val widthPx = viewport.px(item.width)
    val lengthPx = viewport.px(item.height)
    val degrees = degreesOf(item.rotation)
    scenerySprites[item.kind]?.let {
        drawScenerySprite(it, center, widthPx, lengthPx, degrees, colors)
        return
    }
    sceneryRows[item.kind]?.let { drawSceneryRow(it.sprite, center, widthPx, lengthPx, degrees, colors, it.overlap, it.stagger) }
}
