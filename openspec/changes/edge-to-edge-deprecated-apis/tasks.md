## 1. Investigate

- [ ] 1.1 Scan the release runtime classpath (unpack AARs and jars, `play-services-ads` 25.5.0 first) for deprecated edge-to-edge calls: `setStatusBarColor`, `setNavigationBarColor`, `setNavigationBarDividerColor`, `setDecorFitsSystemWindows`, `LAYOUT_IN_DISPLAY_CUTOUT_MODE_*`, `SYSTEM_UI_FLAG_*`. Verify: each hit is listed with library, version and class.
- [ ] 1.2 Check the merged theme and the `Theme.FinalApproach` parent for deprecated window attributes. Verify: result noted.
- [ ] 1.3 Write the findings in `design.md` under Findings, each with its chosen fix. Verify: no finding without a resolution.

## 2. Fix

- [ ] 2.1 Fix each finding at its source (dependency bump in `libs.versions.toml`, our code, or theme / `values-v35`). Verify: the call no longer appears in the app's sources, or the classpath resolves the fixed release.
- [ ] 2.2 For a library with no fixed release, record it and what we do instead in `design.md`. Verify: library, version, call and decision are written.

## 3. Verify

- [ ] 3.1 Run `./gradlew :androidApp:lint`. Verify: no deprecated-API or edge-to-edge finding in our code or theme.
- [ ] 3.2 Run `./gradlew :androidApp:assembleDebug` and `./gradlew :shared:allTests` from `app/src`. Verify: both succeed.
- [ ] 3.3 Fix any screen with content under a bar or cutout found in 3.4, using the existing inset handling. Verify: the screen is clear on re-check.
- [ ] 3.4 Human: on Android 15+ check 3-button navigation, gesture navigation, a display cutout and landscape on every screen, and the in-game immersive swipe. Verify: nothing hidden by a bar or cutout.
- [ ] 3.5 Human: upload the next release and confirm both Play Console messages are gone. Verify: the Play Console shows neither.
