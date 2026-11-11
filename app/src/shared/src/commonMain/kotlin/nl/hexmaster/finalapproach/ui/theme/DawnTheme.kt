package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * The Dawn palette, for levels set before sunrise: the ground and strips are dim and warm, while
 * panels, chips, text and aircraft stay light as in Night. It is not an inverted UI.
 */
val DawnColors: FaColors = DayColors.copy(
    bgSky = Color(0xFF6C6A92),
    bgField = Color(0xFF8A8650),
    surface = Color(0xFFF4EBD8),
    sky100 = Color(0xFF3A3D6B),
    sky300 = Color(0xFF55578A),
    grass300 = Color(0xFFA29C5F),
    grass500 = Color(0xFF8A8650),
    grass700 = Color(0xFF726F42),
    grass900 = Color(0xFF4F4D30),
    asphalt300 = Color(0xFF7C8298),
    asphalt500 = Color(0xFF585E73),
    asphalt700 = Color(0xFF3B4156),
    marking = Color(0xFFE9E6E2),
    cloud = Color(0xFFD9B8C4),
    cloudShade = Color(0xFFA98CA8),
    shadowGround = Color(20, 22, 30, 90),
)

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
