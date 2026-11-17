# Design

## Context

`PathDrawing.extend` asks `LandingRules.check` whether the new drag segment enters a zone. On a hit it smooths `points + hit.entryPoint` and, for a runway, runs the result through `RunwayLeadIn.build`. For a helipad the smoothed path is used as is, so it ends on the zone edge. `PathSmoother` keeps the last point exactly where it was given and rounds the corners in between (Douglas-Peucker, even resampling, Chaikin).

## Decisions

### Append the centre before smoothing

For a helipad hit, smooth `points + hit.entryPoint + zone.area.center` instead of `points + hit.entryPoint`. The smoother keeps the last point exactly, so the path ends on the centre; Chaikin rounds the corner at the entry point like any other corner of the drawn path, so the join has no sharp corner.

Alternatives considered:
- A lead-in like the runway one: not needed. Helicopters turn fast, the zone is small and a helipad has no direction to line up with.
- Appending the straight leg after smoothing: leaves a hard corner at the entry point, which the issue rules out.

The rounded corner may cut a little inside the entry point, so the path can leave the drawn line slightly before the zone edge. That is the same effect smoothing has on every other corner and is acceptable.

### Start inside the zone

`LandingRules.check` already returns the segment start as the entry point when the segment starts inside the circle (fraction 0). With an empty path that start is the helicopter position, so the list handed to the smoother is `[position, centre]` and the path goes straight to the centre. The existing rule in `smooth` that drops leading points level with or behind the nose always keeps the last point, so a centre behind the helicopter still ends the path on the centre. No special case is needed; a test pins it.

### What stays

`LandingRules` is unchanged: it still decides whether and where the path entered the zone. The cut-off, the lock time (which drives the white flash and the clearance sound) and the `ClearedToLand` status are set at the same moment as before. The runway branch is untouched.

## Risks / Trade-offs

- A helicopter whose path enters the zone edge while heading away from the centre turns sharply onto the final leg. Helicopters have a very high turn rate and the path follower has no turn limit, so this is acceptable.
- Recorded runs that replay drag input (seeded baselines) end helicopter paths differently. `SeededRunBaselineTest` may need its expected values refreshed if it covers a helipad landing.
