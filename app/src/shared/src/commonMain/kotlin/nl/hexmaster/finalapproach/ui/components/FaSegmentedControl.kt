package nl.hexmaster.finalapproach.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.semantics.Role
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/**
 * A row of two to four mutually exclusive choices, one always selected. The trough is a well; the
 * selected segment is a small moulded sun piece, so the choice never depends on colour alone.
 * Segments are drawn `segment-height` high and take touches over the full height of the trough,
 * which is a touch target.
 *
 * @param label the visible text of an option, one word
 */
@Composable
fun <T> FaSegmentedControl(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    val troughPadding = FaDimens.lineUi + FaDimens.space1
    Row(
        modifier
            .height(maxOf(FaDimens.segmentHeight + troughPadding * 2, FaDimens.touchMin))
            .drawBehind {
                drawWell(
                    fill = colors.paper300,
                    edge = colors.paper400,
                    outline = colors.outline,
                    border = FaDimens.lineUi.toPx(),
                    radius = FaDimens.radiusControl.toPx(),
                )
            }
            .padding(horizontal = troughPadding)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(FaDimens.space1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                Modifier
                    .fillMaxHeight()
                    .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(option) }),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .height(FaDimens.segmentHeight)
                        .drawBehind {
                            if (isSelected) {
                                drawMoulded(
                                    tone = colors.toneSegment,
                                    outline = colors.outline,
                                    border = FaDimens.lineUiSm.toPx(),
                                    radius = FaDimens.radiusStrip.toPx(),
                                    lip = FaDimens.lipInset.toPx(),
                                    insetLip = true,
                                    lipEdge = 0f,
                                )
                            }
                        }
                        .padding(horizontal = FaDimens.space4),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        label(option),
                        // On the selected piece the label sits in the middle of the face, above the lip.
                        modifier = if (isSelected) Modifier.offset(y = -FaDimens.lipInset / 2) else Modifier,
                        style = type.buttonLabelSm.copy(color = if (isSelected) colors.ink else colors.inkMuted),
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
