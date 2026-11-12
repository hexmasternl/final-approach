package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.abs

/**
 * Finds aircraft whose collision shapes touched. Only aircraft with the same altitude state can
 * collide: a flying aircraft passes over one that has landed. Two airplanes rolling out on the
 * same runway in the same direction never collide with each other. Every aircraft in the list counts, rolling ones
 * included; aircraft removed after landing are simply not in it. Distance alone is never a
 * collision.
 *
 * Each aircraft is taken to move in a straight line from its position at the start of the step
 * to its position at the end, with its end heading, so two aircraft that pass through each other
 * within one step still collide.
 */
object Collisions {

    /**
     * Pairs of aircraft ids, lower id first, whose shapes touched during the step that ended with
     * [aircraft]. [previous] holds positions at the start of the step; an aircraft missing from
     * it (it just arrived) is tested where it is.
     */
    fun find(
        aircraft: List<Aircraft>,
        specs: EffectiveAircraftSpecs,
        previous: Map<Int, Vec2> = emptyMap(),
    ): List<Pair<Int, Int>> = buildList {
        for (i in aircraft.indices) for (j in i + 1 until aircraft.size) {
            val a = aircraft[i]
            val b = aircraft[j]
            if (a.altitude != b.altitude) continue
            if (rollingTogether(a, b)) continue
            if (touched(a, previous[a.id] ?: a.position, b, previous[b.id] ?: b.position, specs)) {
                add(minOf(a.id, b.id) to maxOf(a.id, b.id))
            }
        }
    }

    /** Rollout directions closer than this, in radians, count as the same direction. */
    const val SAME_DIRECTION_TOLERANCE = 0.1

    /**
     * True when [a] and [b] both roll out (or fade) on the same runway in the same direction:
     * such airplanes never collide with each other.
     */
    fun rollingTogether(a: Aircraft, b: Aircraft): Boolean {
        val rollA = a.status as? AircraftStatus.RollingOut ?: return false
        val rollB = b.status as? AircraftStatus.RollingOut ?: return false
        return rollA.runwayId == rollB.runwayId &&
            abs(Angles.difference(rollA.direction, rollB.direction)) < SAME_DIRECTION_TOLERANCE
    }

    /** True when the shapes of [a] and [b] touch or overlap where they are now. */
    fun overlap(a: Aircraft, b: Aircraft, specs: EffectiveAircraftSpecs): Boolean =
        touched(a, a.position, b, b.position, specs)

    private fun touched(a: Aircraft, aFrom: Vec2, b: Aircraft, bFrom: Vec2, specs: EffectiveAircraftSpecs): Boolean {
        val shapeA = specs[a.type].shape
        val shapeB = specs[b.type].shape
        val start = aFrom - bFrom
        val motion = (a.position - aFrom) - (b.position - bFrom)
        if (closestApproach(start, motion) > shapeA.boundingRadius + shapeB.boundingRadius) return false
        return shapeA.circles.any { circleA ->
            val offsetA = circleA.center.rotated(a.heading)
            shapeB.circles.any { circleB ->
                val offsetB = circleB.center.rotated(b.heading)
                closestApproach(start + offsetA - offsetB, motion) <= circleA.radius + circleB.radius
            }
        }
    }

    /** Smallest length of `start + motion * t` for t from 0 to 1. */
    private fun closestApproach(start: Vec2, motion: Vec2): Double {
        val lengthSquared = motion dot motion
        if (lengthSquared == 0.0) return start.length
        val t = (-(start dot motion) / lengthSquared).coerceIn(0.0, 1.0)
        return (start + motion * t).length
    }
}