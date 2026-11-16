package nl.hexmaster.finalapproach.ui.game

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.Bounds
import nl.hexmaster.finalapproach.domain.Island
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.LevelScenery
import nl.hexmaster.finalapproach.domain.SceneryDefaults
import nl.hexmaster.finalapproach.domain.SceneryItem
import nl.hexmaster.finalapproach.domain.SceneryKind
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.sprites.GroundMetrics.Ground
import nl.hexmaster.finalapproach.ui.sprites.ScenerySprite
import nl.hexmaster.finalapproach.ui.sprites.drawSceneryArt
import nl.hexmaster.finalapproach.ui.sprites.drawScenerySprite
import nl.hexmaster.finalapproach.ui.theme.FaColors
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * The flat ground of a level, drawn as the design system builds it (`Ground.md`): every piece in
 * its own material with the band that joins it to its neighbour, and none of them in `outline`
 * (W6). Everything is drawn in world units, so the sizes and line widths of `GroundMetrics`
 * apply as they are and the whole field shrinks with the viewport.
 */

/**
 * Runs [block] in the world's own frame: the origin is the world's top-left corner and one unit
 * is one world unit.
 */
internal fun DrawScope.inWorld(viewport: WorldViewport, block: DrawScope.() -> Unit) {
    val origin = viewport.offsetOf(Vec2(0.0, 0.0))
    val unit = viewport.px(WORLD_UNIT)
    withTransform({
        translate(origin.x, origin.y)
        scale(unit, unit, Offset.Zero)
    }, block)
}

/** Runs [block] in the frame of [item] inside the world frame: its centre is the origin, its width across x and its height along y. */
private fun DrawScope.inItem(item: SceneryItem, block: DrawScope.() -> Unit) {
    withTransform({
        translate(item.position.x.toFloat(), item.position.y.toFloat())
        rotate(degreesOf(item.rotation), Offset.Zero)
    }, block)
}

private fun butt(width: Float, effect: PathEffect? = null) = Stroke(width, cap = StrokeCap.Butt, join = StrokeJoin.Round, pathEffect = effect)

private fun round(width: Float) = Stroke(width, cap = StrokeCap.Round, join = StrokeJoin.Round)

private fun DrawScope.band(color: Color, across: Float, along: Float) =
    drawRect(color, Offset(-across / 2f, -along / 2f), Size(across, along))

private fun DrawScope.roundedBox(color: Color, size: Size, radius: Float, style: Stroke? = null) {
    val topLeft = Offset(-size.width / 2f, -size.height / 2f)
    if (style == null) drawRoundRect(color, topLeft, size, CornerRadius(radius)) else drawRoundRect(color, topLeft, size, CornerRadius(radius), style = style)
}

private fun dashes(pattern: List<Float>) = PathEffect.dashPathEffect(pattern.toFloatArray())

private val SceneryItem.size: Size get() = Size(width.toFloat(), height.toFloat())

private fun List<SceneryItem>.of(vararg kinds: SceneryKind) = filter { it.kind in kinds }

/**
 * What is built once per level for drawing its ground: the decoration (a pure function of the
 * level) and the smooth curves of its patches. Level data does not change while it is on screen.
 */
internal class GroundArt(val decoration: LevelDecoration) {
    val patchPaths: List<Path> = decoration.patches.map { smoothClosedPath(it.outline) }
}

private var cachedArt: Pair<LevelConfig, GroundArt>? = null

/** The ground art of [level], built the first time it is asked for and kept while the same level is drawn. */
internal fun groundArtOf(level: LevelConfig): GroundArt {
    cachedArt?.let { (cachedLevel, art) -> if (cachedLevel === level) return art }
    return GroundArt(levelDecoration(level)).also { cachedArt = level to it }
}

/** A smooth closed curve through [points]: the design system's `smooth`, a Catmull-Rom spline as cubic curves. */
internal fun smoothClosedPath(points: List<Vec2>): Path {
    val path = Path()
    val n = points.size
    if (n < 3) return path
    fun at(i: Int) = points[(i + n) % n]
    path.moveTo(points[0].x.toFloat(), points[0].y.toFloat())
    for (i in 0 until n) {
        val p0 = at(i - 1)
        val p1 = at(i)
        val p2 = at(i + 1)
        val p3 = at(i + 2)
        path.cubicTo(
            (p1.x + (p2.x - p0.x) / SPLINE_TENSION).toFloat(), (p1.y + (p2.y - p0.y) / SPLINE_TENSION).toFloat(),
            (p2.x - (p3.x - p1.x) / SPLINE_TENSION).toFloat(), (p2.y - (p3.y - p1.y) / SPLINE_TENSION).toFloat(),
            p2.x.toFloat(), p2.y.toFloat(),
        )
    }
    path.close()
    return path
}

/** A Catmull-Rom curve's handles are the neighbours' distance over six. */
private const val SPLINE_TENSION = 6.0

/** Irregular patches of tone on open ground, under the roads and everything else: tone, not objects. */
internal fun DrawScope.drawPatches(level: LevelConfig, art: GroundArt, viewport: WorldViewport, colors: FaColors) {
    val dry = level.landscape == LevelScenery.Savanna
    inWorld(viewport) {
        art.decoration.patches.forEachIndexed { index, patch ->
            val (color, alpha) = when (patch.tone) {
                PatchTone.Light -> (if (dry) colors.sand300 else colors.grass400) to Ground.Patch.lightOpacity
                PatchTone.Dark -> (if (dry) colors.grass500 else colors.grass600) to Ground.Patch.darkOpacity
                PatchTone.Sea -> colors.water700 to Ground.Patch.seaOpacity
            }
            drawPath(art.patchPaths[index], color, alpha = alpha)
        }
    }
}

/** Tufts, flowers and reeds, in clusters on open ground. */
internal fun DrawScope.drawDecorationDetails(art: GroundArt, viewport: WorldViewport, colors: FaColors) {
    inWorld(viewport) {
        art.decoration.details.forEach { detail ->
            withTransform({ translate(detail.position.x.toFloat(), detail.position.y.toFloat()) }) { drawSceneryArt(detail.detail.art, colors) }
        }
    }
}

/** Roads and dirt tracks, tracks first so a road runs over them. */
internal fun DrawScope.drawRoadsAndTracks(level: LevelConfig, viewport: WorldViewport, colors: FaColors) {
    val tracks = level.scenery.of(SceneryKind.Track)
    val roads = level.scenery.of(SceneryKind.Road)
    inWorld(viewport) {
        drawTracks(tracks, colors)
        drawRoads(roads, colors)
    }
}

/**
 * Roads: the verge of every road is drawn before the kerb of any, and the kerb before the
 * surface, so where two roads meet the bands do not cross the junction. A road's ends are open:
 * end a road under an apron, inside another road or at the world's edge.
 */
private fun DrawScope.drawRoads(roads: List<SceneryItem>, colors: FaColors) {
    val m = Ground.Road
    fun layer(width: Float, color: Color) = roads.forEach { road -> inItem(road) { band(color, width * road.width.toFloat() / m.verge, road.height.toFloat()) } }
    layer(m.verge, colors.grass700)
    layer(m.kerb, colors.road700)
    layer(m.surface, colors.road500)
    roads.forEach { road ->
        inItem(road) {
            val half = road.height.toFloat() / 2f
            drawLine(colors.foam, Offset(0f, -half), Offset(0f, half), Ground.Lines.roadDash, StrokeCap.Butt, dashes(m.dash))
        }
    }
}

/** A dirt track: a darker sand edge, the sand surface and one rut. */
private fun DrawScope.drawTracks(tracks: List<SceneryItem>, colors: FaColors) {
    val m = Ground.Track
    tracks.forEach { track ->
        inItem(track) {
            val width = track.width.toFloat()
            val half = track.height.toFloat() / 2f
            band(colors.sand500, width + m.edge, track.height.toFloat())
            band(colors.sand300, width, track.height.toFloat())
            drawLine(colors.sand500, Offset(-m.rutShift, -half), Offset(-m.rutShift, half), Ground.Lines.trackRut, StrokeCap.Butt, dashes(m.rutDash))
        }
    }
}

/** The crop fields: rows, edge line and verge, in the material of their variant. */
internal fun DrawScope.drawFields(level: LevelConfig, viewport: WorldViewport, colors: FaColors) {
    val fields = level.scenery.of(SceneryKind.Field, SceneryKind.FieldWheat, SceneryKind.FieldGreen, SceneryKind.FieldPlough)
    inWorld(viewport) { fields.forEach { drawCropField(it, colors) } }
}

private class Crop(val fill: Color, val rows: Color, val edge: Color, val rowWidth: Float)

private fun cropOf(kind: SceneryKind, colors: FaColors): Crop = when (kind) {
    SceneryKind.FieldGreen -> Crop(colors.soil500, colors.leaf300, colors.soil700, Ground.Lines.cropRows)
    SceneryKind.FieldPlough -> Crop(colors.soil500, colors.soil700, colors.soil700, Ground.Lines.ploughRows)
    // The plain field of the first version of the levels is wheat.
    else -> Crop(colors.straw300, colors.straw500, colors.straw700, Ground.Lines.cropRows)
}

private fun DrawScope.drawCropField(field: SceneryItem, colors: FaColors) {
    val m = Ground.Field
    val crop = cropOf(field.kind, colors)
    inItem(field) {
        val w = field.width.toFloat()
        val h = field.height.toFloat()
        roundedBox(colors.grass700, Size(w + 2f * m.margin, h + 2f * m.margin), m.outerRx)
        roundedBox(crop.fill, field.size, m.innerRx)
        val shape = Path().apply { addRoundRect(RoundRect(Rect(Offset(-w / 2f, -h / 2f), field.size), CornerRadius(m.innerRx))) }
        clipPath(shape) {
            val rows = Path()
            // Rows run along the longer side, every rowPitch.
            if (h > w) {
                var x = -w / 2f + m.rowInset
                while (x < w / 2f) {
                    rows.moveTo(x, -h / 2f)
                    rows.lineTo(x, h / 2f)
                    x += m.rowPitch
                }
            } else {
                var y = -h / 2f + m.rowInset
                while (y < h / 2f) {
                    rows.moveTo(-w / 2f, y)
                    rows.lineTo(w / 2f, y)
                    y += m.rowPitch
                }
            }
            drawPath(rows, crop.rows, style = round(crop.rowWidth))
        }
        roundedBox(crop.edge, field.size, m.innerRx, round(Ground.Lines.fieldEdge))
    }
}

/** Still water: damp grass, sand, shallows with a shore line, open water and three ripples. */
internal fun DrawScope.drawWaters(level: LevelConfig, viewport: WorldViewport, colors: FaColors) {
    val waters = level.scenery.of(SceneryKind.Pond, SceneryKind.Waterhole)
    inWorld(viewport) { waters.forEach { drawPond(it, colors) } }
}

private fun DrawScope.drawPond(pond: SceneryItem, colors: FaColors) {
    val m = Ground.Pond
    inItem(pond) {
        val topLeft = Offset(-pond.width.toFloat() / 2f, -pond.height.toFloat() / 2f)
        val size = pond.size
        drawOval(colors.grass700, topLeft, size, style = round(m.shore))
        drawOval(colors.sand300, topLeft, size)
        drawOval(colors.sand300, topLeft, size, style = round(m.sand))
        drawOval(colors.water300, topLeft, size)
        drawOval(colors.water700, topLeft, size, style = round(Ground.Lines.pondShore))
        val open = Size(size.width * m.shallowsX, size.height * m.shallowsY)
        drawOval(colors.water500, Offset(-open.width / 2f, -open.height / 2f), open)
        val ripples = Path()
        Ground.pondRipples.forEach { (x, y, controlX, controlY, endX) ->
            ripples.moveTo(x, y)
            ripples.relativeQuadraticBezierTo(controlX, controlY, endX, 0f)
        }
        drawPath(ripples, colors.foam, style = round(Ground.Lines.pondFoam))
    }
}

/**
 * The mown airfield: inside the level's outline the ground is `grass-500` mown in `grass-600`
 * stripes parallel to the level's main runway (its first), with a soft `grass-700` boundary
 * (W8). Nothing outside the outline is striped.
 */
internal fun DrawScope.drawAirfield(level: LevelConfig, viewport: WorldViewport, colors: FaColors) {
    if (level.airfield.size < MIN_OUTLINE_POINTS) return
    val outline = roundedPolygon(level.airfield, Ground.airfieldCorner)
    inWorld(viewport) {
        drawPath(outline, colors.grass700, alpha = Ground.airfieldBoundaryOpacity, style = round(Ground.airfieldBoundary))
        clipPath(outline) {
            drawPath(outline, colors.grass500)
            val main = level.runways.firstOrNull()
            val origin = main?.position ?: Vec2(level.world.width / 2.0, level.world.height / 2.0)
            val turn = main?.let { degreesOf(it.orientation) - QUARTER_TURN_DEGREES } ?: 0f
            withTransform({
                translate(origin.x.toFloat(), origin.y.toFloat())
                rotate(turn, Offset.Zero)
            }) {
                // Stripes lie along the runway: one every stripePitch across it, each stripe wide, as far as the world reaches.
                val reach = hypot(level.world.width, level.world.height).toFloat()
                val first = -ceil(reach / Ground.stripePitch).toInt()
                val last = ceil(reach / Ground.stripePitch).toInt()
                for (n in first..last) drawRect(colors.grass600, Offset(-reach, n * Ground.stripePitch), Size(2f * reach, Ground.stripe))
            }
        }
    }
}

/** A polygon with each corner rounded by [radius] (less on a short side), as one closed path. */
internal fun roundedPolygon(corners: List<Vec2>, radius: Float): Path {
    val path = Path()
    val n = corners.size
    for (i in 0 until n) {
        val before = corners[(i + n - 1) % n]
        val corner = corners[i]
        val after = corners[(i + 1) % n]
        val inLength = hypot(corner.x - before.x, corner.y - before.y)
        val outLength = hypot(after.x - corner.x, after.y - corner.y)
        val cut = minOf(radius.toDouble(), inLength / 2.0, outLength / 2.0)
        val start = Vec2(corner.x + (before.x - corner.x) * cut / inLength, corner.y + (before.y - corner.y) * cut / inLength)
        val end = Vec2(corner.x + (after.x - corner.x) * cut / outLength, corner.y + (after.y - corner.y) * cut / outLength)
        if (i == 0) path.moveTo(start.x.toFloat(), start.y.toFloat()) else path.lineTo(start.x.toFloat(), start.y.toFloat())
        path.quadraticTo(corner.x.toFloat(), corner.y.toFloat(), end.x.toFloat(), end.y.toFloat())
    }
    path.close()
    return path
}

/** An outline needs three corners to hold anything. */
private const val MIN_OUTLINE_POINTS = 3

/** Aprons and car parks, then taxiways, in the order of Ground.md. */
internal fun DrawScope.drawHardSurfaces(level: LevelConfig, viewport: WorldViewport, colors: FaColors) {
    val parked = level.scenery.of(SceneryKind.ParkedAircraft)
    inWorld(viewport) {
        level.scenery.of(SceneryKind.Apron).forEach { drawApron(it, parked, colors) }
        level.scenery.of(SceneryKind.CarPark).forEach { drawCarPark(it, colors) }
        level.scenery.of(SceneryKind.Taxiway).forEach { drawTaxiway(it, level, colors) }
    }
}

/** An apron: a concrete slab with joints and an edge line, a stand mark under each parked aircraft, inside a rough-grass verge. */
private fun DrawScope.drawApron(apron: SceneryItem, parked: List<SceneryItem>, colors: FaColors) {
    val m = Ground.Apron
    inItem(apron) {
        val w = apron.width.toFloat()
        val h = apron.height.toFloat()
        roundedBox(colors.grass700, Size(w + 2f * m.margin, h + 2f * m.margin), m.rx)
        roundedBox(colors.concrete300, apron.size, m.innerRx)
        roundedBox(colors.concrete700, apron.size, m.innerRx, round(Ground.Lines.apronSlab))
        val joints = Path()
        var x = -w / 2f + m.joint
        while (x < w / 2f - JOINT_END_ROOM) {
            joints.moveTo(x, -h / 2f)
            joints.lineTo(x, h / 2f)
            x += m.joint
        }
        joints.moveTo(-w / 2f, 0f)
        joints.lineTo(w / 2f, 0f)
        drawPath(joints, colors.concrete500, style = round(Ground.Lines.apronJoint))
        val stand = Ground.Stand
        parked.filter { Bounds(apron.position.x - w / 2.0, apron.position.y - h / 2.0, apron.position.x + w / 2.0, apron.position.y + h / 2.0).contains(it.position) }
            .forEach {
                val at = it.position - apron.position
                val marks = Path().apply {
                    moveTo(at.x.toFloat(), at.y.toFloat() + stand.down)
                    relativeLineTo(0f, -stand.length)
                    moveTo(at.x.toFloat() - stand.barHalf, at.y.toFloat() + stand.barAt)
                    relativeLineTo(2f * stand.barHalf, 0f)
                }
                drawPath(marks, colors.markingWarn, style = round(Ground.Lines.apronStand))
            }
    }
}

/** How far short of an apron's right edge its last joint ends: joints too close to the edge are left out. */
private const val JOINT_END_ROOM = 10f

/** A car park: a concrete slab with bay lines inside a rough-grass verge. */
private fun DrawScope.drawCarPark(lot: SceneryItem, colors: FaColors) {
    val m = Ground.Lot
    inItem(lot) {
        val w = lot.width.toFloat()
        val h = lot.height.toFloat()
        roundedBox(colors.grass700, Size(w + 2f * m.margin, h + 2f * m.margin), m.rx)
        roundedBox(colors.concrete300, lot.size, m.innerRx)
        roundedBox(colors.concrete700, lot.size, m.innerRx, round(Ground.Lines.lotSlab))
        val bays = Path()
        var x = -w / 2f + m.bay
        while (x < w / 2f - BAY_END_ROOM) {
            bays.moveTo(x, -h / 2f)
            bays.relativeLineTo(0f, h * Ground.lotBayLength)
            x += m.bay
        }
        drawPath(bays, colors.foam, style = round(Ground.Lines.lotBay))
    }
}

/** How far short of a car park's right edge its last bay line ends. */
private const val BAY_END_ROOM = 6f

/**
 * A taxiway: lighter asphalt than a runway, a concrete edge and a rough-grass verge, a yellow
 * centre line and, where it meets a runway, hold-short bars. It has no navy outline.
 */
private fun DrawScope.drawTaxiway(taxiway: SceneryItem, level: LevelConfig, colors: FaColors) {
    val m = Ground.Taxi
    val h = taxiway.height.toFloat()
    val scale = taxiway.width.toFloat() / m.verge
    inItem(taxiway) {
        band(colors.grass700, m.verge * scale, h)
        band(colors.concrete700, m.kerb * scale, h)
        band(colors.asphalt300, m.surface * scale, h)
        drawLine(colors.markingWarn, Offset(0f, -h / 2f), Offset(0f, h / 2f), Ground.Lines.taxiCentre, StrokeCap.Butt)
        val hold = Ground.HoldShort
        if (h >= hold.fromEnd + hold.dashAt + hold.dashGap) {
            // Towards the end that lies nearer a runway, in the taxiway's own frame.
            val sign = towardsRunway(taxiway, level)
            val y = sign * (h / 2f - hold.fromEnd)
            val bars = Path()
            val solid = listOf(0f, hold.gap)
            val dashed = listOf(hold.dashAt, hold.dashAt + hold.dashGap)
            solid.forEach {
                bars.moveTo(-hold.half, y + sign * it)
                bars.relativeLineTo(2f * hold.half, 0f)
            }
            drawPath(bars, colors.markingWarn, style = round(Ground.Lines.taxiHold))
            val dashedBars = Path()
            dashed.forEach {
                dashedBars.moveTo(-hold.half, y + sign * it)
                dashedBars.relativeLineTo(2f * hold.half, 0f)
            }
            drawPath(dashedBars, colors.markingWarn, style = butt(Ground.Lines.taxiHold, dashes(hold.dash)))
        }
    }
}

/** 1 when the end of [taxiway] at the positive end of its own length axis lies nearer a runway, else -1. */
internal fun towardsRunway(taxiway: SceneryItem, level: LevelConfig): Float {
    // The item's local y axis (its height) points where the unrotated down direction turns to.
    val down = Vec2(-sin(taxiway.rotation), cos(taxiway.rotation))
    val end = taxiway.position + down * (taxiway.height / 2.0)
    val start = taxiway.position - down * (taxiway.height / 2.0)
    fun gap(point: Vec2) = level.runways.minOfOrNull { it.shape.toLocal(point).let { local -> hypot(maxOf(abs(local.x) - it.length / 2.0, 0.0), maxOf(abs(local.y) - it.width / 2.0, 0.0)) } } ?: Double.MAX_VALUE
    return if (gap(end) <= gap(start)) 1f else -1f
}

/** The ground of the paddock: lighter grass with an edge line. Its fence stands, so it is drawn with the standing scenery. */
internal fun DrawScope.drawPaddockGround(paddock: Bounds, viewport: WorldViewport, colors: FaColors) {
    inWorld(viewport) {
        val topLeft = Offset(paddock.left.toFloat(), paddock.top.toFloat())
        val size = Size(paddock.width.toFloat(), paddock.height.toFloat())
        drawRect(colors.grass400, topLeft, size, alpha = Ground.paddockOpacity)
        drawRect(colors.grass700, topLeft, size, alpha = Ground.paddockOpacity, style = butt(Ground.Lines.paddock))
    }
}

/**
 * The shore of an island, outwards from its grass: a sand beach with an edge line, shallows, and a
 * broken line of foam off the beach, then waves further out; the grass has a darker rim.
 */
internal fun DrawScope.drawIslandShore(island: Island, viewport: WorldViewport, colors: FaColors) {
    val m = Ground.Island
    inWorld(viewport) {
        fun oval(extra: Float): Pair<Offset, Size> {
            val rx = island.radiusX.toFloat() + extra
            val ry = island.radiusY.toFloat() + extra
            return Offset(island.center.x.toFloat() - rx, island.center.y.toFloat() - ry) to Size(2f * rx, 2f * ry)
        }
        val (shallowsAt, shallowsSize) = oval(m.beach + m.shallows)
        drawOval(colors.water300, shallowsAt, shallowsSize)
        val (foamAt, foamSize) = oval(m.beach + m.foamOut)
        drawOval(colors.foam, foamAt, foamSize, style = butt(m.foamWidth, dashes(m.foamDash)))
        val (sandAt, sandSize) = oval(m.beach)
        drawOval(colors.sand300, sandAt, sandSize)
        drawOval(colors.sand500, sandAt, sandSize, style = round(m.sandEdge))
        val (grassAt, grassSize) = oval(0f)
        drawOval(colors.grass500, grassAt, grassSize)
        drawOval(colors.grass700, grassAt, grassSize, style = round(m.grassEdge))
    }
    drawWaves(island, viewport, colors)
}

/** Still waves in a ring around the island, evenly spaced along the shore, further out than the foam. */
private fun DrawScope.drawWaves(island: Island, viewport: WorldViewport, colors: FaColors) {
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
        drawScenerySprite(ScenerySprite.Wave, viewport.offsetOf(at), width, length, 0f, colors)
    }
}

/** The kinds of scenery that have their own ground drawing above and so are not drawn as a sprite. */
internal val groundPieceKinds: Set<SceneryKind> = setOf(
    SceneryKind.Road, SceneryKind.Track, SceneryKind.Field, SceneryKind.FieldWheat, SceneryKind.FieldGreen,
    SceneryKind.FieldPlough, SceneryKind.Pond, SceneryKind.Waterhole, SceneryKind.Apron, SceneryKind.CarPark,
    SceneryKind.Taxiway,
)
