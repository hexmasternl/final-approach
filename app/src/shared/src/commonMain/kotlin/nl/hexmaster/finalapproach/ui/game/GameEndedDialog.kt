package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.engine.GameOverReason
import nl.hexmaster.finalapproach.ui.components.FaButton
import nl.hexmaster.finalapproach.ui.components.FaButtonVariant
import nl.hexmaster.finalapproach.ui.components.FaDialog
import nl.hexmaster.finalapproach.ui.components.displayName
import nl.hexmaster.finalapproach.ui.components.faBlock
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.sprites.drawAircraftBody
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaShadows
import nl.hexmaster.finalapproach.ui.theme.FaTheme

internal object GameEndedText {
    const val TITLE = "Game over"
    const val COLLISION = "Two aircraft got too close."
    const val LEFT_AREA = "An aircraft left without landing."
    const val TOTAL = "Total landed"
    const val POINTS = "Points"
    const val TRY_AGAIN = "Try again"
    const val MAIN_PAGE = "Main page"

    fun body(reason: GameOverReason): String = when (reason) {
        GameOverReason.Collision -> COLLISION
        GameOverReason.LeftArea -> LEFT_AREA
    }
}

/** The dialog that ends a level: why, how many of each type landed, the total and the points. */
@Composable
fun GameEndedDialog(
    reason: GameOverReason,
    types: List<AircraftType>,
    landed: Map<AircraftType, Int>,
    total: Int,
    points: Int,
    onTryAgain: () -> Unit,
    onMainPage: () -> Unit,
) {
    FaDialog(
        GameEndedText.TITLE,
        GameEndedText.body(reason),
        content = {
            types.forEach { type -> StatsWell(type.displayName, landed[type] ?: 0, type) }
            StatsWell(GameEndedText.TOTAL, total, null)
            StatsWell(GameEndedText.POINTS, points, null)
        },
    ) {
        FaButton(GameEndedText.TRY_AGAIN, onTryAgain, Modifier.fillMaxWidth(), FaButtonVariant.Primary, icon = FaIcon.Restart)
        FaButton(GameEndedText.MAIN_PAGE, onMainPage, Modifier.fillMaxWidth(), FaButtonVariant.Ghost, icon = FaIcon.Home)
    }
}

@Composable
private fun StatsWell(label: String, value: Int, type: AircraftType?) {
    val colors = FaTheme.colors
    val style = FaTheme.type
    Row(
        Modifier
            .padding(top = FaDimens.space2)
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = "$label $value" }
            .faBlock(
                fill = colors.surfaceSunken,
                outline = colors.outline,
                border = FaDimens.line3,
                radius = FaDimens.radiusMd,
                lip = FaShadows.lipSm,
            )
            .padding(horizontal = FaDimens.space3, vertical = FaDimens.space2),
        horizontalArrangement = Arrangement.spacedBy(FaDimens.space3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (type != null) {
            Canvas(Modifier.size(FaDimens.iconLg)) {
                drawAircraftBody(type, colors.fuselage, Offset(size.width / 2f, size.height / 2f), size.minDimension, 0f, 0f, colors)
            }
        }
        BasicText(label, Modifier.weight(1f), style = style.label.copy(color = colors.inkMuted))
        BasicText(value.toString(), style = style.titleMd.copy(color = colors.ink))
    }
}
