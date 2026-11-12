# Proposal

GitHub issue: #13

## Why

The game needs a source of income. A full-screen ad in the natural pause before a level starts earns revenue without touching gameplay. This change replaces the earlier plan of issue #1 (an ad after every third game): the owner decided the ad is shown before every level start, and that AdMob controls how long it stays up.

## What Changes

- Show an AdMob interstitial ad before a level starts: when the player starts a level from level select, and when a level is restarted ("Try again" on the game ended dialog, "Restart" in the options panel). The level begins when the ad is dismissed.
- AdMob controls the timing and the close button. The app does not enforce a minimum display time and does not lock the ad (owner decision, issue #13: standard AdMob interstitial).
- Never delay the player: if no ad is ready, the level starts immediately.
- Collect consent before any ad is requested, and request ads only when consent allows it.
- Request only Google's test ads in debug builds.
- Keep ads behind an interface on `AppHost`, with the AdMob implementation in the Android app only, so iOS can be added later. The same interface offers the banner that `game-ended-dialog` shows.

Out of scope: a "remove ads" in-app purchase, an iOS ad implementation, rewarded ads, a frequency cap, the banner itself (see `game-ended-dialog`).

Depends on: the scaffold and the game screen, which exist. Releasing with real ads also depends on the manual account and store setup listed in the tasks.

## Capabilities

### New Capabilities

- `level-start-ads`: when an interstitial ad may be shown, what happens when none is available, and the consent and test-ad rules that guard every ad request, banners included.

### Modified Capabilities

None.

## Impact

- `shared/` (`commonMain`): an `Ads` interface on `AppHost`, a no-op implementation, and the game screen holding the level until the ad is dismissed. No ads SDK or Android reference in shared code.
- `androidApp`: the AdMob and consent (UMP) SDK dependencies through `gradle/libs.versions.toml`, the AdMob App ID and the `com.google.android.gms.permission.AD_ID` permission in the manifest, and the Android implementation of the interface.
- Build configuration: test ad unit and App ID in debug builds, real IDs in release builds only.
- Outside the code: AdMob account and ad units, consent message, Play Console declarations, `app-ads.txt`, and a privacy policy. These are manual and need the owner.
- Privacy: the app starts using the advertising ID and device data for ads.
