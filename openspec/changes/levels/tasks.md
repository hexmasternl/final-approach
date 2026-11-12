# Tasks

## 1. Design system first

- [x] 1.1 Add the Dawn palette, light tokens (`light-max-opacity`, nav red and green, white, beacon, AGL) and beacon and running-light durations to the design system, and verify the showcase shows them and the generator emits them into `ui/theme`
- [ ] 1.2 Add sprites for cow, farmer, bird, yacht, sailboat, container vessel, acacia, bush, rock, fence, control tower with beacon, plus aircraft lights, and verify each appears in the showcase on the 4-unit outline style
- [x] 1.3 Add the square LevelCard variant to the design system and verify it shows a miniature and a name

## 2. Level data

- [x] 2.1 Add per-runway accepted aircraft types and `accepts(type)` to the runway and helipad data (aligned with `LevelConfig`), and verify with unit tests that runways default to all airplanes, refuse the helicopter, and that helipads accept only the helicopter
- [x] 2.2 Define level one (one runway, one helipad) and verify with unit tests for its zone counts and acceptance
- [x] 2.3 Define level two (main runway, shorter diagonal runway refusing the jumbo, one helipad, island centred) and verify with unit tests for crossing, angle not 0 or 90, main longer, and acceptance
- [x] 2.4 Define level three (two parallel mains, crossing runway refusing the jumbo, two helipads) and verify with unit tests for parallel headings, crossing both, and acceptance
- [x] 2.5 Add the `Levels` catalogue and a validation test that every zone is inside the world, clear of the HUD band, and that helipads do not overlap runways

## 3. Ambient life

- [x] 3.1 Add `LevelAmbienceConfig` (cow count and speed, bird and boat gaps and speeds, light cycles) and verify by a test that no beacon or running-light cycle is under 0.5 s and no light opacity exceeds the token
- [x] 3.2 Implement the pure ambient simulation for cows and verify with seeded unit tests that cows never leave the fence and move slowly
- [x] 3.3 Implement birds and boats and verify with seeded unit tests that there is at most one of each on screen, boats never overlap the island, all three boat kinds appear over many spawns, and nothing spawns under reduced motion
- [x] 3.4 Implement the light phases (running light along the runway, beacon) and verify with unit tests that the sequence advances along the runway and repeats, and is static under reduced motion

## 4. Drawing

- [ ] 4.1 Draw the Meadow scenery with the paddock, farmer, cows, trees and a bird, and verify in a preview that scenery does not cover a zone and the draw order is field, bird, glow, aircraft
- [ ] 4.2 Draw the Island scenery with water, centred island and boats, and verify in a preview that the airport is fully on the island and the container vessel is the largest boat
- [ ] 4.3 Draw the Savanna scenery at dawn with decorations, tower, running lights and beacon, and verify in a preview that the lights are dim and the HUD surfaces stay light
- [ ] 4.4 Draw aircraft navigation lights and headlight when the theme is Dawn, and verify in a preview that levels one and two show none
- [ ] 4.5 Draw the tile miniature from a `LevelConfig` and verify in a preview that runway and helipad counts and angles match all three levels

## 5. Selection page and flow

- [ ] 5.1 Build `LevelSelectPage` with square tiles in a horizontally scrolling row that scrolls only when needed, and verify by running the app on a narrow and a wide screen
- [ ] 5.2 Add `Screen.LevelSelect` to `App`: Take off opens it, tapping a tile opens the game screen on that level, back goes to the previous screen, and verify by running the app that each level shows its own airfield
- [ ] 5.3 Apply reduced motion to all scenery and verify by running with animations disabled that nothing moves or blinks

## 6. Docs

- [x] 6.1 Update `docs/game-design.md` (levels, runway aircraft restrictions) and `docs/architecture.md` (screen flow, level scenery and ambient life), and verify they match the code names

## 7. Human checks

- [ ] 7.1 Human check on a real device: watch level three for two minutes and confirm lights are dim and not annoying, and that cows, birds and boats look natural on a small and a large phone
