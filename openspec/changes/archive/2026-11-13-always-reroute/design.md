## Context

`PathDrawing.begin` only resets the path when the status is `Flying`, `PathDrawing.extend` ignores anything not `Flying`, and a cleared aircraft has a locked path (`FlightPath.lockedAt`) with status `ClearedToLand(zone)`. `GameEngine.select` already excludes only `RollingOut` aircraft, so a cleared aircraft can be selected but not redrawn.

## Decisions

### 1. Beginning a path cancels the clearance

`PathDrawing.begin` now accepts `Flying` and `ClearedToLand`: it returns the aircraft with an empty unlocked path and status `Flying`, keeping position and heading. `RollingOut` stays untouched. `extend` and `finish` need no change because the status is `Flying` by then.

Rejected: letting the player draw a second path while the clearance stays until the new path is finished (the old path would stay visible and the aircraft could land on a path the player has already abandoned); a confirmation step (the game is real time, and the issue says "always").

### 2. Cancel on touch, not on first dragged point

The clearance is cancelled when the touch starts, in line with the existing "touch hides the old path" rule, so the behaviour is the same for cleared and uncleared aircraft.

### 3. Helicopter landing sequence is "landed"

The edge of "actually landed" for a helicopter is the moment it reaches the helipad. That keeps this change independent of the animation; once `helicopter-landing-sequence` adds a status for it, that status is excluded from `begin` like `RollingOut`.

### Assumptions

- A cancelled clearance is not undoable.
- Re-routing in the lead-in curve of a runway approach is allowed; the airplane keeps its current heading.
- No score or event is produced by cancelling.
