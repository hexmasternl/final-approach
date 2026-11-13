# Proposal

GitHub issue: #67

## Why

Players watch traffic and plan without touching the screen. With a short screen timeout the device dims or locks mid-level while aircraft keep flying in the dark. The game should keep the screen on while a level runs, and let the player turn that off.

## What Changes

- While a level is running (not paused, not over, not waiting for the interstitial ad), the screen does not time out. Everywhere else the device's normal timeout applies.
- A "Keep screen on / While a level is playing" toggle on the settings page, in a new Gameplay section. On by default, saved with the other settings, survives a restart.
- With the toggle off, the normal timeout applies during a level too.
- Keeping the screen on uses the window's keep-screen-on flag only. No `WAKE_LOCK` permission, no wake locks. The `WAKE_LOCK` that the ads SDK's dependencies currently merge into the manifest is removed.
- A new `AppHost` member, `keepScreenOn(on: Boolean)`, implemented in `androidApp`.
- `docs/game-design.md` (settings) and `docs/architecture.md` (the new `AppHost` member) are updated.

## Capabilities

### New Capabilities

- `keep-screen-on`: when the screen is kept on during play, the setting that controls it, and the no-permission constraint.

### Modified Capabilities

None. The settings page itself is introduced by the change for #66; this change adds a row to it, specified here.

## Out of scope

- Keeping the device awake in the background, or any behaviour while the app is not in front.
- Changing the setting from inside a level (settings open from the main page only).
- iOS: the `AppHost` member is defined in common code; the iOS host implements it when iOS is added.
- Settings for screen brightness or orientation.

## Depends on

- #66, the settings page and its settings store. This change adds one row and one stored value to them, and must be implemented after #66's change is merged.

## Impact

- `shared/.../platform/AppHost.kt` (new member), `commonTest` `FakeAppHost`.
- `shared/.../ui/game/GameScreen.kt` (applies the rule), a small rule function under `ui/game` or `engine`, the settings page and settings store from #66.
- `androidApp/.../AndroidAppHost.kt` (window flag).
- `docs/game-design.md`, `docs/architecture.md`.
