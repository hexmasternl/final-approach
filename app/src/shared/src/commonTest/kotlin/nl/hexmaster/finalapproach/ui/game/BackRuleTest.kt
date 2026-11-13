package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.engine.GameOverReason
import nl.hexmaster.finalapproach.engine.GameState
import nl.hexmaster.finalapproach.engine.GameStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class BackRuleTest {

    @Test
    fun backDuringPlayOpensTheOptionsPanelAndNeverQuits() {
        assertEquals(BackAction.OpenOptions, backAction(GameState(), optionsOpen = false))
    }

    @Test
    fun backDuringTheAdWaitOpensTheOptionsPanel() {
        assertEquals(BackAction.OpenOptions, backAction(GameState(paused = true), optionsOpen = false))
    }

    @Test
    fun backWithTheOptionsPanelOpenClosesIt() {
        assertEquals(BackAction.CloseOptions, backAction(GameState(paused = true), optionsOpen = true))
    }

    @Test
    fun backAfterTheLevelEndedLeavesForTheMainPage() {
        val over = GameState(status = GameStatus.GameOver(GameOverReason.Collision, listOf(1, 2)))
        assertEquals(BackAction.Quit, backAction(over, optionsOpen = false))
    }
}
