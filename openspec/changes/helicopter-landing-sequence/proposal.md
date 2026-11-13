## Why

A helicopter that reaches its helipad is removed at once, with no sense of a landing. It should descend and touch down over a short sequence, and during that time it must not be able to collide with other aircraft, so the player is not punished for a landing already in progress.

GitHub issue: #53

## What Changes

- When a cleared helicopter reaches the end of its path on the helipad, it starts a landing sequence of 3 seconds in which it descends and lands on the pad.
- During the sequence the helicopter cannot collide with any other aircraft, and no other aircraft can collide with it.
- When the sequence ends the helicopter has landed: the landing is counted and scored, and the helicopter is removed.
- The duration is a configuration value (3 seconds by default).

Out of scope: sequences for airplanes, sound, scoring changes, landing on a pad that is occupied by another helicopter, and any change to how a helicopter is cleared to land.

Depends on: nothing. Coordinates with `always-reroute` (a helicopter in its sequence cannot be re-routed).

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `landing-zones`: a cleared helicopter lands through a timed landing sequence instead of being removed on arrival.
- `aircraft-collision`: an aircraft in a landing sequence cannot collide.

## Impact

- `shared` `domain`: `AircraftStatus` (a landing status with elapsed time), `LandingConfig` (duration).
- `shared` `engine`: `AircraftMotion.approach`, collision and proximity-warning filters in `GameEngine`.
- `shared` `ui/game`: aircraft drawing for the descent (sprite and shadow), with reduced-motion handling.
- `commonTest`: sequence timing, collision exclusion, scoring on completion.
- Docs: `docs/game-design.md`.
