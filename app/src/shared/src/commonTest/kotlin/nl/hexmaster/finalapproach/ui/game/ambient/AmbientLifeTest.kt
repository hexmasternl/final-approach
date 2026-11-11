package nl.hexmaster.finalapproach.ui.game.ambient

import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.ui.theme.FaLights
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AmbientLifeTest {
    private val config = LevelAmbienceConfig()

    @Test
    fun defaultLightsAreGentle() {
        assertEquals(emptyList(), config.problems())
        assertTrue(config.beaconCycleSeconds >= FaLights.MIN_CYCLE_SECONDS)
        assertTrue(config.runningLightCycleSeconds >= FaLights.MIN_CYCLE_SECONDS)
        assertTrue(config.lightOpacity <= FaLights.MAX_OPACITY)
        assertTrue(LevelAmbienceConfig(beaconCycleSeconds = 0.2).problems().isNotEmpty())
        assertTrue(LevelAmbienceConfig(lightOpacity = 1f).problems().isNotEmpty())
    }

    @Test
    fun cowsNeverLeaveTheFenceAndMoveSlowly() {
        val level = Levels.meadowField
        val paddock = requireNotNull(level.paddock)
        val life = AmbientLife(level, config, seed = 7)
        assertEquals(config.cowCount, life.cows.size)
        repeat(6000) {
            val before = life.cows.map { it.position }
            life.update(0.1)
            life.cows.forEachIndexed { i, cow ->
                assertTrue(cow.position.x in paddock.left..paddock.right && cow.position.y in paddock.top..paddock.bottom)
                assertTrue(before[i].distanceTo(cow.position) <= config.cowSpeed * 0.1 + 1e-9)
            }
        }
    }

    @Test
    fun boatsClearTheIslandAndAllKindsAppear() {
        val level = Levels.islandAirport
        val island = requireNotNull(level.island)
        val life = AmbientLife(level, config.copy(boatGapMin = 0.5, boatGapMax = 1.0), seed = 3)
        val kinds = mutableSetOf<BoatKind>()
        repeat(60000) {
            life.update(0.1)
            life.boat?.let { boat ->
                kinds += boat.kind
                val half = boat.kind.beam / 2
                val clearAbove = boat.position.y + half < island.center.y - island.radiusY
                val clearBelow = boat.position.y - half > island.center.y + island.radiusY
                assertTrue(clearAbove || clearBelow, "boat overlaps the island")
            }
        }
        assertEquals(BoatKind.entries.toSet(), kinds)
        assertTrue(BoatKind.ContainerVessel.length > BoatKind.Yacht.length)
        assertTrue(BoatKind.ContainerVessel.length > BoatKind.Sailboat.length)
    }

    @Test
    fun nothingSpawnsOrMovesUnderReducedMotion() {
        val tight = config.copy(boatGapMin = 0.1, boatGapMax = 0.2, birdGapMin = 0.1, birdGapMax = 0.2)
        val life = AmbientLife(Levels.islandAirport, tight, seed = 1, reduceMotion = true)
        repeat(1000) { life.update(0.1) }
        assertNull(life.bird)
        assertNull(life.boat)
        assertNull(life.runningLightHead(10))
        assertEquals(config.lightOpacity, life.beaconOpacity())
    }

    @Test
    fun runningLightAdvancesAlongTheRunwayAndRepeats() {
        val life = AmbientLife(Levels.savannaDawn, config, seed = 1)
        val count = 6
        val heads = mutableListOf<Int>()
        repeat(40) {
            heads += assertNotNull(life.runningLightHead(count))
            life.update(config.runningLightCycleSeconds / 20)
        }
        assertEquals(0, heads.first())
        assertEquals(count - 1, heads.max())
        assertTrue(heads.zipWithNext().any { (a, b) -> b < a }, "the sequence repeats")
        assertTrue(heads.zipWithNext().all { (a, b) -> b >= a || b == 0 })
    }

    @Test
    fun beaconNeverExceedsTheLightToken() {
        val life = AmbientLife(Levels.savannaDawn, config, seed = 1)
        repeat(400) {
            life.update(0.05)
            assertTrue(life.beaconOpacity() in 0f..FaLights.MAX_OPACITY)
        }
    }
}
