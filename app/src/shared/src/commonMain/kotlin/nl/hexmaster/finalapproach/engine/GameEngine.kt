package nl.hexmaster.finalapproach.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.LandingZones
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.random.Random

/**
 * Everything the game screen draws, as of one moment.
 *
 * @param levelTime seconds since the level started
 * @param aircraft every aircraft in play, in the order they arrived
 * @param score points from landings so far
 * @param landings number of aircraft that touched down
 * @param selectedId the aircraft the player last picked, if it is still in play
 * @param events what happened during the last update
 */
data class GameState(
    val levelTime: Double = 0.0,
    val aircraft: List<Aircraft> = emptyList(),
    val score: Int = 0,
    val landings: Int = 0,
    val selectedId: Int? = null,
    val events: List<GameEvent> = emptyList(),
)

/**
 * Owns and advances the state of one level. The state changes only through [update] and the
 * input calls; the engine is deterministic for a seed and uses no UI or platform API.
 */
class GameEngine(
    val level: LevelConfig,
    seed: Long = level.run.seed ?: Random.nextLong(),
    val specs: EffectiveAircraftSpecs = level.aircraftTuning.resolve(),
    val config: EngineConfig = EngineConfig(),
) {
    val zones: List<LandingZone> = LandingZones.of(level)

    private val spawner = Spawner(level, seed, specs::speedOf)
    private val viewport = level.world.viewport
    private val playArea = viewport.expandedBy(level.world.leaveMargin)
    private var nextId = 1

    private val mutableState = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = mutableState.asStateFlow()

    /** Advances the level by [deltaSeconds]: moves aircraft, lands them, removes leavers and spawns. */
    fun update(deltaSeconds: Double) {
        if (deltaSeconds <= 0.0) return
        val current = mutableState.value
        val time = current.levelTime + deltaSeconds
        val events = mutableListOf<GameEvent>()
        val moved = current.aircraft.mapNotNull { aircraft ->
            val result = AircraftMotion.step(aircraft, specs, level.landing, deltaSeconds)
            events += result.events
            result.aircraft?.let(::trackViewport)
        }
        val inPlay = moved.filterNot(::hasLeft)
        val spawn = spawner.update(deltaSeconds, time, inPlay.filter(::isAirborne).map { it.position })
        val aircraft = if (spawn == null) inPlay else inPlay + arrive(spawn, inPlay)
        mutableState.value = GameState(
            levelTime = time,
            aircraft = aircraft,
            score = Scoring.apply(current.score, events, specs),
            landings = current.landings + events.count { it is GameEvent.Landed },
            selectedId = current.selectedId?.takeIf { id -> aircraft.any { it.id == id } },
            events = events,
        )
    }

    /**
     * Selects the aircraft nearest to [point] within [pickRadius] world units, or nothing when
     * none is that close. Aircraft rolling out on the runway cannot be picked. Returns its id.
     */
    fun select(point: Vec2, pickRadius: Double): Int? {
        val picked = mutableState.value.aircraft
            .filter { it.status !is AircraftStatus.RollingOut }
            .map { it to it.position.distanceTo(point) }
            .filter { (_, distance) -> distance <= pickRadius }
            .minByOrNull { (_, distance) -> distance }
            ?.first?.id
        mutableState.value = mutableState.value.copy(selectedId = picked)
        return picked
    }

    /** Starts a new path for aircraft [id], replacing its old one. */
    fun beginPath(id: Int) {
        updateAircraft(id) { PathDrawing.begin(it) }
        mutableState.value = mutableState.value.copy(selectedId = id.takeIf { has(it) })
    }

    /**
     * Adds [point] to the path of aircraft [id] when it is at least the minimum spacing from the
     * path's last point. A path that enters a landing zone ends there and ignores further points.
     */
    fun extendPath(id: Int, point: Vec2) {
        val time = mutableState.value.levelTime
        updateAircraft(id) { aircraft ->
            val last = aircraft.path.points.lastOrNull() ?: aircraft.position
            if (last.distanceTo(point) < config.minPointSpacing) {
                aircraft
            } else {
                PathDrawing.extend(aircraft, point, zones, level.landing, time)
            }
        }
    }

    /** Ends drawing for aircraft [id]; the path stays as drawn. */
    fun endPath(id: Int) = Unit

    private fun has(id: Int) = mutableState.value.aircraft.any { it.id == id }

    /** Replaces the aircraft in play; for tests that need a known situation. */
    internal fun place(aircraft: List<Aircraft>) {
        nextId = (aircraft.maxOfOrNull { it.id } ?: 0) + 1
        mutableState.value = mutableState.value.copy(aircraft = aircraft)
    }

    private fun updateAircraft(id: Int, change: (Aircraft) -> Aircraft) {
        val current = mutableState.value
        mutableState.value = current.copy(aircraft = current.aircraft.map { if (it.id == id) change(it) else it })
    }

    private fun arrive(spawn: Spawn, inPlay: List<Aircraft>): Aircraft {
        val used = inPlay.map { it.livery }.toSet()
        val id = nextId++
        val livery = (0 until LIVERY_COUNT).firstOrNull { it !in used } ?: ((id - 1) % LIVERY_COUNT)
        return Aircraft(id = id, type = spawn.type, position = spawn.position, heading = spawn.heading, livery = livery)
    }

    private fun trackViewport(aircraft: Aircraft): Aircraft =
        if (!aircraft.hasEnteredViewport && viewport.contains(aircraft.position)) {
            aircraft.copy(hasEnteredViewport = true)
        } else {
            aircraft
        }

    private fun hasLeft(aircraft: Aircraft) = aircraft.hasEnteredViewport && !playArea.contains(aircraft.position)

    private fun isAirborne(aircraft: Aircraft) = aircraft.status !is AircraftStatus.RollingOut

    companion object {
        /** Number of livery colours, path1 to path6. */
        const val LIVERY_COUNT = 6
    }
}
