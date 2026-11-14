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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.translate
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
 * A dark speech bubble that teaches: a flat surface in the outline colour on the field's contact
 * shadow, without a lip. Without [onGotIt] it is a gesture or tracking mark (max width
 * `coach-max-width`); with it, an explanation mark (`coach-explain-max-width`) that ends only
 * with its button. The step line is in `label-sm`, the title in `title-sm`, the body in `row`.
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
            .drawWithCache {
                val bubble = Path()
                onDrawBehind {
                    val tailPx = tailLength.toPx()
                    val top = if (tail == FaCoachTail.Up) tailPx else 0f
                    val bodyHeight = size.height - tailPx
                    val radius = FaDimens.radiusControl.toPx()
                    val tipX = (tailOffset?.toPx() ?: (size.width / 2f)).coerceIn(radius + tailPx, size.width - radius - tailPx)
                    // Bubble and tail are one shape, so the shadow under them has one even tone.
                    bubble.rewind()
                    bubble.addRoundRect(RoundRect(Rect(0f, top, size.width, top + bodyHeight), CornerRadius(radius)))
                    if (tail == FaCoachTail.Down) {
                        bubble.moveTo(tipX - tailPx, bodyHeight)
                        bubble.lineTo(tipX, bodyHeight + tailPx)
                        bubble.lineTo(tipX + tailPx, bodyHeight)
                    } else {
                        bubble.moveTo(tipX - tailPx, top)
                        bubble.lineTo(tipX + tailPx, top)
                        bubble.lineTo(tipX, 0f)
                    }
                    bubble.close()
                    translate(top = FaDimens.contactOffsetField.toPx()) { drawPath(bubble, colors.contactField) }
                    drawPath(bubble, colors.outline)
                }
            }
            .padding(
                start = FaDimens.space4,
                end = FaDimens.space4,
                top = FaDimens.space3 + if (tail == FaCoachTail.Up) tailLength else ZeroDp,
                bottom = FaDimens.space3 + if (tail == FaCoachTail.Down) tailLength else ZeroDp,
            ),
        verticalArrangement = Arrangement.spacedBy(FaDimens.space3),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(FaDimens.space3), verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) FaIconImage(icon, colors.onOutline, FaDimens.iconLg)
            Column {
                BasicText(stepLine.uppercase(), style = type.labelSm.copy(color = colors.onOutline))
                BasicText(title, style = type.titleSm.copy(color = colors.onOutline))
                BasicText(body, style = type.row.copy(color = colors.onOutline))
            }
        }
        if (onGotIt != null) {
            FaButton(text = gotItLabel, onClick = onGotIt, size = FaButtonSize.Small)
        }
    }
}
