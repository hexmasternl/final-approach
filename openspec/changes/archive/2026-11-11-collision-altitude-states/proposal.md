# Proposal

GitHub issue: #11

## Why

An aircraft that has touched down is on the ground; one still flying can pass over it. Without that distinction, a flying aircraft crossing a landed airplane rolling out on a runway would count as a collision, which looks and feels wrong.

## What Changes

- Give every aircraft an altitude state: **In Flight** or **Landed**. It is a state only, not a numeric altitude.
- Every aircraft spawns In Flight. It becomes Landed when it touches the runway (an airplane: just after the start of the runway, at the landing zone).
- Collision detection only considers two aircraft that have the same altitude state. An In Flight aircraft can cross over a Landed one without colliding.
- Two In Flight aircraft can collide, and two Landed aircraft can collide. On a two-way runway, two airplanes rolling out on the same runway are both Landed and therefore collide.

Out of scope: a numeric altitude or any altitude simulation, a visual change for descending or landed aircraft (such as shadow or size), changing the collision shapes or the proximity warning, and the landing zones and rollout themselves.

Depends on: the landing zones and rollout (issue #7), which decide when a landing happens, and the collision detection (issue #9), which this refines. Neither has an implemented change yet; this change defines the rule they must apply.

## Capabilities

### New Capabilities

- `aircraft-altitude-state`: the In Flight / Landed state of an aircraft, when it changes, and the rule that only aircraft with the same state can collide.

### Modified Capabilities

None.

## Impact

- `shared/` `domain`: an altitude state type on the aircraft.
- `shared/` `engine`: `LandingRules`/`GameEngine` set the state on touchdown; `Collisions` filters pairs by state.
- `commonTest`: unit tests for the state change and the pair filter.
- `docs/game-design.md`: the failure rules mention altitude states.
- No UI, design-system or platform change. No new dependencies.
