## 1. Landing speed factor

- [x] 1.1 Add `LANDING_SPEED_FACTOR = 0.5` to `LevelDefaults` and `landingSpeedFactor` to `LandingConfig` (KDoc: the landing speed is the reference type's flight speed times this factor); verify the shared module compiles
- [x] 1.2 Use the factor in `AircraftMotion.rolloutDistance` and update its KDoc; verify with `commonTest` in `AircraftMotionTest`: replace `theLightPropRollsOutAtItsFlightSpeed` with a test that the light prop starts at its flight speed and reaches half of it after the deceleration time; change the faster-airplanes and reference-type tests to expect half the reference speed; add a test that a factor of 1 gives the full reference speed; keep the step-size and slowdown-ends tests passing
- [x] 1.3 Add a test that the rolled distance before the fade is still two thirds of the runway with the default configuration; verify `./gradlew :shared:allTests` passes, including the same-direction rollout collision tests

## 2. Docs

- [x] 2.1 Update the landing rules and the collision paragraph in `docs/game-design.md` (landing speed is half the light prop's flight speed, the light prop slows too, same-direction rollouts do not collide because all airplanes reach one landing speed); verify no "light prop's speed" wording about rollouts remains
- [ ] 2.2 Update the matching core rule in `CLAUDE.md` and the landing line in `.github/copilot-instructions.md` so both say airplanes roll out at one shared landing speed (half the light prop's flight speed); verify the two files agree with `docs/game-design.md`

## 3. Check

- [ ] 3.1 Human check: play Meadow Field on a device, land each airplane type and confirm the rollout looks half as fast, still runs two thirds of the runway, and that opposite-direction landings remain fair
