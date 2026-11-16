# Proposal

GitHub issue: #83

## Why

All three levels are open from the first launch, so the player has no reason to master Meadow field before the harder levels. Locking Island airport and Savanna dawn until they are earned gives the game a progression.

## What Changes

- Meadow field is always open. Island airport opens once the player has landed more than 1,000 aircraft in total on Meadow field. Savanna dawn opens once the player has earned 30,000 points or more in total on Island airport.
- Totals are summed over all runs of a level, kept on the device, and updated when a run ends (including a lost run).
- A level, once unlocked, stays unlocked. Players who already have a best score on a level or on a later level keep it unlocked.
- A locked tile on the level selection page cannot be started and shows a lock icon, the unlock condition in words and the progress.
- The thresholds are config values.
- `docs/game-design.md` describes the unlock rules.

## Capabilities

### New Capabilities

- `level-unlocks`: per-level totals, the unlock rules and their persistence.

### Modified Capabilities

- `level-selection`: tiles of locked levels show the locked state and cannot be started.

## Out of scope

- A way to reset progress or unlock everything.
- Counting a run that the system kills before it ends.
- Unlocks for levels beyond the three existing ones, and rewards other than access.
- Redrawing the level card in the design system: this change uses the locked state of the level card planned in the follow-up of #77 and adds only what that lacks (see design).

## Depends on

- The level selection page and the high score store (existing).
- Coordination with the level card work of #77: if its locked state is not yet in the design system, it is added there first.
