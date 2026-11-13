package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.engine.GameState
import nl.hexmaster.finalapproach.engine.GameStatus

/**
 * Whether a level is running: not paused (options panel, the ad before a level) and not over.
 * A tutorial step counts as running. The screen stays on, the system bars hide and the side edges
 * are protected from system gestures exactly while this holds.
 */
fun isLevelRunning(state: GameState): Boolean = !state.paused && state.status is GameStatus.Playing

/**
 * Whether the screen should stay on: the player allows it, and the level is running. A paused
 * level and a finished one use the normal timeout.
 */
fun shouldKeepScreenOn(setting: Boolean, state: GameState): Boolean = setting && isLevelRunning(state)
