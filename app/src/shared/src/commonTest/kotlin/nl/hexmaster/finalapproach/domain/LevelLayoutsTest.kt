package nl.hexmaster.finalapproach.domain

import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LevelLayoutsTest {
    private val one = Levels.meadowField
    private val two = Levels.islandAirport
    private val three = Levels.savannaDawn

    @Test
    fun cataloguePlaysInOrder() {
        assertEquals(listOf(one, two, three), Levels.all)
        assertEquals(listOf(1, 2, 3), Levels.all.map { it.order })
        assertEquals(two, Levels.byId(two.id))
    }

    @Test
    fun everyLevelIsValid() {
        Levels.all.forEach { assertEquals(emptyList(), it.validate(), it.id) }
    }

    @Test
    fun runwaysDefaultToAllAirplanesAndRefuseTheHelicopter() {
        val runway = one.runways.single()
        AircraftType.entries.filter { it.kind == AircraftKind.Airplane }.forEach { assertTrue(runway.accepts(it)) }
        assertFalse(runway.accepts(AircraftType.Helicopter))
    }

    @Test
    fun helipadsAcceptOnlyTheHelicopter() {
        val pad = one.helipads.single()
        assertTrue(pad.accepts(AircraftType.Helicopter))
        AircraftType.entries.filter { it != AircraftType.Helicopter }.forEach { assertFalse(pad.accepts(it)) }
    }

    @Test
    fun helicoptersNeverUseRunwaysOnAnyLevel() {
        Levels.all.forEach { level ->
            level.runways.forEach { assertFalse(it.accepts(AircraftType.Helicopter), "${level.id}/${it.id}") }
            level.helipads.forEach { pad ->
                AircraftType.entries.filter { it.kind == AircraftKind.Airplane }.forEach { assertFalse(pad.accepts(it)) }
            }
        }
    }

    @Test
    fun levelOneHasOneRunwayAndOneHelipad() {
        assertEquals(1, one.runways.size)
        assertEquals(1, one.helipads.size)
        assertEquals(2, LandingZones.of(one).size)
    }

    @Test
    fun levelTwoHasACrossingDiagonalRunwayThatRefusesTheJumbo() {
        val (main, diagonal) = two.runways
        assertTrue(Overlap.rects(main.shape, diagonal.shape))
        assertTrue(main.length > diagonal.length)
        val offAxis = abs(Angles.normalize(diagonal.orientation)) % (PI / 2)
        assertTrue(offAxis > 0.01 && offAxis < PI / 2 - 0.01, "the diagonal is not at 0 or 90 degrees")
        assertTrue(main.accepts(AircraftType.Jumbo))
        assertFalse(diagonal.accepts(AircraftType.Jumbo))
        assertTrue(diagonal.accepts(AircraftType.Jet))
        assertEquals(1, two.helipads.size)
    }

    @Test
    fun levelTwoStandsEntirelyOnTheIsland() {
        val island = requireNotNull(two.island)
        assertEquals(Vec2(two.world.width / 2, two.world.height / 2), island.center)
        two.runways.flatMap { it.shape.corners }.forEach { assertTrue(island.contains(it), "runway corner $it") }
        two.helipads.forEach { pad ->
            val c = pad.position
            val r = pad.radius
            listOf(Vec2(c.x - r, c.y), Vec2(c.x + r, c.y), Vec2(c.x, c.y - r), Vec2(c.x, c.y + r))
                .forEach { assertTrue(island.contains(it), "helipad edge $it") }
        }
        // Everything stands on the island, except the jetty, which reaches from the beach into the sea.
        two.scenery.filterNot { it.kind == SceneryKind.Jetty }
            .forEach { assertTrue(island.contains(it.position), "${it.kind} at ${it.position}") }
        val jetty = two.scenery.single { it.kind == SceneryKind.Jetty }
        assertFalse(island.contains(jetty.position), "the jetty lies in the sea")
        assertTrue(jetty.shape.corners.any { island.contains(it, margin = -SceneryDefaults.BEACH_WIDTH) }, "the jetty starts on the beach")
    }

    @Test
    fun theSeaIsWiderThanBeforeOnEverySide() {
        val island = requireNotNull(two.island)
        val world = two.world
        // Before the zoom-out: 120 units of sea left and right, 90 above and below.
        assertTrue(island.center.x - island.radiusX > 120.0)
        assertTrue(world.width - island.center.x - island.radiusX > 120.0)
        assertTrue(island.center.y - island.radiusY > 90.0)
        assertTrue(world.height - island.center.y - island.radiusY > 90.0)
    }

    @Test
    fun runwaysAndHelipadsAreWhatTheyWereBeforeTheZoom() {
        fun summary(level: LevelConfig) = level.runways.map { Triple(it.id, it.length, it.accepted) } to level.helipads.map { it.id }
        val all = LevelDefaults.ALL_AIRPLANES
        val noJumbo = all - AircraftType.Jumbo
        assertEquals(listOf(Triple("09-27", 640.0, all)) to listOf("h1"), summary(one))
        assertEquals(listOf(Triple("09-27", 700.0, all), Triple("04-22", 400.0, noJumbo)) to listOf("h1"), summary(two))
        assertEquals(
            listOf(Triple("09L-27R", 640.0, all), Triple("09R-27L", 640.0, all), Triple("18-36", 340.0, noJumbo)) to listOf("h1", "h2"),
            summary(three),
        )
        assertEquals(listOf(PI / 2), one.runways.map { it.orientation })
        assertEquals(listOf(PI / 2, PI / 4), two.runways.map { it.orientation })
        assertEquals(listOf(PI / 2, PI / 2, 0.0), three.runways.map { it.orientation })
        fun mode(level: LevelConfig, id: String) = level.runways.first { it.id == id }.mode
        assertEquals(RunwayMode.OneWay(3 * PI / 2), mode(one, "09-27"))
        assertEquals(RunwayMode.TwoWay, mode(two, "09-27"))
        assertEquals(RunwayMode.OneWay(5 * PI / 4), mode(two, "04-22"))
        assertEquals("22", two.runways.first { it.id == "04-22" }.let { it.numberFor(it.landingHeadings.single()) })
        three.runways.forEach { assertEquals(RunwayMode.TwoWay, it.mode, it.id) }
    }

    @Test
    fun theAirfieldLiesInTheMiddleWithSurroundingsOnEverySide() {
        Levels.all.forEach { level ->
            val world = level.world
            val points = level.runways.flatMap { it.shape.corners } +
                level.helipads.flatMap { listOf(it.position + Vec2(-it.radius, -it.radius), it.position + Vec2(it.radius, it.radius)) }
            val left = points.minOf { it.x }
            val right = world.width - points.maxOf { it.x }
            val top = points.minOf { it.y }
            val bottom = world.height - points.maxOf { it.y }
            listOf(left, right, top, bottom).forEach { assertTrue(it >= 200.0, "${level.id}: only $it units of surroundings") }
            // Centred in the part of the world below the HUD band, give or take a sixth of it.
            val middleY = (LevelDefaults.HUD_BAND + world.height) / 2
            val fieldY = (points.minOf { it.y } + points.maxOf { it.y }) / 2
            assertTrue(abs(fieldY - middleY) <= (world.height - LevelDefaults.HUD_BAND) / 6, "${level.id}: field centre at y $fieldY")
            val fieldX = (points.minOf { it.x } + points.maxOf { it.x }) / 2
            assertTrue(abs(fieldX - world.width / 2) <= world.width / 6, "${level.id}: field centre at x $fieldX")
        }
    }

    @Test
    fun everyApronHoldsAParkedVehicleAndAParkedAircraft() {
        val vehicles = setOf(SceneryKind.Car, SceneryKind.FuelTruck, SceneryKind.BaggageCart)
        Levels.all.forEach { level ->
            val aprons = level.scenery.filter { it.kind == SceneryKind.Apron }
            assertTrue(aprons.isNotEmpty(), "${level.id} has no apron")
            aprons.forEach { apron ->
                val on = level.scenery.filter { it !== apron && apron.shape.contains(it.position) }
                assertTrue(on.any { it.kind in vehicles }, "${level.id}: no vehicle on the apron")
                assertTrue(on.any { it.kind == SceneryKind.ParkedAircraft }, "${level.id}: no parked aircraft on the apron")
            }
        }
    }

    @Test
    fun parkedAircraftAreSmallerThanAnyAircraftInPlay() {
        assertEquals(67.5, SceneryDefaults.PARKED_AIRCRAFT_SIZE)
        val smallestInPlay = DefaultAircraftSpecs.all.values.minOf { it.spriteSize }
        Levels.all.forEach { level ->
            level.scenery.filter { it.kind == SceneryKind.ParkedAircraft }.forEach {
                assertEquals(SceneryDefaults.PARKED_AIRCRAFT_SIZE, it.width, "${level.id}")
                assertEquals(SceneryDefaults.PARKED_AIRCRAFT_SIZE, it.height, "${level.id}")
                assertTrue(it.width < smallestInPlay && it.height < smallestInPlay, "${level.id}")
            }
        }
    }

    @Test
    fun onlyFlatGroundLiesUnderTheHudBand() {
        Levels.all.forEach { level ->
            level.scenery.filterNot { it.isGround }.forEach { item ->
                item.shape.corners.forEach {
                    assertTrue(it.y >= LevelDefaults.HUD_BAND, "${level.id}: ${item.kind} at ${item.position} is in the HUD band")
                }
            }
        }
        val paddock = requireNotNull(one.paddock)
        assertTrue(paddock.top >= LevelDefaults.HUD_BAND, "the paddock, with its cows and farmer, is in the HUD band")
    }

    @Test
    fun animalsPeopleAndVehiclesAreLargeEnoughToReadOnAPhone() {
        // 10dp on the 800 by 360dp reference phone, where a world unit is 0.2dp.
        val minimum = 50.0
        val kinds = setOf(
            SceneryKind.Car, SceneryKind.FuelTruck, SceneryKind.BaggageCart, SceneryKind.ParkedAircraft,
            SceneryKind.Zebra, SceneryKind.Elephant,
        )
        Levels.all.forEach { level ->
            level.scenery.filter { it.kind in kinds }.forEach {
                assertTrue(maxOf(it.width, it.height) >= minimum, "${level.id}: ${it.kind} is ${maxOf(it.width, it.height)} long")
            }
        }
    }

    @Test
    fun theIslandAndTheSavannaHaveTheirSurroundings() {
        val islandKinds = two.scenery.map { it.kind }.toSet()
        assertTrue(islandKinds.containsAll(setOf(SceneryKind.Palm, SceneryKind.Jetty)), "$islandKinds")
        val savannaKinds = three.scenery.map { it.kind }.toSet()
        val expected = setOf(
            SceneryKind.Acacia, SceneryKind.Bush, SceneryKind.Rock, SceneryKind.Road,
            SceneryKind.Waterhole, SceneryKind.Zebra, SceneryKind.Elephant,
        )
        assertTrue(savannaKinds.containsAll(expected), "missing ${expected - savannaKinds}")
        assertTrue(three.scenery.count { it.kind == SceneryKind.Zebra } >= 3, "a small herd of zebras")
    }

    @Test
    fun levelThreeHasTwoParallelMainsCrossedByASmallerRunway() {
        val (a, b, crossing) = three.runways
        assertEquals(a.orientation, b.orientation)
        assertFalse(Overlap.rects(a.shape, b.shape))
        assertTrue(Overlap.rects(a.shape, crossing.shape))
        assertTrue(Overlap.rects(b.shape, crossing.shape))
        assertTrue(crossing.length < a.length)
        assertFalse(crossing.accepts(AircraftType.Jumbo))
        assertTrue(a.accepts(AircraftType.Jumbo))
        assertEquals(2, three.helipads.size)
        assertEquals(LevelTheme.Dawn, three.theme)
    }

    @Test
    fun everyZoneIsInTheWorldAndClearOfTheHudBand() {
        Levels.all.forEach { level ->
            val world = level.world.viewport
            val points = level.runways.flatMap { it.shape.corners } +
                LandingZones.of(level).filterIsInstance<LandingZone.Runway>().flatMap { it.area.corners } +
                level.helipads.flatMap { pad ->
                    val r = pad.radius
                    listOf(Vec2(pad.position.x - r, pad.position.y - r), Vec2(pad.position.x + r, pad.position.y + r))
                }
            points.forEach {
                assertTrue(world.contains(it), "${level.id}: $it is outside the world")
                assertTrue(it.y >= LevelDefaults.HUD_BAND, "${level.id}: $it is in the HUD band")
            }
        }
    }

    @Test
    fun paddockStaysClearOfZones() {
        val paddock = requireNotNull(one.paddock)
        val rect = OrientedRect(
            Vec2((paddock.left + paddock.right) / 2, (paddock.top + paddock.bottom) / 2),
            0.0,
            paddock.bottom - paddock.top,
            paddock.right - paddock.left,
        )
        LandingZones.of(one).forEach { zone ->
            val clear = when (zone) {
                is LandingZone.Runway -> !Overlap.rects(rect, zone.area)
                is LandingZone.Helipad -> !Overlap.rectCircle(rect, zone.area)
            }
            assertTrue(clear, "paddock touches ${zone.id}")
        }
    }
}
