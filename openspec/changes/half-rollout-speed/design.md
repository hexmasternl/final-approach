## Context

`AircraftMotion.rolloutDistance` integrates a speed that changes linearly from the airplane's flight speed to the landing speed over `LandingConfig.rolloutDecelerationSeconds`, then stays there. The landing speed is `specs[landing.landingSpeedReference].speed`, the effective flight speed of the reference type (light prop by default), so the light prop today never slows down. The rollout length is `runway.length * rolloutFraction` (two thirds), fixed per zone. Same-direction rollouts on one runway are excluded from collisions by rule (`Collisions`), not by their speeds.

The author answered the earlier question with "the speed of every aircraft on the ground", meaning the rollout after touchdown, not the flight speed.

## Goals / Non-Goals

**Goals:** halve the rollout speed for every airplane, as one tunable value, without touching flight speeds or rollout length.

**Non-Goals:** flight speeds (`aircraft-speed-tuning`), helicopters, touchdown point, fade, deceleration time.

## Decisions

### 1. A factor on the reference speed, not an absolute speed

Add `landingSpeedFactor: Double = LevelDefaults.LANDING_SPEED_FACTOR` to `LandingConfig`, with `LANDING_SPEED_FACTOR = 0.5` in `LevelDefaults`, documented like the other landing values. `rolloutDistance` uses `landed = specs[landing.landingSpeedReference].speed * landing.landingSpeedFactor`. The integral stays exact, so step-size independence holds.

Rejected: a fixed landing speed in world units per second (for example 27.5). It would stop following the level's `AircraftTuning`, the configured reference type and the world zoom, and it would not compose with `aircraft-speed-tuning`.

Rejected: changing the reference type to the helicopter (about half the light prop). It ties the rollout to an unrelated aircraft and is only roughly a half.

### 2. Keep the rollout length

The rollout stays two thirds of the runway, so a rollout lasts about twice as long. The length is a runway property that the issue does not mention, and keeping it leaves touchdown, fade and runway layout untouched.

Rejected: halving the rollout fraction too, which would keep the duration but change how the landing looks, beyond what was asked.

### 3. The light prop slows down too

With the factor below 1 the light prop's flight speed is above the landing speed, so it now decelerates over the same 1 second like the other airplanes. No special case.

### 4. Same-direction rollouts still do not collide

The collision exclusion is a rule, unchanged. Only its stated reason in the docs changes: all airplanes reach one shared landing speed, rather than "the light prop's speed".

### Assumptions (author can overrule)

- "Ground speed" means the rollout after touchdown of airplanes; helicopters have no ground movement and are unaffected.
- "Reduced by half the current speed" means the landing speed is half the reference type's current effective flight speed (factor 0.5), not an absolute value.
- The rollout length stays two thirds of the runway; a rollout takes longer (the earlier question 2 was not answered, this is the default).
- The deceleration time stays 1 second.
- With `aircraft-speed-tuning` also applied, the rollout ends up at one third of today's speed (half of the reduced light prop speed). Order of applying the two changes does not matter.

## Risks / Trade-offs

- Rollouts take about twice as long, so a runway is occupied longer and an opposite-direction landing on a two-way runway is more likely to collide. This is a gameplay consequence of the request; the human play check covers it.
- A longer rollout keeps landed airplanes on screen longer. The spawn cap counts only airborne aircraft (`GameEngine` passes `aircraft.filter(::isAirborne)` to the spawner), so spawning is not slowed.
