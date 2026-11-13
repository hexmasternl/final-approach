# Tasks

## 1. Level data

- [x] 1.1 In `domain/Levels.kt`, add named headings for west and south-west next to `EAST` and `SOUTH`, set Meadow Field `09-27` to `RunwayMode.OneWay(<west>)` and Island `04-22` to `RunwayMode.OneWay(<south-west>)`, and update the Meadow Field KDoc. Verify `Levels.all.forEach { it.validate() }` returns no errors (existing `LevelsTest.meadowFieldValidates` and the layout validation tests pass).
- [x] 1.2 Update `LevelsTest.meadowFieldHasATwoWayRunwayAndAHelipad` into a one-way test (single landing heading west, `numberFor` gives `27`) and replace the all-two-way assertion in `LevelLayoutsTest` with: Meadow `09-27` one-way west, Island `09-27` two-way, Island `04-22` one-way south-west (number `22`), every Savanna runway two-way. Verify `./gradlew :shared:allTests` passes.
- [x] 1.3 Add level landing tests in `commonTest` using `LandingRules` with each level's real zones: an aligned westbound path into the east end of Meadow `09-27` lands and rolls west; an aligned eastbound path into its west end does not land and the path keeps taking points; the same pair for Island `04-22` (south-west lands, north-east does not); both ends of Island `09-27` land. Verify the tests pass.
- [x] 1.4 Add the room check test (design decision 7) for every one-way runway in `Levels.all`: the reverse-heading segment from the threshold to the world edge stays inside the world and below `LevelDefaults.HUD_BAND` and is at least `runwayZoneDepth` long. Verify it passes for Meadow and Island.
- [ ] 1.5 Update `docs/game-design.md`: Meadow Field has one one-way runway, runway 27 (approach from the east), with the helipad on the right; the Island diagonal is one-way runway 22 (approach from the north-east) and its main runway stays two-way; Savanna is unchanged. Verify no sentence in `docs/` still calls the Meadow runway or the Island diagonal two-way (`grep -n -i "two-way" docs/`).

## 2. Design system: one-way runway marking

- [ ] 2.1 Add the one-way variant to `docs/design-system/components/Runway.md` (a `mode` input: markings only at the landing end, chevron centre line in `marking`, `line-4` stroke, round joins, arm `space-5`, spacing `space-4`) and a line under Landing zones in `docs/design-system/world.md`. Verify both files describe the same construction as design decision 3.
- [ ] 2.2 Add a one-way runway example next to the existing runway in `docs/design-system/showcase.html` (and its CSS/JS in `bundle.css`/`bundle.js` if the runway is drawn there). Verify by opening the showcase that the one-way runway shows one marked end and chevrons, and still reads in a greyscale screenshot. Human task: regenerate `showcase.pdf` if the project keeps it in step.

## 3. Runway drawing

- [ ] 3.1 In `ui/game/AirfieldDrawing.kt`, make `drawRunway` draw threshold bars and touchdown patches only at ends in `runway.landingHeadings`, and draw the centre line as chevrons pointing along the landing heading when `runway.mode` is `RunwayMode.OneWay`, with every size from `FaDimens` through `FieldScale` and the colour `colors.marking`. Two-way runways look exactly as before. Verify `./gradlew :androidApp:assembleDebug` builds and the `GamePreviews` preview of Meadow Field shows 27 with left-pointing chevrons and no markings at the left end.
- [ ] 3.2 Human task: on a device, play Meadow Field and Island in Day and Dawn themes and check that runway 27 and runway 22 read as one-way, that a westbound approach to 27 and a south-westbound approach to 22 land, and that approaches to the 09 and 04 ends do nothing.
