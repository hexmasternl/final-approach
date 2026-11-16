package nl.hexmaster.finalapproach.domain

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The mown airfield of a level is level data: a polygon that holds every landing zone's ground. */
class AirfieldOutlineTest {
    private val square = listOf(Vec2(0.0, 0.0), Vec2(10.0, 0.0), Vec2(10.0, 10.0), Vec2(0.0, 10.0))

    @Test
    fun aPolygonHoldsItsInsideAndNotItsOutside() {
        assertTrue(Polygons.contains(square, Vec2(5.0, 5.0)))
        assertFalse(Polygons.contains(square, Vec2(11.0, 5.0)))
        assertFalse(Polygons.contains(square, Vec2(5.0, -1.0)))
        assertFalse(Polygons.contains(square.take(2), Vec2(5.0, 0.0)), "fewer than three corners hold nothing")
    }

    @Test
    fun everyLevelHasAnOutlineAndADecorationSeed() {
        Levels.all.forEach {
            assertTrue(it.airfield.size >= 3, it.id)
            assertTrue(it.decorationSeed != 0L, it.id)
        }
        assertEquals(Levels.all.size, Levels.all.map { it.decorationSeed }.toSet().size, "each level has its own seed")
    }

    @Test
    fun theIslandOutlineLiesOnTheIsland() {
        val level = Levels.islandAirport
        val island = requireNotNull(level.island)
        assertTrue(level.airfield.all { island.contains(it) })
    }

    @Test
    fun everyRunwayAndHelipadWithItsFringeLiesInsideTheOutline() {
        Levels.all.forEach { level ->
            level.runways.forEach { runway ->
                fringeOfRunway(runway).forEach { point ->
                    assertTrue(Polygons.contains(level.airfield, point), "${level.id}: runway ${runway.id} fringe at $point")
                }
            }
            level.helipads.forEach { pad ->
                fringeOfHelipad(pad).forEach { point ->
                    assertTrue(Polygons.contains(level.airfield, point), "${level.id}: helipad ${pad.id} fringe at $point")
                }
            }
        }
    }

    @Test
    fun anOutlineThatMissesARunwayIsRejected() {
        val level = Levels.meadowField
        val tooSmall = level.copy(airfield = listOf(Vec2(0.0, 0.0), Vec2(100.0, 0.0), Vec2(100.0, 100.0)))
        assertTrue(tooSmall.validate().any { "Runway" in it && "outside the airfield outline" in it })
        assertTrue(tooSmall.validate().any { "Helipad" in it && "outside the airfield outline" in it })
        assertTrue(level.copy(airfield = square.take(2)).validate().any { "at least three points" in it })
        val offIsland = Levels.islandAirport.copy(airfield = Levels.islandAirport.airfield.map { it + Vec2(400.0, 0.0) })
        assertTrue(offIsland.validate().any { "on the island" in it })
    }

    /** Points on the rounded rectangle of a runway's rough-grass fringe: the design system's fringe, turned like the runway. */
    private fun fringeOfRunway(runway: RunwayConfig): List<Vec2> {
        val across = runway.width / FRINGE_WIDTH_REFERENCE
        return roundedRectPoints(
            runway.position,
            runway.orientation,
            halfAlong = runway.length / 2.0 + RUNWAY_FRINGE,
            halfAcross = RUNWAY_FRINGE_HALF * across,
            radius = RUNWAY_FRINGE_RX,
        )
    }

    private fun fringeOfHelipad(pad: HelipadConfig): List<Vec2> {
        val half = HELIPAD_FRINGE_HALF * pad.radius / HELIPAD_DISC
        return roundedRectPoints(pad.position, 0.0, half, half, HELIPAD_FRINGE_RX * pad.radius / HELIPAD_DISC)
    }

    /** The straight sides' ends and three points on each corner arc of a rounded rectangle. */
    private fun roundedRectPoints(center: Vec2, heading: Double, halfAlong: Double, halfAcross: Double, radius: Double): List<Vec2> {
        val local = buildList {
            for (sx in listOf(-1.0, 1.0)) for (sy in listOf(-1.0, 1.0)) {
                val arc = Vec2(sx * (halfAlong - radius), sy * (halfAcross - radius))
                for (angle in listOf(0.0, PI / 4, PI / 2)) {
                    val sideX = if (sx > 0) angle else PI - angle
                    val dx = cos(sideX)
                    val dy = if (sy > 0) sin(angle) else -sin(angle)
                    add(arc + Vec2(dx * radius, dy * radius))
                }
            }
        }
        return local.map { center + it.rotated(heading - PI / 2.0) }
    }

    private companion object {
        // The design system's fringe sizes (GroundMetrics), in world units; the domain does not read the UI.
        const val RUNWAY_FRINGE = 24.0
        const val RUNWAY_FRINGE_HALF = 54.0
        const val RUNWAY_FRINGE_RX = 30.0
        const val FRINGE_WIDTH_REFERENCE = 64.0
        const val HELIPAD_FRINGE_HALF = 76.0
        const val HELIPAD_FRINGE_RX = 22.0
        const val HELIPAD_DISC = 56.0
    }
}
