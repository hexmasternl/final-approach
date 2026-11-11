package nl.hexmaster.finalapproach.input

import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorldViewportTest {

    @Test
    fun aWideScreenShowsTheFullHeightCentredHorizontally() {
        val viewport = WorldViewport(1280.0, 720.0, 2400.0, 1080.0)
        assertEquals(1.5, viewport.scale)
        assertEquals(Vec2(240.0, 0.0), viewport.offset)
        assertEquals(Vec2(2160.0, 1080.0), viewport.toScreen(Vec2(1280.0, 720.0)))
    }

    @Test
    fun aTallScreenShowsTheFullWidthCentredVertically() {
        val viewport = WorldViewport(1280.0, 720.0, 1280.0, 1000.0)
        assertEquals(1.0, viewport.scale)
        assertEquals(Vec2(0.0, 140.0), viewport.offset)
    }

    @Test
    fun aPointSurvivesTheRoundTrip() {
        val viewport = WorldViewport(1280.0, 720.0, 2340.0, 1080.0)
        val point = Vec2(321.5, 654.25)
        val back = viewport.toWorld(viewport.toScreen(point))
        assertTrue(abs(back.x - point.x) < 1e-9 && abs(back.y - point.y) < 1e-9, "$back")
        assertEquals(72.0, viewport.toScreenLength(viewport.toWorldLength(72.0)), 1e-9)
    }
}
