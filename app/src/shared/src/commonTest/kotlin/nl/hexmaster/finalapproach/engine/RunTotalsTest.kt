package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.LevelTotals
import nl.hexmaster.finalapproach.platform.InMemoryHighScoreStore
import nl.hexmaster.finalapproach.platform.InMemoryLevelProgressStore
import nl.hexmaster.finalapproach.domain.AircraftType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RunTotalsTest {
    private val store = InMemoryLevelProgressStore()
    private val totals = RunTotals(store, "meadow-field")

    private fun run(landed: Int, score: Int) = GameState(
        score = score,
        landedByType = if (landed > 0) mapOf(AircraftType.LightProp to landed) else emptyMap(),
    )

    @Test
    fun aLostRunAddsItsLandingsAndPoints() {
        assertTrue(totals.finish(run(landed = 12, score = 340)))
        assertEquals(LevelTotals(landings = 12, points = 340), store.totals("meadow-field"))
    }

    @Test
    fun aRunIsCountedOnce() {
        totals.finish(run(landed = 12, score = 340))
        assertFalse(totals.finish(run(landed = 12, score = 340)))
        assertEquals(LevelTotals(landings = 12, points = 340), store.totals("meadow-field"))
    }

    @Test
    fun aNewRunCountsAgain() {
        totals.finish(run(landed = 12, score = 340))
        totals.begin()
        totals.finish(run(landed = 5, score = 100))
        assertEquals(LevelTotals(landings = 17, points = 440), store.totals("meadow-field"))
    }

    @Test
    fun totalsAreKeptPerLevel() {
        totals.finish(run(landed = 3, score = 30))
        assertEquals(LevelTotals(), store.totals("island-airport"))
    }

    @Test
    fun settlingKeepsAnOpenedLevelOpenWhenTheTotalsLaterDrop() {
        val unlocks = LevelUnlocks()
        val scores = InMemoryHighScoreStore()
        store.add("meadow-field", landings = 500, points = 0)
        assertTrue("island-airport" in unlocks.settle(store, scores))
        assertTrue(store.isUnlocked("island-airport"))
        val fresh = LevelUnlocks(LevelUnlockRules(listOf(UnlockRule("island-airport", "meadow-field", UnlockMetric.Landings, 5_000, strict = true))))
        assertTrue(fresh.isUnlocked("island-airport", store, scores))
    }

    @Test
    fun anExistingPlayerWithABestScoreOnSavannaHasEveryLevelOpen() {
        val scores = InMemoryHighScoreStore().also { it.save("savanna-dawn", 4_000) }
        assertEquals(setOf("meadow-field", "island-airport", "savanna-dawn"), LevelUnlocks().settle(store, scores))
    }
}
