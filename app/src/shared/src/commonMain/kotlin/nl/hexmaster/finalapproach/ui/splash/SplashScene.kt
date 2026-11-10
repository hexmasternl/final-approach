package nl.hexmaster.finalapproach.ui.splash

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin
import nl.hexmaster.finalapproach.ui.components.drawBlock
import nl.hexmaster.finalapproach.ui.sprites.AirportSilhouette
import nl.hexmaster.finalapproach.ui.sprites.drawSideViewLightProp
import nl.hexmaster.finalapproach.ui.sprites.parsePath
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.SunsetColors

/** Geometry of the splash scene that is not a token or a timing. */
internal object SplashSceneDefaults {
    /** Where each sky band ends, as a share of the screen height from the top. */
    val skyBandEnds = floatArrayOf(0.30f, 0.52f, 0.72f, 1f)

    /** The control tower, tip of the antenna to the ground, on the silhouette grid. */
    const val TOWER_HEIGHT_UNITS = AirportSilhouette.BASE_Y - 10f

    /** The tower is at least this tall and at most that tall, as a share of the screen height. */
    const val MIN_TOWER_SHARE = 0.24f
    const val MAX_TOWER_SHARE = 0.44f

    /** The sun on the silhouette grid: low, and partly behind the terminal. */
    const val SUN_X_UNITS = 400f
    const val SUN_Y_UNITS = 154f
    const val SUN_RADIUS_UNITS = 88f

    /** The tow line hangs this far below a straight line at its middle, as a share of its length. */
    const val TOW_LINE_SAG = 0.18f

    /** Where on the airplane, measured from its middle, the tow line is tied. */
    const val TOW_HOOK = 0.44f

    /** The banner is drawn in vertical slices this wide, each shifted by the wave. */
    const val WAVE_SLICE_DP = 2f
}

private val silhouetteShapes: List<Path> by lazy { AirportSilhouette.shapes.map(::parsePath) }
private val silhouetteLights: List<Path> by lazy { AirportSilhouette.lights.map(::parsePath) }

/** The sunset sky: four flat bands, darkest at the top. No gradient. */
internal fun DrawScope.drawSunsetSky(sunset: SunsetColors) {
    val bands = listOf(sunset.sunsetSkyTop, sunset.sunsetSkyUpper, sunset.sunsetSkyLower, sunset.sunsetSkyHorizon)
    var top = 0f
    bands.forEachIndexed { index, color ->
        val bottom = size.height * SplashSceneDefaults.skyBandEnds[index]
        // One pixel of overlap, so no seam shows between two bands.
        drawRect(color, Offset(0f, top), Size(size.width, bottom - top + 1f))
        top = bottom
    }
}

/**
 * The sun and the airport silhouette along the bottom edge. The silhouette keeps its
 * proportions on every screen: it is scaled as a whole, centred, and the ground runs on to both
 * edges.
 */
internal fun DrawScope.drawAirport(sunset: SunsetColors) {
    val towerUnits = SplashSceneDefaults.TOWER_HEIGHT_UNITS
    val scale = (size.width / AirportSilhouette.WIDTH).coerceIn(
        SplashSceneDefaults.MIN_TOWER_SHARE * size.height / towerUnits,
        SplashSceneDefaults.MAX_TOWER_SHARE * size.height / towerUnits,
    )
    val left = size.width / 2f - AirportSilhouette.WIDTH * scale / 2f
    val top = size.height - AirportSilhouette.HEIGHT * scale
    val groundTop = top + AirportSilhouette.BASE_Y * scale

    drawCircle(
        sunset.sunsetSun,
        radius = SplashSceneDefaults.SUN_RADIUS_UNITS * scale,
        center = Offset(left + SplashSceneDefaults.SUN_X_UNITS * scale, top + SplashSceneDefaults.SUN_Y_UNITS * scale),
    )
    // The ground reaches both edges, whatever the silhouette's own width.
    drawRect(sunset.sunsetSilhouette, Offset(0f, groundTop - 1f), Size(size.width, size.height - groundTop + 1f))
    withTransform({
        translate(left, top)
        scale(scale, scale, Offset.Zero)
    }) {
        for (shape in silhouetteShapes) drawPath(shape, sunset.sunsetSilhouette)
        for (light in silhouetteLights) drawPath(light, sunset.sunsetTowerLight)
    }
}

/**
 * Renders the banner once into a bitmap: an outlined pennant with the name on it. The scene
 * then paints it in slices to make it wave.
 */
internal fun renderBanner(
    widthPx: Int,
    heightPx: Int,
    name: TextLayoutResult,
    colors: FaColors,
    density: Density,
): ImageBitmap {
    val image = ImageBitmap(widthPx, heightPx)
    val size = Size(widthPx.toFloat(), heightPx.toFloat())
    CanvasDrawScope().draw(density, LayoutDirection.Ltr, Canvas(image), size) {
        drawBlock(
            fill = colors.surface,
            outline = colors.outline,
            border = FaDimens.line3.toPx(),
            radius = FaDimens.radiusMd.toPx(),
            lip = 0f,
        )
        drawText(
            name,
            topLeft = Offset((size.width - name.size.width) / 2f, (size.height - name.size.height) / 2f),
        )
    }
    return image
}

/**
 * Paints [banner] with its middle at [center], waving. Each narrow vertical slice is moved up
 * or down by a sine of its place along the banner, so the wave travels along it.
 */
internal fun DrawScope.drawWavingBanner(
    banner: ImageBitmap,
    center: Offset,
    wavePhase: Float,
    amplitudePx: Float,
    waveLengthPx: Float,
) {
    val left = (center.x - banner.width / 2f).roundToInt()
    val top = center.y - banner.height / 2f
    val slice = (SplashSceneDefaults.WAVE_SLICE_DP * density).roundToInt().coerceAtLeast(1)
    var x = 0
    while (x < banner.width) {
        val width = minOf(slice, banner.width - x)
        val lift = amplitudePx * sin(2.0 * PI * (x / waveLengthPx - wavePhase)).toFloat()
        drawImage(
            image = banner,
            srcOffset = IntOffset(x, 0),
            srcSize = IntSize(width, banner.height),
            dstOffset = IntOffset(left + x, (top + lift).roundToInt()),
            dstSize = IntSize(width, banner.height),
        )
        x += width
    }
}

/** How far the wave lifts the banner at [distanceFromLeftPx] along it. */
internal fun waveLift(distanceFromLeftPx: Float, wavePhase: Float, amplitudePx: Float, waveLengthPx: Float): Float =
    amplitudePx * sin(2.0 * PI * (distanceFromLeftPx / waveLengthPx - wavePhase)).toFloat()

/** The tow line between two points, hanging a little in the middle. */
internal fun DrawScope.drawTowLine(from: Offset, to: Offset, colors: FaColors) {
    val sag = (to - from).getDistance() * SplashSceneDefaults.TOW_LINE_SAG
    val line = Path().apply {
        moveTo(from.x, from.y)
        quadraticTo((from.x + to.x) / 2f, (from.y + to.y) / 2f + sag, to.x, to.y)
    }
    drawPath(line, colors.outline, style = Stroke(FaDimens.line2.toPx(), cap = StrokeCap.Round))
}

/** The side-view light prop, nose to the right, in the first flight-path colour. */
internal fun DrawScope.drawTowPlane(center: Offset, lengthPx: Float, timeSeconds: Float, colors: FaColors) {
    drawSideViewLightProp(colors.path1, center, lengthPx, timeSeconds, colors)
}
