# Design

## Context

No code is scaffolded yet; this change assumes the scaffold (roadmap milestone 1) and a game-over screen with a "play again" action (milestone 6). The architecture in `CLAUDE.md` applies: `commonMain` uses only `kotlin.*` and `kotlinx.*`, platform concerns sit behind an interface implemented in the host app, and the engine knows nothing about UI.

Ads bring two SDKs (Google Mobile Ads and the User Messaging Platform for consent), both Android-only here, and obligations outside the code: store declarations, a consent message and a privacy policy.

## Goals / Non-Goals

**Goals:**

- Shared code that decides when an ad is due, with no knowledge of AdMob.
- An Android implementation that can be swapped for a no-op in tests and on iOS.
- The frequency cap as pure, unit-tested logic.
- No path on which the player waits for an ad.

**Non-Goals:**

- Mediation, banner or rewarded formats.
- An iOS implementation.
- Tracking ad revenue or analytics.

## Decisions

**Ads sit behind the interface the issue specifies.**

```kotlin
interface InterstitialAds {
    fun preload()
    fun showIfReady(onDismissed: () -> Unit)
}
```

It lives in `commonMain`. `androidApp` implements it with the AdMob interstitial API and its full-screen callback; `NoOpInterstitialAds` calls `onDismissed` straight away and is used in tests and on iOS. `showIfReady` always ends by calling `onDismissed` exactly once, whether an ad was shown, failed to show, or was not ready, so the caller has one path to the next game. Rejected: calling the SDK from the UI layer directly, which would pull Android types into shared code.

**The cap is a separate pure class in `commonMain`.** `AdFrequencyCap` counts finished games and answers whether an ad is due. `onGameFinished()` increments; `isAdDue()` is true from the third game; `onAdShown()` resets. If an ad was due but not shown, the count is not reset, so the ad stays due. The threshold (3) is a constructor value from config. It is tested in `commonTest`. Rejected: capping inside the Android implementation, which could not be unit tested in shared code and would have to be rewritten for iOS.

**The UI layer triggers the ad, not the engine.** On "play again" the game-over screen asks the cap; if an ad is due it calls `showIfReady { startNewGame() }` and reports `onAdShown()` only when an ad was actually displayed; otherwise it calls `startNewGame()` directly. The engine and `GameState` are unchanged.

**Preloading happens during gameplay.** `preload()` is called when a game starts if no ad is loaded and consent allows it, and again after each dismissal. Loading never happens on the game-over tap, which is what guarantees "no ad, no delay".

**Consent gates everything.** On app start the Android implementation runs the UMP consent flow. The Mobile Ads SDK is initialised only after that flow resolves and reports that ads can be requested. Until then, and if the flow fails, `preload()` does nothing and `showIfReady` calls `onDismissed` immediately. The consent flow runs alongside the splash and main pages and never blocks them.

**Ad unit IDs come from the build type.** Debug builds use Google's public test interstitial unit; the real App ID and ad unit ID are supplied to release builds through build configuration and are not committed as literals in shared code. The App ID is declared in the Android manifest, as the SDK requires at launch.

**Dependencies are Android-only.** `play-services-ads` and `user-messaging-platform` are added to the Android source set through `gradle/libs.versions.toml`. Nothing is added to `commonMain`.

**Audience assumption: 13 and over.** The implementation assumes the Play target age group excludes under-13s, so the Families policy and child-directed tagging do not apply. This is the owner's decision; if it changes, child-directed request configuration must be added before release.

## Risks / Trade-offs

- [An ad that fails to dismiss cleanly could strand the player] → `showIfReady` guarantees a single `onDismissed` on every outcome, including show failure.
- [Consent or SDK initialisation slows app start] → Both run after the first screen is shown and never block it.
- [Real ad IDs leak into debug builds, risking invalid traffic and account suspension] → IDs are chosen by build type; a task verifies debug builds request only the test unit.
- [Store policy rejection] → The manual setup tasks (Data safety, "Contains ads", privacy policy, `app-ads.txt`) are part of this change and must be done before a release with real ads.
- [The target-age assumption is wrong] → It is recorded here and in the issue for the owner to confirm before release.

## Migration Plan

Ship behind the build type: debug builds exercise the whole flow with test ads. Real ads start with the first release build that carries the real IDs, after the manual setup is complete. Rollback is replacing the Android implementation with `NoOpInterstitialAds`.

## Open Questions

- Whether to add a time-based cap (at most one ad per so many minutes) on top of the every-third-game rule. It can be added inside `AdFrequencyCap` later without changing callers.
