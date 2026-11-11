# Proposal

GitHub issue: #8

## Why

Differing speeds are the core challenge of the game, and landings are what the player is scored on. Both must be defined per aircraft type, as data, so the engine has no magic numbers and levels can tune them.

## What Changes

- Give each of the five aircraft types a fixed speed (world units per second) and a landing point value, as config with defaults. Speed order, slowest to fastest: helicopter, light prop, turboprop, jet, jumbo. Points follow the same order.
- Speed is the same for every aircraft of a type and never changes during a run.
- A level may override speeds (a multiplier and per-type values) and points (a multiplier and per-type values). The effective values are resolved once at level start and stay fixed for that run.
- A successful landing adds the type's effective points to the score. A collision or a miss awards nothing.
- Turn rate stays in the same per-type config; its values are not changed here.
- Update `docs/game-design.md` (aircraft roster, scoring).

Out of scope: speed changes during a run (boost, wind, emergencies); per-instance variation of any kind; bonus scoring (fast landing, alignment, streaks), which is level-config scoring; drawing the score in the HUD and the points popup at the landing spot (the HUD does not exist yet).

Depends on: the level configuration (#6), which carries the overrides, and the landing rules (#7), which decide when a landing happens. Neither is built yet; this change defines the type config and the override resolution so they plug in without rework (see design).

## Capabilities

### New Capabilities

- `aircraft-types`: per-type speed and landing points, level overrides resolved at level start, and scoring of landings.

### Modified Capabilities

None.

## Impact

- `shared/` `commonMain` `domain`: `AircraftType` and its spec data class (speed, turn rate, landing points, radius), the level override type, and the resolved effective specs.
- `shared/` `commonMain` `engine`: `PathFollower` reads the effective speed; scoring reads the effective points.
- `shared/` `commonTest`: tests listed in the tasks.
- `docs/game-design.md`.
- No UI and no new dependencies.
