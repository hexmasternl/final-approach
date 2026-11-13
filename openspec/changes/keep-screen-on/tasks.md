## 0. Prerequisite

- [ ] 0.1 Confirm `settings-page` (#66: settings page and settings store) is merged into `development` before starting. Verify: the settings page and its store exist on the branch.

## 1. Platform hook

- [ ] 1.1 Add `fun keepScreenOn(on: Boolean)` to `AppHost` with a KDoc comment; record the last value in `FakeAppHost` (`commonTest`). Verify: `./gradlew :shared:allTests` compiles and passes.
- [ ] 1.2 Implement it in `AndroidAppHost` by adding or clearing `WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON` on `activity.window`. No `PowerManager`. Verify: `./gradlew :androidApp:assembleDebug`.
- [ ] 1.3 Add `<uses-permission android:name="android.permission.WAKE_LOCK" tools:node="remove" />` to `androidApp/src/main/AndroidManifest.xml`. Verify: build release and debug, then confirm neither `merged_manifest/*/AndroidManifest.xml` contains `android.permission.WAKE_LOCK`.
- [ ] 1.4 Document `keepScreenOn` in the `AppHost` list in `docs/architecture.md`, including that it uses the window flag and needs no permission. Verify: read the section.

## 2. Setting

- [ ] 2.1 Add `keepScreenOn: Boolean`, default `true`, to the settings store from #66, persisted like its other values. Add a `commonTest` test that the default is on and a saved value is read back. Verify: `./gradlew :shared:allTests`.
- [ ] 2.2 Add a Gameplay section between Sound and Accessibility on the settings page with an `fa-setting` row and Toggle: "Keep screen on" / "While a level is playing", bound to the stored value. Use theme tokens and the section header from #66; no literals. Verify: preview or run, the row shows on by default and toggling it changes the stored value at once.
- [ ] 2.3 Add the Keep screen on setting to the settings section of `docs/game-design.md`. Verify: read the section.

## 3. Rule and game screen

- [ ] 3.1 Add `shouldKeepScreenOn(setting: Boolean, state: GameState): Boolean` in `ui/game` with `commonTest` tests: true when setting on, not paused and `Playing`; false when paused, when `GameOver`, and when the setting is off. Verify: `./gradlew :shared:allTests`.
- [ ] 3.2 In `GameScreen`, read the setting, call `host.keepScreenOn(shouldKeepScreenOn(setting, state))` whenever the value changes, and call `host.keepScreenOn(false)` on dispose. Verify: `./gradlew :androidApp:assembleDebug`; no other screen calls `keepScreenOn`.

## 4. Device checks (human)

- [ ] 4.1 Human: set the device screen timeout to 15 or 30 seconds. With the setting on, play a level without touching it for longer than the timeout; the screen stays on. Open the options panel and wait; the screen dims and locks.
- [ ] 4.2 Human: with the setting off, a running level dims and locks after the timeout. Restart the app; the toggle is still off.
- [ ] 4.3 Human: on the main page, level select and settings page the screen times out normally; after quitting a level it times out normally again.
- [ ] 4.4 Human: on a release build, ads still load and show and logcat shows no wake-lock `SecurityException`. If they fail, stop and follow the fallback in design decision 6.
