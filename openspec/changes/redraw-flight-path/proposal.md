# Proposal

GitHub issue: #17

## Why

Players cannot cancel a path or redraw one cleanly (issue #17). Touching an aircraft should clear its old path at once so new input is unambiguous.

## What Changes

- Tapping an aircraft that has a path removes the path; the aircraft keeps its current heading and flies straight.
- Touching an aircraft (tap or start of a drag) immediately hides its previous path.
- Tap-and-drag draws a new path, replacing the old one.

## Capabilities

### New Capabilities
- `flight-path-drawing`: selecting an aircraft, clearing and (re)drawing its path.

### Modified Capabilities

## Impact

- `input` (tap/drag handling), `engine` (clear path on aircraft), `ui` (hide path), `docs/game-design.md`.
- Tests in `commonTest` for clearing and replacing paths.

## Out of scope

- Smoothing of the drawn path (see #16); depends on nothing else.
