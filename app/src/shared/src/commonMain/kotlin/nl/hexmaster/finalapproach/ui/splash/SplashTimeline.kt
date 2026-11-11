package nl.hexmaster.finalapproach.ui.splash

import nl.hexmaster.finalapproach.ui.theme.FaMotion

/** Where the splash animation is. */
enum class SplashPhase {
    /** The airplane is pulling the banner across the screen. */
    Towing,

    /** The banner has let go and is coming to rest; the airplane flies on. */
    Released,

    /** The banner is at rest at the centre, waving. */
    Settled,
}

/**
 * The splash scene at one moment. Horizontal positions are shares of the screen width, measured
 * to the middle of the thing.
 */
data class SplashFrame(
    val phase: SplashPhase,
    /** Middle of the airplane, or null when there is no airplane to draw. */
    val airplaneX: Float?,
    /** Middle of the banner. */
    val bannerX: Float,
    /** True while the tow line still joins the airplane and the banner. */
    val towAttached: Boolean,
    /** How far the banner has dipped after letting go: 0 at rest, about 1 at the deepest point. */
    val bannerDip: Float,
    /** Position within the wave, in whole waves. It only ever grows, so the wave never jumps. */
    val wavePhase: Float,
    /** Strength of the wave: 0 is a flat banner, 1 the full wave. */
    val waveStrength: Float,
    /** True once the page should hand over to the next screen. */
    val isFinished: Boolean,
)

/**
 * The whole splash animation as a function of time. It has no state: the same time always gives
 * the same frame.
 *
 * With [reduceMotion] the scene is settled from the first moment: the banner is centred and
 * still, and there is no airplane. The hand-over still happens.
 */
class SplashTimeline(
    private val config: SplashConfig,
    private val geometry: SplashGeometry,
    private val reduceMotion: Boolean = false,
) {
    /** Distance from the middle of the airplane back to the middle of the towed banner. */
    private val towOffset = geometry.airplane / 2f + geometry.towLine + geometry.bannerWidth / 2f

    /** The airplane starts with its nose at the left edge, so nothing is in view yet. */
    private val airplaneStartX = -geometry.airplane / 2f

    /** The moment the middle of the banner reaches the middle of the screen. */
    val releaseSeconds: Float =
        if (reduceMotion) 0f else (CENTRE + towOffset - airplaneStartX) / config.airplaneSpeed

    /** The moment the banner has come to rest. */
    val settledSeconds: Float = if (reduceMotion) 0f else releaseSeconds + config.settleSeconds

    /** The moment the page hands over. */
    val finishSeconds: Float = settledSeconds + config.handOverSeconds

    /** The moment the airplane and its loose tow line are completely off the right edge. */
    val airplaneGoneSeconds: Float =
        if (reduceMotion) 0f
        else (1f + geometry.airplane / 2f + geometry.towLine - airplaneStartX) / config.airplaneSpeed

    fun frameAt(elapsedSeconds: Float): SplashFrame {
        val time = elapsedSeconds.coerceAtLeast(0f)
        if (reduceMotion) {
            return SplashFrame(
                phase = SplashPhase.Settled,
                airplaneX = null,
                bannerX = CENTRE,
                towAttached = false,
                bannerDip = 0f,
                wavePhase = 0f,
                waveStrength = 0f,
                isFinished = time >= finishSeconds,
            )
        }

        val airplaneX = airplaneStartX + config.airplaneSpeed * time
        val towing = time < releaseSeconds
        val sinceRelease = time - releaseSeconds
        val settling = if (time >= settledSeconds) 1f else (sinceRelease / config.settleSeconds).coerceIn(0f, 1f)

        return SplashFrame(
            phase = when {
                towing -> SplashPhase.Towing
                time < settledSeconds -> SplashPhase.Released
                else -> SplashPhase.Settled
            },
            airplaneX = airplaneX.takeIf { time < airplaneGoneSeconds },
            // Once it has let go, the banner never leaves the centre again.
            bannerX = if (towing) airplaneX - towOffset else CENTRE,
            towAttached = towing,
            bannerDip = if (towing || time >= settledSeconds) 0f else dip(settling),
            wavePhase = if (towing) {
                time / config.towedWavePeriodSeconds
            } else {
                releaseSeconds / config.towedWavePeriodSeconds + sinceRelease / config.wavePeriodSeconds
            },
            waveStrength = if (towing) {
                config.towedWaveStrength
            } else {
                config.towedWaveStrength + (1f - config.towedWaveStrength) * settling
            },
            isFinished = time >= finishSeconds,
        )
    }

    /**
     * One dip, shaped by ease-bounce: the banner goes down, comes back a little past rest as the
     * easing overshoots, and is still.
     */
    private fun dip(progress: Float): Float {
        val eased = FaMotion.easeBounce.transform(progress)
        return 4f * eased * (1f - eased)
    }

    private companion object {
        const val CENTRE = 0.5f
    }
}
