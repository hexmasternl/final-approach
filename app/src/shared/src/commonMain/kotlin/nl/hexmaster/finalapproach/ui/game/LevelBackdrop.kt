package nl.hexmaster.finalapproach.ui.game

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.Bounds
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.LevelScenery
import nl.hexmaster.finalapproach.domain.LevelTheme
import nl.hexmaster.finalapproach.domain.RunwayConfig
import nl.hexmaster.finalapproach.domain.SceneryKind
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.game.ambient.AmbientLife
import nl.hexmaster.finalapproach.ui.game.ambient.BoatKind
import nl.hexmaster.finalapproach.ui.sprites.drawLawn
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaLights

/** Sizes of the landscape details, in world units. */
private object Backdrop {
    const val COW_LENGTH = 30.0
    const val COW_WIDTH = 16.0
    const val FARMER_RADIUS = 7.0
    const val FENCE_POST = 5.0
    const val FENCE_POST_GAP = 36.0
    const val BIRD_SPAN = 18.0
    const val ACACIA_RADIUS = 34.0
    const val BUSH_RADIUS = 16.0
    const val ROCK_RADIUS = 14.0
    const val BEACON_RADIUS = 22.0
    const val RUNNING_LIGHT_RADIUS = 4.0
    const val RUNNING_LIGHTS_PER_EDGE = 12
    const val TRAIL_FADE = 0.35f
    const val UNLIT_FRACTION = 0.2f
    const val SHORE_WIDTH = 14.0

    /** Where the savanna decorations stand, kept clear of every landing zone. */
    val acacias = listOf(Vec2(70.0, 330.0), Vec2(200.0, 640.0), Vec2(1210.0, 640.0), Vec2(1220.0, 400.0))
    val bushes = listOf(Vec2(180.0, 200.0), Vec2(900.0, 640.0), Vec2(980.0, 170.0), Vec2(60.0, 520.0))
    val rocks = listOf(Vec2(120.0, 560.0), Vec2(1000.0, 420.0), Vec2(780.0, 670.0))
}

private fun DrawScope.outline() = Stroke(FaDimens.line3.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

/** The ground of a level: lawn, sea and island, or dry dawn grass, plus the fixed decorations. */
internal fun DrawScope.drawLevelGround(level: LevelConfig, viewport: WorldViewport, colors: FaColors) {
    when (level.landscape) {
        LevelScenery.Meadow -> {
            drawLawn(GameScreenDefaults.lawnStripe.toPx(), colors)
            level.paddock?.let { drawPaddock(it, viewport, colors) }
        }

        LevelScenery.Island -> {
            drawRect(colors.sky500)
            val island = level.island ?: return
            val center = viewport.offsetOf(island.center)
            val rx = viewport.px(island.radiusX)
            val ry = viewport.px(island.radiusY)
            val shore = viewport.px(Backdrop.SHORE_WIDTH)
            drawOval(colors.sun, center - Offset(rx + shore, ry + shore), Size((rx + shore) * 2f, (ry + shore) * 2f))
            drawOval(colors.grass500, center - Offset(rx, ry), Size(rx * 2f, ry * 2f))
            drawOval(colors.outline, center - Offset(rx + shore, ry + shore), Size((rx + shore) * 2f, (ry + shore) * 2f), style = outline())
        }

        LevelScenery.Savanna -> {
            drawLawn(FaDimens.space8.toPx(), colors)
            Backdrop.rocks.forEach { drawRock(viewport.offsetOf(it), viewport.px(Backdrop.ROCK_RADIUS), colors) }
            Backdrop.bushes.forEach { drawBush(viewport.offsetOf(it), viewport.px(Backdrop.BUSH_RADIUS), colors) }
            Backdrop.acacias.forEach { drawAcacia(viewport.offsetOf(it), viewport.px(Backdrop.ACACIA_RADIUS), colors) }
        }
    }
}

private fun DrawScope.drawPaddock(paddock: Bounds, viewport: WorldViewport, colors: FaColors) {
    val topLeft = viewport.offsetOf(Vec2(paddock.left, paddock.top))
    val size = Size(viewport.px(paddock.right - paddock.left), viewport.px(paddock.bottom - paddock.top))
    drawRect(colors.grass300, topLeft, size)
    val post = viewport.px(Backdrop.FENCE_POST)
    val gap = viewport.px(Backdrop.FENCE_POST_GAP)
    drawRect(colors.tangerineDeep, topLeft, size, style = Stroke(FaDimens.line2.toPx()))
    var x = topLeft.x
    while (x <= topLeft.x + size.width) {
        drawCircle(colors.tangerineDeep, post, Offset(x, topLeft.y))
        drawCircle(colors.tangerineDeep, post, Offset(x, topLeft.y + size.height))
        x += gap
    }
    var y = topLeft.y
    while (y <= topLeft.y + size.height) {
        drawCircle(colors.tangerineDeep, post, Offset(topLeft.x, y))
        drawCircle(colors.tangerineDeep, post, Offset(topLeft.x + size.width, y))
        y += gap
    }
}

private fun DrawScope.drawAcacia(at: Offset, radius: Float, colors: FaColors) {
    drawRoundRect(colors.tangerineDeep, at - Offset(radius / 8f, 0f), Size(radius / 4f, radius), CornerRadius(radius / 8f))
    val canopy = Size(radius * 2f, radius)
    val topLeft = at - Offset(radius, radius * 0.9f)
    drawOval(colors.grass700, topLeft, canopy)
    drawOval(colors.outline, topLeft, canopy, style = outline())
}

private fun DrawScope.drawBush(at: Offset, radius: Float, colors: FaColors) {
    drawCircle(colors.grass700, radius, at)
    drawCircle(colors.outline, radius, at, style = outline())
}

private fun DrawScope.drawRock(at: Offset, radius: Float, colors: FaColors) {
    val size = Size(radius * 2f, radius * 1.4f)
    drawRoundRect(colors.asphalt300, at - Offset(radius, radius * 0.7f), size, CornerRadius(radius * 0.6f))
    drawRoundRect(colors.outline, at - Offset(radius, radius * 0.7f), size, CornerRadius(radius * 0.6f), style = outline())
}

/**
 * The moving and glowing details of a level: cows and farmer, the bird, boats, and at Dawn the
 * running lights and the tower beacon. They are drawn above the field and below the zone glows.
 */
internal fun DrawScope.drawAmbient(level: LevelConfig, life: AmbientLife, viewport: WorldViewport, colors: FaColors) {
    val paddock = level.paddock
    if (paddock != null) {
        life.cows.forEach { drawCow(viewport.offsetOf(it.position), viewport, colors) }
        drawFarmer(viewport.offsetOf(Vec2(paddock.left + FARMER_INSET, paddock.top + FARMER_INSET)), viewport, colors)
    }
    life.boat?.let { drawBoat(it.kind, viewport.offsetOf(it.position), it.direction.toFloat(), viewport, colors) }
    life.bird?.let { drawBird(viewport.offsetOf(it.position), viewport.px(Backdrop.BIRD_SPAN), colors) }
    if (level.theme == LevelTheme.Dawn) {
        level.runways.forEach { drawRunwayLights(it, life, viewport) }
        level.scenery.filter { it.kind == SceneryKind.Tower }.forEach {
            val alpha = life.beaconOpacity()
            if (alpha > 0f) drawCircle(FaLights.beacon.copy(alpha = alpha), viewport.px(Backdrop.BEACON_RADIUS), viewport.offsetOf(it.position))
        }
    }
}

private const val FARMER_INSET = 22.0

private fun DrawScope.drawCow(at: Offset, viewport: WorldViewport, colors: FaColors) {
    val size = Size(viewport.px(Backdrop.COW_LENGTH), viewport.px(Backdrop.COW_WIDTH))
    val topLeft = at - Offset(size.width / 2f, size.height / 2f)
    drawOval(colors.surfaceRaised, topLeft, size)
    drawCircle(colors.outline, size.height / 4f, at + Offset(size.width / 8f, 0f))
    drawOval(colors.outline, topLeft, size, style = outline())
}

private fun DrawScope.drawFarmer(at: Offset, viewport: WorldViewport, colors: FaColors) {
    val radius = viewport.px(Backdrop.FARMER_RADIUS)
    drawCircle(colors.sky700, radius, at)
    drawCircle(colors.sun, radius / 2f, at)
    drawCircle(colors.outline, radius, at, style = outline())
}

private fun DrawScope.drawBird(at: Offset, span: Float, colors: FaColors) {
    val wing = Path().apply {
        moveTo(at.x - span / 2f, at.y - span / 4f)
        lineTo(at.x, at.y + span / 6f)
        lineTo(at.x + span / 2f, at.y - span / 4f)
    }
    drawPath(wing, colors.outline, style = Stroke(FaDimens.line3.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun DrawScope.drawBoat(kind: BoatKind, at: Offset, direction: Float, viewport: WorldViewport, colors: FaColors) {
    val length = viewport.px(kind.length)
    val beam = viewport.px(kind.beam)
    val hull = Rect(at - Offset(length / 2f, beam / 2f), Size(length, beam))
    val corner = CornerRadius(beam / 2f)
    val body = when (kind) {
        BoatKind.Sailboat -> colors.surfaceRaised
        BoatKind.Yacht -> colors.cherry
        BoatKind.ContainerVessel -> colors.asphalt700
    }
    drawRoundRect(body, hull.topLeft, hull.size, corner)
    drawRoundRect(colors.outline, hull.topLeft, hull.size, corner, style = outline())
    when (kind) {
        BoatKind.Sailboat -> {
            val sail = Path().apply {
                moveTo(at.x - direction * length / 6f, at.y - beam / 2f)
                lineTo(at.x + direction * length / 3f, at.y)
                lineTo(at.x - direction * length / 6f, at.y + beam / 2f)
                close()
            }
            drawPath(sail, colors.sun)
            drawPath(sail, colors.outline, style = outline())
        }

        BoatKind.Yacht -> drawRoundRect(colors.surfaceRaised, at - Offset(length / 5f, beam / 4f), Size(length / 2.5f, beam / 2f), CornerRadius(beam / 6f))

        BoatKind.ContainerVessel -> listOf(colors.tangerine, colors.sky500, colors.success, colors.cherry).forEachIndexed { i, tint ->
            val box = Size(length / 6f, beam / 2f)
            drawRect(tint, hull.topLeft + Offset(length / 10f + i * length / 5f, beam / 4f), box)
        }
    }
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

/** Navigation lights and a headlight on an aircraft at [at], heading [heading], for Dawn levels. */
internal fun DrawScope.drawAircraftLights(at: Offset, heading: Double, spritePx: Float, opacity: Float) {
    val forward = Angles.direction(heading)
    val ahead = Offset(forward.x.toFloat(), forward.y.toFloat())
    val right = Offset(-forward.y.toFloat(), forward.x.toFloat())
    val radius = spritePx / LIGHT_DIVISOR
    drawCircle(FaLights.navGreen.copy(alpha = opacity), radius, at + right * (spritePx / 2f))
    drawCircle(FaLights.navRed.copy(alpha = opacity), radius, at - right * (spritePx / 2f))
    drawCircle(FaLights.white.copy(alpha = opacity), radius, at + ahead * (spritePx / 2f))
}

private const val LIGHT_DIVISOR = 14f
