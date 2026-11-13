package nl.hexmaster.finalapproach.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/**
 * The panel: the paper surface a screen's content stands on. A paper face on a paper lip inside
 * the outline, on a contact shadow.
 *
 * @param header optional band across the top, with a dashed rule under it: a title, a back button
 * @param content the body, padded by the panel
 */
@Composable
fun FaPanel(
    modifier: Modifier = Modifier,
    header: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = FaTheme.colors
    Column(
        modifier
            .faMouldedSurface(
                tone = colors.tonePaper,
                outline = colors.outline,
                radius = FaDimens.radiusPanel,
                lip = FaDimens.lipPanel,
                contact = colors.contact,
                contactOffset = FaDimens.contactOffset,
            )
            .padding(
                start = FaDimens.lineUi,
                top = FaDimens.lineUi,
                end = FaDimens.lineUi,
                bottom = FaDimens.lineUi + FaDimens.lipPanel,
            )
            .clip(faFaceShape(FaDimens.radiusPanel, FaDimens.lipPanel)),
    ) {
        if (header != null) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(colors.paper200)
                    .drawBehind {
                        val line = FaDimens.lineDetail.toPx()
                        val y = size.height - line / 2f
                        drawDashedRule(colors.paper500, Offset(0f, y), Offset(size.width, y), line)
                    }
                    .padding(horizontal = FaDimens.space4, vertical = FaDimens.space3),
                horizontalArrangement = Arrangement.spacedBy(FaDimens.space4),
                verticalAlignment = Alignment.CenterVertically,
                content = header,
            )
        }
        Column(Modifier.fillMaxWidth().padding(FaDimens.space5), content = content)
    }
}

