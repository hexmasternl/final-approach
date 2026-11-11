package nl.hexmaster.finalapproach.domain

import kotlin.math.PI

/** The levels of the game, in play order. */
object Levels {

    /** The level with this [id], or null. */
    fun byId(id: String): LevelConfig? = all.firstOrNull { it.id == id }

    private val WITHOUT_JUMBO: Set<AircraftType> =
        LevelDefaults.ALL_AIRPLANES - AircraftType.Jumbo

    private fun busyPhases(maxAircraft: Int): List<DifficultyPhase> = listOf(
        DifficultyPhase(
            startSeconds = 0.0,
            spawnIntervalMin = 6.0,
            spawnIntervalMax = 9.0,
            weights = mapOf(AircraftType.LightProp to 3, AircraftType.Turboprop to 2, AircraftType.Helicopter to 1),
            maxAircraft = maxAircraft - 1,
        ),
        DifficultyPhase(
            startSeconds = 60.0,
            spawnIntervalMin = 4.0,
            spawnIntervalMax = 7.0,
            weights = mapOf(
                AircraftType.LightProp to 3,
                AircraftType.Turboprop to 2,
                AircraftType.Jet to 2,
                AircraftType.Jumbo to 1,
                AircraftType.Helicopter to 2,
            ),
            maxAircraft = maxAircraft,
        ),
    )

    /**
     * Level 2: an airport on an island in the sea. A long main runway, a shorter diagonal runway
     * that crosses it (the jumbo does not fit on it) and a helipad.
     */
    val islandAirport: LevelConfig = LevelConfig(
        id = "island-airport",
        name = "Island",
        order = 2,
        world = WorldConfig(width = 1280.0, height = 720.0),
        landscape = LevelScenery.Island,
        allowCrossingRunways = true,
        island = Island(Vec2(640.0, 400.0), radiusX = 520.0, radiusY = 270.0),
        runways = listOf(
            RunwayConfig("09-27", Vec2(600.0, 410.0), PI / 2, length = 700.0),
            RunwayConfig(
                id = "04-22",
                position = Vec2(620.0, 420.0),
                orientation = PI / 4,
                length = 400.0,
                accepted = WITHOUT_JUMBO,
            ),
        ),
        helipads = listOf(HelipadConfig(id = "h1", position = Vec2(1030.0, 300.0))),
        scenery = listOf(
            SceneryItem(SceneryKind.Tower, Vec2(420.0, 250.0), width = 40.0, height = 40.0),
            SceneryItem(SceneryKind.Terminal, Vec2(560.0, 250.0), width = 120.0, height = 60.0),
            SceneryItem(SceneryKind.Hangar, Vec2(900.0, 230.0), width = 120.0, height = 80.0),
            SceneryItem(SceneryKind.Windsock, Vec2(900.0, 500.0), width = 24.0, height = 24.0),
            SceneryItem(SceneryKind.Tree, Vec2(260.0, 520.0), width = 44.0, height = 44.0),
            SceneryItem(SceneryKind.Tree, Vec2(330.0, 570.0), width = 44.0, height = 44.0),
            SceneryItem(SceneryKind.Tree, Vec2(1000.0, 560.0), width = 44.0, height = 44.0),
        ),
        phases = busyPhases(maxAircraft = 4),
    )

    /**
     * Level 3: a savanna airfield at dawn. Two parallel main runways, a smaller runway across both
     * (no jumbos) and two helipads.
     */
    val savannaDawn: LevelConfig = LevelConfig(
        id = "savanna-dawn",
        name = "Savanna",
        order = 3,
        world = WorldConfig(width = 1280.0, height = 720.0),
        theme = LevelTheme.Dawn,
        landscape = LevelScenery.Savanna,
        allowCrossingRunways = true,
        runways = listOf(
            RunwayConfig("09L-27R", Vec2(600.0, 260.0), PI / 2, length = 640.0),
            RunwayConfig("09R-27L", Vec2(600.0, 400.0), PI / 2, length = 640.0),
            RunwayConfig(
                id = "18-36",
                position = Vec2(700.0, 330.0),
                orientation = 0.0,
                length = 340.0,
                accepted = WITHOUT_JUMBO,
            ),
        ),
        helipads = listOf(
            HelipadConfig(id = "h1", position = Vec2(1120.0, 230.0)),
            HelipadConfig(id = "h2", position = Vec2(1120.0, 540.0)),
        ),
        scenery = listOf(
            SceneryItem(SceneryKind.Tower, Vec2(330.0, 560.0), width = 40.0, height = 40.0),
            SceneryItem(SceneryKind.Terminal, Vec2(480.0, 590.0), width = 120.0, height = 60.0),
            SceneryItem(SceneryKind.Hangar, Vec2(700.0, 600.0), width = 120.0, height = 80.0),
            SceneryItem(SceneryKind.Tree, Vec2(90.0, 200.0), width = 56.0, height = 56.0),
            SceneryItem(SceneryKind.Tree, Vec2(150.0, 470.0), width = 56.0, height = 56.0),
            SceneryItem(SceneryKind.Tree, Vec2(1210.0, 380.0), width = 56.0, height = 56.0),
            SceneryItem(SceneryKind.Tree, Vec2(960.0, 650.0), width = 48.0, height = 48.0),
        ),
        phases = busyPhases(maxAircraft = 5),
    )

    /** Level 1: one two-way runway in the middle, one helipad on the right, a gentle build-up. */
    val meadowField: LevelConfig = LevelConfig(
        id = "meadow-field",
        name = "Meadow Field",
        order = 1,
        world = WorldConfig(width = 1280.0, height = 720.0),
        runways = listOf(
            RunwayConfig(
                id = "09-27",
                position = Vec2(640.0, 400.0),
                orientation = PI / 2,
                length = 640.0,
                mode = RunwayMode.TwoWay,
            ),
        ),
        helipads = listOf(HelipadConfig(id = "h1", position = Vec2(1110.0, 400.0))),
        paddock = Bounds(left = 40.0, top = 220.0, right = 250.0, bottom = 420.0),
        scenery = listOf(
            SceneryItem(SceneryKind.Apron, Vec2(560.0, 230.0), width = 220.0, height = 90.0),
            SceneryItem(SceneryKind.Taxiway, Vec2(600.0, 310.0), width = 40.0, height = 70.0),
            SceneryItem(SceneryKind.Terminal, Vec2(470.0, 150.0), width = 120.0, height = 60.0),
            SceneryItem(SceneryKind.Tower, Vec2(370.0, 160.0), width = 40.0, height = 40.0),
            SceneryItem(SceneryKind.Hangar, Vec2(800.0, 210.0), width = 120.0, height = 80.0),
            SceneryItem(SceneryKind.Windsock, Vec2(700.0, 485.0), width = 24.0, height = 24.0),
            SceneryItem(SceneryKind.Tree, Vec2(80.0, 80.0), width = 48.0, height = 48.0),
            SceneryItem(SceneryKind.Tree, Vec2(140.0, 640.0), width = 48.0, height = 48.0),
            SceneryItem(SceneryKind.Tree, Vec2(250.0, 570.0), width = 40.0, height = 40.0),
            SceneryItem(SceneryKind.Tree, Vec2(1200.0, 90.0), width = 48.0, height = 48.0),
            SceneryItem(SceneryKind.Tree, Vec2(1180.0, 650.0), width = 48.0, height = 48.0),
            SceneryItem(SceneryKind.Tree, Vec2(980.0, 610.0), width = 40.0, height = 40.0),
        ),
        phases = listOf(
            DifficultyPhase(
                startSeconds = 0.0,
                spawnIntervalMin = 6.0,
                spawnIntervalMax = 9.0,
                weights = mapOf(AircraftType.LightProp to 3, AircraftType.Helicopter to 1),
                maxAircraft = 2,
            ),
            DifficultyPhase(
                startSeconds = 45.0,
                spawnIntervalMin = 5.0,
                spawnIntervalMax = 8.0,
                weights = mapOf(
                    AircraftType.LightProp to 3,
                    AircraftType.Helicopter to 2,
                    AircraftType.Turboprop to 2,
                ),
                maxAircraft = 3,
            ),
            DifficultyPhase(
                startSeconds = 120.0,
                spawnIntervalMin = 4.0,
                spawnIntervalMax = 7.0,
                weights = mapOf(
                    AircraftType.LightProp to 3,
                    AircraftType.Helicopter to 2,
                    AircraftType.Turboprop to 2,
                    AircraftType.Jet to 2,
                    AircraftType.Jumbo to 1,
                ),
                maxAircraft = 3,
            ),
        ),
    )

    val all: List<LevelConfig> = listOf(meadowField, islandAirport, savannaDawn)
}
