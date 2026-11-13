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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.sprites.drawIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/**
 * An on/off switch for settings that apply at once. The track is a well, 60 x 34 with a 48dp
 * touch area; the knob is a small moulded piece. On is told by the knob's position, the mint
 * track and a check mark together; the knob slides in `dur-quick` with `ease-bounce`, and jumps
 * with reduced motion. Disabled, it is drawn in paper tones. Put a visible label next to it.
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
    Box(
        modifier
            .defaultMinSize(minWidth = FaDimens.toggleWidthV2, minHeight = FaDimens.touchMin)
            .toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(FaDimens.toggleWidthV2, FaDimens.toggleHeightV2)) {
            val border = FaDimens.lineUi.toPx()
            // The bounce overshoots; colours and the check mark stay inside their range.
            val shade = position.coerceIn(0f, 1f)
            val outline = if (enabled) colors.outline else colors.ink3
            drawWell(
                fill = if (enabled) lerp(colors.paper300, colors.mint500, shade) else colors.paper200,
                edge = if (enabled) lerp(colors.paper400, colors.mint700, shade) else colors.paper200,
                outline = outline,
                border = border,
                radius = size.height / 2f,
            )
            if (enabled && shade > 0f) {
                val icon = FaDimens.iconSm.toPx()
                val margin = (size.height - icon) / 2f
                inset(left = margin, top = margin, right = size.width - margin - icon, bottom = margin) {
                    drawIcon(FaIcon.Check, colors.mint900.copy(alpha = shade))
                }
            }
            val knob = FaDimens.toggleKnobV2.toPx()
            val gap = (size.height - knob) / 2f
            val travel = size.width - knob - 2f * gap
            drawMoulded(
                tone = colors.toneKnob,
                outline = outline,
                border = border,
                radius = knob / 2f,
                lip = FaDimens.lipInset.toPx(),
                topLeft = Offset(gap + travel * position, gap),
                size = Size(knob, knob),
                insetLip = true,
                lipEdge = 0f,
            )
        }
    }
}
