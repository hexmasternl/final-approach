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

    @Test
    fun meadowFieldReadsAsASmallAirfield() {
        val kinds = Levels.meadowField.scenery.map { it.kind }.toSet()
        val airfield = setOf(
            SceneryKind.Taxiway, SceneryKind.Apron, SceneryKind.Terminal, SceneryKind.Tower,
            SceneryKind.Hangar, SceneryKind.Windsock, SceneryKind.Tree,
        )
        assertTrue(kinds.containsAll(airfield), "missing ${airfield - kinds}")
    }

    @Test
    fun meadowFieldLiesInFarmland() {
        val kinds = Levels.meadowField.scenery.map { it.kind }.toSet()
        val surroundings = setOf(
            SceneryKind.Field, SceneryKind.Hedge, SceneryKind.Fence, SceneryKind.Farmhouse,
            SceneryKind.Barn, SceneryKind.Road, SceneryKind.Pond, SceneryKind.ForestEdge,
        )
        assertTrue(kinds.containsAll(surroundings), "missing ${surroundings - kinds}")
        assertTrue(Levels.meadowField.paddock != null, "the paddock holds the cows and the farmer")
    }
}