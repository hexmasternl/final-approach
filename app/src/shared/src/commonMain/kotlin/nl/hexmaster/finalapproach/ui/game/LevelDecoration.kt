package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.domain.Circle
import nl.hexmaster.finalapproach.domain.Island
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.LandingZones
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.LevelDefaults
import nl.hexmaster.finalapproach.domain.LevelScenery
import nl.hexmaster.finalapproach.domain.OrientedRect
import nl.hexmaster.finalapproach.domain.Polygons
import nl.hexmaster.finalapproach.domain.SceneryItem
import nl.hexmaster.finalapproach.domain.SceneryKind
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.ui.sprites.GroundMetrics
import nl.hexmaster.finalapproach.ui.sprites.ScenerySprite
import nl.hexmaster.finalapproach.ui.sprites.SceneryDetail
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

/**
 * The decoration of a level's open ground (`Ground.md`, W8): irregular patches of tone, clusters
 * of tufts, flowers and reeds, and the trees of a wood. It lies outside the airfield and off
 * roads, fields, water, runways, helipads, landing zones and the level's own scenery.
 *
 * It is a pure function of the level and its [LevelConfig.decorationSeed], with its own random
 * numbers: never the engine's, so it can not change a run.
 */
internal data class LevelDecoration(
    val patches: List<DecorationPatch>,
    val details: List<DecorationDetail>,
    val trees: List<DecorationTree>,
)

/** What a patch of tone is made of. */
internal enum class PatchTone { Light, Dark, Sea }

/** An irregular patch of tone: the corners of its outline, to be drawn as a smooth closed curve. */
internal data class DecorationPatch(val tone: PatchTone, val outline: List<Vec2>)

/** One tuft, flower or reed. */
internal data class DecorationDetail(val detail: SceneryDetail, val position: Vec2)

/** One tree, bush, palm or rock of a wood, scaled by [scale] from its sprite's natural size. */
internal data class DecorationTree(val sprite: ScenerySprite, val position: Vec2, val scale: Float) {
    /** Half the size of the sprite on the ground, in world units. */
    val radius: Double get() = max(sprite.width, sprite.height) * scale / 2.0
}

/** A wood: [count] of [sprites] inside a region of the world given as fractions of its size, at least [gap] apart. */
internal class Wood(
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double,
    val sprites: List<ScenerySprite>,
    val count: Int,
    val gap: Double,
)

/** How much of each kind of decoration a landscape has. Tunable values, in world units. */
internal class DecorationPlan(
    val patches: Int,
    val seaPatches: Int,
    val clusters: Int,
    val clusterDetails: List<SceneryDetail>,
    val reedsPerWater: Int,
    val woods: List<Wood>,
)

/** The numbers of the decoration of each landscape; they follow the showcase scenes. */
internal object DecorationDefaults {
    private val meadowClusters = listOf(
        SceneryDetail.Flower, SceneryDetail.Tuft, SceneryDetail.TuftLight, SceneryDetail.Tuft,
        SceneryDetail.FlowerPink, SceneryDetail.Tuft, SceneryDetail.TuftLight, SceneryDetail.Tuft,
    )

    /** Dry ground has tufts and no flowers. */
    private val savannaClusters = listOf(SceneryDetail.Tuft, SceneryDetail.TuftLight, SceneryDetail.Tuft)

    private val meadow = DecorationPlan(
        patches = 26,
        seaPatches = 0,
        clusters = 46,
        clusterDetails = meadowClusters,
        reedsPerWater = 6,
        woods = listOf(
            Wood(0.0, 0.14, 0.07, 0.37, listOf(ScenerySprite.Pine, ScenerySprite.PineSmall, ScenerySprite.Pine), 7, 50.0),
            Wood(0.76, 0.14, 0.995, 0.28, listOf(ScenerySprite.Tree, ScenerySprite.TreeSmall, ScenerySprite.TreeOak, ScenerySprite.Pine), 10, 55.0),
            Wood(0.87, 0.30, 0.995, 0.42, listOf(ScenerySprite.Tree, ScenerySprite.TreeSmall), 5, 55.0),
            Wood(0.73, 0.95, 0.995, 0.995, listOf(ScenerySprite.Tree, ScenerySprite.PineSmall, ScenerySprite.TreeSmall), 5, 55.0),
            Wood(0.95, 0.65, 0.995, 0.93, listOf(ScenerySprite.TreeSmall, ScenerySprite.Pine), 5, 55.0),
        ),
    )

    private val island = DecorationPlan(
        patches = 0,
        seaPatches = 14,
        clusters = 24,
        clusterDetails = meadowClusters,
        reedsPerWater = 0,
        woods = listOf(Wood(0.18, 0.21, 0.82, 0.83, listOf(ScenerySprite.Palm, ScenerySprite.Palm, ScenerySprite.Bush), 14, 58.0)),
    )

    private val savanna = DecorationPlan(
        patches = 30,
        seaPatches = 0,
        clusters = 40,
        clusterDetails = savannaClusters,
        reedsPerWater = 6,
        woods = listOf(Wood(0.0, 0.14, 1.0, 1.0, listOf(ScenerySprite.Bush, ScenerySprite.Bush, ScenerySprite.Rock), 12, 120.0)),
    )

    fun of(landscape: LevelScenery): DecorationPlan = when (landscape) {
        LevelScenery.Meadow -> meadow
        LevelScenery.Island -> island
        LevelScenery.Savanna -> savanna
    }

    /** How many tries a cluster or a tree gets before it is left out, per piece asked for. */
    const val TRIES_PER_PIECE = 14

    /** Room kept between decoration and what it must stay off, in world units. */
    const val ROOM = 6.0

    /** The airfield counts as this much larger than its outline when keeping decoration out, and patches as this much. */
    const val AIRFIELD_GROWTH = 1.08
    const val AIRFIELD_PATCH_GROWTH = 1.2

    /** Room a cluster's pieces keep from the island's shore, and sea patches from the island's shallows. */
    const val SHORE_ROOM = 30.0

    /** Reeds stand this far beyond the shore of the water they belong to. */
    const val REED_OUT = 16.0

    /** A tree keeps this fraction of its radius clear of other things; the rest of its crown may overhang. */
    const val STANDING_ROOM_FACTOR = 0.5

    /** The scale a tree may be drawn at, as in the showcase. */
    const val TREE_SCALE_MIN = 0.85f
    const val TREE_SCALE_SPAN = 0.35f
}

/** The decoration of [level]: always the same for the same level. */
internal fun levelDecoration(level: LevelConfig): LevelDecoration {
    val random = Random(level.decorationSeed)
    val ground = OpenGround(level)
    val plan = DecorationDefaults.of(level.landscape)
    val patches = buildList {
        addAll(landPatches(level, plan.patches, random))
        addAll(seaPatches(level, plan.seaPatches, random))
    }
    val details = buildList {
        addAll(clusters(level, plan, ground, random))
        addAll(reeds(level, plan, ground, random))
    }
    return LevelDecoration(patches, details, woods(level, plan, ground, random))
}

private val metricsPatch = GroundMetrics.Ground.Patch

/** Irregular patches of tone around [cx], [cy]: the design system's `organic` outline. */
private fun organic(cx: Double, cy: Double, rx: Double, ry: Double, random: Random): List<Vec2> =
    List(metricsPatch.lobes.toInt()) { i ->
        val angle = i.toDouble() / metricsPatch.lobes * 2.0 * PI
        val wobble = 1.0 + (random.nextDouble() - 0.5) * 2.0 * metricsPatch.jitter
        Vec2(cx + cos(angle) * rx * wobble, cy + sin(angle) * ry * wobble)
    }

private fun Random.between(range: List<Float>): Double = range[0] + nextDouble() * (range[1] - range[0])

private fun landPatches(level: LevelConfig, count: Int, random: Random): List<DecorationPatch> = buildList {
    val grown = grown(level.airfield, DecorationDefaults.AIRFIELD_PATCH_GROWTH)
    for (i in 0 until count) {
        val x = random.nextDouble() * level.world.width
        val y = random.nextDouble() * level.world.height
        val outline = organic(x, y, random.between(metricsPatch.rx), random.between(metricsPatch.ry), random)
        val clear = !Polygons.contains(grown, Vec2(x, y)) && outline.none { Polygons.contains(level.airfield, it) }
        // The wobble above is drawn either way, so the patches that are kept do not depend on the ones that are not.
        if (clear) add(DecorationPatch(if (i % 3 == 0) PatchTone.Light else PatchTone.Dark, outline))
    }
}

private fun seaPatches(level: LevelConfig, count: Int, random: Random): List<DecorationPatch> = buildList {
    val island = level.island ?: return@buildList
    val island0 = GroundMetrics.Ground.Island
    val reach = island0.beach + island0.shallows + island0.foamOut
    for (i in 0 until count) {
        val x = random.nextDouble() * level.world.width
        val y = random.nextDouble() * level.world.height
        val outline = organic(x, y, random.between(metricsPatch.seaRx), random.between(metricsPatch.seaRy), random)
        if ((outline + Vec2(x, y)).none { island.contains(it, margin = -reach.toDouble()) }) add(DecorationPatch(PatchTone.Sea, outline))
    }
}

/** Clusters of tufts and flowers on open ground. */
private fun clusters(level: LevelConfig, plan: DecorationPlan, ground: OpenGround, random: Random): List<DecorationDetail> = buildList {
    val m = GroundMetrics.Ground.Cluster
    var placed = 0
    var tries = 0
    while (placed < plan.clusters && tries < plan.clusters * DecorationDefaults.TRIES_PER_PIECE) {
        tries++
        val at = Vec2(random.nextDouble() * level.world.width, random.nextDouble() * level.world.height)
        if (!ground.freeForDetail(at)) continue
        val detail = plan.clusterDetails[placed % plan.clusterDetails.size]
        val pieces = if (detail == SceneryDetail.Flower || detail == SceneryDetail.FlowerPink) m.flower.toInt() else m.tuft.toInt()
        repeat(pieces) {
            val piece = Vec2(at.x + (random.nextDouble() * 2.0 - 1.0) * m.spreadX, at.y + (random.nextDouble() * 2.0 - 1.0) * m.spreadY)
            if (ground.freeForDetail(piece)) add(DecorationDetail(detail, piece))
        }
        placed++
    }
}

/** Reeds in small groups around each pond and waterhole, a little beyond their shore. */
private fun reeds(level: LevelConfig, plan: DecorationPlan, ground: OpenGround, random: Random): List<DecorationDetail> = buildList {
    if (plan.reedsPerWater == 0) return@buildList
    val m = GroundMetrics.Ground.Cluster
    level.scenery.filter { it.kind == SceneryKind.Pond || it.kind == SceneryKind.Waterhole }.forEach { water ->
        repeat(plan.reedsPerWater) {
            val angle = random.nextDouble() * 2.0 * PI
            val at = Vec2(
                water.position.x + cos(angle) * (water.width / 2.0 + DecorationDefaults.REED_OUT),
                water.position.y + sin(angle) * (water.height / 2.0 + DecorationDefaults.REED_OUT),
            )
            repeat(REEDS_PER_GROUP) {
                val piece = Vec2(at.x + (random.nextDouble() * 2.0 - 1.0) * m.spreadX / 2.0, at.y + (random.nextDouble() * 2.0 - 1.0) * m.spreadY / 2.0)
                if (ground.freeForDetail(piece, atWater = true)) add(DecorationDetail(SceneryDetail.Reed, piece))
            }
        }
    }
}

/** Reeds in a group by the water. */
private const val REEDS_PER_GROUP = 2

/** The trees of each wood: spaced apart, never in the HUD band and never on a blocked spot. */
private fun woods(level: LevelConfig, plan: DecorationPlan, ground: OpenGround, random: Random): List<DecorationTree> = buildList {
    val width = level.world.width
    val height = level.world.height
    plan.woods.forEach { wood ->
        var placed = 0
        var tries = 0
        val here = mutableListOf<DecorationTree>()
        while (placed < wood.count && tries < wood.count * DecorationDefaults.TRIES_PER_PIECE) {
            tries++
            val at = Vec2(
                (wood.left + random.nextDouble() * (wood.right - wood.left)) * width,
                (wood.top + random.nextDouble() * (wood.bottom - wood.top)) * height,
            )
            val tree = DecorationTree(
                wood.sprites[random.nextInt(wood.sprites.size)],
                at,
                DecorationDefaults.TREE_SCALE_MIN + random.nextFloat() * DecorationDefaults.TREE_SCALE_SPAN,
            )
            val apart = (here + this@buildList).all { other -> other.position.distanceTo(at) >= max(wood.gap, tree.radius + other.radius) }
            if (apart && ground.freeForStanding(at, tree.radius)) {
                here += tree
                placed++
            }
        }
        addAll(here)
    }
}

/** The corners of [outline] moved away from their centre by the factor [by]. */
internal fun grown(outline: List<Vec2>, by: Double): List<Vec2> {
    if (outline.isEmpty()) return outline
    val centre = Vec2(outline.sumOf { it.x } / outline.size, outline.sumOf { it.y } / outline.size)
    return outline.map { centre + (it - centre) * by }
}

/** How far [point] lies outside [rect]: zero on or inside it. */
internal fun distanceOutside(rect: OrientedRect, point: Vec2): Double {
    val local = rect.toLocal(point)
    return hypot(max(abs(local.x) - rect.length / 2.0, 0.0), max(abs(local.y) - rect.width / 2.0, 0.0))
}

/** What decoration has to stay off in one level, asked for a piece at a time. */
private class OpenGround(private val level: LevelConfig) {
    private val airfield = level.airfield
    private val airfieldGrown = grown(level.airfield, DecorationDefaults.AIRFIELD_GROWTH)
    private val hud = LevelDefaults.HUD_BAND
    private val zones = LandingZones.of(level)

    /** True when something that lies flat (a tuft, a flower, a reed) may lie at [point]. */
    fun freeForDetail(point: Vec2, atWater: Boolean = false): Boolean = free(point, 0.0, atWater)

    /** True when something with [radius] that stands may stand at [point]: also clear of the HUD band. */
    fun freeForStanding(point: Vec2, radius: Double): Boolean = point.y - radius >= hud && free(point, radius * DecorationDefaults.STANDING_ROOM_FACTOR, atWater = false)

    private fun free(point: Vec2, radius: Double, atWater: Boolean): Boolean {
        if (point.x < 0.0 || point.y < 0.0 || point.x > level.world.width || point.y > level.world.height) return false
        if (Polygons.contains(airfield, point) || Polygons.contains(airfieldGrown, point)) return false
        level.island?.let { island -> if (!onIslandGrass(island, point, radius)) return false }
        val room = DecorationDefaults.ROOM + radius
        if (level.runways.any { distanceOutside(it.shape, point) < room + GroundMetrics.Runway.fringe }) return false
        if (level.helipads.any { point.distanceTo(it.position) < it.radius * GroundMetrics.Helipad.Fringe.half / GroundMetrics.Helipad.disc + room }) return false
        if (zones.any { zone -> insideZone(zone, point, room) }) return false
        level.paddock?.let { if (it.expandedBy(room).contains(point)) return false }
        return level.scenery.none { item -> blocks(item, point, room, atWater) }
    }

    private fun onIslandGrass(island: Island, point: Vec2, radius: Double): Boolean =
        // Flat details keep off the shore; a palm may stand right at the beach.
        island.contains(point, margin = if (radius == 0.0) DecorationDefaults.SHORE_ROOM else radius)

    private fun insideZone(zone: LandingZone, point: Vec2, room: Double): Boolean = when (zone) {
        is LandingZone.Runway -> distanceOutside(zone.area, point) < room
        is LandingZone.Helipad -> zone.area.let { circle: Circle -> point.distanceTo(circle.center) < circle.radius + room }
    }

    /** True when [item] keeps [point] away: ground pieces by the band around them, everything else by a little room. */
    private fun blocks(item: SceneryItem, point: Vec2, room: Double, atWater: Boolean): Boolean {
        val band = when (item.kind) {
            SceneryKind.Road -> GroundMetrics.Ground.Road.clearance - GroundMetrics.Ground.Road.verge / 2f
            SceneryKind.Track -> GroundMetrics.Ground.Track.clearance
            SceneryKind.FieldWheat, SceneryKind.FieldGreen, SceneryKind.FieldPlough -> GroundMetrics.Ground.Field.margin * 2f
            SceneryKind.Pond, SceneryKind.Waterhole -> GroundMetrics.Ground.Pond.shore / 2f
            SceneryKind.Apron, SceneryKind.CarPark, SceneryKind.Taxiway -> GroundMetrics.Ground.Apron.margin
            else -> 0f
        }
        val onWater = item.kind == SceneryKind.Pond || item.kind == SceneryKind.Waterhole
        if (onWater) {
            // Water is an oval: reeds stand at its shore, so keep off the oval and its bands.
            val local = item.shape.toLocal(point)
            // Reeds stand at the shore: they only keep off the open water itself.
            val reach = if (atWater) 0.0 else band + room
            val rx = item.width / 2.0 + reach
            val ry = item.height / 2.0 + reach
            val dx = local.y / rx
            val dy = local.x / ry
            return dx * dx + dy * dy < 1.0
        }
        return distanceOutside(item.shape, point) < band + room
    }
}
