package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.LevelTotals
import nl.hexmaster.finalapproach.engine.UnlockMetric
import nl.hexmaster.finalapproach.engine.UnlockRule
import nl.hexmaster.finalapproach.ui.components.formatScore

/**
 * The words on a locked level card: the unlock condition and the player's progress towards it.
 * Pure, so the copy is tested without a screen.
 */
internal fun lockedInfo(level: LevelConfig, rule: UnlockRule, required: LevelConfig, totals: LevelTotals): LockedInfo {
    // A strict rule ("more than N") is shown as the number to reach: the first count past it opens the level.
    val target = if (rule.strict) rule.threshold + 1 else rule.threshold
    val shown = rule.valueOf(totals).coerceAtMost(target)
    val (verb, noun) = when (rule.metric) {
        UnlockMetric.Landings -> "Land" to "aircraft"
        UnlockMetric.Points -> "Earn" to "points"
    }
    val unit = if (rule.metric == UnlockMetric.Landings) "landings" else "points"
    return LockedInfo(
        text = "${formatScore(shown)} of ${formatScore(target)} $unit",
        condition = "$verb ${formatScore(target)} $noun on ${required.name} to open ${level.name}.",
    )
}
