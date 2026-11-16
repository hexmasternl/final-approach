package nl.hexmaster.finalapproach.domain

import nl.hexmaster.finalapproach.ui.sprites.GroundMetrics
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
    fun theIslandOutlineKeepsToTheGrassAroundTheAirfield() {
        // Smaller than the island, so there is open grass left for flowers and palms.
        val level = Levels.islandAirport
        val island = requireNotNull(level.island)
        assertTrue(level.airfield.count { !island.contains(it, margin = 30.0) } < level.airfield.size / 2, "only the bulge for the helipad reaches the shore")
    }

    @Test
    fun theDomainCopiesOfTheHelipadFringeEqualTheDesignSystem() {
        assertEquals(GroundMetrics.Helipad.Fringe.half.toDouble(), Levels.HELIPAD_FRINGE_HALF)
        assertEquals(GroundMetrics.Helipad.Fringe.rx.toDouble(), Levels.HELIPAD_FRINGE_RX)
        assertEquals(GroundMetrics.Helipad.disc.toDouble(), Levels.HELIPAD_DISC)
    }

    @Test
    fun theConvexHullOfPointsHoldsThemAll() {
        val points = listOf(Vec2(0.0, 0.0), Vec2(4.0, 0.0), Vec2(4.0, 4.0), Vec2(0.0, 4.0), Vec2(2.0, 2.0), Vec2(2.0, 0.0))
        val hull = Polygons.convexHull(points)
        assertEquals(4, hull.size)
        assertTrue(Polygons.contains(hull, Vec2(2.0, 2.0)))
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
        val across = runway.width / GroundMetrics.Runway.width
        return roundedRectPoints(
            runway.position,
            runway.orientation,
            halfAlong = runway.length / 2.0 + GroundMetrics.Runway.fringe,
            halfAcross = GroundMetrics.Runway.fringeHalf * across,
            radius = GroundMetrics.Runway.fringeRx.toDouble(),
        )
    }

    private fun fringeOfHelipad(pad: HelipadConfig): List<Vec2> {
        val unit = pad.radius / GroundMetrics.Helipad.disc
        val half = GroundMetrics.Helipad.Fringe.half * unit
        return roundedRectPoints(pad.position, 0.0, half, half, GroundMetrics.Helipad.Fringe.rx * unit.toDouble())
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
}
