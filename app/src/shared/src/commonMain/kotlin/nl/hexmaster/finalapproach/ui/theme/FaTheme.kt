package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LocalFaColors = staticCompositionLocalOf { DayColors }
private val LocalFaNight = staticCompositionLocalOf { false }
private val LocalFaReduceMotion = staticCompositionLocalOf { false }
private val LocalFaTypography = staticCompositionLocalOf<FaTypography> {
    error("FaTheme is missing: wrap the content in FaTheme { }")
}

/**
 * Provides the design system to everything inside [content]. Day is the default theme;
 * Night is for night levels. With [reduceMotion], components change state at once instead of
 * animating.
 */
@Composable
fun FaTheme(
    night: Boolean = false,
    reduceMotion: Boolean = false,
    dawn: Boolean = false,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalFaColors provides when {
            night -> NightColors
            dawn -> DawnColors
            else -> DayColors
        },
        LocalFaNight provides night,
        LocalFaReduceMotion provides reduceMotion,
        LocalFaTypography provides rememberFaTypography(),
        content = content,
    )
}

/** Access to the current theme: `FaTheme.colors`, `FaTheme.type`. */
object FaTheme {
    val colors: FaColors
        @Composable @ReadOnlyComposable get() = LocalFaColors.current

    val type: FaTypography
        @Composable @ReadOnlyComposable get() = LocalFaTypography.current

    val isNight: Boolean
        @Composable @ReadOnlyComposable get() = LocalFaNight.current

    /** True when animation should be reduced: the device asks for it, or the player turned it on. */
    val reduceMotion: Boolean
        @Composable @ReadOnlyComposable get() = LocalFaReduceMotion.current

    /** Colour of the translucent second lip under a dialog. */
    val softLip: Color
        @Composable @ReadOnlyComposable get() = if (LocalFaNight.current) FaShadows.softLipNight else FaShadows.softLipDay
}
