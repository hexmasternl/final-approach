# Proposal

GitHub issue: #5

## Why

The game has a splash page and a main page, but Take off still opens a placeholder. This change makes the game playable for the first time: the Meadow Field airport from issue #5, aircraft that enter from the edges, and the minimum engine and input to steer them to a landing (tap an aircraft, drag a path).

The blocking question on the issue (steering) is answered with option A: this change includes the minimum engine and steering, because no other issue delivers them and the level is not playable without them.

## What Changes

- Add `GameEngine` in `engine`: owns the `GameState` (aircraft, paths, level time, score), exposes it as a `StateFlow`, and advances it in `update(deltaSeconds)`. It hooks in the spawner (#6), the effective aircraft specs (#8) and the landing rules (#7).
- Add path following: an aircraft steers towards the next point of its path at its type's speed and turn rate, consumes the path as it flies, and flies straight on in its last heading when the path ends.
- Add input in `input`: tapping picks the nearest aircraft within the pick radius; dragging from it draws a new path that replaces the old one. Pointer positions are converted from pixels to world units at the boundary.
- Add the game screen in `ui/game`: the world scaled to fit the screen, drawn in the design-system layer order (field with runway, helipad and decoration; paths; ground shadows; aircraft with selection ring).
- Describe the Meadow Field scenery (taxiway, apron, terminal with tower, hangar, windsock, trees) as level data, and draw it with the design-system rules. Add the new scenery drawings to the design system.
- Force landscape while the game screen is shown, through a new `AppHost` capability implemented in `androidApp`; restore the previous orientation on leaving.
- Take off opens the game screen instead of the placeholder. Back returns to the main page.
- Until #9 lands, an aircraft that leaves the viewport is removed and makes room for a new one.
- Update `docs/architecture.md` and `docs/game-design.md`.

Out of scope: collisions, misses as failures, lives and game over (#9); the HUD and score display (#10); level selection and more levels (#14); zone halos while a path is drawn; sound.

Depends on: #6 (level config and spawner), #8 (aircraft types), #7 (landing zones). These are implemented first on the same branch.

## Capabilities

### New Capabilities

- `game-engine`: the engine loop, state ownership and path following.
- `flight-path-input`: selecting an aircraft and drawing its path.
- `first-level`: the Meadow Field level screen, its scenery, the airport fit to the screen and the landscape lock.

### Modified Capabilities

None.

## Impact

- `shared/` `commonMain`: `engine/GameEngine`, `engine/PathFollower`, `domain/FlightPath`, `domain/Aircraft`, `input/`, `ui/game/`, `platform/AppHost`.
- `androidApp`: `AndroidAppHost` implements the orientation lock.
- `shared/` `commonTest`: engine, path following and input tests.
- `docs/architecture.md`, `docs/game-design.md`, `docs/design-system/components/Scenery.md`.
