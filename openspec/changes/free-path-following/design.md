## Context

`PathSmoother` flies a virtual aircraft with `turnRate / minRadiusFactor` to bend the drawn line, and `PathFollower` steers at a point `lookaheadTurnFraction * turningRadius` ahead with at most `turnRate * dt` per step. Both derive a turning radius `speed / turnRate`. `PathDrawing` and `RunwayLeadIn` also read it.

## Decisions

### 1. Move along the path by arc length

`PathFollower.step` advances the aircraft `speed * dt` of arc length along its remaining points and sets the heading to the direction of the segment it is on. No lookahead and no turn limit. The path is consumed as the aircraft passes points, as now.

Rejected: keeping the steering follower with a very high turn rate (still lags and overshoots at corners, and keeps a hidden constant); snapping the heading only at path points (heading would jump mid-curve).

### 2. Smoothing is geometric only

`PathSmoother` keeps tolerance filtering, resampling and Chaikin corner cutting, and drops the virtual flight and `minRadiusFactor`. Resampling spacing becomes a fixed distance from `EngineConfig` (`minPointSpacing`-based), not a fraction of the turning radius. Unused `PathSmoothingConfig` fields (`spacingTurnFraction`, `minRadiusFactor`, `flightSubsteps`, `flightLengthFactor`, `lookaheadTurnFraction`) are removed; remaining tunables stay in config, no literals.

Rejected: no smoothing at all (jittery fingers give jagged flight and heading flicker; the issue says the path is smoothed).

### 3. `turnRate` stays only for the runway lead-in

`RunwayLeadIn` sizes its lead-in from `leadInTurningRadii * speed / turnRate`. Removing `turnRate` would change landing geometry, which the issue does not ask for. The field stays, its KDoc says it only sets the lead-in length, and `CLAUDE.md` is reworded ("each has its own speed"). A later change may replace it with a plain lead-in length.

Rejected: deleting `turnRate` and the lead-in scaling here (larger, unrequested change to the landing spec).

### Assumptions

- Corners are rounded by Chaikin only, so heading may change quickly at a tight corner; sprite banking is not modelled.
- Existing `landing-zones` lead-in requirements are unchanged.
