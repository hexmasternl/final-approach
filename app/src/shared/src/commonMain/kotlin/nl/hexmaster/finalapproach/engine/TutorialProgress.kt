package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.platform.TutorialStore

/**
 * Remembers that the tutorial of a level was completed. Feed it the events of every update; it
 * writes to the store once, when it sees [GameEvent.TutorialCompleted].
 */
class TutorialProgress(private val store: TutorialStore, private val tutorialId: String) {
    private var written = store.isCompleted(tutorialId)

    /** True when the tutorial was completed on this device, now or before. */
    val completed: Boolean get() = written

    fun onEvents(events: List<GameEvent>) {
        if (events.any { it is GameEvent.TutorialCompleted }) markCompleted()
    }

    /** Writes completion now, once. */
    fun markCompleted() {
        if (written) return
        written = true
        store.markCompleted(tutorialId)
    }
}
