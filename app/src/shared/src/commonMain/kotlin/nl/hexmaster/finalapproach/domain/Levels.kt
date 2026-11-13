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
    private const val WEST = 3 * PI / 2
    private const val SOUTH_WEST = 5 * PI / 4

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
     * Level 2: an airport on an island in the sea. A long main runway, a shorter one-way diagonal runway (22, approached from the north-east)
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
        island = Island(Vec2(960.0, 540.0), radiusX = 560.0, radiusY = 300.0),
        runways = listOf(
            RunwayConfig("09-27", Vec2(920.0, 560.0), EAST, length = 700.0),
            RunwayConfig(
                id = "04-22",
                position = Vec2(940.0, 570.0),
                orientation = PI / 4,
                length = 400.0,
                accepted = WITHOUT_JUMBO,
                mode = RunwayMode.OneWay(SOUTH_WEST),
            ),
        ),
        helipads = listOf(HelipadConfig(id = "h1", position = Vec2(1350.0, 450.0))),
        scenery = listOf(
            item(SceneryKind.Jetty, 1584.0, 540.0, EAST),
            area(SceneryKind.Apron, 750.0, 456.0, width = 260.0, height = 100.0),
            area(SceneryKind.Taxiway, 800.0, 517.0, width = 44.0, height = 22.0),
            item(SceneryKind.Terminal, 720.0, 352.0, SOUTH),
            item(SceneryKind.Tower, 850.0, 358.0),
            item(SceneryKind.Hangar, 1194.0, 340.0, SOUTH),
            item(SceneryKind.ParkedAircraft, 680.0, 456.0),
            item(SceneryKind.ParkedAircraft, 756.0, 456.0),
            item(SceneryKind.FuelTruck, 832.0, 436.0, EAST),
            item(SceneryKind.BaggageCart, 824.0, 484.0, EAST),
            item(SceneryKind.Windsock, 1140.0, 656.0, EAST),
            item(SceneryKind.Tree, 1060.0, 710.0),
            item(SceneryKind.Tree, 1320.0, 580.0),
            item(SceneryKind.Tree, 560.0, 670.0),
            item(SceneryKind.Palm, 580.0, 406.0),
            item(SceneryKind.Palm, 464.0, 530.0),
            item(SceneryKind.Palm, 496.0, 630.0),
            item(SceneryKind.Palm, 684.0, 746.0),
            item(SceneryKind.Palm, 920.0, 790.0),
            item(SceneryKind.Palm, 1240.0, 740.0),
            item(SceneryKind.Palm, 1450.0, 630.0),
            item(SceneryKind.Rock, 1470.0, 520.0),
            item(SceneryKind.Rock, 800.0, 796.0),
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
            RunwayConfig("09L-27R", Vec2(920.0, 460.0), EAST, length = 640.0),
            RunwayConfig("09R-27L", Vec2(920.0, 600.0), EAST, length = 640.0),
            RunwayConfig(
                id = "18-36",
                position = Vec2(1020.0, 530.0),
                orientation = 0.0,
                length = 340.0,
                accepted = WITHOUT_JUMBO,
            ),
        ),
        helipads = listOf(
            HelipadConfig(id = "h1", position = Vec2(1440.0, 430.0)),
            HelipadConfig(id = "h2", position = Vec2(1440.0, 730.0)),
        ),
        scenery = listOf(
            strip(SceneryKind.Road, 960.0, 194.0, length = 3400.0, width = TRACK_WIDTH, rotation = 1.48),
            strip(SceneryKind.Road, 305.0, 730.0, length = 610.0, width = TRACK_WIDTH, rotation = EAST),
            area(SceneryKind.Waterhole, 1630.0, 590.0, width = 190.0, height = 130.0),
            area(SceneryKind.Apron, 760.0, 730.0, width = 300.0, height = 100.0),
            area(SceneryKind.Taxiway, 800.0, 656.0, width = 44.0, height = 48.0),
            item(SceneryKind.Tower, 584.0, 858.0),
            item(SceneryKind.Terminal, 720.0, 852.0),
            item(SceneryKind.Hangar, 884.0, 854.0),
            item(SceneryKind.ParkedAircraft, 670.0, 730.0),
            item(SceneryKind.ParkedAircraft, 746.0, 730.0),
            item(SceneryKind.FuelTruck, 842.0, 710.0, EAST),
            item(SceneryKind.BaggageCart, 850.0, 756.0, EAST),
            item(SceneryKind.Windsock, 1160.0, 690.0, EAST),
            item(SceneryKind.Acacia, 310.0, 390.0),
            item(SceneryKind.Acacia, 460.0, 340.0),
            item(SceneryKind.Acacia, 290.0, 890.0),
            item(SceneryKind.Acacia, 1290.0, 340.0),
            item(SceneryKind.Acacia, 1340.0, 890.0),
            item(SceneryKind.Acacia, 1680.0, 810.0),
            item(SceneryKind.Bush, 380.0, 850.0),
            item(SceneryKind.Bush, 240.0, 630.0),
            item(SceneryKind.Bush, 500.0, 530.0),
            item(SceneryKind.Bush, 1120.0, 810.0),
            item(SceneryKind.Bush, 1160.0, 316.0),
            item(SceneryKind.Bush, 1700.0, 390.0),
            item(SceneryKind.Rock, 460.0, 920.0),
            item(SceneryKind.Rock, 250.0, 510.0),
            item(SceneryKind.Rock, 1140.0, 910.0),
            item(SceneryKind.Rock, 1360.0, 585.0),
            item(SceneryKind.Acacia, 850.0, 340.0),
            item(SceneryKind.Bush, 780.0, 940.0),
            item(SceneryKind.Rock, 1560.0, 920.0),
            item(SceneryKind.Zebra, 1586.0, 702.0, 0.5),
            item(SceneryKind.Zebra, 1640.0, 730.0, 0.2),
            item(SceneryKind.Zebra, 1600.0, 790.0, 0.9),
            item(SceneryKind.Elephant, 1630.0, 462.0, SOUTH),
            // The open savanna around the airfield.
        ),
        phases = busyPhases(maxAircraft = 5),
    )

    /**
     * Level 1: one one-way runway (27, approached from the east) in the middle, one helipad on the right, a gentle build-up. The
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
                position = Vec2(960.0, 630.0),
                orientation = EAST,
                length = 640.0,
                mode = RunwayMode.OneWay(WEST),
            ),
        ),
        helipads = listOf(HelipadConfig(id = "h1", position = Vec2(1430.0, 630.0))),
        paddock = Bounds(left = 220.0, top = 520.0, right = 500.0, bottom = 770.0),
        scenery = listOf(
            strip(SceneryKind.Field, 460.0, 200.0, length = 180.0, width = 520.0),
            strip(SceneryKind.Field, 1010.0, 190.0, length = 480.0, width = 160.0, rotation = EAST),
            strip(SceneryKind.Field, 1530.0, 210.0, length = 200.0, width = 380.0),
            strip(SceneryKind.Field, 920.0, 880.0, length = 520.0, width = 150.0, rotation = EAST),
            strip(SceneryKind.Road, 60.0, 462.0, length = 1400.0, width = ROAD_WIDTH, rotation = EAST),
            strip(SceneryKind.Road, 570.0, 951.0, length = 978.0, width = ROAD_WIDTH),
            area(SceneryKind.Pond, 1500.0, 860.0, width = 240.0, height = 140.0),
            area(SceneryKind.Apron, 920.0, 462.0, width = 320.0, height = 120.0),
            area(SceneryKind.Taxiway, 920.0, 560.0, width = 44.0, height = 76.0),
            item(SceneryKind.Terminal, 860.0, 348.0, SOUTH),
            item(SceneryKind.Tower, 730.0, 358.0),
            item(SceneryKind.Hangar, 1040.0, 352.0, SOUTH),
            item(SceneryKind.ParkedAircraft, 810.0, 462.0),
            item(SceneryKind.ParkedAircraft, 888.0, 462.0),
            item(SceneryKind.FuelTruck, 962.0, 446.0, EAST),
            item(SceneryKind.BaggageCart, 1006.0, 494.0, EAST),
            item(SceneryKind.Car, 1052.0, 436.0),
            item(SceneryKind.Windsock, 1040.0, 730.0, EAST),
            item(SceneryKind.Farmhouse, 356.0, 380.0),
            item(SceneryKind.Barn, 490.0, 372.0),
            strip(SceneryKind.Hedge, 440.0, 312.0, length = 480.0, width = HEDGE_WIDTH, rotation = EAST),
            strip(SceneryKind.Hedge, 1530.0, 336.0, length = 380.0, width = HEDGE_WIDTH, rotation = EAST),
            strip(SceneryKind.Hedge, 920.0, 790.0, length = 520.0, width = HEDGE_WIDTH, rotation = EAST),
            strip(SceneryKind.Fence, 400.0, 428.0, length = 288.0, width = FENCE_WIDTH, rotation = EAST),
            strip(SceneryKind.ForestEdge, 360.0, 890.0, length = 360.0, width = FOREST_WIDTH, rotation = EAST),
            strip(SceneryKind.ForestEdge, 1700.0, 610.0, length = 420.0, width = FOREST_WIDTH),
            item(SceneryKind.Tree, 1250.0, 390.0),
            item(SceneryKind.Tree, 1560.0, 420.0),
            item(SceneryKind.Tree, 1310.0, 780.0),
            item(SceneryKind.Tree, 1620.0, 730.0),
            item(SceneryKind.Tree, 1170.0, 500.0),
            item(SceneryKind.Tree, 760.0, 736.0),
            item(SceneryKind.Tree, 1690.0, 920.0),
            // The farmland around the airfield.
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
