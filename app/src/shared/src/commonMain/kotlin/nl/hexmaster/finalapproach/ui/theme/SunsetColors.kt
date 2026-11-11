// Generated from the design system (docs/design-system/tokens.json). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * The palette of the splash page: four flat sky bands, the sun, the airport silhouette and
 * the lit tower window. It is not a theme, and nothing but the splash page may use it.
 */
@Immutable
data class SunsetColors(
    val sunsetSkyTop: Color,
    val sunsetSkyUpper: Color,
    val sunsetSkyLower: Color,
    val sunsetSkyHorizon: Color,
    val sunsetSun: Color,
    val sunsetSilhouette: Color,
    val sunsetTowerLight: Color,
)

val Sunset = SunsetColors(
    sunsetSkyTop = Color(0xFFA82A2A),
    sunsetSkyUpper = Color(0xFFD4481F),
    sunsetSkyLower = Color(0xFFF0731F),
    sunsetSkyHorizon = Color(0xFFFFA23A),
    sunsetSun = Color(0xFFFFE08A),
    sunsetSilhouette = Color(0xFF2A1630),
    sunsetTowerLight = Color(0xFFFFE9A8),
)
