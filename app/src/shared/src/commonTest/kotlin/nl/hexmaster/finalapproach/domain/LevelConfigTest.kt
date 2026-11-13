package nl.hexmaster.finalapproach.domain

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LevelConfigTest {

    private fun minimal() = LevelConfig(
        id = "test",
        name = "Test",
        order = 1,
        world = WorldConfig(1000.0, 600.0),
        runways = listOf(RunwayConfig("r", Vec2(500.0, 300.0), PI / 2, 400.0)),
        phases = listOf(DifficultyPhase(0.0, 5.0, 8.0, mapOf(AircraftType.LightProp to 1), 2)),
    )

    @Test
    fun optionalFieldsDefaultToInertValues() {
        val config = minimal()
        assertEquals(1.0, config.speedMultiplier)
        assertNull(config.wind)
        assertFalse(config.emergencyAircraft)
        assertNull(config.tutorial)
        assertEquals(LevelTheme.Day, config.theme)
        assertEquals(Ambience(), config.ambience)
        assertEquals(LevelDefaults.SPAWN_SEPARATION, config.world.spawnSeparation)
        assertTrue(config.run.endless)
        assertNull(config.run.seed)
    }

    @Test
    fun landingConfigDefaultsToTheCentreLineValues() {
        val landing = LandingConfig()
        assertEquals(48.0, landing.touchdownDistance)
        assertEquals(2.0, landing.leadInTurningRadii)
        assertEquals(0.4, landing.leadInHandleFraction)
    }

    @Test
    fun overriddenSeparationApplies() {
        val config = minimal().copy(world = WorldConfig(1000.0, 600.0, spawnSeparation = 120.0))
        assertEquals(120.0, config.world.spawnSeparation)
    }

    @Test
    fun activePhaseIsTheLastStarted() {
        val phases = listOf(
            DifficultyPhase(0.0, 5.0, 8.0, mapOf(AircraftType.LightProp to 1), 2),
            DifficultyPhase(30.0, 4.0, 6.0, mapOf(AircraftType.LightProp to 1), 3),
            DifficultyPhase(90.0, 3.0, 5.0, mapOf(AircraftType.Jet to 1), 3),
        )
        val config = minimal().copy(phases = phases)
        assertEquals(phases[0], config.phaseAt(0.0))
        assertEquals(phases[0], config.phaseAt(29.9))
        assertEquals(phases[1], config.phaseAt(30.0))
        assertEquals(phases[2], config.phaseAt(1000.0))
    }

    private val rampBase = DifficultyPhase(
        100.0, 4.0, 7.0,
        mapOf(AircraftType.LightProp to 3, AircraftType.Jet to 2, AircraftType.Jumbo to 1),
        3,
    )

    private fun rampLevel(ramp: DifficultyRamp?) = minimal().copy(
        phases = listOf(DifficultyPhase(0.0, 6.0, 9.0, mapOf(AircraftType.LightProp to 1), 2), rampBase),
        ramp = ramp,
    )

    private val ramp = DifficultyRamp(intervalFloorSeconds = 2.5, maxAircraftCap = 5)

    @Test
    fun aRampDefaultsToTheConfiguredValues() {
        assertEquals(60.0, ramp.stepSeconds)
        assertEquals(0.5, ramp.intervalDropSeconds)
        assertEquals(2, ramp.aircraftEverySteps)
        assertEquals(listOf(WeightCap(AircraftType.Jet, 4), WeightCap(AircraftType.Jumbo, 3)), ramp.weightCaps)
    }

    @Test
    fun withoutARampTheLastPhaseStaysUnchanged() {
        val level = rampLevel(null)
        assertEquals(rampBase, level.phaseAt(100.0))
        assertEquals(rampBase, level.phaseAt(100_000.0))
    }

    @Test
    fun beforeTheFirstRampStepTheLastPhaseIsActive() {
        val level = rampLevel(ramp)
        assertEquals(rampBase, level.phaseAt(100.0))
        assertEquals(rampBase, level.phaseAt(159.9))
        assertEquals(1.0 * 60 + 100.0, level.phaseAt(160.0).startSeconds)
    }

    @Test
    fun theIntervalDropsButNeverBelowTheFloorAndKeepsItsWidth() {
        val level = rampLevel(ramp)
        val step1 = level.phaseAt(160.0)
        assertEquals(3.5, step1.spawnIntervalMin, 1e-9)
        assertEquals(6.5, step1.spawnIntervalMax, 1e-9)
        val step10 = level.phaseAt(100.0 + 10 * 60.0)
        assertEquals(2.5, step10.spawnIntervalMin, 1e-9)
        assertEquals(5.5, step10.spawnIntervalMax, 1e-9)
    }

    @Test
    fun weightsAlternateToTheirCapsWithoutSkipping() {
        val level = rampLevel(ramp)
        fun weights(step: Int) = level.phaseAt(100.0 + step * 60.0).weights
        assertEquals(3, weights(1)[AircraftType.Jet])
        assertEquals(1, weights(1)[AircraftType.Jumbo])
        assertEquals(3, weights(2)[AircraftType.Jet])
        assertEquals(2, weights(2)[AircraftType.Jumbo])
        assertEquals(4, weights(3)[AircraftType.Jet])
        assertEquals(2, weights(3)[AircraftType.Jumbo])
        assertEquals(4, weights(4)[AircraftType.Jet])
        assertEquals(3, weights(4)[AircraftType.Jumbo])
        assertEquals(weights(4), weights(9))
        assertEquals(3, weights(9)[AircraftType.LightProp])
    }

    @Test
    fun theMaximumInTheAirGrowsEveryAircraftStepUpToTheCap() {
        val level = rampLevel(ramp)
        fun most(step: Int) = level.phaseAt(100.0 + step * 60.0).maxAircraft
        assertEquals(3, most(1))
        assertEquals(4, most(2))
        assertEquals(4, most(3))
        assertEquals(5, most(4))
        assertEquals(5, most(50))
    }

    @Test
    fun thePhaseIsIdenticalFromTheFinalStepOn() {
        val level = rampLevel(ramp)
        val final = level.phaseAt(100.0 + 4 * 60.0)
        assertEquals(final, level.phaseAt(100.0 + 5 * 60.0))
        assertEquals(final, level.phaseAt(1_000_000.0))
        assertEquals(rampBase.allowedTypes, final.allowedTypes)
    }

    private val east = PI / 2
    private val tolerance = Angles.degrees(20.0)

    @Test
    fun oneWayRunwayAcceptsItsHeading() {
        val runway = RunwayConfig("r", Vec2.Zero, east, 400.0, mode = RunwayMode.OneWay(east))
        assertTrue(runway.acceptsHeading(east, tolerance))
        assertTrue(runway.acceptsHeading(east + Angles.degrees(19.0), tolerance))
    }

    @Test
    fun oneWayRunwayRefusesTheOppositeHeading() {
        val runway = RunwayConfig("r", Vec2.Zero, east, 400.0, mode = RunwayMode.OneWay(east))
        assertFalse(runway.acceptsHeading(east + PI, tolerance))
    }

    @Test
    fun twoWayRunwayAcceptsBothEnds() {
        val runway = RunwayConfig("r", Vec2.Zero, east, 400.0, mode = RunwayMode.TwoWay)
        assertEquals(Angles.normalize(east), runway.landingHeading(east, tolerance))
        assertEquals(Angles.normalize(east + PI), runway.landingHeading(east + PI, tolerance))
    }

    @Test
    fun acrossTheRunwayIsRefusedForBothModes() {
        val oneWay = RunwayConfig("r", Vec2.Zero, east, 400.0, mode = RunwayMode.OneWay(east))
        val twoWay = oneWay.copy(mode = RunwayMode.TwoWay)
        assertFalse(oneWay.acceptsHeading(0.0, tolerance))
        assertFalse(twoWay.acceptsHeading(0.0, tolerance))
        assertFalse(twoWay.acceptsHeading(PI, tolerance))
    }

    @Test
    fun toleranceEdge() {
        val runway = RunwayConfig("r", Vec2.Zero, east, 400.0, mode = RunwayMode.OneWay(east))
        assertTrue(runway.acceptsHeading(east + tolerance - 1e-9, tolerance))
        assertFalse(runway.acceptsHeading(east + tolerance + 1e-6, tolerance))
    }

    @Test
    fun anglesBeyondPiAreNormalised() {
        val runway = RunwayConfig("r", Vec2.Zero, 0.0, 400.0, mode = RunwayMode.OneWay(0.0))
        assertTrue(runway.acceptsHeading(2 * PI + Angles.degrees(5.0), tolerance))
        assertTrue(runway.acceptsHeading(-2 * PI - Angles.degrees(5.0), tolerance))
        assertTrue(runway.acceptsHeading(Angles.degrees(355.0), tolerance))
    }

    @Test
    fun runwayNumbersFollowTheHeading() {
        val runway = RunwayConfig("r", Vec2.Zero, east, 400.0)
        assertEquals("09", runway.numberFor(east))
        assertEquals("27", runway.numberFor(east + PI))
        assertEquals("36", runway.numberFor(0.0))
    }
}
