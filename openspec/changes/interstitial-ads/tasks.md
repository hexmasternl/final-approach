# Tasks

## 1. Shared abstraction and frequency cap

- [ ] 1.1 Add the `InterstitialAds` interface (`preload()`, `showIfReady(onDismissed)`) and `NoOpInterstitialAds` to `commonMain`, and verify with a unit test that the no-op calls `onDismissed` exactly once
- [ ] 1.2 Implement `AdFrequencyCap` with a configurable threshold (default 3) and verify with unit tests in `commonTest` that no ad is due after one or two games, one is due after the third, the count restarts after `onAdShown()`, and a due ad stays due when none was shown
- [ ] 1.3 Verify that `commonMain` contains no `android.*`, `com.google.android.*` or ads SDK reference (search the source set; the shared tests must pass on the JVM without the Android SDK)
- [ ] 1.4 Document the ads interface and the cap in `docs/architecture.md` and verify the described names match the code

## 2. Android implementation

- [ ] 2.1 Add `play-services-ads` and `user-messaging-platform` to the Android source set through `gradle/libs.versions.toml` and verify `./gradlew :androidApp:assembleDebug` succeeds
- [ ] 2.2 Add the AdMob App ID meta-data to the Android manifest, supplied per build type, and verify the debug app launches without crashing
- [ ] 2.3 Implement the consent flow with UMP on app start and initialise the Mobile Ads SDK only after it resolves and allows ad requests; verify with logging on a device that no ad request is made before consent resolves, and none at all when consent does not allow it or the flow fails
- [ ] 2.4 Implement `InterstitialAds` for Android (load, show, full-screen callback) so that `onDismissed` is called exactly once whether the ad was shown, failed to show, or was not ready; verify each of the three outcomes on a device
- [ ] 2.5 Select the ad unit by build type (Google's test interstitial unit in debug, the real unit in release) and verify that a debug build's ad request uses the test unit ID

## 3. Game flow

- [ ] 3.1 Route the game-over "play again" action through `AdFrequencyCap` and `InterstitialAds` in the UI layer, leaving the engine unchanged, and verify by running a debug build that a test ad appears after every third game and the next game starts on dismissal
- [ ] 3.2 Call `preload()` when a game starts and after each dismissal, and verify with the device offline that "play again" starts the next game immediately when no ad is loaded
- [ ] 3.3 Verify by running the app that no ad appears on app start, during a game, or when quitting

## 4. Account and store setup (human tasks)

- [ ] 4.1 Human: create the AdMob account, register the app and create an Interstitial ad unit; deliver the App ID and ad unit ID for the release build configuration
- [ ] 4.2 Human: configure the consent message in AdMob under Privacy & messaging and verify it appears on a test device set to an EEA region
- [ ] 4.3 Human: confirm the Play target age group. If it includes under-13s, stop and extend this change with child-directed request configuration before release
- [ ] 4.4 Human: in Play Console set "Contains ads" to yes and update the Data safety form for the advertising ID and device data
- [ ] 4.5 Human: publish `app-ads.txt` on the developer website named in the store listing and verify AdMob reports it as found
- [ ] 4.6 Human: add a privacy policy URL to the store listing that mentions ads and the advertising ID

## 5. Integration check

- [ ] 5.1 Human check on a real device with a release-candidate build: play six games and confirm an ad appears after the third and the sixth only, never mid-game, and that declining consent results in no ads at all
