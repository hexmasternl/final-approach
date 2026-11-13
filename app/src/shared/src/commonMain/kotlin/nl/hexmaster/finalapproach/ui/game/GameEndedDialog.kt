package nl.hexmaster.finalapproach.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.engine.GameOverReason
import nl.hexmaster.finalapproach.ui.components.FaButtonVariant
import nl.hexmaster.finalapproach.ui.components.FaDialog
import nl.hexmaster.finalapproach.ui.components.FaDialogAction
import nl.hexmaster.finalapproach.ui.components.FaDialogLayout
import nl.hexmaster.finalapproach.ui.components.FaDialogStat
import nl.hexmaster.finalapproach.ui.components.FaDialogStats
import nl.hexmaster.finalapproach.ui.components.FaDialogTone
import nl.hexmaster.finalapproach.ui.components.LocalFaDialogLayout
import nl.hexmaster.finalapproach.ui.components.displayName
import nl.hexmaster.finalapproach.ui.components.formatScore
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.sprites.drawAircraftBody
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

internal object GameEndedText {
    const val EYEBROW = "Tower log"
    const val TITLE = "Game over"
    const val COLLISION = "Two aircraft got too close."
    const val LEFT_AREA = "An aircraft left without landing."
    const val COLLISION_STAMP = "Too close"
    const val LEFT_AREA_STAMP = "Left the area"
    const val TOTAL = "Total landed"
    const val POINTS = "Points"
    const val TRY_AGAIN = "Try again"
    const val MAIN_PAGE = "Main page"

    fun body(reason: GameOverReason): String = when (reason) {
        GameOverReason.Collision -> COLLISION
        GameOverReason.LeftArea -> LEFT_AREA
    }

    /** The reason in two or three words, for the stamp under the body. */
    fun stamp(reason: GameOverReason): String = when (reason) {
        GameOverReason.Collision -> COLLISION_STAMP
        GameOverReason.LeftArea -> LEFT_AREA_STAMP
    }
}

/**
 * The dialog that ends a level, as a boarding pass: why (as a sentence, and stamped), then on the
 * stub how many of each type landed, the total and the points, and the two buttons.
 */
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
        eyebrow = GameEndedText.EYEBROW,
        tone = FaDialogTone.Failure,
        stamp = GameEndedText.stamp(reason),
        content = {
            FaDialogStats(
                types.map { type -> FaDialogStat(type.displayName, formatScore(landed[type] ?: 0)) { AircraftPicture(type) } } +
                    FaDialogStat(GameEndedText.TOTAL, formatScore(total)) +
                    FaDialogStat(GameEndedText.POINTS, formatScore(points)),
            )
        },
        below = below,
    )
}

/** The aircraft a stat counts, smaller in the compact layout so four rows fit a landscape phone. */
@Composable
private fun AircraftPicture(type: AircraftType) {
    val colors = FaTheme.colors
    val compact = LocalFaDialogLayout.current == FaDialogLayout.Compact
    Canvas(Modifier.size(if (compact) FaDimens.iconMd else FaDimens.iconLg)) {
        drawAircraftBody(type, colors.fuselage, Offset(size.width / 2f, size.height / 2f), size.minDimension, 0f, 0f, colors)
    }
}
