package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.FlightPath
import nl.hexmaster.finalapproach.domain.LandingConfig
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.Vec2

/** Builds an aircraft's path from drag input, and cuts it off where it enters a landing zone. */
object PathDrawing {

    /**
     * Starts a fresh path, replacing the old one. An aircraft cleared to land has its clearance
     * cancelled: its locked path is removed and it flies freely again, keeping position and heading.
     * An airplane that has touched down (rolling out) cannot be re-routed.
     */
    fun begin(aircraft: Aircraft): Aircraft =
        when (aircraft.status) {
            AircraftStatus.Flying, is AircraftStatus.ClearedToLand ->
                aircraft.copy(path = FlightPath(), status = AircraftStatus.Flying)
            else -> aircraft
        }

    /**
     * Adds [point] to the path. When the new segment validly enters a zone, the path ends at the
     * entry point and is smoothed for the aircraft's type ([specs], [config]); at a runway its end
     * is then snapped onto the centre line through a [RunwayLeadIn]. The path is locked at [time]
     * and the aircraft is cleared to land. A locked path, or an aircraft no longer flying freely,
     * ignores further points.
     */
    fun extend(
        aircraft: Aircraft,
        point: Vec2,
        zones: List<LandingZone>,
        landing: LandingConfig,
        time: Double,
        specs: EffectiveAircraftSpecs = EffectiveAircraftSpecs.Defaults,
        config: EngineConfig = EngineConfig(),
    ): Aircraft {
        if (aircraft.path.locked || aircraft.status != AircraftStatus.Flying) return aircraft
        val points = aircraft.path.points
        val from = points.lastOrNull() ?: aircraft.position
        val hit = LandingRules.check(from, point, aircraft.type.kind, zones, landing.alignmentTolerance)
            ?: return aircraft.copy(path = aircraft.path.copy(points = points + point))
        val smoothed = smooth(aircraft, points + hit.entryPoint, config)
        val zone = hit.zone
        val final = if (zone is LandingZone.Runway) {
            val spec = specs[aircraft.type]
            RunwayLeadIn.build(
                aircraft.position, aircraft.heading, smoothed, zone,
                spec.speed / spec.turnRate, landing, config.minPointSpacing,
            )
        } else {
            smoothed
        }
        return aircraft.copy(
            path = FlightPath(final, lockedAt = time),
            status = AircraftStatus.ClearedToLand(zone),
        )
    }

    /** Replaces the path of a freely flying aircraft by its smoothed version; a locked path stays. */
    fun finish(aircraft: Aircraft, specs: EffectiveAircraftSpecs, config: EngineConfig): Aircraft {
        if (aircraft.path.locked || aircraft.status != AircraftStatus.Flying) return aircraft
        return aircraft.copy(path = aircraft.path.copy(points = smooth(aircraft, aircraft.path.points, config)))
    }

    /**
     * Smooths [points] from the aircraft. Leading points the aircraft is already level with or past
     * (for example where the finger first touched, behind the nose) are dropped so the path never
     * hooks back; the last point is always kept.
     */
    private fun smooth(aircraft: Aircraft, points: List<Vec2>, config: EngineConfig): List<Vec2> {
        val forward = Angles.direction(aircraft.heading)
        var first = 0
        while (first < points.lastIndex && ((points[first] - aircraft.position) dot forward) <= 0.0) first++
        return PathSmoother.smooth(aircraft.position, points.drop(first), config)
    }
}
