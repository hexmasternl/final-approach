# Proposal

GitHub issue: #71

## Why

When two aircraft collide, the game freezes and the game ended dialog appears at once, so the player barely sees which aircraft collided or where. A short sequence that isolates the two aircraft before the dialog makes the failure readable.

## What Changes

- On a collision, the two colliding aircraft get a dashed ring in the `danger` colour (red) instead of the `warning` colour, at full size, frozen with the rest of the game.
- Over 1000 ms, everything else (field, zones, paths, other aircraft and their rings, clouds, HUD) fades to a near-black blackout. The two aircraft and their red rings stay fully visible above it.
- The blackout holds for 1000 ms; then the existing game ended dialog (with the ad banner under it) appears over it. Total: 2000 ms from collision to dialog.
- Input on the field stays blocked during the sequence; a tap does not skip it. Back does what it does on the dialog today (main page).
- Reduced motion: no fade; the blacked-out end state shows at once and the dialog follows after the 1000 ms hold.
- Design system: new tokens `blackout` (colour), `dur-collision-fade` and `dur-collision-hold` (1000 ms each); the fade uses the existing `ease-sway` easing. The showcase gets a collision variant of the proximity ring.
- `docs/game-design.md` (Game over) describes the sequence.

## Capabilities

### New Capabilities
- `collision-sequence`: what the player sees between a collision and the game ended dialog: red rings, blackout fade, hold, timing, input and reduced motion.

### Modified Capabilities
- `game-ended-dialog`: after a collision the dialog appears once the collision sequence has finished, not at once. An aircraft leaving the viewport still shows it at once.

## Out of scope

- An aircraft leaving the viewport: keeps today's behaviour (dialog at once, no sequence).
- The tutorial: a collision there restarts the step and never reaches game over, so it gets no sequence.
- Sound for the collision, a crash or poof effect, camera zoom or shake.
- Any change to the engine, collision rules or the dialog content.

## Dependencies

- Builds on the existing `proximity-warning` ring drawing, `level-failure` game-over state (which already names the two colliding aircraft) and the `game-ended-dialog`.
- No dependency on other active changes. `landscape-dialogs` touches the dialog layout only; this change does not alter the dialog itself.

## Impact

- `docs/design-system/tokens.json`, `showcase.html` (and regenerated `ui/theme` via `tools/gen-kotlin.cjs`).
- `shared/.../ui/game/`: `GameScreen.kt`, `ProximityRing.kt`, a new collision sequence timeline and overlay.
- `shared/src/commonTest/.../ui/game/`: tests for the timeline.
- `docs/game-design.md`.
