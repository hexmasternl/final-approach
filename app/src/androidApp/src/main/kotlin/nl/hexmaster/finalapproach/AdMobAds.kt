package nl.hexmaster.finalapproach

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import nl.hexmaster.final_approach.BuildConfig
import nl.hexmaster.finalapproach.platform.Ads

private const val TAG = "AdMobAds"

/**
 * AdMob behind the shared [Ads] interface. Nothing is requested until the User Messaging
 * Platform consent flow has resolved and allows ad requests; when it fails, there are no ads
 * in this session and the game plays on.
 *
 * Every player is treated as a child (Google Play Families policy): all requests are tagged
 * child-directed and under the age of consent, capped at rating G, and never personalised. The
 * tags are set once for the whole SDK, so a new ad format cannot miss them.
 */
class AdMobAds(private val activity: ComponentActivity) : Ads {
    // Read by the banner composable, so it appears as soon as consent allows ads.
    private var canRequestAds by mutableStateOf(false)
    private var interstitial: InterstitialAd? = null
    private var loading = false

    init {
        gatherConsent()
    }

    private fun gatherConsent() {
        val consent = UserMessagingPlatform.getConsentInformation(activity)
        consent.requestConsentInfoUpdate(
            activity,
            ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(true).build(),
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    when {
                        formError != null -> Log.w(TAG, "Consent form failed, no ads: ${formError.message}")
                        consent.canRequestAds() -> startSdk()
                        else -> Log.i(TAG, "Consent does not allow ad requests")
                    }
                }
            },
            { error -> Log.w(TAG, "Consent info update failed, no ads: ${error.message}") },
        )
    }

    private fun startSdk() {
        MobileAds.setRequestConfiguration(childDirectedConfiguration())
        Log.i(TAG, "Child-directed request configuration applied")
        MobileAds.initialize(activity) {
            canRequestAds = true
            preload()
        }
    }

    override fun preload() {
        if (!canRequestAds || loading || interstitial != null) return
        loading = true
        InterstitialAd.load(
            activity,
            BuildConfig.INTERSTITIAL_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    loading = false
                    interstitial = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    loading = false
                    Log.i(TAG, "Interstitial failed to load: ${error.message}")
                }
            },
        )
    }

    override fun showInterstitial(onDismissed: () -> Unit) {
        val ad = interstitial
        if (!canRequestAds || ad == null) {
            onDismissed()
            return
        }
        interstitial = null
        var done = false
        // Every outcome ends here, once, so the level can never be left waiting.
        fun finish() {
            if (done) return
            done = true
            onDismissed()
            preload()
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() = finish()

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.i(TAG, "Interstitial failed to show: ${error.message}")
                finish()
            }
        }
        ad.show(activity)
    }

    @Composable
    override fun Banner(modifier: Modifier) {
        if (!canRequestAds) return
        BoxWithConstraints(modifier.fillMaxWidth()) {
            val widthDp = maxWidth.value.toInt()
            AndroidView(
                factory = { context ->
                    AdView(context).apply {
                        adUnitId = BuildConfig.BANNER_UNIT_ID
                        setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp))
                        loadAd(AdRequest.Builder().build())
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                onRelease = { it.destroy() },
            )
        }
    }
}

/** The request configuration of the whole SDK: child-directed, under the age of consent, rated G at most. */
internal fun childDirectedConfiguration(): RequestConfiguration = RequestConfiguration.Builder()
    .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
    .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
    .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
    .build()
