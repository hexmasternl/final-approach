package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.platform.LevelProgressStore

/**
 * Adds the result of one run to the totals of its level, once. A run ends when the level is lost
 * or when the player leaves it through Restart or Quit; [begin] marks the start of the next run
 * on the same screen. Calling [finish] again for a run that was counted does nothing, so a
 * dialog that stays open or a quit after a lost run never counts twice.
 */
class RunTotals(private val store: LevelProgressStore, private val levelId: String) {
    private var counted = false

    /** Counts the run that stands at [state]. Returns true when it was added, false when it was already counted. */
    fun finish(state: GameState): Boolean {
        if (counted) return false
        counted = true
        store.add(levelId, landings = state.totalLanded, points = state.score)
        return true
    }

    /** A new run starts: the next [finish] counts again. */
    fun begin() {
        counted = false
    }
}
