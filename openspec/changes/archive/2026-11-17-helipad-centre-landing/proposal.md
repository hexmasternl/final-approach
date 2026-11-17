# Proposal

GitHub issue: #96

## Why

When a helicopter path is dragged into a helipad zone, the path is cut off where it entered the zone: on the edge of the zone circle. The helicopter stops there and runs its landing sequence beside the pad, so it looks as if it landed next to the H instead of on it, and where it lands depends on the side the path came from. Airplanes are already snapped onto the runway centre line; helicopters need the same treatment for the helipad.

## What Changes

- When a helicopter path enters a helipad zone and clears the helicopter to land, the path continues from the entry point to the helipad centre and ends exactly on the centre.
- The final leg is a straight line from the entry point to the centre. The join with the drawn path is smoothed like the rest of the path, so there is no sharp corner. No lead-in like the runway one: helicopters turn fast and the zone is small.
- A path that starts inside the zone (the helicopter is already over the pad when it is touched) goes straight to the centre.
- The clearance moment is unchanged: cut-off, white flash, success sound and ignoring further drag points still happen the moment the path enters the zone.
- The shown path is the finished path, so the player sees the line end on the H. The helicopter follows it and starts its 3-second landing sequence on the centre.
- Airplane runway clearance and the runway lead-in are unchanged. Deciding whether and where a path enters a zone is unchanged.
- `docs/game-design.md` describes the new rule; the core rules in `CLAUDE.md` and `.github/copilot-instructions.md` mention it next to the runway snap.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `landing-zones`: a helicopter path that clears to land ends on the helipad centre instead of at the zone entry point, and the landing sequence takes place on the centre.

## Impact

- Engine: the helipad branch of path drawing (`engine/PathDrawing.kt`). `LandingRules` stays as it is.
- Tests: `PathDrawingTest` in `commonTest`.
- Docs: `docs/game-design.md`, `CLAUDE.md`, `.github/copilot-instructions.md`.

## Out of scope

- Any change to the helipad zone radius, the landing sequence duration or its animation.
- Any change to airplane clearance, the runway lead-in or alignment.
- Visual changes to the helipad or the path drawing style.
