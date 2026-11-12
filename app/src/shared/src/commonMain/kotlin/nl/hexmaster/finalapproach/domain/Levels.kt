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

    private const val EAST = PI / 2
    private const val SOUTH = PI

    private fun world() = WorldConfig(width = LevelDefaults.WORLD_WIDTH, height = LevelDefaults.WORLD_HEIGHT)

    /** Sizes of the standing scenery, across and along its front, in world units: the grid of each sprite. */
    private val SIZES: Map<SceneryKind, Pair<Double, Double>> = mapOf(
        SceneryKind.Terminal to (140.0 to 96.0),
        SceneryKind.Tower to (72.0 to 72.0),
        SceneryKind.Hangar to (120.0 to 88.0),
        SceneryKind.Windsock to (36.0 to 82.0),
        SceneryKind.Car to (28.0 to 52.0),
        SceneryKind.FuelTruck to (32.0 to 76.0),
        SceneryKind.BaggageCart to (26.0 to 80.0),
        SceneryKind.ParkedAircraft to (SceneryDefaults.PARKED_AIRCRAFT_SIZE to SceneryDefaults.PARKED_AIRCRAFT_SIZE),
        SceneryKind.Tree to (64.0 to 64.0),
        SceneryKind.Farmhouse to (84.0 to 72.0),
        SceneryKind.Barn to (104.0 to 72.0),
        SceneryKind.Palm to (64.0 to 64.0),
        SceneryKind.Jetty to (28.0 to 96.0),
        SceneryKind.Acacia to (115.0 to 80.0),
        SceneryKind.Bush to (40.0 to 36.0),
        SceneryKind.Rock to (48.0 to 36.0),
        SceneryKind.Zebra to (32.0 to 68.0),
        SceneryKind.Elephant to (64.0 to 84.0),
    )

    /** Width of a hedge, a fence, a forest edge and a road, in world units. */
    private const val HEDGE_WIDTH = 28.0
    private const val FENCE_WIDTH = 12.0
    private const val FOREST_WIDTH = 100.0
    private const val ROAD_WIDTH = 40.0
    private const val TRACK_WIDTH = 32.0

    /** A piece of standing scenery of its usual size at [x], [y], its front pointing along [rotation]. */
    private fun item(kind: SceneryKind, x: Double, y: Double, rotation: Double = 0.0): SceneryItem {
        val (width, height) = SIZES.getValue(kind)
        return SceneryItem(kind, Vec2(x, y), width, height, rotation)
    }

    /** A flat area [width] wide (west to east) and [height] high (north to south), centred on [x], [y]. */
    private fun area(kind: SceneryKind, x: Double, y: Double, width: Double, height: Double) =
        SceneryItem(kind, Vec2(x, y), width, height)

    /** A strip of [kind], [width] across, running [length] along [rotation] through [x], [y]. */
    private fun strip(kind: SceneryKind, x: Double, y: Double, length: Double, width: Double, rotation: Double = 0.0) =
        SceneryItem(kind, Vec2(x, y), width, length, rotation)

    /**
     * Level 2: an airport on an island in the sea. A long main runway, a shorter diagonal runway
     * that crosses it (the jumbo does not fit on it) and a helipad. Around it a beach with palms,
     * a jetty on the east shore and open sea on every side.
     */
    val islandAirport: LevelConfig = LevelConfig(
        id = "island-airport",
        name = "Island",
        order = 2,
        world = world(),
        landscape = LevelScenery.Island,
        allowCrossingRunways = true,
        island = Island(Vec2(800.0, 450.0), radiusX = 560.0, radiusY = 300.0),
        runways = listOf(
            RunwayConfig("09-27", Vec2(760.0, 470.0), EAST, length = 700.0),
            RunwayConfig(
                id = "04-22",
                position = Vec2(780.0, 480.0),
                orientation = PI / 4,
                length = 400.0,
                accepted = WITHOUT_JUMBO,
            ),
        ),
        helipads = listOf(HelipadConfig(id = "h1", position = Vec2(1190.0, 360.0))),
        scenery = listOf(
            item(SceneryKind.Jetty, 1424.0, 450.0, EAST),
            area(SceneryKind.Apron, 590.0, 366.0, width = 260.0, height = 100.0),
            area(SceneryKind.Taxiway, 640.0, 427.0, width = 44.0, height = 22.0),
            item(SceneryKind.Terminal, 560.0, 262.0, SOUTH),
            item(SceneryKind.Tower, 690.0, 268.0),
            item(SceneryKind.Hangar, 1034.0, 250.0, SOUTH),
            item(SceneryKind.ParkedAircraft, 520.0, 366.0),
            item(SceneryKind.ParkedAircraft, 596.0, 366.0),
            item(SceneryKind.FuelTruck, 672.0, 346.0, EAST),
            item(SceneryKind.BaggageCart, 664.0, 394.0, EAST),
            item(SceneryKind.Windsock, 980.0, 566.0, EAST),
            item(SceneryKind.Tree, 900.0, 620.0),
            item(SceneryKind.Tree, 1160.0, 490.0),
            item(SceneryKind.Tree, 400.0, 580.0),
            item(SceneryKind.Palm, 420.0, 316.0),
            item(SceneryKind.Palm, 304.0, 440.0),
            item(SceneryKind.Palm, 336.0, 540.0),
            item(SceneryKind.Palm, 524.0, 656.0),
            item(SceneryKind.Palm, 760.0, 700.0),
            item(SceneryKind.Palm, 1080.0, 650.0),
            item(SceneryKind.Palm, 1290.0, 540.0),
            item(SceneryKind.Rock, 1310.0, 430.0),
            item(SceneryKind.Rock, 640.0, 706.0),
        ),
        phases = busyPhases(maxAircraft = 4),
    )

    /**
     * Level 3: a savanna airfield at dawn. Two parallel main runways, a smaller runway across both
     * (no jumbos) and two helipads, among acacias, rocks, a waterhole and its animals.
     */
    val savannaDawn: LevelConfig = LevelConfig(
        id = "savanna-dawn",
        name = "Savanna",
        order = 3,
        world = world(),
        theme = LevelTheme.Dawn,
        landscape = LevelScenery.Savanna,
        allowCrossingRunways = true,
        runways = listOf(
            RunwayConfig("09L-27R", Vec2(760.0, 370.0), EAST, length = 640.0),
            RunwayConfig("09R-27L", Vec2(760.0, 510.0), EAST, length = 640.0),
            RunwayConfig(
                id = "18-36",
                position = Vec2(860.0, 440.0),
                orientation = 0.0,
                length = 340.0,
                accepted = WITHOUT_JUMBO,
            ),
        ),
        helipads = listOf(
            HelipadConfig(id = "h1", position = Vec2(1280.0, 340.0)),
            HelipadConfig(id = "h2", position = Vec2(1280.0, 640.0)),
        ),
        scenery = listOf(
            strip(SceneryKind.Road, 800.0, 104.0, length = 1700.0, width = TRACK_WIDTH, rotation = 1.48),
            strip(SceneryKind.Road, 225.0, 640.0, length = 450.0, width = TRACK_WIDTH, rotation = EAST),
            area(SceneryKind.Waterhole, 1470.0, 500.0, width = 190.0, height = 130.0),
            area(SceneryKind.Apron, 600.0, 640.0, width = 300.0, height = 100.0),
            area(SceneryKind.Taxiway, 640.0, 566.0, width = 44.0, height = 48.0),
            item(SceneryKind.Tower, 424.0, 768.0),
            item(SceneryKind.Terminal, 560.0, 762.0),
            item(SceneryKind.Hangar, 724.0, 764.0),
            item(SceneryKind.ParkedAircraft, 510.0, 640.0),
            item(SceneryKind.ParkedAircraft, 586.0, 640.0),
            item(SceneryKind.FuelTruck, 682.0, 620.0, EAST),
            item(SceneryKind.BaggageCart, 690.0, 666.0, EAST),
            item(SceneryKind.Windsock, 1000.0, 600.0, EAST),
            item(SceneryKind.Acacia, 150.0, 300.0),
            item(SceneryKind.Acacia, 300.0, 250.0),
            item(SceneryKind.Acacia, 130.0, 800.0),
            item(SceneryKind.Acacia, 1130.0, 250.0),
            item(SceneryKind.Acacia, 1180.0, 800.0),
            item(SceneryKind.Acacia, 1520.0, 720.0),
            item(SceneryKind.Bush, 220.0, 760.0),
            item(SceneryKind.Bush, 80.0, 540.0),
            item(SceneryKind.Bush, 340.0, 440.0),
            item(SceneryKind.Bush, 960.0, 720.0),
            item(SceneryKind.Bush, 1000.0, 226.0),
            item(SceneryKind.Bush, 1540.0, 300.0),
            item(SceneryKind.Rock, 300.0, 830.0),
            item(SceneryKind.Rock, 90.0, 420.0),
            item(SceneryKind.Rock, 980.0, 820.0),
            item(SceneryKind.Rock, 1200.0, 495.0),
            item(SceneryKind.Acacia, 690.0, 250.0),
            item(SceneryKind.Bush, 620.0, 850.0),
            item(SceneryKind.Rock, 1400.0, 830.0),
            item(SceneryKind.Zebra, 1426.0, 612.0, 0.5),
            item(SceneryKind.Zebra, 1480.0, 640.0, 0.2),
            item(SceneryKind.Zebra, 1440.0, 700.0, 0.9),
            item(SceneryKind.Elephant, 1470.0, 372.0, SOUTH),
        ),
        phases = busyPhases(maxAircraft = 5),
    )

    /**
     * Level 1: one two-way runway in the middle, one helipad on the right, a gentle build-up. The
     * airfield lies in farmland: crop fields, hedges, a farm, two roads, a pond and a forest edge.
     */
    val meadowField: LevelConfig = LevelConfig(
        id = "meadow-field",
        name = "Meadow Field",
        order = 1,
        world = world(),
        runways = listOf(
            RunwayConfig(
                id = "09-27",
                position = Vec2(800.0, 540.0),
                orientation = EAST,
                length = 640.0,
                mode = RunwayMode.TwoWay,
            ),
        ),
        helipads = listOf(HelipadConfig(id = "h1", position = Vec2(1270.0, 540.0))),
        paddock = Bounds(left = 60.0, top = 430.0, right = 340.0, bottom = 680.0),
        scenery = listOf(
            strip(SceneryKind.Field, 300.0, 110.0, length = 180.0, width = 520.0),
            strip(SceneryKind.Field, 850.0, 100.0, length = 480.0, width = 160.0, rotation = EAST),
            strip(SceneryKind.Field, 1370.0, 120.0, length = 200.0, width = 380.0),
            strip(SceneryKind.Field, 760.0, 790.0, length = 520.0, width = 150.0, rotation = EAST),
            strip(SceneryKind.Road, 300.0, 372.0, length = 600.0, width = ROAD_WIDTH, rotation = EAST),
            strip(SceneryKind.Road, 410.0, 636.0, length = 528.0, width = ROAD_WIDTH),
            area(SceneryKind.Pond, 1340.0, 770.0, width = 240.0, height = 140.0),
            area(SceneryKind.Apron, 760.0, 372.0, width = 320.0, height = 120.0),
            area(SceneryKind.Taxiway, 760.0, 470.0, width = 44.0, height = 76.0),
            item(SceneryKind.Terminal, 700.0, 258.0, SOUTH),
            item(SceneryKind.Tower, 570.0, 268.0),
            item(SceneryKind.Hangar, 880.0, 262.0, SOUTH),
            item(SceneryKind.ParkedAircraft, 650.0, 372.0),
            item(SceneryKind.ParkedAircraft, 728.0, 372.0),
            item(SceneryKind.FuelTruck, 802.0, 356.0, EAST),
            item(SceneryKind.BaggageCart, 846.0, 404.0, EAST),
            item(SceneryKind.Car, 892.0, 346.0),
            item(SceneryKind.Windsock, 880.0, 640.0, EAST),
            item(SceneryKind.Farmhouse, 196.0, 290.0),
            item(SceneryKind.Barn, 330.0, 282.0),
            strip(SceneryKind.Hedge, 280.0, 222.0, length = 480.0, width = HEDGE_WIDTH, rotation = EAST),
            strip(SceneryKind.Hedge, 1370.0, 246.0, length = 380.0, width = HEDGE_WIDTH, rotation = EAST),
            strip(SceneryKind.Hedge, 760.0, 700.0, length = 520.0, width = HEDGE_WIDTH, rotation = EAST),
            strip(SceneryKind.Fence, 240.0, 338.0, length = 288.0, width = FENCE_WIDTH, rotation = EAST),
            strip(SceneryKind.ForestEdge, 200.0, 800.0, length = 360.0, width = FOREST_WIDTH, rotation = EAST),
            strip(SceneryKind.ForestEdge, 1540.0, 520.0, length = 420.0, width = FOREST_WIDTH),
            item(SceneryKind.Tree, 1090.0, 300.0),
            item(SceneryKind.Tree, 1400.0, 330.0),
            item(SceneryKind.Tree, 1150.0, 690.0),
            item(SceneryKind.Tree, 1460.0, 640.0),
            item(SceneryKind.Tree, 1010.0, 410.0),
            item(SceneryKind.Tree, 600.0, 646.0),
            item(SceneryKind.Tree, 1530.0, 830.0),
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
