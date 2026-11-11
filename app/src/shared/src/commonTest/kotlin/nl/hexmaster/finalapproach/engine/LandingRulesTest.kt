package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.AircraftKind
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.HelipadConfig
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.LandingZones
import nl.hexmaster.finalapproach.domain.LevelDefaults
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.RunwayConfig
import nl.hexmaster.finalapproach.domain.RunwayMode
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LandingRulesTest {

    private val east = PI / 2
    private val tolerance = LevelDefaults.LANDING_TOLERANCE

    // Runway from x 320 to 960 at y 400; helipad at 1110, 400.
    private fun zones(mode: RunwayMode = RunwayMode.TwoWay): List<LandingZone> = LandingZones.of(
        Levels.meadowField.copy(
            runways = listOf(RunwayConfig("r", Vec2(640.0, 400.0), east, 640.0, mode = mode)),
            helipads = listOf(HelipadConfig("h", Vec2(1110.0, 400.0))),
        ),
    )

    private fun check(from: Vec2, to: Vec2, kind: AircraftKind, mode: RunwayMode = RunwayMode.TwoWay) =
        LandingRules.check(from, to, kind, zones(mode), tolerance)

    @Test
    fun helicopterPathThroughThePadLands() {
        val hit = assertNotNull(check(Vec2(1000.0, 400.0), Vec2(1200.0, 400.0), AircraftKind.Helicopter))
        assertEquals("h", hit.zone.id)
        assertTrue(abs(hit.entryPoint.x - (1110.0 - LevelDefaults.HELIPAD_ZONE_RADIUS)) < 1e-9, "${hit.entryPoint}")
    }

    @Test
    fun pathThatMissesThePadDoesNotLand() {
        assertNull(check(Vec2(1000.0, 300.0), Vec2(1200.0, 300.0), AircraftKind.Helicopter))
    }

    @Test
    fun wrongAircraftKindDoesNothing() {
        assertNull(check(Vec2(1000.0, 400.0), Vec2(1200.0, 400.0), AircraftKind.Airplane))
        assertNull(check(Vec2(200.0, 400.0), Vec2(400.0, 400.0), AircraftKind.Helicopter))
    }

    @Test
    fun fastSegmentSkippingOverTheZoneStillLands() {
        val hit = check(Vec2(900.0, 400.0), Vec2(1270.0, 400.0), AircraftKind.Helicopter)
        assertEquals("h", hit?.zone?.id)
    }

    @Test
    fun alignedApproachLands() {
        val hit = assertNotNull(check(Vec2(200.0, 400.0), Vec2(400.0, 410.0), AircraftKind.Airplane))
        assertTrue(abs(hit.entryPoint.x - 320.0) < 1e-6, "${hit.entryPoint}")
    }

    @Test
    fun oppositeDirectionOnAOneWayRunwayDoesNotLand() {
        assertNull(check(Vec2(1050.0, 400.0), Vec2(850.0, 400.0), AircraftKind.Airplane, RunwayMode.OneWay(east)))
        assertNotNull(check(Vec2(200.0, 400.0), Vec2(400.0, 400.0), AircraftKind.Airplane, RunwayMode.OneWay(east)))
    }

    @Test
    fun steepAngleDoesNotLand() {
        assertNull(check(Vec2(380.0, 250.0), Vec2(400.0, 500.0), AircraftKind.Airplane))
    }

    @Test
    fun twoWayRunwayLandsAtTheEndOfEachDirection() {
        val fromWest = assertNotNull(check(Vec2(200.0, 400.0), Vec2(400.0, 400.0), AircraftKind.Airplane))
        val fromEast = assertNotNull(check(Vec2(1050.0, 400.0), Vec2(850.0, 400.0), AircraftKind.Airplane))
        val west = fromWest.zone as LandingZone.Runway
        val eastEnd = fromEast.zone as LandingZone.Runway
        assertTrue(abs(Angles.difference(west.direction, east)) < 1e-9)
        assertTrue(abs(Angles.difference(eastEnd.direction, -east)) < 1e-9)
        assertTrue(abs(fromEast.entryPoint.x - 960.0) < 1e-6)
    }

    @Test
    fun midRunwayCrossingDoesNothing() {
        assertNull(check(Vec2(640.0, 250.0), Vec2(640.0, 550.0), AircraftKind.Airplane))
        assertNull(check(Vec2(560.0, 400.0), Vec2(720.0, 400.0), AircraftKind.Airplane))
    }
}
