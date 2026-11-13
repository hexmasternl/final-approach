package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftKind
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.LandingConfig
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.LandingZones
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.domain.meadow
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AircraftMotionTest {

    private val level = twoWayMeadowField
    private val runway = level.runways.single()
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
        val heli = cleared(AircraftType.Helicopter, meadow(900.0, 400.0), PI / 2, meadow(1200.0, 400.0))
        assertIs<AircraftStatus.ClearedToLand>(heli.status)
        val (states, events) = runOut(heli)
        assertEquals(listOf(GameEvent.Landed(1, AircraftType.Helicopter)), events)
        assertTrue(states.none { it.status is AircraftStatus.RollingOut })
    }

    @Test
    fun anOffCentreLandingTouchesDownAndRollsOutOnTheCentreLine() {
        AircraftType.entries.filter { it.kind == AircraftKind.Airplane }.forEach { type ->
            val plane = cleared(type, meadow(-400.0, 440.0), PI / 2, meadow(0.0, 440.0), meadow(400.0, 440.0))
            val zone = assertIs<LandingZone.Runway>(assertIs<AircraftStatus.ClearedToLand>(plane.status).zone)
            val rolling = runOut(plane).first.filter { it.status is AircraftStatus.RollingOut }
            val touchdown = rolling.first().position
            val along = zone.distanceAlong(touchdown)
            assertTrue(along >= 0.0 && along <= zone.area.length, "$type touched down $along past the threshold")
            rolling.forEach { assertTrue(abs(it.position.y - runway.position.y) < 1e-6, "$type rolled at ${it.position}") }
        }
    }

    @Test
    fun airplaneRollsTwoThirdsOfTheRunwayThenFadesAndIsRemoved() {
        val plane = cleared(AircraftType.Jet, meadow(150.0, 400.0), PI / 2, meadow(400.0, 400.0))
        val (states, events) = runOut(plane)
        assertEquals(listOf(GameEvent.Landed(1, AircraftType.Jet)), events)
        val rolling = states.filter { it.status is AircraftStatus.RollingOut }
        val touchdown = rolling.first().position
        val stopped = rolling.last().position
        assertTrue(abs(touchdown.x - (runway.position.x - runway.length / 2.0 + level.landing.touchdownDistance)) < 1e-6, "touchdown $touchdown")
        assertTrue(abs(stopped.distanceTo(touchdown) - runway.length * 2.0 / 3.0) < 1e-6, "rolled ${stopped.distanceTo(touchdown)}")
        assertTrue(stopped.x > touchdown.x)
        val fading = rolling.count { (it.status as AircraftStatus.RollingOut).fadeElapsed > 0.0 }
        assertTrue(fading * dt <= level.landing.fadeSeconds + dt, "fade steps $fading")
    }

    @Test
    fun eachEndRollsOutInItsOwnDirection() {
        val fromWest = cleared(AircraftType.LightProp, meadow(150.0, 400.0), PI / 2, meadow(400.0, 400.0))
        val fromEast = cleared(AircraftType.LightProp, meadow(1130.0, 400.0), -PI / 2, meadow(880.0, 400.0))
        val westRoll = runOut(fromWest).first.first { it.status is AircraftStatus.RollingOut }
        val eastRoll = runOut(fromEast).first.first { it.status is AircraftStatus.RollingOut }
        assertTrue(abs(Angles.difference(westRoll.heading, PI / 2)) < 1e-9)
        assertTrue(abs(Angles.difference(eastRoll.heading, -PI / 2)) < 1e-9)
    }

    @Test
    fun opposingLandingsOnATwoWayRunwayCollideMidRunway() {
        var west: Aircraft? = cleared(AircraftType.Jet, meadow(200.0, 400.0), PI / 2, meadow(400.0, 400.0)).copy(id = 1)
        var east: Aircraft? = cleared(AircraftType.Jet, meadow(1080.0, 400.0), -PI / 2, meadow(880.0, 400.0)).copy(id = 2)
        var collision: List<Pair<Int, Int>> = emptyList()
        repeat(2000) {
            if (collision.isNotEmpty()) return@repeat
            val previous = listOfNotNull(west, east).associate { it.id to it.position }
            west = west?.let { AircraftMotion.step(it, specs, level.landing, dt).aircraft }
            east = east?.let { AircraftMotion.step(it, specs, level.landing, dt).aircraft }
            collision = Collisions.find(listOfNotNull(west, east), specs, previous)
        }
        assertEquals(listOf(1 to 2), collision)
        assertIs<AircraftStatus.RollingOut>(assertNotNull(west).status)
        assertIs<AircraftStatus.RollingOut>(assertNotNull(east).status)
    }

    @Test
    fun removedAircraftAreNotInTheCollisionCheck() {
        val a = Aircraft(1, AircraftType.Jet, Vec2(100.0, 100.0), 0.0)
        assertNull(Collisions.find(listOf(a), specs).firstOrNull())
    }

    private fun touchedDown(type: AircraftType, remaining: Double = 10_000.0): Aircraft =
        Aircraft(1, type, Vec2(0.0, 0.0), PI / 2, status = AircraftStatus.RollingOut(PI / 2, remaining, "r"))

    /** The speed of [aircraft] over a tiny step, and the aircraft after it. */
    private fun speedAt(aircraft: Aircraft, landing: LandingConfig = level.landing): Double {
        val tiny = 1e-4
        val next = assertNotNull(AircraftMotion.step(aircraft, specs, landing, tiny).aircraft)
        return next.position.distanceTo(aircraft.position) / tiny
    }

    /** Rolls [aircraft] for [seconds] in steps of [step]. */
    private fun roll(aircraft: Aircraft, seconds: Double, step: Double, landing: LandingConfig = level.landing): Aircraft =
        (1..(seconds / step).roundToInt()).fold(aircraft) { a, _ ->
            assertNotNull(AircraftMotion.step(a, specs, landing, step).aircraft)
        }

    @Test
    fun theLightPropStartsAtItsFlightSpeedAndSlowsToHalfOfItWithinTheDecelerationTime() {
        val prop = touchedDown(AircraftType.LightProp)
        val speed = specs[AircraftType.LightProp].speed
        val decel = level.landing.rolloutDecelerationSeconds
        assertEquals(speed, speedAt(prop), 0.1)
        assertEquals(speed * 0.5, speedAt(roll(prop, decel, dt)), 1e-3)
        assertEquals(speed * 0.5, speedAt(roll(prop, 3.0, dt)), 1e-3)
    }

    @Test
    fun everyAirplaneSlowsDownToHalfTheLightPropSpeedWithinTheDecelerationTime() {
        val landed = specs[AircraftType.LightProp].speed * 0.5
        listOf(AircraftType.Turboprop, AircraftType.Jet, AircraftType.Jumbo).forEach { type ->
            val plane = touchedDown(type)
            assertEquals(specs[type].speed, speedAt(plane), 0.1, "$type at touchdown")
            val half = speedAt(roll(plane, level.landing.rolloutDecelerationSeconds / 2.0, dt))
            assertTrue(half < specs[type].speed && half > landed, "$type halfway at $half")
            assertEquals(landed, speedAt(roll(plane, level.landing.rolloutDecelerationSeconds, dt)), 1e-3, "$type after the slowdown")
            assertEquals(landed, speedAt(roll(plane, 3.0, dt)), 1e-3, "$type later")
        }
    }

    @Test
    fun theLandingSpeedFollowsTheConfiguredReferenceType() {
        val landing = level.landing.copy(landingSpeedReference = AircraftType.Turboprop)
        val jet = roll(touchedDown(AircraftType.Jet), 2.0, dt, landing)
        assertEquals(specs[AircraftType.Turboprop].speed * 0.5, speedAt(jet, landing), 1e-3)
    }

    @Test
    fun aFactorOfOneGivesTheFullReferenceSpeed() {
        val landing = level.landing.copy(landingSpeedFactor = 1.0)
        val speed = specs[AircraftType.LightProp].speed
        assertEquals(speed, speedAt(roll(touchedDown(AircraftType.LightProp), 2.0, dt, landing), landing), 1e-3)
        assertEquals(speed, speedAt(roll(touchedDown(AircraftType.Jet), 2.0, dt, landing), landing), 1e-3)
    }

    @Test
    fun theRolloutStillCoversTwoThirdsOfTheRunwayBeforeTheFade() {
        val zone = zones.filterIsInstance<LandingZone.Runway>().first { it.runwayId == runway.id }
        val plane = Aircraft(1, AircraftType.Jet, Vec2(0.0, 0.0), PI / 2, status = AircraftStatus.RollingOut(PI / 2, zone.rolloutDistance, runway.id))
        val (states, _) = runOut(plane)
        val rolled = states.last().position.distanceTo(plane.position)
        assertEquals(runway.length * level.landing.rolloutFraction, rolled, 1e-6)
    }

    @Test
    fun theRolledDistanceDoesNotDependOnTheStepSize() {
        val jumbo = touchedDown(AircraftType.Jumbo)
        val big = assertNotNull(AircraftMotion.step(jumbo, specs, level.landing, 1.5).aircraft)
        val small = roll(jumbo, 1.5, 0.01)
        assertTrue(big.position.distanceTo(small.position) < 1e-6, "${big.position} vs ${small.position}")
    }

    @Test
    fun aRolloutThatEndsDuringTheSlowdownStillFadesOut() {
        val (states, events) = runOut(touchedDown(AircraftType.Jumbo, remaining = 30.0))
        assertTrue(events.isEmpty())
        val last = assertIs<AircraftStatus.RollingOut>(states.last().status)
        assertEquals(0.0, last.remaining)
        assertTrue(last.elapsed < level.landing.rolloutDecelerationSeconds)
        assertTrue(states.any { (it.status as AircraftStatus.RollingOut).fadeElapsed > 0.0 })
    }
}
