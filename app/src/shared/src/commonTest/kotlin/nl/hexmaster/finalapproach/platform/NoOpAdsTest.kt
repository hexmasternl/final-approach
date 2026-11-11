package nl.hexmaster.finalapproach.platform

import kotlin.test.Test
import kotlin.test.assertEquals

class NoOpAdsTest {
    @Test
    fun showingAnInterstitialDismissesAtOnceExactlyOnce() {
        var dismissed = 0
        NoOpAds.preload()
        NoOpAds.showInterstitial { dismissed++ }
        assertEquals(1, dismissed)
    }
}
