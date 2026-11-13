package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.Vec2

/**
 * Moves an aircraft along its path. Each step it covers its type's speed times the step length as
 * arc length along the remaining points, and its heading is always the direction of the segment it
 * is on, so it stays on the drawn line through every corner. A point it reaches is dropped. With no
 * points left it flies straight on in its last heading.
 */
object PathFollower {

    fun step(
        aircraft: Aircraft,
        specs: EffectiveAircraftSpecs,
        deltaSeconds: Double,
    ): Aircraft {
        var left = specs[aircraft.type].speed * deltaSeconds
        var position = aircraft.position
        var heading = aircraft.heading
        var points = aircraft.path.points
        while (left > 0.0 && points.isNotEmpty()) {
            val to = points.first() - position
            val distance = to.length
            if (distance > SAME_POINT) heading = Angles.heading(to)
            if (distance <= left) {
                position = points.first()
                left -= distance
                points = points.drop(1)
            } else {
                position += Angles.direction(heading) * left
                left = 0.0
            }
        }
        if (left > 0.0) position += Angles.direction(heading) * left
        return aircraft.copy(
            position = position,
            heading = heading,
            path = aircraft.path.copy(points = points),
        )
    }

    /** A point closer than this is the aircraft's own position; its direction is only rounding noise. */
    private const val SAME_POINT = 1e-9
}
