package nl.hexmaster.finalapproach.ui.sprites

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.hypot
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaOpacity

/**
 * The cloud's silhouette: the first shape of the generated cloud, a lobed outline in one piece.
 * Its shadow and the cut-out of that shadow are made from it.
 */
private val cloudSilhouette: Path by lazy { parsePath((sceneryCloud.nodes.first() as SceneryShape).pathData) }

/** The width of the outline the cloud is drawn with, in the cloud's own units: the last stroke of its art. */
private val cloudOutlineUnits: Float by lazy {
    sceneryCloud.nodes.filterIsInstance<SceneryShape>().last { it.stroke == SceneryPaint.Outline }.strokeWidth
}

/**
 * Draws a cloud with its ground shadow. The cloud is the one of the design system (generated from
 * `cloudMarkup`): a silhouette in three tones with its outline. Its shadow is the same silhouette,
 * offset, in `shadow-ground`: a cloud is airborne, so it keeps a soft shadow, not a hard one.
 *
 * @param center middle of the cloud in pixels
 * @param widthPx drawn width of the cloud's silhouette in pixels
 * @param shadowOffset where its ground shadow falls, in pixels
 */
internal fun DrawScope.drawCloud(center: Offset, widthPx: Float, colors: FaColors, shadowOffset: Offset) {
    // The silhouette's own bounds: the shade and the highlight lie inside it, behind a clip.
    val bounds = cloudSilhouette.getBounds()
    val scale = widthPx / bounds.width
    fun withCloud(at: Offset, block: DrawScope.() -> Unit) = withTransform({
        translate(at.x, at.y)
        scale(scale, scale, Offset.Zero)
        translate(-bounds.center.x, -bounds.center.y)
    }) { block() }
    // The silhouette and half its outline, as one shape.
    fun DrawScope.body(color: androidx.compose.ui.graphics.Color, mode: BlendMode = BlendMode.SrcOver) {
        drawPath(cloudSilhouette, color, blendMode = mode)
        drawPath(cloudSilhouette, color, style = Stroke(cloudOutlineUnits, cap = StrokeCap.Round, join = StrokeJoin.Round), blendMode = mode)
    }
    val shadow = colors.shadowGround
    alphaLayer(shadow.alpha) {
        withCloud(center + shadowOffset) { body(shadow.copy(alpha = 1f)) }
        // The cloud is slightly see-through, so cut its own shape out of the shadow:
        // otherwise the shadow would show as a grey patch inside the cloud.
        // The colour does not matter: Clear erases whatever it covers.
        withCloud(center) { body(colors.outline, BlendMode.Clear) }
    }
    alphaLayer(FaOpacity.cloud) {
        withCloud(center) { drawSceneryArt(sceneryCloud, colors) }
    }
}

/** Angle covered by one ray of the sky sunburst, and by each gap between rays. */
internal const val SKY_RAY_DEGREES = 9f

/**
 * Fills the draw area with the sky sunburst: rays of `sky-300` on `bg-sky`, fanning out from
 * [center] and turned by [rotationDegrees]. Each ray and each gap covers [rayDegrees].
 */
internal fun DrawScope.drawSunburst(
    center: Offset,
    rotationDegrees: Float,
    colors: FaColors,
    rayDegrees: Float = SKY_RAY_DEGREES,
) {
    drawRect(colors.bgSky)
    // Far enough to cover every corner from any centre inside the draw area.
    val reach = hypot(size.width, size.height)
    val topLeft = Offset(center.x - reach, center.y - reach)
    val box = Size(reach * 2f, reach * 2f)
    var start = rotationDegrees
    val end = rotationDegrees + 360f
    while (start < end - 0.001f) {
        drawArc(colors.sky300, startAngle = start, sweepAngle = rayDegrees, useCenter = true, topLeft = topLeft, size = box)
        start += rayDegrees * 2f
    }
}
