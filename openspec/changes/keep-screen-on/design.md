# Design

## Context

`GameScreen` already asks the host for a platform behaviour while it is shown (`AppHost.lockLandscape`, set in a `DisposableEffect`). The engine exposes `GameState.paused` and `GameState.status` (`Playing` or `GameOver`). `GameScreen` pauses the engine while the options panel is open and while the interstitial before a level is showing; a restart from the game-ended dialog keeps the status `GameOver` until the ad is dismissed. The settings page and its device-local settings store come from the `settings-page` change (#66) and are not built yet.

## Decisions

### 1. The platform call goes behind `AppHost.keepScreenOn(on: Boolean)`

Add `fun keepScreenOn(on: Boolean)` to `AppHost`. `AndroidAppHost` adds or clears `WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON` on `activity.window`. `FakeAppHost` records the last value.

Rejected: a Compose keep-screen-on modifier. Compose's `Modifier.keepScreenOn()` is an Android-side API and is not guaranteed in Compose Multiplatform `commonMain` at 1.12; relying on it would either break the common build or leave iOS without a hook. The `AppHost` route matches `lockLandscape` and is testable with `FakeAppHost`.

Rejected: `PowerManager` wake locks and the `WAKE_LOCK` permission. They keep the device awake in the background, are flagged by Play vitals, and contradict the privacy policy (#64). The window flag needs no permission and clears itself when the activity leaves the foreground.

### 2. One pure rule decides, tested in `commonTest`

```kotlin
fun shouldKeepScreenOn(setting: Boolean, state: GameState): Boolean =
    setting && !state.paused && state.status is GameStatus.Playing
```

Place it next to `GameScreen` in `ui/game` (for example `KeepScreenOn.kt`); it depends only on engine state and a boolean, so it is a plain function, not composable logic. "Running" is defined by engine state alone: the options panel and the pre-level interstitial both pause the engine, and the game-ended dialog shows exactly when the status is `GameOver`, so no UI flag is needed.

Rejected: deciding from `optionsOpen` and dialog visibility in the composable. That duplicates what the engine already knows and is not unit-testable.

### 3. `GameScreen` applies the rule; leaving always clears it

`GameScreen` derives the value from `state` and the setting and calls `host.keepScreenOn(value)` when it changes (a `LaunchedEffect` keyed on the value), and a `DisposableEffect(host)` calls `host.keepScreenOn(false)` on dispose. So quitting, the game-ended Main page action, and any other way out restore the normal timeout. No other screen calls it, so the splash, main, level select and settings pages always have the normal timeout.

The setting is read from the settings store from #66 when the game screen opens. Settings open only from the main page, so the value cannot change mid-level; if Settings ever opens from the game, reading it as observable state makes the change apply at once without changing the rule.

### 4. The setting lives in the `settings-page` store

Add a `keepScreenOn: Boolean` value, default `true`, to the settings store that #66 introduces, persisted the same way as its other values (device-local, not backed up). Use whatever read/write shape #66 settles on; do not create a second store.

### 5. Settings row: a new Gameplay section

Put the row in a new "Gameplay" section, between Sound and Accessibility. Keeping the screen on is about play, not about access needs, and a Gameplay section gives #62's tutorial row a natural home later. Use the design system's `fa-setting` row with a Toggle (`fa-toggle`), as #66 does for Sound effects: label "Keep screen on", description "While a level is playing". Use the section header that #66 adds to the design system and theme; this change adds no new token or component.

Rejected: under Accessibility. It would mix a battery and comfort preference with access needs, and Accessibility is where players look for motion and contrast options.

### 6. Strip the `WAKE_LOCK` that dependencies merge in

The app's own manifest declares no `WAKE_LOCK`, but the current debug merged manifest (`androidApp/build/intermediates/merged_manifest/debug/...`) already holds `android.permission.WAKE_LOCK` and `FOREGROUND_SERVICE`, merged in by the Google Mobile Ads SDK's dependencies (WorkManager and Play services). To meet the "no `WAKE_LOCK` in the merged manifest" criterion, add to `androidApp/src/main/AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.WAKE_LOCK" tools:node="remove" />
```

(with `xmlns:tools` on the manifest element). Then verify that ads still load and show, and that no `SecurityException` from a wake lock appears in logcat, on a release build.

Rejected: leaving the merged permission in place. The issue requires its absence, and the privacy policy (#64) says the game asks for no other permissions.

Fallback, if removal breaks ads: keep the SDK's merged permission, narrow the criterion to "the app declares no `WAKE_LOCK` and uses no wake lock itself", and update the issue, this spec, and the privacy policy wording together. That is the author's call, not the implementer's.

## Assumptions

- "Playing" means running: paused (options panel), waiting for the interstitial, and the game-ended dialog all use the normal timeout, as the issue proposed.
- The row goes in a new Gameplay section between Sound and Accessibility.
- The setting is read when the level opens; mid-level changes are not possible today.
- No in-game indicator shows that the screen is being kept on.
- The `WAKE_LOCK` that the ads SDK merges in is removed with `tools:node="remove"` (decision 6).

## Risks

- **`settings-page` (#66) not merged first:** this change cannot add a row to a page that does not exist. Implement it after `settings-page` is merged into `development`.
- **Removing a dependency's `WAKE_LOCK`:** WorkManager or Play services may try to take a wake lock and fail without the permission. Decision 6 makes this a checked step with a stated fallback.
