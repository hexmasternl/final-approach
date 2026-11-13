package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs

/** Something that happened to aircraft during an update. */
sealed interface GameEvent {
    /** An aircraft touched down safely. */
    data class Landed(val aircraftId: Int, val type: AircraftType) : GameEvent

    /** The path of an aircraft was cut off at [zone]: it is cleared to land. Reported once per clearance. */
    data class ClearedToLand(val aircraftId: Int, val zone: LandingZone) : GameEvent

    /** Two or more aircraft collided. */
    data class Collided(val aircraftIds: Set<Int>) : GameEvent

    /** An aircraft left the area without landing. */
    data class LeftArea(val aircraftId: Int) : GameEvent
}

/** Only landings score: each adds its type's effective landing points. */
object Scoring {
    fun pointsFor(event: GameEvent, specs: EffectiveAircraftSpecs): Int = when (event) {
        is GameEvent.Landed -> specs.pointsOf(event.type)
        is GameEvent.ClearedToLand, is GameEvent.Collided, is GameEvent.LeftArea -> 0
    }

    fun apply(score: Int, events: List<GameEvent>, specs: EffectiveAircraftSpecs): Int =
        score + events.sumOf { pointsFor(it, specs) }
}
