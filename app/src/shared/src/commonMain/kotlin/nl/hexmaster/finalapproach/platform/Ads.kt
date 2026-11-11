package nl.hexmaster.finalapproach.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Ads, as far as shared code is concerned. The host implements it with an ads SDK; the game
 * never learns which one. Every method is safe to call at any time: when ads are not
 * available (no consent, no network, no SDK) they do nothing and never block the player.
 */
interface Ads {
    /** Starts loading the next interstitial, if ads may be requested. Never blocks. */
    fun preload()

    /**
     * Shows an interstitial if one is ready. [onDismissed] is called exactly once: when the ad
     * has been closed, when it failed to show, or at once when none was ready.
     */
    fun showInterstitial(onDismissed: () -> Unit)

    /** A banner that fills the width it is given. Empty when no ad is available. */
    @Composable
    fun Banner(modifier: Modifier)
}

/** The ads of a platform without ads, and of tests: nothing is ever shown. */
object NoOpAds : Ads {
    override fun preload() = Unit

    override fun showInterstitial(onDismissed: () -> Unit) = onDismissed()

    @Composable
    override fun Banner(modifier: Modifier) = Unit
}
