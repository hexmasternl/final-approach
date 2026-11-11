package nl.hexmaster.finalapproach.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AircraftTuningTest {

    private val defaults = DefaultAircraftSpecs.all

    @Test
    fun noOverridesEqualsDefaults() {
        val effective = AircraftTuning().resolve()
        AircraftType.entries.forEach { assertEquals(defaults.getValue(it), effective[it]) }
    }

    @Test
    fun speedMultiplierAppliesToEveryType() {
        val effective = AircraftTuning(speedMultiplier = 1.5).resolve()
        AircraftType.entries.forEach { assertEquals(defaults.getValue(it).speed * 1.5, effective.speedOf(it)) }
    }

    @Test
    fun perTypeSpeedOverrideLeavesOtherTypesAlone() {
        val effective = AircraftTuning(speedOverrides = mapOf(AircraftType.Jet to 90.0)).resolve()
        assertEquals(90.0, effective.speedOf(AircraftType.Jet))
        AircraftType.entries.filter { it != AircraftType.Jet }.forEach {
            assertEquals(defaults.getValue(it).speed, effective.speedOf(it))
        }
    }

    @Test
    fun overrideThenMultiplier() {
        val effective = AircraftTuning(speedMultiplier = 1.5, speedOverrides = mapOf(AircraftType.Jet to 90.0)).resolve()
        assertEquals(135.0, effective.speedOf(AircraftType.Jet))
    }

    @Test
    fun pointsMultiplierWithAPerTypeOverride() {
        val effective = AircraftTuning(
            pointsMultiplier = 2.0,
            pointsOverrides = mapOf(AircraftType.Helicopter to 15),
        ).resolve()
        assertEquals(30, effective.pointsOf(AircraftType.Helicopter))
        AircraftType.entries.filter { it != AircraftType.Helicopter }.forEach {
            assertEquals(defaults.getValue(it).landingPoints * 2, effective.pointsOf(it))
        }
    }

    @Test
    fun zeroAndNegativeValuesAreRejectedByName() {
        val cases = mapOf(
            AircraftTuning(speedMultiplier = 0.0) to "speedMultiplier",
            AircraftTuning(pointsMultiplier = -1.0) to "pointsMultiplier",
            AircraftTuning(speedOverrides = mapOf(AircraftType.Jet to -5.0)) to "speedOverrides[Jet]",
            AircraftTuning(pointsOverrides = mapOf(AircraftType.Jumbo to 0)) to "pointsOverrides[Jumbo]",
        )
        cases.forEach { (tuning, field) ->
            assertTrue(tuning.problems().any { it.contains(field) }, "$field: ${tuning.problems()}")
            assertFailsWith<IllegalArgumentException> { tuning.resolve() }
        }
    }

    @Test
    fun levelValidationRejectsAnInvalidOverride() {
        val level = Levels.meadowField.copy(speedOverrides = mapOf(AircraftType.Jet to 0.0))
        assertTrue(level.validate().any { it.contains("speedOverrides[Jet]") })
    }

    @Test
    fun levelResolvesItsTuning() {
        val level = Levels.meadowField.copy(
            speedMultiplier = 2.0,
            scoring = ScoringConfig(pointsMultiplier = 3.0),
            pointsOverrides = mapOf(AircraftType.Jet to 7),
        )
        val effective = level.aircraftTuning.resolve()
        assertEquals(DefaultAircraftSpecs.jumbo.speed * 2.0, effective.speedOf(AircraftType.Jumbo))
        assertEquals(21, effective.pointsOf(AircraftType.Jet))
    }

    @Test
    fun effectiveValuesStayFixedWhileTimePasses() {
        val effective = AircraftTuning(speedMultiplier = 1.2).resolve()
        val before = AircraftType.entries.map { effective[it] }
        val spawner = nl.hexmaster.finalapproach.engine.Spawner(Levels.meadowField, 1, effective::speedOf)
        var time = 0.0
        repeat(1000) {
            spawner.update(0.1, time, emptyList())
            time += 0.1
        }
        assertEquals(before, AircraftType.entries.map { effective[it] })
    }
}
