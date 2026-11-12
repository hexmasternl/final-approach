// Generated from the design system (docs/design-system/tokens.json). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Tokens for the lights of Dawn levels. Light is gentle: no light is brighter than
 * [MAX_OPACITY] and no light cycle is faster than [MIN_CYCLE_SECONDS].
 */
object FaLights {
    const val MAX_OPACITY = 0.6f
    const val MIN_CYCLE_SECONDS = 0.5

    /** One full flash of the control tower beacon, in seconds. */
    const val BEACON_CYCLE_SECONDS = 1.6

    /** One sweep of the running light along a runway, in seconds. */
    const val RUNNING_LIGHT_CYCLE_SECONDS = 1.2

    val navRed = Color(0xFFFF4D4D)
    val navGreen = Color(0xFF3DDC84)
    val white = Color(0xFFFFFFFF)
    val beacon = Color(0xFFFFF3B0)
    val agl = Color(0xFFFFE9A8)
}
