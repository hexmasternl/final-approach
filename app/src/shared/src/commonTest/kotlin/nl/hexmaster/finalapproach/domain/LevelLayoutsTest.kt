package nl.hexmaster.finalapproach.domain

import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LevelLayoutsTest {
    private val one = Levels.meadowField
    private val two = Levels.islandAirport
    private val three = Levels.savannaDawn

    @Test
    fun cataloguePlaysInOrder() {
        assertEquals(listOf(one, two, three), Levels.all)
        assertEquals(listOf(1, 2, 3), Levels.all.map { it.order })
        assertEquals(two, Levels.byId(two.id))
    }

    @Test
    fun everyLevelIsValid() {
        Levels.all.forEach { assertEquals(emptyList(), it.validate(), it.id) }
    }

    @Test
    fun runwaysDefaultToAllAirplanesAndRefuseTheHelicopter() {
        val runway = one.runways.single()
        AircraftType.entries.filter { it.kind == AircraftKind.Airplane }.forEach { assertTrue(runway.accepts(it)) }
        assertFalse(runway.accepts(AircraftType.Helicopter))
    }

    @Test
    fun helipadsAcceptOnlyTheHelicopter() {
        val pad = one.helipads.single()
        assertTrue(pad.accepts(AircraftType.Helicopter))
        AircraftType.entries.filter { it != AircraftType.Helicopter }.forEach { assertFalse(pad.accepts(it)) }
    }

    @Test
    fun levelOneHasOneRunwayAndOneHelipad() {
        assertEquals(1, one.runways.size)
        assertEquals(1, one.helipads.size)
        assertEquals(3, LandingZones.of(one).size)
    }

    @Test
    fun levelTwoHasACrossingDiagonalRunwayThatRefusesTheJumbo() {
        val (main, diagonal) = two.runways
        assertTrue(Overlap.rects(main.shape, diagonal.shape))
        assertTrue(main.length > diagonal.length)
        val offAxis = abs(Angles.normalize(diagonal.orientation)) % (PI / 2)
        assertTrue(offAxis > 0.01 && offAxis < PI / 2 - 0.01, "the diagonal is not at 0 or 90 degrees")
        assertTrue(main.accepts(AircraftType.Jumbo))
        assertFalse(diagonal.accepts(AircraftType.Jumbo))
        assertTrue(diagonal.accepts(AircraftType.Jet))
        assertEquals(1, two.helipads.size)
    }

    @Test
    fun levelTwoStandsEntirelyOnTheIsland() {
        val island = requireNotNull(two.island)
        two.runways.flatMap { it.shape.corners }.forEach { assertTrue(island.contains(it), "runway corner $it") }
        two.helipads.forEach { pad ->
            val c = pad.position
            val r = pad.radius
            listOf(Vec2(c.x - r, c.y), Vec2(c.x + r, c.y), Vec2(c.x, c.y - r), Vec2(c.x, c.y + r))
                .forEach { assertTrue(island.contains(it), "helipad edge $it") }
        }
        two.scenery.forEach { assertTrue(island.contains(it.position), "${it.kind} at ${it.position}") }
    }

    @Test
    fun levelThreeHasTwoParallelMainsCrossedByASmallerRunway() {
        val (a, b, crossing) = three.runways
        assertEquals(a.orientation, b.orientation)
        assertFalse(Overlap.rects(a.shape, b.shape))
        assertTrue(Overlap.rects(a.shape, crossing.shape))
        assertTrue(Overlap.rects(b.shape, crossing.shape))
        assertTrue(crossing.length < a.length)
        assertFalse(crossing.accepts(AircraftType.Jumbo))
        assertTrue(a.accepts(AircraftType.Jumbo))
        assertEquals(2, three.helipads.size)
        assertEquals(LevelTheme.Dawn, three.theme)
    }

    @Test
    fun everyZoneIsInTheWorldAndClearOfTheHudBand() {
        Levels.all.forEach { level ->
            val world = level.world.viewport
            val points = level.runways.flatMap { it.shape.corners } +
                LandingZones.of(level).filterIsInstance<LandingZone.Runway>().flatMap { it.area.corners } +
                level.helipads.flatMap { pad ->
                    val r = pad.radius
                    listOf(Vec2(pad.position.x - r, pad.position.y - r), Vec2(pad.position.x + r, pad.position.y + r))
                }
            points.forEach {
                assertTrue(world.contains(it), "${level.id}: $it is outside the world")
                assertTrue(it.y >= LevelDefaults.HUD_BAND, "${level.id}: $it is in the HUD band")
            }
        }
    }

    @Test
    fun paddockStaysClearOfZones() {
        val paddock = requireNotNull(one.paddock)
        val rect = OrientedRect(
            Vec2((paddock.left + paddock.right) / 2, (paddock.top + paddock.bottom) / 2),
            0.0,
            paddock.bottom - paddock.top,
            paddock.right - paddock.left,
        )
        LandingZones.of(one).forEach { zone ->
            val clear = when (zone) {
                is LandingZone.Runway -> !Overlap.rects(rect, zone.area)
                is LandingZone.Helipad -> !Overlap.rectCircle(rect, zone.area)
            }
            assertTrue(clear, "paddock touches ${zone.id}")
        }
    }
}
