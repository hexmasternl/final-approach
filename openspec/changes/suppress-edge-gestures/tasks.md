## 1. Running rule and pickable test

- [x] 1.1 Add `isLevelRunning(state)` beside `shouldKeepScreenOn` and make `shouldKeepScreenOn` use it. Extend `KeepScreenOnTest` (or add `LevelRunningTest`) for running, paused, game over and tutorial step. Verify: `./gradlew :shared:allTests`.
- [x] 1.2 Extract `Aircraft.isPickable` (not `RollingOut`, not `Landing`) and use it in `GameEngine.select`. Existing selection tests stay green; add one test per excluded status. Verify: `./gradlew :shared:allTests`.

## 2. Platform boundary

- [x] 2.1 Add `ScreenRect`, `setImmersive(enabled)` and `setGestureExclusion(rects)` with no-op defaults to `platform/AppHost.kt`; override both in `FakeAppHost`, recording the last values. Verify: `./gradlew :shared:allTests`.
- [x] 2.2 Implement both in `AndroidAppHost` (insets controller with `BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE`; `decorView.systemGestureExclusionRects` guarded by API 29) and re-apply the remembered immersive value in `MainActivity.onWindowFocusChanged`. Verify: `./gradlew :androidApp:assembleDebug`.
- [x] 2.3 Document both methods in `docs/architecture.md` (Platform boundary). Verify: read the section back.

## 3. Exclusion planner

- [x] 3.1 Add `GestureExclusionConfig` (`maxHeightPerEdge = 200.dp`, `zoneHalfHeight = 48.dp`) and the pure `planGestureExclusion` in `ui/game/GestureExclusion.kt` as in design decision 4. Verify: compiles.
- [x] 3.2 Add `GestureExclusionTest` in `commonTest`: aircraft near the left and the right edge get a zone spanning the strip and centred on them; an aircraft away from the edges gets none; a non-pickable aircraft gets none; zero strip width gives none; zones are clamped to the screen; overlapping zones merge; with many aircraft on one edge the total stays within the cap; the selected aircraft is kept first, then the closest to the edge. Verify: `./gradlew :shared:allTests`.

## 4. Game screen wiring

- [x] 4.1 In `GameScreen`, call `host.setImmersive(isLevelRunning(state))` from a `DisposableEffect` that sends `false` on dispose. Extend the game screen tests that use `FakeAppHost`, if present, or test the rule through 1.1. Verify: `./gradlew :shared:allTests`.
- [x] 4.2 Plan the zones each frame while running (from `WindowInsets.systemGestures`, the viewport and the pick radius), send them only when they changed, send an empty list when not running and on dispose. Verify: `./gradlew :androidApp:assembleDebug`.
- [x] 4.3 Add a test that pins down the back rule during play (running level or ad wait opens the options panel and pauses; it never calls `onQuit`), extracting the `BackHandler` decision into a small function if needed. Verify: `./gradlew :shared:allTests`.
- [x] 4.4 Pad `Hud` with insets that ignore bar visibility (design decision 7) so it keeps its place. Verify: Android Studio preview and 5.1.
- [x] 4.5 Add one line on edge gestures to `docs/game-design.md` (near the HUD/back rule): bars hidden and side edges protected while a level runs; back pauses. Verify: read it back.

## 5. Device checks (human)

- [ ] 5.1 (Human) On an Android 10+ phone with gesture navigation: during play the bars are hidden and the HUD does not move; one swipe down from the top only shows the status bar briefly; one swipe up from the bottom only shows the navigation bar briefly; touching down on an aircraft inside the left or right back strip selects it and draws a path without going back; a swipe in from a side edge away from aircraft pauses the level.
- [ ] 5.2 (Human) Open the options panel, wait for an interstitial, end a level and quit to the main page: each time the bars are visible and a side swipe works as back. Continue hides the bars again; leaving the app and returning during play hides them again.
- [ ] 5.3 (Human) On an Android 8 or 9 device or emulator, a level plays normally with no crash.
