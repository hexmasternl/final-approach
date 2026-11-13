package nl.hexmaster.finalapproach.ui.game.ambient

import nl.hexmaster.finalapproach.domain.LevelDefaults
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.SceneryDefaults
import nl.hexmaster.finalapproach.ui.theme.FaLights
import kotlin.math.PI
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
                val shore = island.radiusY + SceneryDefaults.BEACH_WIDTH
                val clearAbove = boat.position.y + half < island.center.y - shore
                val clearBelow = boat.position.y - half > island.center.y + shore
                assertTrue(clearAbove || clearBelow, "boat overlaps the island or its beach")
                assertTrue(boat.position.y - half >= LevelDefaults.HUD_BAND, "boat sails under the HUD band")
                assertTrue(boat.position.y + half <= level.world.height, "boat sails off the world")
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
        assertTrue(life.flock.isEmpty())
        assertNull(life.boat)
        assertNull(life.runningLightHead(10))
        assertEquals(config.lightOpacity, life.beaconOpacity())
    }

    @Test
    fun cowsAndWingsHoldStillUnderReducedMotion() {
        val life = AmbientLife(Levels.meadowField, config.copy(birdGapMin = 0.1, birdGapMax = 0.2), seed = 5, reduceMotion = true)
        val before = life.cows.map { it.position to it.heading }
        val wings = mutableSetOf<Boolean>()
        repeat(2000) {
            life.update(0.1)
            wings += life.wingsOut
        }
        assertEquals(before, life.cows.map { it.position to it.heading })
        assertEquals(setOf(true), wings)
        assertNull(life.bird)
    }

    @Test
    fun threeBirdsShareOneCrossing() {
        val level = Levels.meadowField
        val life = AmbientLife(level, config.copy(birdGapMin = 1.0, birdGapMax = 2.0), seed = 9)
        var crossings = 0
        var flying = false
        repeat(4000) {
            life.update(0.05)
            val flock = life.flock
            val leader = life.bird
            if (leader == null) {
                assertTrue(flock.isEmpty())
                flying = false
                return@repeat
            }
            if (!flying) crossings++
            flying = true
            assertEquals(3, flock.size)
            assertEquals(leader.position, flock.first())
            // The followers fly behind the leader, one on each side, in a fixed formation.
            val (left, right) = flock.drop(1)
            assertEquals(leader.position.x - config.flockTrail, left.x, 1e-9)
            assertEquals(leader.position.x - config.flockTrail, right.x, 1e-9)
            assertEquals(leader.position.y - config.flockSpread, left.y, 1e-9)
            assertEquals(leader.position.y + config.flockSpread, right.y, 1e-9)
            assertEquals(PI / 2, life.flockHeading, 1e-9)
            flock.forEach {
                assertTrue(it.y - config.birdSpan / 2 >= LevelDefaults.HUD_BAND, "a bird flies under the HUD band")
                assertTrue(it.y + config.birdSpan / 2 <= level.world.height, "a bird flies below the world")
            }
        }
        assertTrue(crossings >= 3, "only $crossings crossings")
    }

    @Test
    fun wingsAlternateNoFasterThanEveryQuarterSecond() {
        assertTrue(config.wingSeconds >= LevelAmbienceConfig.MIN_WING_SECONDS)
        assertEquals(0.25, LevelAmbienceConfig.MIN_WING_SECONDS)
        assertTrue(LevelAmbienceConfig(wingSeconds = 0.1).problems().isNotEmpty())
        val life = AmbientLife(Levels.meadowField, config, seed = 2)
        val step = 0.01
        val runs = mutableListOf<Int>()
        var last = life.wingsOut
        var length = 0
        repeat(1000) {
            life.update(step)
            if (life.wingsOut == last) {
                length++
            } else {
                runs += length + 1
                last = life.wingsOut
                length = 0
            }
        }
        assertTrue(runs.size >= 10, "the wings flapped only ${runs.size} times in ten seconds")
        // The first and the unfinished last frame aside, every frame lasts the configured time.
        runs.drop(1).forEach { assertTrue(it * step >= LevelAmbienceConfig.MIN_WING_SECONDS - 1e-9, "a wing frame lasted ${it * step}s") }
        runs.drop(1).forEach { assertEquals(config.wingSeconds, it * step, step + 1e-9) }
    }

    @Test
    fun ambientSizesMeetTheMinimumDrawnSizes() {
        // On the 800 by 360dp reference phone a world unit is 0.2dp: 10dp is 50 units, 8dp is 40.
        assertTrue(config.cowLength >= 50.0)
        assertTrue(maxOf(config.farmerWidth, config.farmerLength) >= 50.0)
        assertTrue(config.birdSpan >= 40.0)
        BoatKind.entries.forEach { assertTrue(it.length >= 50.0, "$it") }
        // A cow keeps its whole body inside the fence, whichever way it faces.
        assertTrue(config.cowFenceInset >= config.cowLength / 2)
    }

    @Test
    fun birdsAndBoatsCrossTheWorldInTheSameTimeAsBeforeTheZoom() {
        val before = LevelDefaults.BASE_WORLD_WIDTH
        val now = Levels.islandAirport.world.width
        assertEquals(before / 90.0, now / config.birdSpeed, 1e-9)
        assertEquals(before / 14.0, now / BoatKind.Sailboat.speed, 1e-9)
        assertEquals(before / 18.0, now / BoatKind.Yacht.speed, 1e-9)
        assertEquals(before / 10.0, now / BoatKind.ContainerVessel.speed, 1e-9)
    }

    @Test
    fun aBirdCrossingTakesAsLongAsItsSpeedSays() {
        val level = Levels.meadowField
        val life = AmbientLife(level, config.copy(birdGapMin = 0.5, birdGapMax = 0.6), seed = 4)
        val step = 0.02
        while (life.bird == null) life.update(step)
        var inside = 0
        while (life.bird != null) {
            val x = life.bird?.position?.x ?: break
            if (x in 0.0..level.world.width) inside++
            life.update(step)
        }
        assertEquals(level.world.width / config.birdSpeed, inside * step, 2 * step)
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
