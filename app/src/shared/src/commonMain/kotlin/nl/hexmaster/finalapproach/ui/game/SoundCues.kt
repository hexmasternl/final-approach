package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.engine.GameEvent
import nl.hexmaster.finalapproach.platform.SoundEffect

/** Which sound each moment of the game makes. Pure, so it is tested without a screen. */
object SoundCues {

    /** The sounds to play for the [events] of one update, in order: one success per clearance. */
    fun of(events: List<GameEvent>): List<SoundEffect> = events.mapNotNull { event ->
        when (event) {
            is GameEvent.ClearedToLand -> SoundEffect.Success
            is GameEvent.Landed, is GameEvent.Collided, is GameEvent.LeftArea, GameEvent.TutorialCompleted -> null
        }
    }
}
