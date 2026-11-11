## 1. Type config

- [x] 1.1 Add `AircraftType` (five types) and `AircraftTypeSpec` (speed, turn rate, landing points, radius) in `domain`, with `DefaultAircraftSpecs` holding the defaults from design. Verify: `commonTest` checks every type has speed and points above zero, speed and points ordering helicopter < light prop < turboprop < jet < jumbo, and every neighbour speed ratio at least 1.15.

## 2. Level overrides

- [x] 2.1 Add `AircraftTuning` and `EffectiveAircraftSpecs` with `resolve(base)` in `domain`, plus validation of positive multipliers and values that names the field. Verify: tests for no overrides equals defaults, speed multiplier, per-type override leaving other types alone, points multiplier combined with a per-type override, and rejection of zero and negative values.
- [x] 2.2 Make `EffectiveAircraftSpecs` immutable and resolved once at level start; carry `AircraftTuning` on `LevelConfig` (or hand it to #6 if that lands first, see design). Verify: test that the effective values are unchanged after simulated time passes.

## 3. Engine

- [x] 3.1 Make `PathFollower` read speed from `EffectiveAircraftSpecs`, with no speed on `Aircraft` itself. Verify: tests that two aircraft of one type have exactly equal speed and cover equal distance in equal time steps, on a path and straight on after it ends.
- [x] 3.2 Add the landing score step: a landing adds the type's effective points, collision and miss add nothing. Verify: tests that a helicopter awards the least and a jumbo the most, that overridden points are used, and that collision and miss leave the score unchanged. Needs the landing and failure events from #7 and the engine; use fakes if they are not built yet.

## 4. Docs

- [x] 4.1 Update `docs/game-design.md`: aircraft roster with the speed order and the points per type, the level overrides, and the scoring rule that only landings score. Verify: the doc matches the specs and the default values in code.
- [x] 4.2 Run `./gradlew :shared:allTests` from `app/src`. Verify: all tests pass.
