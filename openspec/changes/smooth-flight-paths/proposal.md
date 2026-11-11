# Proposal

GitHub issue: #16

## Why

Aircraft sometimes leave a drawn path at a sharp corner, slide out, then circle trying to rejoin the path and get stuck (issue #16). A turn rate limit makes sharp corners physically unflyable, so the path itself must be made flyable.

## What Changes

- Drawn paths are smoothed when the drag ends (or as points are added) so sharp corners become gentler curves.
- Smoothing never lets a corner be tighter than the aircraft's turn rate can follow; the path stays close to what the player drew.
- Path following keeps the aircraft on the (smoothed) line and never makes it loop back to the start of the path.
- Landing zone endpoints are preserved: a path that ends on a runway/helipad still ends there after smoothing.

## Capabilities

### New Capabilities
- `flight-path-following`: how a drawn path is smoothed and how an aircraft stays on it.

### Modified Capabilities

## Impact

- `domain` (FlightPath), `engine` (PathFollower), `input` (drag-draw), `docs/game-design.md`.
- Unit tests in `commonTest` for smoothing and path following.
- No new dependencies.

## Out of scope

- Changing aircraft speeds or turn rates; visual path styling.
