# Proposal

GitHub issue: #93

## Why

Island airport (level 2) opens only after more than 1,000 landings in total on Meadow field, so on the 1,001st. That bar is too high: players stay on the first level too long before they see anything new.

## What Changes

- Island airport opens once the player has landed **500 or more** aircraft in total on Meadow field, summed over all runs. 499 is locked, 500 is open.
- The rule becomes "this many or more", the same form as the Savanna dawn gate, instead of "strictly more than".
- The locked Island card shows the new target: "Land 500 aircraft on Meadow Field to open Island." and "412 of 500 landings".
- Players who already have 500 or more landings on Meadow field find Island airport open after the update, without playing another run. This already follows from how unlocks are judged (from the stored totals each time); the change adds a test for it.
- `docs/game-design.md` describes the new threshold.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `level-unlocks`: the Island airport threshold drops from more than 1,000 to 500 or more landings on Meadow field.
- `level-selection`: the locked tile and the unlock-on-return scenario use the new target.

## Out of scope

- The Savanna dawn gate (30,000 points on Island airport) stays as it is.
- Any other change to how totals are counted or kept.
- Re-locking: a level that is open stays open, as before.
