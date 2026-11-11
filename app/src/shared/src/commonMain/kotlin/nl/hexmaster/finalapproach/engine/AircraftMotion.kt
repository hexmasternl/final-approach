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
            return MotionResult(null, listOf(landed))
        }
        val rolling = moved.copy(
            position = touchdown,
            heading = zone.direction,
            altitude = AltitudeState.Landed,
            status = AircraftStatus.RollingOut(zone.direction, zone.rolloutDistance),
        )
        return MotionResult(rolling, listOf(landed))
    }

    private fun rollOut(
        aircraft: Aircraft,
        status: AircraftStatus.RollingOut,
        specs: EffectiveAircraftSpecs,
        landing: LandingConfig,
        deltaSeconds: Double,
    ): MotionResult {
        if (status.remaining > 0.0) {
            val spec = specs[aircraft.type]
            val distance = min(spec.speed * spec.rolloutSpeedFraction * deltaSeconds, status.remaining)
            return MotionResult(
                aircraft.copy(
                    position = aircraft.position + Angles.direction(status.direction) * distance,
                    status = status.copy(remaining = status.remaining - distance),
                ),
            )
        }
        val faded = status.fadeElapsed + deltaSeconds
        if (faded >= landing.fadeSeconds) return MotionResult(null)
        return MotionResult(aircraft.copy(status = status.copy(fadeElapsed = faded)))
    }
}
