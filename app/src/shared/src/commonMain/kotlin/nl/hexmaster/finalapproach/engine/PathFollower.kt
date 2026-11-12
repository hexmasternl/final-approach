package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.abs
import kotlin.math.max

/**
 * Steers an aircraft along its path. Progress along the path only moves forward: a point the
 * aircraft has drawn level with is dropped, so it never turns back to it. Each step the aircraft
 * turns, by at most its type's turn rate, towards the point a lookahead distance further along
 * the path, then moves its type's speed along its heading. With no points left it flies straight on.
 */
object PathFollower {

    fun step(
        aircraft: Aircraft,
        specs: EffectiveAircraftSpecs,
        deltaSeconds: Double,
        config: EngineConfig = EngineConfig(),
    ): Aircraft {
        val spec = specs[aircraft.type]
        val pose = advance(
            Pose(aircraft.position, aircraft.heading, aircraft.path.points),
            spec.speed,
            spec.turnRate,
            deltaSeconds,
            config,
        )
        return aircraft.copy(
            position = pose.position,
            heading = pose.heading,
            path = aircraft.path.copy(points = pose.points),
        )
    }

    /** Where an aircraft is, where it points, and the path still ahead of it. */
    internal data class Pose(val position: Vec2, val heading: Double, val points: List<Vec2>)

    /** One step of following, for any [speed] and [turnRate]. */
    internal fun advance(
        pose: Pose,
        speed: Double,
        turnRate: Double,
        deltaSeconds: Double,
        config: EngineConfig,
    ): Pose {
        val stepLength = speed * deltaSeconds
        val turningRadius = speed / turnRate
        val points = progress(pose, turningRadius, stepLength, config)
        var heading = pose.heading
        target(pose.position, points, config.smoothing.lookaheadTurnFraction * turningRadius)?.let { target ->
            val turn = Angles.difference(heading, Angles.heading(target - pose.position))
            val maxTurn = turnRate * deltaSeconds
            heading = Angles.normalize(heading + turn.coerceIn(-maxTurn, maxTurn))
        }
        val position = pose.position + Angles.direction(heading) * stepLength
        return Pose(position, heading, points)
    }

    /** Drops the points the aircraft has passed or reached; progress never goes backwards. */
    private fun progress(pose: Pose, turningRadius: Double, stepLength: Double, config: EngineConfig): List<Vec2> {
        val finalReach = max(config.minReachRadius, stepLength)
        var points = pose.points
        while (points.isNotEmpty()) {
            val first = points.first()
            val distance = pose.position.distanceTo(first)
            val passed = if (points.size == 1) {
                distance <= finalReach ||
                    insideTurningCircle(pose.position, pose.heading, first, turningRadius)
            } else {
                val along = alongSegment(first, points[1], pose.position)
                distance <= config.minReachRadius || along >= 1.0 ||
                    (along >= 0.0 && distance <= first.distanceTo(points[1]))
            }
            if (!passed) break
            points = points.drop(1)
        }
        return points
    }

    /** The point [lookahead] further along the path than the aircraft's place on it. */
    private fun target(position: Vec2, points: List<Vec2>, lookahead: Double): Vec2? {
        if (points.size < 2) return points.firstOrNull()
        val first = points[0]
        val along = alongSegment(first, points[1], position).coerceIn(0.0, 1.0)
        var from = first + (points[1] - first) * along
        var left = lookahead
        for (next in points.drop(1)) {
            val length = from.distanceTo(next)
            if (length >= left) return from + (next - from) * (left / length)
            left -= length
            from = next
        }
        return points.last()
    }

    /** Where [point] projects onto the segment [from]–[to], as a fraction of it (unclamped). */
    private fun alongSegment(from: Vec2, to: Vec2, point: Vec2): Double {
        val d = to - from
        val lengthSquared = d dot d
        if (lengthSquared == 0.0) return 1.0
        return ((point - from) dot d) / lengthSquared
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
