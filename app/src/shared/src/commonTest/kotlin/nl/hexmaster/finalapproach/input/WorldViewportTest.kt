package nl.hexmaster.finalapproach.input

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftSpeeds
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.AircraftTypeSpec
import nl.hexmaster.finalapproach.domain.DefaultAircraftSpecs
import nl.hexmaster.finalapproach.domain.LevelDefaults
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.engine.GameEngine
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorldViewportTest {

    @Test
    fun aWideScreenShowsTheFullHeightCentredHorizontally() {
        val viewport = WorldViewport(1280.0, 720.0, 2400.0, 1080.0)
        assertEquals(1.5, viewport.scale)
        assertEquals(Vec2(240.0, 0.0), viewport.offset)
        assertEquals(Vec2(2160.0, 1080.0), viewport.toScreen(Vec2(1280.0, 720.0)))
    }

    @Test
    fun aTallScreenShowsTheFullWidthCentredVertically() {
        val viewport = WorldViewport(1280.0, 720.0, 1280.0, 1000.0)
        assertEquals(1.0, viewport.scale)
        assertEquals(Vec2(0.0, 140.0), viewport.offset)
    }

    @Test
    fun aPointSurvivesTheRoundTrip() {
        val viewport = WorldViewport(1280.0, 720.0, 2340.0, 1080.0)
        val point = Vec2(321.5, 654.25)
        val back = viewport.toWorld(viewport.toScreen(point))
        assertTrue(abs(back.x - point.x) < 1e-9 && abs(back.y - point.y) < 1e-9, "$back")
        assertEquals(72.0, viewport.toScreenLength(viewport.toWorldLength(72.0)), 1e-9)
    }

    /** The reference phone: 800 by 360dp in landscape, taken at one pixel per dp. */
    private val phoneWidth = 800.0
    private val phoneHeight = 360.0
    private val phone = WorldViewport(LevelDefaults.WORLD_WIDTH, LevelDefaults.WORLD_HEIGHT, phoneWidth, phoneHeight)
    private val phoneBeforeZoom = WorldViewport(LevelDefaults.BASE_WORLD_WIDTH, LevelDefaults.BASE_WORLD_HEIGHT, phoneWidth, phoneHeight)

    @Test
    fun onThePhoneEveryAircraftIsDrawnAsLargeAsBeforeTheZoom() {
        assertEquals(1.0 / 3.0, phone.scale, 1e-12)
        val formerRadius = mapOf(
            AircraftType.Helicopter to 26.0,
            AircraftType.LightProp to 24.0,
            AircraftType.Turboprop to 28.0,
            AircraftType.Jet to 30.0,
            AircraftType.Jumbo to 22.0,
        )
        AircraftType.entries.forEach { type ->
            val before = phoneBeforeZoom.toScreenLength(formerRadius.getValue(type) * AircraftTypeSpec.SPRITE_SIZE_PER_RADIUS)
            val now = phone.toScreenLength(DefaultAircraftSpecs.all.getValue(type).spriteSize)
            assertEquals(before, now, 1e-9, "$type")
        }
    }

    @Test
    fun onThePhoneAircraftMoveAndWarnAsBeforeTheZoom() {
        assertEquals(phoneBeforeZoom.toScreenLength(AircraftSpeeds.LIGHT_PROP), phone.toScreenLength(DefaultAircraftSpecs.lightProp.speed), 1e-9)
        assertEquals(phoneBeforeZoom.toScreenLength(150.0), phone.toScreenLength(LevelDefaults.WARNING_DISTANCE), 1e-9)
        assertEquals(phoneBeforeZoom.toScreenLength(160.0), phone.toScreenLength(LevelDefaults.HUD_BAND), 1e-9)
        assertEquals(phoneBeforeZoom.toScreenLength(60.0), phone.toScreenLength(LevelDefaults.SPAWN_MARGIN), 1e-9)
    }

    @Test
    fun onThePhoneATapThirtySixDpFromTheSmallestAircraftPicksIt() {
        val pickRadiusDp = 72.0
        val level = Levels.meadowField.let { it.copy(spawn = it.spawn.copy(firstSpawnDelay = 10_000.0)) }
        val smallest = AircraftType.entries.minBy { DefaultAircraftSpecs.all.getValue(it).spriteSize }
        val at = Vec2(400.0, 300.0)
        val engine = GameEngine(level, seed = 1).apply { place(listOf(Aircraft(1, smallest, at, 0.0))) }
        val tap = phone.toWorld(phone.toScreen(at) + Vec2(36.0, 0.0))
        assertEquals(216.0, phone.toWorldLength(pickRadiusDp), 1e-9)
        assertEquals(1, engine.select(tap, phone.toWorldLength(pickRadiusDp)))
    }

    @Test
    fun onThePhoneTheAirfieldIsSmallerThanBeforeAndStillReadable() {
        // Airfield sizes stay in world units; the 1920 x 1080 world is 1.5 times the original, so on screen they shrink by that.
        val original = WorldViewport(LevelDefaults.BASE_WORLD_WIDTH, LevelDefaults.BASE_WORLD_HEIGHT, phoneWidth, phoneHeight)
        Levels.all.forEach { level ->
            level.runways.forEach {
                assertEquals(original.toScreenLength(it.width) / 1.5, phone.toScreenLength(it.width), 1e-9, "${level.id}/${it.id}")
                assertEquals(original.toScreenLength(it.length) / 1.5, phone.toScreenLength(it.length), 1e-9, "${level.id}/${it.id}")
            }
        }
        assertEquals(64.0 / 3.0, phone.toScreenLength(LevelDefaults.RUNWAY_WIDTH), 1e-9)
        assertEquals(112.0 / 3.0, phone.toScreenLength(LevelDefaults.HELIPAD_RADIUS * 2), 1e-9)
    }
}
