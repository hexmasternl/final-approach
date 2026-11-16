package nl.hexmaster.finalapproach.ui.sprites

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import nl.hexmaster.finalapproach.ui.theme.FaColors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * A sprite ready to draw: every path parsed once, with the transforms of its groups already
 * applied to the geometry. Applying them to the geometry, and not to the canvas, is what lets a
 * non-scaling stroke keep its width under a squashing group (the acacia's crown).
 */
internal sealed class BakedNode

private class BakedShape(
    val fill: SceneryPaint?,
    val stroke: SceneryPaint?,
    val strokeWidth: Float,
    val opacity: Float,
    val path: Path,
) : BakedNode()

private class BakedGroup(val clip: Path?, val children: List<BakedNode>) : BakedNode()

/** A sprite's baked shapes and the bounds of their geometry, strokes and shadow left out. */
internal class BakedArt internal constructor(internal val nodes: List<BakedNode>, val bounds: Rect)

/** An SVG matrix `[a, b, c, d, e, f]`: x' = ax + cy + e, y' = bx + dy + f. */
private class Affine(val a: Float, val b: Float, val c: Float, val d: Float, val e: Float, val f: Float) {
    /** This matrix applied after [n]: first [n], then this. */
    fun after(n: Affine) = Affine(
        a * n.a + c * n.b, b * n.a + d * n.b,
        a * n.c + c * n.d, b * n.c + d * n.d,
        a * n.e + c * n.f + e, b * n.e + d * n.f + f,
    )

    /** How much the matrix scales a length, evenly: the square root of its area factor. */
    fun lengthScale(): Float = sqrt(abs(a * d - b * c))

    fun toMatrix(): Matrix = Matrix().also {
        it.values[Matrix.ScaleX] = a
        it.values[Matrix.SkewY] = b
        it.values[Matrix.SkewX] = c
        it.values[Matrix.ScaleY] = d
        it.values[Matrix.TranslateX] = e
        it.values[Matrix.TranslateY] = f
    }
}

private fun affineOf(svg: List<Float>) = Affine(svg[0], svg[1], svg[2], svg[3], svg[4], svg[5])

private fun transformed(path: Path, affine: Affine?): Path =
    if (affine == null) path else Path().apply { addPath(path); transform(affine.toMatrix()) }

private val baked = HashMap<SceneryArt, BakedArt>()

/** The baked form of [art]. Built on first use and kept. */
internal fun bakedArt(art: SceneryArt): BakedArt = baked.getOrPut(art) {
    var bounds: Rect? = null
    fun grow(r: Rect) {
        bounds = bounds?.let { Rect(min(it.left, r.left), min(it.top, r.top), max(it.right, r.right), max(it.bottom, r.bottom)) } ?: r
    }
    fun bake(nodes: List<SceneryNode>, outer: Affine?): List<BakedNode> = nodes.map { node ->
        when (node) {
            is SceneryGroup -> {
                val m = node.matrix?.let { svg -> outer?.after(affineOf(svg)) ?: affineOf(svg) } ?: outer
                BakedGroup(node.clip?.let { transformed(parsePath(it), m) }, bake(node.children, m))
            }
            is SceneryShape -> {
                val m = node.matrix?.let { svg -> outer?.after(affineOf(svg)) ?: affineOf(svg) } ?: outer
                val path = transformed(parsePath(node.pathData), m)
                grow(path.getBounds())
                // A stroke grows and shrinks with the matrices above it, unless it is non-scaling.
                val widthScale = if (node.nonScalingStroke || m == null) 1f else m.lengthScale()
                BakedShape(node.fill, node.stroke, node.strokeWidth * widthScale, node.opacity, path)
            }
        }
    }
    val nodes = bake(art.nodes, null)
    BakedArt(nodes, bounds ?: Rect.Zero)
}

private fun Color.withOpacity(opacity: Float) = if (opacity == 1f) this else copy(alpha = alpha * opacity)

private fun DrawScope.drawBaked(nodes: List<BakedNode>, colors: FaColors) {
    for (node in nodes) {
        when (node) {
            is BakedGroup -> {
                val clip = node.clip
                if (clip == null) drawBaked(node.children, colors)
                else withTransform({ clipPath(clip) }) { drawBaked(node.children, colors) }
            }
            is BakedShape -> {
                node.fill?.let { drawPath(node.path, colors.scenery(it).withOpacity(node.opacity)) }
                node.stroke?.let {
                    val style = Stroke(node.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    drawPath(node.path, colors.scenery(it).withOpacity(node.opacity), style = style)
                }
            }
        }
    }
}

/** The bounds of the sprite's own shapes (shadow and strokes left out), in the sprite's units. */
internal val ScenerySprite.bounds: Rect get() = bakedArt(art).bounds

/**
 * Draws [art] around the current origin in its own units. Strokes use the widths of the design
 * system, in the same units, so they grow and shrink with whatever scale the caller applied.
 */
internal fun DrawScope.drawSceneryArt(art: SceneryArt, colors: FaColors) = drawBaked(bakedArt(art).nodes, colors)

/**
 * Draws a scenery sprite without its shadow (the shadow has its own layer): flat fills and the
 * strokes of the sprite, all from the theme. The sprite is scaled evenly, strokes included, so
 * that the bounds of its own shapes fit in [widthPx] by [lengthPx]. At the sprite's natural size
 * (the footprint in the level data) the scale is one world unit to one world unit, so lines have
 * their token width.
 *
 * @param center where the middle of the sprite's bounds goes, in pixels
 * @param widthPx room across the sprite, in pixels
 * @param lengthPx room along the sprite, front to back, in pixels
 * @param rotationDegrees where its front points, clockwise from straight up; ignored for a
 *   sprite that stands (`rotates` is false: buildings, trees, the silo, the windsock)
 */
internal fun DrawScope.drawScenerySprite(
    sprite: ScenerySprite,
    center: Offset,
    widthPx: Float,
    lengthPx: Float,
    rotationDegrees: Float,
    colors: FaColors,
) {
    val bounds = sprite.bounds
    if (bounds.width <= 0f || bounds.height <= 0f) return
    val scale = min(widthPx / bounds.width, lengthPx / bounds.height)
    if (scale <= 0f) return
    withTransform({
        translate(center.x, center.y)
        if (sprite.rotates) rotate(rotationDegrees, Offset.Zero)
        scale(scale, scale, Offset.Zero)
        translate(-bounds.center.x, -bounds.center.y)
    }) {
        drawBaked(bakedArt(sprite.art).nodes, colors)
    }
}

/**
 * Draws [sprite] piece after piece along a strip: a hedge, a fence, a forest edge. The pieces
 * are spread evenly over the length, so the row starts and ends on a whole piece.
 *
 * @param center middle of the strip, in pixels
 * @param widthPx width of the strip; one piece is this wide, less twice the [stagger]
 * @param lengthPx length of the strip along [rotationDegrees]
 * @param overlap how far a piece overlaps the one before it, as a fraction of its length
 * @param stagger how far pieces step aside, to the left and to the right in turn, as a fraction
 *   of the strip's width
 */
internal fun DrawScope.drawSceneryRow(
    sprite: ScenerySprite,
    center: Offset,
    widthPx: Float,
    lengthPx: Float,
    rotationDegrees: Float,
    colors: FaColors,
    overlap: Float = 0f,
    stagger: Float = 0f,
) {
    val bounds = sprite.bounds
    if (bounds.width <= 0f) return
    val pieceWidth = widthPx * (1f - 2f * stagger)
    val pieceLength = pieceWidth * bounds.height / bounds.width
    val pitch = pieceLength * (1f - overlap)
    if (pitch <= 0f) return
    val count = max(1, ((lengthPx - pieceLength) / pitch).roundToInt() + 1)
    val step = if (count > 1) (lengthPx - pieceLength) / (count - 1) else 0f
    val first = -(count - 1) * step / 2f
    withTransform({
        translate(center.x, center.y)
        rotate(rotationDegrees, Offset.Zero)
    }) {
        // From the far end towards the front, so each piece lies over the one behind it.
        for (i in count - 1 downTo 0) {
            val aside = if (stagger == 0f) 0f else widthPx * stagger * (if (i % 2 == 0) -1f else 1f)
            drawScenerySprite(sprite, Offset(aside, first + i * step), pieceWidth, pieceLength, 0f, colors)
        }
    }
}
