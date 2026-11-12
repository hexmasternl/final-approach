package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.LandingConfig
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.ceil
import kotlin.math.max

/**
 * Snaps the end of a path that entered a runway zone onto the runway centre line. The path keeps
 * its drawn part up to a join point J, [LandingConfig.leadInTurningRadii] turning radii back from
 * where it entered the zone, then follows a cubic Bezier lead-in from J to the touchdown point T on
 * the centre line, leaving J along the path's own direction and arriving at T in the landing
 * direction.
 */
object RunwayLeadIn {

    /**
     * The snapped path. [path] is the drawn path, already smoothed, ending at the zone entry
     * point; the aircraft is at [start] heading [startHeading]. When [path] is shorter than the
     * lead-in, the lead-in starts at the aircraft. Points are spaced about [spacing] apart.
     */
    fun build(
        start: Vec2,
        startHeading: Double,
        path: List<Vec2>,
        zone: LandingZone.Runway,
        turningRadius: Double,
        landing: LandingConfig,
        spacing: Double,
    ): List<Vec2> {
        val entry = path.lastOrNull() ?: start
        val touchdown = zone.centreLinePoint(max(landing.touchdownDistance, zone.distanceAlong(entry)))
        val join = joinPoint(start, startHeading, path, turningRadius * landing.leadInTurningRadii)
        val along = zone.along
        // The curve ends one spacing short of T, so the last segment lies exactly on the centre line.
        val end = touchdown - along * spacing
        val chord = join.point.distanceTo(end)
        val handle = chord * landing.leadInHandleFraction
        val c1 = join.point + Angles.direction(join.heading) * handle
        val c2 = end - along * handle
        val estimate = chord + 2.0 * handle
        val count = max(1, ceil(estimate / spacing).toInt())
        val curve = (1..count).map { i -> bezier(join.point, c1, c2, end, i.toDouble() / count) }
        return join.kept + curve + touchdown
    }

    private class Join(val kept: List<Vec2>, val point: Vec2, val heading: Double)

    /** Walks [length] back from the end of [path]; the part before that point is kept. */
    private fun joinPoint(start: Vec2, startHeading: Double, path: List<Vec2>, length: Double): Join {
        val full = listOf(start) + path
        var remaining = length
        for (i in full.size - 1 downTo 1) {
            val a = full[i - 1]
            val b = full[i]
            val segment = a.distanceTo(b)
            if (segment >= remaining && segment > 0.0) {
                val point = b + (a - b) * (remaining / segment)
                val before = full.subList(1, i)
                val kept = if ((before.lastOrNull()?.distanceTo(point) ?: 1.0) < 1e-9) before else before + point
                return Join(kept, point, Angles.heading(b - a))
            }
            remaining -= segment
        }
        return Join(emptyList(), start, startHeading)
    }

    private fun bezier(p0: Vec2, p1: Vec2, p2: Vec2, p3: Vec2, t: Double): Vec2 {
        val u = 1.0 - t
        return p0 * (u * u * u) + p1 * (3.0 * u * u * t) + p2 * (3.0 * u * t * t) + p3 * (t * t * t)
    }
}
