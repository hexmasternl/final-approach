# Tasks

## 1. Config and state

- [x] 1.1 Add `ROLLOUT_DECELERATION_SECONDS = 1.0` and `LANDING_SPEED_REFERENCE = AircraftType.LightProp` to `LevelDefaults`, and `rolloutDecelerationSeconds` and `landingSpeedReference` to `LandingConfig` with those defaults and KDoc; verify `./gradlew :shared:allTests` compiles and passes.
- [x] 1.2 Add `runwayId: String` and `elapsed: Double = 0.0` to `AircraftStatus.RollingOut`, fill `runwayId` in `AircraftMotion.approach` from the runway zone, and update test fixtures that build `RollingOut`; verify `./gradlew :shared:allTests` passes.

## 2. Matched rollout speed

- [x] 2.1 In `AircraftMotion.rollOut`, compute the distance per step from the linear speed profile in design decision 3 (flight speed at touchdown, landing speed from `specs[landing.landingSpeedReference].speed` after `rolloutDecelerationSeconds`), advance `elapsed`, and clamp to `remaining`; verify with the tests in 2.3.
- [x] 2.2 Remove `AircraftTypeSpec.rolloutSpeedFraction` and `LevelDefaults.ROLLOUT_SPEED_FRACTION`; verify a repository search finds neither name and the build passes.
- [x] 2.3 Add `kotlin.test` cases in `AircraftMotionTest`: the light prop rolls out at its own speed throughout; the turboprop, jet and jumbo move at their flight speed at touchdown, at the light prop's speed 1 s after touchdown and after that; a configured turboprop reference changes the landing speed; one large step and many small steps over the same time end at the same position; a rollout that ends within the slowdown fades out as usual. Verify `./gradlew :shared:allTests` passes.
- [x] 2.4 Update the airplane rollout rule in `docs/game-design.md` (Landing rules) to the shared landing speed and the 1-second slowdown; verify the doc no longer says "a fraction of its flight speed".

## 3. Same-direction rollouts do not collide

- [x] 3.1 In `Collisions.find`, skip a pair when both are `RollingOut` with the same `runwayId` and a rollout direction within the named angular tolerance (design decision 1); verify with the tests in 3.2.
- [ ] 3.2 Add `kotlin.test` cases: in `CollisionsTest`, same runway same direction overlapping (rolling and fading) is not a collision, same runway opposite directions overlapping is, different runways overlapping is; in `GameEngineTest`, a jet landing right behind a light prop in the same direction completes both rollouts without game over, and two airplanes landing from opposite ends of a two-way runway end the level with a collision. Verify `./gradlew :shared:allTests` passes.
- [ ] 3.3 Update the collision rule in `docs/game-design.md` (Failure and scoring) and the airplane rollout sentence in `CLAUDE.md` and `.github/copilot-instructions.md` to say all airplanes roll out at the light prop's speed and same-direction rollouts on one runway never collide; verify the three texts agree.

## 4. Play check

- [ ] 4.1 Human: on a device, land a jet or jumbo right behind a light prop on Meadow Field, and two airplanes from opposite ends; confirm the first pair causes no failure, the second does, and that rollouts feel right and the runway does not clog.
