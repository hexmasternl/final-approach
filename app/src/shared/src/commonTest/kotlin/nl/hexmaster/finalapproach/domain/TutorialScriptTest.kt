package nl.hexmaster.finalapproach.domain

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TutorialScriptTest {

    private companion object {
        const val REACH = 400.0
    }

    private val level = Levels.meadowField
    private val script = assertNotNull(level.tutorial)
    private val viewport = level.world.viewport

    /** The first point of the straight flight from [entry], at [speed], where the aircraft is well in view. */
    private fun wellInViewPoint(entry: ScriptedEntry, speed: Double = 1.0): Vec2 {
        val inset = script.wellInViewInset
        val direction = Angles.direction(entry.heading)
        var position = entry.position
        repeat(20_000) {
            val inside = position.x >= viewport.left + inset && position.x <= viewport.right - inset &&
                position.y >= viewport.top + inset && position.y <= viewport.bottom - inset
            if (inside) return position
            position += direction * speed
        }
        error("never well in view")
    }

    @Test
    fun onlyMeadowFieldHasAScript() {
        assertEquals(listOf("meadow-field"), Levels.all.filter { it.tutorial != null }.map { it.id })
    }

    @Test
    fun noTutorialIsTheDefault() {
        assertEquals(null, LevelConfig("x", "x", 1, WorldConfig(100.0, 100.0), emptyList()).tutorial)
    }

    @Test
    fun theLevelValidatesWithItsScript() {
        assertEquals(emptyList(), level.validate())
    }

    @Test
    fun anEntryInsideTheViewportIsRejected() {
        val inside = script.copy(lightProp = script.lightProp.copy(position = Vec2(500.0, 500.0)))
        assertTrue(level.copy(tutorial = inside).validate().any { it.contains("tutorial entry") })
    }

    @Test
    fun eachAirplaneEntryLeavesRoomForAnAlignedWestboundApproachToRunway27() {
        val runway = level.runways.single()
        val threshold = runway.position + Vec2(runway.length / 2.0, 0.0)
        val depth = level.landing.runwayZoneDepth
        for (entry in listOf(script.lightProp, script.turboprop)) {
            val point = wellInViewPoint(entry)
            assertTrue(viewport.contains(point), "well in view point $point is outside the viewport")
            // It is east of the threshold, so a path can still line up and fly west into the zone.
            assertTrue(point.x > threshold.x + depth, "point $point leaves no room before the threshold")
        }
        assertEquals(PI * 3 / 2, runway.landingHeadings.single() + 2 * PI, 1e-9)
    }

    @Test
    fun theHelicopterLineComesWithinReachOfTheHelipad() {
        val pad = level.helipads.single().position
        val point = wellInViewPoint(script.helicopter)
        assertTrue(viewport.contains(point))
        // Flying east, it passes the pad on its left and within a short drag of it.
        assertTrue(point.x < pad.x && kotlin.math.abs(pad.y - point.y) < REACH, "helicopter at $point cannot reach the pad")
    }
}
