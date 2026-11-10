package nl.hexmaster.finalapproach.ui.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme
import nl.hexmaster.finalapproach.ui.theme.Sunset

private object SplashText {
    const val NAME = "Final Approach"
    const val SKIP = "Skip"
}

private object SplashPageDefaults {
    /** Longest step the animation takes in one frame, so a stalled frame does not make it jump. */
    const val MAX_FRAME_SECONDS = 0.1f

    /** From the edge of the banner to the name: its outline, then space-5 of clear room. */
    val bannerPaddingX = FaDimens.line3 + FaDimens.space5
}

/**
 * The first screen of the app. An airplane tows a banner with the name of the game across a
 * sunset sky; the banner lets go at the centre and stays, waving, while the airplane flies on.
 *
 * [onFinished] is called once: 2 seconds after the banner has settled, or at once when the
 * player taps the screen.
 */
@Composable
fun SplashPage(
    host: AppHost,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    config: SplashConfig = remember { SplashConfig() },
) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    var screen by remember { mutableStateOf(IntSize.Zero) }
    var elapsed by remember { mutableFloatStateOf(0f) }

    // Hand over exactly once, whichever comes first: the timer or a tap.
    val currentOnFinished by rememberUpdatedState(onFinished)
    var handedOver by remember { mutableStateOf(false) }
    val handOver = {
        if (!handedOver) {
            handedOver = true
            currentOnFinished()
        }
    }

    LaunchedEffect(Unit) {
        var last = withFrameNanos { it }
        while (true) {
            val now = withFrameNanos { it }
            elapsed += ((now - last) / 1_000_000_000f).coerceAtMost(SplashPageDefaults.MAX_FRAME_SECONDS)
            last = now
        }
    }

    val screenWidthDp = screen.width / density.density
    val geometry = remember(config, screenWidthDp) { config.geometry(screenWidthDp.coerceAtLeast(1f)) }
    val timeline = remember(config, geometry, host.reduceMotion) {
        SplashTimeline(config, geometry, host.reduceMotion)
    }
    val frame = timeline.frameAt(elapsed)
    if (frame.isFinished && screen != IntSize.Zero) {
        LaunchedEffect(Unit) { handOver() }
    }

    // The banner is rendered once per screen size and theme, then painted in slices every frame.
    val bannerWidthPx = (geometry.bannerWidth * screen.width).roundToInt()
    val bannerHeightPx = (bannerWidthPx / config.bannerAspect).roundToInt()
    val banner = remember(bannerWidthPx, bannerHeightPx, colors, density) {
        if (bannerWidthPx <= 0 || bannerHeightPx <= 0) {
            null
        } else {
            val roomForName = bannerWidthPx - 2f * with(density) { SplashPageDefaults.bannerPaddingX.toPx() }
            val name = fitName(roomForName) { fontScale ->
                val style = type.displayMd.copy(color = colors.ink, fontSize = type.displayMd.fontSize * fontScale)
                textMeasurer.measure(SplashText.NAME, style, maxLines = 1)
            }
            renderBanner(bannerWidthPx, bannerHeightPx, name, colors, density)
        }
    }

    Canvas(
        modifier
            .fillMaxSize()
            .onSizeChanged { screen = it }
            .semantics {
                contentDescription = SplashText.NAME
                // The same as a tap, for screen readers, keyboards and switch access.
                onClick(label = SplashText.SKIP) {
                    handOver()
                    true
                }
            }
            .pointerInput(Unit) { detectTapGestures { handOver() } },
    ) {
        drawSunsetSky(Sunset)
        drawAirport(Sunset)
        if (banner == null) return@Canvas

        val flightY = size.height * config.flightHeight
        val amplitude = config.waveAmplitude * density.density * frame.waveStrength
        val waveLength = config.waveLength * density.density
        val bannerCenter = Offset(
            x = frame.bannerX * size.width,
            y = flightY + frame.bannerDip * config.releaseDip * density.density,
        )
        val airplaneX = frame.airplaneX
        if (airplaneX != null) {
            val planeLength = geometry.airplane * size.width
            val planeCenter = Offset(airplaneX * size.width, flightY)
            val hook = Offset(planeCenter.x - planeLength * SplashSceneDefaults.TOW_HOOK, planeCenter.y)
            val lineEnd = if (frame.towAttached) {
                // The line is tied to the leading edge of the banner and rides its wave.
                val lift = waveLift(banner.width.toFloat(), frame.wavePhase, amplitude, waveLength)
                Offset(bannerCenter.x + banner.width / 2f, bannerCenter.y + lift)
            } else {
                // Loose: it trails behind the airplane and leaves with it.
                Offset(hook.x - geometry.towLine * size.width, hook.y)
            }
            drawTowLine(lineEnd, hook, colors)
            drawTowPlane(planeCenter, planeLength, elapsed, colors)
        }
        drawWavingBanner(banner, bannerCenter, frame.wavePhase, amplitude, waveLength)
    }
}

/**
 * Measures the name at full size and, when it is wider than [roomPx], once more at the size
 * that fits.
 */
private fun fitName(roomPx: Float, measure: (fontScale: Float) -> TextLayoutResult): TextLayoutResult {
    val full = measure(1f)
    return if (full.size.width <= roomPx || roomPx <= 0f) full else measure(roomPx / full.size.width)
}
