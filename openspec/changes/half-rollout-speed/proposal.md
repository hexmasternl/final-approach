## Why

Airplanes roll out on the runway too fast after touchdown. The author wants the ground speed of every aircraft halved.

GitHub issue: #52

## What Changes

- The shared landing speed that every airplane rolls out at becomes half of what it is today: half the effective flight speed of the landing speed reference type (the light prop by default), instead of all of it.
- The half is one configuration value, a landing speed factor with default 0.5, next to the reference type and the deceleration time.
- At touchdown an airplane still moves at its own flight speed and slows steadily to the (now lower) landing speed over the deceleration time (1 second). The light prop now slows down too, where today it keeps its flight speed.
- The rollout length stays two thirds of the runway, so a rollout takes longer.
- `docs/game-design.md`, `CLAUDE.md` and `.github/copilot-instructions.md` stop saying that airplanes roll out "at the light prop's speed".

Out of scope:

- Flight speeds in the air (see `aircraft-speed-tuning`, issue #54).
- Helicopters: they have no ground movement; they land on the pad and are removed (or perform the landing sequence of `helicopter-landing-sequence`).
- The rollout length, the touchdown point, the fade and the deceleration time.
- The rule that same-direction rollouts on one runway do not collide; it is unchanged.

Depends on: nothing. Related:

- `aircraft-speed-tuning` (#54) lowers the light prop's flight speed to 2/3. Because the landing speed is a factor of the reference type's speed, the two compose: with both applied, airplanes roll out at 1/2 x 2/3 = 1/3 of today's rollout speed. Either change can be applied first.
- `world-zoom-retune` and `detailed-scenery` multiply aircraft speeds by the zoom factor; the landing speed factor is unitless, so the landing speed follows the zoom without further work.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `landing-zones`: the shared landing speed is the reference type's flight speed times a configured factor (default one half), and the light prop now slows down after touchdown like the others.

## Impact

- `shared` `domain`: `LandingConfig` (new `landingSpeedFactor`), `LevelDefaults` (new `LANDING_SPEED_FACTOR`).
- `shared` `engine`: `AircraftMotion.rolloutDistance`.
- `shared` tests: `AircraftMotionTest` rollout speed tests.
- Docs: `docs/game-design.md`, `CLAUDE.md`, `.github/copilot-instructions.md`.
