package nl.hexmaster.finalapproach.engine

/** Why a level was lost. */
enum class GameOverReason {
    /** Two aircraft touched. */
    Collision,

    /** An aircraft flew out of the viewport without landing. */
    LeftArea,
}

/** Whether the level is still being played. */
sealed interface GameStatus {
    data object Playing : GameStatus

    /** The level is lost because of [reason]; [aircraftIds] are the one or two aircraft involved. */
    data class GameOver(val reason: GameOverReason, val aircraftIds: List<Int>) : GameStatus
}