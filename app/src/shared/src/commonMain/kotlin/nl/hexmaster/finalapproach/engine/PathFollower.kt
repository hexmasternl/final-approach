package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.abs
import kotlin.math.max

/**
 * Steers an aircraft along its path: each step it turns towards the next point by at most its
 * type's turn rate, then moves its type's speed along its heading. Reached points are removed,
 * so the path shortens from the aircraft's end. With no points left it flies straight on.
 */
object PathFollower {

    fun step(
        aircraft: Aircraft,
        specs: EffectiveAircraftSpecs,
        deltaSeconds: Double,
        config: EngineConfig = EngineConfig(),
    ): Aircraft {
        val spec = specs[aircraft.type]
        val stepLength = spec.speed * deltaSeconds
        val turningRadius = spec.speed / spec.turnRate
        val intermediateReach = max(config.minReachRadius, config.reachTurnFraction * turningRadius)
        val finalReach = max(config.minReachRadius, stepLength)

        var points = aircraft.path.points
        while (points.isNotEmpty()) {
            val target = points.first()
            val distance = aircraft.position.distanceTo(target)
            val reached = if (points.size == 1) {
                distance <= finalReach ||
                    insideTurningCircle(aircraft.position, aircraft.heading, target, turningRadius)
            } else {
                distance <= intermediateReach
            }
            if (!reached) break
            points = points.drop(1)
        }

        var heading = aircraft.heading
        points.firstOrNull()?.let { target ->
            val wanted = Angles.heading(target - aircraft.position)
            val turn = Angles.difference(heading, wanted)
            val maxTurn = spec.turnRate * deltaSeconds
            heading = Angles.normalize(heading + turn.coerceIn(-maxTurn, maxTurn))
        }
        val position = aircraft.position + Angles.direction(heading) * stepLength
        return aircraft.copy(position = position, heading = heading, path = aircraft.path.copy(points = points))
    }

    /** True when [target] lies inside the circle the aircraft would fly turning hard towards it. */
    private fun insideTurningCircle(position: Vec2, heading: Double, target: Vec2, radius: Double): Boolean {
        val side = Angles.difference(heading, Angles.heading(target - position))
        if (abs(side) < SAME_LINE) return false
        val turnDirection = if (side > 0) 1.0 else -1.0
        val center = position + Angles.direction(heading + turnDirection * HALF_PI) * radius
        return center.distanceTo(target) < radius
    }

    private const val HALF_PI = kotlin.math.PI / 2.0
    private const val SAME_LINE = 1e-9
}