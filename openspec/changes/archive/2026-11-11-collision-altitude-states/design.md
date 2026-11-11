# Design

## Context

Issue #9 makes collisions depend on shape overlap in `engine/Collisions`; issue #7 defines the landing zones and rollout. Neither is implemented yet. This change adds the one rule that links them: only aircraft with the same altitude state collide. All logic lives in `domain` and `engine`; no composable and no platform API is involved.

## Decisions

### Altitude state is a two-value enum on the aircraft
`AltitudeState { InFlight, Landed }` in `domain`, a field of the aircraft state, defaulting to `InFlight` at spawn.
- Rejected: a numeric altitude with a threshold. The issue explicitly asks for a state only; a number invites simulation nobody asked for.
- Rejected: a boolean `landed`. An enum reads better at call sites and leaves room for more states without a signature change.

### Touchdown is set by the landing logic, at the landing zone
The engine sets `Landed` at the moment the landing from #7 is registered: when an airplane reaches the touchdown point just after the start of the runway (the landing zone), not when the path is first cleared to land. The aircraft is In Flight while it follows the path to that point.
- Rejected: Landed as soon as the path is cleared. The airplane is still in the air then, and the issue says altitude changes "once they touched the runway".
- The state never goes back to In Flight.

### Collisions filter pairs by state before the shape test
`Collisions` skips any pair whose states differ, ahead of the broad phase. This keeps the filter in one place and costs nothing.
- Rejected: filtering in the game-over handler after detection. That would still treat the pair as a collision internally.

### Helicopters
A helicopter becomes Landed when it lands on the pad. It is removed by the landing animation, so the state has no further effect. Same enum, no special case.

## Assumptions

- Touchdown (In Flight to Landed) happens at the touchdown point after the start of the runway, which is where #7 registers a landing. If #7 places that point differently, this change follows it.
- Helicopters use the same states and become Landed on touching the pad.
- The proximity warning from #9 is not changed by this change. Whether it should also ignore pairs with different states is an open, non-blocking question; until decided, it follows #9 as written.

## Risks

- #7 and #9 are not built. If they change how landings or collisions are structured, only the two touch points (set state on landing, filter pairs) move.
