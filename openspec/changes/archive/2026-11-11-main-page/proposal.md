# Proposal

GitHub issue: #3

## Why

The game needs a home: a screen the player lands on after the splash page, from which a game is started, settings are reached and the app is left. It is also the place the player returns to, so it should feel alive rather than be a static list of buttons.

## What Changes

- Add the main page with two actions: **Take off**, which starts a game, and **Settings**, which opens the settings page.
- Show the current app version in the bottom-right corner.
- Make the system back button the way to quit: it opens a confirmation dialog with **Quit** (closes the app) and **Cancel** (stays on the main page).
- Add an animated background behind the buttons:
  - dark and light stripes in a circular, sunburst pattern that rotates very slowly;
  - three clouds that hang in place and move around a little;
  - random airplanes and helicopters that fly through the screen one at a time, the next one appearing 3 seconds after the previous one has left.
- Hold the background still when the device has reduced motion enabled.

Out of scope: the contents of the settings page (the Settings button opens a placeholder page with a way back until that page has its own issue), a game title or wordmark on this page, menu music, airport selection, and quitting on iOS.

Depends on: the project scaffold (roadmap milestone 1) and a game screen to start. Related: the splash page (issue #2) hands over to this page.

## Capabilities

### New Capabilities

- `main-page`: the main menu of the app: its actions, the version label, quitting through the back button with confirmation, and its animated background.

### Modified Capabilities

None.

## Impact

- `shared/` (`commonMain`, `ui` package): the main page screen, the quit dialog, the backdrop drawing, and a small pure state model for the backdrop animation.
- App screen flow: main page added as the screen after the splash page and the return point from a game and from settings.
- A small platform interface for the app version, closing the app, and back presses, implemented in `androidApp`. It shares the reduced-motion interface introduced by the splash page change.
- `ui/theme`: backdrop timing and motion constants.
- Design system: the page uses existing components (Button, Dialog, Cloud, Aircraft) and the sky sunburst; the MainMenu reference composition is updated to match this page.
- No new third-party dependencies.
