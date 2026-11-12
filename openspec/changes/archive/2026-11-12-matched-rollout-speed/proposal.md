# Proposal

GitHub issue: #30

## Why

Every airplane rolls out at a fraction of its own flight speed, so a jet or jumbo that lands just behind a slower airplane catches up with it on the runway and the two collide. The player fails a landing sequence that should be fine.

## What Changes

- All airplanes share one landing speed on the runway: the light prop's speed in the current run. The light prop keeps its own speed after touchdown.
- After touchdown an airplane changes its speed linearly, over 1 second, from its flight speed to the landing speed, then rolls out at the landing speed until the rollout distance is covered and it fades out as today.
- Two airplanes rolling out on the same runway in the same direction never collide with each other. Airplanes rolling out in opposite directions on one runway still collide when their shapes touch.
- The deceleration time and the aircraft type whose speed is the landing speed live in `LandingConfig`, with defaults in `LevelDefaults`. The per-type `rolloutSpeedFraction` and `ROLLOUT_SPEED_FRACTION` are removed.
- `docs/game-design.md` describes the new landing and collision rules. The airplane rollout rule in `CLAUDE.md` and `.github/copilot-instructions.md` mentions the matched landing speed.

Unchanged: rollout distance (two thirds of the runway length), fade-out, helicopters, and the rule that a flying aircraft passes over a landed one.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `landing-zones`: rollout speed is one shared landing speed reached within the deceleration time, instead of a per-type fraction of the flight speed.
- `aircraft-collision`: airplanes rolling out on the same runway in the same direction do not collide with each other.
- `aircraft-altitude-state`: the scenario that two Landed aircraft always collide is narrowed to exclude same-direction rollouts on one runway.

## Out of scope

- Snapping the approach to the runway centre line (issue #29).
- Any change to the rollout distance, the fade or the touchdown point.
- Visual changes: no new effect or animation for braking.
- Tuning the shared landing speed per level beyond picking the reference type.

## Dependencies

None. Works on the engine as it is on `development`; it does not conflict with the active changes.

## Impact

- `domain/AircraftTypeSpec.kt`, `domain/LevelDefaults.kt`, `domain/LevelConfig.kt` (`LandingConfig`), `domain/Aircraft.kt` (`AircraftStatus.RollingOut`).
- `engine/AircraftMotion.kt` (`approach`, `rollOut`), `engine/Collisions.kt`.
- Tests in `commonTest`: `AircraftMotionTest`, `CollisionsTest`, `GameEngineTest`.
- Docs: `docs/game-design.md`, `CLAUDE.md`, `.github/copilot-instructions.md`.
