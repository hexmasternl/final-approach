package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.AircraftKind
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Scenery is decoration: a run plays out the same with it and without it. */
class SceneryIsDecorationTest {
    private val dt = 0.05
    private val seconds = 240.0

    /** What a run produced: every state it went through. */
    private class Run(val states: List<GameState>) {
        val last: GameState get() = states.last()
    }

    /**
     * Plays [level] with a fixed seed and a simple controller: every aircraft that has entered the
     * world and has no path yet is sent to the nearest landing zone of its kind, an airplane by
     * way of a point on the extended centre line.
     */
    private fun play(level: LevelConfig, seed: Long): Run {
        val engine = GameEngine(level, seed = seed)
        val states = mutableListOf<GameState>()
        repeat((seconds / dt).toInt()) {
            engine.update(dt)
            val state = engine.state.value
            states += state
            state.aircraft
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
        return Run(states)
    }

    private fun entryOf(zone: LandingZone): Vec2 = when (zone) {
        is LandingZone.Runway -> zone.centreLinePoint(zone.area.length / 2.0)
        is LandingZone.Helipad -> zone.area.center
    }

    @Test
    fun aSeededRunIsTheSameWithoutScenery() {
        Levels.all.forEach { level ->
            val bare = level.copy(scenery = emptyList())
            assertTrue(level.scenery.isNotEmpty(), level.id)
            listOf(3L, 11L, 42L).forEach { seed ->
                val with = play(level, seed)
                val without = play(bare, seed)
                assertEquals(with.states.size, without.states.size)
                with.states.zip(without.states).forEachIndexed { step, (a, b) ->
                    assertEquals(a, b, "${level.id}, seed $seed: the runs differ at step $step")
                }
                assertEquals(with.last.score, without.last.score)
                assertEquals(with.last.spawnedByType, without.last.spawnedByType)
                assertEquals(with.last.landedByType, without.last.landedByType)
                assertEquals(with.last.status, without.last.status)
            }
        }
    }

    @Test
    fun theComparedRunsSpawnLandAndEnd() {
        // The comparison above only means something when the runs have something in them.
        val runs = Levels.all.flatMap { level -> listOf(3L, 11L, 42L).map { play(level, it) } }
        assertTrue(runs.all { run -> run.last.spawnedByType.values.sum() > 0 }, "every run spawns")
        assertTrue(runs.any { it.last.landings > 0 }, "some run lands an aircraft")
        assertTrue(runs.any { it.last.score > 0 }, "some run scores")
        assertTrue(runs.any { it.last.landedByType.keys.any { type -> type.kind == AircraftKind.Helicopter } }, "some run lands a helicopter")
        assertTrue(runs.any { it.last.status is GameStatus.GameOver }, "some run ends in a collision or a miss")
    }

    private companion object {
        /** How far before the threshold the controller lines an airplane up, in world units. */
        const val APPROACH = 260.0
    }
}
