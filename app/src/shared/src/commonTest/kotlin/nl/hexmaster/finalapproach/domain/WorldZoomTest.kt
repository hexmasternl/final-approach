package nl.hexmaster.finalapproach.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The world is zoomed out by one factor, and everything about aircraft follows it. */
class WorldZoomTest {
    private val zoom = LevelDefaults.WORLD_ZOOM

    @Test
    fun everyLevelIsTheFormerWorldTimesTheOneZoomFactor() {
        assertEquals(1.5, zoom)
        assertEquals((1.0 + 2.0) / 2.0, zoom)
        Levels.all.forEach { level ->
            assertEquals(1280.0 * zoom, level.world.width, level.id)
            assertEquals(720.0 * zoom, level.world.height, level.id)
            assertEquals(1920.0, level.world.width, level.id)
            assertEquals(1080.0, level.world.height, level.id)
        }
    }

    @Test
    fun aircraftDistancesAreTheirFormerValueTimesTheZoom() {
        assertEquals(160.0 * zoom, LevelDefaults.HUD_BAND)
        assertEquals(160.0 * zoom, LevelDefaults.SPAWN_SEPARATION)
        assertEquals(150.0 * zoom, LevelDefaults.WARNING_DISTANCE)
        assertEquals(60.0 * zoom, LevelDefaults.SPAWN_MARGIN)
        assertEquals(60.0 * zoom, LevelDefaults.LEAVE_MARGIN)
        // The lead-in is flown on the aircraft's turning circle, so where it touches down follows the zoom.
        assertEquals(32.0 * zoom, LevelDefaults.TOUCHDOWN_DISTANCE)
        Levels.all.forEach { level ->
            assertEquals(LevelDefaults.SPAWN_SEPARATION, level.world.spawnSeparation, level.id)
            assertEquals(LevelDefaults.WARNING_DISTANCE, level.world.warningDistance, level.id)
            assertEquals(LevelDefaults.SPAWN_MARGIN, level.world.spawnMargin, level.id)
            assertEquals(LevelDefaults.LEAVE_MARGIN, level.world.leaveMargin, level.id)
        }
        assertTrue(LevelDefaults.SPAWN_SEPARATION > LevelDefaults.WARNING_DISTANCE, "no aircraft arrives already warned")
    }

    @Test
    fun theAirfieldKeepsItsWorldSizes() {
        assertEquals(64.0, LevelDefaults.RUNWAY_WIDTH)
        assertEquals(56.0, LevelDefaults.HELIPAD_RADIUS)
        assertEquals(44.0, LevelDefaults.HELIPAD_ZONE_RADIUS)
        assertEquals(140.0, LevelDefaults.RUNWAY_ZONE_DEPTH)
        assertEquals(96.0, LevelDefaults.RUNWAY_ZONE_WIDTH)
        assertEquals(67.5, SceneryDefaults.PARKED_AIRCRAFT_SIZE)
    }

    @Test
    fun timingsTurnsAndTheLandingToleranceStay() {
        assertEquals(Angles.degrees(30.0), LevelDefaults.LANDING_TOLERANCE, 1e-12)
        assertEquals(4.0, LevelDefaults.MINIMUM_TIME_ON_SCREEN)
        assertEquals(0.02, LevelDefaults.MAX_STEP_SECONDS)
        assertEquals(
            listOf(3.0, 1.6, 1.2, 0.9, 0.6),
            (DefaultAircraftSpecs.speedOrder + AircraftType.Jumbo).map { DefaultAircraftSpecs.all.getValue(it).turnRate },
        )
    }
}
