package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.platform.ScreenRect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GestureExclusionTest {

    private val width = 2000
    private val height = 1000
    private val strip = 60
    private val pick = 100f
    private val cap = 200
    private val half = 48

    private fun plan(
        vararg aircraft: GestureTarget,
        selected: Int? = null,
        leftStrip: Int = strip,
        rightStrip: Int = strip,
    ) = planGestureExclusion(aircraft.toList(), selected, width, height, leftStrip, rightStrip, pick, cap, half)

    private fun at(id: Int, x: Float, y: Float, pickable: Boolean = true) = GestureTarget(id, x, y, pickable)

    private fun List<ScreenRect>.height() = sumOf { it.bottom - it.top }

    @Test
    fun anAircraftNearTheLeftEdgeGetsAZoneSpanningTheStripAndCentredOnIt() {
        val zones = plan(at(1, 120f, 500f))
        assertEquals(listOf(ScreenRect(0, 452, strip, 548)), zones)
    }

    @Test
    fun anAircraftNearTheRightEdgeGetsAZoneOnTheRightStrip() {
        val zones = plan(at(1, width - 120f, 500f))
        assertEquals(listOf(ScreenRect(width - strip, 452, width, 548)), zones)
    }

    @Test
    fun anAircraftAwayFromTheEdgesGetsNone() {
        assertEquals(emptyList(), plan(at(1, 1000f, 500f)))
        assertEquals(emptyList(), plan(at(1, 161f, 500f)))
    }

    @Test
    fun anAircraftWhosePickAreaIsEntirelyOffScreenGetsNoneAndTakesNoBudget() {
        assertEquals(emptyList(), plan(at(1, -(pick + 10f), 500f)))
        val onScreen = (0 until 3).map { at(10 + it, 100f, 100f + it * 300f) }
        val off = (0 until 3).map { at(20 + it, -500f, 150f + it * 300f) }
        val zones = plan(*(off + onScreen).toTypedArray(), rightStrip = 0)
        assertEquals(2, zones.size)
        assertTrue(zones.all { it.top in 0..1000 })
    }

    @Test
    fun aNonPickableAircraftGetsNone() {
        assertEquals(emptyList(), plan(at(1, 50f, 500f, pickable = false)))
    }

    @Test
    fun aZeroStripWidthGivesNoZonesForThatEdge() {
        assertEquals(emptyList(), plan(at(1, 50f, 500f), leftStrip = 0))
        assertEquals(1, plan(at(1, 50f, 500f), at(2, width - 50f, 500f), leftStrip = 0).size)
    }

    @Test
    fun zonesAreClampedToTheScreen() {
        val zones = plan(at(1, 50f, 10f), at(2, 50f, height - 10f))
        assertTrue(zones.all { it.top >= 0 && it.bottom <= height && it.left >= 0 && it.right <= width })
        assertEquals(listOf(0, height - 58), zones.map { it.top })
    }

    @Test
    fun overlappingZonesMerge() {
        val zones = plan(at(1, 50f, 500f), at(2, 80f, 540f))
        assertEquals(listOf(ScreenRect(0, 452, strip, 588)), zones)
    }

    @Test
    fun withManyAircraftOnOneEdgeTheTotalStaysWithinTheCap() {
        val aircraft = (0 until 8).map { at(it + 1, 50f + it, 100f + it * 110f) }
        val zones = plan(*aircraft.toTypedArray())
        assertTrue(zones.height() <= cap, "height ${zones.height()}")
        assertTrue(zones.isNotEmpty())
    }

    @Test
    fun theSelectedAircraftIsKeptFirstThenTheClosestToTheEdge() {
        val far = at(1, 150f, 100f)
        val near = at(2, 20f, 400f)
        val selected = at(3, 140f, 800f)
        val zones = plan(far, near, selected, selected = 3, rightStrip = 0)
        // Two zones of 96 fit in 200; the third would not.
        assertEquals(listOf(ScreenRect(0, 352, strip, 448), ScreenRect(0, 752, strip, 848)), zones)
    }
}
