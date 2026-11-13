package nl.hexmaster.finalapproach.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Which side of a coach mark its tail is on: [Down] points at something below it, [Up] at something above. */
enum class FaCoachTail { Down, Up }

/** Geometry of the Coach Mark that is not a token of its own. */
private object FaCoachMarkDefaults {
    /** Length of the tail, and half its width: a 45 degree wedge. */
    val tail = 10.dp
}

/**
 * A dark speech bubble that teaches. Without [onGotIt] it is a gesture or tracking mark (max
 * width `coach-max-width`); with it, an explanation mark (`coach-explain-max-width`) that ends
 * only with its button. The step line is in `label`, the title in `title-sm`, the body in `body`.
 *
 * @param tailOffset distance of the tail's tip from the bubble's left edge; the centre when null
 * @param onGotIt shows the Got it button when not null
 */
@Composable
fun FaCoachMark(
    stepLine: String,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    tail: FaCoachTail = FaCoachTail.Down,
    tailOffset: Dp? = null,
    icon: FaIcon? = null,
    gotItLabel: String = "",
    onGotIt: (() -> Unit)? = null,
) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    val tailLength = FaCoachMarkDefaults.tail
    val maxWidth = if (onGotIt != null) FaDimens.coachExplainMaxWidth else FaDimens.coachMaxWidth
    Column(
        modifier
            .widthIn(max = maxWidth)
            .semantics { liveRegion = LiveRegionMode.Polite }
            .drawBehind {
                val tailPx = tailLength.toPx()
                val top = if (tail == FaCoachTail.Up) tailPx else 0f
                val bodyHeight = size.height - tailPx
                val radius = FaDimens.radiusMd.toPx()
                drawRoundRect(colors.outline, Offset(0f, top), Size(size.width, bodyHeight), CornerRadius(radius, radius))
                val tipX = (tailOffset?.toPx() ?: (size.width / 2f)).coerceIn(radius + tailPx, size.width - radius - tailPx)
                val path = Path().apply {
                    if (tail == FaCoachTail.Down) {
                        moveTo(tipX - tailPx, bodyHeight)
                        lineTo(tipX + tailPx, bodyHeight)
                        lineTo(tipX, bodyHeight + tailPx)
                    } else {
                        moveTo(tipX - tailPx, top)
                        lineTo(tipX + tailPx, top)
                        lineTo(tipX, 0f)
                    }
                    close()
                }
                drawPath(path, colors.outline)
            }
            .padding(
                start = FaDimens.space4,
                end = FaDimens.space4,
                top = FaDimens.space3 + if (tail == FaCoachTail.Up) tailLength else 0.dp,
                bottom = FaDimens.space3 + if (tail == FaCoachTail.Down) tailLength else 0.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(FaDimens.space3),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(FaDimens.space3), verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) FaIconImage(icon, colors.onOutline, FaDimens.iconLg)
            Column {
                BasicText(stepLine.uppercase(), style = type.label.copy(color = colors.onOutline))
                BasicText(title, style = type.titleSm.copy(color = colors.onOutline))
                BasicText(body, style = type.body.copy(color = colors.onOutline))
            }
        }
        if (onGotIt != null) {
            FaButton(text = gotItLabel, onClick = onGotIt, size = FaButtonSize.Small)
        }
    }
}
