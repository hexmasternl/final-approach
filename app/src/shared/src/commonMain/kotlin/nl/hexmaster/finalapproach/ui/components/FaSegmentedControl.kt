package nl.hexmaster.finalapproach.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.semantics.Role
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaShadows
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/**
 * A row of two to four mutually exclusive choices, one always selected. Segments are drawn 40dp
 * high with a 48dp touch area. The selected segment is a raised sun tile, so the choice never
 * depends on colour alone.
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
    val well = if (FaTheme.isNight) FaShadows.wellNight else FaShadows.wellDay
    Row(
        modifier
            .height(IntrinsicSize.Min)
            .drawBehind {
                val border = FaDimens.line2.toPx()
                val radius = CornerRadius(FaDimens.radiusMd.toPx(), FaDimens.radiusMd.toPx())
                val innerRadius = CornerRadius(radius.x - border, radius.y - border)
                val innerSize = Size(size.width - 2f * border, size.height - 2f * border)
                drawRoundRect(colors.outline, Offset.Zero, size, radius)
                drawRoundRect(colors.surfaceSunken, Offset(border, border), innerSize, innerRadius)
                val clip = Path().apply {
                    addRoundRect(RoundRect(border, border, border + innerSize.width, border + innerSize.height, innerRadius))
                }
                clipPath(clip) {
                    drawRect(well, Offset(border, border), Size(innerSize.width, FaShadows.wellInset.toPx()))
                }
            }
            .padding(FaDimens.line2)
            .padding(horizontal = FaDimens.space1)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(FaDimens.space1),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                Modifier
                    .height(FaDimens.touchMin)
                    .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(option) }),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .height(FaDimens.toggleHeight)
                        .drawBehind {
                            if (isSelected) {
                                val border = FaDimens.line2.toPx()
                                val radius = CornerRadius(FaDimens.radiusSm.toPx(), FaDimens.radiusSm.toPx())
                                val lip = FaDimens.line1.toPx()
                                val face = Size(size.width, size.height - lip)
                                drawRoundRect(colors.outline, Offset(0f, lip), face, radius)
                                drawRoundRect(colors.outline, Offset.Zero, face, radius)
                                drawRoundRect(
                                    colors.sun,
                                    Offset(border, border),
                                    Size(face.width - 2f * border, face.height - 2f * border),
                                    CornerRadius(radius.x - border, radius.y - border),
                                )
                            }
                        }
                        .padding(horizontal = FaDimens.space4),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        label(option),
                        style = type.buttonLabelSm.copy(color = if (isSelected) colors.onAccent else colors.ink),
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
