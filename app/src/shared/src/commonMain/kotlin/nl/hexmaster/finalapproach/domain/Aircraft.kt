package nl.hexmaster.finalapproach.domain

/**
 * A drawn flight path: the points still ahead of the aircraft, in world units. [lockedAt] is the
 * level time at which a landing zone cut the path off; a locked path takes no more points.
 */
data class FlightPath(
    val points: List<Vec2> = emptyList(),
    val lockedAt: Double? = null,
) {
    val isEmpty: Boolean get() = points.isEmpty()
    val locked: Boolean get() = lockedAt != null
}

/** Whether an aircraft is in the air or on the ground; only aircraft in the same state can collide. */
enum class AltitudeState { InFlight, Landed }

/** Where an aircraft is in its life. */
sealed interface AircraftStatus {
    /** In the air, following its path or flying straight on. */
    data object Flying : AircraftStatus

    /** Its path entered [zone]; it follows the locked path and touches down at its end. */
    data class ClearedToLand(val zone: LandingZone) : AircraftStatus

    /**
     * An airplane on runway [runwayId] after touchdown, rolling along [direction] for [remaining]
     * more world units, then fading out for [fadeElapsed] seconds so far. [elapsed] is the time
     * since touchdown, in seconds.
     */
    data class RollingOut(
        val direction: Double,
        val remaining: Double,
        val runwayId: String,
        val fadeElapsed: Double = 0.0,
        val elapsed: Double = 0.0,
    ) : AircraftStatus
}

/**
 * One aircraft in a level. It holds no speed of its own: speed comes from the run's
 * [EffectiveAircraftSpecs] for its [type], so every aircraft of a type flies alike.
 */
data class Aircraft(
    val id: Int,
    val type: AircraftType,
    val position: Vec2,
    val heading: Double,
    val path: FlightPath = FlightPath(),
    val status: AircraftStatus = AircraftStatus.Flying,
    val livery: Int = 0,
    /** True once the aircraft has been inside the viewport; only then can it leave it. */
    val hasEnteredViewport: Boolean = false,
    val altitude: AltitudeState = AltitudeState.InFlight,
)
