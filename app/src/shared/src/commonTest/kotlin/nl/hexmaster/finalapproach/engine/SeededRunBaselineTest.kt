package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pins the engine before the world port (OpenSpec change `world-v2-port`): each level is played
 * with a fixed seed and a simple controller, and its spawns, landings and score must equal the
 * values recorded on base commit c289489. The port touches rendering and level decoration only,
 * so these numbers must never change because of it.
 *
 * The Meadow Field rows were recorded again when its runway turned round to land eastbound (runway
 * 09): that is a change of the level's rules, so its runs differ. The other levels are as recorded.
 */
class SeededRunBaselineTest {
    private val dt = 0.05
    private val seconds = 240.0

    private fun summary(level: LevelConfig, seed: Long): String {
        val engine = GameEngine(level, seed = seed)
        repeat((seconds / dt).toInt()) {
            engine.update(dt)
            engine.state.value.aircraft
                .filter { it.hasEnteredViewport && it.path.isEmpty && it.status == AircraftStatus.Flying }
                .forEach { aircraft ->
                    val zone = engine.zones
                        .filter { it.accepts == aircraft.type.kind }
                        .minBy { entryOf(it).distanceTo(aircraft.position) }
                    engine.beginPath(aircraft.id)
                    if (zone is LandingZone.Runway) engine.extendPath(aircraft.id, zone.centreLinePoint(-APPROACH))
                    engine.extendPath(aircraft.id, entryOf(zone))
                    engine.endPath(aircraft.id)
                }
        }
        val s = engine.state.value
        val spawned = s.spawnedByType.entries.sortedBy { it.key.name }.joinToString(",") { "${it.key.name}=${it.value}" }
        val landed = s.landedByType.entries.sortedBy { it.key.name }.joinToString(",") { "${it.key.name}=${it.value}" }
        return "score=${s.score} landings=${s.landings} status=${s.status::class.simpleName} spawned=[$spawned] landed=[$landed]"
    }

    private fun entryOf(zone: LandingZone): Vec2 = when (zone) {
        is LandingZone.Runway -> zone.centreLinePoint(zone.area.length / 2.0)
        is LandingZone.Helipad -> zone.area.center
    }

    private val recorded = mapOf(
        ("meadow-field" to 3L) to "score=40 landings=2 status=GameOver spawned=[Helicopter=1,LightProp=4] landed=[LightProp=2]",
        ("meadow-field" to 11L) to "score=100 landings=6 status=GameOver spawned=[Helicopter=2,LightProp=4,Turboprop=3] landed=[Helicopter=2,LightProp=4]",
        ("meadow-field" to 42L) to "score=130 landings=7 status=GameOver spawned=[Helicopter=1,LightProp=8,Turboprop=1] landed=[Helicopter=1,LightProp=6]",
        ("island-airport" to 3L) to "score=20 landings=1 status=GameOver spawned=[Helicopter=1,LightProp=2,Turboprop=1] landed=[LightProp=1]",
        ("island-airport" to 11L) to "score=130 landings=7 status=GameOver spawned=[Helicopter=3,Jet=1,LightProp=5,Turboprop=1] landed=[Helicopter=2,LightProp=4,Turboprop=1]",
        ("island-airport" to 42L) to "score=30 landings=2 status=GameOver spawned=[Helicopter=1,LightProp=2,Turboprop=1] landed=[Helicopter=1,LightProp=1]",
        ("savanna-dawn" to 3L) to "score=220 landings=9 status=GameOver spawned=[Helicopter=3,Jet=2,Jumbo=1,LightProp=4,Turboprop=1] landed=[Helicopter=3,Jet=2,LightProp=3,Turboprop=1]",
        ("savanna-dawn" to 11L) to "score=100 landings=5 status=GameOver spawned=[Helicopter=2,LightProp=4,Turboprop=2] landed=[Helicopter=1,LightProp=3,Turboprop=1]",
        ("savanna-dawn" to 42L) to "score=0 landings=0 status=GameOver spawned=[Helicopter=1,LightProp=2] landed=[]",
    )

    @Test
    fun everySeededRunMatchesTheBaseline() {
        assertEquals(Levels.all.map { it.id }.toSet(), recorded.keys.map { it.first }.toSet())
        recorded.forEach { (key, expected) ->
            val level = Levels.all.first { it.id == key.first }
            assertEquals(expected, summary(level, key.second), "${key.first}, seed ${key.second}")
        }
    }

    private companion object {
        const val APPROACH = 260.0
    }
}
