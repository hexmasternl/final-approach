package nl.hexmaster.finalapproach.ui.game

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import nl.hexmaster.finalapproach.ui.theme.FaDimens

/**
 * The strokes and corner the ground pieces of the app's world still draw with, in pixels: the
 * [outline] (`line-2`), the [fine] detail line (`line-fine`) and the [corner] radius of flat
 * scenery (`radius-sm`). Interim, version 1: the ground pieces move to the design system's
 * ground metrics in tasks 5.2 and 5.3 of the `world-v2-port` change, and this goes with them.
 * Sprites no longer use it.
 */
internal class GroundLines(val outline: Float, val fine: Float, val corner: Float)

/** The ground lines for a world drawn at [pxPerWorldUnit]: world lengths that shrink with the field. */
internal fun DrawScope.groundLines(pxPerWorldUnit: Float): GroundLines {
    val pxPerUnit = pxPerWorldUnit * GROUND_UNITS_PER_DP
    fun shrinking(token: Dp): Float = minOf(token.toPx(), token.value * pxPerUnit)
    return GroundLines(
        outline = shrinking(FaDimens.line2),
        fine = shrinking(FaDimens.lineFine),
        corner = shrinking(FaDimens.radiusSm),
    )
}

private const val GROUND_UNITS_PER_DP = 0.8f
