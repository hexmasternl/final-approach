package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.platform.HighScoreStore

/**
 * The personal best of one level. The best shown is the larger of the stored best and the
 * current score, and a score that beats the stored best is saved at once, so a restart, a quit
 * or a killed app all keep it.
 */
class BestScore(private val store: HighScoreStore, private val levelId: String) {
    private var stored = store.best(levelId)

    /** The best score to show for a run that is at [score]. */
    fun shown(score: Int): Int = maxOf(stored, score)

    /** Call with the current score; saves it when it beats the stored best. */
    fun update(score: Int) {
        if (score > stored) {
            stored = score
            store.save(levelId, score)
        }
    }
}
