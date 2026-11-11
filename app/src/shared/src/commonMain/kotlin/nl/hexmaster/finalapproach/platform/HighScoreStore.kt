package nl.hexmaster.finalapproach.platform

/** Where the personal best score of each level is kept, on this device. The host implements it. */
interface HighScoreStore {
    /** The best score stored for [levelId], or 0 when there is none. */
    fun best(levelId: String): Int

    /** Stores [score] as the best for [levelId]. */
    fun save(levelId: String, score: Int)
}

/** A [HighScoreStore] that keeps scores in memory only; used by tests and as a safe default. */
class InMemoryHighScoreStore : HighScoreStore {
    private val scores = mutableMapOf<String, Int>()

    /** How many times [save] was called, so tests can check that a gain is written once. */
    var saveCalls = 0
        private set

    override fun best(levelId: String): Int = scores[levelId] ?: 0

    override fun save(levelId: String, score: Int) {
        saveCalls++
        scores[levelId] = score
    }
}
