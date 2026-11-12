package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import nl.hexmaster.finalapproach.ui.components.FaButtonVariant
import nl.hexmaster.finalapproach.ui.components.FaDialog
import nl.hexmaster.finalapproach.ui.components.FaDialogAction
import nl.hexmaster.finalapproach.ui.components.FaDialogLayout
import nl.hexmaster.finalapproach.ui.components.LocalFaDialogLayout
import nl.hexmaster.finalapproach.ui.components.displayName
import nl.hexmaster.finalapproach.ui.components.faBlock
import nl.hexmaster.finalapproach.ui.components.formatScore
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
    below: @Composable () -> Unit = {},
) {
    FaDialog(
        GameEndedText.TITLE,
        GameEndedText.body(reason),
        actions = listOf(
            FaDialogAction(GameEndedText.TRY_AGAIN, onTryAgain, FaButtonVariant.Primary, FaIcon.Restart),
            FaDialogAction(GameEndedText.MAIN_PAGE, onMainPage, FaButtonVariant.Ghost, FaIcon.Home),
        ),
        content = {
            val compact = LocalFaDialogLayout.current == FaDialogLayout.Compact
            val wells = types.map { Triple(it.displayName, landed[it] ?: 0, it) } +
                Triple(GameEndedText.TOTAL, total, null) +
                Triple(GameEndedText.POINTS, points, null)
            Column(
                if (compact) Modifier.fillMaxWidth() else Modifier.fillMaxWidth().padding(top = FaDimens.space3),
                verticalArrangement = Arrangement.spacedBy(if (compact) FaDimens.space1 else FaDimens.space2),
            ) {
                // A two-column grid; in the compact layout it sits beside the copy, in one-line wells.
                wells.chunked(GRID_COLUMNS).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(FaDimens.space2)) {
                        row.forEach { (label, value, type) ->
                            if (compact) {
                                CompactStatsWell(label, value, type, Modifier.weight(1f))
                            } else {
                                StatsWell(label, value, type, Modifier.weight(1f))
                            }
                        }
                        repeat(GRID_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        },
        below = below,
    )
}

private const val GRID_COLUMNS = 2

@Composable
private fun StatsWell(label: String, value: Int, type: AircraftType?, modifier: Modifier = Modifier) {
    val colors = FaTheme.colors
    val style = FaTheme.type
    Row(
        modifier
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

/** The compact stats well: aircraft, label and value on one line, so seven wells fit a landscape phone. */
@Composable
private fun CompactStatsWell(label: String, value: Int, type: AircraftType?, modifier: Modifier = Modifier) {
    val colors = FaTheme.colors
    val style = FaTheme.type
    val shown = formatScore(value)
    Row(
        modifier
            .semantics(mergeDescendants = true) { contentDescription = "$label $shown" }
            .faBlock(
                fill = colors.surfaceSunken,
                outline = colors.outline,
                border = FaDimens.line2,
                radius = FaDimens.radiusMd,
            )
            .padding(FaDimens.space2),
        horizontalArrangement = Arrangement.spacedBy(FaDimens.space2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (type != null) {
            Canvas(Modifier.size(FaDimens.iconMd)) {
                drawAircraftBody(type, colors.fuselage, Offset(size.width / 2f, size.height / 2f), size.minDimension, 0f, 0f, colors)
            }
        }
        // The label style is written in capitals.
        BasicText(label.uppercase(), Modifier.weight(1f), style = style.label.copy(color = colors.inkMuted))
        BasicText(shown, style = style.titleSm.copy(color = colors.ink), maxLines = 1)
    }
}
