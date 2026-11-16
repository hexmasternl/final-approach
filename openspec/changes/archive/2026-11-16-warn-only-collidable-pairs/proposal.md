# Proposal

GitHub issue: #73

## Why

The proximity warning ring appears for every two aircraft that are close, including pairs that cannot collide: two airplanes rolling out on the same runway in the same direction, and a landed airplane with another aircraft flying over it. The ring then warns of a danger that does not exist, and the player learns to ignore it.

## What Changes

- The engine marks two aircraft as warned only when they are within the warning distance **and** the collision rules allow that pair to collide.
- No warning for a pair of airplanes rolling out on the same runway in the same direction (fading ones included).
- No warning for a pair with different altitude states: one Landed, one In Flight.
- Still a warning for two In Flight aircraft, for two airplanes rolling out on the same runway in opposite directions, and for two airplanes rolling out on different runways (for example where they cross).
- An aircraft keeps its ring as long as at least one aircraft it can collide with is within the warning distance, whatever else is near it.
- A helicopter in its landing sequence keeps having no ring and causing none (today's behaviour, now written into the spec).
- The ring itself (look, spin, transition, reduced motion) and the warning distance do not change.
- `docs/game-design.md`, `CLAUDE.md` and `.github/copilot-instructions.md` describe the narrowed rule.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `proximity-warning`: a pair is warned only when it is within the warning distance and can collide; the ring requirements are reworded to follow the warned state instead of distance alone.

## Out of scope

- Any change to the collision rules themselves (`aircraft-collision`, `aircraft-altitude-state`).
- Predicting collisions from headings, paths or closing speed: two In Flight aircraft flying apart are still warned while they are close.
- A warning for an aircraft on final approach towards an airplane rolling out towards it; that pair is only warned from touchdown on (see design, risks).
- The warning distance, the ring's look, colour, motion and transition, and the red rings of the collision sequence.

## Dependencies

- Builds on the pair rules already in `aircraft-collision` and `aircraft-altitude-state`; both are implemented.
- No dependency on other active changes. `collision-sequence` (#71) recolours the rings of two aircraft that have collided and does not touch which aircraft are warned.

## Impact

- `shared/.../engine/Collisions.kt`: the pair rule becomes one shared function.
- `shared/.../engine/GameEngine.kt`: `warned` uses it.
- `shared/src/commonTest/.../engine/`: `CollisionsTest.kt`, `GameEngineTest.kt`.
- `docs/game-design.md`, `CLAUDE.md`, `.github/copilot-instructions.md`.
- No UI, theme or design-system change: the UI already draws rings from `GameState.warnedAircraft`.
