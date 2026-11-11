package nl.hexmaster.finalapproach.domain

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class LevelConfigValidatorTest {

    private val east = PI / 2

    private fun valid() = LevelConfig(
        id = "test",
        name = "Test",
        order = 1,
        world = WorldConfig(1000.0, 600.0),
        runways = listOf(RunwayConfig("main", Vec2(450.0, 300.0), east, 400.0)),
        helipads = listOf(HelipadConfig("pad", Vec2(850.0, 300.0))),
        phases = listOf(
            DifficultyPhase(0.0, 5.0, 8.0, mapOf(AircraftType.LightProp to 2, AircraftType.Helicopter to 1), 2),
            DifficultyPhase(30.0, 4.0, 6.0, mapOf(AircraftType.Jet to 1), 3),
        ),
    )

    private fun problems(config: LevelConfig) = config.validate()

    private fun assertProblem(config: LevelConfig, vararg words: String) {
        val found = problems(config)
        assertTrue(
            found.any { p -> words.all { p.contains(it, ignoreCase = true) } },
            "Expected a problem mentioning ${words.toList()}, got $found",
        )
    }

    @Test
    fun validConfigPasses() {
        assertEquals(emptyList(), problems(valid()))
    }

    @Test
    fun noLandingZone() {
        val config = valid().copy(runways = emptyList(), helipads = emptyList())
        assertProblem(config, "landing zone is required")
    }

    @Test
    fun runwayOutsideTheViewport() {
        val config = valid().copy(runways = listOf(RunwayConfig("edge", Vec2(150.0, 300.0), east, 400.0)))
        assertProblem(config, "edge", "outside")
    }

    @Test
    fun helipadOutsideTheViewport() {
        val config = valid().copy(helipads = listOf(HelipadConfig("rim", Vec2(980.0, 300.0))))
        assertProblem(config, "rim", "outside")
    }

    @Test
    fun overlappingZonesAreNamed() {
        val config = valid().copy(helipads = listOf(HelipadConfig("onstrip", Vec2(600.0, 300.0))))
        assertProblem(config, "main", "onstrip", "overlap")
    }

    @Test
    fun rotatedRunwaysUseTheirRealShape() {
        // Two diagonal runways whose bounding boxes overlap, but whose strips do not.
        val a = RunwayConfig("a", Vec2(350.0, 300.0), PI / 4, 300.0)
        val b = RunwayConfig("b", Vec2(550.0, 300.0), PI / 4, 300.0)
        assertTrue(problems(valid().copy(runways = listOf(a, b))).none { it.contains("overlap") })
        val crossing = RunwayConfig("c", Vec2(350.0, 300.0), -PI / 4, 300.0)
        assertProblem(valid().copy(runways = listOf(a, crossing)), "'a'", "'c'", "overlap")
    }

    @Test
    fun rotatedRunwayCornerOutsideTheViewport() {
        val tilted = RunwayConfig("tilt", Vec2(500.0, 300.0), PI / 4, 860.0)
        assertProblem(valid().copy(runways = listOf(tilted), helipads = emptyList()), "tilt", "outside")
    }

    @Test
    fun minimumIntervalAboveMaximum() {
        val phase = DifficultyPhase(0.0, 9.0, 5.0, mapOf(AircraftType.LightProp to 1), 2)
        assertProblem(valid().copy(phases = listOf(phase)), "Phase 1", "minimum spawn interval")
    }

    @Test
    fun nonPositiveWeight() {
        val phase = DifficultyPhase(0.0, 5.0, 8.0, mapOf(AircraftType.LightProp to 0), 2)
        assertProblem(valid().copy(phases = listOf(phase)), "Phase 1", "weight")
    }

    @Test
    fun startTimesMustIncrease() {
        val phases = valid().phases.let { listOf(it[0], it[1].copy(startSeconds = 0.0)) }
        assertProblem(valid().copy(phases = phases), "Phase 2", "not after")
    }

    @Test
    fun firstPhaseStartsAtZero() {
        val phases = valid().phases.let { listOf(it[0].copy(startSeconds = 5.0), it[1]) }
        assertProblem(valid().copy(phases = phases), "Phase 1", "time 0")
    }

    @Test
    fun maximumBelowOne() {
        val phases = valid().phases.let { listOf(it[0].copy(maxAircraft = 0), it[1]) }
        assertProblem(valid().copy(phases = phases), "Phase 1", "at least 1")
    }

    @Test
    fun helicoptersNeedAHelipad() {
        assertProblem(valid().copy(helipads = emptyList()), "helicopters need a helipad")
    }

    @Test
    fun airplanesNeedARunway() {
        assertProblem(valid().copy(runways = emptyList()), "airplanes need a runway")
    }

    @Test
    fun separationMustBePositive() {
        assertProblem(valid().copy(world = WorldConfig(1000.0, 600.0, minimumSeparation = 0.0)), "separation")
    }

    @Test
    fun requireValidThrowsWithTheMessages() {
        val error = assertFailsWith<IllegalArgumentException> {
            valid().copy(runways = emptyList(), helipads = emptyList()).requireValid()
        }
        assertTrue(error.message.orEmpty().contains("landing zone is required"))
    }

    @Test
    fun sceneryOnARunwayOrHelipadIsRejected() {
        val onRunway = SceneryItem(SceneryKind.Hangar, Vec2(450.0, 300.0), 60.0, 60.0)
        assertProblem(valid().copy(scenery = listOf(onRunway)), "scenery", "runway 'main'")
        val onPad = SceneryItem(SceneryKind.Tree, Vec2(850.0, 300.0), 30.0, 30.0)
        assertProblem(valid().copy(scenery = listOf(onPad)), "scenery", "helipad 'pad'")
    }

    @Test
    fun sceneryInALandingZoneBesideTheRunwayIsRejected() {
        val besideThreshold = SceneryItem(SceneryKind.Windsock, Vec2(300.0, 300.0 + 32.0 + 10.0), 10.0, 10.0)
        assertProblem(valid().copy(scenery = listOf(besideThreshold)), "scenery", "landing zone")
    }

    @Test
    fun sceneryClearOfEverythingPasses() {
        val tree = SceneryItem(SceneryKind.Tree, Vec2(100.0, 80.0), 40.0, 40.0)
        assertEquals(emptyList(), problems(valid().copy(scenery = listOf(tree))))
    }
}