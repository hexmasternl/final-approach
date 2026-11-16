# Proposal

GitHub issue: #72

## Why

Every level stops getting harder at its last difficulty phase (120 s on Meadow Field, 60 s on Island and Savanna) and then plateaus for as long as the endless run lasts, so a good player can keep going forever at a pace they have mastered. Difficulty should keep rising for the whole run, so every run ends and a high score means something.

## What Changes

- A level can carry an optional **difficulty ramp** next to its phases. Without one, a level behaves exactly as today.
- The configured phases stay as the opening. From the start of the last phase, every `stepSeconds` the ramp adds one **ramp step**: one more phase derived from the previous one. Phases still step and do not blend, and the ramp draws no random numbers, so seeded runs still replay exactly.
- Each step: the spawn interval minimum and maximum drop by `intervalDropSeconds` (range width kept) until the minimum reaches the level's floor; the next type in the ramp's weight list (jet on odd steps, jumbo on even steps) gains 1 weight up to its cap; every `aircraftEverySteps` steps the maximum in the air goes up by 1 up to the level's cap. Once all limits are reached, the level stays at that final pressure.
- The ramp never adds a type that the last phase does not allow, and never lowers a value.
- `LevelDefaults` holds the shared defaults (step 60 s, drop 0.5 s, every 2 steps, jet cap 4, jumbo cap 3). Each level sets its floor and cap: Meadow Field 2.5 s / 5, Island 2.0 s / 6, Savanna 1.5 s / 7 with 45 s steps.
- `LevelConfigValidator` checks the ramp.
- The ramp counts from the end of the tutorial, as the difficulty curve already does.
- `docs/game-design.md` describes the ramp and the per-level settings.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `level-config`: the difficulty curve gains an optional ramp after the last phase (fields, step rule, limits, defaults), validation covers the ramp, and the first level's three-aircraft limit applies to its configured phases while its ramp may reach five.
- `aircraft-spawning`: spawn pressure keeps rising after the last configured phase, per level, with the replay, fair entry and separation rules unchanged.

## Out of scope

- Continuous (blended) difficulty; the ramp steps like the phases.
- Changing aircraft speeds over time; speeds stay fixed per type.
- Changing the configured opening phases of any level.
- Adding new aircraft types through the ramp, or per-runway jumbo handling on Island.
- Showing the ramp step in the HUD or any other UI.
- Final tuning: the numbers are a starting point for playtesting.

## Dependencies

- Builds on the existing difficulty curve (`DifficultyPhase`, `phaseAt`), the spawner and the tutorial hand-over offset.
- No dependency on other active changes. `detailed-scenery`, `larger-level-worlds` and `world-zoom-retune` also edit `domain/Levels.kt` and `domain/LevelDefaults.kt` (layouts, zoom) and `docs/game-design.md` is edited by several changes; expect only textual merge conflicts, no conflicting behaviour.

## Impact

- `shared/src/commonMain/.../domain/`: `LevelConfig.kt` (`DifficultyRamp`, `WeightCap`, `ramp` field, `phaseAt`), `LevelDefaults.kt`, `LevelConfigValidator.kt`, `Levels.kt`.
- `shared/src/commonMain/.../engine/GameEngine.kt`: an internal accessor for the difficulty time, for tests only.
- `shared/src/commonTest/`: `LevelConfigTest`, `LevelConfigValidatorTest`, `LevelsTest`, `SpawnerTest`, `TutorialEngineTest`.
- `docs/game-design.md`, `docs/architecture.md` (one line on `DifficultyRamp`).
