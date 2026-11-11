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
