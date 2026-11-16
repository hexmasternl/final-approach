package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.ui.sprites.GroundMetrics
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RunwayPlanTest {
    private val both = RunwayEnd.entries.toSet()
    private val lightSpacing = GroundMetrics.Runway.Lights.Edge.spacing

    @Test
    fun aTwoWayRunwayIsMarkedAtBothEndsAndAOneWayRunwayAtItsLandingEndOnly() {
        assertEquals(both, runwayPlan(760f, both).ends.map { it.end }.toSet())
        val oneWay = runwayPlan(760f, setOf(RunwayEnd.High))
        assertEquals(listOf(RunwayEnd.High), oneWay.ends.map { it.end })
        assertEquals(RunwayEnd.High, oneWay.approachEnd)
    }

    @Test
    fun everyMarkedEndHasSixThresholdBarsFourThresholdLightsAndANumber() {
        runwayPlan(760f, both).ends.forEach { end ->
            assertEquals(6, end.bars.size, "${end.end}")
            assertEquals(4, end.thresholdLights.size)
        }
        val plan = runwayPlan(760f, both)
        assertEquals(-plan.ends.first { it.end == RunwayEnd.High }.numberAt.x, plan.ends.first { it.end == RunwayEnd.Low }.numberAt.x)
    }

    @Test
    fun aimingBlocksComeFrom460AndTouchdownMarksFrom560() {
        fun counts(length: Float) = runwayPlan(length, both).ends.map { it.aiming.size to it.touchdown.size }.toSet()
        assertEquals(setOf(0 to 0), counts(240f))
        assertEquals(setOf(0 to 0), counts(459f))
        assertEquals(setOf(2 to 0), counts(460f))
        assertEquals(setOf(2 to 0), counts(559f))
        assertEquals(setOf(2 to 4), counts(560f))
    }

    @Test
    fun tyreRubberFollowsTheAimingBlocks() {
        assertEquals(0, runwayPlan(459f, both).ends.first().rubber.size)
        assertEquals(4, runwayPlan(460f, both).ends.first().rubber.size)
    }

    @Test
    fun aRunwayShorterThanTheMinimumIsPlannedAtTheMinimum() {
        assertEquals(GroundMetrics.Runway.minLength, runwayPlan(100f, both).length)
    }

    @Test
    fun aTwoWayCentreLineIsDashedAndAOneWayCentreLineIsChevrons() {
        val twoWay = runwayPlan(760f, both)
        assertTrue(twoWay.centreDashes != null && twoWay.chevrons.isEmpty())
        val oneWay = runwayPlan(760f, setOf(RunwayEnd.Low))
        assertNull(oneWay.centreDashes)
        assertTrue(oneWay.chevrons.isNotEmpty())
    }

    @Test
    fun chevronsPointAwayFromTheLandingEnd() {
        // Airplanes land towards the far end: a chevron's arms lie on the landing end's side of its tip.
        val high = runwayPlan(760f, setOf(RunwayEnd.High)).chevrons
        assertTrue(high.all { it.armA.x > it.tip.x })
        val low = runwayPlan(760f, setOf(RunwayEnd.Low)).chevrons
        assertTrue(low.all { it.armA.x < it.tip.x })
        assertEquals(high.map { it.tip.x }, low.map { -it.tip.x })
    }

    @Test
    fun edgeLightsLieEvery58UnitsOnBothSidesAndEvenlyAboutTheMiddle() {
        listOf(240f, 459f, 760f).forEach { length ->
            val plan = runwayPlan(length, both)
            val side = plan.edgeLights.filter { it.y < 0f }.map { it.x }
            assertEquals(plan.edgeLights.filter { it.y > 0f }.map { it.x }, side, "both sides at the same places")
            assertEquals(side.size, plan.edgeLightsPerSide)
            side.zipWithNext().forEach { (a, b) -> assertEquals(lightSpacing, b - a, 1e-3f) }
            assertTrue(side.all { kotlin.math.abs(it) <= length / 2f })
            assertEquals(0f, side.first() + side.last(), 1e-3f)
        }
    }

    @Test
    fun approachLightsLieBesideOneLandingEndTwoRedAndTwoWhite() {
        val twoWay = runwayPlan(760f, both)
        assertEquals(RunwayEnd.Low, twoWay.approachEnd)
        assertEquals(listOf(true, true, false, false), twoWay.approachLights.map { it.red })
        val oneWay = runwayPlan(760f, setOf(RunwayEnd.High))
        assertEquals(listOf(false, false, true, true), oneWay.approachLights.map { it.red })
        // Beside the end the lights belong to, in the fringe.
        assertTrue(twoWay.approachLights.all { it.box.x < 0f })
        assertTrue(oneWay.approachLights.all { it.box.x > 0f })
    }

    @Test
    fun theAcrossScaleStretchesWhatLiesAcrossTheRunwayOnly() {
        val one = runwayPlan(760f, both, acrossScale = 1f)
        val two = runwayPlan(760f, both, acrossScale = 2f)
        assertEquals(one.ends.first().bars.first().y * 2f, two.ends.first().bars.first().y)
        assertEquals(one.ends.first().bars.first().width, two.ends.first().bars.first().width)
        assertEquals(one.edgeLights.first().x, two.edgeLights.first().x)
    }
}
