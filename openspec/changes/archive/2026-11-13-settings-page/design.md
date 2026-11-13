# Design

## Context

- `Screen.Settings` in `ui/App.kt` shows `PlaceholderPage` ("Sound and music switches will live here.") with a sky sunburst background and a panel.
- Reduced motion is read in two ways today: `App` passes `host.reduceMotion` into `FaTheme`, and components and the game read `FaTheme.reduceMotion`; but `MainPage` and `SplashPage` read `host.reduceMotion` directly. `AppHost.reduceMotion` is read once, when `AndroidAppHost` is built.
- `HighScoreStore` is the precedent for on-device storage: an interface in `platform/`, an `InMemoryHighScoreStore` for tests, and a private `SharedPreferences` file in `AndroidAppHost`. The manifest already sets `android:allowBackup="false"`.
- The design system has the `fa-setting` row, Toggle (68 x 40) and SegmentedControl (segments 40 high) in `showcase.html` and `docs/design-system/components/`. Group labels use the `label` text style ("Uppercase eyebrow labels above HUD values and settings groups"), and the `caption` style exists, so no new type token is needed. Compose has neither a toggle nor a segmented control yet.
- There is no sound playback yet; #63 adds the first sound player behind `AppHost`.

## Goals / Non-Goals

**Goals:**
- One source of truth for the player's settings and for the effective reduced-motion value, testable in `commonTest`.
- Platform storage and URL opening behind `AppHost`, nothing Android in `commonMain`.
- One gated wrapper around `AppHost.sounds` that every sound effect passes, so muting cannot be forgotten by a future sound.

**Non-Goals:**
- Reacting live to the device's reduced-motion setting changing while the app runs. `AppHost.reduceMotion` stays a value read at start, as today.
- A general preferences framework or a settings library.

## Decisions

### 1. Settings as one immutable data class, owned by a small holder
`domain/PlayerSettings.kt`: `data class PlayerSettings(val soundEffects: Boolean = true, val reduceMotion: ReduceMotionChoice = ReduceMotionChoice.Device)`, `enum class ReduceMotionChoice { Device, On, Off }`, and a pure `fun ReduceMotionChoice.resolve(device: Boolean): Boolean`. A `SettingsController` (in `platform/`, next to the store) loads once from the store, exposes `StateFlow<PlayerSettings>`, and on each `update(transform)` writes the new value through to the store and emits it. The page and `App` collect the flow.

*Alternative:* each composable reads and writes the store directly. Rejected: no single source of truth, and the reduced-motion combination would be spread across screens.

### 2. Typed store interface behind `AppHost`
`platform/SettingsStore.kt`: `interface SettingsStore { fun load(): PlayerSettings; fun save(settings: PlayerSettings) }` plus `InMemorySettingsStore`. `AppHost` gains `val settings: SettingsStore`. Android implements it with a private preferences file `settings` (separate from `best-scores`), one key per field; the enum is stored by name, and an unknown or missing value falls back to the default.

*Alternative:* a generic key-value store (`getBoolean(key)`). Rejected: keys and defaults would leak into shared code and every caller; a typed load/save keeps defaults in one place (`PlayerSettings()`).

*Alternative:* DataStore or multiplatform-settings. Rejected: a new dependency for two values; `SharedPreferences` is already in use.

### 3. Effective reduced motion flows through `FaTheme`
`App` computes `settings.reduceMotion.resolve(host.reduceMotion)` and passes it to `FaTheme(reduceMotion = ...)`. `MainPage` and `SplashPage` switch from `host.reduceMotion` to `FaTheme.reduceMotion`. `GameScreen` already reads `FaTheme.reduceMotion` and forwards it to the engine-free helpers (`AmbientLife`, paths, rings), so the game follows automatically. After the change, `host.reduceMotion` is read only in `App`; a grep shows no other use.

*Alternative:* add the override inside `AndroidAppHost.reduceMotion`. Rejected: the combination is a game rule, belongs in shared code and must be unit-tested; the host should only report the device.

### 4. The toggle gates `AppHost.sounds` through a wrapper
`path-cleared-sound` (#63) defines `SoundEffect`, `interface SoundPlayer { fun play(effect: SoundEffect) }` and `SilentSoundPlayer` in `platform/`, exposed as `AppHost.sounds` (silent by default), and its game screen plays cues through that player. This change adds `platform/GatedSoundPlayer.kt`: `class GatedSoundPlayer(private val inner: SoundPlayer, private val enabled: () -> Boolean) : SoundPlayer`, which forwards `play` only while `enabled()` is true. `App` builds one `GatedSoundPlayer(host.sounds) { controller.settings.value.soundEffects }` and the game plays through it instead of `host.sounds`, so the game never reads the setting itself. The check happens at play time: a sound already playing is not cut off, but nothing new starts.

Whichever change lands second wires the two. If this change lands first, it adds `SoundEffect`, `SoundPlayer`, `SilentSoundPlayer` and `AppHost.sounds` exactly as `path-cleared-sound` designs them, so that change only adds the Android player and the cue mapping and plays through the gated player.

*Alternative:* tell the platform player to mute. Rejected: the rule would live in each platform host.

*Alternative:* the game screen checks `soundEffects` before each `play`. Rejected: every future sound call site would have to remember it.

### 5. `AppHost.openUrl(url: String)`
Android starts an `Intent.ACTION_VIEW` for the URL and swallows `ActivityNotFoundException`, so a device without a browser stays on the page. The URL is a constant in a shared `AppLinks` object (`PRIVACY_POLICY_URL`), the one fixed by `update-privacy-policy`.

*Alternative:* an in-app web view. Rejected: needs the internet permission flow inside the app and more surface for the Families policy.

### 6. Page structure and design-system use
`ui/settings/SettingsPage.kt`, background the same sky sunburst as the placeholder (drawn static, as the placeholder already is), content in one panel built with `faBlock(surface, outline, line3, radiusLg, lipLg)`, max width as the placeholder panel, inside a vertical scroll with safe-drawing insets.
- Header: `FaIconButton(FaIcon.ArrowLeft, "Back", onBack)` and the title in `titleLg`, `ink`.
- Group labels: `label` style, `inkMuted`, uppercase text (SOUND, ACCESSIBILITY, ABOUT), `space5` above each group.
- Rows: `fa-setting` layout, label in `titleSm` `ink`, description in `bodySm` `inkMuted`, control at the end.
- New `FaToggle` (`ui/components`): track 68 x 40 dp, `radiusPill`, `line2` `outline`, `surfaceSunken` with the `shadow-well` inset when off and `sky500` when on; 28dp knob `surfaceRaised` off and `sun` on, sliding over `FaMotion.durQuickMs` with `FaMotion.easeBounce`, snapping with reduced motion. Semantics role Switch.
- New `FaSegmentedControl` (`ui/components`): trough `surfaceSunken`, `line2` `outline`, `radiusMd`, `space1` padding and gap; selected segment a raised `sun` tile with `line2` outline and 2dp lip, text `onAccent`; others `ink`; labels in `buttonLabelSm`. Semantics role RadioButton per segment.
- Privacy policy: `FaButton(variant = Ghost)`.
- Caption: `caption` style, `inkMuted`, "Final Approach " + `host.versionName`.
Missing tokens are added to the theme (and to `tokens.json` where they are new to the design system) rather than written as literals: the toggle track 68 x 40 and knob 28 in `FaDimens`, and `shadow-well`, which `FaShadows` does not have yet.

### 7. 48dp touch targets around 40dp visuals
The toggle and each segment stay drawn at 40dp, as the design system shows, but their touch area is padded to `touchMin` (48dp), the same approach `FaButton` already uses for small buttons. `Toggle.md` and `SegmentedControl.md` get one line saying so.

*Alternative:* draw them 48dp high. Rejected: changes the design system's look for every future use.

### 8. Copy
Page strings live in a private `SettingsText` object: "Settings", "Sound", "Sound effects", "Engines, dings and boings", "Accessibility", "Reduce motion", "Device", "On", "Off", "About", "Privacy policy", "Final Approach", "Back" (content description).

## Assumptions

- The page keeps the placeholder's sky background and a single panel, scrolling when it does not fit.
- The Sound effects row is shown even if #63 (`path-cleared-sound`) has not landed yet; either order works, and whichever lands second wires the gated player.
- Tapping Privacy policy with no browser does nothing visible.
- The settings page is not locked to landscape; it follows the device orientation like the main page.
- Group labels use the existing `label` style and the version line the existing `caption` style; no new type token.
- The device reduced-motion value is still read once at start.

## Risks / Trade-offs

- [This lands before #63, so the toggle has nothing to mute] → The setting is stored and `GatedSoundPlayer` wraps the silent player; `path-cleared-sound` then only adds the Android player and plays through the gated one.
- [A link out of a mixed-audience app] → Linking to the privacy policy is expected by Play; the play-policy-critic should still review it. A parental gate is out of scope.
- [Reduced motion changes mid-animation in components remembered with the old value] → `remember` keys that capture reduced motion (as `GameScreen` already does for `AmbientLife`) must include it; checked in the device task.

## Migration Plan

No migration: there are no stored settings yet, so every install starts at the defaults. Rollback is reverting the change; the `settings` preferences file is then ignored.
