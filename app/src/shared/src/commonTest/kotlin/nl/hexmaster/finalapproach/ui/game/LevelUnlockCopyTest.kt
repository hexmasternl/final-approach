package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.domain.LevelTotals
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.engine.LevelUnlockRules
import nl.hexmaster.finalapproach.ui.components.formatScore
import kotlin.test.Test
import kotlin.test.assertEquals

class LevelUnlockCopyTest {
    private val island = checkNotNull(LevelUnlockRules.Default.ruleFor("island-airport"))
    private val savanna = checkNotNull(LevelUnlockRules.Default.ruleFor("savanna-dawn"))

    @Test
    fun islandAirportNamesTheConditionAndTheLandingProgress() {
        val info = lockedInfo(Levels.islandAirport, island, Levels.meadowField, LevelTotals(landings = 412))
        assertEquals("${formatScore(412)} of ${formatScore(500)} landings", info.text)
        assertEquals("Land ${formatScore(500)} aircraft on Meadow Field to open Island.", info.condition)
    }

    @Test
    fun savannaDawnShowsPointsProgress() {
        val info = lockedInfo(Levels.savannaDawn, savanna, Levels.islandAirport, LevelTotals(points = 12_500))
        assertEquals("${formatScore(12_500)} of ${formatScore(30_000)} points", info.text)
        assertEquals("Earn ${formatScore(30_000)} points on Island to open Savanna.", info.condition)
    }

    @Test
    fun progressNeverShowsMoreThanTheTarget() {
        val over = lockedInfo(Levels.islandAirport, island, Levels.meadowField, LevelTotals(landings = 5_000))
        assertEquals("${formatScore(500)} of ${formatScore(500)} landings", over.text)
    }
}
