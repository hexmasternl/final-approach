// Generated from the design system (docs/design-system/tokens.json). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import nl.hexmaster.finalapproach.resources.Res
import nl.hexmaster.finalapproach.resources.dynapuff_bold
import nl.hexmaster.finalapproach.resources.dynapuff_semibold
import nl.hexmaster.finalapproach.resources.inconsolata_extrabold
import nl.hexmaster.finalapproach.resources.inconsolata_medium
import org.jetbrains.compose.resources.Font

/** One text style per type style of the design system. */
@Immutable
class FaTypography(
    val displayXl: TextStyle,
    val displayLg: TextStyle,
    val displayMd: TextStyle,
    val score: TextStyle,
    val titleLg: TextStyle,
    val titleMd: TextStyle,
    val titleSm: TextStyle,
    val buttonLabel: TextStyle,
    val buttonLabelSm: TextStyle,
    val bodyLg: TextStyle,
    val body: TextStyle,
    val bodySm: TextStyle,
    val caption: TextStyle,
    val readout: TextStyle,
    val label: TextStyle,
    val callsign: TextStyle,
)

/**
 * DynaPuff for headers, titles, button labels and the score; Inconsolata for regular text,
 * labels and readouts. Only the weights the app ships are bundled: other weights resolve to
 * the nearest one.
 */
@Composable
fun rememberFaTypography(): FaTypography {
    val display = FontFamily(
        Font(Res.font.dynapuff_semibold, FontWeight.SemiBold),
        Font(Res.font.dynapuff_bold, FontWeight.Bold),
    )
    val text = FontFamily(
        Font(Res.font.inconsolata_medium, FontWeight.Medium),
        Font(Res.font.inconsolata_extrabold, FontWeight.ExtraBold),
    )
    return remember(display, text) {
        FaTypography(
            displayXl = TextStyle(fontFamily = display, fontWeight = FontWeight(700), fontSize = 72.sp, lineHeight = 68.sp, letterSpacing = -0.01.em),
            displayLg = TextStyle(fontFamily = display, fontWeight = FontWeight(700), fontSize = 48.sp, lineHeight = 48.sp),
            displayMd = TextStyle(fontFamily = display, fontWeight = FontWeight(700), fontSize = 36.sp, lineHeight = 40.sp),
            score = TextStyle(fontFamily = display, fontWeight = FontWeight(700), fontSize = 32.sp, lineHeight = 32.sp),
            titleLg = TextStyle(fontFamily = display, fontWeight = FontWeight(600), fontSize = 28.sp, lineHeight = 32.sp),
            titleMd = TextStyle(fontFamily = display, fontWeight = FontWeight(600), fontSize = 22.sp, lineHeight = 28.sp),
            titleSm = TextStyle(fontFamily = display, fontWeight = FontWeight(600), fontSize = 18.sp, lineHeight = 24.sp),
            buttonLabel = TextStyle(fontFamily = display, fontWeight = FontWeight(600), fontSize = 18.sp, lineHeight = 20.sp),
            buttonLabelSm = TextStyle(fontFamily = display, fontWeight = FontWeight(600), fontSize = 15.sp, lineHeight = 18.sp),
            bodyLg = TextStyle(fontFamily = text, fontWeight = FontWeight(500), fontSize = 18.sp, lineHeight = 28.sp),
            body = TextStyle(fontFamily = text, fontWeight = FontWeight(500), fontSize = 16.sp, lineHeight = 24.sp),
            bodySm = TextStyle(fontFamily = text, fontWeight = FontWeight(500), fontSize = 14.sp, lineHeight = 20.sp),
            caption = TextStyle(fontFamily = text, fontWeight = FontWeight(600), fontSize = 12.sp, lineHeight = 16.sp),
            readout = TextStyle(fontFamily = text, fontWeight = FontWeight(800), fontSize = 20.sp, lineHeight = 24.sp),
            label = TextStyle(fontFamily = text, fontWeight = FontWeight(800), fontSize = 13.sp, lineHeight = 16.sp, letterSpacing = 0.08.em),
            callsign = TextStyle(fontFamily = text, fontWeight = FontWeight(800), fontSize = 12.sp, lineHeight = 14.sp, letterSpacing = 0.04.em),
        )
    }
}
