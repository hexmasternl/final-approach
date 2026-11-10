package nl.hexmaster.finalapproach.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.sprites.drawIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion
import nl.hexmaster.finalapproach.ui.theme.FaOpacity
import nl.hexmaster.finalapproach.ui.theme.FaShadows
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** What a button is for. One primary button per screen. */
enum class FaButtonVariant { Primary, Secondary, Ghost, Danger }

enum class FaButtonSize { Small, Medium, Large }

/** Geometry of the Button component that is not a token of its own. */
private object FaButtonDefaults {
    /** Band of the deep colour along the bottom of the face. */
    val deepBand = 5.dp
    val deepBandSmall = 3.dp

    /** A pressed face moves down by the difference between the resting and the pressed lip. */
    val pressTravel = FaShadows.lip - FaShadows.lipSm

    /** Width of each band of the two-tone focus ring. */
    val focusBand = 3.dp
}

/**
 * The chunky toy-block button. The label starts with a verb and is one to three words.
 *
 * @param icon optional leading icon
 * @param enabled a disabled button is faded and does not react
 */
@Composable
fun FaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: FaButtonVariant = FaButtonVariant.Primary,
    size: FaButtonSize = FaButtonSize.Medium,
    icon: FaIcon? = null,
    enabled: Boolean = true,
) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    val (fill, deep, content) = when (variant) {
        FaButtonVariant.Primary -> Triple(colors.sun, colors.sunDeep, colors.onAccent)
        FaButtonVariant.Secondary -> Triple(colors.sky500, colors.sky700, colors.onAccent)
        FaButtonVariant.Ghost -> Triple(colors.surface, colors.surfaceSunken, colors.ink)
        FaButtonVariant.Danger -> Triple(colors.cherry, colors.cherryDeep, colors.onAccent)
    }
    val spec = when (size) {
        FaButtonSize.Small -> ButtonSpec(
            FaDimens.buttonSm, FaDimens.space4, FaDimens.line2, FaDimens.radiusMd,
            FaShadows.lipSm, FaButtonDefaults.deepBandSmall, FaDimens.iconSm, type.buttonLabelSm,
        )

        FaButtonSize.Medium -> ButtonSpec(
            FaDimens.buttonMd, FaDimens.space5, FaDimens.line3, FaDimens.radiusMd,
            FaShadows.lip, FaButtonDefaults.deepBand, FaDimens.iconMd, type.buttonLabel,
        )

        FaButtonSize.Large -> ButtonSpec(
            FaDimens.buttonLg, FaDimens.space6, FaDimens.line3, FaDimens.radiusLg,
            FaShadows.lip, FaButtonDefaults.deepBand, FaDimens.iconMd, type.buttonLabelLg,
        )
    }
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val focused by interaction.collectIsFocusedAsState()
    // Pressing drops the face onto its lip, in dur-press with ease-snap; at once with reduced motion.
    val pressMotion = if (FaTheme.reduceMotion) snap() else tween<Dp>(FaMotion.durPressMs, easing = FaMotion.easeSnap)
    val down = pressed && enabled
    val travel by animateDpAsState(if (down) FaButtonDefaults.pressTravel else 0.dp, pressMotion)
    val lip = spec.lip - travel
    val outline = colors.outline
    val focus = colors.focus

    Box(
        modifier
            .defaultMinSize(minWidth = FaDimens.touchMin)
            // Small buttons are drawn 40dp high but stay a full touch target.
            .height(maxOf(spec.height + spec.lip, FaDimens.touchMin))
            .graphicsLayer { alpha = if (enabled) 1f else FaOpacity.disabled }
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .drawBehind {
                val faceHeight = spec.height.toPx()
                val top = travel.toPx()
                val border = spec.border.toPx()
                val radius = spec.radius.toPx()
                if (focused) {
                    val band = FaButtonDefaults.focusBand.toPx()
                    // Two-tone ring: focus colour next to the button, outline colour outside it.
                    for ((color, distance) in listOf(outline to band * 1.5f, focus to band * 0.5f)) {
                        drawRoundRect(
                            color,
                            Offset(-distance, top - distance),
                            Size(this.size.width + 2f * distance, faceHeight + lip.toPx() + 2f * distance),
                            CornerRadius(radius + distance, radius + distance),
                            style = Stroke(band),
                        )
                    }
                }
                drawBlock(deep, outline, border, radius, lip.toPx(), top, faceHeight)
                // The fill stops short of the bottom, leaving a band of the deep colour.
                val inner = (radius - border).coerceAtLeast(0f)
                drawRoundRect(
                    fill,
                    Offset(border, top + border),
                    Size(this.size.width - 2f * border, faceHeight - 2f * border - spec.deepBand.toPx()),
                    CornerRadius(inner, inner),
                )
            },
        contentAlignment = Alignment.TopCenter,
    ) {
        Row(
            Modifier
                .offset(y = travel)
                .height(spec.height)
                .padding(start = spec.paddingX, end = spec.paddingX, bottom = spec.deepBand),
            horizontalArrangement = Arrangement.spacedBy(FaDimens.space2, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) FaIconImage(icon, content, spec.iconSize)
            BasicText(text, style = spec.textStyle.copy(color = content), maxLines = 1)
        }
    }
}

private class ButtonSpec(
    val height: Dp,
    val paddingX: Dp,
    val border: Dp,
    val radius: Dp,
    val lip: Dp,
    val deepBand: Dp,
    val iconSize: Dp,
    val textStyle: TextStyle,
)

/** An icon from the design system's icon set, drawn in [color]. */
@Composable
fun FaIconImage(icon: FaIcon, color: Color, size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) { drawIcon(icon, color) }
}
