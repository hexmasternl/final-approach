# Proposal

GitHub issue: #6

## Why

Levels must differ in layout, pacing and aircraft mix without code changes. Today the game design only says "layouts are defined per level as data" and "difficulty rises over time". This change makes that concrete: one level configuration that the engine, the spawner and the UI read, and a spawner that builds up difficulty over time with fair entry trajectories.

## What Changes

- Add `LevelConfig` in `domain`: identity and presentation (id, name, order, Day or Night theme, ambience), airfield (runways with a one-way or two-way mode, helipads), world (world size, spawn margin, minimum separation), run rules (seed, duration or endless, goal) and a difficulty curve.
- Validate a `LevelConfig` and reject an invalid one with a clear message.
- Define a runway's landing-direction rule: a one-way runway accepts one heading, a two-way runway accepts either end.
- Add the spawner in `engine`, driven only by the config: seeded, build-up over time (spawn interval shortens, aircraft types and their weights per phase, maximum aircraft in the air), jittered spawn time and type.
- Add the fair entry rule: aircraft appear just outside a permitted edge, aimed at a random point in a central safe region; entries that only clip a corner, leave soon or have a short chord are re-rolled. No spawn within the minimum separation of another aircraft.
- Carry optional, defaulted fields that later features will use: speed multiplier, scoring (lives and a points multiplier), wind, emergency aircraft flag, tutorial hints.
- Express the first level (issue #5, Meadow Field) as a `LevelConfig`.
- Update `docs/game-design.md` and `docs/architecture.md` with the level config and the spawn rules.

Out of scope: level selection UI and saving progress; a level editor or loading levels from files (config stays Kotlin data, kept serialisation-friendly); acting on wind, emergency aircraft, tutorial hints and scoring bonuses (fields only, or not at all, see design); drawing the level; the landing and collision rules themselves.

Depends on: the scaffold (roadmap milestone 1). Related: issue #5 (first level, uses this model), issue #8 (aircraft types, resolves speed and points overrides from this config), issue #7 (landing rules, consume the runway landing-direction rule).

## Capabilities

### New Capabilities

- `level-config`: the level configuration data, its defaults, validation and the runway landing-direction rule.
- `aircraft-spawning`: how the spawner uses the configuration: seeded, build-up over time, weighted types, fair entry, separation.

### Modified Capabilities

None.

## Impact

- `shared/` `commonMain` `domain`: `LevelConfig` and its parts, `LevelConfigValidator` (or equivalent), the first level definition.
- `shared/` `commonMain` `engine`: `Spawner` and its entry-trajectory logic.
- `shared/` `commonTest`: tests listed in the tasks, including a property-style test over many seeds.
- `docs/game-design.md`, `docs/architecture.md`.
- No UI, no platform code and no new dependencies.
