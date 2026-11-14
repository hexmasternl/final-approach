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
import nl.hexmaster.finalapproach.resources.nunito_bold
import nl.hexmaster.finalapproach.resources.nunito_extrabold
import nl.hexmaster.finalapproach.resources.nunito_semibold
import org.jetbrains.compose.resources.Font

/** One text style per type style of the design system. */
@Immutable
class FaTypography(
    val displayXl: TextStyle,
    val displayLg: TextStyle,
    val displayMd: TextStyle,
    val score: TextStyle,
    val scoreCompact: TextStyle,
    val stripValue: TextStyle,
    val dialogTitle: TextStyle,
    val dialogTitleCompact: TextStyle,
    val titleLg: TextStyle,
    val titleMd: TextStyle,
    val titleSm: TextStyle,
    val buttonLabelLg: TextStyle,
    val buttonLabel: TextStyle,
    val buttonLabelSm: TextStyle,
    val bodyLg: TextStyle,
    val row: TextStyle,
    val body: TextStyle,
    val bodySm: TextStyle,
    val caption: TextStyle,
    val readout: TextStyle,
    val label: TextStyle,
    val callsign: TextStyle,
    val labelSm: TextStyle,
)

/**
 * DynaPuff for headers, titles, button labels and the score; Nunito for running text;
 * Inconsolata for labels, readouts and callsigns. Only the weights the app ships are bundled:
 * other weights resolve to the nearest one.
 */
@Composable
fun rememberFaTypography(): FaTypography {
    val display = FontFamily(
        Font(Res.font.dynapuff_semibold, FontWeight.SemiBold),
        Font(Res.font.dynapuff_bold, FontWeight.Bold),
    )
    val body = FontFamily(
        Font(Res.font.nunito_semibold, FontWeight.SemiBold),
        Font(Res.font.nunito_bold, FontWeight.Bold),
        Font(Res.font.nunito_extrabold, FontWeight.ExtraBold),
    )
    val text = FontFamily(
        Font(Res.font.inconsolata_extrabold, FontWeight.ExtraBold),
    )
    return remember(display, body, text) {
        FaTypography(
            displayXl = TextStyle(fontFamily = display, fontWeight = FontWeight(700), fontSize = 72.sp, lineHeight = 68.sp, letterSpacing = -0.01.em),
            displayLg = TextStyle(fontFamily = display, fontWeight = FontWeight(700), fontSize = 48.sp, lineHeight = 48.sp),
            displayMd = TextStyle(fontFamily = display, fontWeight = FontWeight(700), fontSize = 36.sp, lineHeight = 40.sp),
            score = TextStyle(fontFamily = display, fontWeight = FontWeight(700), fontSize = 32.sp, lineHeight = 32.sp, fontFeatureSettings = "tnum"),
            scoreCompact = TextStyle(fontFamily = display, fontWeight = FontWeight(700), fontSize = 24.sp, lineHeight = 24.sp, fontFeatureSettings = "tnum"),
            stripValue = TextStyle(fontFamily = display, fontWeight = FontWeight(700), fontSize = 21.sp, lineHeight = 24.sp, fontFeatureSettings = "tnum"),
            dialogTitle = TextStyle(fontFamily = display, fontWeight = FontWeight(700), fontSize = 38.sp, lineHeight = 40.sp),
            dialogTitleCompact = TextStyle(fontFamily = display, fontWeight = FontWeight(700), fontSize = 32.sp, lineHeight = 36.sp),
            titleLg = TextStyle(fontFamily = display, fontWeight = FontWeight(600), fontSize = 28.sp, lineHeight = 32.sp),
            titleMd = TextStyle(fontFamily = display, fontWeight = FontWeight(600), fontSize = 22.sp, lineHeight = 28.sp),
            titleSm = TextStyle(fontFamily = display, fontWeight = FontWeight(600), fontSize = 18.sp, lineHeight = 24.sp),
            buttonLabelLg = TextStyle(fontFamily = display, fontWeight = FontWeight(700), fontSize = 24.sp, lineHeight = 28.sp),
            buttonLabel = TextStyle(fontFamily = display, fontWeight = FontWeight(600), fontSize = 18.sp, lineHeight = 20.sp),
            buttonLabelSm = TextStyle(fontFamily = display, fontWeight = FontWeight(600), fontSize = 15.sp, lineHeight = 18.sp),
            bodyLg = TextStyle(fontFamily = body, fontWeight = FontWeight(600), fontSize = 18.sp, lineHeight = 28.sp),
            row = TextStyle(fontFamily = body, fontWeight = FontWeight(700), fontSize = 15.sp, lineHeight = 20.sp),
            body = TextStyle(fontFamily = body, fontWeight = FontWeight(600), fontSize = 16.sp, lineHeight = 24.sp),
            bodySm = TextStyle(fontFamily = body, fontWeight = FontWeight(600), fontSize = 14.sp, lineHeight = 20.sp),
            caption = TextStyle(fontFamily = body, fontWeight = FontWeight(700), fontSize = 12.sp, lineHeight = 16.sp),
            readout = TextStyle(fontFamily = text, fontWeight = FontWeight(800), fontSize = 20.sp, lineHeight = 24.sp),
            label = TextStyle(fontFamily = text, fontWeight = FontWeight(800), fontSize = 13.sp, lineHeight = 16.sp, letterSpacing = 0.08.em),
            callsign = TextStyle(fontFamily = text, fontWeight = FontWeight(800), fontSize = 12.sp, lineHeight = 14.sp, letterSpacing = 0.04.em),
            labelSm = TextStyle(fontFamily = text, fontWeight = FontWeight(800), fontSize = 11.5.sp, lineHeight = 14.sp, letterSpacing = 0.09.em),
        )
    }
}
