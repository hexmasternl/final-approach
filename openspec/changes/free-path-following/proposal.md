## Why

Aircraft are bound by a minimum turn radius: the drawn path is bent to fit it and the aircraft steers towards it with a limited turn rate, so it cuts corners and lags behind what the player drew. The player wants the aircraft to fly exactly the line they drew.

GitHub issue: #47

## What Changes

- Remove the turn-radius limit from path drawing and path following. The drawn path is still smoothed (finger jitter removed, corners rounded) but no longer bent to a turning radius.
- An aircraft moves along its smoothed path at its type's speed with its heading always along the path, so it stays on the line through every corner.
- After the path ends it flies straight on in its last heading, as before.
- The type's `turnRate` stays in the aircraft spec only as a tuning value for the length of the runway lead-in; it no longer limits any turn.

Out of scope: changing the smoothing look beyond dropping the radius limit, landing rules, aircraft speeds, and the visual banking of sprites.

Depends on: nothing. Touches the same engine files as `world-zoom-retune` and `aircraft-speed-tuning` (reach radius, speed); they do not conflict.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `flight-path-following`: smoothing no longer enforces a turn radius; the aircraft follows the path exactly.

## Impact

- `shared` `engine`: `PathFollower`, `PathSmoother`, `PathDrawing`, `EngineConfig` (`PathSmoothingConfig`), `RunwayLeadIn` call site.
- `commonTest`: path-following, smoothing and landing tests.
- Docs: `docs/game-design.md`, `CLAUDE.md` and `.github/copilot-instructions.md` (core rules mention a turn rate), `docs/architecture.md` if it describes turning.
