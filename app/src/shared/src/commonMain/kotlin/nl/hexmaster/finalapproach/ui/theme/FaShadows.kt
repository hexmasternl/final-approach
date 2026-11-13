// Generated from the design system (docs/design-system/tokens.json). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Depth tokens. A lip is a hard, unblurred offset straight down in the outline colour. */
object FaShadows {
    val lipSm = 3.dp
    val lip = 5.dp
    val lipLg = 8.dp

    /** The translucent second lip that grounds a dialog on the scrim. */
    val softLip = 10.dp
    val softLipDay = Color(29, 37, 69, 41)
    val softLipNight = Color(4, 7, 18, 102)

    /** The inner top edge of sunken wells: toggle track, segmented trough. */
    val wellInset = 3.dp
    val wellDay = Color(29, 37, 69, 36)
    val wellNight = Color(4, 7, 18, 115)

    /** Ground shadow of airborne things: offset down-right. */
    val floatOffsetX = 6.dp
    val floatOffsetY = 10.dp
}
