package nl.hexmaster.finalapproach.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaDimens

/** No distance at all. Not a design value, so it is not a token. */
internal val ZeroDp = Dp(0f)

/** Geometry shared by the surfaces that is not a token of its own. */
internal object FaSurfaceDefaults {
    /** Length of one dash of a dashed rule: a panel's header rule, a dialog's perforation. */
    val ruleDash = Dp(6f)

    /** Gap between two dashes of a dashed rule. */
    val ruleGap = Dp(4f)
}

/**
 * The four tones of one moulded surface: the highlight along the top of the face, the face, the
 * lip under it and the line along the bottom of the lip.
 */
@Immutable
internal class FaTone(val light: Color, val base: Color, val lip: Color, val edge: Color)

internal val FaColors.tonePrimary get() = FaTone(sun200, sun500, sun700, sun900)
internal val FaColors.toneSecondary get() = FaTone(sky300, sky500, sky700, sky900)
internal val FaColors.toneDanger get() = FaTone(cherry200, cherry500, cherry700, cherry900)
internal val FaColors.toneGhost get() = FaTone(paper0, paper0, paper300, paper500)

/** Panels, dialogs and level cards. */
internal val FaColors.tonePaper get() = FaTone(paper0, paper100, paper400, paper500)

/** Any disabled control: paper, without a coloured face. */
internal val FaColors.toneDisabled get() = FaTone(paper100, paper200, paper300, paper400)

/** The knob of a toggle. */
internal val FaColors.toneKnob get() = FaTone(paper0, paper0, paper400, paper400)

/** The selected segment of a segmented control. */
internal val FaColors.toneSegment get() = FaTone(sun200, sun500, sun700, sun700)

/**
 * Draws one outlined block of version 1 of the design system: a flat fill inside a thick outline,
 * standing on a hard lip. Only the splash scene still draws with it; components use
 * [drawMoulded] and [drawFlat].
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
 * The moulded surface: what every pressable thing, and every panel, is made of. Back to front: a
 * flat contact shadow, the outline, the lip with a line of its edge tone along the bottom, and the
 * face with a highlight along its top. All lengths are in pixels; [size] includes the outline and
 * the resting lip.
 *
 * While pressed, [lip] is smaller than [restLip]: the face and the top of the outline move down by
 * the difference and the bottom stays where it is. Inner corners follow the nested-radius rule.
 *
 * @param insetLip the lip is cut out of the piece's own shape (a round knob, a segment) instead of
 *   being a band under a face with its own corners
 */
internal fun DrawScope.drawMoulded(
    tone: FaTone,
    outline: Color,
    border: Float,
    radius: Float,
    lip: Float,
    restLip: Float = lip,
    topLeft: Offset = Offset.Zero,
    size: Size = this.size,
    contact: Color = Color.Transparent,
    contactOffset: Float = 0f,
    insetLip: Boolean = false,
    lipEdge: Float = FaDimens.lipEdge.toPx(),
    highlight: Float = FaDimens.highlight.toPx(),
) {
    val travel = restLip - lip
    val corner = CornerRadius(radius)
    if (contactOffset > 0f) drawRoundRect(contact, topLeft + Offset(0f, contactOffset), size, corner)
    val outerTop = topLeft + Offset(0f, travel)
    drawRoundRect(outline, outerTop, Size(size.width, size.height - travel), corner)

    val inner = CornerRadius((radius - border).coerceAtLeast(0f))
    val body = Rect(
        left = outerTop.x + border,
        top = outerTop.y + border,
        right = outerTop.x + size.width - border,
        bottom = topLeft.y + size.height - border,
    )
    if (body.width <= 0f || body.height <= 0f) return
    val bodyPath = Path().apply { addRoundRect(RoundRect(body, inner)) }
    val facePath = Path().apply {
        if (insetLip) {
            addRoundRect(RoundRect(body.translate(0f, -lip), inner))
        } else {
            val bottom = CornerRadius((inner.x - lip).coerceAtLeast(0f))
            addRoundRect(
                RoundRect(
                    Rect(body.left, body.top, body.right, body.bottom - lip),
                    topLeft = inner,
                    topRight = inner,
                    bottomRight = bottom,
                    bottomLeft = bottom,
                ),
            )
        }
    }
    clipPath(bodyPath) {
        drawRect(tone.edge, body.topLeft, body.size)
        drawRoundRect(tone.lip, body.topLeft - Offset(0f, lipEdge), body.size, inner)
        clipPath(facePath) {
            drawRect(tone.light, body.topLeft, body.size)
            drawRoundRect(tone.base, body.topLeft + Offset(0f, highlight), body.size, inner)
        }
    }
}

/**
 * The flat surface: what every readout is made of. A fill inside the outline on a contact shadow.
 * It has no lip, so it never looks pressable.
 */
internal fun DrawScope.drawFlat(
    fill: Color,
    outline: Color,
    border: Float,
    radius: Float,
    contact: Color = Color.Transparent,
    contactOffset: Float = 0f,
    topLeft: Offset = Offset.Zero,
    size: Size = this.size,
) {
    val corner = CornerRadius(radius)
    if (contactOffset > 0f) drawRoundRect(contact, topLeft + Offset(0f, contactOffset), size, corner)
    drawRoundRect(outline, topLeft, size, corner)
    drawRoundRect(
        fill,
        topLeft + Offset(border, border),
        Size(size.width - 2f * border, size.height - 2f * border),
        CornerRadius((radius - border).coerceAtLeast(0f)),
    )
}

/** A sunken well: a fill inside the outline with a band of [edge] along its inner top. */
internal fun DrawScope.drawWell(
    fill: Color,
    edge: Color,
    outline: Color,
    border: Float,
    radius: Float,
    inset: Float = FaDimens.wellInset.toPx(),
    topLeft: Offset = Offset.Zero,
    size: Size = this.size,
) {
    drawRoundRect(outline, topLeft, size, CornerRadius(radius))
    val inner = CornerRadius((radius - border).coerceAtLeast(0f))
    val body = Rect(topLeft.x + border, topLeft.y + border, topLeft.x + size.width - border, topLeft.y + size.height - border)
    clipPath(Path().apply { addRoundRect(RoundRect(body, inner)) }) {
        drawRect(edge, body.topLeft, body.size)
        drawRoundRect(fill, body.topLeft + Offset(0f, inset), body.size, inner)
    }
}

/** A dashed rule from [from] to [to], [width] pixels thick. */
internal fun DrawScope.drawDashedRule(color: Color, from: Offset, to: Offset, width: Float) {
    drawLine(
        color,
        from,
        to,
        strokeWidth = width,
        cap = StrokeCap.Butt,
        pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(FaSurfaceDefaults.ruleDash.toPx(), FaSurfaceDefaults.ruleGap.toPx()),
        ),
    )
}

/**
 * Makes the element a moulded surface that is not pressed: a panel, a dialog. The element includes
 * its outline and [lip]; clip the content with [faFaceShape] to keep it on the face. The contact
 * shadow is drawn [contactOffset] below the element.
 */
internal fun Modifier.faMouldedSurface(
    tone: FaTone,
    outline: Color,
    radius: Dp,
    lip: Dp,
    contact: Color,
    contactOffset: Dp,
    border: Dp = FaDimens.lineUi,
): Modifier = drawBehind {
    drawMoulded(
        tone = tone,
        outline = outline,
        border = border.toPx(),
        radius = radius.toPx(),
        lip = lip.toPx(),
        contact = contact,
        contactOffset = contactOffset.toPx(),
    )
}

/** Makes the element a flat surface; see [drawFlat]. */
internal fun Modifier.faFlatSurface(
    fill: Color,
    outline: Color,
    radius: Dp,
    contact: Color,
    contactOffset: Dp,
    border: Dp = FaDimens.lineUi,
): Modifier = drawBehind {
    drawFlat(fill, outline, border.toPx(), radius.toPx(), contact, contactOffset.toPx())
}

/** The shape of the face of a moulded surface with outer [radius] and [lip], for clipping its content. */
internal fun faFaceShape(radius: Dp, lip: Dp, border: Dp = FaDimens.lineUi): Shape {
    val top = (radius - border).coerceAtLeast(ZeroDp)
    val bottom = (top - lip).coerceAtLeast(ZeroDp)
    return RoundedCornerShape(topStart = top, topEnd = top, bottomEnd = bottom, bottomStart = bottom)
}
