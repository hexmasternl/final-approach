package nl.hexmaster.finalapproach.ui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import nl.hexmaster.finalapproach.ui.theme.FaMotion

/**
 * The presentation between a collision and the game ended dialog: the screen fades to black over
 * `dur-collision-fade`, isolating the two colliding aircraft [aircraftIds] with red rings frozen at
 * [ringAngleDegrees], holds for `dur-collision-hold`, then the dialog may show. Driven by the
 * frame loop through [advance]; the engine is not involved. With [reduceMotion] the blackout is
 * there at once and only the hold remains.
 */
internal class CollisionSequence(
    val aircraftIds: List<Int>,
    val ringAngleDegrees: Float,
    private val reduceMotion: Boolean,
) {
    /** Milliseconds since the collision. Observable, so the overlay redraws as it grows. */
    var elapsedMs: Float by mutableFloatStateOf(0f)
        private set

    fun advance(deltaMs: Float) {
        elapsedMs += deltaMs
    }

    /** Opacity of the blackout: 0 at the collision, 1 at the end of the fade. */
    val blackoutAlpha: Float
        get() = if (reduceMotion) 1f else FaMotion.easeSway.transform((elapsedMs / FaMotion.durCollisionFadeMs).coerceIn(0f, 1f))

    /** True once the hold has ended and the dialog may appear. */
    val dialogVisible: Boolean
        get() = elapsedMs >= if (reduceMotion) FaMotion.durCollisionHoldMs else FaMotion.durCollisionFadeMs + FaMotion.durCollisionHoldMs
}
