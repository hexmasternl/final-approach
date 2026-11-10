package nl.hexmaster.finalapproach.ui.menu

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import nl.hexmaster.finalapproach.ui.sprites.drawAircraft
import nl.hexmaster.finalapproach.ui.sprites.drawCloud
import nl.hexmaster.finalapproach.ui.sprites.drawSunburst
import nl.hexmaster.finalapproach.ui.theme.FaShadows
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/**
 * Draws the main page backdrop for [state], back to front: rotating stripes, clouds, then the
 * passing aircraft. It only draws; [MenuBackdropSimulation] decides what moves.
 */
@Composable
internal fun MenuBackdrop(
    state: MenuBackdropState,
    simulation: MenuBackdropSimulation,
    config: MenuBackdropConfig,
    modifier: Modifier = Modifier,
) {
    val colors = FaTheme.colors
    Canvas(modifier) {
        val width = size.width / density
        val height = size.height / density

        drawSunburst(center, state.rotationDegrees, config.rayDegrees, colors)

        // Clouds fly higher than aircraft, so their shadow falls further away.
        val cloudShadow = Offset(FaShadows.floatOffsetX.toPx(), FaShadows.floatOffsetY.toPx()) * CLOUD_SHADOW_FACTOR
        for (cloud in config.clouds) {
            val at = simulation.cloudCenter(cloud, state.elapsedSeconds, width, height)
            drawCloud(Offset(at.x * density, at.y * density), cloud.width * density, colors, cloudShadow)
        }

        val slot = state.slot
        if (slot is AircraftSlot.Flying) {
            val aircraft = slot.aircraft
            drawAircraft(
                type = aircraft.type,
                livery = colors.path(aircraft.livery),
                center = Offset(aircraft.x * density, aircraft.y * density),
                sizePx = config.spriteSize(aircraft.type) * density,
                headingDegrees = aircraft.headingDegrees,
                timeSeconds = state.elapsedSeconds,
                colors = colors,
                shadowOffset = Offset(FaShadows.floatOffsetX.toPx(), FaShadows.floatOffsetY.toPx()) * config.aircraftScale,
            )
        }
    }
}

private const val CLOUD_SHADOW_FACTOR = 1.6f
