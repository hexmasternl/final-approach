# Design

## Context

The game screen, level select and the game ended dialog exist. The architecture applies: `commonMain` uses only `kotlin.*` and `kotlinx.*` plus Compose, platform concerns sit behind `AppHost`, and the engine knows nothing about UI or ads.

Ads bring two SDKs (Google Mobile Ads and the User Messaging Platform for consent), both Android-only here, and obligations outside the code: store declarations, a consent message and a privacy policy.

## Goals / Non-Goals

**Goals:**

- An ad before every level start, with AdMob in control of how long it shows.
- Shared code with no knowledge of AdMob; an Android implementation that a no-op can replace in tests and on iOS.
- No path on which the player waits for an ad.

**Non-Goals:**

- An app-enforced 10-second lock. A standard AdMob interstitial cannot be locked by the app, and a custom ad screen would risk AdMob's forced-ad policies. The owner chose the standard interstitial (issue #13, answer A).
- A frequency cap, mediation, rewarded formats, an iOS implementation.

## Decisions

**Ads sit behind an interface on `AppHost`.**

```kotlin
interface Ads {
    fun preload()
    fun showInterstitial(onDismissed: () -> Unit)
    @Composable fun Banner(modifier: Modifier)
}
```

`AppHost.ads` returns it. `NoOpAds` (the default for tests and iOS) calls `onDismissed` straight away and draws no banner. `showInterstitial` always ends by calling `onDismissed` exactly once, whether an ad was shown, failed to show, or was not ready, so the caller has one path to the level. Rejected: calling the SDK from the UI layer directly, which would pull Android types into shared code.

**The game screen holds the level until the ad is gone.** When a level starts (the game screen enters, or a restart is requested) the screen asks `ads.showInterstitial { ... }`. Until `onDismissed` the engine stays paused and the field is visible but frozen; then the level begins. A restart waits for `onDismissed` before calling `engine.restart()`. The engine is unchanged. No ad, no wait: an unready ad calls `onDismissed` at once.

**Restart counts as a level start.** The AdMob unit is named "before-level-starts" and the issue asked for an ad before every level; "Try again" and the options-panel restart therefore show an ad too. This is an assumption the owner can overrule; the change is one call site.

**Preloading.** `preload()` is called when the game screen is entered, and the Android implementation preloads again after each dismissal, so the next ad is ready by the time the next level starts. Loading never happens on the tap that starts a level.

**Consent gates everything.** On app start the Android implementation runs the UMP consent flow. The Mobile Ads SDK is initialised only after that flow resolves and reports that ads can be requested. Until then, and if the flow fails, `preload()` does nothing, `showInterstitial` calls `onDismissed` immediately and the banner is empty. The consent flow runs alongside the splash and main pages and never blocks them.

**IDs come from the build type.** Debug builds use Google's public test App ID and test units (interstitial and banner). Release builds use the real IDs through `manifestPlaceholders` and `BuildConfig` fields, never as literals in shared code:

- App ID `ca-app-pub-1310013076234295~7766080345`
- Interstitial "before-level-starts" `ca-app-pub-1310013076234295/6684168624`
- Banner "after-level-finished" `ca-app-pub-1310013076234295/3427449894` (used by `game-ended-dialog`)

These are identifiers, not secrets; they ship in every build.

**The manifest declares the advertising ID permission.** `com.google.android.gms.permission.AD_ID` is declared with `<uses-permission>`. Apps targeting Android 13 (API 33) or higher need it to read the advertising ID; without it the ID is zeroed, ad revenue drops and Play Console flags the declaration. The App ID goes in manifest meta-data `com.google.android.gms.ads.APPLICATION_ID`, as the SDK requires at launch.

**Dependencies are Android-only.** `play-services-ads` and `user-messaging-platform` are added to the Android source set through `gradle/libs.versions.toml`. Nothing is added to `commonMain` except the interface.

**Audience assumption: 13 and over.** The Families policy and child-directed tagging do not apply. The owner must confirm the Play target age group before release; if it includes under-13s, child-directed request configuration must be added first.

## Risks / Trade-offs

- [An ad that fails to dismiss cleanly could strand the player] → `showInterstitial` guarantees a single `onDismissed` on every outcome, including show failure.
- [An ad before every level, restarts included, may annoy players] → Easy to relax later by gating the call; AdMob's own pacing still applies.
- [Consent or SDK initialisation slows app start] → Both run after the first screen is shown and never block it.
- [Real ad IDs leak into debug builds, risking invalid traffic and account suspension] → IDs are chosen by build type; a task verifies debug builds request only test units.
- [Store policy rejection] → The manual setup tasks (Data safety, "Contains ads", privacy policy, `app-ads.txt`) are part of this change and must be done before a release with real ads.

## Migration Plan

Ship behind the build type: debug builds exercise the whole flow with test ads. Real ads start with the first release build that carries the real IDs, after the manual setup is complete. Rollback is returning `NoOpAds` from the host.

## Open Questions

- Whether a restart should show an ad (assumed yes, see above).
