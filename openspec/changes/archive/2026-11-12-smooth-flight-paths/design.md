# Design

## Context

The engine follows a `FlightPath` with per-type speed and turn rate. When a corner demands a tighter turn than the turn rate allows, the aircraft overshoots, then steers back toward a path point behind it and loops. See proposal.md.

## Goals / Non-Goals

**Goals:**
- Smooth in the domain/engine layer (pure Kotlin, unit-testable), not in composables.
- Make following robust even for paths that are still too tight.

**Non-Goals:**
- Changing aircraft speeds or turn rates.
- Visual path styling.

## Decisions

- **Smooth at draw time, per aircraft type.** After resampling to even spacing, apply corner-cutting (Chaikin) iterations and then enforce a minimum curvature radius derived from `speed / turnRate`. Alternative: smooth only at render; rejected because the followed line must equal the smoothed one.
- **Follow by progress along the path.** The follower tracks monotonic arc-length progress and steers toward a lookahead point ahead of the nearest point, never one behind. This prevents the return-to-start loop. Alternative: keep nearest-point targeting; rejected as the cause of the bug.
- **Config, not literals.** Tolerance, lookahead distance and iteration count live in a config data class.

## Risks / Trade-offs

- [Smoothing moves the end off a landing zone] → Pin first and last points; test landing after smoothing.
- [Over-smoothing feels unresponsive] → Limit deviation to a configured tolerance.
