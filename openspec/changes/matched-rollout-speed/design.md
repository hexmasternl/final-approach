# Design

## Context

`AircraftMotion.rollOut` moves a rolling airplane at `spec.speed * spec.rolloutSpeedFraction` (default 0.6). `AircraftStatus.RollingOut` holds the direction, the remaining distance and the fade time, but not the runway or the time since touchdown. `Collisions.find` tests every pair with equal `AltitudeState`, so two Landed airplanes on one runway always collide when their shapes touch. Speeds come from the run's `EffectiveAircraftSpecs`, which already applies level overrides.

## Goals / Non-Goals

**Goals:** one shared landing speed reached within a configurable time; same-direction rollouts on one runway can never fail the level; everything tunable sits in `LandingConfig` / `LevelDefaults`.

**Non-Goals:** braking visuals, changing the rollout distance or fade, path snapping (#29).

## Decisions

### 1. Same-direction safety: skip the pair in `Collisions`, not timing

`Collisions.find` skips a pair when both aircraft are `RollingOut` with the same runway id and the same rollout direction (both in the roll and in the fade phase).

Rejected: relying on speeds and timing. During the 1-second slowdown a jumbo is still faster than a light prop ahead of it, touchdown points vary with the drawn path, a rolled-out airplane stands still while it fades, and level speed overrides can change the order of speeds. No timing argument holds across all of that; the skip does.

Direction match: the stored rollout direction is the zone's landing heading, so two airplanes on the same runway end have exactly the same value. Compare with a small angular tolerance (`Angles` difference below a tenth of a radian, as a named constant in `Collisions`) instead of `==`, so floating-point noise never flips the rule. Opposite ends differ by π and are never matched.

### 2. `RollingOut` carries the runway id and the elapsed rollout time

Add `runwayId: String` and `elapsed: Double = 0.0` (seconds since touchdown) to `AircraftStatus.RollingOut`. `approach` fills `runwayId` from `LandingZone.Runway.runwayId`. The current speed is derived from `elapsed`, not stored.

Rejected: storing the current speed. It would duplicate state that follows from the touchdown speed and the elapsed time, and the touchdown speed is already the type's flight speed.

### 3. Speed profile: linear over the deceleration time, distance integrated exactly

Speed at time `t` after touchdown: `v(t) = vFlight + (vLanding - vFlight) * min(t / decel, 1)`. The distance for a step from `t0` to `t1` is the integral of `v` over that interval (trapezoid over the linear part, constant after), clamped to `remaining`. This keeps the result independent of the step size, so tests with large `deltaSeconds` give the same position as many small steps. `decel = 0` means the landing speed applies at once.

Rejected: `speed * delta` with the speed at the start of the step. It depends on step size and overshoots on large frames.

### 4. Config

- `LandingConfig.rolloutDecelerationSeconds: Double = LevelDefaults.ROLLOUT_DECELERATION_SECONDS` (`1.0`).
- `LandingConfig.landingSpeedReference: AircraftType = LevelDefaults.LANDING_SPEED_REFERENCE` (`AircraftType.LightProp`).
- The landing speed is `specs[landing.landingSpeedReference].speed`, read from the run's effective specs, so a level's speed multiplier and per-type speeds apply to it.
- Remove `AircraftTypeSpec.rolloutSpeedFraction` and `LevelDefaults.ROLLOUT_SPEED_FRACTION`; nothing else uses them.

Rejected: a fixed landing speed in world units per second. It would ignore level speed overrides and drift from the light prop when its speed is tuned.

### 5. Airplanes slower than the landing speed

Only reachable through level speed overrides. Every airplane moves to the one landing speed, so a slower airplane speeds up to it over the deceleration time. The collision skip keeps this safe either way.

## Assumptions

- The landing speed is the light prop's *effective* speed in the run (after level overrides), not the default 55.
- The slowdown is linear over the deceleration time.
- Same-direction rollouts never collide during the fade phase either.
- The rule is per runway: airplanes rolling out on different (crossing) runways still collide.
- A slower airplane (only possible through overrides) speeds up to the landing speed.

## Risks / Trade-offs

- [Light prop rollouts get faster: 55 instead of 33 units/s; jets roll out at about 55 instead of 60] → Rollout distance is unchanged, so rollouts take about the same time or less. Runways do not clog more than today; check in play (task 4.1).
- [A faster airplane visibly overlaps a slower one on the runway for a moment] → Accepted by the issue; both are on the ground and the overlap lasts at most the slowdown.
- [`Collisions` now reads `AircraftStatus`] → It already depends on `Aircraft`; the rule stays in the engine, not in UI.
