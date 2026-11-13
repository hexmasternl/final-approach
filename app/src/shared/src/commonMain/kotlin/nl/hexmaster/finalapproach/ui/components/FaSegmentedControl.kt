package nl.hexmaster.finalapproach.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.zIndex
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Values of the Segmented Control that are not a token of their own. */
private object FaSegmentedControlDefaults {
    /** Draw order of the focused segment: above its neighbours, so nothing covers its focus ring. */
    const val FOCUSED_Z_INDEX = 1f
}

/**
 * A row of two to four mutually exclusive choices, one always selected. The trough is a well; the
 * selected segment is a small moulded sun piece, so the choice never depends on colour alone.
 * Segments are drawn `segment-height` high and take touches over the full height of the trough,
 * which is a touch target. Focus shows the two-tone ring around the focused segment.
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
            .drawWithCache {
                val clip = Path()
                onDrawBehind {
                    drawWell(
                        clip = clip,
                        fill = colors.paper300,
                        edge = colors.paper400,
                        outline = colors.outline,
                        border = FaDimens.lineUi.toPx(),
                        radius = FaDimens.radiusControl.toPx(),
                    )
                }
            }
            .padding(horizontal = troughPadding)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(FaDimens.space1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            val interaction = remember { MutableInteractionSource() }
            val focused by interaction.collectIsFocusedAsState()
            Box(
                Modifier
                    .zIndex(if (focused) FaSegmentedControlDefaults.FOCUSED_Z_INDEX else 0f)
                    .fillMaxHeight()
                    .selectable(
                        selected = isSelected,
                        interactionSource = interaction,
                        indication = null,
                        role = Role.RadioButton,
                        onClick = { onSelect(option) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .height(FaDimens.segmentHeight)
                        .drawWithCache {
                            val paths = MouldedPaths()
                            onDrawBehind {
                                if (focused) {
                                    drawFocusRing(
                                        focus = colors.focus,
                                        outline = colors.outline,
                                        radius = FaDimens.radiusStrip.toPx(),
                                    )
                                }
                                if (isSelected) {
                                    drawMoulded(
                                        tone = colors.toneSegment,
                                        paths = paths,
                                        outline = colors.outline,
                                        border = FaDimens.lineUiSm.toPx(),
                                        radius = FaDimens.radiusStrip.toPx(),
                                        lip = FaDimens.lipInset.toPx(),
                                        insetLip = true,
                                        lipEdge = 0f,
                                    )
                                }
                            }
                        }
                        .padding(horizontal = FaDimens.space4),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        label(option),
                        // On the selected piece the label sits in the middle of the face, above the lip.
                        modifier = if (isSelected) Modifier.offset(y = -FaDimens.lipInset / 2) else Modifier,
                        style = type.buttonLabelSm.copy(color = if (isSelected) colors.onAccent else colors.inkMuted),
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
