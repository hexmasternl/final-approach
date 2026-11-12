package nl.hexmaster.finalapproach.input

import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.engine.GameEngine

/**
 * Turns one pointer gesture into engine calls, in world units. Touching an aircraft clears its
 * old path at once; a tap leaves it cleared, a drag past the touch slop draws a new one.
 */
class PathGesture(private val engine: GameEngine) {
    private var aircraftId: Int? = null
    private var drawing = false

    /** The pointer went down at [point]. Returns true when it picked an aircraft. */
    fun down(point: Vec2, pickRadius: Double): Boolean {
        val id = engine.select(point, pickRadius)
        aircraftId = id
        drawing = false
        if (id == null) return false
        engine.clearPath(id)
        return true
    }

    /** The pointer moved to [point]; [pastSlop] is true once it has moved beyond the touch slop. */
    fun move(point: Vec2, pastSlop: Boolean) {
        val id = aircraftId ?: return
        if (!drawing && pastSlop) {
            drawing = true
            engine.beginPath(id)
        }
        if (drawing) engine.extendPath(id, point)
    }

    /** The pointer went up or the gesture was cancelled. */
    fun up() {
        val id = aircraftId
        if (id != null && drawing) engine.endPath(id)
        aircraftId = null
        drawing = false
    }
}
