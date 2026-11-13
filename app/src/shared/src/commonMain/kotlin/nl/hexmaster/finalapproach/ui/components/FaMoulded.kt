package nl.hexmaster.finalapproach.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion
import nl.hexmaster.finalapproach.ui.theme.FaOpacity
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/**
 * A pressable moulded surface: buttons, icon buttons and level cards are all this. Pressing drops
 * the face onto its lip in `dur-press` with `ease-snap`, at once with reduced motion. Focus shows
 * the two-tone ring. Disabled, it is drawn in paper tones at full opacity, without shine or
 * contact shadow, and does not react.
 *
 * The element's size comes from [modifier] and includes the outline and the [lip]. [content] is
 * laid out on the face and moves with it.
 *
 * @param shine draws the small white shine on the face; for coloured faces only
 * @param touchInsetX room left and right of the drawn surface that still takes the touch
 * @param touchInsetY room above and below the drawn surface that still takes the touch
 */
@Composable
internal fun FaMoulded(
    onClick: () -> Unit,
    tone: FaTone,
    radius: Dp,
    lip: Dp,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shine: Boolean = false,
    touchInsetX: Dp = ZeroDp,
    touchInsetY: Dp = ZeroDp,
    contact: Color = FaTheme.colors.contact,
    contactOffset: Dp = FaDimens.contactOffset,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = FaTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val focused by interaction.collectIsFocusedAsState()
    val motion = if (FaTheme.reduceMotion) snap() else tween<Dp>(FaMotion.durPressMs, easing = FaMotion.easeSnap)
    // A disabled control lies flat on a sliver of lip and stays there.
    val restLip = if (enabled) lip else FaDimens.lipPressed
    val currentLip = animateDpAsState(if (pressed && enabled) FaDimens.lipPressed else restLip, motion)
    val shownTone = if (enabled) tone else colors.toneDisabled
    val outline = if (enabled) colors.outline else colors.ink3
    val focus = colors.focus
    val ringOutline = colors.outline
    val shineColor = colors.glassShine.copy(alpha = FaOpacity.shine)
    val border = FaDimens.lineUi

    Box(
        modifier
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .drawWithCache {
                val paths = MouldedPaths()
                onDrawBehind {
                    val inset = Offset(touchInsetX.toPx(), touchInsetY.toPx())
                    val drawn = Size(size.width - 2f * inset.x, size.height - 2f * inset.y)
                    val travel = (restLip - currentLip.value).toPx()
                    val radiusPx = radius.toPx()
                    if (focused) {
                        drawFocusRing(
                            focus = focus,
                            outline = ringOutline,
                            radius = radiusPx,
                            topLeft = inset + Offset(0f, travel),
                            size = Size(drawn.width, drawn.height - travel),
                            band = border.toPx(),
                        )
                    }
                    drawMoulded(
                        tone = shownTone,
                        paths = paths,
                        outline = outline,
                        border = border.toPx(),
                        radius = radiusPx,
                        lip = currentLip.value.toPx(),
                        restLip = restLip.toPx(),
                        topLeft = inset,
                        size = drawn,
                        contact = contact,
                        contactOffset = if (enabled) contactOffset.toPx() else 0f,
                    )
                    if (shine && enabled) {
                        val height = FaDimens.shineHeight.toPx()
                        drawRoundRect(
                            shineColor,
                            inset + Offset(border.toPx() + FaDimens.shineInsetX.toPx(), travel + border.toPx() + FaDimens.shineInsetY.toPx()),
                            Size(FaDimens.shineWidth.toPx(), height),
                            CornerRadius(height / 2f),
                        )
                    }
                }
            }
            .padding(
                start = touchInsetX + border,
                top = touchInsetY + border,
                end = touchInsetX + border,
                bottom = touchInsetY + border + restLip,
            )
            .offset { IntOffset(0, (restLip - currentLip.value).roundToPx()) },
        contentAlignment = contentAlignment,
        content = content,
    )
}
