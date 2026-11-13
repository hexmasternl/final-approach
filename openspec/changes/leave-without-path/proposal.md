# Proposal

GitHub issue: #65

## Why

Today any aircraft that leaves the level without landing ends it, which punishes a deliberate route such as a wide turn near the edge to sequence a fast aircraft behind a slow one. A path means the player is guiding the aircraft, so only an aircraft nobody guides should count as lost.

## What Changes

- An aircraft that has been inside the level and is beyond the leave margin ends the level only when it has no path. An aircraft following a path out and back in before the path ends does not lose the level.
- When an aircraft's path ends while it is beyond the leave margin, the level ends with reason `left area` in that same simulation step.
- Unchanged: aircraft that never had a path (or whose path ended earlier) still lose the level when they leave; aircraft still entering are never counted; the edge and leave margin are the ones used today (the world bounds, which are the engine's viewport, expanded by the leave margin).
- Update the core rule in `docs/game-design.md`, `CLAUDE.md` and `.github/copilot-instructions.md`.

Out of scope:

- An edge marker for an aircraft that is off screen while on a path.
- Clipping, clamping or restyling path parts that lie outside the level; they are drawn as any path is.
- Any change to how a path can be drawn, picked or replaced, to the leave margin value, or to the game-ended dialog copy.
- Aircraft that have never been inside the level but follow a path that keeps them outside (unchanged from today).

Depends on: nothing. It reads the world size and leave margin from level config, so it holds under any zoom from `detailed-scenery`, `larger-level-worlds` or `world-zoom-retune`.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `level-failure`: leaving the level loses it only for an aircraft without a path; a path that ends outside loses it at that moment.

## Impact

- `shared/` `commonMain`: `engine/GameEngine.kt` (the leave check).
- `commonTest`: `engine/GameEngineTest.kt`, a case per row of the rule table.
- Docs: `docs/game-design.md`, `CLAUDE.md`, `.github/copilot-instructions.md`.
