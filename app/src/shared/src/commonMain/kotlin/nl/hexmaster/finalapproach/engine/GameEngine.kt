package nl.hexmaster.finalapproach.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.LandingZone
import nl.hexmaster.finalapproach.domain.LandingZones
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.ceil
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
 * @param status playing, or game over with its reason; once over, nothing moves any more
 * @param warnedAircraft aircraft within the warning distance of another aircraft
 * @param spawnedByType how many aircraft of each type have spawned in this run
 * @param landedByType how many aircraft of each type have landed in this run
 * @param paused true while the player has paused the level; nothing moves or spawns
 * @param tutorial where the first-time tutorial is, or null when none runs (any more)
 */
data class GameState(
    val levelTime: Double = 0.0,
    val aircraft: List<Aircraft> = emptyList(),
    val score: Int = 0,
    val landings: Int = 0,
    val selectedId: Int? = null,
    val events: List<GameEvent> = emptyList(),
    val status: GameStatus = GameStatus.Playing,
    val warnedAircraft: Set<Int> = emptySet(),
    val spawnedByType: Map<AircraftType, Int> = emptyMap(),
    val landedByType: Map<AircraftType, Int> = emptyMap(),
    val paused: Boolean = false,
    val tutorial: TutorialState? = null,
) {
    /** Total number of aircraft that landed. */
    val totalLanded: Int get() = landedByType.values.sum()
}

/**
 * Owns and advances the state of one level. The state changes only through [update] and the
 * input calls; the engine is deterministic for a seed and uses no UI or platform API.
 */
class GameEngine(
    val level: LevelConfig,
    seed: Long = level.run.seed ?: Random.nextLong(),
    val specs: EffectiveAircraftSpecs = level.aircraftTuning.resolve(),
    val config: EngineConfig = EngineConfig(),
    tutorialActive: Boolean = false,
    private val onTutorialCompleted: () -> Unit = {},
) {
    val zones: List<LandingZone> = LandingZones.of(level)

    private var spawner = Spawner(level, seed, specs::speedOf)
    private val viewport = level.world.viewport
    private val playArea = viewport.expandedBy(level.world.leaveMargin)
    private var nextId = 1

    private val director: TutorialDirector? = level.tutorial?.let { TutorialDirector(level, it) }

    /** True while the first-time tutorial runs; the normal spawner is off until it ends. */
    private var tutorialRunning = tutorialActive && director != null

    /** The game as it was when the current tutorial step began, for a restart of that step. */
    private var snapshot: StepSnapshot? = null

    /** Level time when the tutorial ended; the difficulty curve and first spawn delay count from here. */
    private var handoverTime = 0.0

    /** Clearances made by input since the last update; the next update reports them. */
    private var pendingEvents: List<GameEvent> = emptyList()

    private val mutableState = MutableStateFlow(GameState())

    init {
        if (tutorialRunning) beginTutorial()
    }
    val state: StateFlow<GameState> = mutableState.asStateFlow()

    /**
     * Advances the level by [deltaSeconds]: moves aircraft, lands them, checks for collisions and
     * aircraft that left, and spawns. A long frame is split into steps no longer than the level's
     * maximum step. After a game over, or while paused, it does nothing.
     */
    fun update(deltaSeconds: Double) {
        val current = mutableState.value
        if (deltaSeconds <= 0.0 || current.paused || current.status is GameStatus.GameOver || current.tutorial?.holding == true) {
            // An update that does nothing reports nothing, so a sound cue is never played twice.
            if (current.events.isNotEmpty()) mutableState.value = current.copy(events = emptyList())
            return
        }
        val steps = ceil(deltaSeconds / level.world.maxStepSeconds).toInt().coerceAtLeast(1)
        val stepSeconds = deltaSeconds / steps
        val events = mutableListOf<GameEvent>()
        events += pendingEvents
        pendingEvents = emptyList()
        var aircraft = current.aircraft
        var status: GameStatus = GameStatus.Playing
        for (step in 0 until steps) {
            val previous = aircraft.associate { it.id to it.position }
            aircraft = aircraft.mapNotNull { plane ->
                val result = AircraftMotion.step(plane, specs, level.landing, stepSeconds)
                events += result.events
                result.aircraft?.let(::trackViewport)
            }
            status = failureOf(aircraft, previous)
            if (status is GameStatus.GameOver) break
        }
        val time = current.levelTime + deltaSeconds
        var spawned = current.spawnedByType
        if (status is GameStatus.GameOver && tutorialRunning) {
            val after = restartStep(current.copy(levelTime = time, aircraft = aircraft), status.reason)
            mutableState.value = after.copy(events = events, warnedAircraft = warned(after.aircraft))
            return
        }
        if (status == GameStatus.Playing && !tutorialRunning) {
            val spawn = spawner.update(deltaSeconds, time - handoverTime, aircraft.filter(::isAirborne).map { it.position })
            if (spawn != null) {
                aircraft = aircraft + arrive(spawn, aircraft)
                spawned = spawned + (spawn.type to (spawned[spawn.type] ?: 0) + 1)
            }
        }
        val landed = events.filterIsInstance<GameEvent.Landed>()
            .fold(current.landedByType) { counts, event -> counts + (event.type to (counts[event.type] ?: 0) + 1) }
        mutableState.value = GameState(
            levelTime = time,
            aircraft = aircraft,
            score = Scoring.apply(current.score, events, specs),
            landings = current.landings + events.count { it is GameEvent.Landed },
            selectedId = current.selectedId?.takeIf { id -> aircraft.any { it.id == id } },
            events = events,
            status = status,
            warnedAircraft = warned(aircraft),
            spawnedByType = spawned,
            landedByType = landed,
            tutorial = current.tutorial,
        ).let(::advanceTutorial)
    }

    /** Freezes the level: nothing moves, spawns or scores, and input is ignored, until [resume]. */
    fun pause() {
        mutableState.value = mutableState.value.copy(paused = true, events = emptyList())
    }

    /** Carries on from exactly where [pause] stopped. */
    fun resume() {
        mutableState.value = mutableState.value.copy(paused = false)
    }

    /** Starts the level again from scratch, with a fresh seed unless the level fixes one. */
    fun restart(seed: Long = level.run.seed ?: Random.nextLong()) {
        spawner = Spawner(level, seed, specs::speedOf)
        nextId = 1
        pendingEvents = emptyList()
        handoverTime = 0.0
        snapshot = null
        mutableState.value = GameState()
        if (tutorialRunning) beginTutorial()
    }

    /**
     * Selects the aircraft nearest to [point] within [pickRadius] world units, or nothing when
     * none is that close. Aircraft rolling out on the runway, and helicopters landing, cannot be
     * picked. Returns its id.
     */
    fun select(point: Vec2, pickRadius: Double): Int? {
        if (inputBlocked) return null
        val picked = mutableState.value.aircraft
            .filter { it.status !is AircraftStatus.RollingOut && it.status !is AircraftStatus.Landing }
            .map { it to it.position.distanceTo(point) }
            .filter { (_, distance) -> distance <= pickRadius }
            .minByOrNull { (_, distance) -> distance }
            ?.first?.id
        mutableState.value = mutableState.value.copy(selectedId = picked)
        return picked
    }

    /**
     * Removes the path of aircraft [id]; it flies straight on in its current heading. An aircraft
     * cleared to land has its clearance cancelled too. A rolling-out airplane is left alone.
     */
    fun clearPath(id: Int) {
        if (inputBlocked) return
        updateAircraft(id) { PathDrawing.begin(it) }
    }

    /** Starts a new path for aircraft [id], replacing its old one. */
    fun beginPath(id: Int) {
        if (inputBlocked) return
        updateAircraft(id) { PathDrawing.begin(it) }
        mutableState.value = mutableState.value.copy(selectedId = id.takeIf { has(it) })
    }

    /**
     * Adds [point] to the path of aircraft [id] when it is at least the minimum spacing from the
     * path's last point. A path that enters a landing zone ends there and ignores further points.
     */
    fun extendPath(id: Int, point: Vec2) {
        if (inputBlocked) return
        val time = mutableState.value.levelTime
        val before = mutableState.value.aircraft.firstOrNull { it.id == id }?.status
        updateAircraft(id) { aircraft ->
            val last = aircraft.path.points.lastOrNull() ?: aircraft.position
            if (last.distanceTo(point) < config.minPointSpacing) {
                aircraft
            } else {
                PathDrawing.extend(aircraft, point, zones, level.landing, time, specs, config)
            }
        }
        val after = mutableState.value.aircraft.firstOrNull { it.id == id }?.status
        if (before !is AircraftStatus.ClearedToLand && after is AircraftStatus.ClearedToLand) {
            pendingEvents = pendingEvents + GameEvent.ClearedToLand(id, after.zone)
        }
        mutableState.value = advanceTutorial(mutableState.value)
    }

    /** Ends drawing for aircraft [id]: its path is smoothed into one it can fly. */
    fun endPath(id: Int) {
        if (inputBlocked) return
        updateAircraft(id) { PathDrawing.finish(it, specs, config) }
        mutableState.value = advanceTutorial(mutableState.value)
    }

    /** The player tapped Got it on a tutorial explanation. Does nothing when none is shown. */
    fun acknowledgeTutorial() {
        val current = mutableState.value
        if (current.paused || current.status is GameStatus.GameOver) return
        val tutorial = current.tutorial ?: return
        val transition = director?.acknowledge(tutorial) ?: return
        mutableState.value = advanceTutorial(applyTransition(current, transition))
    }

    /** Ends the tutorial at once: aircraft stay, the normal spawner starts, normal failures apply. */
    fun skipTutorial() {
        val current = mutableState.value
        if (current.tutorial == null || current.paused || current.status is GameStatus.GameOver) return
        mutableState.value = applyTransition(current, TutorialTransition(state = null, completed = true))
    }

    private fun beginTutorial() {
        val start = director?.start() ?: return
        mutableState.value = applyTransition(mutableState.value, start)
    }

    /** Moves the tutorial on for as long as the aircraft as they are now ask for it. */
    private fun advanceTutorial(state: GameState): GameState {
        val tutorial = state.tutorial ?: return state
        val next = director?.advance(tutorial, state.aircraft) ?: return state
        return advanceTutorial(applyTransition(state, next))
    }

    /** Does what [transition] says: snapshot, send in an aircraft, show the new state, or hand over. */
    private fun applyTransition(state: GameState, transition: TutorialTransition): GameState {
        if (transition.completed) {
            tutorialRunning = false
            snapshot = null
            handoverTime = state.levelTime
            pendingEvents = pendingEvents + GameEvent.TutorialCompleted
            onTutorialCompleted()
            return state.copy(tutorial = null)
        }
        if (transition.snapshot) {
            snapshot = StepSnapshot(state.aircraft, state.score, state.landings, state.spawnedByType, state.landedByType, nextId)
        }
        var next = state
        var tutorial = transition.state
        val spawn = transition.spawn
        val entry = spawn?.let { type -> level.tutorial?.entries?.firstOrNull { it.first == type }?.second }
        if (spawn != null && entry != null) {
            val aircraft = arriveAt(spawn, entry.position, entry.heading, next.aircraft)
            next = next.copy(
                aircraft = next.aircraft + aircraft,
                spawnedByType = next.spawnedByType + (spawn to (next.spawnedByType[spawn] ?: 0) + 1),
            )
            tutorial = tutorial?.copy(trackedAircraftId = aircraft.id)
        }
        return next.copy(tutorial = tutorial, warnedAircraft = warned(next.aircraft))
    }

    /** After a failure during the tutorial: back to the game as it was when the step began, and the step again. */
    private fun restartStep(state: GameState, reason: GameOverReason): GameState {
        val step = checkNotNull(state.tutorial) { "restartStep without a tutorial" }.step
        val saved = checkNotNull(snapshot) { "tutorial step without a snapshot" }
        val transition = checkNotNull(director) { "tutorial without a director" }.retry(step, reason)
        nextId = saved.nextId
        val restored = state.copy(
            aircraft = saved.aircraft,
            score = saved.score,
            landings = saved.landings,
            spawnedByType = saved.spawnedByType,
            landedByType = saved.landedByType,
            selectedId = null,
            status = GameStatus.Playing,
        )
        return applyTransition(restored, transition)
    }

    /** The part of the game a tutorial step restarts from. */
    private class StepSnapshot(
        val aircraft: List<Aircraft>,
        val score: Int,
        val landings: Int,
        val spawnedByType: Map<AircraftType, Int>,
        val landedByType: Map<AircraftType, Int>,
        val nextId: Int,
    )

    private fun has(id: Int) = mutableState.value.aircraft.any { it.id == id }

    /** Input does nothing once the level is over or while it is paused. */
    private val inputBlocked: Boolean
        get() = mutableState.value.let { it.paused || it.status is GameStatus.GameOver || it.tutorial?.touchEnabled == false }

    /** Replaces the aircraft in play; for tests that need a known situation. */
    internal fun place(aircraft: List<Aircraft>) {
        nextId = (aircraft.maxOfOrNull { it.id } ?: 0) + 1
        mutableState.value = mutableState.value.copy(aircraft = aircraft, warnedAircraft = warned(aircraft))
    }

    private fun updateAircraft(id: Int, change: (Aircraft) -> Aircraft) {
        val current = mutableState.value
        mutableState.value = current.copy(aircraft = current.aircraft.map { if (it.id == id) change(it) else it })
    }

    private fun arrive(spawn: Spawn, inPlay: List<Aircraft>): Aircraft =
        arriveAt(spawn.type, spawn.position, spawn.heading, inPlay)

    private fun arriveAt(type: AircraftType, position: Vec2, heading: Double, inPlay: List<Aircraft>): Aircraft {
        val used = inPlay.map { it.livery }.toSet()
        val id = nextId++
        val livery = (0 until LIVERY_COUNT).firstOrNull { it !in used } ?: ((id - 1) % LIVERY_COUNT)
        return Aircraft(id = id, type = type, position = position, heading = heading, livery = livery)
    }

    private fun trackViewport(aircraft: Aircraft): Aircraft =
        if (!aircraft.hasEnteredViewport && viewport.contains(aircraft.position)) {
            aircraft.copy(hasEnteredViewport = true)
        } else {
            aircraft
        }

    /** An aircraft that has been in the viewport and is beyond the leave margin is lost only when it has no path. */
    private fun hasLeft(aircraft: Aircraft) =
        aircraft.hasEnteredViewport && aircraft.path.isEmpty && !playArea.contains(aircraft.position)

    /** Game over when two aircraft touched during the step from [previous], or one has left. */
    private fun failureOf(aircraft: List<Aircraft>, previous: Map<Int, Vec2>): GameStatus {
        Collisions.find(aircraft, specs, previous).firstOrNull()?.let { (a, b) ->
            return GameStatus.GameOver(GameOverReason.Collision, listOf(a, b))
        }
        val left = aircraft.filter(::hasLeft)
        if (left.isNotEmpty()) return GameStatus.GameOver(GameOverReason.LeftArea, left.map { it.id })
        return GameStatus.Playing
    }

    /** Ids of aircraft whose centre is closer than the warning distance to another one; a landing helicopter is ignored. */
    private fun warned(aircraft: List<Aircraft>): Set<Int> = buildSet {
        val candidates = aircraft.filter { it.status !is AircraftStatus.Landing }
        for (i in candidates.indices) for (j in i + 1 until candidates.size) {
            if (candidates[i].position.distanceTo(candidates[j].position) < level.world.warningDistance) {
                add(candidates[i].id)
                add(candidates[j].id)
            }
        }
    }

    private fun isAirborne(aircraft: Aircraft) = aircraft.status !is AircraftStatus.RollingOut

    companion object {
        /** Number of livery colours, path1 to path6. */
        const val LIVERY_COUNT = 6
    }
}
