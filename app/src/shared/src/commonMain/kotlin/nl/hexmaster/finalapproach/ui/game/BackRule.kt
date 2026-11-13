package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.engine.GameState
import nl.hexmaster.finalapproach.engine.GameStatus

/** What a system back press does on the game screen. */
internal enum class BackAction { Quit, CloseOptions, OpenOptions }

/**
 * Back on the game screen: after the level ended it leaves for the main page; with the options
 * panel open it closes the panel and resumes; otherwise, during play or the ad wait, it pauses and
 * opens the options panel. It never leaves the level while one is running.
 */
internal fun backAction(state: GameState, optionsOpen: Boolean): BackAction = when {
    state.status is GameStatus.GameOver -> BackAction.Quit
    optionsOpen -> BackAction.CloseOptions
    else -> BackAction.OpenOptions
}
