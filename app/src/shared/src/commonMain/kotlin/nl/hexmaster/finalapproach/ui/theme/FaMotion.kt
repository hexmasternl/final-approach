// Generated from the design system (docs/design-system/tokens.json). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.animation.core.CubicBezierEasing

/** Durations in milliseconds and easings. */
object FaMotion {
    const val durPressMs = 90
    const val durQuickMs = 160
    const val durPopMs = 280
    const val durLandMs = 420
    const val durBurstMs = 600
    const val durDialogMs = 360
    const val durRotorMs = 240
    const val durMarchMs = 1800
    const val durSwayMs = 2400
    const val durDriftMs = 48000
    const val durRingSpinMs = 4000
    const val durLightMinMs = 500
    const val durBeaconMs = 1600
    const val durRunningLightMs = 1200

    val easeBounce = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
    val easeSnap = CubicBezierEasing(0.2f, 0.9f, 0.3f, 1f)
    val easeOutSoft = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
    val easeSway = CubicBezierEasing(0.45f, 0f, 0.55f, 1f)
}

/** Opacity tokens. */
object FaOpacity {
    const val disabled = 0.45f
    const val cloud = 0.85f
    const val pathIdle = 0.7f
    const val ghost = 0.5f
    const val descentScaleEnd = 0.6f
}
