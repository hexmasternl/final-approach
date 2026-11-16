package nl.hexmaster.finalapproach.ui.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
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
import nl.hexmaster.finalapproach.domain.SceneryItem
import nl.hexmaster.finalapproach.domain.SceneryKind
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.cos
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.game.ambient.AmbientLife
import nl.hexmaster.finalapproach.ui.game.ambient.Boat
import nl.hexmaster.finalapproach.ui.game.ambient.BoatKind
import nl.hexmaster.finalapproach.ui.game.ambient.LevelAmbienceConfig
import nl.hexmaster.finalapproach.ui.sprites.GroundMetrics
import nl.hexmaster.finalapproach.ui.sprites.ScenerySprite
import nl.hexmaster.finalapproach.ui.sprites.drawSceneryRow
import nl.hexmaster.finalapproach.ui.sprites.drawSceneryRowShadows
import nl.hexmaster.finalapproach.ui.sprites.drawSceneryShadowAt
import nl.hexmaster.finalapproach.ui.sprites.drawScenerySprite
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaLights
import nl.hexmaster.finalapproach.ui.theme.FaOpacity

/**
 * Tuning of the lights of a Dawn level: the sizes of the beacon and the running light, in world
 * units, and how the running light fades. The design system names the light colours (`light-*`)
 * but gives no sizes or fade for them, so these are the app's own animation values and not design
 * metrics; if the design system ever sizes them, they move into its metrics table.
 */
internal object DawnLightDefaults {
    /** Radius of the glow of the tower beacon. */
    const val BEACON_RADIUS = 22.0

    /** The running light glows this much wider than the edge light it lights. */
    const val RUNNING_LIGHT_GLOW = 1.6f

    /** How much of its brightness the running light loses with each light behind its head. */
    const val TRAIL_FADE = 0.35f

    /** The least brightness of a light the running light has passed, as a fraction of full. */
    const val UNLIT_FRACTION = 0.2f
}

private object Backdrop {
    /** A boat's bow points up in its sprite; it sails to the right or to the left. */
    const val QUARTER_TURN_DEGREES = 90f
}

/**
 * The field layer of a level, everything below the zone glows and aircraft, in the order of
 * Ground.md: flat ground, flat scenery, helipads and runways, then the one layer of hard
 * shadows, then everything with height from north to south, then the ambient life.
 */
internal fun DrawScope.drawField(
    level: LevelConfig,
    ambient: AmbientLife,
    viewport: WorldViewport,
    colors: FaColors,
    measurer: TextMeasurer,
    numberStyle: TextStyle,
    art: GroundArt,
) {
    drawFieldStatic(level, viewport, colors, measurer, numberStyle, art, ambient)
    drawAmbient(level, ambient, viewport, colors)
}

/**
 * The part of the field that does not move: everything [drawField] draws except the ambient
 * life and the animated lights. Ambient shadows are drawn only when [ambient] is given. The
 * level tile's miniature draws this, in a bitmap the size of the world, and shows a crop of it.
 */
internal fun DrawScope.drawFieldStatic(
    level: LevelConfig,
    viewport: WorldViewport,
    colors: FaColors,
    measurer: TextMeasurer,
    numberStyle: TextStyle,
    art: GroundArt,
    ambient: AmbientLife? = null,
) {
    drawLevelGround(level, viewport, colors)
    drawPatches(level, art, viewport, colors)
    drawRoadsAndTracks(level, viewport, colors)
    drawFields(level, viewport, colors)
    drawWaters(level, viewport, colors)
    drawDecorationDetails(art, viewport, colors)
    drawAirfield(level, viewport, colors)
    drawHardSurfaces(level, viewport, colors)
    val (flat, raised) = level.scenery.partition { it.drawsOnGround }
    flat.filter { it.kind !in groundPieceKinds }.forEach { drawScenery(it, viewport, colors) }
    level.runways.forEach { drawRunway(it, viewport, colors, measurer, numberStyle) }
    level.helipads.forEach { drawHelipad(it, viewport, colors) }
    drawShadowLayer(level, raised, art.decoration.trees, ambient, viewport, colors)
    // Everything with height, from north to south: the level's scenery and the trees of its woods.
    val standing = raised.map { it.position.y to { drawScenery(it, viewport, colors) } } +
        art.decoration.trees.map { it.position.y to { drawTree(it, viewport, colors) } }
    standing.sortedBy { it.first }.forEach { it.second() }
    level.paddock?.let { drawPaddockFence(it, viewport, colors) }
}

/**
 * The one layer of hard ground shadows: the shadow of everything with height, scenery and
 * ambient life alike, each filled opaque in `shadow-cast` inside one layer that is faded as a
 * whole at `opacity-shadow-cast`. Overlapping shadows therefore do not darken twice. Flat
 * sprites (waves, wakes, crop tiles) cast none.
 */
private fun DrawScope.drawShadowLayer(
    level: LevelConfig,
    raised: List<SceneryItem>,
    trees: List<DecorationTree>,
    ambient: AmbientLife?,
    viewport: WorldViewport,
    colors: FaColors,
) {
    drawIntoCanvas { canvas ->
        canvas.saveLayer(Rect(Offset.Zero, size), Paint().apply { alpha = FaOpacity.shadowCast })
        raised.forEach { drawSceneryItemShadow(it, viewport, colors.shadowCast) }
        trees.forEach { drawTreeShadow(it, viewport, colors.shadowCast) }
        level.paddock?.let { paddock -> paddockFence(paddock, viewport).forEach { it.shadow(this, colors.shadowCast) } }
        ambient?.let { drawAmbientShadows(it, viewport, colors.shadowCast) }
        canvas.restore()
    }
}

/**
 * The ground under everything else: one flat tone (`grass-500`, `grass-400` on the dry savanna, or
 * the sea around the island with its shore) and the paddock's lighter grass. It is never striped:
 * only the airfield is mown.
 */
internal fun DrawScope.drawLevelGround(level: LevelConfig, viewport: WorldViewport, colors: FaColors) {
    when (level.landscape) {
        LevelScenery.Meadow -> drawRect(colors.grass500)
        LevelScenery.Savanna -> drawRect(colors.grass400)
        LevelScenery.Island -> {
            drawRect(colors.water500)
            level.island?.let { drawIslandShore(it, viewport, colors) }
        }
    }
    level.paddock?.let { drawPaddockGround(it, viewport, colors) }
}

/** One side of the paddock fence: a row of fence pieces along a strip. */
private class FenceSide(val center: Offset, val lengthPx: Float, val widthPx: Float, val degrees: Float) {
    fun draw(scope: DrawScope, colors: FaColors) =
        scope.drawSceneryRow(ScenerySprite.Fence, center, widthPx, lengthPx, degrees, colors)

    fun shadow(scope: DrawScope, color: Color) =
        scope.drawSceneryRowShadows(ScenerySprite.Fence, center, widthPx, lengthPx, degrees, color)
}

/** The four sides of the fence around [paddock]: the two that run north to south, then the two west to east. */
private fun paddockFence(paddock: Bounds, viewport: WorldViewport): List<FenceSide> {
    val topLeft = viewport.offsetOf(Vec2(paddock.left, paddock.top))
    val size = Size(viewport.px(paddock.width), viewport.px(paddock.height))
    val fence = viewport.px(ScenerySprite.Fence.width.toDouble())
    val middle = Offset(topLeft.x + size.width / 2f, topLeft.y + size.height / 2f)
    return listOf(topLeft.x, topLeft.x + size.width).map { FenceSide(Offset(it, middle.y), size.height, fence, 0f) } +
        listOf(topLeft.y, topLeft.y + size.height).map { FenceSide(Offset(middle.x, it), size.width, fence, Backdrop.QUARTER_TURN_DEGREES) }
}

private fun DrawScope.drawPaddockFence(paddock: Bounds, viewport: WorldViewport, colors: FaColors) =
    paddockFence(paddock, viewport).forEach { it.draw(this, colors) }

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
        )
    }
    life.farmer?.let {
        drawScenerySprite(ScenerySprite.Farmer, viewport.offsetOf(it), viewport.px(ambience.farmerWidth), viewport.px(ambience.farmerLength), 0f, colors)
    }
    life.boat?.let { drawBoat(it, ambience, viewport, colors) }
    val bird = if (life.wingsOut) ScenerySprite.BirdUp else ScenerySprite.BirdDown
    life.flock.forEach {
        drawScenerySprite(bird, viewport.offsetOf(it), viewport.px(ambience.birdSpan), viewport.px(ambience.birdLength), degreesOf(life.flockHeading), colors)
    }
    if (level.theme == LevelTheme.Dawn) {
        level.runways.forEach { drawRunwayLights(it, life, viewport) }
        level.scenery.filter { it.kind == SceneryKind.Tower }.forEach {
            val alpha = life.beaconOpacity()
            if (alpha > 0f) drawCircle(FaLights.beacon.copy(alpha = alpha), viewport.px(DawnLightDefaults.BEACON_RADIUS), viewport.offsetOf(it.position))
        }
    }
}

/** The shadows of the ambient life that casts one: cows, the farmer, boats and the flock. */
private fun DrawScope.drawAmbientShadows(life: AmbientLife, viewport: WorldViewport, color: Color) {
    val ambience = life.config
    life.cows.forEach { cow ->
        drawSceneryShadowAt(ScenerySprite.Cow, viewport.offsetOf(cow.position), viewport.px(ambience.cowWidth), viewport.px(ambience.cowLength), degreesOf(cow.heading), color)
    }
    life.farmer?.let {
        drawSceneryShadowAt(ScenerySprite.Farmer, viewport.offsetOf(it), viewport.px(ambience.farmerWidth), viewport.px(ambience.farmerLength), 0f, color)
    }
    life.boat?.let { boat ->
        val sprite = boatSprite(boat.kind)
        drawSceneryShadowAt(sprite, viewport.offsetOf(boat.position), viewport.px(boat.kind.beam), viewport.px(boat.kind.length), boat.direction.toFloat() * Backdrop.QUARTER_TURN_DEGREES, color)
    }
    val bird = if (life.wingsOut) ScenerySprite.BirdUp else ScenerySprite.BirdDown
    life.flock.forEach {
        drawSceneryShadowAt(bird, viewport.offsetOf(it), viewport.px(ambience.birdSpan), viewport.px(ambience.birdLength), degreesOf(life.flockHeading), color)
    }
}

private fun boatSprite(kind: BoatKind): ScenerySprite = when (kind) {
    BoatKind.Sailboat -> ScenerySprite.Sailboat
    BoatKind.Yacht -> ScenerySprite.Yacht
    BoatKind.ContainerVessel -> ScenerySprite.ContainerVessel
}

/** A boat with its deck, and behind it the wake it leaves in its direction of travel. */
private fun DrawScope.drawBoat(boat: Boat, ambience: LevelAmbienceConfig, viewport: WorldViewport, colors: FaColors) {
    val sprite = boatSprite(boat.kind)
    val heading = boat.direction.toFloat() * Backdrop.QUARTER_TURN_DEGREES
    val wakeLength = ambience.wakeLength(boat.kind)
    // The wake starts at the stern and trails away from the bow.
    val behind = boat.position - Vec2(boat.direction * (boat.kind.length + wakeLength) / 2.0, 0.0)
    drawScenerySprite(ScenerySprite.Wake, viewport.offsetOf(behind), viewport.px(ambience.wakeWidth(boat.kind)), viewport.px(wakeLength), heading, colors)
    drawScenerySprite(sprite, viewport.offsetOf(boat.position), viewport.px(boat.kind.beam), viewport.px(boat.kind.length), heading, colors)
}

/**
 * The running light of a Dawn level: it runs along both edges of [runway], one edge light after
 * the other, over the edge lights of the runway's plan.
 */
private fun DrawScope.drawRunwayLights(runway: RunwayConfig, life: AmbientLife, viewport: WorldViewport) {
    val landingEnds = runway.landingHeadings.map { if (cos(it - runway.orientation) >= 0.0) RunwayEnd.Low else RunwayEnd.High }.toSet()
    val plan = runwayPlan(runway.length.toFloat(), landingEnds, runway.width.toFloat() / GroundMetrics.Runway.width)
    val count = plan.edgeLightsPerSide
    val head = life.runningLightHead(count)
    val radius = GroundMetrics.Runway.Lights.Edge.r * DawnLightDefaults.RUNNING_LIGHT_GLOW
    inRunwayFrame(runway, viewport) {
        plan.edgeLights.forEachIndexed { index, light ->
            val alpha = lightAlpha(index / 2, head, count, life.lightOpacity)
            drawCircle(FaLights.agl.copy(alpha = alpha), radius, Offset(light.x, light.y))
        }
    }
}

/** The light at [index] is brightest at [head]; behind it the light fades out along the runway. */
private fun lightAlpha(index: Int, head: Int?, count: Int, max: Float): Float {
    if (head == null) return max
    val behind = (head - index + count) % count
    return (max * (1f - behind * DawnLightDefaults.TRAIL_FADE)).coerceAtLeast(max * DawnLightDefaults.UNLIT_FRACTION)
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
