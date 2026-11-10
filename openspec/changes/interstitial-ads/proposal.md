# Proposal

GitHub issue: #1

## Why

The game needs a source of income that does not interrupt play. A full-screen ad in the natural pause between two games earns revenue without touching gameplay, provided it is capped and never shown at a moment the player did not expect.

## What Changes

- Show an interstitial ad (Google AdMob) between games: on the step from the game-over screen to the next game.
- Cap the frequency: an ad is offered after every third finished game, not after every round.
- Never delay the player: if no ad is ready, the next game starts immediately.
- Collect consent before any ad is requested, and request ads only when consent allows it.
- Request only Google's test ads in debug builds.
- Keep ads behind an interface in shared code, with the AdMob implementation in the Android app only, so iOS can be added later.

Out of scope: a "remove ads" in-app purchase, an iOS ad implementation, banner or rewarded ads.

Depends on: the project scaffold (roadmap milestone 1) and the game-over flow (roadmap milestone 6). Releasing with real ads also depends on the manual account and store setup listed in the tasks.

## Capabilities

### New Capabilities

- `interstitial-ads`: when full-screen ads may be shown between games, how often, what happens when none is available, and the consent and test-ad rules that guard every ad request.

### Modified Capabilities

None.

## Impact

- `shared/` (`commonMain`): an ads interface, a no-op implementation, and the frequency-cap logic with unit tests. No ads SDK or Android reference in shared code.
- `androidApp`: the AdMob and consent (UMP) SDK dependencies through `gradle/libs.versions.toml`, the AdMob App ID in the manifest, and the Android implementation of the interface.
- UI layer: the game-over "play again" action goes through the ads interface before starting the next game.
- Build configuration: test ad unit in debug builds, real IDs in release builds only.
- Outside the code: AdMob account and ad unit, consent message, Play Console declarations, `app-ads.txt`, and a privacy policy. These are manual and need the owner.
- Privacy: the app starts using the advertising ID and device data for ads.
