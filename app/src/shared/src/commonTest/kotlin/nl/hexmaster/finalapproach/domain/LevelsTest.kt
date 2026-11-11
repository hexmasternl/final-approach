package nl.hexmaster.finalapproach.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LevelsTest {

    @Test
    fun meadowFieldValidates() {
        assertEquals(emptyList(), Levels.meadowField.validate())
    }

    @Test
    fun firstPhaseAllowsOnlyLightPropsAndHelicopters() {
        assertEquals(
            setOf(AircraftType.LightProp, AircraftType.Helicopter),
            Levels.meadowField.phases.first().allowedTypes,
        )
    }

    @Test
    fun noPhaseExceedsThreeAircraft() {
        assertTrue(Levels.meadowField.phases.all { it.maxAircraft <= 3 })
    }

    @Test
    fun meadowFieldHasATwoWayRunwayAndAHelipad() {
        val level = Levels.meadowField
        assertEquals(1, level.runways.size)
        assertEquals(RunwayMode.TwoWay, level.runways.single().mode)
        assertEquals(1, level.helipads.size)
    }
}
