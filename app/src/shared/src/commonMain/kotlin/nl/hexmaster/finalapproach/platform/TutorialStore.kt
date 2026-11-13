package nl.hexmaster.finalapproach.platform

/** Where it is kept, on this device, that the first-time tutorial of a level was completed. The host implements it. */
interface TutorialStore {
    /** True when the tutorial [tutorialId] (the level id) was completed or skipped. */
    fun isCompleted(tutorialId: String): Boolean

    /** Records that the tutorial [tutorialId] was completed. */
    fun markCompleted(tutorialId: String)
}

/** A [TutorialStore] that keeps completion in memory only; used by tests and as a safe default. */
class InMemoryTutorialStore : TutorialStore {
    private val completed = mutableSetOf<String>()

    /** How many times [markCompleted] was called. */
    var markCalls = 0
        private set

    override fun isCompleted(tutorialId: String): Boolean = tutorialId in completed

    override fun markCompleted(tutorialId: String) {
        markCalls++
        completed += tutorialId
    }
}
