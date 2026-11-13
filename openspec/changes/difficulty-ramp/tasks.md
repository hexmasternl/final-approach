# Tasks

## 1. Ramp config and rule

- [x] 1.1 Add `WeightCap`, `DifficultyRamp` and `LevelConfig.ramp: DifficultyRamp? = null` in `domain/LevelConfig.kt`, and `RAMP_STEP_SECONDS`, `RAMP_INTERVAL_DROP_SECONDS`, `RAMP_AIRCRAFT_EVERY_STEPS`, `RAMP_WEIGHT_CAPS` in `domain/LevelDefaults.kt` (design decision 4). KDoc each field. Verify: `./gradlew :shared:allTests` compiles and passes.
- [x] 1.2 Extend `LevelConfig.phaseAt` with the closed-form ramp step, clamped at the final step (design decisions 1 to 3). Verify: `LevelConfigTest` cases for no ramp (unchanged), before step 1, interval floor clamp, weights alternating to their caps with no skip, maximum in the air every `aircraftEverySteps`, identical phase at the final step and far beyond it, allowed types equal to the last phase's; `./gradlew :shared:allTests` passes.

## 2. Validation

- [x] 2.1 Add `rampProblems` to `LevelConfigValidator` (design decision 6): positive step and drop, floor positive and not above the last phase's minimum, cap not below the last phase's maximum, aircraft step count at least 1, weight cap types allowed in the last phase, no duplicates, caps not below the last-phase weight. Verify: one `LevelConfigValidatorTest` case per rule asserting a message starting with "The difficulty ramp", and a valid ramp yields no problem.

## 3. Levels

- [x] 3.1 Set the ramps in `domain/Levels.kt`: Meadow Field floor 2.5 / cap 5, Island 2.0 / 6, Savanna 1.5 / 7 with `stepSeconds = 45.0`; leave `busyPhases` and Meadow's phases untouched (design decision 5). Verify: `LevelsTest` asserts each level's timeline from the spec (Meadow at 3:00, 4:00, 5:00, 6:00 and 10:00; Island at 2:00, 3:00, 4:00, 5:00 and 10:00; Savanna at 1:45, 2:30, 3:15, 4:00, 4:45 and 10:00), that all levels validate, that Meadow's configured phases stay at most 3 and its active phase at most 5 at any time, and that the configured phases equal their previous values.

## 4. Spawner and engine

- [x] 4.1 `SpawnerTest`: on Meadow Field with 3 aircraft held, no spawn at 3:30 and a spawn possible at 4:30; spawn intervals after a ramp step lie in that step's range; over many seeds spawns per minute after the final step exceed the last configured phase; two runs with one seed past the final step produce identical spawns; every spawn after the final step is fair and separated. Verify: `./gradlew :shared:allTests` passes with no change to `Spawner.kt` (design decision 7).
- [x] 4.2 Add `internal val difficultyTime` to `GameEngine` and use it where the spawner gets `time - handoverTime` (design decision 8). `TutorialEngineTest`: skip the tutorial at level time T, then `level.phaseAt(engine.difficultyTime)` is the last configured phase at T + 179 s and ramp step 1 at T + 180 s. Verify: `./gradlew :shared:allTests` passes.

## 5. Docs and checks

- [x] 5.1 Update `docs/game-design.md`: replace "Difficulty rises over time: spawn rate and aircraft variety increase." with the ramp rule; add the ramp to the Difficulty curve bullet under Levels and to Build-up; add a short per-level table (ramp start, step, floor, cap, all limits reached at 6:00, 5:00, 4:45); state that times count from the end of the tutorial and the values are for playtesting. Verify: the doc matches the specs and `Levels.kt`.
- [ ] 5.2 Add `DifficultyRamp` to the `domain/LevelConfig.kt` line in `docs/architecture.md`. Verify: read it back.
- [ ] 5.3 Run `./gradlew :androidApp:assembleDebug` and `./gradlew :shared:allTests`. Verify: both succeed.
- [ ] 5.4 Human check (playtest): play each level past its final ramp step and judge whether the final pressure is hard but readable; note especially whether five aircraft on Meadow Field is too many. Record any changed floors or caps in the issue.
