## Context

`AircraftMotion.approach` removes a cleared helicopter the step its path is empty and emits `GameEvent.Landed`. Statuses are `Flying`, `ClearedToLand(zone)` and `RollingOut`. Collision and proximity warning in `GameEngine` filter by `AltitudeState` and status.

## Decisions

### 1. A new status for the sequence

Add `AircraftStatus.Landing(elapsed: Double)`. When the helicopter's path is empty, `approach` returns the helicopter at the path end with status `Landing(0.0)`, heading kept, no `Landed` event. `AircraftMotion.step` advances `elapsed`; when it reaches `LandingConfig.helicopterLandingSeconds` (default 3.0) it emits `Landed` and removes the aircraft. The aircraft does not move during the sequence.

Rejected: a flag on `ClearedToLand` (mixes two phases and makes the exclusion checks easy to miss); using `AltitudeState.Landed` from the start (a landed aircraft collides with other landed aircraft, which is what must not happen).

### 2. Excluded from collision and warning

Collision candidates and the proximity warning skip aircraft whose status is `Landing`. The warning is skipped too because a ring around something that cannot collide would mislead.

Rejected: only excluding collisions (a red ring with no danger).

### 3. Scoring at the end

`Landed` is emitted when the sequence ends, so score and the landed counters change when the helicopter disappears.

Rejected: scoring on arrival (the player would be paid for a landing that has not happened yet).

### 4. Descent drawing

The renderer draws the sprite shrinking towards the shadow over the sequence, with the scale range and the easing taken from `FaMotion` tokens and a config value, not literals. Reduced motion draws one still pose. The design system has no helicopter descent component; the task checks the showcase first and, if it lacks the descent, adds a note under the Aircraft component before the code uses it.

Rejected: a spinning rotor effect (not asked, and needs new art).

### Assumptions

- The duration is 3 seconds for every helicopter and level, configured in `LandingConfig`.
- The sequence starts where the path ended, which is on the helipad.
- A helicopter in its sequence cannot be re-routed or picked (`always-reroute` agrees).
- The proximity warning also ignores a landing helicopter.
