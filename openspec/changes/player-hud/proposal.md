# Proposal

GitHub issue: #10

## Why

While a level is played the player needs to see how they are doing and needs a way to pause, restart or leave. Today the game screen shows nothing but the field.

## What Changes

- Add a HUD over the game screen with:
  - the personal best score for the current level;
  - the current score, which does a short grow-and-shrink pop each time it rises;
  - a counter per aircraft type of how many aircraft have spawned, only for types the level spawns;
  - a cog wheel button in the top-right corner.
- Add the options panel, opened by the cog wheel, which pauses the game and offers **Continue**, **Restart** and **Quit**.
- Remember the best score per level on the device so the HUD can show it.

Out of scope: lives, streak and next-aircraft hint in the HUD; bonus points and score popups on the field; the game-ended dialog (issue #12); sound, music and other settings in the options panel; a quit confirmation; system back button handling during play; how points per type are defined (issue #8).

Depends on: the game engine with scoring (roadmap milestones 4 and 5), points per aircraft type (#8), the level configuration that says which types a level spawns and identifies the level (#6, #5), and the main page as the target of Quit (#3).

## Capabilities

### New Capabilities

- `player-hud`: what the HUD shows during a level: best score, current score with its pop, per-type spawn counters, and the cog wheel.
- `pause-menu`: the options panel: pausing, Continue, Restart, Quit.

### Modified Capabilities

None.

## Impact

- `shared/` (`commonMain`): `ui` package gets the HUD and options panel composables; `engine` exposes spawned counts per type and a pause/reset capability; `domain` level config is read for the types a level spawns.
- `platform`: a small `HighScoreStore` interface (best score per level id), implemented in `androidApp`.
- `ui/theme`: HUD pop scale constant and timings from existing tokens.
- Design system: a spawn-counter chip is added, and the HudBar guideline is updated to allow it.
- No new third-party dependencies.
