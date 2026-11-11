package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.FlightPath
import nl.hexmaster.finalapproach.domain.LandingConfig
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.Vec2

/** Builds an aircraft's path from drag input, and cuts it off where it enters a landing zone. */
object PathDrawing {

    /** Starts a fresh path, replacing the old one, unless the aircraft is already cleared to land. */
    fun begin(aircraft: Aircraft): Aircraft =
        if (aircraft.status == AircraftStatus.Flying) aircraft.copy(path = FlightPath()) else aircraft

    /**
     * Adds [point] to the path. When the new segment validly enters a zone, the path ends at the
     * entry point, is locked at [time] and the aircraft is cleared to land. A locked path, or an
     * aircraft no longer flying freely, ignores further points.
     */
    fun extend(
        aircraft: Aircraft,
        point: Vec2,
        zones: List<LandingZone>,
        landing: LandingConfig,
        time: Double,
    ): Aircraft {
        if (aircraft.path.locked || aircraft.status != AircraftStatus.Flying) return aircraft
        val points = aircraft.path.points
        val from = points.lastOrNull() ?: aircraft.position
        val hit = LandingRules.check(from, point, aircraft.type.kind, zones, landing.alignmentTolerance)
            ?: return aircraft.copy(path = aircraft.path.copy(points = points + point))
        return aircraft.copy(
            path = FlightPath(points + hit.entryPoint, lockedAt = time),
            status = AircraftStatus.ClearedToLand(hit.zone),
        )
    }
}
