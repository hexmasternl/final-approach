package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.LevelTotals
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LevelUnlocksTest {
    private val unlocks = LevelUnlocks()

    private fun open(
        levelId: String,
        meadow: LevelTotals = LevelTotals(),
        island: LevelTotals = LevelTotals(),
        best: Set<String> = emptySet(),
        flagged: Boolean = false,
    ): Boolean = unlocks.isUnlocked(
        levelId,
        totals = { if (it == "meadow-field") meadow else if (it == "island-airport") island else LevelTotals() },
        hasBestScore = { it in best },
        flagged = flagged,
    )

    @Test
    fun meadowFieldIsAlwaysOpen() = assertTrue(open("meadow-field"))

    @Test
    fun islandAirportIsLockedAtExactlyAThousandLandings() = assertFalse(open("island-airport", meadow = LevelTotals(landings = 1_000)))

    @Test
    fun islandAirportOpensAtAThousandAndOneLandings() = assertTrue(open("island-airport", meadow = LevelTotals(landings = 1_001)))

    @Test
    fun savannaDawnIsLockedAt29999Points() = assertFalse(open("savanna-dawn", island = LevelTotals(points = 29_999)))

    @Test
    fun savannaDawnOpensAt30000Points() = assertTrue(open("savanna-dawn", island = LevelTotals(points = 30_000)))

    @Test
    fun landingsOnIslandDoNotOpenIslandAndPointsOnMeadowDoNotOpenIt() {
        assertFalse(open("island-airport", island = LevelTotals(landings = 5_000, points = 99_000)))
        assertFalse(open("island-airport", meadow = LevelTotals(landings = 0, points = 99_000)))
    }

    @Test
    fun totalsSummedOverRunsOpenTheLevel() {
        val total = LevelTotals(landings = 600) + LevelTotals(landings = 401)
        assertTrue(open("island-airport", meadow = total))
    }

    @Test
    fun aLevelOpenedBeforeStaysOpenWhateverTheTotals() = assertTrue(open("island-airport", flagged = true))

    @Test
    fun aBestScoreOnALaterLevelOpensTheEarlierOnes() {
        val best = setOf("savanna-dawn")
        assertTrue(open("island-airport", best = best))
        assertTrue(open("savanna-dawn", best = best))
    }

    @Test
    fun aBestScoreOnMeadowOnlyDoesNotOpenIsland() = assertFalse(open("island-airport", best = setOf("meadow-field")))

    @Test
    fun aBestScoreOnIslandOpensIslandButNotSavanna() {
        val best = setOf("island-airport")
        assertTrue(open("island-airport", best = best))
        assertFalse(open("savanna-dawn", best = best))
    }

    @Test
    fun thresholdsComeFromTheConfig() {
        val easier = LevelUnlocks(
            LevelUnlockRules(listOf(UnlockRule("island-airport", "meadow-field", UnlockMetric.Landings, 10, strict = true))),
        )
        assertTrue(easier.isUnlocked("island-airport", { LevelTotals(landings = 11) }, { false }))
        assertFalse(easier.isUnlocked("island-airport", { LevelTotals(landings = 10) }, { false }))
        assertNotNull(LevelUnlockRules.Default.ruleFor("savanna-dawn"))
        assertEquals(LevelUnlockRules.SAVANNA_POINTS, LevelUnlockRules.Default.ruleFor("savanna-dawn")?.threshold)
    }
}
