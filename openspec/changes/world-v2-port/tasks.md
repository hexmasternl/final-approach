# Tasks

The visual reference for every task is `docs/design-system/showcase.html` (sections Scenery, Ground and landing zones, Screens) with `docs/design-system/world.md` and `components/Ground.md`, `Runway.md`, `Helipad.md`. Gradle commands run from `app/src`; node tools from the repository root.

## 1. Baseline

- [x] 1.1 Add a seeded-run test in `commonTest/engine` that plays each of the three levels with a fixed seed and compares spawns, landings, collisions and score with values recorded on this base commit (design Decision 11); verify `./gradlew :shared:allTests` passes and note the base commit hash in the test's KDoc

## 2. Generator and sprite data

- [x] 2.1 Move the sizes of the runway, helipad, taxiway and ground bands in `bundle.js` into one exported table that `runwayMarkup`, `helipadMarkup` and the `Scene` builder read (design Decision 5); verify `node docs/design-system/tools/build-showcase.cjs` leaves `showcase.html` without a diff
- [x] 2.2 Replace the v1 scenery pass of `tools/gen-kotlin.cjs` by one that writes `ui/sprites/ScenerySprites.kt` from `WORLD`, `DETAILS` and the cloud (design Decision 1), and writes the metrics table of 2.1 as a Kotlin object under `ui/sprites`; delete `tools/scenery-v1.js`; verify the generator fails with the sprite's name on a sample sprite with an unknown attribute, that running it twice gives no diff, and that `grep -r scenery-v1` finds nothing outside `openspec/`
- [x] 2.3 Rewrite `ui/sprites/ScenerySpriteDrawing.kt` for the new data: fills, strokes with their own width, opacity, clip groups, transforms, the non-scaling stroke, and the fit of design Decision 2; remove `SceneryLines`; keep the callers compiling; verify `./gradlew :shared:allTests :androidApp:assembleDebug` passes and the file reads no `line2`, `lineFine` or `radiusSm`
- [x] 2.4 Update the sprite sheet previews in `GamePreviews.kt` to show every v2 sprite in Day, Night and Dawn; verify each sprite next to its tile in the showcase, and that windows are glass in Day and lit in Night and Dawn
- [x] 2.5 Update `docs/design-system/compose.md` and `docs/architecture.md` for the generator: scenery sprite data and ground metrics come from `bundle.js`, no v1 sprite source remains; verify neither file mentions `scenery-v1.js`

## 3. Shadows and rotation

- [x] 3.1 Add the pure shadow-placement function and the single shadow pass in `drawField` (design Decision 3): one alpha layer in `shadowCast` at `FaOpacity.shadowCast`, between ground and everything with height, which is drawn north to south; verify with a unit test that a sprite rotated by 0, 90 and 180 degrees gets the same shadow offset, and in a preview that two overlapping tree shadows are not darker where they overlap
- [x] 3.2 Add `SceneryKind.standing`, the validator rule that a standing item has no rotation, and remove the rotations of buildings and windsocks in `Levels.kt` (design Decision 4); verify with `LevelConfigValidatorTest` cases that pass and fail the rule, a test that `standing` is the opposite of each sprite's `rotates`, and that all three levels still validate

## 4. Runways and helipads

- [ ] 4.1 Add `runwayPlan` (design Decision 6) with unit tests: marked ends for one-way and two-way, six threshold bars, aiming blocks from 460 units, touchdown marks from 560, chevrons or dashes, edge lights every 58 units, four threshold lights per landing end, approach lights at one landing end
- [ ] 4.2 Draw the runway from the plan and the metrics object: fringe, shoulder, outlined asphalt at the level's footprint, edge lines, markings, tyre rubber, number, lights; move the Dawn running light onto the plan's edge lights and remove the replaced constants in `LevelBackdrop.kt`; verify previews of a one-way, a two-way and a short runway against `Runway.md`, and that `LevelLayoutsTest`, `LandingZonesTest` and the test of 1.1 pass unchanged
- [ ] 4.3 Draw the helipad: fringe, concrete pad with joints and corner lights, outlined disc at the level's radius, worn ring, warning ring, H; verify a preview against `Helipad.md` and that `AirfieldDrawing.kt` no longer reads `line3`, `line4`, `radiusSm` or a `space` token for a world size
- [ ] 4.4 Update the Compose notes of `components/Runway.md` and `components/Helipad.md`; verify they no longer say the app renders version 1

## 5. Ground

- [ ] 5.1 Add the airfield outline and the decoration seed to `LevelConfig` and give each level its outline (design Decision 7); verify with a test that every runway and helipad with its fringe lies inside its level's outline, and for the island that the outline lies on the island
- [ ] 5.2 Draw the ground pieces with their bands and no `outline` stroke: road, dirt track, taxiway with centre line and hold-short bar, apron, car park, crop fields (wheat, green, ploughed), pond and waterhole, paddock ground, island beach with shallows and foam; verify each in a preview against `Ground.md` and by grep that the only `colors.outline` in `AirfieldDrawing.kt` and `LevelBackdrop.kt` is on runways and helipads
- [ ] 5.3 Draw the base ground and the mown airfield: stripes inside the outline, parallel to the main runway, with the soft boundary; one flat tone outside; replace `drawLawn` and its tilt constant; verify in a preview of each level that no stripe lies outside the airfield
- [ ] 5.4 Add the seeded decoration function (patches, sea patches, clusters of tufts, flowers and reeds, the trees of a wood) and draw its result; verify with unit tests that the same level gives the same decoration twice, that nothing lies inside the airfield or on a road, field, pond, runway or helipad, that no standing thing lies in the HUD band, and that level three has no flowers
- [ ] 5.5 Update the Compose note of `components/Ground.md`; verify it names the files that draw the ground and no longer says version 1

## 6. Level data, ambient life and cloud

- [ ] 6.1 Add the scenery kinds of design Decision 8 and bring each level's scenery list to its row in `world.md`, Surroundings per landscape, with footprints at the sprites' natural sizes; verify `LevelsTest`, `LevelLayoutsTest`, `LevelConfigValidatorTest`, `SceneryIsDecorationTest` and the test of 1.1 pass, and that runway and helipad entries in `Levels.kt` show no diff
- [ ] 6.2 Draw cows, farmer, flock and boats with their wakes from the v2 sprites, with their shadows in the shadow layer; verify `AmbientLifeTest` passes unchanged and in a preview that a turned cow's shadow lies to its lower right
- [ ] 6.3 Port the cloud from the generated data in `SceneryDrawing.kt` (design Decision 10) and update the Compose note of `components/Cloud.md`; verify previews of the game and the main menu backdrop against the showcase cloud
- [ ] 6.4 Update the level previews in `GamePreviews.kt` to render each level in its theme and in the other two; verify each level in its theme side by side with its game screen in the showcase, and list any difference other than layout in the pull request

## 7. Level select

- [ ] 7.1 Add the pure miniature-crop function (design Decision 9); verify with a unit test that for each level the crop is square, lies inside the world and holds every runway and helipad shape
- [ ] 7.2 Split the static part out of `drawField` and draw the level tile's miniature with it, in the level's own palette, recorded once per size and palette; remove `DAWN_ALPHA`, `RUNWAY_STROKE_FRACTION`, `sky300` and `sky700` from `LevelSelectPage.kt`; verify previews of the page in Day and Night: the island's sea is water in both, tiles are square, and the `level-selection` scenarios hold
- [ ] 7.3 Update `CLAUDE.md`, `.github/copilot-instructions.md` and the Compose note of `components/LevelCard.md`: the app's world renders version 2, and the rule against v1 tokens in new code stays until the token follow-up; verify the two instruction files say the same

## 8. Review and wrap-up

- [ ] 8.1 Run `./gradlew :shared:allTests :androidApp:assembleDebug`; verify by grep that no file under `ui/sprites`, `ui/game/AirfieldDrawing.kt`, `ui/game/LevelBackdrop.kt`, `ui/game/LevelSelectPage.kt` or `ui/game/ambient` reads a token whose KDoc starts with "v1.", and that `git diff <base> -- app/src/shared/src/commonMain/kotlin/nl/hexmaster/finalapproach/engine` is empty
- [ ] 8.2 Have the design-critic agent review screenshots of each level in its theme, with aircraft and flight paths over the busiest scenery, and of the level-select page, then the android-critic agent review the branch; fix what they find; verify the design-critic's final report confirms aircraft, paths and landing zones read first and names no W1 to W9 violation
- [ ] 8.3 Create two follow-up issues that link back to #77: one to move the remaining readers (flight path end dot, proximity ring, tutorial highlight and drag demo, selection ring, splash banner, `FaShadows`) off the v1 tokens and then remove the deprecated tokens and `tools/migrate-tokens-v2.cjs`; one for the level card's best-score chip, tags and locked state; verify both exist and are named in the pull request
- [ ] 8.4 Human check: play each level for two minutes on a phone of about 800 by 360dp; confirm aircraft, paths and landing zones read clearly over the new scenery, landings work on every runway and helipad as before, and the frame rate holds; if it does not, record the static field into graphics layers (design, Risks) and check again
- [ ] 8.5 Human check with the system's reduced-motion setting on: confirm no bird or boat appears, cows stand still, and the running light and beacon are steady
