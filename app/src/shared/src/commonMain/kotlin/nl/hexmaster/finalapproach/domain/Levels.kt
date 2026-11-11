package nl.hexmaster.finalapproach.domain

import kotlin.math.PI

/** The levels of the game, in play order. */
object Levels {

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

    val all: List<LevelConfig> = listOf(meadowField)
}
