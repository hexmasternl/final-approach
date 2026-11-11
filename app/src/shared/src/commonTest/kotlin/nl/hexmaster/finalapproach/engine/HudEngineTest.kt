package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.platform.InMemoryHighScoreStore
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HudEngineTest {

    private val level = Levels.meadowField
    private val dt = 0.05
    private val quiet = level.copy(spawn = level.spawn.copy(firstSpawnDelay = 10_000.0))

    private fun GameEngine.run(seconds: Double) = repeat((seconds / dt).toInt()) { update(dt) }

    @Test
    fun aSpawnRaisesOnlyItsTypesCount() {
        val engine = GameEngine(level, seed = 1)
        while (engine.state.value.aircraft.isEmpty()) engine.update(dt)
        val type = engine.state.value.aircraft.single().type
        assertEquals(mapOf(type to 1), engine.state.value.spawnedByType)
        assertTrue(engine.state.value.landedByType.isEmpty())
    }

    @Test
    fun aLandingRaisesTheScoreAndLandedCountButNotTheSpawnCounts() {
        val engine = GameEngine(quiet, seed = 1)
        val helipad = quiet.helipads.single().position
        engine.place(listOf(Aircraft(1, AircraftType.Helicopter, helipad + Vec2(0.0, -200.0), PI)))
        engine.beginPath(1)
        engine.extendPath(1, helipad)
        engine.run(10.0)
        val state = engine.state.value
        assertEquals(engine.specs.pointsOf(AircraftType.Helicopter), state.score)
        assertEquals(mapOf(AircraftType.Helicopter to 1), state.landedByType)
        assertEquals(1, state.totalLanded)
        assertTrue(state.spawnedByType.isEmpty())
    }

    @Test
    fun nothingChangesWhilePausedAndResumeContinuesFromTheSameState() {
        val engine = GameEngine(level, seed = 1)
        engine.run(5.0)
        engine.pause()
        val frozen = engine.state.value
        engine.run(5.0)
        assertEquals(frozen, engine.state.value)
        engine.resume()
        engine.update(dt)
        assertEquals(frozen.levelTime + dt, engine.state.value.levelTime, 1e-9)
    }

    @Test
    fun restartClearsTheRunAndKeepsTheStoredBest() {
        val store = InMemoryHighScoreStore()
        val best = BestScore(store, level.id)
        val engine = GameEngine(level, seed = 1)
        engine.run(20.0)
        best.update(120)
        engine.restart()
        val state = engine.state.value
        assertEquals(0, state.score)
        assertTrue(state.aircraft.isEmpty())
        assertTrue(state.spawnedByType.isEmpty())
        assertEquals(0.0, state.levelTime)
        assertEquals(120, store.best(level.id))
    }

    @Test
    fun theSpawnedTypesAreTheLevelsTypesInRosterOrder() {
        val phase = level.phases.first().copy(
            weights = mapOf(AircraftType.Helicopter to 1, AircraftType.LightProp to 1),
        )
        val small = level.copy(phases = listOf(phase))
        assertEquals(listOf(AircraftType.LightProp, AircraftType.Helicopter), small.spawnedTypes)
    }

    @Test
    fun theBestScoreStartsAtZeroFollowsAHigherScoreAndIsSavedOncePerGain() {
        val store = InMemoryHighScoreStore()
        val best = BestScore(store, "a")
        assertEquals(0, best.shown(0))
        best.update(50)
        best.update(50)
        assertEquals(60, best.shown(60))
        best.update(60)
        assertEquals(2, store.saveCalls)
        assertEquals(60, store.best("a"))
        assertEquals(0, store.best("b"))
        assertEquals(60, BestScore(store, "a").shown(0))
    }
}
