## 1. Domain and engine

- [x] 1.1 Add `AircraftStatus.Landing(elapsed)` and `LandingConfig.helicopterLandingSeconds = 3.0` (validated greater than zero); verify with a config validation test
- [x] 1.2 Change `AircraftMotion` so a helicopter whose path is empty starts `Landing`, advances `elapsed`, and on completion emits `Landed` and is removed; verify with `commonTest` for sequence length, no `Landed` before the end and exactly one at the end
- [x] 1.3 Skip `Landing` aircraft in collision detection, in the proximity warning and in `GameEngine.select`; verify with `commonTest`: an aircraft overlapping a landing helicopter does not collide, other pairs still do, and the helicopter can collide before the sequence starts

## 2. UI

- [ ] 2.1 Check the showcase for a descent treatment; if missing, add it to the design system (Aircraft component notes and any token) before use; verify the doc and tokens carry it
- [ ] 2.2 Draw the descent in the aircraft layer from the theme and config, with a still pose for reduced motion; verify with a preview and that no literal duration, scale or easing appears in the composable

## 3. Docs and checks

- [ ] 3.1 Update `docs/game-design.md` with the 3 second landing sequence; verify the rules match the specs
- [ ] 3.2 Run `./gradlew :shared:allTests` and `./gradlew :androidApp:assembleDebug`; verify both pass
- [ ] 3.3 Human check: land a helicopter, fly an aircraft across the pad during the sequence, and see no collision
