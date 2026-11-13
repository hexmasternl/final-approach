## Context

Defaults sit in `DefaultAircraftSpecs` as `spec(speed = ..., turnRate = ..., radius = ...)` calls with the collision circles traced on the sprite in sprite units. Levels bend them through `AircraftTuning` (multiplier and per-type overrides). `LandingConfig.landingSpeedReference` names the light prop, whose speed is the rollout landing speed.

## Decisions

### 1. One `AircraftSpeeds` object

A new `domain/AircraftSpeeds.kt` holds one value per type, each written as the previous value times the author's fraction (for example `LIGHT_PROP = 55.0 * 2.0 / 3.0`), so the origin is visible and a tweak is one edit. `DefaultAircraftSpecs` reads them and holds no speed literal.

Rejected: a per-level override only (the issue asks for a default parameter file); a JSON resource (needs a loader in `commonMain` for no gain).

### 2. Halve the jumbo through its radius

The sprite size is `radius * 2.4` and the collision shape is built with `CollisionShape.fromSprite(spriteSize, circles)`, so halving `radius` (44 to 22) halves both. The traced circles are in sprite units and need no change. `SPRITE_SIZE_PER_RADIUS` stays.

Rejected: scaling only the drawing (the collision would no longer match, which breaks `aircraft-collision`).

### 3. Narrow the ordering rule

The new speeds make the jumbo (65) slower than the jet (66.67). The author's numbers are explicit, so the spec follows them and the ordering and 15 percent rules now cover helicopter to jet.

Rejected: silently bumping the jumbo to stay fastest (contradicts the stated fraction).

### Assumptions (author can overrule)

- "Current speed" means the defaults in `DefaultAircraftSpecs` today (40, 55, 75, 100, 130); level overrides are untouched.
- "Half of its current size" means half the radius, sprite and collision shape, in both dimensions.
- The values are at zoom factor 1; `world-zoom-retune` multiplies speeds and radii by the zoom factor.
- Rollouts slow down because the reference speed (light prop) is lower; the deceleration time is unchanged.

### Open questions (non-blocking)

- The jumbo is now smaller than the light prop (radius 22 against 24) and about as fast as the jet, against the "toy scale" principle (the jumbo is nearly twice the light prop) and the speed table in `docs/game-design.md`. Intended?
- Should the jumbo keep the highest landing points?
