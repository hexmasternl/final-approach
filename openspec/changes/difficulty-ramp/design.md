# Design

## Context

`LevelConfig.phaseAt(levelTime)` returns the last `DifficultyPhase` that has started; `Spawner.update` asks for it on every due spawn and uses its interval range, weights and `maxAircraft`. `GameEngine` passes `time - handoverTime` to the spawner, so the curve already counts from the end of the tutorial. After the last phase nothing changes. Levels 2 and 3 share `busyPhases(maxAircraft)` in `domain/Levels.kt`; Meadow Field has its own three phases. The issue's per-level timelines were checked against `Levels.kt`: the opening phases (Meadow 0/45/120 s, Island and Savanna 0/60 s, caps 3/4/5) and every derived step match.

## Goals / Non-Goals

**Goals:** pressure keeps rising after the last phase up to per-level limits; the ramp is level data with defaults in `LevelDefaults`; seeded replays unchanged; no change to the spawner's rules.

**Non-Goals:** blended difficulty, speed changes, HUD display of the ramp, final tuning.

## Decisions

1. **The ramp is computed inside `phaseAt`, as a derived `DifficultyPhase`.** `phaseAt(t)` returns the configured phase as today when there is no ramp or `t` is before the first step; otherwise it builds the phase for step `n = floor((t - last.startSeconds) / stepSeconds)`, clamped to the final step. The spawner, HUD and tests keep reading one phase type. *Rejected:* expanding the ramp into extra entries in `phases` at config time (the list would be unbounded or need a precomputed length, and validation of configured phases would mix with derived ones); a separate ramp lookup in `Spawner` (the rule would live in the engine instead of the level data, and two places would answer "what is active").

2. **Closed form, no iteration, clamped at the final step.** For step `n`: `min = max(floor, last.min - n * drop)`, `max = min + (last.max - last.min)`; `maxAircraft = min(cap, last.maxAircraft + n / aircraftEverySteps)`; for weight cap `i` of `k` entries, raises = number of steps `s` in `1..n` with `(s - 1) % k == i`, i.e. `if (n > i) (n - 1 - i) / k + 1 else 0`, weight `= min(cap, base + raises)`. The final step is the largest of: `ceil((last.min - floor) / drop)`, `(cap - last.maxAircraft) * aircraftEverySteps`, and per weight cap with need `d > 0`: `i + 1 + (d - 1) * k`. Clamping `n` to it makes the returned phase (including `startSeconds`) identical forever after, so "stays at final pressure" is testable with `assertEquals`. Use a small epsilon (1e-9) in the floor and ceil to absorb floating-point error. *Rejected:* looping step by step from the last phase (needless work per spawn, and unbounded for long runs).

3. **The rotation does not skip capped types.** Step `n` always targets weight cap `(n - 1) % k`; if that type is at its cap the step raises no weight. This matches the issue ("odd steps jet, even steps jumbo") and Savanna's step 5 (jet already 4, nothing changes but the interval). *Rejected:* skipping to the next uncapped type (changes which step raises what, and the issue's timeline).

4. **Config shape.** In `domain/LevelConfig.kt`:

   ```kotlin
   data class WeightCap(val type: AircraftType, val maxWeight: Int)
   data class DifficultyRamp(
       val intervalFloorSeconds: Double,
       val maxAircraftCap: Int,
       val stepSeconds: Double = LevelDefaults.RAMP_STEP_SECONDS,
       val intervalDropSeconds: Double = LevelDefaults.RAMP_INTERVAL_DROP_SECONDS,
       val aircraftEverySteps: Int = LevelDefaults.RAMP_AIRCRAFT_EVERY_STEPS,
       val weightCaps: List<WeightCap> = LevelDefaults.RAMP_WEIGHT_CAPS,
   )
   ```

   and `LevelConfig.ramp: DifficultyRamp? = null`. `weightCaps` is an ordered list rather than the issue's map because its order is the rotation; a `Map` would make the rotation depend on insertion order. Floor and cap have no default: they are the per-level tuning and a silent default would hide a missing choice. `LevelDefaults`: `RAMP_STEP_SECONDS = 60.0`, `RAMP_INTERVAL_DROP_SECONDS = 0.5`, `RAMP_AIRCRAFT_EVERY_STEPS = 2`, `RAMP_WEIGHT_CAPS = listOf(WeightCap(Jet, 4), WeightCap(Jumbo, 3))`. `spawnedTypes` is unchanged because the ramp never adds a type.

5. **Levels.** `meadowField.ramp = DifficultyRamp(intervalFloorSeconds = 2.5, maxAircraftCap = 5)`; Island `DifficultyRamp(2.0, 6)`; Savanna `DifficultyRamp(1.5, 7, stepSeconds = 45.0)`. `busyPhases` stays shared and untouched; each level sets its own ramp.

6. **Validation** in `LevelConfigValidator`, a new `rampProblems` next to `curveProblems`, run only when `ramp != null` and phases exist, messages starting with "The difficulty ramp" (for example "The difficulty ramp floor of 4.5s is above the last phase's minimum interval of 4.0s"). Rules as in the spec. A weight cap for a type the last phase lacks is rejected rather than silently ignored, so a typo cannot pass.

7. **Spawner unchanged.** It already calls `config.phaseAt(levelTime)` per due spawn. An interval drawn before a step starts is not shortened when the step begins; the next draw uses the new range. A `pendingType` kept from an earlier phase stays valid because the ramp never removes types. Fair entry and separation run on every candidate as before.

8. **Testing the tutorial offset.** `GameEngine` gets `internal val difficultyTime: Double` (level time minus `handoverTime`, the value it already passes to the spawner), so `TutorialEngineTest` can assert `level.phaseAt(engine.difficultyTime)` after a skip at time T. *Rejected:* asserting it through spawned aircraft counts (needs minutes of simulated play without collisions or departures; slow and flaky).

## Risks / Trade-offs

- [Five aircraft on Meadow Field's one runway and one helipad may be too many] The caps are data; playtesting can lower them without code changes.
- [Island has a runway that refuses jumbos, and jumbos become more common] Accepted as part of the challenge; recorded as an open question in the issue.
- [Merge conflicts in `Levels.kt`, `LevelDefaults.kt`, `docs/game-design.md` with `detailed-scenery`, `larger-level-worlds`, `world-zoom-retune`] Textual only; rebase on `development` before the pull request.

## Assumptions

- The numbers in the issue's settings table are the chosen defaults and a starting point for playtesting.
- The ramp starts at the last configured phase's start time; step 1 comes one step time later.
- The range keeps the width of the last phase (3 s on every level today).
- Speeds stay fixed per type.
- No UI shows the ramp step.
- An interval drawn before a step starts is not redrawn when the step begins.
