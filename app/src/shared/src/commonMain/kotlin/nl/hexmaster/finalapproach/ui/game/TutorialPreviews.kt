package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.engine.TutorialDemo
import nl.hexmaster.finalapproach.engine.TutorialDirector
import nl.hexmaster.finalapproach.engine.TutorialPhase
import nl.hexmaster.finalapproach.engine.TutorialState
import nl.hexmaster.finalapproach.engine.TutorialStep
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.game.ambient.AmbientLife
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Sample data for the tutorial previews. Not used by the game. */
private object TutorialPreviewData {
    const val WIDTH = 800
    const val HEIGHT = 380
    const val AIRCRAFT_ID = 1
    val lightPropAt = Vec2(1400.0, 300.0)
    val runwayEnd = Vec2(1280.0, 630.0)
}

/** One tutorial step on the Meadow Field, as the game draws it: field, highlight, demo, aircraft and coach mark. */
@Composable
private fun TutorialStepPreview(step: TutorialStep, phase: TutorialPhase? = null, retry: Boolean = false) {
    val level = Levels.meadowField
    val script = requireNotNull(level.tutorial)
    val director = remember { TutorialDirector(level, script) }
    val base = requireNotNull(director.enter(step).state)
    val tutorial = when (phase) {
        TutorialPhase.Drawing -> base.copy(
            phase = phase,
            holding = true,
            touchEnabled = true,
            demo = TutorialDemo(TutorialPreviewData.lightPropAt, TutorialPreviewData.runwayEnd),
        )
        null -> base
        else -> base.copy(phase = phase)
    }.copy(
        trackedAircraftId = TutorialPreviewData.AIRCRAFT_ID,
        retryReason = if (retry) nl.hexmaster.finalapproach.engine.GameOverReason.LeftArea else null,
    )
    val aircraft = if (step == TutorialStep.Goal || step == TutorialStep.Runway || step == TutorialStep.Helipad) {
        emptyList()
    } else {
        listOf(Aircraft(TutorialPreviewData.AIRCRAFT_ID, AircraftType.LightProp, TutorialPreviewData.lightPropAt, 5.5, hasEnteredViewport = true))
    }
    FaTheme(reduceMotion = true) {
        val colors = FaTheme.colors
        val measurer = rememberTextMeasurer()
        val numberStyle = FaTheme.type.titleMd
        val letterStyle = FaTheme.type.titleLg
        val ambient = remember(level) { AmbientLife(level, seed = level.id.hashCode().toLong(), reduceMotion = true) }
        Box(Modifier.fillMaxSize()) {
            Canvas(Modifier.fillMaxSize()) {
                val viewport = WorldViewport(level.world.width, level.world.height, size.width.toDouble(), size.height.toDouble())
                drawField(level, ambient, viewport, colors, measurer, numberStyle, letterStyle)
                tutorial.highlight?.let { drawTutorialHighlight(it, level, viewport, colors, 0.0, reduceMotion = true) }
                tutorial.demo?.let {
                    drawTutorialDemoPath(it, viewport, colors)
                    drawTutorialDemoFinger(it, viewport, colors, 0.0, reduceMotion = true)
                }
            }
            val screen = androidx.compose.ui.platform.LocalDensity.current
            androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxSize()) {
                val viewport = with(screen) {
                    WorldViewport(level.world.width, level.world.height, maxWidth.toPx().toDouble(), maxHeight.toPx().toDouble())
                }
                TutorialOverlay(tutorial, level, aircraft, viewport, onGotIt = {})
            }
        }
    }
}

@Preview(widthDp = TutorialPreviewData.WIDTH, heightDp = TutorialPreviewData.HEIGHT)
@Composable
private fun TutorialGoalPreview() = TutorialStepPreview(TutorialStep.Goal)

@Preview(widthDp = TutorialPreviewData.WIDTH, heightDp = TutorialPreviewData.HEIGHT)
@Composable
private fun TutorialRunwayPreview() = TutorialStepPreview(TutorialStep.Runway)

@Preview(widthDp = TutorialPreviewData.WIDTH, heightDp = TutorialPreviewData.HEIGHT)
@Composable
private fun TutorialLightPropTrackingPreview() = TutorialStepPreview(TutorialStep.LightProp)

@Preview(widthDp = TutorialPreviewData.WIDTH, heightDp = TutorialPreviewData.HEIGHT)
@Composable
private fun TutorialLightPropDrawingPreview() = TutorialStepPreview(TutorialStep.LightProp, TutorialPhase.Drawing)

@Preview(widthDp = TutorialPreviewData.WIDTH, heightDp = TutorialPreviewData.HEIGHT)
@Composable
private fun TutorialTurbopropExplainingPreview() = TutorialStepPreview(TutorialStep.Turboprop, TutorialPhase.Explaining)

@Preview(widthDp = TutorialPreviewData.WIDTH, heightDp = TutorialPreviewData.HEIGHT)
@Composable
private fun TutorialHelipadPreview() = TutorialStepPreview(TutorialStep.Helipad)

@Preview(widthDp = TutorialPreviewData.WIDTH, heightDp = TutorialPreviewData.HEIGHT)
@Composable
private fun TutorialHelicopterExplainingPreview() = TutorialStepPreview(TutorialStep.Helicopter, TutorialPhase.Explaining)

@Preview(widthDp = TutorialPreviewData.WIDTH, heightDp = TutorialPreviewData.HEIGHT)
@Composable
private fun TutorialRetryPreview() = TutorialStepPreview(TutorialStep.Turboprop, retry = true)
