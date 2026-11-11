package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs

/** Moves an aircraft along its path at its type's speed, then straight on in its last heading. */
object PathFollower {

    fun step(aircraft: Aircraft, specs: EffectiveAircraftSpecs, deltaSeconds: Double): Aircraft {
        var remaining = specs.speedOf(aircraft.type) * deltaSeconds
        var position = aircraft.position
        var heading = aircraft.heading
        var points = aircraft.path.points
        while (remaining > 0.0 && points.isNotEmpty()) {
            val target = points.first()
            val toTarget = target - position
            val distance = toTarget.length
            if (distance > 0.0) heading = Angles.heading(toTarget)
            if (distance <= remaining) {
                position = target
                remaining -= distance
                points = points.drop(1)
            } else {
                position += Angles.direction(heading) * remaining
                remaining = 0.0
            }
        }
        if (remaining > 0.0) position += Angles.direction(heading) * remaining
        return aircraft.copy(position = position, heading = heading, path = aircraft.path.copy(points = points))
    }
}
