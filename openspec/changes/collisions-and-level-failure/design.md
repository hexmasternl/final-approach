# Design

## Context

Builds on the engine, aircraft types (#8), level config (#6) and rollout (#7), none of which is in the code yet. `CLAUDE.md` applies: rules in `engine/` and `domain/`, not in composables; no Android APIs in `commonMain`; tunable values in config; no literals in drawing code.

## Decisions

**Collision shape is a small set of circles per aircraft type, in the aircraft type spec (`domain`).** Circle centres are offsets in the aircraft's local frame, rotated by the heading when tested. Rejected: convex polygons, which fit sprites slightly better but need SAT and swept tests; circles make overlap and the swept test simple and still match toy-scale sprites. Each type also carries a bounding radius derived from its circles, used for the broad phase.

**Detection runs in `engine/Collisions` inside `update`, broad phase then narrow phase.** Pairs whose bounding circles do not overlap are skipped; remaining pairs test circle against circle. Rejected: distance between aircraft centres, which is the old minimum-separation rule.

**No tunnelling: test along the frame's movement, and clamp the step as a fallback.** Each aircraft moves in a straight segment during a frame (position at start to end). Relative motion of a pair is a segment; the narrow phase finds whether any shape circle pair comes within the sum of radii during the frame (closest approach of two moving points, with heading treated as that of the frame end). Rejected: only clamping delta time, because it makes simulation cost depend on the worst frame. `GameEngine.update` additionally splits a frame into sub-steps no longer than a config maximum, so the swept assumption of straight movement stays accurate during turns.

**Game over is a field of `GameState`, not a separate screen state.** `GameState.status` gains `GameOver(reason, aircraftIds)`; reasons are `Collision` and `LeftArea`. After it is set, `update` returns without moving anything. The overlay (#12) reads it. Rejected: a callback, because state flows through the existing `StateFlow`.

**Proximity warning is derived each update from centre distance.** `GameState.warnedAircraft` is a set of aircraft ids; an aircraft is in it when another in-play aircraft is within `warningDistance`. Both ends of a pair are included by construction. Warning distance is measured between aircraft centres. Rejected: surface-to-surface distance, an extra shape query for no player-visible gain.

**The warning distance is set in the level config (#6)** as `warningDistance`; the old collision/separation distance is removed there.

**Leaving the viewport uses a per-aircraft `hasEnteredViewport` flag.** It turns true the first update the aircraft position is inside the viewport. An aircraft is lost when the flag is true and its position is outside the viewport expanded by `leaveMargin` (config). Rejected: a spawn grace timer, which is wrong for slow entries.

**The ring is drawn in the effects layer from `warnedAircraft`.** A composable draws a dashed circle with radius = aircraft bounding radius + `FaDimens` ring margin, stroke from line tokens, colour `FaColors.warning`. The spin angle is a continuous transition driven by the frame clock; fade/scale in and out uses `dur-pop` with `ease-bounce` for arriving and `dur-quick` with `ease-out-soft` for leaving. With reduced motion (from `AppHost`) the angle is fixed at 0. Rejected: drawing it from the engine, since it is presentation only.

**Design system gap.** The `warning` colour token exists ("near-miss warning ring"), but there is no ring component, dash pattern or spin duration. Add a "proximity ring" component to the design system with a dash pattern, a margin dimension and a spin duration token (`dur-ring-spin`) before using them from the theme.

## Assumptions

- Spin duration is 4 seconds per turn: slow, as the issue asks, but faster than the 7s selection ring so it reads as urgent. Overrule via `dur-ring-spin`.
- The dashed shape and spin are enough of a non-colour cue; no text or icon is added to the ring.
- The game-over overlay is #12; until it lands, game over only freezes the simulation.
- Warning distance is between centres.
- Warning sound is out of scope.
- Rolling-out aircraft collide; the finer Landed/In Flight rule is #11.

## Risks

- Swept tests on turning aircraft are approximate; sub-stepping bounds the error.
- #11 changes which pairs may collide; the pair filter in `Collisions` is the single place to change.
