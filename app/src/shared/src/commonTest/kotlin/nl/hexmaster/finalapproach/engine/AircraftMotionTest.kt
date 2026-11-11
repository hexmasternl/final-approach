package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.LandingZones
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AircraftMotionTest {

    private val level = Levels.meadowField
    private val zones = LandingZones.of(level)
    private val specs = EffectiveAircraftSpecs.Defaults
    private val dt = 0.05

    private fun cleared(type: AircraftType, start: Vec2, heading: Double, vararg points: Vec2): Aircraft =
        points.fold(PathDrawing.begin(Aircraft(1, type, start, heading))) { a, p ->
            PathDrawing.extend(a, p, zones, level.landing, 0.0)
        }

    /** Steps until the aircraft is removed; returns its states and every event. */
    private fun runOut(aircraft: Aircraft, maxSteps: Int = 10_000): Pair<List<Aircraft>, List<GameEvent>> {
        val states = mutableListOf(aircraft)
        val events = mutableListOf<GameEvent>()
        repeat(maxSteps) {
            val result = AircraftMotion.step(states.last(), specs, level.landing, dt)
            events += result.events
            states += result.aircraft ?: return states to events
        }
        error("aircraft was never removed")
    }

    @Test
    fun helicopterLandsAtThePathEndAndIsRemoved() {
        val heli = cleared(AircraftType.Helicopter, Vec2(900.0, 400.0), PI / 2, Vec2(1200.0, 400.0))
        assertIs<AircraftStatus.ClearedToLand>(heli.status)
        val (states, events) = runOut(heli)
        assertEquals(listOf(GameEvent.Landed(1, AircraftType.Helicopter)), events)
        assertTrue(states.none { it.status is AircraftStatus.RollingOut })
    }

    @Test
    fun airplaneRollsTwoThirdsOfTheRunwayThenFadesAndIsRemoved() {
        val plane = cleared(AircraftType.Jet, Vec2(150.0, 400.0), PI / 2, Vec2(400.0, 400.0))
        val (states, events) = runOut(plane)
        assertEquals(listOf(GameEvent.Landed(1, AircraftType.Jet)), events)
        val rolling = states.filter { it.status is AircraftStatus.RollingOut }
        val touchdown = rolling.first().position
        val stopped = rolling.last().position
        assertTrue(abs(touchdown.x - 320.0) < 1e-6, "touchdown $touchdown")
        assertTrue(abs(stopped.distanceTo(touchdown) - 640.0 * 2.0 / 3.0) < 1e-6, "rolled ${stopped.distanceTo(touchdown)}")
        assertTrue(stopped.x > touchdown.x)
        val fading = rolling.count { (it.status as AircraftStatus.RollingOut).fadeElapsed > 0.0 }
        assertTrue(fading * dt <= level.landing.fadeSeconds + dt, "fade steps $fading")
    }

    @Test
    fun eachEndRollsOutInItsOwnDirection() {
        val fromWest = cleared(AircraftType.LightProp, Vec2(150.0, 400.0), PI / 2, Vec2(400.0, 400.0))
        val fromEast = cleared(AircraftType.LightProp, Vec2(1130.0, 400.0), -PI / 2, Vec2(880.0, 400.0))
        val westRoll = runOut(fromWest).first.first { it.status is AircraftStatus.RollingOut }
        val eastRoll = runOut(fromEast).first.first { it.status is AircraftStatus.RollingOut }
        assertTrue(abs(Angles.difference(westRoll.heading, PI / 2)) < 1e-9)
        assertTrue(abs(Angles.difference(eastRoll.heading, -PI / 2)) < 1e-9)
    }

    @Test
    fun opposingLandingsOnATwoWayRunwayCollideMidRunway() {
        var west: Aircraft? = cleared(AircraftType.Jet, Vec2(200.0, 400.0), PI / 2, Vec2(400.0, 400.0)).copy(id = 1)
        var east: Aircraft? = cleared(AircraftType.Jet, Vec2(1080.0, 400.0), -PI / 2, Vec2(880.0, 400.0)).copy(id = 2)
        var collision: List<Pair<Int, Int>> = emptyList()
        repeat(2000) {
            if (collision.isNotEmpty()) return@repeat
            west = west?.let { AircraftMotion.step(it, specs, level.landing, dt).aircraft }
            east = east?.let { AircraftMotion.step(it, specs, level.landing, dt).aircraft }
            collision = Collisions.find(listOfNotNull(west, east), level.world.minimumSeparation)
        }
        assertEquals(listOf(1 to 2), collision)
        assertIs<AircraftStatus.RollingOut>(assertNotNull(west).status)
        assertIs<AircraftStatus.RollingOut>(assertNotNull(east).status)
    }

    @Test
    fun removedAircraftAreNotInTheCollisionCheck() {
        val a = Aircraft(1, AircraftType.Jet, Vec2(100.0, 100.0), 0.0)
        assertNull(Collisions.find(listOf(a), 80.0).firstOrNull())
    }
}
