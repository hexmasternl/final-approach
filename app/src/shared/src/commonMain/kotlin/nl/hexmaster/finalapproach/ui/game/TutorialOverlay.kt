package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.engine.GameOverReason
import nl.hexmaster.finalapproach.engine.TutorialHighlight
import nl.hexmaster.finalapproach.engine.TutorialPhase
import nl.hexmaster.finalapproach.engine.TutorialState
import nl.hexmaster.finalapproach.engine.TutorialStep
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.components.FaCoachMark
import nl.hexmaster.finalapproach.ui.components.FaCoachTail
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens

/** What a coach mark of the tutorial says. */
internal data class CoachCopy(val title: String, val body: String, val icon: FaIcon? = null)

/** Every string of the tutorial. */
internal object TutorialText {
    const val GOT_IT = "Got it"
    const val SKIP = "Skip tutorial"
    private const val IT_NEEDS_TO_LAND = "It needs to land."
    private const val RETRY = "Let's try that again."

    fun stepLine(number: Int, count: Int) = "Step $number of $count"

    fun retry(reason: GameOverReason) = when (reason) {
        GameOverReason.Collision -> "Two aircraft got too close. $RETRY"
        GameOverReason.LeftArea -> "An aircraft left the area. $RETRY"
    }

    /** The coach mark for [state], or null when the step shows none (the turboprop on its way down). */
    fun copyFor(state: TutorialState, runwayNumber: String): CoachCopy? = when (state.step to state.phase) {
        TutorialStep.Goal to TutorialPhase.Explaining -> CoachCopy(
            "Welcome to the tower",
            "Guide every aircraft to a safe landing. Keep them apart: if two touch, the level ends.",
        )

        TutorialStep.Runway to TutorialPhase.Explaining -> CoachCopy(
            "This is runway $runwayNumber",
            "Airplanes land here. Come in from the right, lined up with the runway. " +
                "This runway takes landings from one end only; others take them from both ends.",
        )

        TutorialStep.LightProp to TutorialPhase.Tracking -> CoachCopy("Light prop inbound", IT_NEEDS_TO_LAND, FaIcon.Plane)

        TutorialStep.LightProp to TutorialPhase.Drawing -> CoachCopy(
            "Draw its path",
            "Drag from the plane to the runway. End lined up with the arrow.",
            FaIcon.Finger,
        )

        TutorialStep.Turboprop to TutorialPhase.Tracking -> CoachCopy("Turboprop inbound", "It needs to land too.", FaIcon.Plane)

        TutorialStep.Turboprop to TutorialPhase.Explaining -> CoachCopy(
            "Faster traffic",
            "This plane needs to land as well. There are four types of airplane, each with its own speed. " +
                "This one is faster than the light prop, so it catches up with slower traffic.",
        )

        TutorialStep.Helipad to TutorialPhase.Explaining -> CoachCopy("The helipad", "Helicopters land here, and only here.")

        TutorialStep.Helicopter to TutorialPhase.Tracking -> CoachCopy("Helicopter inbound", IT_NEEDS_TO_LAND, FaIcon.Helicopter)

        TutorialStep.Helicopter to TutorialPhase.Explaining -> CoachCopy(
            "Land it on the helipad",
            "Draw its path to the pad. No need to line up.",
        )

        else -> null
    }
}

/**
 * The tutorial's coach mark: it follows the tracked aircraft while one flies in or is being
 * drawn, and otherwise stands clear of the highlighted zone. Explanations carry Got it. It only
 * renders [tutorial]; every rule lives in the engine.
 */
@Composable
internal fun TutorialOverlay(
    tutorial: TutorialState,
    level: LevelConfig,
    aircraft: List<Aircraft>,
    viewport: WorldViewport,
    onGotIt: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val runway = level.runways.firstOrNull()
    val runwayNumber = runway?.numberFor(runway.landingHeadings.first()).orEmpty()
    val base = TutorialText.copyFor(tutorial, runwayNumber) ?: return
    val copy = tutorial.retryReason?.let { base.copy(body = TutorialText.retry(it) + " " + base.body) } ?: base
    val stepLine = TutorialText.stepLine(tutorial.stepNumber, TutorialState.STEP_COUNT)
    val explaining = tutorial.phase == TutorialPhase.Explaining
    val tracked = aircraft.firstOrNull { it.id == tutorial.trackedAircraftId }
    val following = tracked != null && !explaining
    val density = LocalDensity.current

    // Unpadded, so positions share the canvas origin; only the standing coach mark keeps clear of the system bars.
    BoxWithConstraints(modifier.fillMaxSize()) {
        val screenHeight = with(density) { maxHeight.toPx() }
        if (following && tracked != null) {
            val anchor = viewport.offsetOf(tracked.position)
            // Beyond the pick radius of the aircraft, below it when it is in the top half of the screen.
            val clearance = with(density) { (FaDimens.touchAircraft / 2 + FaDimens.space3).toPx() }
            val below = anchor.y < screenHeight / 2f
            Box(
                Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
                    val margin = FaDimens.space4.roundToPx()
                    val x = (anchor.x - placeable.width / 2f).toInt().coerceIn(margin, (constraints.maxWidth - placeable.width - margin).coerceAtLeast(margin))
                    val y = if (below) (anchor.y + clearance).toInt() else (anchor.y - clearance - placeable.height).toInt()
                    layout(constraints.maxWidth, constraints.maxHeight) { placeable.place(IntOffset(x, y)) }
                },
            ) {
                FaCoachMark(
                    stepLine = stepLine,
                    title = copy.title,
                    body = copy.body,
                    tail = if (below) FaCoachTail.Up else FaCoachTail.Down,
                    icon = copy.icon,
                )
            }
        } else {
            // Away from the highlighted zone: the zone is in the lower half of the screen more often than not.
            val zoneY = zoneScreenY(tutorial, level, viewport)
            val alignment = when {
                zoneY == null -> Alignment.Center
                zoneY > screenHeight / 2f -> Alignment.TopCenter
                else -> Alignment.BottomCenter
            }
            Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(FaDimens.space4), contentAlignment = alignment) {
                FaCoachMark(
                    stepLine = stepLine,
                    title = copy.title,
                    body = copy.body,
                    tail = if (alignment == Alignment.BottomCenter) FaCoachTail.Down else FaCoachTail.Up,
                    icon = copy.icon,
                    gotItLabel = TutorialText.GOT_IT,
                    onGotIt = if (explaining) onGotIt else null,
                )
            }
        }
    }
}

/** The screen y of the highlighted zone's centre, or null when nothing is highlighted. */
private fun zoneScreenY(tutorial: TutorialState, level: LevelConfig, viewport: WorldViewport): Float? {
    val position = when (val highlight = tutorial.highlight) {
        is TutorialHighlight.Runway -> level.runways.firstOrNull { it.id == highlight.id }?.position
        is TutorialHighlight.Helipad -> level.helipads.firstOrNull { it.id == highlight.id }?.position
        // Explanations of an aircraft keep clear of the runway, where it is going.
        null -> level.runways.firstOrNull()?.position.takeIf { tutorial.step != TutorialStep.Goal }
    }
    return position?.let { viewport.offsetOf(it).y }
}
