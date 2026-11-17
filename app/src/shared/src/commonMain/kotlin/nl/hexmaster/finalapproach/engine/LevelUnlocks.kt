package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.LevelTotals
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.platform.HighScoreStore
import nl.hexmaster.finalapproach.platform.LevelProgressStore

/** What an unlock rule counts. */
enum class UnlockMetric { Landings, Points }

/**
 * Level [levelId] opens once the player's total [metric] on [requiredLevelId] passes [threshold]:
 * more than it when [strict], at least it otherwise.
 */
data class UnlockRule(
    val levelId: String,
    val requiredLevelId: String,
    val metric: UnlockMetric,
    val threshold: Int,
    val strict: Boolean,
) {
    /** The part of [totals] this rule counts. */
    fun valueOf(totals: LevelTotals): Int = when (metric) {
        UnlockMetric.Landings -> totals.landings
        UnlockMetric.Points -> totals.points
    }

    /** True when [totals] of the required level open the level. */
    fun isMet(totals: LevelTotals): Boolean = valueOf(totals).let { if (strict) it > threshold else it >= threshold }
}

/** The unlock rules of the game: tunable values, kept out of the rule code and the screens. */
data class LevelUnlockRules(val rules: List<UnlockRule>) {
    /** The rule that locks [levelId], or null when the level is always open. */
    fun ruleFor(levelId: String): UnlockRule? = rules.firstOrNull { it.levelId == levelId }

    companion object {
        /** Landings on Meadow field that open Island airport: this many or more. */
        const val ISLAND_LANDINGS = 500

        /** Points on Island airport that open Savanna dawn: this many or more. */
        const val SAVANNA_POINTS = 30_000

        val Default = LevelUnlockRules(
            listOf(
                UnlockRule("island-airport", "meadow-field", UnlockMetric.Landings, ISLAND_LANDINGS, strict = false),
                UnlockRule("savanna-dawn", "island-airport", UnlockMetric.Points, SAVANNA_POINTS, strict = false),
            ),
        )
    }
}

/**
 * Which levels are open. Pure: the totals, the flags and the best scores come in as functions, so
 * the rules are tested without a store.
 *
 * @param order the level ids in level order; a best score on a later level opens the earlier ones
 */
class LevelUnlocks(
    val rules: LevelUnlockRules = LevelUnlockRules.Default,
    private val order: List<String> = Levels.all.map { it.id },
) {
    /**
     * True when [levelId] is open: it has no rule, it was opened before ([flagged]), the player
     * already has a best score on it or a later level, or the totals now meet its rule.
     */
    fun isUnlocked(
        levelId: String,
        totals: (String) -> LevelTotals,
        hasBestScore: (String) -> Boolean,
        flagged: Boolean = false,
    ): Boolean {
        val rule = rules.ruleFor(levelId) ?: return true
        if (flagged) return true
        val index = order.indexOf(levelId)
        if (index >= 0 && order.drop(index).any(hasBestScore)) return true
        return rule.isMet(totals(rule.requiredLevelId))
    }

    /** True when [levelId] is open on this device, judged from [progress] and [highScores]. */
    fun isUnlocked(levelId: String, progress: LevelProgressStore, highScores: HighScoreStore): Boolean =
        isUnlocked(levelId, progress::totals, { highScores.best(it) > 0 }, progress.isUnlocked(levelId))

    /** Remembers every level that is open now, so it stays open; returns the open levels. */
    fun settle(progress: LevelProgressStore, highScores: HighScoreStore): Set<String> =
        order.filter { isUnlocked(it, progress, highScores) }.onEach(progress::markUnlocked).toSet()
}
