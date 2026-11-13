# Proposal

GitHub issue: #66

## Why

The main page has a Settings button that opens a placeholder. #63 adds the first sound and the design system says the game must be fully playable muted, so players need a way to turn sound off; a reduced-motion override helps players whose device setting does not match what they want in the game.

## What Changes

- Replace the settings placeholder with a real settings page: a header with a back icon button and the title "Settings", then three groups.
  - **Sound:** a "Sound effects" toggle ("Engines, dings and boings"), on by default. No Music row.
  - **Accessibility:** "Reduce motion", a segmented control with Device, On and Off, Device by default.
  - **About:** a "Privacy policy" ghost button that opens the published policy URL from `update-privacy-policy` (#64) in the browser, and a caption with "Final Approach" and the installed version.
- Changes apply at once; there is no Save button. Back and the back icon button return to the main page.
- Settings are stored on the device, survive restarts and are not backed up.
- One effective reduced-motion value combines the device setting with the player's choice. Every reduced-motion behaviour already in the specs (splash, main page, paths, proximity ring, scenery, HUD, dialogs, landing) follows it.
- When Sound effects is off, no sound effect plays. This gates the sound player that #63 adds.
- Shared code gets two new platform members behind `AppHost`: a settings store and a way to open a URL.
- New design-system components in Compose: Toggle and Segmented control, plus the `fa-setting` row.

Out of scope: Help / Replay tutorial (#62), Music, ad privacy choices (none exist while #64 treats every player as under the age of consent), Keep screen on (a separate issue that builds on this one), a parental gate in front of the policy link.

Depends on: `update-privacy-policy` (#64) for the policy URL and the policy text about on-device settings. Works best after #63; if this change lands first, the setting is stored and the row is shown, and #63 must route its sound through the gate this change adds.

## Capabilities

### New Capabilities
- `settings-page`: the settings page itself, meaning its layout, controls, navigation, the privacy policy link and the version caption.
- `player-settings`: the player's stored preferences, their defaults and persistence, and how they take effect in the rest of the game (sound effects muting and the effective reduced-motion value).

### Modified Capabilities
None. The main-page spec already says Settings opens the settings page and back returns to the main page. Existing reduced-motion requirements keep their wording; `player-settings` defines that "reduced motion enabled" means the effective value.

## Impact

- `commonMain`: `platform/AppHost.kt` (settings store, `openUrl`), a new settings store interface with an in-memory implementation, a settings type with the reduced-motion combination, a new `ui/settings` page, new `FaToggle` and `FaSegmentedControl` components, `ui/App.kt` (route, effective reduced motion into `FaTheme`), `ui/menu/MainPage.kt` and `ui/splash/SplashPage.kt` (stop reading `host.reduceMotion` directly).
- `androidApp`: `AndroidAppHost.kt` (a private preferences file for settings, `openUrl` through an `ACTION_VIEW` intent).
- `commonTest`: tests for the reduced-motion combination, defaults and persistence round trip.
- Docs: `docs/game-design.md` (settings), `docs/architecture.md` (settings store, new `AppHost` members), `docs/design-system/components/Toggle.md` and `SegmentedControl.md` (48dp touch target note).
- No new permissions or dependencies.
