package nl.hexmaster.finalapproach.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaDimens

/** No distance at all. Not a design value, so it is not a token. */
internal val ZeroDp = Dp(0f)

/** Geometry of the shared surfaces that is not a token of its own. */
private object FaSurfaceDefaults {
    /** Distance from a control to the centre line of the inner band of its focus ring, in band widths. */
    const val RING_INNER_CENTRE = 0.5f

    /** Distance from a control to the centre line of the outer band of its focus ring, in band widths. */
    const val RING_OUTER_CENTRE = 1.5f
}

/**
 * The four tones of one moulded surface: the highlight along the top of the face, the face, the
 * lip under it and the line along the bottom of the lip.
 */
@Immutable
internal data class FaTone(val light: Color, val base: Color, val lip: Color, val edge: Color)

internal val FaColors.tonePrimary get() = FaTone(sun200, sun500, sun700, sun900)
internal val FaColors.toneSecondary get() = FaTone(skyBand, sky500, sky700, sky900)
internal val FaColors.toneDanger get() = FaTone(cherry200, cherry500, cherry700, cherry900)
internal val FaColors.toneGhost get() = FaTone(paper0, paper0, paper300, paper500)

/** Panels, dialogs and level cards. */
internal val FaColors.tonePaper get() = FaTone(paper0, paper100, paper400, paper500)

/** Any disabled control: a flat paper face without a highlight, on a sliver of lip without a darker edge. */
internal val FaColors.toneDisabled get() = FaTone(light = paper200, base = paper200, lip = paper300, edge = paper300)

/** The knob of a toggle. */
internal val FaColors.toneKnob get() = FaTone(paper0, paper0, paper400, paper400)

/** The selected segment of a segmented control. */
internal val FaColors.toneSegment get() = FaTone(sun200, sun500, sun700, sun700)

/**
 * How far the touch target of a control reaches beyond what is drawn of it on each side: a control
 * [drawn] lower or narrower than `touch-min` stands in the middle of a full touch target. Zero for
 * a control that is a touch target by itself.
 */
internal fun touchInset(drawn: Dp): Dp = ((FaDimens.touchMin - drawn) / 2).coerceAtLeast(ZeroDp)

/**
 * The two clip paths [drawMoulded] works with. Create one per call site in the cache block of
 * `drawWithCache`, so drawing a frame allocates nothing; [drawMoulded] rewinds them itself.
 */
internal class MouldedPaths(val body: Path = Path(), val face: Path = Path())

/**
 * The moulded surface: what every pressable thing, and every panel, is made of. Back to front: a
 * flat contact shadow, the outline, the lip with a line of its edge tone along the bottom, and the
 * face with a highlight along its top. All lengths are in pixels; [size] includes the outline and
 * the resting lip.
 *
 * While pressed, [lip] is smaller than [restLip]: the face and the top of the outline move down by
 * the difference and the bottom stays where it is. Inner corners follow the nested-radius rule.
 *
 * @param paths reused clip paths; their content is replaced
 * @param insetLip the lip is cut out of the piece's own shape (a round knob, a segment) instead of
 *   being a band under a face with its own corners
 */
internal fun DrawScope.drawMoulded(
    tone: FaTone,
    paths: MouldedPaths,
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
    val bodyPath = paths.body
    bodyPath.rewind()
    bodyPath.addRoundRect(RoundRect(body, inner))
    val facePath = paths.face
    facePath.rewind()
    if (insetLip) {
        facePath.addRoundRect(RoundRect(body.translate(0f, -lip), inner))
    } else {
        val bottom = CornerRadius((inner.x - lip).coerceAtLeast(0f))
        facePath.addRoundRect(
            RoundRect(
                Rect(body.left, body.top, body.right, body.bottom - lip),
                topLeft = inner,
                topRight = inner,
                bottomRight = bottom,
                bottomLeft = bottom,
            ),
        )
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

/**
 * A sunken well: a fill inside the outline with a band of [edge] along its inner top.
 *
 * @param clip a reused clip path, created in the cache block of `drawWithCache`; its content is replaced
 */
internal fun DrawScope.drawWell(
    clip: Path,
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
    clip.rewind()
    clip.addRoundRect(RoundRect(body, inner))
    clipPath(clip) {
        drawRect(edge, body.topLeft, body.size)
        drawRoundRect(fill, body.topLeft + Offset(0f, inset), body.size, inner)
    }
}

/**
 * The focus ring of a control whose drawn bounds are [topLeft] and [size], with outer corner
 * [radius]: two bands, each [band] wide, the [focus] colour next to the control and the [outline]
 * colour outside it. All lengths are in pixels.
 */
internal fun DrawScope.drawFocusRing(
    focus: Color,
    outline: Color,
    radius: Float,
    topLeft: Offset = Offset.Zero,
    size: Size = this.size,
    band: Float = FaDimens.lineUi.toPx(),
) {
    val outer = band * FaSurfaceDefaults.RING_OUTER_CENTRE
    drawRoundRect(
        outline,
        topLeft - Offset(outer, outer),
        Size(size.width + 2f * outer, size.height + 2f * outer),
        CornerRadius(radius + outer),
        style = Stroke(band),
    )
    val inner = band * FaSurfaceDefaults.RING_INNER_CENTRE
    drawRoundRect(
        focus,
        topLeft - Offset(inner, inner),
        Size(size.width + 2f * inner, size.height + 2f * inner),
        CornerRadius(radius + inner),
        style = Stroke(band),
    )
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
            floatArrayOf(FaDimens.ruleDash.toPx(), FaDimens.ruleGap.toPx()),
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
): Modifier = drawWithCache {
    val paths = MouldedPaths()
    onDrawBehind {
        drawMoulded(
            tone = tone,
            paths = paths,
            outline = outline,
            border = border.toPx(),
            radius = radius.toPx(),
            lip = lip.toPx(),
            contact = contact,
            contactOffset = contactOffset.toPx(),
        )
    }
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
