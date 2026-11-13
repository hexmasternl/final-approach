package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import nl.hexmaster.finalapproach.engine.GameEngine
import nl.hexmaster.finalapproach.engine.GameState
import nl.hexmaster.finalapproach.input.WorldViewport
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/**
 * The collision view over the whole game screen, HUD included: a blackout at [CollisionSequence.blackoutAlpha],
 * with the two colliding aircraft and their red rings on top. It takes every pointer event, so
 * nothing below it can be tapped or dragged while the sequence plays.
 */
@Composable
internal fun CollisionOverlay(
    sequence: CollisionSequence,
    state: GameState,
    engine: GameEngine,
    viewport: WorldViewport,
    reduceMotion: Boolean,
    lightOpacity: Float?,
) {
    val colors = FaTheme.colors
    Canvas(
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    do {
                        val event = awaitPointerEvent()
                        event.changes.forEach { it.consume() }
                    } while (event.changes.any { it.pressed })
                }
            },
    ) {
        drawCollisionView(sequence, state, engine, viewport, colors, reduceMotion, lightOpacity)
    }
}

/** Blackout, then the two aircraft, then their rings at full size with the frozen dash angle. */
internal fun DrawScope.drawCollisionView(
    sequence: CollisionSequence,
    state: GameState,
    engine: GameEngine,
    viewport: WorldViewport,
    colors: FaColors,
    reduceMotion: Boolean,
    lightOpacity: Float?,
) {
    drawRect(colors.blackout, alpha = sequence.blackoutAlpha)
    val colliding = state.aircraft.filter { it.id in sequence.aircraftIds }
    val time = if (reduceMotion) 0f else state.levelTime.toFloat()
    for (aircraft in colliding) drawOneAircraft(aircraft, engine, viewport, colors, reduceMotion, lightOpacity, time)
    val margin = FaDimens.warnRingMargin.toPx()
    for (aircraft in colliding) {
        drawProximityRing(
            center = viewport.offsetOf(aircraft.position),
            radius = viewport.px(engine.specs[aircraft.type].shape.boundingRadius) + margin,
            angleDegrees = sequence.ringAngleDegrees,
            scale = 1f,
            alpha = 1f,
            colors = colors,
            dashColor = colors.danger,
        )
    }
}
