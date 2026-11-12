# Tasks

## 1. Config and domain

- [x] 1.1 Add `touchdownDistance`, `leadInTurningRadii` and `leadInHandleFraction` to `LandingConfig`, with constants in `LevelDefaults` (40.0, 2.0, 0.4); verify a `LevelConfigTest` case checks the defaults and no new literal appears in engine code
- [x] 1.2 Add a derived `threshold` and a centre-line point helper to `LandingZone.Runway`; verify `LandingZonesTest` cases for a one-way runway and both ends of a two-way runway

## 2. Snap the path to the centre line

- [x] 2.1 Implement the lead-in builder in `engine` (find J back along the drawn path, touchdown point T, cubic Bezier sampled at `minPointSpacing`, short-path fallback) as a pure function; verify `commonTest` cases: ends on the centre line, last segment along the landing direction, no corner at J, and for every airplane type, entries at the zone centre and both side edges at 0 and plus/minus the full tolerance give no turn tighter than the turning radius
- [x] 2.2 Use it in `PathDrawing.extend` for runway hits only, and pass the airplane's effective turning radius from `GameEngine.extendPath`; update `PathDrawingTest` and `GameEngineTest` expectations that used the raw entry point; verify tests: off-centre entry ends on the centre line, two-way runway uses the entered end, misaligned and mid-runway crossings are not snapped, helipad paths still end at the entry point, a locked path ignores further points
- [x] 2.3 Update the landing rule in `docs/game-design.md`, the core rules in `CLAUDE.md` and `.github/copilot-instructions.md`; verify all three say the path is snapped onto the centre line with a lead-in and match the spec

## 3. Touchdown and rollout

- [x] 3.1 Add `AircraftMotionTest` cases: an airplane cleared with an off-centre entry touches down on the centre line within the zone depth from the threshold, and every rollout position lies on the centre line, for each airplane type; change `AircraftMotion` only if a case fails

## 4. Integration

- [x] 4.1 Run `./gradlew :shared:allTests` and `./gradlew :androidApp:assembleDebug` from `app/src`; verify both succeed
- [ ] 4.2 Human check on a device: in level 1, land each airplane type entering near a side edge of the zone; verify the shown path bends onto the centre line, the airplane follows it without looping or jumping, and it rolls out along the centre line
