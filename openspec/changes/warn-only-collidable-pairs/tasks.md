# Tasks

## 1. Shared pair rule

- [x] 1.1 Add `Collisions.canCollide(a, b)` in `engine/Collisions.kt` (design decision 1) and make `Collisions.find` use it instead of its three inline exclusions; update the KDoc of the object. Verify: `./gradlew :shared:allTests` passes with `CollisionsTest` unchanged, so no collision outcome changed.
- [x] 1.2 Add `CollisionsTest` cases for `canCollide`: two In Flight (true), In Flight and Landed (false), same runway same direction (false), one of those fading (false), same runway opposite directions (true), different runways (true), a helicopter in status `Landing` with any other aircraft (false). Verify: `./gradlew :shared:allTests` passes.

## 2. Warning follows the pair rule

- [x] 2.1 In `GameEngine.warned`, warn a pair only when `Collisions.canCollide` holds and the centres are closer than `level.world.warningDistance`; drop the separate `Landing` filter and update the KDoc of `warned` and of `GameState.warnedAircraft`. Verify: the existing warning tests in `GameEngineTest` (`closeAircraftAreWarnedUntilTheySeparate`, `everyAircraftOfACloseGroupIsWarned`, the landing-helicopter test) pass unchanged.
- [x] 2.2 Add `GameEngineTest` cases, one per scenario of the `proximity-warning` delta, placing aircraft with `engine.place` within the warning distance: same runway same direction (none warned), rolling behind a fading airplane (none), In Flight over a Landed airplane (none), same runway opposite directions (both), two different runways (both), mixed group (first and third only), In Flight aircraft over a warned rolling pair (the pair only). Verify: `./gradlew :shared:allTests` passes.
- [x] 2.3 Add a `GameEngineTest` case for the touchdown: an airplane cleared to land on a two-way runway, within the warning distance of an airplane rolling out towards it, is not warned in the updates before its altitude state becomes Landed and is warned in the update in which it does. Verify: `./gradlew :shared:allTests` passes.
- [ ] 2.4 Update the Proximity warning line in `docs/game-design.md`, and the warning sentence in the core rules of `CLAUDE.md` and `.github/copilot-instructions.md`: the ring shows only for pairs that can collide (not for same-direction rollouts on one runway, not between a landed and a flying aircraft, not for a helicopter in its landing sequence). Verify: the three texts say the same as the spec delta.

## 3. Check on a device

- [ ] 3.1 Human check on a device: land two airplanes right behind each other on one runway (no rings on the runway); fly an aircraft over a rolling airplane (no rings); bring two flying aircraft close (both ringed, rings fade when they separate); on a level with a two-way or crossing runway, land from both ends or on both runways (rings from touchdown on).
