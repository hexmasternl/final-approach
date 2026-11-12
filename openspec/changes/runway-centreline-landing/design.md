# Design

## Context

See proposal.md for why. Today `PathDrawing.extend` asks `LandingRules.check` whether the new drag segment validly enters a zone, then ends the path at `hit.entryPoint` and locks it. `AircraftMotion.approach` follows the path; when it is used up, the airplane is put on the path's last point, turned to `zone.direction` and rolls out from there. So touchdown and rollout are on the centre line as soon as the path's last point is.

Numbers that shape the design (defaults): runway width 64, zone width 96 and depth 140, alignment tolerance 30 degrees. Turning radius (speed / turn rate): light prop 34, turboprop 63, jet 111, jumbo 217 world units. The shortest runway (level 3) is 340 long, with a rollout of two thirds of that.

## Goals / Non-Goals

**Goals:**
- The snap is an engine rule, computed once when the path is cleared, in pure Kotlin, unit-testable.
- The lead-in is flyable for every airplane type in the worst valid entry (zone edge, full tolerance angle).

**Non-Goals:**
- Changing `LandingRules` (validity) or the follower's steering.
- Any change for helicopters.

## Decisions

1. **Snap when the path is cleared, in `PathDrawing`.** On a runway hit, `extend` builds the snapped path instead of appending `hit.entryPoint`. Rejected: steering onto the centre line in `AircraftMotion.approach`, because the shown path would not match what the airplane flies and the follower would need landing-specific logic. Rejected: snapping only in the renderer, which would leave the simulation off-centre.

2. **Touchdown point T.** T lies on the centre line of the entered zone at distance `max(touchdownDistance, entryAlong)` from the threshold, where `entryAlong` is the entry point projected onto the runway axis (0 to zone depth). So touchdown is near the threshold and never behind where the path entered, and the rollout ends no farther down the runway than it does today. Rejected: T at the entry point's perpendicular foot only, which for a threshold entry puts T on the threshold edge with no room for a final straight.

3. **The lead-in replaces the end of the drawn path.** Walk back along the drawn path (from the entry point through the earlier points to the airplane's position) a distance `L = leadInTurningRadii × turningRadius`. That point is J; its tangent is the direction of the drawn segment it lies on. Drawn points after J are dropped. If the drawn path is shorter than L, J is the airplane's position and its tangent the airplane's heading. Rejected: a lead-in that starts at the entry point and runs forward along the runway, because the jumbo would need about 430 units of runway before touchdown and then roll off the end.

4. **Curve: one cubic Bezier from J to T.** Control points sit on J's tangent and behind T along the landing direction, each at `leadInHandleFraction × |JT|`. The curve is sampled at `EngineConfig.minPointSpacing`, the same spacing as drawn points, and T is the last point. A numeric check of the worst case (entry at 48 units off-centre, 30 degrees off the landing direction, straight drawn approach) with `leadInTurningRadii = 2.0` and `leadInHandleFraction = 0.4` gives a tightest radius of about 1.19 × the turning radius; 1.5 gives only 0.9. Rejected: a Dubins arc-line-arc path, which is exactly flyable but more code and looks mechanical; rejected: Chaikin smoothing of the joint, which does not guarantee the final heading.

5. **Turning radius from effective specs.** `GameEngine.extendPath` passes the airplane's turning radius from `EffectiveAircraftSpecs` (speed multiplier included) into `PathDrawing.extend`. The lead-in length is per type: short for a light prop, long for the jumbo.

6. **Domain helpers.** `LandingZone.Runway` gets a derived `threshold` (zone centre minus half the depth along `direction`) and a way to get the centre-line point at a distance from it. No stored state changes.

7. **Tunables.** `LandingConfig` gains `touchdownDistance` (default 40 world units), `leadInTurningRadii` (2.0) and `leadInHandleFraction` (0.4), with constants in `LevelDefaults`. No literals in engine code.

8. **Touchdown and rollout code stays.** `AircraftMotion.approach` already places the airplane on the path's last point and rolls out along `zone.direction`; with T on the centre line both follow. Tests prove it; code changes only if a test fails.

9. **No visual change.** The renderer already draws `path.points` with the path colour token and the cut-off flash; the snapped path is drawn the same way. The design system needs nothing new.

## Assumptions

- Helicopters keep ending at the helipad entry point (the issue allows either; unchanged is the smaller change).
- Defaults: touchdown 40 units past the threshold, lead-in of 2 turning radii, handle fraction 0.4.
- When the drawn path is too short for a full lead-in, the lead-in is tighter than the turn rate allows; the airplane follows as best it can and is still placed on the centre line at touchdown.
- Validity is judged on the raw drawn segment, as today; the snap happens only after a valid hit.

## Risks / Trade-offs

- [For the jumbo the lead-in replaces about 430 units of the drawn path, so the player's line visibly changes well before the runway] → `leadInTurningRadii` is tunable; play-test and lower it if it feels wrong, at the cost of a tighter turn.
- [Short drawn path: the lead-in is too tight, the airplane cuts the curve and is moved onto T at touchdown, a visible jump] → Rare (the player draws from the airplane); covered by a test that the airplane still lands on the centre line.
- [`smooth-flight-paths` (#16) also reworks paths and the follower] → Its smoothing pins the last point; whichever change lands second adds a test that a snapped path keeps T and its final heading after smoothing, and applies smoothing before the snap or leaves the lead-in alone.
- [Existing tests expect the path to end at the entry point] → Update them in the same task as the snap.
