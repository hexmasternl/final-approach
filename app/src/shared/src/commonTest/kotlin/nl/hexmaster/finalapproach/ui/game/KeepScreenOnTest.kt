package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.engine.GameOverReason
import nl.hexmaster.finalapproach.engine.GameState
import nl.hexmaster.finalapproach.engine.GameStatus
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KeepScreenOnTest {

    @Test
    fun aRunningLevelWithTheSettingOnKeepsTheScreenOn() {
        assertTrue(shouldKeepScreenOn(true, GameState()))
    }

    @Test
    fun aPausedLevelDoesNot() {
        assertFalse(shouldKeepScreenOn(true, GameState(paused = true)))
    }

    @Test
    fun aFinishedLevelDoesNot() {
        val over = GameState(status = GameStatus.GameOver(GameOverReason.Collision, listOf(1, 2)))
        assertFalse(shouldKeepScreenOn(true, over))
    }

    @Test
    fun withTheSettingOffNeverDoes() {
        assertFalse(shouldKeepScreenOn(false, GameState()))
    }
}
