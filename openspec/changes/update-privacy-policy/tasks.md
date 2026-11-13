# Tasks

## 1. Manifest

- [ ] 1.1 Remove the duplicate `AD_ID` permission and `com.google.android.gms.ads.APPLICATION_ID` meta-data entries from `androidApp/src/main/AndroidManifest.xml`. Verify that `./gradlew :androidApp:assembleDebug` succeeds and the merged debug manifest has the `APPLICATION_ID` meta-data exactly once.
- [ ] 1.2 Add the `tools` namespace and remove `com.google.android.gms.permission.AD_ID` with `tools:node="remove"` (design decision 3). Verify that the merged manifests for both debug and release under `androidApp/build/intermediates/merged_manifest/` contain no `AD_ID` permission (spec: No advertising ID permission).

## 2. Child-directed ad requests

- [ ] 2.1 In `AdMobAds.kt`, set a global `RequestConfiguration` before `MobileAds.initialize`: child-directed true, under age of consent true, max ad content rating G (design decision 1). Verify on a device with a debug build that test interstitials and banners still load, and that logcat shows the configuration was applied before initialisation.
- [ ] 2.2 Build the UMP `ConsentRequestParameters` with `setTagForUnderAgeOfConsent(true)` (design decision 2). Verify on a device with UMP's EEA debug geography that the game stays playable and that no ad is requested before consent resolves. Record whether ads show in that geography in design.md, under the matching risk.
- [ ] 2.3 Update the ads paragraph in `docs/architecture.md` to describe the child-directed configuration and the removed `AD_ID`. Verify the paragraph matches the code from 2.1 and 2.2.

## 3. Privacy policy

- [ ] 3.1 Rewrite `docs/privacy-policy.md` from the outline in the proposal. Cover ads and consent, the data Google may collect (with a link to https://policies.google.com/technologies/partner-sites), internet and permissions, data on the device, children, third parties, changes (pointing to the public URL) and contact. Set "Last updated" to the release date. Verify each requirement of the `privacy-policy` spec against the text, and that nothing contradicts the code after group 2.
- [ ] 3.2 Add the privacy policy URL, the target audience and the Families declarations to the Play Console steps in `docs/deployment.md`. Verify that the URL there is exactly https://github.com/hexmasternl/final-approach/blob/main/docs/privacy-policy.md.

## 4. Checks

- [ ] 4.1 Run `./gradlew :shared:allTests` and `./gradlew :androidApp:assembleDebug` from `app/src`. Verify both succeed.
- [ ] 4.2 Run the play-policy-critic agent on the change. Fix every finding within scope, and file any finding about ad placement or format as a new issue. Verify the critic reports no open violations within scope.
- [ ] 4.3 Run `openspec validate update-privacy-policy --strict`. Verify it passes.

## 5. Human: Play Console and AdMob

- [ ] 5.1 Play Console > App content > Target audience and content: include the under-13 age groups. Verify the Families policy questions are answered.
- [ ] 5.2 Play Console > App content: Ads ("contains ads") and Advertising ID ("does not use"). Verify both are saved.
- [ ] 5.3 Play Console > Data safety: match the new policy (data collected by the ads SDK, nothing collected by the game itself). Verify it agrees with `docs/privacy-policy.md`.
- [ ] 5.4 Play Console > App content > Privacy policy: set https://github.com/hexmasternl/final-approach/blob/main/docs/privacy-policy.md. Verify the link opens after the release merge into `main`.
- [ ] 5.5 AdMob > app settings: mark the app as child-directed with a maximum ad content rating of G. Verify it matches the in-app configuration.
