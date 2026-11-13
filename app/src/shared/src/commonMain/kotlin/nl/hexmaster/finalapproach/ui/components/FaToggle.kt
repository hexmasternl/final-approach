package nl.hexmaster.finalapproach.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion
import nl.hexmaster.finalapproach.ui.theme.FaOpacity
import nl.hexmaster.finalapproach.ui.theme.FaShadows
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/**
 * An on/off switch for settings that apply at once. Drawn 68 x 40 with a 48dp touch area. The
 * state is carried by the knob's position and colour together; the knob slides in `dur-quick`
 * with `ease-bounce`, and jumps with reduced motion. Put a visible label next to it.
 */
@Composable
fun FaToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = FaTheme.colors
    val motion = if (FaTheme.reduceMotion) snap() else tween<Float>(FaMotion.durQuickMs, easing = FaMotion.easeBounce)
    val position by animateFloatAsState(if (checked) 1f else 0f, motion)
    val well = if (FaTheme.isNight) FaShadows.wellNight else FaShadows.wellDay
    Box(
        modifier
            .defaultMinSize(minWidth = FaDimens.toggleWidth, minHeight = FaDimens.touchMin)
            .graphicsLayer { alpha = if (enabled) 1f else FaOpacity.disabled }
            .toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(FaDimens.toggleWidth, FaDimens.toggleHeight)) {
            val border = FaDimens.line2.toPx()
            val trackRadius = CornerRadius(size.height / 2f, size.height / 2f)
            val innerRadius = CornerRadius(trackRadius.x - border, trackRadius.y - border)
            val innerSize = Size(size.width - 2f * border, size.height - 2f * border)
            // Track: the outline, then the fill; a sunken track gets the inner top edge of a well.
            drawRoundRect(colors.outline, Offset.Zero, size, trackRadius)
            drawRoundRect(lerp(colors.surfaceSunken, colors.sky500, position), Offset(border, border), innerSize, innerRadius)
            if (position < 1f) {
                val clip = Path().apply {
                    addRoundRect(RoundRect(border, border, border + innerSize.width, border + innerSize.height, innerRadius))
                }
                clipPath(clip) {
                    drawRect(
                        well.copy(alpha = well.alpha * (1f - position)),
                        Offset(border, border),
                        Size(innerSize.width, FaShadows.wellInset.toPx()),
                    )
                }
            }
            // Knob: a disc with its own outline and a small hard lip, slid across the track.
            val knob = FaDimens.toggleKnob.toPx()
            val gap = (size.height - knob) / 2f
            val travel = size.width - knob - 2f * gap
            val centre = Offset(gap + knob / 2f + travel * position, size.height / 2f)
            drawCircle(colors.outline, knob / 2f, centre + Offset(0f, FaDimens.line1.toPx()))
            drawCircle(colors.outline, knob / 2f, centre)
            drawCircle(lerp(colors.surfaceRaised, colors.sun, position), knob / 2f - border, centre)
        }
    }
}
