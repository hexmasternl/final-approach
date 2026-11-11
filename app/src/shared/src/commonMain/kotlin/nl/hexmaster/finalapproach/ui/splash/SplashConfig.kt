package nl.hexmaster.finalapproach.ui.splash

import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion

/** Every tunable value of the splash page. Lengths are in dp, times in seconds. */
data class SplashConfig(
    /** How fast the airplane crosses the screen, in screen widths per second. */
    val airplaneSpeed: Float = 0.34f,
    /** Length of the side-view airplane, nose to tail. */
    val airplaneLength: Float = FaDimens.spriteLightPropSide.value,
    /** Length of the tow line between the airplane's tail and the banner. */
    val towLineLength: Float = FaDimens.towLine.value,
    /** The banner is this share of the screen width, but never wider than [bannerMaxWidth]. */
    val bannerWidthFraction: Float = 0.62f,
    val bannerMaxWidth: Float = 420f,
    /** The banner's height is its width divided by this. */
    val bannerAspect: Float = 5.6f,
    /** Height at which the airplane and the banner fly, as a share of the screen height from the top. */
    val flightHeight: Float = 0.3f,
    /** Time the banner takes to settle after it lets go. */
    val settleSeconds: Float = FaMotion.durBurstMs / 1000f,
    /** Time between the banner settling and the page handing over. */
    val handOverSeconds: Float = 2f,
    /** How far the banner dips when it lets go. */
    val releaseDip: Float = 10f,
    /** One slow wave of the loose banner. */
    val wavePeriodSeconds: Float = FaMotion.durSwayMs / 1000f,
    /** One quick ripple of the banner while it is towed. */
    val towedWavePeriodSeconds: Float = 0.9f,
    /** Height of the wave, from rest to crest. Small enough that the name stays readable. */
    val waveAmplitude: Float = 5f,
    /** Distance between two wave crests along the banner. */
    val waveLength: Float = 190f,
    /** Strength of the ripple while towed, as a share of the full wave. */
    val towedWaveStrength: Float = 0.6f,
) {
    /** The sizes of the moving parts on a screen [screenWidth] dp wide. */
    fun geometry(screenWidth: Float): SplashGeometry {
        val bannerWidth = minOf(bannerWidthFraction * screenWidth, bannerMaxWidth)
        return SplashGeometry(
            bannerWidth = bannerWidth / screenWidth,
            towLine = towLineLength / screenWidth,
            airplane = airplaneLength / screenWidth,
        )
    }
}

/** Sizes of the moving parts of the splash scene, each as a share of the screen width. */
data class SplashGeometry(
    val bannerWidth: Float,
    val towLine: Float,
    val airplane: Float,
)
