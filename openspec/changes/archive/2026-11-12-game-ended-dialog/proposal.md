# Proposal

GitHub issue: #12

## Why

When a level is lost, the player needs to see how the run went and be given a clear next step. Today a lost level only stops the simulation; there is nothing that summarises the run or lets the player go on.

## What Changes

- Show a modal dialog when the game has ended, either because two aircraft collided or because an aircraft left the viewport without landing.
- The dialog shows the number of aircraft landed per aircraft type, the total number landed, and the total points.
- The dialog offers two actions: restart the level, or return to the main page.
- The simulation is frozen while the dialog is open.
- Show an AdMob banner ad (unit "after-level-finished", `ca-app-pub-1310013076234295/3427449894`) at the bottom of the dialog, at full screen width directly under the dialog, behind the consent gate from the `level-start-ads` change (GitHub issue #13).
- Collect the per-type landing counts in the game state, so the dialog only displays them.

Out of scope: lives or retries inside a level, a "level completed" or win dialog, highscore storage and a "new highscore" message, sharing results, level selection (the dialog returns to the main page), the interstitial ad before a level starts (handled by the `level-start-ads` change), sound.

Depends on: the failure states from issue #9 (the engine's game-over state with a reason), the score and landing per aircraft type from issues #8 and #10, and the Dialog component of the design system. Related: issue #14 (levels), where the main page may later lead through level selection.

## Capabilities

### New Capabilities

- `game-ended-dialog`: when the end-of-game dialog appears, what it reports, and the actions it offers.

### Modified Capabilities

None.

## Impact

- `shared/` (`engine`, `domain`): `GameState` exposes landed counts per aircraft type next to the score and the game-over reason; unit tests in `commonTest`.
- `shared/` (`ui`): a game-ended dialog composable built from the Dialog component, shown by the game screen as an overlay.
- Game screen flow: restart and return-to-main-page actions wired to the same level restart and main page navigation that the HUD options panel (issue #10) uses.
- `docs/design-system`: a Dialog variant for game ended (stats grid per aircraft type) if the showcase lacks it.
- No new third-party dependencies, no platform interfaces.
