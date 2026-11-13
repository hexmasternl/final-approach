## Why

An aircraft whose path ends in a landing zone is locked: touching it does nothing and the player cannot change their mind. The player wants to be able to re-route any aircraft until it has actually landed.

GitHub issue: #51

## What Changes

- Every aircraft the player can pick can be given a new path until it has landed. This includes an aircraft cleared to land (on its path to a runway or helipad).
- Touching a cleared aircraft cancels its clearance at once: its old path is hidden and removed, it keeps its heading, and a new drag draws a replacement path. A tap without a drag also cancels the landing and the aircraft flies straight on.
- A new path that enters a valid landing zone clears the aircraft again, as today.
- "Landed" means touchdown for an airplane (it then rolls out and cannot be picked, as today) and the start of the landing sequence for a helicopter (see `helicopter-landing-sequence`).

Out of scope: re-routing after touchdown, undo of a cancelled landing, any confirmation prompt, and a limit on how often an aircraft can be re-routed.

Depends on: nothing. If `helicopter-landing-sequence` lands first, a helicopter in its landing sequence cannot be re-routed.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `flight-path-drawing`: an aircraft cleared to land can be re-routed until it has landed.

## Impact

- `shared` `engine`: `PathDrawing.begin` and `GameEngine.clearPath`/`beginPath` (the status check that blocks cleared aircraft), `GameEngine.select`.
- `shared` `ui/game`: the path and clearance flash drawing must follow the removed locked path.
- `commonTest`: path drawing and engine input tests.
- Docs: `docs/game-design.md` (core rules).
