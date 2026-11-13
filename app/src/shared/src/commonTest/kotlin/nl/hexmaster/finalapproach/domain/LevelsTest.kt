package nl.hexmaster.finalapproach.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LevelsTest {

    @Test
    fun meadowFieldValidates() {
        assertEquals(emptyList(), Levels.meadowField.validate())
    }

    private fun assertPhase(
        level: LevelConfig,
        seconds: Int,
        min: Double,
        max: Double,
        aircraft: Int,
        weights: List<Int>,
    ) {
        val phase = level.phaseAt(seconds.toDouble())
        val label = "${level.name} at $seconds s"
        assertEquals(min, phase.spawnIntervalMin, 1e-9, label)
        assertEquals(max, phase.spawnIntervalMax, 1e-9, label)
        assertEquals(aircraft, phase.maxAircraft, label)
        val order = listOf(
            AircraftType.LightProp, AircraftType.Turboprop, AircraftType.Jet, AircraftType.Jumbo, AircraftType.Helicopter,
        )
        assertEquals(weights, order.map { phase.weights.getValue(it) }, label)
    }

    @Test
    fun everyLevelHasAValidRamp() {
        Levels.all.forEach {
            assertTrue(it.ramp != null, "${it.name} has a ramp")
            assertEquals(emptyList(), it.validate(), it.name)
        }
    }

    @Test
    fun meadowFieldRampTimeline() {
        val level = Levels.meadowField
        assertPhase(level, 180, 3.5, 6.5, 3, listOf(3, 2, 3, 1, 2))
        assertPhase(level, 240, 3.0, 6.0, 4, listOf(3, 2, 3, 2, 2))
        assertPhase(level, 300, 2.5, 5.5, 4, listOf(3, 2, 4, 2, 2))
        assertPhase(level, 360, 2.5, 5.5, 5, listOf(3, 2, 4, 3, 2))
        assertPhase(level, 600, 2.5, 5.5, 5, listOf(3, 2, 4, 3, 2))
        assertEquals(level.phaseAt(360.0), level.phaseAt(36_000.0))
    }

    @Test
    fun islandRampTimeline() {
        val level = Levels.islandAirport
        assertPhase(level, 120, 3.5, 6.5, 4, listOf(3, 2, 3, 1, 2))
        assertPhase(level, 180, 3.0, 6.0, 5, listOf(3, 2, 3, 2, 2))
        assertPhase(level, 240, 2.5, 5.5, 5, listOf(3, 2, 4, 2, 2))
        assertPhase(level, 300, 2.0, 5.0, 6, listOf(3, 2, 4, 3, 2))
        assertPhase(level, 600, 2.0, 5.0, 6, listOf(3, 2, 4, 3, 2))
    }

    @Test
    fun savannaRampTimeline() {
        val level = Levels.savannaDawn
        assertPhase(level, 105, 3.5, 6.5, 5, listOf(3, 2, 3, 1, 2))
        assertPhase(level, 150, 3.0, 6.0, 6, listOf(3, 2, 3, 2, 2))
        assertPhase(level, 195, 2.5, 5.5, 6, listOf(3, 2, 4, 2, 2))
        assertPhase(level, 240, 2.0, 5.0, 7, listOf(3, 2, 4, 3, 2))
        assertPhase(level, 285, 1.5, 4.5, 7, listOf(3, 2, 4, 3, 2))
        assertPhase(level, 600, 1.5, 4.5, 7, listOf(3, 2, 4, 3, 2))
    }

    @Test
    fun meadowFieldKeepsAtMostThreeAircraftInItsConfiguredPhasesAndFiveInTheRamp() {
        val level = Levels.meadowField
        assertTrue(level.phases.all { it.maxAircraft <= 3 })
        assertTrue((0..1200).all { level.phaseAt(it.toDouble()).maxAircraft <= 5 })
    }

    @Test
    fun theConfiguredPhasesAreUnchanged() {
        assertEquals(listOf(0.0, 45.0, 120.0), Levels.meadowField.phases.map { it.startSeconds })
        assertEquals(listOf(2, 3, 3), Levels.meadowField.phases.map { it.maxAircraft })
        assertEquals(listOf(0.0, 60.0), Levels.islandAirport.phases.map { it.startSeconds })
        assertEquals(listOf(3, 4), Levels.islandAirport.phases.map { it.maxAircraft })
        assertEquals(listOf(0.0, 60.0), Levels.savannaDawn.phases.map { it.startSeconds })
        assertEquals(listOf(4, 5), Levels.savannaDawn.phases.map { it.maxAircraft })
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
    fun meadowFieldHasAOneWayRunwayAndAHelipad() {
        val level = Levels.meadowField
        assertEquals(1, level.runways.size)
        val runway = level.runways.single()
        assertEquals(listOf(Angles.normalize(3 * kotlin.math.PI / 2)), runway.landingHeadings)
        assertEquals("27", runway.numberFor(runway.landingHeadings.single()))
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