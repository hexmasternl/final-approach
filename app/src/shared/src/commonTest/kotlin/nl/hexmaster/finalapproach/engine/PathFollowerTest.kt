package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftTuning
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.FlightPath
import nl.hexmaster.finalapproach.domain.Overlap
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
        assertEquals(fly(a, 50).map { it.position }, fly(b, 50).map { it.position })
    }

    @Test
    fun equalDistanceInEqualStepsStraightOnAfterThePath() {
        val start = Aircraft(1, AircraftType.Turboprop, Vec2.Zero, PI / 2, FlightPath(listOf(Vec2(30.0, 0.0))))
        val expected = specs.speedOf(AircraftType.Turboprop) * dt
        fly(start, 40).map { it.position }.zipWithNext().forEach { (from, to) ->
            assertTrue(abs(from.distanceTo(to) - expected) < 1e-9, "step ${from.distanceTo(to)}")
        }
    }

    @Test
    fun pathIsConsumedAndTheLastHeadingIsKept() {
        val start = Aircraft(1, AircraftType.LightProp, Vec2.Zero, 0.0, FlightPath(listOf(Vec2(0.0, -60.0), Vec2(300.0, -60.0))))
        val states = fly(start, 120)
        val emptied = states.indexOfFirst { it.path.isEmpty }
        assertTrue(emptied > 0, "path never consumed")
        val after = states.drop(emptied)
        assertTrue(abs(after.first().heading - PI / 2) < 1e-9, "heading ${after.first().heading}")
        after.forEach { assertEquals(after.first().heading, it.heading) }
    }

    @Test
    fun everyTypeStaysOnThePathThroughAHairpinAndFliesStraightOnAfter() {
        val corner = Vec2(300.0, 0.0)
        val back = Angles.direction(PI / 2 + Angles.degrees(150.0))
        val path = listOf(corner, corner + back * 300.0)
        val line = listOf(Vec2.Zero) + path
        AircraftType.entries.forEach { type ->
            var aircraft = Aircraft(1, type, Vec2.Zero, PI / 2, FlightPath(path))
            var steps = 0
            while (!aircraft.path.isEmpty && steps < 2000) {
                aircraft = PathFollower.step(aircraft, specs, dt)
                val off = line.zipWithNext().minOf { (a, b) -> Overlap.closestPointOnSegment(a, b, aircraft.position).distanceTo(aircraft.position) }
                if (!aircraft.path.isEmpty) assertTrue(off < 1e-6, "$type strayed $off")
                steps++
            }
            assertTrue(aircraft.path.isEmpty, "$type never finished")
            val last = Angles.heading(path.last() - corner)
            assertTrue(abs(Angles.difference(aircraft.heading, last)) < 1e-9, "$type heading ${aircraft.heading} vs $last")
            val after = PathFollower.step(aircraft, specs, dt)
            assertEquals(aircraft.heading, after.heading)
            assertTrue(abs(aircraft.position.distanceTo(after.position) - specs[type].speed * dt) < 1e-9, "$type step")
        }
    }

    @Test
    fun aStepThatCrossesSeveralPointsStillCoversTheFullDistance() {
        val start = Aircraft(1, AircraftType.Jumbo, Vec2.Zero, 0.0, FlightPath(listOf(Vec2(0.0, 1.0), Vec2(1.0, 1.0), Vec2(1.0, 2.0))))
        val moved = PathFollower.step(start, specs, 1.0)
        assertTrue(moved.path.isEmpty)
        assertEquals(Angles.heading(Vec2(0.0, 1.0)), moved.heading)
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
