## Why

On Android with gesture navigation, aircraft enter from the screen edges, so players tap and drag near the edges and trigger back, the notification drawer or home by accident. Android cannot switch these gestures off, but the app can make them much harder to trigger while a level is running.

GitHub issue: #70

## What Changes

- **Immersive play:** while a level is running, the status and navigation bars are hidden, with transient bars on swipe. A swipe from the top or bottom edge only reveals the bars briefly; a second swipe is needed to open the drawer or go home.
- **Back-gesture exclusion on the side edges (Android 10+):** while a level is running, the app asks the system to exclude zones on the left and right edges around pickable aircraft whose pick area reaches that edge's back-gesture strip, the selected aircraft first. The zones follow the aircraft, so they are in place before the finger touches down. Each edge stays within the system cap of 200dp of exclusion height.
- **Only during active play:** the bars come back and the zones are removed whenever the level is not running: options panel open, waiting for the interstitial ad, game-ended dialog, and after leaving the game screen.
- **Back pauses the level:** a back gesture or button press that still gets through during a running level opens the options panel (pauses) instead of leaving. The game screen already does this; this change makes it a requirement and corrects the stale `first-level` scenario that says back returns to the main page.
- `AppHost` gains `setImmersive(enabled)` and `setGestureExclusion(rects)` with no-op defaults, implemented in `androidApp`; `commonMain` stays free of `android.*`.

## Capabilities

### New Capabilities

- `system-gestures`: how the game screen keeps the operating system's edge gestures from interfering with play (immersive bars, side-edge exclusion zones, back pauses).

### Modified Capabilities

- `first-level`: the Back scenario of "Take off opens the first level" changes from returning to the main page to pausing the level, matching the code, `docs/game-design.md` and the `system-gestures` requirement.

## Out of scope

- Screen pinning / lock task mode (needs user confirmation; too heavy for a casual game).
- Excluding the top or bottom edges: Android does not allow excluding the home gesture, and immersive mode already covers both.
- A setting to turn this behaviour off.
- Immersive mode or exclusion zones on any page other than the game screen.
- iOS (no host yet); it keeps the no-op defaults.

## Dependencies

- None blocking. Builds on the existing `AppHost`, `BackDispatcher`/`BackHandler` and the running-level rule of `keep-screen-on` (`shouldKeepScreenOn`).
- Touches `GameScreen.kt` and `Hud.kt`, which active changes `landscape-dialogs` and `world-zoom-retune` also touch; expect small merge conflicts, no behaviour overlap.

## Impact

- `shared/.../platform/AppHost.kt` (two methods with no-op defaults, a `ScreenRect` type), `FakeAppHost` in `commonTest`.
- New pure planner and config in `shared/.../ui/game/` with `commonTest` tests; `GameScreen.kt` wiring; `Hud.kt` insets so the HUD does not jump when the bars hide or show.
- `androidApp/.../AndroidAppHost.kt`, `MainActivity.kt` (re-apply immersive on focus regain).
- `docs/architecture.md` (Platform boundary), `docs/game-design.md` (one line on edge gestures).
