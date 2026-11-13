package nl.hexmaster.finalapproach.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.sprites.drawIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** What a button is for. One primary button per screen. */
enum class FaButtonVariant { Primary, Secondary, Ghost, Danger }

enum class FaButtonSize { Small, Medium, Large }

/**
 * The moulded button: a face on a lip of its own colour. The label starts with a verb and is one
 * to three words. Its height, outline and lip included, is the `button-sm`, `button-md` or
 * `button-lg` token; a small button keeps a full touch target around its 40dp.
 *
 * @param icon optional leading icon
 * @param enabled a disabled button is drawn in paper tones and does not react
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
    val tone = when (variant) {
        FaButtonVariant.Primary -> colors.tonePrimary
        FaButtonVariant.Secondary -> colors.toneSecondary
        FaButtonVariant.Ghost -> colors.toneGhost
        FaButtonVariant.Danger -> colors.toneDanger
    }
    val spec = when (size) {
        FaButtonSize.Small -> ButtonSpec(
            FaDimens.buttonSm, FaDimens.space4, FaDimens.radiusControlSm, FaDimens.lipSm, FaDimens.iconSm, type.buttonLabelSm,
        )

        FaButtonSize.Medium -> ButtonSpec(
            FaDimens.buttonMd, FaDimens.space5, FaDimens.radiusControl, FaDimens.lipMd, FaDimens.iconMd, type.buttonLabel,
        )

        FaButtonSize.Large -> ButtonSpec(
            FaDimens.buttonLg, FaDimens.space6, FaDimens.radiusControlLg, FaDimens.lipLg, FaDimens.iconMd, type.buttonLabelLg,
        )
    }
    val content = if (enabled) colors.ink else colors.ink3
    // A button lower than a touch target is drawn in the middle of one.
    val touchInsetY = ((FaDimens.touchMin - spec.height) / 2).coerceAtLeast(ZeroDp)
    FaMoulded(
        onClick = onClick,
        tone = tone,
        radius = spec.radius,
        lip = spec.lip,
        modifier = modifier
            .defaultMinSize(minWidth = FaDimens.touchMin)
            .height(spec.height + touchInsetY * 2),
        enabled = enabled,
        shine = variant != FaButtonVariant.Ghost,
        touchInsetY = touchInsetY,
    ) {
        Row(
            Modifier.padding(horizontal = spec.paddingX),
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
    val radius: Dp,
    val lip: Dp,
    val iconSize: Dp,
    val textStyle: TextStyle,
)

/**
 * A square moulded button with only an icon; [description] is what a screen reader says. It is a
 * `button-md` square, or with [compact] a `strip-height` square for the HUD, drawn in the middle
 * of a full touch target and standing on the field.
 */
@Composable
fun FaIconButton(
    icon: FaIcon,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    enabled: Boolean = true,
) {
    val colors = FaTheme.colors
    val drawn = if (compact) FaDimens.stripHeight else FaDimens.buttonMd
    val touchInset = ((FaDimens.touchMin - drawn) / 2).coerceAtLeast(ZeroDp)
    FaMoulded(
        onClick = onClick,
        tone = colors.toneGhost,
        radius = if (compact) FaDimens.radiusStrip else FaDimens.radiusControl,
        lip = if (compact) FaDimens.lipSm else FaDimens.lipMd,
        modifier = modifier
            .size(drawn + touchInset * 2)
            .semantics { contentDescription = description },
        enabled = enabled,
        touchInsetX = touchInset,
        touchInsetY = touchInset,
        contact = if (compact) colors.contactField else colors.contact,
        contactOffset = if (compact) FaDimens.contactOffsetField else FaDimens.contactOffset,
    ) {
        FaIconImage(icon, if (enabled) colors.ink else colors.ink3, if (compact) FaDimens.iconSm else FaDimens.iconMd)
    }
}

/** An icon from the design system's icon set, drawn in [color]. */
@Composable
fun FaIconImage(icon: FaIcon, color: Color, size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) { drawIcon(icon, color) }
}
