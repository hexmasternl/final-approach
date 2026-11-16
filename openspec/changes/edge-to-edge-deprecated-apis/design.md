## Decisions

- **Find the flagged call before changing anything.** Scan the release runtime classpath: unpack the AARs and jars (start with `play-services-ads` 25.5.0 and its transitive ads/UI libraries) and search the bytecode for `setStatusBarColor`, `setNavigationBarColor`, `setNavigationBarDividerColor`, `setDecorFitsSystemWindows`, `layoutInDisplayCutoutMode` / `LAYOUT_IN_DISPLAY_CUTOUT_MODE_*`, `SYSTEM_UI_FLAG_*`. Also check the merged theme and the parent `android:Theme.Material.Light.NoActionBar` for deprecated attributes. Rejected: guessing a fix without a located call, because the fix depends on where it lives.
- **Fix at the source.** Dependency: bump the version in `libs.versions.toml` to a release that no longer makes the call. Our code: `enableEdgeToEdge(...)` / `WindowCompat` / `WindowInsetsControllerCompat`. Theme: drop the attribute, add a `values-v35` theme if needed. Rejected: suppressing lint, which hides the report without removing the call.
- **No fixed release.** Record the library, version and call here, and what we do instead (for example keep the newest release and wait). Rejected: removing the ads SDK, which the game depends on (`level-start-ads`).
- **Lint is a gate.** `:androidApp:lint` must show no edge-to-edge or deprecated-API finding that is ours. Findings in dependencies are recorded, not suppressed.
- **Inset check on a real surface.** Android 15+ emulator or device, 3-button and gesture navigation, a cutout, landscape (the game locks landscape). Fix any screen with content under a bar or cutout, using the existing inset handling in `FaDialog`, `GameScreen`, `Hud`, `LevelSelectPage`, `TutorialOverlay`, `MainPage`, `SettingsPage`. Keep the rule that nothing covers a landing zone.

## Findings

To be filled in by task 1.3: flagged call, location (class, library, version), chosen fix.

## Assumptions

- Play analysed 0.1.8 and the message is not stale (author's answer).
- The Play class list stays unavailable, so the scan is the source of truth; a finding that does not match Play's report is confirmed by the next upload.
- The flagged call is most likely in a dependency or the inherited theme, since our own code looks clean.
