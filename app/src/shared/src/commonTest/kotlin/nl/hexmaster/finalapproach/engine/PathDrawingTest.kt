package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.LandingZones
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PathDrawingTest {

    private val level = Levels.meadowField
    private val zones = LandingZones.of(level)

    private fun draw(aircraft: Aircraft, vararg points: Vec2): Aircraft =
        points.fold(PathDrawing.begin(aircraft)) { a, p -> PathDrawing.extend(a, p, zones, level.landing, 5.0) }

    @Test
    fun pathEndsAtTheZoneAndIsLocked() {
        val plane = Aircraft(1, AircraftType.LightProp, Vec2(100.0, 400.0), PI / 2)
        val drawn = draw(plane, Vec2(200.0, 400.0), Vec2(400.0, 400.0))
        val cleared = assertIs<AircraftStatus.ClearedToLand>(drawn.status)
        assertIs<LandingZone.Runway>(cleared.zone)
        assertEquals(5.0, drawn.path.lockedAt)
        assertTrue(kotlin.math.abs(drawn.path.points.last().x - 320.0) < 1e-6)
    }

    @Test
    fun laterPointsAreIgnored() {
        val heli = Aircraft(1, AircraftType.Helicopter, Vec2(900.0, 400.0), PI / 2)
        val locked = draw(heli, Vec2(1000.0, 400.0), Vec2(1200.0, 400.0))
        assertTrue(locked.path.locked)
        val drawn = listOf(Vec2(1200.0, 100.0), Vec2(50.0, 50.0))
            .fold(locked) { a, p -> PathDrawing.extend(a, p, zones, level.landing, 6.0) }
        assertEquals(locked, drawn)
        assertEquals(drawn, PathDrawing.begin(drawn))
    }

    @Test
    fun aPathThatMissesKeepsEveryPoint() {
        val heli = Aircraft(1, AircraftType.Helicopter, Vec2(900.0, 100.0), PI / 2)
        val drawn = draw(heli, Vec2(1000.0, 100.0), Vec2(1200.0, 100.0))
        assertEquals(AircraftStatus.Flying, drawn.status)
        assertEquals(listOf(Vec2(1000.0, 100.0), Vec2(1200.0, 100.0)), drawn.path.points)
    }
}
