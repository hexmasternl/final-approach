package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.AircraftTuning
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.FlightPath
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PathFollowerTest {

    private val specs = EffectiveAircraftSpecs.Defaults
    private val dt = 0.1

    private fun fly(aircraft: Aircraft, steps: Int, effective: EffectiveAircraftSpecs = specs): List<Aircraft> {
        val states = mutableListOf(aircraft)
        repeat(steps) { states += PathFollower.step(states.last(), effective, dt) }
        return states
    }

    @Test
    fun twoAircraftOfOneTypeFlyExactlyAlike() {
        val path = FlightPath(listOf(Vec2(100.0, 0.0), Vec2(100.0, 100.0)))
        val a = Aircraft(1, AircraftType.Jet, Vec2.Zero, PI / 2, path)
        val b = Aircraft(2, AircraftType.Jet, Vec2.Zero, PI / 2, path, livery = 3)
        val flownA = fly(a, 50).map { it.position }
        val flownB = fly(b, 50).map { it.position }
        assertEquals(flownA, flownB)
    }

    @Test
    fun equalDistanceInEqualStepsStraightOnAfterThePath() {
        val start = Aircraft(1, AircraftType.Turboprop, Vec2.Zero, PI / 2, FlightPath(listOf(Vec2(30.0, 0.0))))
        val positions = fly(start, 40).map { it.position }
        val expected = specs.speedOf(AircraftType.Turboprop) * dt
        positions.zipWithNext().forEach { (from, to) ->
            assertTrue(abs(from.distanceTo(to) - expected) < 1e-9, "step ${from.distanceTo(to)}")
        }
    }

    @Test
    fun pathIsConsumedAndTheLastHeadingIsKept() {
        val start = Aircraft(1, AircraftType.LightProp, Vec2.Zero, 0.0, FlightPath(listOf(Vec2(0.0, -20.0), Vec2(20.0, -20.0))))
        val end = fly(start, 30).last()
        assertTrue(end.path.isEmpty)
        assertTrue(abs(end.heading - PI / 2) < 1e-9)
        assertTrue(end.position.x > 20.0)
    }

    @Test
    fun speedComesFromTheEffectiveSpecs() {
        val fast = AircraftTuning(speedMultiplier = 2.0).resolve()
        val start = Aircraft(1, AircraftType.Helicopter, Vec2.Zero, PI / 2)
        val normal = PathFollower.step(start, specs, 1.0).position.x
        val doubled = PathFollower.step(start, fast, 1.0).position.x
        assertEquals(normal * 2.0, doubled)
    }
}
