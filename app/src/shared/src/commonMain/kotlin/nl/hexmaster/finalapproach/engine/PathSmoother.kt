package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Overlap
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max

/**
 * Turns a drawn polyline into one an aircraft can fly. Finger jitter is removed, the line is
 * resampled evenly and its corners are cut. Then a virtual aircraft of the same speed, but able
 * to turn only as tight as the minimum radius, follows that line from the real aircraft's pose;
 * the track it flies is the smoothed path. So no corner is tighter than the minimum radius, and
 * the real aircraft, which can turn a little tighter, can always fly it. The last point stays
 * exactly where it was drawn.
 */
object PathSmoother {

    /**
     * Smooths [points] for an aircraft at [start] with [heading], flying at [speed] with
     * [turnRate]. Returns the new points ahead of the aircraft; [start] itself is not included.
     */
    fun smooth(
        start: Vec2,
        heading: Double,
        points: List<Vec2>,
        speed: Double,
        turnRate: Double,
        config: EngineConfig = EngineConfig(),
    ): List<Vec2> {
        if (points.isEmpty() || speed <= 0.0 || turnRate <= 0.0) return points
        val smoothing = config.smoothing
        val turningRadius = speed / turnRate
        val spacing = max(config.minPointSpacing, turningRadius * smoothing.spacingTurnFraction)
        var line = resample(simplify(listOf(start) + points, smoothing.tolerance), spacing)
        repeat(smoothing.chaikinIterations) { line = chaikin(line) }
        return fly(start, heading, line.drop(1), speed, turnRate / smoothing.minRadiusFactor, spacing, config)
    }

    /** Radius of the circle through [a], [b] and [c]; infinite for points on one line. */
    fun circumradius(a: Vec2, b: Vec2, c: Vec2): Double {
        val cross = abs((b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x))
        if (cross < COLLINEAR) return Double.POSITIVE_INFINITY
        return a.distanceTo(b) * b.distanceTo(c) * c.distanceTo(a) / (2.0 * cross)
    }

    /** The track of a virtual aircraft following [line], sampled every [spacing]. */
    private fun fly(
        start: Vec2,
        heading: Double,
        line: List<Vec2>,
        speed: Double,
        turnRate: Double,
        spacing: Double,
        config: EngineConfig,
    ): List<Vec2> {
        val end = line.last()
        val stepSeconds = spacing / config.smoothing.flightSubsteps / speed
        val stepLength = speed * stepSeconds
        val drawnLength = (listOf(start) + line).zipWithNext().sumOf { (a, b) -> a.distanceTo(b) }
        val circle = 2.0 * PI * speed / turnRate
        val maxSteps = ceil(config.smoothing.flightLengthFactor * (drawnLength + circle) / stepLength).toInt()
        val track = mutableListOf<Vec2>()
        var pose = PathFollower.Pose(start, heading, line)
        var sinceSample = 0.0
        var steps = 0
        while (pose.points.isNotEmpty() && steps < maxSteps) {
            pose = PathFollower.advance(pose, speed, turnRate, stepSeconds, config)
            sinceSample += stepLength
            if (sinceSample >= spacing && pose.points.isNotEmpty()) {
                track += pose.position
                sinceSample = 0.0
            }
            steps++
        }
        while (track.isNotEmpty() && track.last().distanceTo(end) < spacing) track.removeAt(track.lastIndex)
        return track + end
    }

    /** Douglas-Peucker: drops points within [tolerance] of the line between kept points. */
    private fun simplify(points: List<Vec2>, tolerance: Double): List<Vec2> {
        if (points.size < 3) return points
        val keep = BooleanArray(points.size)
        keep[0] = true
        keep[points.lastIndex] = true
        val ranges = ArrayDeque(listOf(0 to points.lastIndex))
        while (ranges.isNotEmpty()) {
            val (from, to) = ranges.removeLast()
            var farthest = -1
            var farthestDistance = tolerance
            for (i in from + 1 until to) {
                val d = Overlap.closestPointOnSegment(points[from], points[to], points[i]).distanceTo(points[i])
                if (d > farthestDistance) {
                    farthestDistance = d
                    farthest = i
                }
            }
            if (farthest >= 0) {
                keep[farthest] = true
                ranges.addLast(from to farthest)
                ranges.addLast(farthest to to)
            }
        }
        return points.filterIndexed { i, _ -> keep[i] }
    }

    /** Points every [spacing] (or a little less) along the line; both ends are kept exactly. */
    private fun resample(points: List<Vec2>, spacing: Double): List<Vec2> {
        val lengths = points.zipWithNext().map { (a, b) -> a.distanceTo(b) }
        val total = lengths.sum()
        val count = ceil(total / spacing).toInt()
        if (count < 2) return listOf(points.first(), points.last())
        val step = total / count
        val result = mutableListOf(points.first())
        var segment = 0
        var segmentStart = 0.0
        for (k in 1 until count) {
            val at = k * step
            while (segment < lengths.lastIndex && segmentStart + lengths[segment] < at) {
                segmentStart += lengths[segment]
                segment++
            }
            val length = lengths[segment]
            val t = if (length == 0.0) 0.0 else ((at - segmentStart) / length).coerceIn(0.0, 1.0)
            result += points[segment] + (points[segment + 1] - points[segment]) * t
        }
        result += points.last()
        return result
    }

    /** One corner-cutting pass that keeps both end points. */
    private fun chaikin(points: List<Vec2>): List<Vec2> {
        if (points.size < 3) return points
        val result = mutableListOf(points.first())
        points.zipWithNext().forEachIndexed { i, (a, b) ->
            if (i > 0) result += a * NEAR + b * FAR
            if (i < points.lastIndex - 1) result += a * FAR + b * NEAR
        }
        result += points.last()
        return result
    }

    private const val NEAR = 0.75
    private const val FAR = 0.25
    private const val COLLINEAR = 1e-9
}
