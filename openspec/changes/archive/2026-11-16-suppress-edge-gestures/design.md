## Context

The game screen (`ui/game/GameScreen.kt`) already locks landscape and keeps the screen on through `AppHost`, and its `BackHandler` already opens the options panel during play (and quits from the game-ended dialog). The app runs edge-to-edge (`enableEdgeToEdge()` in `MainActivity`), so the game `Canvas` covers the whole window and its pixel coordinates are window coordinates. Aircraft are picked within `FaDimens.touchAircraft` (72dp) of their centre, used as a pick radius; rolling-out airplanes and landing helicopters cannot be picked (`GameEngine.select`). `minSdk` is 26; gesture exclusion exists from API 29. The shared module has only `commonMain`/`commonTest`.

## Goals / Non-Goals

**Goals:** hide the bars and add side-edge exclusion zones only while a level runs; keep every platform call behind `AppHost`; keep the zone planning a pure, unit-tested function.

**Non-Goals:** lock task mode, top/bottom exclusion, a setting, iOS behaviour.

## Decisions

### 1. One "level is running" rule

Add `isLevelRunning(state: GameState): Boolean = !state.paused && state.status is GameStatus.Playing` next to `shouldKeepScreenOn` (`KeepScreenOn.kt`, renamed or kept), and make `shouldKeepScreenOn` use it. Immersive mode and exclusion use the same rule, so the three behaviours never disagree. A tutorial step counts as running (the engine is not paused), the same as for keep-screen-on.
*Rejected:* tracking `optionsOpen` and the ad wait separately in the screen; the engine's `paused` flag already covers both.

### 2. `AppHost` gets two methods with no-op defaults

```kotlin
/** Hides the system bars (transient on swipe) while [enabled]; false shows them again. */
fun setImmersive(enabled: Boolean) {}

/** Excludes [rects] (window pixels) from system edge gestures; an empty list removes all zones. */
fun setGestureExclusion(rects: List<ScreenRect>) {}

data class ScreenRect(val left: Int, val top: Int, val right: Int, val bottom: Int)
```

Defaults keep iOS and existing fakes compiling. `FakeAppHost` overrides both and records the last value.
*Rejected:* `Modifier.systemGestureExclusion` from Compose: it is Android-only and would need an `androidMain` source set in `shared`, which the architecture keeps out.

### 3. Android implementation

- `setImmersive(true)`: `WindowCompat.getInsetsController(window, window.decorView)`, `systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE`, `hide(WindowInsetsCompat.Type.systemBars())`; `false` calls `show(systemBars())`. The host remembers the last requested value.
- `MainActivity.onWindowFocusChanged(hasFocus = true)` re-applies the remembered value, because the system can show the bars again after another window (an ad, the drawer, a return from Recents) took focus.
- `setGestureExclusion(rects)`: on API 29+ sets `window.decorView.systemGestureExclusionRects` to the rects as `android.graphics.Rect`; below API 29 it does nothing. The decor view spans the window, so window pixels need no conversion.
*Rejected:* `SYSTEM_UI_FLAG_IMMERSIVE_STICKY` flags: deprecated since API 30; the compat controller covers API 26 to 37.

### 4. A pure planner decides the zones

`planGestureExclusion(...)` in `ui/game/GestureExclusion.kt`, in pixels, with no Compose types:

- Inputs: aircraft screen positions with id and pickable flag, the selected id, screen size, the left and right back-gesture strip widths, the pick radius, and the config values converted to pixels.
- An aircraft is near the left edge when `x - pickRadius < leftStrip` (right edge mirrored). Only pickable aircraft count.
- Its zone spans from the edge to the strip width horizontally (exclusion beyond the strip has no effect, and only height counts toward the cap) and `y ± zoneHalfHeight` vertically, clamped to the screen.
- Per edge, candidates are taken in priority order: selected aircraft, then by distance to that edge. A candidate is added if the union of the accepted vertical intervals plus it stays within `maxHeightPerEdge`; otherwise it is skipped and the next is tried. Overlapping zones are merged in the output.
- A strip width of 0 (button navigation, or no gesture insets) gives no zones for that edge.

The strip widths come from `WindowInsets.systemGestures` (left and right) read in `GameScreen`, which is available in Compose Multiplatform common code.
*Rejected:* a fixed strip width in config: Android lets the user change the back-gesture sensitivity, and the inset reports the real width. *Rejected:* zones that follow the finger: they would arrive after touch-down, too late for the system.

### 5. Tunables in a config object

`GestureExclusionConfig` in `ui/game/` with `maxHeightPerEdge = 200.dp` (the system cap) and `zoneHalfHeight = 48.dp` (the margin around an aircraft: two zones of 96dp fit in one edge). These are platform limits and layout tuning, not design tokens, so they live in config rather than `FaDimens`; the pick radius still comes from `FaDimens.touchAircraft`.

### 6. Wiring in `GameScreen`

- `DisposableEffect(host, running) { host.setImmersive(running); onDispose { host.setImmersive(false) } }`, as for keep-screen-on.
- The zones are planned from the state and viewport each frame when running; an empty list otherwise. `host.setGestureExclusion` is called only when the planned list differs from the last one sent (rects are whole pixels). `onDispose` sends an empty list.
- The pickable test is shared with `GameEngine.select` (extract it as `Aircraft.isPickable` in `domain/Aircraft.kt`), so selection and zones never disagree.
- `BackHandler` is unchanged: it already pauses during play. Tests pin it down.

### 7. The HUD does not jump

`Hud` pads with `WindowInsets.safeDrawing`, which shrinks when the bars hide, so the chips would move when play starts and when transient bars appear. Pad it with the union of `WindowInsets.displayCutout` and `WindowInsets.systemBarsIgnoringVisibility` instead, so its position does not depend on bar visibility. If `systemBarsIgnoringVisibility` is not available in common code in the Compose Multiplatform version used, keep the insets captured while the bars were last visible on the game screen instead.
*Rejected:* letting the HUD move: a jumping score chip at every edge swipe looks broken.

No design-system token or component is added: nothing new is drawn.

## Risks / Trade-offs

- The system honours at most 200dp per edge; with three or more aircraft near one edge some taps can still start a back gesture. Mitigation: back then pauses instead of leaving.
- Updating exclusion rects every frame costs a little; the "only when changed" check limits it, but a moving aircraft changes its rect most frames. Acceptable; check on a low-end device.
- Immersive mode can be cancelled by the system (focus loss); mitigated by re-applying on focus regain.
- Merge conflicts with `landscape-dialogs` and `world-zoom-retune` in `GameScreen.kt` and `Hud.kt`.

## Assumptions

- "Results screen" in the issue is the game-ended dialog; there is no other results screen.
- A level counts as running exactly when the keep-screen-on rule says so (not paused, not waiting for the ad, not over), including tutorial steps.
- Zone height around an aircraft is 96dp (48dp half height), so two separate zones fit per edge.
- An aircraft gets a zone when its pick area (72dp radius) reaches the back-gesture strip; the zone covers only the strip horizontally.
- Priority within the cap: selected aircraft, then closest to that edge; a zone that does not fit is left out rather than shortened.
- Back while the options panel is open keeps closing the panel (resume), and back on the game-ended dialog keeps returning to the main page, as today.
- The HUD should keep its place when the bars hide or show.
