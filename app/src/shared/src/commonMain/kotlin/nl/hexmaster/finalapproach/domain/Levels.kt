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
        island = Island(Vec2(1600.0, 900.0), radiusX = 560.0, radiusY = 300.0),
        runways = listOf(
            RunwayConfig("09-27", Vec2(1560.0, 920.0), EAST, length = 700.0),
            RunwayConfig(
                id = "04-22",
                position = Vec2(1580.0, 930.0),
                orientation = PI / 4,
                length = 400.0,
                accepted = WITHOUT_JUMBO,
            ),
        ),
        helipads = listOf(HelipadConfig(id = "h1", position = Vec2(1990.0, 810.0))),
        scenery = listOf(
            item(SceneryKind.Jetty, 2224.0, 900.0, EAST),
            area(SceneryKind.Apron, 1390.0, 816.0, width = 260.0, height = 100.0),
            area(SceneryKind.Taxiway, 1440.0, 877.0, width = 44.0, height = 22.0),
            item(SceneryKind.Terminal, 1360.0, 712.0, SOUTH),
            item(SceneryKind.Tower, 1490.0, 718.0),
            item(SceneryKind.Hangar, 1834.0, 700.0, SOUTH),
            item(SceneryKind.ParkedAircraft, 1320.0, 816.0),
            item(SceneryKind.ParkedAircraft, 1396.0, 816.0),
            item(SceneryKind.FuelTruck, 1472.0, 796.0, EAST),
            item(SceneryKind.BaggageCart, 1464.0, 844.0, EAST),
            item(SceneryKind.Windsock, 1780.0, 1016.0, EAST),
            item(SceneryKind.Tree, 1700.0, 1070.0),
            item(SceneryKind.Tree, 1960.0, 940.0),
            item(SceneryKind.Tree, 1200.0, 1030.0),
            item(SceneryKind.Palm, 1220.0, 766.0),
            item(SceneryKind.Palm, 1104.0, 890.0),
            item(SceneryKind.Palm, 1136.0, 990.0),
            item(SceneryKind.Palm, 1324.0, 1106.0),
            item(SceneryKind.Palm, 1560.0, 1150.0),
            item(SceneryKind.Palm, 1880.0, 1100.0),
            item(SceneryKind.Palm, 2090.0, 990.0),
            item(SceneryKind.Rock, 2110.0, 880.0),
            item(SceneryKind.Rock, 1440.0, 1156.0),
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
            RunwayConfig("09L-27R", Vec2(1560.0, 820.0), EAST, length = 640.0),
            RunwayConfig("09R-27L", Vec2(1560.0, 960.0), EAST, length = 640.0),
            RunwayConfig(
                id = "18-36",
                position = Vec2(1660.0, 890.0),
                orientation = 0.0,
                length = 340.0,
                accepted = WITHOUT_JUMBO,
            ),
        ),
        helipads = listOf(
            HelipadConfig(id = "h1", position = Vec2(2080.0, 790.0)),
            HelipadConfig(id = "h2", position = Vec2(2080.0, 1090.0)),
        ),
        scenery = listOf(
            strip(SceneryKind.Road, 1600.0, 554.0, length = 3400.0, width = TRACK_WIDTH, rotation = 1.48),
            strip(SceneryKind.Road, 625.0, 1090.0, length = 1250.0, width = TRACK_WIDTH, rotation = EAST),
            area(SceneryKind.Waterhole, 2270.0, 950.0, width = 190.0, height = 130.0),
            area(SceneryKind.Apron, 1400.0, 1090.0, width = 300.0, height = 100.0),
            area(SceneryKind.Taxiway, 1440.0, 1016.0, width = 44.0, height = 48.0),
            item(SceneryKind.Tower, 1224.0, 1218.0),
            item(SceneryKind.Terminal, 1360.0, 1212.0),
            item(SceneryKind.Hangar, 1524.0, 1214.0),
            item(SceneryKind.ParkedAircraft, 1310.0, 1090.0),
            item(SceneryKind.ParkedAircraft, 1386.0, 1090.0),
            item(SceneryKind.FuelTruck, 1482.0, 1070.0, EAST),
            item(SceneryKind.BaggageCart, 1490.0, 1116.0, EAST),
            item(SceneryKind.Windsock, 1800.0, 1050.0, EAST),
            item(SceneryKind.Acacia, 950.0, 750.0),
            item(SceneryKind.Acacia, 1100.0, 700.0),
            item(SceneryKind.Acacia, 930.0, 1250.0),
            item(SceneryKind.Acacia, 1930.0, 700.0),
            item(SceneryKind.Acacia, 1980.0, 1250.0),
            item(SceneryKind.Acacia, 2320.0, 1170.0),
            item(SceneryKind.Bush, 1020.0, 1210.0),
            item(SceneryKind.Bush, 880.0, 990.0),
            item(SceneryKind.Bush, 1140.0, 890.0),
            item(SceneryKind.Bush, 1760.0, 1170.0),
            item(SceneryKind.Bush, 1800.0, 676.0),
            item(SceneryKind.Bush, 2340.0, 750.0),
            item(SceneryKind.Rock, 1100.0, 1280.0),
            item(SceneryKind.Rock, 890.0, 870.0),
            item(SceneryKind.Rock, 1780.0, 1270.0),
            item(SceneryKind.Rock, 2000.0, 945.0),
            item(SceneryKind.Acacia, 1490.0, 700.0),
            item(SceneryKind.Bush, 1420.0, 1300.0),
            item(SceneryKind.Rock, 2200.0, 1280.0),
            item(SceneryKind.Zebra, 2226.0, 1062.0, 0.5),
            item(SceneryKind.Zebra, 2280.0, 1090.0, 0.2),
            item(SceneryKind.Zebra, 2240.0, 1150.0, 0.9),
            item(SceneryKind.Elephant, 2270.0, 822.0, SOUTH),
            // The open savanna around the airfield.
            item(SceneryKind.Acacia, 400.0, 600.0),
            item(SceneryKind.Acacia, 700.0, 1500.0),
            item(SceneryKind.Acacia, 1500.0, 1650.0),
            item(SceneryKind.Acacia, 2500.0, 1600.0),
            item(SceneryKind.Acacia, 2700.0, 700.0),
            item(SceneryKind.Acacia, 3000.0, 1300.0),
            item(SceneryKind.Bush, 250.0, 1000.0),
            item(SceneryKind.Bush, 900.0, 1600.0),
            item(SceneryKind.Bush, 2000.0, 1700.0),
            item(SceneryKind.Bush, 2900.0, 1000.0),
            item(SceneryKind.Bush, 3100.0, 500.0),
            item(SceneryKind.Rock, 550.0, 1300.0),
            item(SceneryKind.Rock, 2800.0, 1500.0),
            item(SceneryKind.Rock, 150.0, 1650.0),
            item(SceneryKind.Rock, 3050.0, 1700.0),
            item(SceneryKind.Zebra, 2900.0, 1580.0, 0.4),
            item(SceneryKind.Zebra, 2960.0, 1620.0, 0.1),
            item(SceneryKind.Elephant, 500.0, 950.0, SOUTH),
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
                position = Vec2(1600.0, 990.0),
                orientation = EAST,
                length = 640.0,
                mode = RunwayMode.TwoWay,
            ),
        ),
        helipads = listOf(HelipadConfig(id = "h1", position = Vec2(2070.0, 990.0))),
        paddock = Bounds(left = 860.0, top = 880.0, right = 1140.0, bottom = 1130.0),
        scenery = listOf(
            strip(SceneryKind.Field, 1100.0, 560.0, length = 180.0, width = 520.0),
            strip(SceneryKind.Field, 1650.0, 550.0, length = 480.0, width = 160.0, rotation = EAST),
            strip(SceneryKind.Field, 2170.0, 570.0, length = 200.0, width = 380.0),
            strip(SceneryKind.Field, 1560.0, 1240.0, length = 520.0, width = 150.0, rotation = EAST),
            strip(SceneryKind.Road, 700.0, 822.0, length = 1400.0, width = ROAD_WIDTH, rotation = EAST),
            strip(SceneryKind.Road, 1210.0, 1311.0, length = 978.0, width = ROAD_WIDTH),
            area(SceneryKind.Pond, 2140.0, 1220.0, width = 240.0, height = 140.0),
            area(SceneryKind.Apron, 1560.0, 822.0, width = 320.0, height = 120.0),
            area(SceneryKind.Taxiway, 1560.0, 920.0, width = 44.0, height = 76.0),
            item(SceneryKind.Terminal, 1500.0, 708.0, SOUTH),
            item(SceneryKind.Tower, 1370.0, 718.0),
            item(SceneryKind.Hangar, 1680.0, 712.0, SOUTH),
            item(SceneryKind.ParkedAircraft, 1450.0, 822.0),
            item(SceneryKind.ParkedAircraft, 1528.0, 822.0),
            item(SceneryKind.FuelTruck, 1602.0, 806.0, EAST),
            item(SceneryKind.BaggageCart, 1646.0, 854.0, EAST),
            item(SceneryKind.Car, 1692.0, 796.0),
            item(SceneryKind.Windsock, 1680.0, 1090.0, EAST),
            item(SceneryKind.Farmhouse, 996.0, 740.0),
            item(SceneryKind.Barn, 1130.0, 732.0),
            strip(SceneryKind.Hedge, 1080.0, 672.0, length = 480.0, width = HEDGE_WIDTH, rotation = EAST),
            strip(SceneryKind.Hedge, 2170.0, 696.0, length = 380.0, width = HEDGE_WIDTH, rotation = EAST),
            strip(SceneryKind.Hedge, 1560.0, 1150.0, length = 520.0, width = HEDGE_WIDTH, rotation = EAST),
            strip(SceneryKind.Fence, 1040.0, 788.0, length = 288.0, width = FENCE_WIDTH, rotation = EAST),
            strip(SceneryKind.ForestEdge, 1000.0, 1250.0, length = 360.0, width = FOREST_WIDTH, rotation = EAST),
            strip(SceneryKind.ForestEdge, 2340.0, 970.0, length = 420.0, width = FOREST_WIDTH),
            item(SceneryKind.Tree, 1890.0, 750.0),
            item(SceneryKind.Tree, 2200.0, 780.0),
            item(SceneryKind.Tree, 1950.0, 1140.0),
            item(SceneryKind.Tree, 2260.0, 1090.0),
            item(SceneryKind.Tree, 1810.0, 860.0),
            item(SceneryKind.Tree, 1400.0, 1096.0),
            item(SceneryKind.Tree, 2330.0, 1280.0),
            // The farmland around the airfield.
            strip(SceneryKind.Field, 400.0, 250.0, length = 300.0, width = 560.0),
            strip(SceneryKind.Field, 2750.0, 230.0, length = 260.0, width = 600.0),
            strip(SceneryKind.Field, 400.0, 1150.0, length = 400.0, width = 520.0),
            strip(SceneryKind.Field, 2850.0, 1300.0, length = 500.0, width = 400.0),
            strip(SceneryKind.Field, 2300.0, 1620.0, length = 600.0, width = 200.0, rotation = EAST),
            strip(SceneryKind.Hedge, 2850.0, 1020.0, length = 400.0, width = HEDGE_WIDTH, rotation = EAST),
            strip(SceneryKind.ForestEdge, 3150.0, 1100.0, length = 1200.0, width = FOREST_WIDTH),
            strip(SceneryKind.ForestEdge, 50.0, 1300.0, length = 800.0, width = FOREST_WIDTH),
            item(SceneryKind.Farmhouse, 2900.0, 520.0),
            item(SceneryKind.Tree, 300.0, 600.0),
            item(SceneryKind.Tree, 600.0, 520.0),
            item(SceneryKind.Tree, 2600.0, 560.0),
            item(SceneryKind.Tree, 2950.0, 700.0),
            item(SceneryKind.Tree, 1700.0, 1500.0),
            item(SceneryKind.Tree, 500.0, 1600.0),
            item(SceneryKind.Tree, 2750.0, 1700.0),
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
