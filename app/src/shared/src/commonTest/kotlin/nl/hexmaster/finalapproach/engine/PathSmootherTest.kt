package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.FlightPath
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.LandingZones
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Overlap
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PathSmootherTest {

    private val specs = EffectiveAircraftSpecs.Defaults
    private val config = EngineConfig()
    private val east = PI / 2

    private fun radiusOf(type: AircraftType) = specs[type].speed / specs[type].turnRate

    private fun smooth(type: AircraftType, start: Vec2, heading: Double, points: List<Vec2>) =
        PathSmoother.smooth(start, heading, points, specs[type].speed, specs[type].turnRate, config)

    /** A drawn path east from the origin, then a 150 degree turn back, as dense drag points. */
    private fun hairpin(): List<Vec2> {
        val corner = Vec2(400.0, 300.0)
        val back = Angles.direction(east + Angles.degrees(150.0))
        return (1..40).map { Vec2(it * 10.0, 300.0) } + (1..40).map { corner + back * (it * 10.0) }
    }

    /** Tightest corner along [line], ignoring the final point that is pinned where it was drawn. */
    private fun tightest(line: List<Vec2>): Double =
        line.dropLast(1).windowed(3).minOf { (a, b, c) -> PathSmoother.circumradius(a, b, c) }

    private fun distanceToLine(point: Vec2, line: List<Vec2>): Double =
        line.zipWithNext().minOf { (a, b) -> Overlap.closestPointOnSegment(a, b, point).distanceTo(point) }

    @Test
    fun aSharpCornerBecomesACurveEveryTypeCanFly() {
        AircraftType.entries.forEach { type ->
            val start = Vec2(0.0, 300.0)
            val smoothed = smooth(type, start, east, hairpin())
            val tightest = tightest(listOf(start) + smoothed)
            assertTrue(tightest >= radiusOf(type), "$type: tightest corner $tightest < ${radiusOf(type)}")
        }
    }

    @Test
    fun aGentlePathStaysCloseToTheDrawnLine() {
        val center = Vec2(0.0, 1000.0)
        val drawn = (0..60).map { val a = it * 0.01; center + Vec2(sin(a), -cos(a)) * 1000.0 }
        listOf(AircraftType.LightProp, AircraftType.Jet).forEach { type ->
            val smoothed = smooth(type, drawn.first(), east, drawn.drop(1))
            smoothed.forEach { point ->
                val off = distanceToLine(point, drawn)
                assertTrue(off <= 2 * config.smoothing.tolerance, "$type: $off off the drawn line")
            }
        }
    }

    @Test
    fun theEndPointStaysWhereItWasDrawn() {
        AircraftType.entries.forEach { type ->
            val drawn = hairpin()
            val smoothed = smooth(type, Vec2(0.0, 300.0), east, drawn)
            assertEquals(drawn.last(), smoothed.last())
        }
    }

    @Test
    fun aFinishedZigZagIsStoredSmoothed() {
        val level = Levels.meadowField.let { it.copy(spawn = it.spawn.copy(firstSpawnDelay = 10_000.0)) }
        val engine = GameEngine(level, seed = 1)
        val start = Vec2(200.0, 150.0)
        engine.place(listOf(Aircraft(1, AircraftType.Jet, start, east)))
        val zigZag = (1..12).map { Vec2(200.0 + it * 40.0, if (it % 2 == 0) 150.0 else 190.0) }
        engine.beginPath(1)
        zigZag.forEach { engine.extendPath(1, it) }
        engine.endPath(1)
        val stored = engine.state.value.aircraft.single().path.points
        assertNotEquals(zigZag, stored)
        assertEquals(zigZag.last(), stored.last())
        assertTrue(tightest(listOf(start) + stored) >= radiusOf(AircraftType.Jet))
    }

    @Test
    fun smoothedCurvedPathsStillLand() {
        val level = Levels.meadowField.let { it.copy(spawn = it.spawn.copy(firstSpawnDelay = 10_000.0)) }
        val pad = LandingZones.of(level).filterIsInstance<LandingZone.Helipad>().first().area.center
        val cases = listOf(
            Aircraft(1, AircraftType.LightProp, Vec2(100.0, 220.0), east) to
                listOf(Vec2(160.0, 240.0), Vec2(220.0, 290.0), Vec2(250.0, 360.0), Vec2(280.0, 400.0), Vec2(420.0, 400.0)),
            Aircraft(1, AircraftType.Helicopter, pad + Vec2(-200.0, -150.0), east) to
                listOf(pad + Vec2(-120.0, -160.0), pad + Vec2(-40.0, -100.0), pad + Vec2(-10.0, -30.0), pad),
        )
        cases.forEach { (aircraft, drawn) ->
            val engine = GameEngine(level, seed = 1)
            engine.place(listOf(aircraft))
            engine.beginPath(1)
            drawn.forEach { engine.extendPath(1, it) }
            engine.endPath(1)
            assertIs<AircraftStatus.ClearedToLand>(engine.state.value.aircraft.single().status, "${aircraft.type}")
            var landed = false
            repeat(400) { if (!landed) engine.update(0.05).also { landed = engine.state.value.landings > 0 } }
            assertTrue(landed, "${aircraft.type} never landed")
            assertEquals(GameStatus.Playing, engine.state.value.status)
        }
    }

    @Test
    fun aHairpinIsFollowedWithoutLoopingAndStaysOnTheLine() {
        val dt = 0.05
        AircraftType.entries.forEach { type ->
            val start = Vec2(0.0, 300.0)
            val smoothed = smooth(type, start, east, hairpin())
            var aircraft = Aircraft(1, type, start, east, FlightPath(smoothed))
            var turned = 0.0
            var worst = 0.0
            var steps = 0
            while (!aircraft.path.isEmpty && steps < 2000) {
                val next = PathFollower.step(aircraft, specs, dt)
                turned += abs(Angles.difference(aircraft.heading, next.heading))
                worst = maxOf(worst, distanceToLine(next.position, listOf(start) + smoothed))
                aircraft = next
                steps++
            }
            assertTrue(aircraft.path.isEmpty, "$type never finished the path")
            // The 150 degree turn plus easing in and out; one full circle more would be a loop.
            assertTrue(turned < 2 * PI, "$type turned $turned rad, it looped")
            assertTrue(worst <= config.smoothing.tolerance * 2, "$type strayed $worst from the line")
        }
    }
}
