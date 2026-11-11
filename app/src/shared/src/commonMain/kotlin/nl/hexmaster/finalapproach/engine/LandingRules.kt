package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.AircraftKind
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.Circle
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.OrientedRect
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/** A path segment entered [zone] at [entryPoint] and clears the aircraft to land there. */
data class LandingHit(val zone: LandingZone, val entryPoint: Vec2)

/**
 * Decides whether a newly drawn path segment clears an aircraft to land. The whole segment is
 * tested, so a fast drag that jumps over a small zone is still caught.
 */
object LandingRules {

    /**
     * The first zone the segment from [from] to [to] validly enters, or null. A zone only
     * accepts its own [kind]; a runway zone also needs the segment's direction within
     * [alignmentTolerance] radians of its landing direction.
     */
    fun check(
        from: Vec2,
        to: Vec2,
        kind: AircraftKind,
        zones: List<LandingZone>,
        alignmentTolerance: Double,
    ): LandingHit? {
        val segment = to - from
        if (segment.length == 0.0) return null
        val heading = Angles.heading(segment)
        return zones
            .filter { it.accepts == kind }
            .filter { it !is LandingZone.Runway || abs(Angles.difference(it.direction, heading)) <= alignmentTolerance }
            .mapNotNull { zone -> entryFraction(from, to, zone)?.let { zone to it } }
            .minByOrNull { it.second }
            ?.let { (zone, t) -> LandingHit(zone, from + segment * t) }
    }

    /** Where along the segment (0 to 1) it first is inside [zone], or null when it never is. */
    fun entryFraction(from: Vec2, to: Vec2, zone: LandingZone): Double? = when (zone) {
        is LandingZone.Helipad -> circleEntry(from, to, zone.area)
        is LandingZone.Runway -> rectEntry(from, to, zone.area)
    }

    private fun circleEntry(from: Vec2, to: Vec2, circle: Circle): Double? {
        val offset = from - circle.center
        val c = (offset dot offset) - circle.radius * circle.radius
        if (c <= 0.0) return 0.0
        val d = to - from
        val a = d dot d
        val b = 2.0 * (offset dot d)
        val discriminant = b * b - 4.0 * a * c
        if (discriminant < 0.0) return null
        val t = (-b - sqrt(discriminant)) / (2.0 * a)
        return t.takeIf { it in 0.0..1.0 }
    }

    private fun rectEntry(from: Vec2, to: Vec2, rect: OrientedRect): Double? {
        val p = rect.toLocal(from)
        val q = rect.toLocal(to)
        var enter = 0.0
        var exit = 1.0
        val halfLength = rect.length / 2.0
        val halfWidth = rect.width / 2.0
        for ((start, delta, half) in listOf(Triple(p.x, q.x - p.x, halfLength), Triple(p.y, q.y - p.y, halfWidth))) {
            if (delta == 0.0) {
                if (abs(start) > half) return null
            } else {
                val t1 = (-half - start) / delta
                val t2 = (half - start) / delta
                enter = max(enter, min(t1, t2))
                exit = min(exit, max(t1, t2))
                if (enter > exit) return null
            }
        }
        return enter
    }
}
