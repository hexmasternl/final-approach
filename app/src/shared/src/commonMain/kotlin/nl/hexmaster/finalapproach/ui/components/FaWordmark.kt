package nl.hexmaster.finalapproach.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Regular is the main page size; small is for the pause screen, credits and compact heights. */
enum class FaWordmarkSize { Regular, Small }

/** Values of the Wordmark component (`.fa-wordmark` in `bundle.css`) that are not tokens. */
private object FaWordmarkDefaults {
    val stroke = 12.dp
    val drop = 7.dp
    val strokeSmall = 8.dp
    val dropSmall = 4.dp
    const val TILT_DEGREES = -3f
    val secondLineIndent = 0.35.em
    val fontSizeSmall = 36.sp
    val lineHeightSmall = 34.sp

    const val FIRST_LINE = "Final"
    const val SECOND_LINE = "Approach"

    /** What a screen reader says, once, as a heading. */
    const val DESCRIPTION = "$FIRST_LINE $SECOND_LINE"
}

/**
 * The game's name as the design system's Wordmark: "Final" in fuselage, "Approach" in sun, both
 * with a thick outline behind the fill and a hard drop, tilted a little. The size it takes in a
 * layout includes the tilt, the outline and the drop, so nothing is clipped.
 */
@Composable
fun FaWordmark(
    modifier: Modifier = Modifier,
    size: FaWordmarkSize = FaWordmarkSize.Regular,
) {
    val colors = FaTheme.colors
    val regular = FaTheme.type.displayXl
    val small = size == FaWordmarkSize.Small
    val strokeWidth: Dp = if (small) FaWordmarkDefaults.strokeSmall else FaWordmarkDefaults.stroke
    val drop: Dp = if (small) FaWordmarkDefaults.dropSmall else FaWordmarkDefaults.drop
    val strokePx = with(LocalDensity.current) { strokeWidth.toPx() }
    val base: TextStyle = remember(regular, small) {
        if (small) {
            regular.copy(fontSize = FaWordmarkDefaults.fontSizeSmall, lineHeight = FaWordmarkDefaults.lineHeightSmall)
        } else {
            regular
        }
    }
    val outlineText = remember { text(FaWordmarkDefaults.FIRST_LINE, FaWordmarkDefaults.SECOND_LINE, null, null) }
    val fillText = remember(colors.fuselage, colors.sun) {
        text(FaWordmarkDefaults.FIRST_LINE, FaWordmarkDefaults.SECOND_LINE, colors.fuselage, colors.sun)
    }
    val outlineFill = base.copy(color = colors.outline, drawStyle = Fill)
    val outlineStroke = base.copy(color = colors.outline, drawStyle = Stroke(width = strokePx, join = StrokeJoin.Round))

    Box(
        modifier
            .clearAndSetSemantics {
                contentDescription = FaWordmarkDefaults.DESCRIPTION
                heading()
            }
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val radians = FaWordmarkDefaults.TILT_DEGREES * (PI.toFloat() / 180f)
                val c = abs(cos(radians))
                val s = abs(sin(radians))
                val width = (placeable.width * c + placeable.height * s).roundToInt()
                val height = (placeable.width * s + placeable.height * c).roundToInt()
                layout(width, height) {
                    placeable.place((width - placeable.width) / 2, (height - placeable.height) / 2)
                }
            }
            .graphicsLayer { rotationZ = FaWordmarkDefaults.TILT_DEGREES }
            .padding(start = strokeWidth / 2, top = strokeWidth / 2, end = strokeWidth / 2, bottom = strokeWidth / 2 + drop),
    ) {
        Box(Modifier.offset(y = drop)) {
            BasicText(outlineText, style = outlineFill)
            BasicText(outlineText, style = outlineStroke)
        }
        BasicText(outlineText, style = outlineStroke)
        BasicText(fillText, style = base.copy(drawStyle = Fill))
    }
}

private fun text(first: String, second: String, firstColor: Color?, secondColor: Color?): AnnotatedString =
    buildAnnotatedString {
        withStyle(ParagraphStyle()) { withStyle(firstColor.span()) { append(first) } }
        withStyle(ParagraphStyle(textIndent = TextIndent(firstLine = FaWordmarkDefaults.secondLineIndent))) {
            withStyle(secondColor.span()) { append(second) }
        }
    }

private fun Color?.span(): SpanStyle =
    if (this == null) SpanStyle() else SpanStyle(color = this)
