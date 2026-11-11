package nl.hexmaster.finalapproach.domain

import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LandingZonesTest {

    private val east = PI / 2

    private fun level(mode: RunwayMode) = Levels.meadowField.copy(
        runways = listOf(RunwayConfig("r", Vec2(640.0, 400.0), east, 640.0, mode = mode)),
    )

    private fun near(a: Double, b: Double) = abs(a - b) < 1e-9

    @Test
    fun oneWayRunwayHasOneZoneAtItsStart() {
        val zones = LandingZones.of(level(RunwayMode.OneWay(east))).filterIsInstance<LandingZone.Runway>()
        assertEquals(1, zones.size)
        val zone = zones.single()
        assertTrue(near(zone.direction, east))
        val depth = LevelDefaults.RUNWAY_ZONE_DEPTH
        assertTrue(near(zone.area.center.x, 320.0 + depth / 2.0), "${zone.area.center}")
        assertTrue(near(zone.area.center.y, 400.0))
    }

    @Test
    fun twoWayRunwayHasTwoOppositeZonesAtItsEnds() {
        val zones = LandingZones.of(level(RunwayMode.TwoWay)).filterIsInstance<LandingZone.Runway>()
        assertEquals(2, zones.size)
        val (a, b) = zones
        assertTrue(near(abs(Angles.difference(a.direction, b.direction)), PI))
        val depth = LevelDefaults.RUNWAY_ZONE_DEPTH
        val xs = zones.map { it.area.center.x }.sorted()
        assertTrue(near(xs[0], 320.0 + depth / 2.0) && near(xs[1], 960.0 - depth / 2.0), "$xs")
        zones.forEach { assertTrue(near(it.rolloutDistance, 640.0 * 2.0 / 3.0)) }
    }

    @Test
    fun helipadZoneUsesTheDefaultOrItsOwnRadius() {
        val pads = listOf(
            HelipadConfig("a", Vec2(1110.0, 200.0)),
            HelipadConfig("b", Vec2(1110.0, 560.0), zoneRadius = 30.0),
        )
        val zones = LandingZones.of(Levels.meadowField.copy(helipads = pads)).filterIsInstance<LandingZone.Helipad>()
        assertEquals(LevelDefaults.HELIPAD_ZONE_RADIUS, zones.first { it.id == "a" }.area.radius)
        assertEquals(30.0, zones.first { it.id == "b" }.area.radius)
        assertEquals(Vec2(1110.0, 200.0), zones.first { it.id == "a" }.area.center)
    }
}
