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
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
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
    val scale = spriteScale(sprite, widthPx, lengthPx)
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

/** The scale at which the own shapes of [sprite] fit [widthPx] by [lengthPx]; 0 when it has no size. */
internal fun spriteScale(sprite: ScenerySprite, widthPx: Float, lengthPx: Float): Float =
    fitScale(sprite.bounds.width, sprite.bounds.height, widthPx, lengthPx)

/** The scale at which shapes of [boundsWidth] by [boundsHeight] fit [widthPx] by [lengthPx]; 0 when they have no size. */
internal fun fitScale(boundsWidth: Float, boundsHeight: Float, widthPx: Float, lengthPx: Float): Float {
    if (boundsWidth <= 0f || boundsHeight <= 0f) return 0f
    return min(widthPx / boundsWidth, lengthPx / boundsHeight)
}

/** One piece of a row: where its middle goes, in pixels, and the room it is fitted into. */
internal class RowPiece(val center: Offset, val widthPx: Float, val lengthPx: Float)

/**
 * The pieces of a row of [sprite]: a hedge, a fence, a forest edge. The pieces are spread evenly
 * over the length, so the row starts and ends on a whole piece. They are listed from the far end
 * towards the front, in the order they are drawn, so each piece lies over the one behind it.
 *
 * @param center middle of the strip, in pixels
 * @param widthPx width of the strip; one piece is this wide, less twice the [stagger]
 * @param lengthPx length of the strip along [rotationDegrees]
 * @param overlap how far a piece overlaps the one before it, as a fraction of its length
 * @param stagger how far pieces step aside, to the left and to the right in turn, as a fraction
 *   of the strip's width
 */
internal fun sceneryRowPieces(
    sprite: ScenerySprite,
    center: Offset,
    widthPx: Float,
    lengthPx: Float,
    rotationDegrees: Float,
    overlap: Float = 0f,
    stagger: Float = 0f,
): List<RowPiece> = rowPieces(sprite.bounds.width, sprite.bounds.height, center, widthPx, lengthPx, rotationDegrees, overlap, stagger)

/** [sceneryRowPieces] for a piece whose shapes are [boundsWidth] by [boundsHeight]. */
internal fun rowPieces(
    boundsWidth: Float,
    boundsHeight: Float,
    center: Offset,
    widthPx: Float,
    lengthPx: Float,
    rotationDegrees: Float,
    overlap: Float = 0f,
    stagger: Float = 0f,
): List<RowPiece> {
    if (boundsWidth <= 0f) return emptyList()
    val pieceWidth = widthPx * (1f - 2f * stagger)
    val pieceLength = pieceWidth * boundsHeight / boundsWidth
    val pitch = pieceLength * (1f - overlap)
    if (pitch <= 0f) return emptyList()
    val count = max(1, ((lengthPx - pieceLength) / pitch).roundToInt() + 1)
    val step = if (count > 1) (lengthPx - pieceLength) / (count - 1) else 0f
    val first = -(count - 1) * step / 2f
    val radians = rotationDegrees * PI.toFloat() / HALF_TURN_DEGREES
    val sin = sin(radians)
    val cos = cos(radians)
    return (count - 1 downTo 0).map { i ->
        val aside = if (stagger == 0f) 0f else widthPx * stagger * (if (i % 2 == 0) -1f else 1f)
        val along = first + i * step
        // The strip is turned about its middle; the pieces keep their own facing.
        RowPiece(Offset(center.x + aside * cos - along * sin, center.y + aside * sin + along * cos), pieceWidth, pieceLength)
    }
}

/**
 * Draws [sprite] piece after piece along a strip, as laid out by [sceneryRowPieces]. A piece of a
 * sprite that stands never turns with the strip, only its place does.
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
    for (piece in sceneryRowPieces(sprite, center, widthPx, lengthPx, rotationDegrees, overlap, stagger)) {
        drawScenerySprite(sprite, piece.center, piece.widthPx, piece.lengthPx, rotationDegrees, colors)
    }
}

private class BakedShadow(val path: Path, val strokeWidth: Float)

private val bakedShadows = HashMap<ScenerySprite, List<BakedShadow>>()

private val ScenerySprite.bakedShadow: List<BakedShadow>
    get() = bakedShadows.getOrPut(this) {
        shadow.map { s ->
            val m = s.matrix?.let(::affineOf)
            BakedShadow(transformed(parsePath(s.pathData), m), s.strokeWidth * (m?.lengthScale() ?: 1f))
        }
    }

/** Draws the shadow shapes of [sprite] around the current origin, in its own units, without its throw. */
private fun DrawScope.drawShadowShapes(sprite: ScenerySprite, color: Color) {
    for (s in sprite.bakedShadow) {
        if (s.strokeWidth > 0f) drawPath(s.path, color, style = Stroke(s.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
        else drawPath(s.path, color)
    }
}

/**
 * Draws the cast shadow silhouette of [sprite] in [color], in the sprite's own units around the
 * current origin and moved by the sprite's throw. Every shape is opaque, so the caller puts them
 * in one alpha layer and overlapping shadows do not darken twice. Flat sprites have no shadow.
 */
internal fun DrawScope.drawSceneryShadow(sprite: ScenerySprite, color: Color) {
    if (sprite.shadow.isEmpty()) return
    withTransform({ translate(sprite.shadowX, sprite.shadowY) }) { drawShadowShapes(sprite, color) }
}

/**
 * Where the middle of a shadow goes relative to its sprite, in pixels. The throw of the sprite
 * is scaled with the sprite and applied in screen axes, outside the rotation: the sun is fixed,
 * so a turned car casts its shadow down and to the right, never turned with it (W1). The
 * [rotationDegrees] of the sprite is a parameter to make that explicit: it changes nothing.
 */
@Suppress("UNUSED_PARAMETER")
internal fun shadowOffset(sprite: ScenerySprite, scale: Float, rotationDegrees: Float): Offset =
    Offset(sprite.shadowX * scale, sprite.shadowY * scale)

/**
 * Draws the shadow of [sprite] placed like [drawScenerySprite] places it: the silhouette follows
 * the sprite's rotation and scale, and its middle moves by [shadowOffset] in screen axes. Draw
 * every shadow of a field this way inside one alpha layer, in [color] opaque.
 */
internal fun DrawScope.drawSceneryShadowAt(
    sprite: ScenerySprite,
    center: Offset,
    widthPx: Float,
    lengthPx: Float,
    rotationDegrees: Float,
    color: Color,
) {
    if (sprite.shadow.isEmpty()) return
    val bounds = sprite.bounds
    val scale = spriteScale(sprite, widthPx, lengthPx)
    if (scale <= 0f) return
    val moved = center + shadowOffset(sprite, scale, rotationDegrees)
    withTransform({
        translate(moved.x, moved.y)
        if (sprite.rotates) rotate(rotationDegrees, Offset.Zero)
        scale(scale, scale, Offset.Zero)
        translate(-bounds.center.x, -bounds.center.y)
    }) { drawShadowShapes(sprite, color) }
}

/** The shadows of a row, piece by piece, as [drawSceneryRow] lays them out. */
internal fun DrawScope.drawSceneryRowShadows(
    sprite: ScenerySprite,
    center: Offset,
    widthPx: Float,
    lengthPx: Float,
    rotationDegrees: Float,
    color: Color,
    overlap: Float = 0f,
    stagger: Float = 0f,
) {
    for (piece in sceneryRowPieces(sprite, center, widthPx, lengthPx, rotationDegrees, overlap, stagger)) {
        drawSceneryShadowAt(sprite, piece.center, piece.widthPx, piece.lengthPx, rotationDegrees, color)
    }
}

private const val HALF_TURN_DEGREES = 180f
