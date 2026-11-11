# Tasks

## 1. Level config model

- [x] 1.1 Add `LevelDefaults` with the global tunables (separation, spawn margin, landing tolerance, safe region, angle jitter, minimum time on screen, minimum chord fraction, maximum re-rolls) and verify by a search that no such value appears as a literal in domain or engine logic
- [x] 1.2 Add the data classes in `domain` (`LevelConfig`, identity and ambience, runway with `RunwayMode`, helipad, world, run rules, `DifficultyPhase`, scoring, wind, hints) with defaults, and verify with a test that a minimal config compiles and the optional fields default to inert values
- [x] 1.3 Implement `Runway.acceptsHeading` and verify with unit tests: one-way correct, one-way opposite, two-way both ends, across the runway, tolerance edge, angles beyond pi

## 2. Validation

- [ ] 2.1 Implement `LevelConfigValidator` with zone geometry (inside viewport, overlap) and verify with unit tests for no zone, outside the viewport, overlapping zones, rotated runways, and a valid config
- [ ] 2.2 Add the difficulty curve and separation rules and verify with unit tests for bad intervals, non-positive weights, non-increasing starts, a first phase not at 0, maximum below 1, and helicopters without a helipad or airplanes without a runway; each failure message names the problem

## 3. First level

- [ ] 3.1 Define `Levels.meadowField` (one two-way runway, one helipad on the right, phased curve with a maximum of three aircraft) and verify with unit tests that it validates, that its first phase allows only light props and helicopters, and that no phase exceeds three aircraft

## 4. Spawner

- [ ] 4.1 Implement phase selection, jittered spawn timing and weighted type choice, driven by a seeded `Random`, and verify with unit tests: same seed gives the same sequence, different seeds differ, the first phase only spawns allowed types, intervals stay within the phase range, heavier weights spawn more often
- [ ] 4.2 Implement the maximum-aircraft cap and verify with unit tests that nothing spawns at the cap and spawning resumes after a landing
- [ ] 4.3 Implement edge choice and spawn position with the spawn margin, plus edge restriction, and verify with unit tests that spawns lie outside the viewport by the margin and only on permitted edges
- [ ] 4.4 Implement the fair entry rule (safe region target, jitter, minimum time on screen, minimum chord, bounded re-rolls, delay) and verify with a property-style test over many seeds and with a case where no entry is fair that the spawn is delayed
- [ ] 4.5 Implement the separation check at spawn, including aircraft outside the viewport, and verify with unit tests
- [ ] 4.6 Verify with a simulation test that spawn rate and variety grow across the phases of a test level over many seeds

## 5. Docs

- [ ] 5.1 Update `docs/game-design.md` (levels, build-up, fair entry rule, runway modes) and `docs/architecture.md` (domain config and spawner), and verify the names and defaults match the code
- [ ] 5.2 Run `./gradlew :shared:allTests` from `app/src` and verify all tests pass
