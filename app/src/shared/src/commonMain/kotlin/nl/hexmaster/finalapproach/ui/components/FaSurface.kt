package nl.hexmaster.finalapproach.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draws one outlined block of the design system: a flat fill inside a thick outline, standing
 * on a hard lip. Layers, back to front: optional soft lip, lip, outline, fill.
 *
 * @param top where the face starts, in pixels: 0 at rest, a little lower while pressed
 * @param faceHeight height of the face in pixels, without its lip
 */
internal fun DrawScope.drawBlock(
    fill: Color,
    outline: Color,
    border: Float,
    radius: Float,
    lip: Float,
    top: Float = 0f,
    faceHeight: Float = size.height - lip,
    softLip: Float = 0f,
    softLipColor: Color = Color.Transparent,
) {
    val face = Size(size.width, faceHeight)
    val corner = CornerRadius(radius, radius)
    if (softLip > 0f) drawRoundRect(softLipColor, Offset(0f, top + softLip), face, corner)
    if (lip > 0f) drawRoundRect(outline, Offset(0f, top + lip), face, corner)
    drawRoundRect(outline, Offset(0f, top), face, corner)
    val inner = (radius - border).coerceAtLeast(0f)
    drawRoundRect(
        fill,
        Offset(border, top + border),
        Size(face.width - 2f * border, face.height - 2f * border),
        CornerRadius(inner, inner),
    )
}

/**
 * Makes the element an outlined block on a hard lip. The element should reserve [lip] (or
 * [softLip], when larger) of extra height at the bottom for the lip to show.
 */
internal fun Modifier.faBlock(
    fill: Color,
    outline: Color,
    border: Dp,
    radius: Dp,
    lip: Dp,
    softLip: Dp = 0.dp,
    softLipColor: Color = Color.Transparent,
): Modifier = drawBehind {
    val reserved = maxOf(lip, softLip).toPx()
    drawBlock(
        fill = fill,
        outline = outline,
        border = border.toPx(),
        radius = radius.toPx(),
        lip = lip.toPx(),
        faceHeight = size.height - reserved,
        softLip = softLip.toPx(),
        softLipColor = softLipColor,
    )
}
