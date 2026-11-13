# Tasks

## 1. Settings model and storage

- [x] 1.1 Add `domain/PlayerSettings.kt` with `PlayerSettings` (defaults: sound effects on, reduce motion Device), `ReduceMotionChoice` and `resolve(device)`; add `commonTest` tests for the defaults and all six choice/device combinations, and verify `./gradlew :shared:allTests` passes
- [x] 1.2 Add `platform/SettingsStore.kt` (interface plus `InMemorySettingsStore`) and `val settings: SettingsStore` on `AppHost`; update every `AppHost` test double; verify `./gradlew :shared:allTests` passes
- [x] 1.3 Add `SettingsController` (loads once, `StateFlow<PlayerSettings>`, `update` writes through); test that an update is emitted and saved, and that a new controller on the same store loads the saved value (the restart case); verify `./gradlew :shared:allTests` passes
- [x] 1.4 Implement the store in `AndroidAppHost` with a private `settings` preferences file, enum stored by name, unknown values falling back to defaults; verify `./gradlew :androidApp:assembleDebug` succeeds and `android:allowBackup="false"` is still in the manifest
- [x] 1.5 Describe the settings store and `SettingsController` in `docs/architecture.md`; verify the text names the new files and `AppHost` member

## 2. Effective reduced motion

- [x] 2.1 In `ui/App.kt`, collect the controller's settings and pass `reduceMotion.resolve(host.reduceMotion)` to `FaTheme`; verify by building and by a grep that `host.reduceMotion` is now read only in `App.kt`
- [x] 2.2 Switch `ui/menu/MainPage.kt` and `ui/splash/SplashPage.kt` from `host.reduceMotion` to `FaTheme.reduceMotion`, and make sure every `remember` that captures reduced motion keys on it; verify `./gradlew :shared:allTests` and `:androidApp:assembleDebug` pass
- [x] 2.3 Add a paragraph on the effective reduced-motion value to `docs/game-design.md` and `docs/architecture.md`; verify both say Device follows the device and On and Off override it

## 3. Sound gate

- [x] 3.1 If `path-cleared-sound` (#63) has not landed, add `SoundEffect`, `SoundPlayer`, `SilentSoundPlayer` and `AppHost.sounds` (silent default) exactly as its design specifies; verify `./gradlew :shared:allTests` passes
- [x] 3.2 Add `platform/GatedSoundPlayer.kt` wrapping a `SoundPlayer` with an `enabled` check at play time; test in `commonTest` with a recording player that `play` with sound off records nothing, with sound on records once, and that switching back on lets later plays through; verify `./gradlew :shared:allTests` passes
- [x] 3.3 In `ui/App.kt`, build one `GatedSoundPlayer(host.sounds)` reading `soundEffects` from the controller and pass it to the game screen; if #63 has landed, make its cue loop play through it instead of `host.sounds`; verify by grep that no call site outside `App.kt` uses `host.sounds` directly
- [x] 3.4 Note in `docs/architecture.md` that sound effects play through the gated player and never read the setting themselves; verify the text is there

## 4. Platform URL opening

- [x] 4.1 Add `fun openUrl(url: String)` to `AppHost` and the `AppLinks.PRIVACY_POLICY_URL` constant (the URL from `update-privacy-policy`); implement it in `AndroidAppHost` with `ACTION_VIEW`, swallowing `ActivityNotFoundException`; update test doubles; verify `:androidApp:assembleDebug` and `:shared:allTests` pass
- [x] 4.2 Add `openUrl` to the `AppHost` description in `docs/architecture.md`; verify the text is there

## 5. Design-system components

- [x] 5.1 Add the missing tokens (toggle track and knob sizes in `FaDimens`, `shadow-well` in `FaShadows`, and in `tokens.json` where new); verify no literal is introduced by grepping the new files for `.dp` and `Color(` outside `ui/theme`
- [x] 5.2 Add `FaToggle` to `ui/components` per `Toggle.md`, drawn 68 x 40 with a 48dp touch area, Switch semantics, bounce slide that snaps with reduced motion; add a debug preview; verify it in the preview in both states, Day and Night
- [x] 5.3 Add `FaSegmentedControl` to `ui/components` per `SegmentedControl.md`, segments drawn 40 high with a 48dp touch area, one selected, RadioButton semantics; add a debug preview; verify it in the preview with each option selected
- [x] 5.4 Add a line on the 48dp touch area to `docs/design-system/components/Toggle.md` and `SegmentedControl.md`; verify both mention `touch-min`

## 6. Settings page

- [x] 6.1 Add `ui/settings/SettingsPage.kt`: header with back icon button and "Settings", SOUND / ACCESSIBILITY / ABOUT groups, Sound effects row with toggle, Reduce motion row with segmented control, Privacy policy ghost button, "Final Approach <version>" caption, vertical scroll and safe-drawing insets, all from theme tokens; verify in a debug preview in portrait and in a short landscape size that every control is reachable
- [ ] 6.2 Wire the page into `ui/App.kt` in place of the placeholder: controls call `SettingsController.update`, Privacy policy calls `host.openUrl`, back and the back icon return to `Screen.Main`; remove the unused placeholder strings; verify `:androidApp:assembleDebug` passes
- [ ] 6.3 Describe the settings page in `docs/game-design.md` and the `Screen.Settings` route in `docs/architecture.md`; verify both match the specs

## 7. Device checks (human)

- [ ] 7.1 (human) On a device: Settings opens the page, system back and the back icon return to the main page, toggles change at once with no Save button
- [ ] 7.2 (human) Switch Sound effects off and Reduce motion On, force-stop and restart the app: both choices are kept; after a reinstall both are back at their defaults
- [ ] 7.3 (human) With device animations on, Reduce motion On stills the main page stripes, clouds and passing aircraft and the in-game rings and paths; with device animations off, Reduce motion Off brings them back; Device follows the device
- [ ] 7.4 (human) With Sound effects off no sound plays when an aircraft is cleared (needs #63); switching it on brings the sound back
- [ ] 7.5 (human) Privacy policy opens the published URL in the browser; the caption shows the installed version
- [ ] 7.6 (human) Check with a layout inspector or accessibility scanner that every control has a touch target of at least 48dp
