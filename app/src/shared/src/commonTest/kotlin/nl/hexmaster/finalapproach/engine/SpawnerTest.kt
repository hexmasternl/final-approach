package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.DifficultyPhase
import nl.hexmaster.finalapproach.domain.HelipadConfig
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.RunwayConfig
import nl.hexmaster.finalapproach.domain.SpawnEdge
import nl.hexmaster.finalapproach.domain.SpawnRules
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.domain.WorldConfig
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SpawnerTest {

    private val speed = { _: AircraftType -> 50.0 }

    private val level = LevelConfig(
        id = "test",
        name = "Test",
        order = 1,
        world = WorldConfig(1000.0, 600.0),
        runways = listOf(RunwayConfig("r", Vec2(450.0, 300.0), PI / 2, 400.0)),
        helipads = listOf(HelipadConfig("h", Vec2(850.0, 300.0))),
        phases = listOf(
            DifficultyPhase(0.0, 6.0, 9.0, mapOf(AircraftType.LightProp to 3, AircraftType.Helicopter to 1), 3),
            DifficultyPhase(60.0, 4.0, 6.0, mapOf(AircraftType.LightProp to 1, AircraftType.Turboprop to 1), 3),
            DifficultyPhase(
                120.0, 2.0, 3.0,
                AircraftType.entries.associateWith { 1 },
                3,
            ),
        ),
    )

    /** Runs a spawner for [seconds] with every aircraft landing at once, and lists the spawns with their times. */
    private fun run(
        config: LevelConfig = level,
        seed: Long,
        seconds: Double,
        step: Double = 0.1,
    ): List<Pair<Double, Spawn>> {
        val spawner = Spawner(config, seed, speed)
        val result = mutableListOf<Pair<Double, Spawn>>()
        var time = 0.0
        while (time < seconds) {
            spawner.update(step, time, emptyList())?.let { result += time to it }
            time += step
        }
        return result
    }

    @Test
    fun sameSeedGivesTheSameSequence() {
        assertEquals(run(seed = 42, seconds = 200.0), run(seed = 42, seconds = 200.0))
    }

    @Test
    fun differentSeedsDiffer() {
        assertNotEquals(run(seed = 1, seconds = 200.0), run(seed = 2, seconds = 200.0))
    }

    @Test
    fun firstPhaseSpawnsOnlyAllowedTypes() {
        for (seed in 0L until 20L) {
            val types = run(seed = seed, seconds = 59.0).map { it.second.type }.toSet()
            assertTrue(types.all { it in setOf(AircraftType.LightProp, AircraftType.Helicopter) }, "seed $seed: $types")
        }
    }

    @Test
    fun intervalsStayWithinThePhaseRange() {
        for (seed in 0L until 20L) {
            val times = run(seed = seed, seconds = 59.0).map { it.first }
            times.zipWithNext().forEach { (a, b) ->
                val gap = b - a
                assertTrue(gap >= 6.0 - 0.2 && gap <= 9.0 + 0.2, "seed $seed: gap $gap")
            }
        }
    }

    @Test
    fun heavierWeightsSpawnMoreOften() {
        val types = (0L until 50L).flatMap { seed -> run(seed = seed, seconds = 59.0).map { it.second.type } }
        val props = types.count { it == AircraftType.LightProp }
        val helis = types.count { it == AircraftType.Helicopter }
        assertTrue(props > helis * 2, "props $props, helicopters $helis")
    }

    @Test
    fun nothingSpawnsAtTheCapAndSpawningResumesAfterALanding() {
        val spawner = Spawner(level, 7, speed)
        val far = listOf(Vec2(-5000.0, -5000.0), Vec2(-6000.0, -5000.0), Vec2(-7000.0, -5000.0))
        var time = 0.0
        repeat(200) {
            assertNull(spawner.update(0.1, time, far))
            time += 0.1
        }
        assertTrue(spawner.timeUntilNextSpawn <= 0.0)
        assertNotNull(spawner.update(0.1, time, far.take(2)))
    }

    @Test
    fun spawnsLieOutsideTheViewportByTheMargin() {
        val margin = level.world.spawnMargin
        for (seed in 0L until 30L) {
            run(seed = seed, seconds = 200.0).forEach { (_, spawn) ->
                val p = spawn.position
                val onEdge = when (spawn.edge) {
                    SpawnEdge.Left -> p.x == -margin
                    SpawnEdge.Right -> p.x == 1000.0 + margin
                    SpawnEdge.Top -> p.y == -margin
                    SpawnEdge.Bottom -> p.y == 600.0 + margin
                }
                assertTrue(onEdge, "seed $seed: $spawn")
                assertTrue(!level.world.viewport.contains(p))
            }
        }
    }

    @Test
    fun onlyPermittedEdgesAreUsed() {
        val restricted = level.copy(spawn = SpawnRules(edges = setOf(SpawnEdge.Left, SpawnEdge.Top)))
        for (seed in 0L until 20L) {
            val edges = run(restricted, seed, 200.0).map { it.second.edge }.toSet()
            assertTrue(edges.all { it == SpawnEdge.Left || it == SpawnEdge.Top }, "seed $seed: $edges")
        }
    }

    @Test
    fun everyEntryIsFairOverManySeeds() {
        val viewport = level.world.viewport
        for (seed in 0L until 100L) {
            run(seed = seed, seconds = 200.0).forEach { (_, spawn) ->
                val crossing = viewport.rayCrossing(
                    spawn.position,
                    nl.hexmaster.finalapproach.domain.Angles.direction(spawn.heading),
                )
                assertNotNull(crossing, "seed $seed: $spawn misses the viewport")
                val chord = crossing.second - crossing.first
                assertTrue(chord >= 0.5 * 600.0, "seed $seed: chord $chord")
                assertTrue(chord / 50.0 >= 4.0, "seed $seed: time on screen ${chord / 50.0}")
            }
        }
    }

    @Test
    fun noFairEntryDelaysTheSpawn() {
        val tooFast = Spawner(level, 3) { 1_000_000.0 }
        assertNull(tooFast.update(level.spawn.firstSpawnDelay, 0.0, emptyList()))
        assertEquals(level.spawn.retryDelay, tooFast.timeUntilNextSpawn)
    }

    @Test
    fun spawnKeepsTheSpawnSeparationFromAllAircraft() {
        val separation = level.world.spawnSeparation
        for (seed in 0L until 50L) {
            val spawner = Spawner(level, seed, speed)
            // An aircraft waiting just outside the left edge, still off screen.
            val waiting = listOf(Vec2(-level.world.spawnMargin, 300.0))
            var time = 0.0
            while (time < 30.0) {
                spawner.update(0.1, time, waiting)?.let {
                    assertTrue(it.position.distanceTo(waiting[0]) >= separation, "seed $seed: $it")
                }
                time += 0.1
            }
        }
    }

    @Test
    fun separationBlocksASpawnWhenEveryEntryIsTooClose() {
        val oneEdge = level.copy(
            world = WorldConfig(1000.0, 600.0, spawnSeparation = 5000.0),
            spawn = SpawnRules(edges = setOf(SpawnEdge.Left)),
        )
        val spawner = Spawner(oneEdge, 5, speed)
        assertNull(spawner.update(oneEdge.spawn.firstSpawnDelay, 0.0, listOf(Vec2(-60.0, 300.0))))
        assertEquals(oneEdge.spawn.retryDelay, spawner.timeUntilNextSpawn)
    }

    @Test
    fun spawnRateAndVarietyGrowAcrossThePhases() {
        val seeds = 0L until 50L
        val windows = listOf(0.0 to 60.0, 60.0 to 120.0, 120.0 to 180.0)
        val counts = IntArray(3)
        val variety = Array(3) { mutableSetOf<AircraftType>() }
        for (seed in seeds) {
            run(seed = seed, seconds = 180.0).forEach { (time, spawn) ->
                val index = windows.indexOfFirst { time >= it.first && time < it.second }
                counts[index]++
                variety[index] += spawn.type
            }
        }
        assertTrue(counts[0] < counts[1] && counts[1] < counts[2], counts.toList().toString())
        assertTrue(variety[2].size > variety[0].size, variety.toList().toString())
    }
}
