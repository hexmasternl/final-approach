## Decisions

- **Find the flagged call before changing anything.** Scan the release runtime classpath: unpack the AARs and jars (start with `play-services-ads` 25.5.0 and its transitive ads/UI libraries) and search the bytecode for `setStatusBarColor`, `setNavigationBarColor`, `setNavigationBarDividerColor`, `setDecorFitsSystemWindows`, `layoutInDisplayCutoutMode` / `LAYOUT_IN_DISPLAY_CUTOUT_MODE_*`, `SYSTEM_UI_FLAG_*`. Also check the merged theme and the parent `android:Theme.Material.Light.NoActionBar` for deprecated attributes. Rejected: guessing a fix without a located call, because the fix depends on where it lives.
- **Fix at the source.** Dependency: bump the version in `libs.versions.toml` to a release that no longer makes the call. Our code: `enableEdgeToEdge(...)` / `WindowCompat` / `WindowInsetsControllerCompat`. Theme: drop the attribute, add a `values-v35` theme if needed. Rejected: suppressing lint, which hides the report without removing the call.
- **No fixed release.** Record the library, version and call here, and what we do instead (for example keep the newest release and wait). Rejected: removing the ads SDK, which the game depends on (`level-start-ads`).
- **Lint is a gate.** `:androidApp:lint` must show no edge-to-edge or deprecated-API finding that is ours. Findings in dependencies are recorded, not suppressed.
- **Inset check on a real surface.** Android 15+ emulator or device, 3-button and gesture navigation, a cutout, landscape (the game locks landscape). Fix any screen with content under a bar or cutout, using the existing inset handling in `FaDialog`, `GameScreen`, `Hud`, `LevelSelectPage`, `TutorialOverlay`, `MainPage`, `SettingsPage`. Keep the rule that nothing covers a landing zone.

## Findings

Scan by autocoder run runner-20261116-vpu1: all AARs of the release runtime classpath (bytecode search) plus our sources, theme and lint.

- **Our code and theme: clean.** `MainActivity` only calls `enableEdgeToEdge()`; `Theme.FinalApproach` sets only `android:windowBackground`. No `setStatusBarColor`, `setNavigationBarColor`, `setDecorFitsSystemWindows`, cutout mode or `SYSTEM_UI_FLAG_*` in `app/src`. Nothing to fix. `:androidApp:lint` reports 0 errors and no edge-to-edge finding (7 unrelated warnings).
- **play-services-ads 25.5.0** (`setDecorFitsSystemWindows`, `layoutInDisplayCutoutMode`) and **user-messaging-platform 4.0.0** (`setDecorFitsSystemWindows`): these are the likeliest source of Play's message. Both are already the latest releases on Google Maven, so there is no fixed release. Decision: keep them (the ads SDK is required by `level-start-ads`) and re-check on each release.
- **androidx.browser 1.8.0** (`setNavigationBarColor`, `setNavigationBarDividerColor`, cutout mode), pulled in by the ads SDK: 1.10.0 still has the same calls, so a bump gains nothing. Left alone.
- **androidx.activity 1.13.0, androidx.core 1.18.0, core-splashscreen**: AndroidX own edge-to-edge implementation, version-guarded; already current. Left alone.

## Assumptions

- Play analysed 0.1.8 and the message is not stale (author's answer).
- The Play class list stays unavailable, so the scan is the source of truth; a finding that does not match Play's report is confirmed by the next upload.
- The flagged call is most likely in a dependency or the inherited theme, since our own code looks clean.
