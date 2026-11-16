package nl.hexmaster.finalapproach.domain

/** What a player has done on one level over all runs: aircraft landed and points earned. */
data class LevelTotals(val landings: Int = 0, val points: Int = 0) {
    operator fun plus(other: LevelTotals): LevelTotals = LevelTotals(landings + other.landings, points + other.points)
}
