package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.domain.HelipadConfig
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LevelCardStateTest {
    private val base = Levels.meadowField
    private val runway = base.runways.first()

    private fun level(runways: Int, helipads: Int): LevelConfig = base.copy(
        runways = List(runways) { runway.copy(id = "r$it") },
        helipads = List(helipads) { HelipadConfig(id = "h$it", position = Vec2(0.0, 0.0)) },
    )

    @Test
    fun oneRunwayIsSingular() = assertEquals(listOf("1 runway"), levelTags(level(1, 0)))

    @Test
    fun twoRunwaysArePlural() = assertEquals(listOf("2 runways"), levelTags(level(2, 0)))

    @Test
    fun oneHelipadIsSingular() = assertEquals(listOf("1 helipad"), levelTags(level(0, 1)))

    @Test
    fun runwaysComeBeforeHelipads() = assertEquals(listOf("1 runway", "1 helipad"), levelTags(level(1, 1)))

    @Test
    fun noLandingZonesGiveNoTags() = assertEquals(emptyList(), levelTags(level(0, 0)))

    @Test
    fun everyLevelHasAtMostTwoTags() {
        Levels.all.forEach { assertTrue(levelTags(it).size <= 2, it.id) }
    }

    @Test
    fun bestScoreChipNeedsAScoreAboveZero() {
        assertFalse(LevelCardState(bestScore = 0).showsBestScore)
        assertTrue(LevelCardState(bestScore = 18_200).showsBestScore)
    }

    @Test
    fun lockedCardNeverShowsABestScore() {
        assertFalse(LevelCardState(bestScore = 500, locked = LockedInfo("Land 50 to unlock")).showsBestScore)
    }

    @Test
    fun stateCarriesTheTagsAndTheLock() {
        val state = levelCardState(level(1, 1), bestScore = 7, locked = LockedInfo("Land 50 to unlock"))
        assertEquals(listOf("1 runway", "1 helipad"), state.tags)
        assertEquals("Land 50 to unlock", state.locked?.text)
    }
}
