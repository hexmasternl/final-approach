package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftKind
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.AltitudeState
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.LandingConfig
import nl.hexmaster.finalapproach.domain.LandingZone
import kotlin.math.min

/** One aircraft after a step: null when it has been removed, plus what happened to it. */
data class MotionResult(val aircraft: Aircraft?, val events: List<GameEvent> = emptyList())

/** Moves one aircraft for a step: flight, touchdown, rollout, fade and removal. */
object AircraftMotion {

    fun step(
        aircraft: Aircraft,
        specs: EffectiveAircraftSpecs,
        landing: LandingConfig,
        deltaSeconds: Double,
    ): MotionResult = when (val status = aircraft.status) {
        AircraftStatus.Flying -> MotionResult(PathFollower.step(aircraft, specs, deltaSeconds))
        is AircraftStatus.ClearedToLand -> approach(aircraft, status.zone, specs, deltaSeconds)
        is AircraftStatus.RollingOut -> rollOut(aircraft, status, specs, landing, deltaSeconds)
        is AircraftStatus.Landing -> descend(aircraft, status, landing, deltaSeconds)
    }

    private fun approach(
        aircraft: Aircraft,
        zone: LandingZone,
        specs: EffectiveAircraftSpecs,
        deltaSeconds: Double,
    ): MotionResult {
        val moved = PathFollower.step(aircraft, specs, deltaSeconds)
        if (!moved.path.isEmpty) return MotionResult(moved)
        val touchdown = aircraft.path.points.lastOrNull() ?: moved.position
        val landed = GameEvent.Landed(aircraft.id, aircraft.type)
        if (aircraft.type.kind == AircraftKind.Helicopter || zone !is LandingZone.Runway) {
            return MotionResult(moved.copy(position = touchdown, status = AircraftStatus.Landing()))
        }
        val rolling = moved.copy(
            position = touchdown,
            heading = zone.direction,
            altitude = AltitudeState.Landed,
            status = AircraftStatus.RollingOut(zone.direction, zone.rolloutDistance, zone.runwayId),
        )
        return MotionResult(rolling, listOf(landed))
    }

    /** A helicopter stands on its pad for [LandingConfig.helicopterLandingSeconds], then has landed. */
    private fun descend(
        aircraft: Aircraft,
        status: AircraftStatus.Landing,
        landing: LandingConfig,
        deltaSeconds: Double,
    ): MotionResult {
        val elapsed = status.elapsed + deltaSeconds
        if (elapsed >= landing.helicopterLandingSeconds) {
            return MotionResult(null, listOf(GameEvent.Landed(aircraft.id, aircraft.type)))
        }
        return MotionResult(aircraft.copy(status = status.copy(elapsed = elapsed)))
    }

    private fun rollOut(
        aircraft: Aircraft,
        status: AircraftStatus.RollingOut,
        specs: EffectiveAircraftSpecs,
        landing: LandingConfig,
        deltaSeconds: Double,
    ): MotionResult {
        if (status.remaining > 0.0) {
            val elapsed = status.elapsed + deltaSeconds
            val rolled = rolloutDistance(aircraft, specs, landing, status.elapsed, elapsed)
            val distance = min(rolled, status.remaining)
            return MotionResult(
                aircraft.copy(
                    position = aircraft.position + Angles.direction(status.direction) * distance,
                    status = status.copy(remaining = status.remaining - distance, elapsed = elapsed),
                ),
            )
        }
        val faded = status.fadeElapsed + deltaSeconds
        if (faded >= landing.fadeSeconds) return MotionResult(null)
        return MotionResult(aircraft.copy(status = status.copy(fadeElapsed = faded)))
    }

    /**
     * The distance rolled between [from] and [to] seconds after touchdown. The speed changes
     * linearly from the airplane's flight speed to the landing speed (the flight speed of
     * [LandingConfig.landingSpeedReference] times [LandingConfig.landingSpeedFactor]) over [LandingConfig.rolloutDecelerationSeconds], then
     * stays there. The distance is the exact integral, so it does not depend on the step size.
     */
    private fun rolloutDistance(
        aircraft: Aircraft,
        specs: EffectiveAircraftSpecs,
        landing: LandingConfig,
        from: Double,
        to: Double,
    ): Double {
        val flight = specs[aircraft.type].speed
        val landed = specs[landing.landingSpeedReference].speed * landing.landingSpeedFactor
        val decel = landing.rolloutDecelerationSeconds
        fun travelled(t: Double): Double =
            if (decel <= 0.0) {
                landed * t
            } else if (t <= decel) {
                flight * t + (landed - flight) * t * t / (2.0 * decel)
            } else {
                (flight + landed) * decel / 2.0 + landed * (t - decel)
            }
        return travelled(to) - travelled(from)
    }
}
