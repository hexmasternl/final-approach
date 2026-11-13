package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.TutorialScript
import nl.hexmaster.finalapproach.domain.Vec2

/** The six steps of the first-time tutorial, in order. */
enum class TutorialStep { Goal, Runway, LightProp, Turboprop, Helipad, Helicopter }

/** What a step is doing right now. */
enum class TutorialPhase {
    /** An explanation is shown and the game is held until Got it. */
    Explaining,

    /** An aircraft is flying in and a coach mark follows it. */
    Tracking,

    /** The game is held, the player draws the aircraft's path and the drag demo plays. */
    Drawing,

    /** The game runs until the tracked aircraft is cleared to land. */
    AwaitingClearance,
}

/** A landing zone shape the tutorial points out. */
sealed interface TutorialHighlight {
    data class Runway(val id: String) : TutorialHighlight

    data class Helipad(val id: String) : TutorialHighlight
}

/** The path the drag demo shows, in world units: from the aircraft to the landing end of the runway. */
data class TutorialDemo(val from: Vec2, val to: Vec2)

/**
 * Where the tutorial is. [holding] freezes the simulation; [touchEnabled] says whether taps and
 * drags reach the aircraft. [retryReason] is set while a step runs again after a failure.
 */
data class TutorialState(
    val step: TutorialStep,
    val phase: TutorialPhase,
    val trackedAircraftId: Int? = null,
    val holding: Boolean = false,
    val touchEnabled: Boolean = false,
    val retryReason: GameOverReason? = null,
    val highlight: TutorialHighlight? = null,
    val demo: TutorialDemo? = null,
) {
    /** The step number, 1 to [STEP_COUNT]. */
    val stepNumber: Int get() = step.ordinal + 1

    companion object {
        val STEP_COUNT: Int = TutorialStep.entries.size
    }
}

/**
 * What the engine must do to move the tutorial on: take a snapshot of the game as it is now
 * ([snapshot]), send in an aircraft ([spawn]), and show [state]; or, when [completed], hand over
 * to the normal level.
 */
internal data class TutorialTransition(
    val state: TutorialState?,
    val snapshot: Boolean = false,
    val spawn: AircraftType? = null,
    val completed: Boolean = false,
)

/**
 * The step and phase rules of the tutorial: a pure state machine. The engine owns the game
 * state, asks the director what comes next, and does what the transition says.
 */
internal class TutorialDirector(private val level: LevelConfig, private val script: TutorialScript) {

    private val viewport = level.world.viewport

    /** The first transition: the goal step. */
    fun start(): TutorialTransition = enter(TutorialStep.Goal)

    /** The player tapped Got it. Null when the state has no Got it. */
    fun acknowledge(state: TutorialState): TutorialTransition? {
        if (state.phase != TutorialPhase.Explaining) return null
        return when (state.step) {
            TutorialStep.Goal -> enter(TutorialStep.Runway)
            TutorialStep.Runway -> enter(TutorialStep.LightProp)
            TutorialStep.Turboprop -> TutorialTransition(
                state.copy(phase = TutorialPhase.AwaitingClearance, holding = false, touchEnabled = true, retryReason = null),
            )
            TutorialStep.Helipad -> enter(TutorialStep.Helicopter)
            TutorialStep.Helicopter -> TutorialTransition(state = null, completed = true)
            TutorialStep.LightProp -> null
        }
    }

    /** What follows from [aircraft] as they are now, or null when nothing changes. */
    fun advance(state: TutorialState, aircraft: List<Aircraft>): TutorialTransition? {
        val tracked = aircraft.firstOrNull { it.id == state.trackedAircraftId }
        return when (state.step to state.phase) {
            TutorialStep.LightProp to TutorialPhase.Tracking ->
                if (tracked == null || wellInView(tracked)) {
                    TutorialTransition(
                        state.copy(
                            phase = TutorialPhase.Drawing,
                            holding = true,
                            touchEnabled = true,
                            demo = tracked?.let { TutorialDemo(it.position, landingEnd()) },
                        ),
                    )
                } else {
                    null
                }

            TutorialStep.LightProp to TutorialPhase.Drawing ->
                if (tracked == null || isCleared(tracked)) enter(TutorialStep.Turboprop) else null

            TutorialStep.Turboprop to TutorialPhase.Tracking, TutorialStep.Helicopter to TutorialPhase.Tracking ->
                if (tracked == null || wellInView(tracked)) {
                    TutorialTransition(state.copy(phase = TutorialPhase.Explaining, holding = true, touchEnabled = false))
                } else {
                    null
                }

            TutorialStep.Turboprop to TutorialPhase.AwaitingClearance ->
                if (tracked == null || isCleared(tracked)) enter(TutorialStep.Helipad) else null

            else -> null
        }
    }

    /** Runs [step] again from its first phase after a failure that was [reason]. */
    fun retry(step: TutorialStep, reason: GameOverReason): TutorialTransition =
        enter(step).copy(snapshot = false).let { it.copy(state = it.state?.copy(retryReason = reason)) }

    /** The first phase of [step]. */
    fun enter(step: TutorialStep): TutorialTransition = when (step) {
        TutorialStep.Goal -> TutorialTransition(
            TutorialState(step, TutorialPhase.Explaining, holding = true),
            snapshot = true,
        )

        TutorialStep.Runway -> TutorialTransition(
            TutorialState(step, TutorialPhase.Explaining, holding = true, highlight = level.runways.firstOrNull()?.let { TutorialHighlight.Runway(it.id) }),
            snapshot = true,
        )

        TutorialStep.LightProp -> TutorialTransition(
            TutorialState(step, TutorialPhase.Tracking, touchEnabled = false),
            snapshot = true,
            spawn = AircraftType.LightProp,
        )

        TutorialStep.Turboprop -> TutorialTransition(
            TutorialState(step, TutorialPhase.Tracking, touchEnabled = true),
            snapshot = true,
            spawn = AircraftType.Turboprop,
        )

        TutorialStep.Helipad -> TutorialTransition(
            TutorialState(step, TutorialPhase.Explaining, holding = true, highlight = level.helipads.firstOrNull()?.let { TutorialHighlight.Helipad(it.id) }),
            snapshot = true,
        )

        TutorialStep.Helicopter -> TutorialTransition(
            TutorialState(step, TutorialPhase.Tracking, touchEnabled = true),
            snapshot = true,
            spawn = AircraftType.Helicopter,
        )
    }

    /** True when the aircraft's centre is at least the script's inset inside every viewport edge. */
    fun wellInView(aircraft: Aircraft): Boolean {
        val inset = script.wellInViewInset
        val p = aircraft.position
        return p.x >= viewport.left + inset && p.x <= viewport.right - inset &&
            p.y >= viewport.top + inset && p.y <= viewport.bottom - inset
    }

    private fun isCleared(aircraft: Aircraft) = aircraft.status is AircraftStatus.ClearedToLand

    /** The threshold of the first runway at the end airplanes land from: where the demo path ends. */
    private fun landingEnd(): Vec2 {
        val runway = level.runways.first()
        val heading = runway.landingHeadings.first()
        return runway.position - Angles.direction(heading) * (runway.length / 2.0)
    }
}
