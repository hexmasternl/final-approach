package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.domain.Bounds
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.OrientedRect
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.ui.sprites.GroundMetrics
import kotlin.math.max
import kotlin.math.min

/** Tunable values of the level tile's miniature, in world units. */
internal object MiniatureDefaults {
    /** Room kept around every runway and helipad, with its fringe, so none touches the edge of the tile. */
    const val MARGIN = 40.0
}

/** The square of the world a level tile shows: its top-left corner and its side, in world units. */
internal class MiniatureCrop(val left: Double, val top: Double, val side: Double) {
    val right: Double get() = left + side
    val bottom: Double get() = top + side

    fun contains(point: Vec2): Boolean = point.x in left..right && point.y in top..bottom
}

/** The corners of the rough-grass fringe of every runway and helipad of [level]: the outermost thing each draws. */
internal fun landingZoneCorners(level: LevelConfig): List<Vec2> {
    val runway = GroundMetrics.Runway
    val runways = level.runways.flatMap {
        val across = it.width / runway.width
        OrientedRect(it.position, it.orientation, it.length + 2.0 * runway.fringe, 2.0 * runway.fringeHalf * across).corners
    }
    val helipads = level.helipads.flatMap {
        val half = GroundMetrics.Helipad.Fringe.half * it.radius / GroundMetrics.Helipad.disc
        listOf(
            Vec2(it.position.x - half, it.position.y - half), Vec2(it.position.x + half, it.position.y - half),
            Vec2(it.position.x - half, it.position.y + half), Vec2(it.position.x + half, it.position.y + half),
        )
    }
    return runways + helipads
}

/**
 * The part of the world the level tile shows: the smallest square around every runway and
 * helipad with its fringe, [MiniatureDefaults.MARGIN] of room around them, kept inside the world.
 * The square is as large as the shortest side of the world at most, so a level whose landing
 * zones spread wider than that shows as much as fits.
 */
internal fun miniatureCrop(level: LevelConfig): MiniatureCrop {
    val corners = landingZoneCorners(level)
    val world = Bounds(0.0, 0.0, level.world.width, level.world.height)
    val shapes = Bounds(corners.minOf { it.x }, corners.minOf { it.y }, corners.maxOf { it.x }, corners.maxOf { it.y })
    val side = min(max(shapes.width, shapes.height) + 2.0 * MiniatureDefaults.MARGIN, min(world.width, world.height))
    val centreX = (shapes.left + shapes.right) / 2.0
    val centreY = (shapes.top + shapes.bottom) / 2.0
    val left = (centreX - side / 2.0).coerceIn(world.left, world.right - side)
    val top = (centreY - side / 2.0).coerceIn(world.top, world.bottom - side)
    return MiniatureCrop(left, top, side)
}
