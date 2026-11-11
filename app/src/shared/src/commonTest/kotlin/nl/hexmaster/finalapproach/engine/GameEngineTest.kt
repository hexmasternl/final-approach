package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GameEngineTest {

    private val level = Levels.meadowField
    private val dt = 0.05

    private fun GameEngine.run(seconds: Double, each: (GameState) -> Unit = {}) {
        repeat((seconds / dt).toInt()) {
            update(dt)
            each(state.value)
        }
    }

    @Test
    fun updatesArePublishedOnTheStateFlow() {
        val engine = GameEngine(level, seed = 1)
        engine.update(dt)
        assertEquals(dt, engine.state.value.levelTime, 1e-12)
    }

    @Test
    fun spawnsAppearWithTheSpawnersTypePositionAndHeading() {
        val engine = GameEngine(level, seed = 7)
        val spawner = Spawner(level, 7, engine.specs::speedOf)
        var expected: Spawn? = null
        var time = 0.0
        while (expected == null) {
            time += dt
            expected = spawner.update(dt, time, emptyList())
        }
        while (engine.state.value.aircraft.isEmpty()) engine.update(dt)
        val first = engine.state.value.aircraft.single()
        assertEquals(expected.type, first.type)
        assertEquals(expected.position, first.position)
        assertEquals(expected.heading, first.heading)
    }

    @Test
    fun theFirstLevelNeverHasMoreThanThreeAircraftInTheAir() {
        val engine = GameEngine(level, seed = 42)
        var most = 0
        engine.run(600.0) { state ->
            most = maxOf(most, state.aircraft.count { it.status !is AircraftStatus.RollingOut })
        }
        assertTrue(most in 1..3, "most aircraft in the air: $most")
    }

    @Test
    fun theSameSeedGivesTheSameStates() {
        val a = GameEngine(level, seed = 3)
        val b = GameEngine(level, seed = 3)
        repeat(2000) {
            a.update(dt)
            b.update(dt)
            assertEquals(a.state.value, b.state.value)
        }
    }

    @Test
    fun anAircraftThatLeavesTheViewportIsRemoved() {
        val engine = GameEngine(level, seed = 5)
        engine.run(120.0)
        val ids = mutableSetOf<Int>()
        val removed = mutableSetOf<Int>()
        engine.run(300.0) { state ->
            val now = state.aircraft.map { it.id }.toSet()
            removed += ids - now
            ids += now
            state.aircraft.forEach { aircraft ->
                if (aircraft.hasEnteredViewport) {
                    assertTrue(level.world.viewport.expandedBy(level.world.leaveMargin).contains(aircraft.position))
                }
            }
        }
        assertTrue(removed.isNotEmpty(), "no aircraft ever left")
    }

    @Test
    fun liveriesTakeTheNextFreeColour() {
        val engine = GameEngine(level, seed = 9)
        engine.run(200.0) { state ->
            val liveries = state.aircraft.map { it.livery }
            assertEquals(liveries.size, liveries.toSet().size, "shared livery in $liveries")
        }
    }

    @Test
    fun tapSelectsTheNearestAircraftWithinThePickRadius() {
        val engine = GameEngine(level, seed = 1)
        engine.place(listOf(jet(1, Vec2(100.0, 100.0)), jet(2, Vec2(160.0, 100.0))))
        assertEquals(2, engine.select(Vec2(140.0, 100.0), 50.0))
        assertEquals(2, engine.state.value.selectedId)
        assertEquals(null, engine.select(Vec2(400.0, 400.0), 50.0))
        assertEquals(null, engine.state.value.selectedId)
    }

    @Test
    fun aNewPathReplacesTheOldOne() {
        val engine = GameEngine(level, seed = 1)
        engine.place(listOf(jet(1, Vec2(100.0, 100.0))))
        engine.beginPath(1)
        engine.extendPath(1, Vec2(100.0, 200.0))
        engine.endPath(1)
        engine.beginPath(1)
        engine.extendPath(1, Vec2(200.0, 100.0))
        assertEquals(listOf(Vec2(200.0, 100.0)), engine.aircraft(1).path.points)
    }

    @Test
    fun pointsCloserThanTheMinimumSpacingAreDropped() {
        val engine = GameEngine(level, seed = 1)
        engine.place(listOf(jet(1, Vec2(100.0, 100.0))))
        engine.beginPath(1)
        engine.extendPath(1, Vec2(150.0, 100.0))
        engine.extendPath(1, Vec2(150.0 + engine.config.minPointSpacing / 2.0, 100.0))
        assertEquals(listOf(Vec2(150.0, 100.0)), engine.aircraft(1).path.points)
    }

    @Test
    fun aPathIntoALandingZoneIsCutOffAndFurtherPointsAreIgnored() {
        val engine = GameEngine(level, seed = 1)
        val helipad = level.helipads.single().position
        engine.place(listOf(Aircraft(1, AircraftType.Helicopter, helipad + Vec2(0.0, -200.0), PI)))
        engine.beginPath(1)
        engine.extendPath(1, helipad)
        engine.extendPath(1, helipad + Vec2(0.0, 200.0))
        val aircraft = engine.aircraft(1)
        assertIs<AircraftStatus.ClearedToLand>(aircraft.status)
        assertTrue(aircraft.path.locked)
        assertTrue(aircraft.path.points.last().y < helipad.y)
    }

    private fun jet(id: Int, position: Vec2) = Aircraft(id, AircraftType.Jet, position, 0.0)

    private fun GameEngine.aircraft(id: Int) = assertNotNull(state.value.aircraft.firstOrNull { it.id == id })
}
