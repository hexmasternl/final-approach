// Generated from the design system (docs/design-system/tokens.json). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Depth tokens of v1: a lip was a hard offset straight down in the outline colour. v2 draws a
 * lip in the control's own colour, from the lip and contact tokens in [FaDimens] and [FaColors].
 * Only the float offsets below are still current; everything else here is v1 and is kept until
 * the app's world is ported.
 */
object FaShadows {
    /** v1. Use [FaDimens.lipSm] in new code. */
    val lipSm = 3.dp
    /** v1. Use [FaDimens.lipMd] in new code. */
    val lip = 5.dp
    /** v1. Use [FaDimens.lipPanel] in new code. */
    val lipLg = 8.dp

    /** v1: the translucent second lip under a dialog. Use [FaColors.contactOverlay] in new code. */
    val softLip = 10.dp
    val softLipDay = Color(29, 37, 69, 41)
    val softLipNight = Color(4, 7, 18, 102)

    /** v1: the inner top edge of a well. Use [FaDimens.wellInset] in new code. */
    val wellInset = 3.dp
    val wellDay = Color(29, 37, 69, 36)
    val wellNight = Color(4, 7, 18, 115)

    /** Ground shadow of airborne things: offset down-right. */
    val floatOffsetX = 6.dp
    val floatOffsetY = 10.dp
}
