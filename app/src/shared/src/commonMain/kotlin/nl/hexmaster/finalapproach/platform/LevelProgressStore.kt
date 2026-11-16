package nl.hexmaster.finalapproach.platform

import nl.hexmaster.finalapproach.domain.LevelTotals

/**
 * Where the player's progress per level is kept on this device: totals over all runs, and which
 * levels were opened. The host implements it.
 */
interface LevelProgressStore {
    /** The totals of [levelId]; zero when nothing was recorded. */
    fun totals(levelId: String): LevelTotals

    /** Adds [landings] and [points] of one finished run to the totals of [levelId]. */
    fun add(levelId: String, landings: Int, points: Int)

    /** True when [levelId] was opened before and stays open. */
    fun isUnlocked(levelId: String): Boolean

    /** Remembers that [levelId] is open. */
    fun markUnlocked(levelId: String)
}

/** A [LevelProgressStore] that keeps everything in memory; used by tests and previews. */
class InMemoryLevelProgressStore : LevelProgressStore {
    private val totals = mutableMapOf<String, LevelTotals>()
    private val unlocked = mutableSetOf<String>()

    override fun totals(levelId: String): LevelTotals = totals[levelId] ?: LevelTotals()

    override fun add(levelId: String, landings: Int, points: Int) {
        totals[levelId] = totals(levelId) + LevelTotals(landings, points)
    }

    override fun isUnlocked(levelId: String): Boolean = levelId in unlocked

    override fun markUnlocked(levelId: String) {
        unlocked += levelId
    }
}
