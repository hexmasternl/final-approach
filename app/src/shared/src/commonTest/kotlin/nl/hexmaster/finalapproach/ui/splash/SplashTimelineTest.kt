package nl.hexmaster.finalapproach.ui.splash

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val SCREEN_WIDTH = 412f
private const val TINY = 0.0005f

class SplashTimelineTest {
    private val config = SplashConfig()
    private val geometry = config.geometry(SCREEN_WIDTH)
    private val timeline = SplashTimeline(config, geometry)

    @Test
    fun theAirplaneStartsOffScreenOnTheLeft() {
        val start = timeline.frameAt(0f)
        val nose = assertNotNull(start.airplaneX) + geometry.airplane / 2f

        assertTrue(nose <= 0f, "nose at $nose")
        assertEquals(SplashPhase.Towing, start.phase)
        assertTrue(start.towAttached)
    }

    @Test
    fun theBannerStartsOffScreenBehindTheAirplane() {
        val start = timeline.frameAt(0f)
        val leadingEdge = start.bannerX + geometry.bannerWidth / 2f

        assertTrue(leadingEdge < assertNotNull(start.airplaneX))
        assertTrue(leadingEdge < 0f)
    }

    @Test
    fun theAirplaneFliesSteadilyToTheRight() {
        var previous = assertNotNull(timeline.frameAt(0f).airplaneX)
        var time = 0.1f
        while (time < timeline.airplaneGoneSeconds - 0.1f) {
            val x = assertNotNull(timeline.frameAt(time).airplaneX)
            assertEquals(config.airplaneSpeed * 0.1f, x - previous, 0.0001f)
            previous = x
            time += 0.1f
        }
    }

    @Test
    fun theAirplaneLeavesThroughTheRightEdgeAndIsThenGone() {
        val lastVisible = timeline.frameAt(timeline.airplaneGoneSeconds - TINY)
        val tail = assertNotNull(lastVisible.airplaneX) - geometry.airplane / 2f

        // Its tail, and the loose tow line behind it, are past the right edge.
        assertTrue(tail - geometry.towLine > 1f - 0.001f, "tail at $tail")
        assertNull(timeline.frameAt(timeline.airplaneGoneSeconds).airplaneX)
        assertNull(timeline.frameAt(timeline.airplaneGoneSeconds + 30f).airplaneX)
    }

    @Test
    fun theBannerLetsGoExactlyWhenItsMiddleReachesTheMiddleOfTheScreen() {
        val before = timeline.frameAt(timeline.releaseSeconds - TINY)
        val at = timeline.frameAt(timeline.releaseSeconds)

        assertTrue(before.towAttached)
        assertTrue(before.bannerX < 0.5f)
        assertEquals(0.5f, before.bannerX, 0.001f)

        assertFalse(at.towAttached)
        assertEquals(0.5f, at.bannerX)
        assertEquals(SplashPhase.Released, at.phase)
    }

    @Test
    fun theBannerNeverLeavesTheCentreAfterLettingGo() {
        var time = timeline.releaseSeconds
        while (time < timeline.finishSeconds + 60f) {
            assertEquals(0.5f, timeline.frameAt(time).bannerX, "at $time s")
            time += 0.05f
        }
    }

    @Test
    fun theAirplaneKeepsFlyingAfterTheBannerLetsGo() {
        // On a wide screen there is room to watch the airplane fly on.
        val wide = SplashTimeline(config, config.geometry(915f))
        val at = assertNotNull(wide.frameAt(wide.releaseSeconds).airplaneX)
        val later = assertNotNull(wide.frameAt(wide.releaseSeconds + 0.5f).airplaneX)

        assertEquals(config.airplaneSpeed * 0.5f, later - at, 0.0001f)
    }

    @Test
    fun theAirplaneIsStillInViewWhenTheBannerLetsGo() {
        for (width in listOf(360f, 412f, 915f)) {
            val g = config.geometry(width)
            val t = SplashTimeline(config, g)
            val tail = assertNotNull(t.frameAt(t.releaseSeconds).airplaneX) - g.airplane / 2f
            assertTrue(tail < 1f, "airplane already gone at release on a $width dp screen")
        }
    }

    @Test
    fun theBannerComesToRestAndKeepsWaving() {
        val settled = timeline.frameAt(timeline.settledSeconds)
        val later = timeline.frameAt(timeline.settledSeconds + 10f)

        assertEquals(SplashPhase.Settled, settled.phase)
        assertEquals(0f, settled.bannerDip)
        assertEquals(1f, settled.waveStrength)
        assertEquals(1f, later.waveStrength)
        assertTrue(later.wavePhase > settled.wavePhase, "the wave stopped")
    }

    @Test
    fun theWaveNeverJumps() {
        var previous = timeline.frameAt(0f).wavePhase
        var time = 0.01f
        while (time < timeline.finishSeconds) {
            val phase = timeline.frameAt(time).wavePhase
            val step = phase - previous
            assertTrue(step > 0f && step < 0.05f, "wave moved $step at $time s")
            previous = phase
            time += 0.01f
        }
    }

    @Test
    fun theWaveIsSmallEnoughToKeepTheNameReadable() {
        val bannerHeight = geometry.bannerWidth * SCREEN_WIDTH / config.bannerAspect

        // Crest to trough stays well under the height of the banner.
        assertTrue(2f * config.waveAmplitude < bannerHeight / 3f)
    }

    @Test
    fun thePageHandsOverTwoSecondsAfterTheBannerSettles() {
        assertEquals(2f, timeline.finishSeconds - timeline.settledSeconds, 0.0001f)
        assertFalse(timeline.frameAt(timeline.finishSeconds - TINY).isFinished)
        assertTrue(timeline.frameAt(timeline.finishSeconds).isFinished)
        assertTrue(timeline.frameAt(timeline.finishSeconds + 100f).isFinished)
    }

    @Test
    fun theOrderIsReleaseThenSettleThenHandOver() {
        assertTrue(timeline.releaseSeconds > 0f)
        assertTrue(timeline.settledSeconds > timeline.releaseSeconds)
        assertTrue(timeline.finishSeconds > timeline.settledSeconds)
        assertTrue(timeline.airplaneGoneSeconds > timeline.releaseSeconds)
    }

    @Test
    fun theTowTakesOnlyAFewSeconds() {
        assertTrue(timeline.releaseSeconds < 5f, "tow takes ${timeline.releaseSeconds} s")
    }

    @Test
    fun withReducedMotionTheSceneIsSettledFromTheStart() {
        val reduced = SplashTimeline(config, geometry, reduceMotion = true)
        val start = reduced.frameAt(0f)

        assertEquals(SplashPhase.Settled, start.phase)
        assertEquals(0.5f, start.bannerX)
        assertNull(start.airplaneX)
        assertFalse(start.towAttached)
        assertEquals(0f, start.waveStrength)
        assertEquals(0f, start.bannerDip)
    }

    @Test
    fun withReducedMotionNothingMovesButThePageStillHandsOverAfterTwoSeconds() {
        val reduced = SplashTimeline(config, geometry, reduceMotion = true)
        val start = reduced.frameAt(0f)

        assertEquals(start, reduced.frameAt(1.9f))
        assertFalse(reduced.frameAt(1.999f).isFinished)
        assertTrue(reduced.frameAt(2f).isFinished)
    }

    @Test
    fun theBannerFitsOnNarrowAndWideScreens() {
        for (width in listOf(320f, 412f, 915f, 1280f)) {
            val g = config.geometry(width)
            assertTrue(g.bannerWidth <= config.bannerWidthFraction + 0.0001f, "banner too wide at $width dp")
            assertTrue(g.bannerWidth * width <= config.bannerMaxWidth + 0.01f, "banner over its maximum at $width dp")
            // Centred, the whole banner is on screen.
            assertTrue(0.5f - g.bannerWidth / 2f > 0f && 0.5f + g.bannerWidth / 2f < 1f)
        }
    }
}
