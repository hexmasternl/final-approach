# Design

## Context

`docs/game-design.md` decides a landing when a path *ends* on a zone. This change moves the decision to the moment a path *enters* a zone, adds direction for runways, and adds rollout. It builds on the level config from issue #6 (runway position, orientation, length, width, mode and heading; helipad position) and the engine layers in `CLAUDE.md`.

## Decisions

**Landing zones are derived, not authored.** `LandingZone` values (centre, shape, landing direction in radians, kind) are built from the `LevelConfig` by a pure function. Helipad: circle at the centre. Runway: a rectangle at each landing end, depth along and width across the axis. One-way gives one zone, two-way gives two. Rejected: storing zones in the level config, because they would duplicate runway data and drift from it.

**Tunables in a `LandingConfig`** (defaults, overridable): helipad zone radius (per helipad override), runway zone depth and width, alignment tolerance (radians), rollout fraction (2/3), fade duration. The rollout speed is a field of the aircraft type config, as a fraction of flight speed. Rejected: a deceleration model, as nothing else needs it and a constant fraction is simpler to test.

**`LandingRules` is pure.** It takes a path segment (previous point to new point) and an aircraft kind and returns a result: no landing, or a landing at an entry point in a zone. It tests the segment against the zone, so fast drags that skip over a small zone are still caught. For runways it also takes the segment direction as the path direction at the zone. Rejected: testing only the sampled points, because a quick drag could jump a zone.

**The entry point ends the path.** The path is truncated at the point where the segment enters the zone and the path is locked. The aircraft keeps the existing path-following and touches down at the end of the truncated path. Rollout distance is measured from the touchdown point and the airplane is turned to the zone's landing direction. Rejected: snapping to the zone centre, which makes the plane jump.

**Aircraft state.** The aircraft gains states `ClearedToLand` and `RollingOut` (with remaining distance and fade progress). `Collisions` includes `RollingOut` aircraft and excludes aircraft that are removed. Helicopters go straight from `ClearedToLand` to removed.

**Flash feedback.** The engine only exposes "path locked at time t". The renderer draws the flash from that. Colour: the `fuselage` token (white); duration: the `dur-pop` token. The design system has no dedicated path-flash token, so both are used as they are and the FlightPath guideline gets a line describing the flash. Reduced motion (read through `AppHost`, as the splash and main page changes do) replaces the flash with a steady change: the path is drawn solid white with the end dot until the aircraft lands. Rejected: a new token, since existing ones fit.

**Architecture.** No game rule in composables, no Android API in `commonMain`, no literals for sizes or timings.

## Assumptions

- The path is cut where it first enters the zone, not at the zone centre.
- Rollout length is measured from the touchdown point, two thirds of the runway length.
- Rollout speed is a fraction of the aircraft type's flight speed.
- The flash uses `fuselage` and `dur-pop`; the author may prefer a new token.
- Reduced motion shows a steady solid white path.
- The path direction at a zone is the direction of the segment that enters it.
- Helicopters do not need alignment.
- Scoring and star burst stay with the landing/scoring work and are out of scope here.

## Risks

- #6 is not implemented yet; the zone derivation needs its types. Tasks start after it merges, or against its agreed model.
