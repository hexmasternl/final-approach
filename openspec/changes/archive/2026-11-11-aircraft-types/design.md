## Context

Speed and points are per-type data. The level configuration (#6) and landing rules (#7) are not built yet, so this change defines the type config and the override resolution as standalone domain code that `LevelConfig` and `LandingRules` consume. `CLAUDE.md` rules apply: no game rules in composables, no literals in the engine, `commonMain` is pure Kotlin, units are world units and seconds.

No UI is touched, so no design-system tokens are needed. The score is only exposed in game state; drawing it (score text style, points popup) belongs to the HUD work.

## Decisions

### Base config as a data class per type
`AircraftType` (enum of the five types) maps to an `AircraftTypeSpec` data class: `speed`, `turnRate`, `landingPoints`, `radius`. Defaults live in one `DefaultAircraftSpecs` object. Turn rate and radius keep their current values; only speed and points are specified here.

Defaults (assumption, tunable): speed 40, 55, 75, 100, 130 world units/s for helicopter, light prop, turboprop, jet, jumbo; points 10, 20, 30, 50, 80. They satisfy the ordering and the 15% gap rule (smallest ratio 1.30).
- Rejected: fields on the enum entries. Specs must be replaceable by a level and by tests.

### Overrides as a separate value, resolved once
`AircraftTuning(speedMultiplier = 1.0, speedOverrides: Map<AircraftType, Double> = emptyMap(), pointsMultiplier = 1.0, pointsOverrides: Map<AircraftType, Int> = emptyMap())`, carried by `LevelConfig` (#6). `AircraftTuning.resolve(base): EffectiveAircraftSpecs` runs once at level start and returns an immutable value. The engine receives `EffectiveAircraftSpecs` and never the base config or the tuning.
- Per-type override replaces the base value, then the multiplier applies to the result (assumption). So `jet override 90` with multiplier 1.5 gives 135.
- Effective points are `round(value * multiplier)` as an `Int`, with a minimum of 1.
- Rejected: reading the level each frame. Resolving once makes "constant for the run" true by construction.

### Where the values are read
`PathFollower` takes the aircraft's speed from `EffectiveAircraftSpecs` for its type. An aircraft holds no speed field of its own, so per-instance variation cannot exist. The engine's scoring step adds `effective[type].landingPoints` when `LandingRules` (#7) reports a landing; collision and miss paths do not touch the score.
- Rejected: copying speed onto each `Aircraft`. It invites drift and variation.

### Validation
Multipliers and per-type values must be greater than zero. Validation runs where #6 validates the level and reports the field name. Ordering of speeds and points is a property of the defaults, tested there; level overrides are not required to keep the ordering (assumption), so a level may bend it on purpose.

### Dependency order
If this change lands before #6, it adds `AircraftTuning` as a standalone type with its own validation function and #6 adds the field to `LevelConfig`. If #6 lands first, this change adds the field. The engine takes `EffectiveAircraftSpecs` as a parameter either way.

## Assumptions
- Default numbers above.
- Speed gap of at least 15% between neighbouring types is the testable meaning of "visible in play".
- The issue's order puts the jumbo faster than the jet. `docs/game-design.md` currently says jumbo is "medium-fast" and the jet "fast"; the doc is updated to the issue's order.
- Override precedence (override, then multiplier) and the rounding of points.
- Level overrides need not keep the type ordering; they only need positive values.
- The HUD score display and landing popup are not part of this change.

## Risks
- Jumbo faster than jet is unusual in real life; it is what the issue asks for, and the jumbo's very low turn rate balances it.
