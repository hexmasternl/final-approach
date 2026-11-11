## Why

Failure is currently "two aircraft closer than a minimum separation", which feels unfair: aircraft that visibly do not touch still lose the level. Players should lose only when shapes really touch (or an aircraft escapes), and should get a clear visual warning before it happens.

GitHub issue: #9

## What Changes

- Every aircraft type gets a collision shape matching its sprite, rotated with the heading. A collision is shape overlap, not distance.
- The engine detects collisions every update, including fast aircraft with large frame deltas, and ends the level with reason `collision` naming both aircraft.
- The engine exposes a proximity warning (aircraft within a configurable warning distance); the UI draws a spinning dashed red ring on every aircraft involved, with a static variant for reduced motion.
- An aircraft that leaves the viewport after having entered it, without landing, ends the level with reason `left area`.
- The level config exposes a warning distance instead of a collision distance.
- `docs/game-design.md` (failure rules), `CLAUDE.md` (core rules failure line) and the design system (proximity ring) are updated.

## Capabilities

### New Capabilities

- `aircraft-collision`: shape-based collision between aircraft and the resulting lost level.
- `proximity-warning`: engine-derived warning state and the dashed, spinning ring drawn around aircraft that are close.
- `level-failure`: the game-over state, its reasons (collision, left area) and the rule for aircraft that leave the viewport.

### Modified Capabilities

None. There are no main specs yet.

## Out of Scope

- Lives or retries inside a level; a lost level is over.
- Near-miss scoring.
- Collision with terrain or the airfield.
- The game-over overlay itself (issue #12); this change only exposes the game-over state and its reason.
- Altitude states (In Flight / Landed) from issue #11; here, aircraft rolling out on a runway collide, aircraft already removed do not.
- The optional warning sound.

## Dependencies

- #6 level configuration (spawn outside the viewport, viewport size, warning distance field).
- #7 landing zones and rollout (rolling-out aircraft stay in play until removed).
- #8 aircraft types (the collision shape lives in the aircraft type spec).
- The `GameEngine`, `GameState` and aircraft domain types from the game engine work.
- Overlaps with #11, which refines which aircraft can collide; whichever lands second adapts.
