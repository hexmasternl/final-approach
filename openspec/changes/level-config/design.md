# Design

## Context

Architecture from `CLAUDE.md` applies: game rules out of composables, `commonMain` uses only `kotlin.*`/`kotlinx.*`, units are world units, units per second and radians, tunables in config. `domain/` today holds only `AircraftType`. The engine, spawner and landing rules do not exist yet, so this change defines the model and the spawner as pure, testable logic that the engine will call.

Related changes: `aircraft-types` (#8) resolves per-type speed and points, reading the overrides from the level config; landing rules (#7) consume the runway landing-direction rule; the first level (#5) uses the first level config.

## Goals / Non-Goals

**Goals:**

- One `LevelConfig` the whole game reads; levels differ by data only.
- A spawner that is deterministic for a seed, builds up difficulty and never produces an unfair entry.

**Non-Goals:**

- Level selection, progress storage, a level editor, loading from files.
- Acting on wind, emergency aircraft, tutorial hints and scoring bonuses.

## Decisions

**Plain data classes in `domain`, defaults in a config object.** `LevelConfig`, `RunwayConfig`, `HelipadConfig`, `WorldConfig`, `DifficultyPhase`, `SpawnRules`, `ScoringConfig` and friends are immutable `data class`es with `val`s and no logic beyond validation helpers. Global defaults (separation, spawn margin, minimum time on screen, minimum chord fraction, safe region, angle jitter, maximum re-rolls, landing tolerance) live in `LevelDefaults`. Rejected: one flat class with every field, which is hard to read and to default; a DSL builder, which is more than hard-coded Kotlin data needs. Keep classes free of platform types so adding `@Serializable` later is mechanical.

**Validation is a pure function returning problems.** `LevelConfigValidator.validate(config)` returns a list of messages, empty when valid; `LevelConfig.init` or a `requireValid()` throws `IllegalArgumentException` with the messages joined. The first-level config is validated in a test. Rejected: validation only in `init`, because tests of each rule then need exceptions for every case and cannot list several problems at once.

**Zones are checked with simple geometry.** A runway is an oriented rectangle, a helipad a circle. "Inside the viewport" uses the runway's rotated corners; "overlap" uses rectangle-rectangle (separating axis), rectangle-circle and circle-circle tests. Rejected: bounding boxes, which would reject legal diagonal layouts.

**Runway mode.** `RunwayMode` is a sealed type: `OneWay(heading)` and `TwoWay`. For `TwoWay` the two headings are the orientation and orientation plus pi. A pure `Runway.acceptsHeading(approachHeading, tolerance)` does the check; angle differences are normalised to (-pi, pi]. The landing rules (#7) call it; this change does not decide when a landing happens. Rejected: a boolean `oneWay` plus a separate heading field, which allows an invalid combination.

**Difficulty curve as a phase list, not a function.** `DifficultyPhase(startSeconds, spawnIntervalMin, spawnIntervalMax, weights: Map<AircraftType, Int>, maxAircraft)`. The active phase is the last one whose start is at or before the level time. Phase lists are plain data, serialisation-friendly and easy to assert on. Rejected: a function of time, which cannot be validated or serialised. Interpolating between phases was also rejected: steps are easy to test and feel like levels in a game; revisit if pacing feels jumpy.

**Run length.** `duration: Seconds?` where null means endless, and `goalLandings: Int?`. An explicit flag plus a nullable duration was rejected because it allows contradictions.

**Seed.** `LevelConfig.seed: Long?`. Null means a fresh seed per run: the caller that starts a run draws one and passes it to `Spawner(config, seed)`, so `Spawner` itself never reads a clock. The spawner uses `kotlin.random.Random(seed)` and nothing else. Rejected: the spawner drawing its own seed, which would make tests non-deterministic.

**Spawner is a pure object stepped by the engine.** `Spawner.update(deltaSeconds, levelTime, aircraftInAir)` returns zero or one new spawn (type, position, heading). It holds the time until the next spawn, drawn uniformly from the active phase's interval range, and picks the type by weight. When the cap is reached or no fair entry is found, it keeps the spawn due and retries on later updates instead of dropping it. Rejected: spawning inside `GameEngine`, which would make the spawner untestable on its own.

**Entry is generated, then judged.** Pick a permitted edge (uniform), a point on it, and place it outside the viewport by the spawn margin. Aim at a random target in the central safe region (default the middle 50% of each axis) and add jitter within a bounded angle (default 15 degrees). Judge the straight line: the chord inside the viewport must be at least `minimumChordFraction` of the smaller viewport side (default 0.5), and the time on screen (chord / speed) at least `minimumTimeOnScreen` (default 4 s). Otherwise re-roll, up to `maxEntryRerolls` (default 8); then delay. Time on screen uses the aircraft's effective speed, so the rule holds for jets as well. Rejected: only aiming at the centre (too predictable) and rejection without aiming (many rerolls, clipped corners).

**Separation at spawn** compares the candidate entry point with the positions of all aircraft, including those still outside the viewport, against the level's `spawnSeparation`. It checks the entry point only, not later flight; later closeness is the collision rule.

**First level in `domain/levels`.** `Levels.meadowField` is a `LevelConfig` matching issue #5: world size 1600 x 900 world units, one two-way runway in the middle lying left to right, one helipad on the right, a few phases, maximum three aircraft in the air. The exact numbers are in the config only and may be tuned.

**Scoring fields.** The issue lists "points per landing". Per-type points are owned by `aircraft-types` (#8), so the level carries `ScoringConfig(lives, pointsMultiplier)` and does not duplicate a per-landing value. Bonuses (fast landing, alignment) are not modelled yet.

**Wind, emergency, hints** are optional fields with inert defaults (`Wind?` null, `emergencyAircraft = false`, `hints = emptyList()`). Nothing reads them yet; they exist so levels can adopt them without a model change.

**Docs.** `docs/game-design.md` gets a Levels section (config contents, build-up, fair entry rule); `docs/architecture.md` gets the domain and engine parts.

## Assumptions

Each can be overruled by the author.

- Phase steps, not interpolation, define the curve.
- The build-up rule ("first phase only light props and few helicopters") is data, checked on the first level, not enforced by validation for every level.
- Defaults: separation and landing tolerance come from `LevelDefaults`; safe region middle 50%; jitter 15 degrees; minimum chord 0.5 of the smaller viewport side; minimum time on screen 4 s; 8 re-rolls.
- Validation also rejects a phase that allows an aircraft type with no matching landing zone.
- All spawn edges are allowed unless the level restricts them.
- Level scoring carries `lives` and a `pointsMultiplier`; per-type points come from #8 and bonuses are left out.
- Meadow Field is a two-way runway with a helipad on the right, as in issue #5; #5 owns drawing and decoration.
- Wind, emergency aircraft and hints are fields only.

## Risks / Trade-offs

- Another change (`aircraft-types`) also touches the level config for overrides. Mitigation: land them in order of dependency, or merge field additions carefully; this change keeps the speed multiplier as one plain field.
- Heavy rejection could stall spawning on a tiny or odd viewport. Mitigation: bounded re-rolls, then delay and retry on the next update; a test covers a case where no entry is fair.
- Geometry tests for rotated runways are easy to get subtly wrong. Mitigation: unit tests with axis-aligned and rotated layouts.
