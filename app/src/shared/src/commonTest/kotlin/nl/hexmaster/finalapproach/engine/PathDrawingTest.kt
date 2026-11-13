package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.LandingZones
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.domain.meadow
import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PathDrawingTest {

    private val level = twoWayMeadowField
    private val zones = LandingZones.of(level)

    private fun draw(aircraft: Aircraft, vararg points: Vec2): Aircraft =
        points.fold(PathDrawing.begin(aircraft)) { a, p -> PathDrawing.extend(a, p, zones, level.landing, 5.0) }

    @Test
    fun pathEndsAtTheZoneAndIsLocked() {
        val plane = Aircraft(1, AircraftType.LightProp, meadow(100.0, 400.0), PI / 2)
        val drawn = draw(plane, meadow(200.0, 400.0), meadow(400.0, 400.0))
        val cleared = assertIs<AircraftStatus.ClearedToLand>(drawn.status)
        assertIs<LandingZone.Runway>(cleared.zone)
        assertEquals(5.0, drawn.path.lockedAt)
        assertTrue(drawn.path.points.last().distanceTo(cleared.zone.let { it as LandingZone.Runway }.centreLinePoint(level.landing.touchdownDistance)) < 1e-6)
    }

    @Test
    fun anOffCentreEntryEndsOnTheCentreLine() {
        val plane = Aircraft(1, AircraftType.LightProp, meadow(60.0, 440.0), PI / 2)
        val drawn = draw(plane, meadow(200.0, 440.0), meadow(400.0, 440.0))
        assertIs<AircraftStatus.ClearedToLand>(drawn.status)
        val points = drawn.path.points
        assertTrue(abs(points.last().y - level.runways.single().position.y) < 1e-6, "${points.last()}")
        val lastHeading = Angles.heading(points.last() - points[points.size - 2])
        assertTrue(abs(Angles.difference(lastHeading, PI / 2)) < 1e-6)
    }

    @Test
    fun aTwoWayRunwayUsesTheEnteredEnd() {
        val plane = Aircraft(1, AircraftType.LightProp, meadow(1220.0, 380.0), -PI / 2)
        val drawn = draw(plane, meadow(1100.0, 380.0), meadow(900.0, 380.0))
        val zone = assertIs<LandingZone.Runway>(assertIs<AircraftStatus.ClearedToLand>(drawn.status).zone)
        assertTrue(abs(Angles.difference(zone.direction, -PI / 2)) < 1e-9)
        assertTrue(drawn.path.points.last().distanceTo(zone.centreLinePoint(level.landing.touchdownDistance)) < 1e-6)
    }

    @Test
    fun misalignedAndMidRunwayCrossingsAreNotSnapped() {
        val plane = Aircraft(1, AircraftType.LightProp, meadow(360.0, 150.0), PI)
        val across = draw(plane, meadow(360.0, 300.0), meadow(360.0, 600.0))
        assertEquals(AircraftStatus.Flying, across.status)
        assertEquals(listOf(meadow(360.0, 300.0), meadow(360.0, 600.0)), across.path.points)
        val mid = draw(plane.copy(position = meadow(640.0, 150.0)), meadow(640.0, 300.0), meadow(640.0, 600.0))
        assertEquals(AircraftStatus.Flying, mid.status)
    }

    @Test
    fun aHelipadPathStillEndsAtTheEntryPoint() {
        val heli = Aircraft(1, AircraftType.Helicopter, meadow(900.0, 400.0), PI / 2)
        val drawn = draw(heli, meadow(1000.0, 400.0), meadow(1200.0, 400.0))
        val zone = assertIs<LandingZone.Helipad>(assertIs<AircraftStatus.ClearedToLand>(drawn.status).zone)
        val last = drawn.path.points.last()
        assertTrue(abs(last.distanceTo(zone.area.center) - zone.area.radius) < 1e-6, "$last")
    }

    @Test
    fun laterPointsAreIgnored() {
        val heli = Aircraft(1, AircraftType.Helicopter, meadow(900.0, 400.0), PI / 2)
        val locked = draw(heli, meadow(1000.0, 400.0), meadow(1200.0, 400.0))
        assertTrue(locked.path.locked)
        val drawn = listOf(meadow(1200.0, 100.0), meadow(50.0, 50.0))
            .fold(locked) { a, p -> PathDrawing.extend(a, p, zones, level.landing, 6.0) }
        assertEquals(locked, drawn)
        assertEquals(drawn, PathDrawing.finish(drawn, EffectiveAircraftSpecs.Defaults, EngineConfig()))
    }

    @Test
    fun aPathThatMissesKeepsEveryPoint() {
        val heli = Aircraft(1, AircraftType.Helicopter, meadow(900.0, 100.0), PI / 2)
        val drawn = draw(heli, meadow(1000.0, 100.0), meadow(1200.0, 100.0))
        assertEquals(AircraftStatus.Flying, drawn.status)
        assertEquals(listOf(meadow(1000.0, 100.0), meadow(1200.0, 100.0)), drawn.path.points)
    }

    @Test
    fun beginningAPathCancelsTheClearanceOfAnAirplaneAndAHelicopter() {
        val plane = draw(Aircraft(1, AircraftType.LightProp, meadow(100.0, 400.0), PI / 2), meadow(200.0, 400.0), meadow(400.0, 400.0))
        val heli = draw(Aircraft(2, AircraftType.Helicopter, meadow(900.0, 400.0), PI / 2), meadow(1000.0, 400.0), meadow(1200.0, 400.0))
        listOf(plane, heli).forEach { cleared ->
            assertIs<AircraftStatus.ClearedToLand>(cleared.status)
            val again = PathDrawing.begin(cleared)
            assertEquals(AircraftStatus.Flying, again.status)
            assertTrue(again.path.isEmpty)
            assertTrue(!again.path.locked)
            assertEquals(cleared.position, again.position)
            assertEquals(cleared.heading, again.heading)
        }
    }

    @Test
    fun aRollingOutAirplaneCannotBeReRouted() {
        val rolling = Aircraft(1, AircraftType.LightProp, meadow(100.0, 400.0), PI / 2)
            .copy(status = AircraftStatus.RollingOut(0.0, 500.0, "r"))
        assertEquals(rolling, PathDrawing.begin(rolling))
    }

    @Test
    fun aReRoutedPathIntoAZoneClearsTheAircraftAgain() {
        val plane = Aircraft(1, AircraftType.LightProp, meadow(100.0, 400.0), PI / 2)
        val cleared = draw(plane, meadow(200.0, 400.0), meadow(400.0, 400.0))
        val again = draw(cleared, meadow(200.0, 400.0), meadow(400.0, 400.0))
        assertIs<AircraftStatus.ClearedToLand>(again.status)
        assertTrue(again.path.locked)
    }

    @Test
    fun aFirstPointBehindTheAircraftIsDroppedNotFlownBackTo() {
        val plane = Aircraft(1, AircraftType.Jet, meadow(300.0, 100.0), PI / 2)
        val drawn = draw(plane, meadow(270.0, 100.0), meadow(500.0, 100.0), meadow(700.0, 100.0))
        val finished = PathDrawing.finish(drawn, EffectiveAircraftSpecs.Defaults, EngineConfig())
        assertTrue(finished.path.points.all { it.x > plane.position.x }, "${finished.path.points}")
        assertEquals(meadow(700.0, 100.0), finished.path.points.last())
    }
}
