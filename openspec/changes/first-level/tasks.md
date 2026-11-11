# Tasks

## 1. Engine

- [ ] 1.1 Add `Aircraft`, `FlightPath` and `GameState` in `domain`/`engine`, and `GameEngine` with a `StateFlow`, spawner hookup and `update(deltaSeconds)`; verify with unit tests that spawns appear and the first level never has more than three aircraft in the air
- [x] 1.2 Add turn-rate-limited path following that consumes reached points and flies straight on after the path; verify with unit tests
- [ ] 1.3 Remove aircraft that leave the viewport after having entered it; verify with a unit test

## 2. Input

- [ ] 2.1 Add `select`, `beginPath`, `extendPath` and `endPath` on the engine with the pick radius, nearest-aircraft rule, minimum spacing and landing cut-off; verify with unit tests
- [ ] 2.2 Add `WorldViewport` (fit, centre, pixels to world and back); verify with unit tests

## 3. Level data and platform

- [ ] 3.1 Add `SceneryItem` to `LevelConfig`, the Meadow Field scenery, and validation that scenery stays clear of zones; verify with a unit test
- [ ] 3.2 Add `AppHost.lockLandscape` and implement it in `AndroidAppHost`; verify by building the Android app

## 4. Game screen

- [ ] 4.1 Add the game screen with the frame loop, gestures and the world drawn in layer order (field, scenery, runway, helipad, paths, shadows, aircraft, selection ring); replace the placeholder on Take off; verify by building the Android app
- [ ] 4.2 Add the Scenery component to the design system (`docs/design-system/components/Scenery.md`)

## 5. Docs and checks

- [ ] 5.1 Update `docs/architecture.md` and `docs/game-design.md` for the engine, input and the first level
- [ ] 5.2 Run `./gradlew :shared:allTests` from `app/src` and verify all tests pass
- [ ] 5.3 Human check on a device: Take off opens Meadow Field in landscape, aircraft arrive, can be steered and land on the runway (both ends) and the helipad
