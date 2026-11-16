package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.domain.LevelConfig

/**
 * Why a level card is locked: [text] is what the chip reads (the progress), [condition] an optional
 * line under it that says what unlocks the level.
 */
data class LockedInfo(val text: String, val condition: String? = null)

/**
 * What a level card shows besides the level itself. It is presentation only: whether a level is
 * locked, and the text that says so, come from the caller.
 *
 * @param bestScore the best score of the level; the card shows no chip when it is not above zero
 * @param tags the tag chips of an unlocked card, at most two
 * @param locked set when the card is locked
 */
data class LevelCardState(
    val bestScore: Int = 0,
    val tags: List<String> = emptyList(),
    val locked: LockedInfo? = null,
) {
    /** The chip the card shows in the art: only for an unlocked level with a best score. */
    val showsBestScore: Boolean get() = locked == null && bestScore > 0
}

private const val RUNWAY = "runway"
private const val HELIPAD = "helipad"

private fun count(n: Int, noun: String) = if (n == 1) "1 $noun" else "$n ${noun}s"

/** The tag chips of [level]: its runways and its helipads, each only when it has any. */
fun levelTags(level: LevelConfig): List<String> = buildList {
    if (level.runways.isNotEmpty()) add(count(level.runways.size, RUNWAY))
    if (level.helipads.isNotEmpty()) add(count(level.helipads.size, HELIPAD))
}

/** The card state of [level] with its [bestScore] and, when locked, the [locked] reason. */
fun levelCardState(level: LevelConfig, bestScore: Int, locked: LockedInfo? = null): LevelCardState =
    LevelCardState(bestScore = bestScore, tags = levelTags(level), locked = locked)
