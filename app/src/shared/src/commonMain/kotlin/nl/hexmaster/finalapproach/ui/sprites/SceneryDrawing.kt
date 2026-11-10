package nl.hexmaster.finalapproach.ui.sprites

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.hypot
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaOpacity

/** A cloud is drawn on a 132 by 76 grid, as in the design system. */
private const val CLOUD_UNITS_WIDTH = 132f
private const val CLOUD_UNITS_HEIGHT = 76f

/** Half the 8-unit outline: outlined lobes are drawn as larger dark lobes behind the white ones. */
private const val CLOUD_EDGE_UNITS = 4f
private const val CLOUD_SHADE_STROKE_UNITS = 5f

/** Centre x, centre y and radius of each lobe. */
private val cloudLobes = listOf(
    Triple(30f, 46f, 18f),
    Triple(54f, 32f, 24f),
    Triple(84f, 38f, 20f),
    Triple(104f, 50f, 14f),
    Triple(66f, 52f, 18f),
    Triple(46f, 52f, 14f),
    Triple(88f, 53f, 13f),
)

private val cloudShade: Path by lazy { parsePath("M 22 58 q 44 12 90 0") }

/**
 * Draws a cloud with its ground shadow.
 *
 * @param center middle of the cloud in pixels
 * @param widthPx drawn width in pixels
 * @param shadowOffset where its ground shadow falls, in pixels
 */
internal fun DrawScope.drawCloud(center: Offset, widthPx: Float, colors: FaColors, shadowOffset: Offset) {
    val scale = widthPx / CLOUD_UNITS_WIDTH
    val topLeft = Offset(center.x - widthPx / 2f, center.y - CLOUD_UNITS_HEIGHT * scale / 2f)
    val shadow = colors.shadowGround
    alphaLayer(shadow.alpha) {
        withTransform({
            translate(topLeft.x + shadowOffset.x, topLeft.y + shadowOffset.y)
            scale(scale, scale, Offset.Zero)
        }) {
            for ((x, y, r) in cloudLobes) drawCircle(shadow.copy(alpha = 1f), r + CLOUD_EDGE_UNITS, Offset(x, y))
        }
        // The cloud is slightly see-through, so cut its own outline out of the shadow:
        // otherwise the shadow would show as a grey patch inside the cloud.
        withTransform({
            translate(topLeft.x, topLeft.y)
            scale(scale, scale, Offset.Zero)
        }) {
            for ((x, y, r) in cloudLobes) {
                drawCircle(Color.Black, r + CLOUD_EDGE_UNITS, Offset(x, y), blendMode = BlendMode.Clear)
            }
        }
    }
    alphaLayer(FaOpacity.cloud) {
        withTransform({
            translate(topLeft.x, topLeft.y)
            scale(scale, scale, Offset.Zero)
        }) {
            for ((x, y, r) in cloudLobes) drawCircle(colors.outline, r + CLOUD_EDGE_UNITS, Offset(x, y))
            for ((x, y, r) in cloudLobes) drawCircle(colors.cloud, r, Offset(x, y))
            drawPath(cloudShade, colors.cloudShade, style = Stroke(CLOUD_SHADE_STROKE_UNITS, cap = StrokeCap.Round))
        }
    }
}

/**
 * Fills the draw area with the sky sunburst: rays of `sky-300` on `bg-sky`, fanning out from
 * [center] and turned by [rotationDegrees]. Each ray and each gap covers [rayDegrees].
 */
internal fun DrawScope.drawSunburst(center: Offset, rotationDegrees: Float, rayDegrees: Float, colors: FaColors) {
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

/** Fills the draw area with the mown lawn: `grass-500` and `grass-700` stripes, tilted 12 degrees. */
internal fun DrawScope.drawLawn(stripeWidthPx: Float, colors: FaColors) {
    drawRect(colors.grass500)
    val reach = hypot(size.width, size.height)
    withTransform({ rotate(LAWN_TILT_DEGREES, Offset(size.width / 2f, size.height / 2f)) }) {
        var x = size.width / 2f - reach
        while (x < size.width / 2f + reach) {
            drawRect(
                colors.grass700,
                topLeft = Offset(x + stripeWidthPx, size.height / 2f - reach),
                size = Size(stripeWidthPx, reach * 2f),
            )
            x += stripeWidthPx * 2f
        }
    }
}

private const val LAWN_TILT_DEGREES = 12f
