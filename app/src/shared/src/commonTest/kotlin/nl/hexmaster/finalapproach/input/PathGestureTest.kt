package nl.hexmaster.finalapproach.input

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.FlightPath
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.engine.GameEngine
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PathGestureTest {

    private val level = Levels.meadowField.let { it.copy(spawn = it.spawn.copy(firstSpawnDelay = 10_000.0)) }
    private val oldPath = FlightPath(listOf(Vec2(300.0, 150.0), Vec2(400.0, 100.0)))
    private val start = Vec2(200.0, 150.0)

    private fun engineWithPath(): GameEngine = GameEngine(level, seed = 1).apply {
        place(listOf(Aircraft(1, AircraftType.LightProp, start, PI / 2, oldPath)))
    }

    private fun GameEngine.plane() = state.value.aircraft.single()

    @Test
    fun touchingAnAircraftHidesItsPathAtOnce() {
        val engine = engineWithPath()
        val gesture = PathGesture(engine)
        assertTrue(gesture.down(start, 30.0))
        assertTrue(engine.plane().path.isEmpty)
    }

    @Test
    fun aTapRemovesThePathAndKeepsTheHeading() {
        val engine = engineWithPath()
        val gesture = PathGesture(engine)
        gesture.down(start, 30.0)
        gesture.move(start + Vec2(1.0, 0.0), pastSlop = false)
        gesture.up()
        assertTrue(engine.plane().path.isEmpty)
        engine.update(1.0)
        assertEquals(PI / 2, engine.plane().heading, 1e-12)
    }

    @Test
    fun aDragDrawsANewPathThatReplacesTheOldOne() {
        val engine = engineWithPath()
        val gesture = PathGesture(engine)
        gesture.down(start, 30.0)
        val drawn = listOf(Vec2(200.0, 200.0), Vec2(200.0, 260.0), Vec2(250.0, 300.0))
        drawn.forEach { gesture.move(it, pastSlop = true) }
        gesture.up()
        val path = engine.plane().path.points
        assertFalse(path.any { it in oldPath.points })
        assertEquals(drawn.last(), path.last())
    }

    @Test
    fun touchingEmptySkyChangesNothing() {
        val engine = engineWithPath()
        val gesture = PathGesture(engine)
        assertFalse(gesture.down(Vec2(900.0, 600.0), 30.0))
        gesture.move(Vec2(950.0, 600.0), pastSlop = true)
        gesture.up()
        assertEquals(oldPath, engine.plane().path)
    }
}
