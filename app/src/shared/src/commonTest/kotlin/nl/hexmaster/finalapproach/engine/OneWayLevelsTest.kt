package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.AircraftKind
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.LandingZones
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.LevelDefaults
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.RunwayConfig
import nl.hexmaster.finalapproach.domain.RunwayMode
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OneWayLevelsTest {

    private val tolerance = LevelDefaults.LANDING_TOLERANCE

    /** A straight 200 unit approach to the threshold of [runway], flown on [heading] (or its reverse). */
    private fun approach(level: LevelConfig, runway: RunwayConfig, heading: Double) =
        LandingRules.check(
            from = runway.position + Angles.direction(heading + kotlin.math.PI) * (runway.length / 2.0 + 150.0),
            to = runway.position + Angles.direction(heading + kotlin.math.PI) * (runway.length / 2.0 - 50.0),
            kind = AircraftKind.Airplane,
            zones = LandingZones.of(level),
            alignmentTolerance = tolerance,
        )

    @Test
    fun meadowRunwayLandsWestboundAndNotEastbound() {
        val runway = Levels.meadowField.runways.single()
        assertNotNull(approach(Levels.meadowField, runway, 3 * kotlin.math.PI / 2))
        assertNull(approach(Levels.meadowField, runway, kotlin.math.PI / 2))
    }

    @Test
    fun islandDiagonalLandsSouthWestAndNotNorthEast() {
        val runway = Levels.islandAirport.runways.first { it.id == "04-22" }
        assertNotNull(approach(Levels.islandAirport, runway, 5 * kotlin.math.PI / 4))
        assertNull(approach(Levels.islandAirport, runway, kotlin.math.PI / 4))
    }

    @Test
    fun islandMainRunwayStillLandsFromBothEnds() {
        val runway = Levels.islandAirport.runways.first { it.id == "09-27" }
        assertNotNull(approach(Levels.islandAirport, runway, kotlin.math.PI / 2))
        assertNotNull(approach(Levels.islandAirport, runway, 3 * kotlin.math.PI / 2))
    }

    @Test
    fun everyOneWayRunwayHasRoomToApproachFromTheWorldEdge() {
        Levels.all.forEach { level ->
            level.runways.filter { it.mode is RunwayMode.OneWay }.forEach { runway ->
                val heading = runway.landingHeadings.single()
                val back = Angles.direction(heading + kotlin.math.PI)
                val threshold = runway.position + back * (runway.length / 2.0)
                // Walk back from the threshold until the segment leaves the world or enters the HUD band.
                var room = 0.0
                while (true) {
                    val p = threshold + back * (room + 1.0)
                    if (p.x < 0 || p.x > level.world.width || p.y > level.world.height || p.y < LevelDefaults.HUD_BAND) break
                    room += 1.0
                }
                assertTrue(room >= level.landing.runwayZoneDepth, "${level.id}/${runway.id} room $room")
            }
        }
    }
}
