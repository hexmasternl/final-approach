package nl.hexmaster.finalapproach.ui.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.Bounds
import nl.hexmaster.finalapproach.domain.Island
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.LevelScenery
import nl.hexmaster.finalapproach.domain.LevelTheme
import nl.hexmaster.finalapproach.domain.RunwayConfig
import nl.hexmaster.finalapproach.domain.SceneryDefaults
import nl.hexmaster.finalapproach.domain.SceneryKind
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.game.ambient.AmbientLife
import nl.hexmaster.finalapproach.ui.game.ambient.Boat
import nl.hexmaster.finalapproach.ui.game.ambient.BoatKind
import nl.hexmaster.finalapproach.ui.game.ambient.LevelAmbienceConfig
import nl.hexmaster.finalapproach.ui.sprites.SceneryLines
import nl.hexmaster.finalapproach.ui.sprites.ScenerySprite
import nl.hexmaster.finalapproach.ui.sprites.drawLawn
import nl.hexmaster.finalapproach.ui.sprites.drawSceneryRow
import nl.hexmaster.finalapproach.ui.sprites.drawScenerySprite
import nl.hexmaster.finalapproach.ui.sprites.sceneryLines
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaLights

/** Sizes of the lights of a Dawn level, in world units, and how the running light fades. */
private object Backdrop {
    const val BEACON_RADIUS = 22.0
    const val RUNNING_LIGHT_RADIUS = 4.0
    const val RUNNING_LIGHTS_PER_EDGE = 12
    const val TRAIL_FADE = 0.35f
    const val UNLIT_FRACTION = 0.2f

    /** A boat's bow points up in its sprite; it sails to the right or to the left. */
    const val QUARTER_TURN_DEGREES = 90f
}

/**
 * The field layer of a level, everything below the zone glows and aircraft: ground, flat
 * scenery, runways, helipads, standing scenery and the ambient life, in that order.
 */
internal fun DrawScope.drawField(
    level: LevelConfig,
    ambient: AmbientLife,
    viewport: WorldViewport,
    colors: FaColors,
    measurer: TextMeasurer,
    numberStyle: TextStyle,
    letterStyle: TextStyle,
) {
    val lines = sceneryLines(viewport.px(WORLD_UNIT))
    drawLevelGround(level, viewport, colors, lines)
    val (ground, standing) = level.scenery.partition { it.isGround }
    ground.filter { it.kind == SceneryKind.Road }.forEach { drawRoadEdge(it, level.landscape, viewport, colors, lines) }
    ground.forEach { drawScenery(it, level.landscape, viewport, colors, lines) }
    level.runways.forEach { drawRunway(it, viewport, colors, measurer, numberStyle) }
    level.helipads.forEach { drawHelipad(it, viewport, colors, measurer, letterStyle) }
    standing.forEach { drawScenery(it, level.landscape, viewport, colors, lines) }
    drawAmbient(level, ambient, viewport, colors, lines)
}

/** One world unit, to ask the viewport how many pixels it is. */
private const val WORLD_UNIT = 1.0

/** The ground of a level: lawn with the paddock, sea with the island and its beach, or dry dawn grass. */
internal fun DrawScope.drawLevelGround(level: LevelConfig, viewport: WorldViewport, colors: FaColors, lines: SceneryLines) {
    when (level.landscape) {
        LevelScenery.Meadow -> {
            drawLawn(GameScreenDefaults.lawnStripe.toPx(), colors)
            level.paddock?.let { drawPaddock(it, viewport, colors, lines) }
        }

        LevelScenery.Island -> {
            drawRect(colors.sky500)
            level.island?.let { drawIsland(it, viewport, colors, lines) }
        }

        LevelScenery.Savanna -> drawLawn(GameScreenDefaults.lawnStripe.toPx(), colors)
    }
}

/** The island: waves off the shore, a sand beach all around, and the grass the airport stands on. */
private fun DrawScope.drawIsland(island: Island, viewport: WorldViewport, colors: FaColors, lines: SceneryLines) {
    val center = viewport.offsetOf(island.center)
    val rx = viewport.px(island.radiusX)
    val ry = viewport.px(island.radiusY)
    val beach = viewport.px(SceneryDefaults.BEACH_WIDTH)
    drawWaves(island, viewport, colors, lines)
    val shore = Size((rx + beach) * 2f, (ry + beach) * 2f)
    drawOval(colors.sand300, center - Offset(rx + beach, ry + beach), shore)
    drawOval(colors.outline, center - Offset(rx + beach, ry + beach), shore, style = sceneryOutline(lines))
    drawOval(colors.grass500, center - Offset(rx, ry), Size(rx * 2f, ry * 2f))
    drawOval(colors.outline, center - Offset(rx, ry), Size(rx * 2f, ry * 2f), style = Stroke(lines.fine))
}

/** Still waves in a ring around the island, evenly spaced along the shore. */
private fun DrawScope.drawWaves(island: Island, viewport: WorldViewport, colors: FaColors, lines: SceneryLines) {
    val reach = SceneryDefaults.BEACH_WIDTH + SceneryDefaults.WAVE_OFFSET
    val rx = island.radiusX + reach
    val ry = island.radiusY + reach
    // The mean radius times a full turn is close enough to the length of the shore for spacing waves.
    val count = ((rx + ry) / 2.0 * Angles.TWO_PI / SceneryDefaults.WAVE_SPACING).toInt()
    val width = viewport.px(SceneryDefaults.WAVE_WIDTH)
    val length = width * ScenerySprite.Wave.height / ScenerySprite.Wave.width
    for (i in 0 until count) {
        val along = Angles.direction(i * Angles.TWO_PI / count)
        val at = Vec2(island.center.x + along.x * rx, island.center.y + along.y * ry)
        drawScenerySprite(ScenerySprite.Wave, viewport.offsetOf(at), width, length, 0f, colors, lines)
    }
}

private fun sceneryOutline(lines: SceneryLines) = Stroke(lines.outline, cap = StrokeCap.Round, join = StrokeJoin.Round)

/** The paddock: lighter grass inside a fence drawn piece by piece along its four sides. */
private fun DrawScope.drawPaddock(paddock: Bounds, viewport: WorldViewport, colors: FaColors, lines: SceneryLines) {
    val topLeft = viewport.offsetOf(Vec2(paddock.left, paddock.top))
    val size = Size(viewport.px(paddock.width), viewport.px(paddock.height))
    drawRect(colors.grass300, topLeft, size)
    val fence = viewport.px(ScenerySprite.Fence.width.toDouble())
    val middle = Offset(topLeft.x + size.width / 2f, topLeft.y + size.height / 2f)
    // The two sides that run north to south, then the two that run west to east.
    for (x in listOf(topLeft.x, topLeft.x + size.width)) {
        drawSceneryRow(ScenerySprite.Fence, Offset(x, middle.y), fence, size.height, 0f, colors, lines)
    }
    for (y in listOf(topLeft.y, topLeft.y + size.height)) {
        drawSceneryRow(ScenerySprite.Fence, Offset(middle.x, y), fence, size.width, Backdrop.QUARTER_TURN_DEGREES, colors, lines)
    }
}

/**
 * The moving and glowing details of a level: cows and farmer, the flock, boats with their wake,
 * and at Dawn the running lights and the tower beacon. They are drawn above the field and below
 * the zone glows. Their sizes come from the ambience configuration of [life].
 */
internal fun DrawScope.drawAmbient(
    level: LevelConfig,
    life: AmbientLife,
    viewport: WorldViewport,
    colors: FaColors,
    lines: SceneryLines,
) {
    val ambience = life.config
    life.cows.forEach { cow ->
        drawScenerySprite(
            ScenerySprite.Cow,
            viewport.offsetOf(cow.position),
            viewport.px(ambience.cowWidth),
            viewport.px(ambience.cowLength),
            degreesOf(cow.heading),
            colors,
            lines,
        )
    }
    life.farmer?.let {
        drawScenerySprite(ScenerySprite.Farmer, viewport.offsetOf(it), viewport.px(ambience.farmerWidth), viewport.px(ambience.farmerLength), 0f, colors, lines)
    }
    life.boat?.let { drawBoat(it, ambience, viewport, colors, lines) }
    val bird = if (life.wingsOut) ScenerySprite.BirdUp else ScenerySprite.BirdDown
    life.flock.forEach {
        drawScenerySprite(bird, viewport.offsetOf(it), viewport.px(ambience.birdSpan), viewport.px(ambience.birdLength), degreesOf(life.flockHeading), colors, lines)
    }
    if (level.theme == LevelTheme.Dawn) {
        level.runways.forEach { drawRunwayLights(it, life, viewport) }
        level.scenery.filter { it.kind == SceneryKind.Tower }.forEach {
            val alpha = life.beaconOpacity()
            if (alpha > 0f) drawCircle(FaLights.beacon.copy(alpha = alpha), viewport.px(Backdrop.BEACON_RADIUS), viewport.offsetOf(it.position))
        }
    }
}

/** A boat with its deck, and behind it the wake it leaves in its direction of travel. */
private fun DrawScope.drawBoat(boat: Boat, ambience: LevelAmbienceConfig, viewport: WorldViewport, colors: FaColors, lines: SceneryLines) {
    val sprite = when (boat.kind) {
        BoatKind.Sailboat -> ScenerySprite.Sailboat
        BoatKind.Yacht -> ScenerySprite.Yacht
        BoatKind.ContainerVessel -> ScenerySprite.ContainerVessel
    }
    val heading = boat.direction.toFloat() * Backdrop.QUARTER_TURN_DEGREES
    val wakeLength = ambience.wakeLength(boat.kind)
    // The wake starts at the stern and trails away from the bow.
    val behind = boat.position - Vec2(boat.direction * (boat.kind.length + wakeLength) / 2.0, 0.0)
    drawScenerySprite(ScenerySprite.Wake, viewport.offsetOf(behind), viewport.px(ambience.wakeWidth(boat.kind)), viewport.px(wakeLength), heading, colors, lines)
    drawScenerySprite(sprite, viewport.offsetOf(boat.position), viewport.px(boat.kind.beam), viewport.px(boat.kind.length), heading, colors, lines)
}

private fun DrawScope.drawRunwayLights(runway: RunwayConfig, life: AmbientLife, viewport: WorldViewport) {
    val count = Backdrop.RUNNING_LIGHTS_PER_EDGE
    val along = Angles.direction(runway.orientation)
    val across = Vec2(-along.y, along.x)
    val head = life.runningLightHead(count)
    val radius = viewport.px(Backdrop.RUNNING_LIGHT_RADIUS)
    for (side in listOf(-1.0, 1.0)) for (i in 0 until count) {
        val t = (i + 0.5) / count - 0.5
        val point = runway.position + along * (t * runway.length) + across * (side * runway.width / 2.0)
        val alpha = lightAlpha(i, head, count, life.lightOpacity)
        drawCircle(FaLights.agl.copy(alpha = alpha), radius, viewport.offsetOf(point))
    }
}

/** The light at [index] is brightest at [head]; behind it the light fades out along the runway. */
private fun lightAlpha(index: Int, head: Int?, count: Int, max: Float): Float {
    if (head == null) return max
    val behind = (head - index + count) % count
    return (max * (1f - behind * Backdrop.TRAIL_FADE)).coerceAtLeast(max * Backdrop.UNLIT_FRACTION)
}

/** Navigation lights (green right, red left, white tail) and a headlight at the nose, for Dawn levels. */
internal fun DrawScope.drawAircraftLights(at: Offset, heading: Double, spritePx: Float, opacity: Float) {
    val forward = Angles.direction(heading)
    val ahead = Offset(forward.x.toFloat(), forward.y.toFloat())
    val right = Offset(-forward.y.toFloat(), forward.x.toFloat())
    val radius = spritePx / LIGHT_DIVISOR
    drawCircle(FaLights.navGreen.copy(alpha = opacity), radius, at + right * (spritePx / 2f))
    drawCircle(FaLights.navRed.copy(alpha = opacity), radius, at - right * (spritePx / 2f))
    drawCircle(FaLights.white.copy(alpha = opacity), radius, at - ahead * (spritePx / 2f))
    drawCircle(FaLights.beacon.copy(alpha = opacity), radius * HEADLIGHT_SCALE, at + ahead * (spritePx / 2f))
}

private const val LIGHT_DIVISOR = 14f

/** The headlight at the nose is this much larger than a navigation light. */
private const val HEADLIGHT_SCALE = 1.5f
