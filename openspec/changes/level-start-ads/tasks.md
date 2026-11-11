# Tasks

## 1. Shared abstraction

- [x] 1.1 Add the `Ads` interface (`preload()`, `showInterstitial(onDismissed)`, composable `Banner`) and `NoOpAds` to `commonMain`, expose it as `AppHost.ads`, and verify with a unit test that `NoOpAds` calls `onDismissed` exactly once
- [x] 1.2 Verify that `commonMain` contains no `android.*`, `com.google.android.*` or ads SDK reference (search the source set; the shared tests must pass on the JVM without the Android SDK)
- [x] 1.3 Document the ads interface in `docs/architecture.md` and verify the described names match the code

## 2. Android implementation

- [x] 2.1 Add `play-services-ads` and `user-messaging-platform` to the Android source set through `gradle/libs.versions.toml` and verify `./gradlew :androidApp:assembleDebug` succeeds
- [x] 2.2 Add the AdMob App ID meta-data to the Android manifest (release App ID `ca-app-pub-1310013076234295~7766080345`, Google's test App ID in debug) and declare `<uses-permission android:name="com.google.android.gms.permission.AD_ID" />`; verify that the merged manifest contains the permission
- [ ] 2.3 Implement the consent flow with UMP on app start and initialise the Mobile Ads SDK only after it resolves and allows ad requests; verify with logging on a device that no ad request is made before consent resolves, and none at all when consent does not allow it or the flow fails
- [ ] 2.4 Implement `Ads.showInterstitial` for Android (load, show, full-screen callback) so that `onDismissed` is called exactly once whether the ad was shown, failed to show, or was not ready; verify each of the three outcomes on a device
- [x] 2.5 Select the ad units by build type (Google's test units in debug; in release the interstitial "before-level-starts" `ca-app-pub-1310013076234295/6684168624` and the banner "after-level-finished" `ca-app-pub-1310013076234295/3427449894`) and verify that a debug build's ad requests use the test units

## 3. Game flow

- [ ] 3.1 Hold the engine paused in the game screen until `showInterstitial` calls back, both on entering a level and on restart (game ended dialog and options panel), leaving the engine unchanged; verify by running a debug build that a test ad appears before the level and before each restart, and the level begins on dismissal
- [ ] 3.2 Call `preload()` at game-screen entry and after each dismissal, and verify with the device offline that a level starts immediately when no ad is loaded
- [ ] 3.3 Verify by running the app that no ad appears on app start, during a game, or when quitting

## 4. Account and store setup (human tasks)

- [x] 4.1 Human: create the AdMob account, register the app and create the ad units (done: app "Final Approach" `ca-app-pub-1310013076234295~7766080345`, interstitial "before-level-starts" `ca-app-pub-1310013076234295/6684168624`, banner "after-level-finished" `ca-app-pub-1310013076234295/3427449894`)
- [ ] 4.2 Human: configure the consent message in AdMob under Privacy & messaging and verify it appears on a test device set to an EEA region
- [ ] 4.3 Human: confirm the Play target age group. If it includes under-13s, stop and extend this change with child-directed request configuration before release
- [ ] 4.4 Human: in Play Console set "Contains ads" to yes, update the Data safety form for the advertising ID and device data, and answer the advertising ID question as "yes" (the app declares the AD_ID permission)
- [ ] 4.5 Human: publish `app-ads.txt` on the developer website named in the store listing and verify AdMob reports it as found
- [ ] 4.6 Human: add a privacy policy URL to the store listing that mentions ads and the advertising ID

## 5. Integration check

- [ ] 5.1 Human check on a real device with a release-candidate build: start and restart levels and confirm an ad appears before each, never mid-game, and that declining consent results in no ads at all
