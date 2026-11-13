package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.engine.GameState
import nl.hexmaster.finalapproach.engine.GameStatus

/**
 * Whether the screen should stay on: the player allows it, and the level is running. A paused
 * level (options panel, the ad before a level) and a finished one use the normal timeout.
 */
fun shouldKeepScreenOn(setting: Boolean, state: GameState): Boolean =
    setting && !state.paused && state.status is GameStatus.Playing
