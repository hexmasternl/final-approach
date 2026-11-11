# Proposal

GitHub issue: #29

## Why

A path cleared to land stops at the raw point where it entered the runway landing zone, which can be anywhere across the zone's width (96 units on a 64-unit runway). Airplanes then touch down and roll out off-centre, along the edge of the runway or partly off it. All airplanes should land on the runway centre line.

## What Changes

- When an airplane path validly enters a runway landing zone, its end snaps to the runway centre line instead of stopping at the entry point.
- The end of the path becomes a smooth lead-in: it leaves the drawn path tangentially, joins the centre line, and ends at a touchdown point on the centre line heading in the landing direction. The lead-in is long enough for the airplane's turn rate.
- Touchdown is at that point on the centre line, near the threshold; the rollout runs along the centre line.
- The stored path is the snapped path, so the player sees the lead-in where the drawn end used to be.
- When a path is valid stays exactly as today (zone type, alignment tolerance, mid-runway crossings).
- New landing tunables (touchdown distance, lead-in length factor, lead-in curve shape) in `LandingConfig` with defaults in `LevelDefaults`.
- `docs/game-design.md`, `CLAUDE.md` and `.github/copilot-instructions.md` describe the new rule.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `landing-zones`: a valid runway crossing no longer ends the path at the entry point; the path ends with a lead-in onto the runway centre line, and touchdown and rollout are on the centre line.

## Impact

- `domain`: `LandingConfig`, `LevelDefaults`, `LandingZone.Runway` (threshold and centre line derived from the zone).
- `engine`: `PathDrawing` (builds the lead-in), `GameEngine.extendPath` (passes the airplane's effective turning radius). `LandingRules` and `AircraftMotion` keep their logic; touchdown already happens at the path's last point.
- Tests in `commonTest`: `PathDrawingTest`, `AircraftMotionTest`, existing landing tests whose expected end point was the raw entry point.
- Docs: `docs/game-design.md`, `CLAUDE.md`, `.github/copilot-instructions.md`.
- No new dependencies.

## Out of scope

- Helicopters and helipads: behaviour unchanged, the path still ends where it enters the pad zone.
- Which crossings are valid (zone size, alignment tolerance, mid-runway rule).
- General path smoothing and path following (`smooth-flight-paths`, #16), and redrawing a path (`redraw-flight-path`, #17).
- The white cut-off flash: unchanged, it now flashes the snapped path.
- Clamping the rollout to the runway end.

## Dependencies

- None blocking. If `smooth-flight-paths` (#16) lands first or later, its smoothing must keep the lead-in's last point and final heading; see design.md.
