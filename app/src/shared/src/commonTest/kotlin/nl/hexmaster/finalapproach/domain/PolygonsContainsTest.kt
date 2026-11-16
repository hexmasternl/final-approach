package nl.hexmaster.finalapproach.domain

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * [Polygons.contains] on the boundary. The even-odd test is half-open: a point on a left or top
 * edge counts as inside and one on a right or bottom edge as outside, so two polygons that share
 * an edge never both hold a point on it. A landing zone with its fringe is therefore checked
 * against an airfield outline with a margin, never one that runs exactly along it.
 */
class PolygonsContainsTest {
    private val square = listOf(Vec2(0.0, 0.0), Vec2(10.0, 0.0), Vec2(10.0, 10.0), Vec2(0.0, 10.0))

    @Test
    fun aPointWellInsideIsInsideAndOneWellOutsideIsOutside() {
        assertTrue(Polygons.contains(square, Vec2(5.0, 5.0)))
        assertFalse(Polygons.contains(square, Vec2(-1.0, 5.0)))
        assertFalse(Polygons.contains(square, Vec2(11.0, 5.0)))
        assertFalse(Polygons.contains(square, Vec2(5.0, -1.0)))
        assertFalse(Polygons.contains(square, Vec2(5.0, 11.0)))
    }

    @Test
    fun aPointOnAnEdgeFallsOnOneSideOnly() {
        assertTrue(Polygons.contains(square, Vec2(0.0, 5.0)), "left edge")
        assertTrue(Polygons.contains(square, Vec2(5.0, 0.0)), "top edge")
        assertFalse(Polygons.contains(square, Vec2(10.0, 5.0)), "right edge")
        assertFalse(Polygons.contains(square, Vec2(5.0, 10.0)), "bottom edge")
    }

    @Test
    fun aPointOnACornerFallsOnOneSideOnly() {
        assertTrue(Polygons.contains(square, Vec2(0.0, 0.0)), "top-left corner")
        assertFalse(Polygons.contains(square, Vec2(10.0, 10.0)), "bottom-right corner")
    }

    @Test
    fun twoPolygonsSharingAnEdgeNeverBothHoldAPointOnIt() {
        val right = listOf(Vec2(10.0, 0.0), Vec2(20.0, 0.0), Vec2(20.0, 10.0), Vec2(10.0, 10.0))
        listOf(1.0, 5.0, 9.0).forEach { y ->
            val onEdge = Vec2(10.0, y)
            assertTrue(Polygons.contains(square, onEdge) != Polygons.contains(right, onEdge), "y = $y")
        }
    }

    @Test
    fun aPolygonOfFewerThanThreeCornersHoldsNothing() {
        assertFalse(Polygons.contains(listOf(Vec2(0.0, 0.0), Vec2(10.0, 10.0)), Vec2(5.0, 5.0)))
        assertFalse(Polygons.contains(emptyList(), Vec2(0.0, 0.0)))
    }
}
