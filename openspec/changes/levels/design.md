## Context

The game screen is still a placeholder, and the engine, `LevelConfig` (issue #6) and the first level (issue #5) are planned but not built. This change adds three levels, a selection page and level scenery. Everything visible must use the design system; where it lacks something it is added there first (see `CLAUDE.md`).

## Decisions

### Levels are `LevelConfig` instances
Layouts, theme (Day, Dawn) and scenery kind live in `LevelConfig` from issue #6, in `domain`, in world units. A `Levels` catalogue lists the three in order. The selection page and the game screen both read the same data, so a tile's miniature can never disagree with the level.
- Rejected: a separate `Level` type for UI. It would duplicate layouts and drift.
- If `LevelConfig` has not landed when this is implemented, this change implements it only as far as it needs and aligns with issue #6.

### Accepted aircraft are data on the zone
A runway carries a set of accepted `AircraftType`s (default: all airplanes); a helipad accepts only the helicopter. `accepts(type)` is a pure function with unit tests. The engine's landing rules (issue #7) will call it; this change does not enforce it in play.
- Rejected: a `jumboAllowed` flag. It does not generalise and the helicopter case needs the same mechanism.
- Rejected: a minimum runway length per type. It would be derived and hide the designer's intent.

### World size and scaling
Layouts use one fixed logical world, assumed to be 1000 by 1000 units, centred in the viewport and scaled to fit; scenery extends beyond it to fill the screen so there are no bars. The HUD band at the top stays clear of zones. Pixel conversion happens only at the render boundary.
- Rejected: separate portrait and landscape layouts. Twice the data for no gameplay difference.

### Scenery is drawn data plus small pure simulations
A `LevelScenery` renderer draws the static field per theme: Meadow (lawn, trees, fence), Island (water, island shape), Savanna (dawn ground, acacia, bushes, rocks, tower). Moving things (cows, birds, boats, light phases) are advanced by pure classes in the style of `MenuBackdropSimulation`: `AmbientLife.update(deltaSeconds)` with an injected seeded random source, and all timings, speeds and counts in `LevelAmbienceConfig`. They are unit tested and never touch the game engine, so scenery cannot affect gameplay.
- Rejected: animating inside composables. It cannot be tested and mixes rules with drawing.

### Layer order
Scenery sits in the `field` layer, birds and boats directly above it and below zone glows; ground-level things (cows, farmer) are in `field`. Runway lights are drawn on top of their runways in the same layer. Aircraft lights are part of the aircraft sprite pass. This follows the layer order in `world.md`.

### Dawn palette and light tokens (design system gap)
The design system has Day and Night only. Add a `DawnColors` palette (dim ground, sky, warm light) with panels and aircraft kept light as in Night, plus tokens: `light-max-opacity`, light colours (`light-nav-red`, `light-nav-green`, `light-white`, `light-beacon`, `light-agl`), and the beacon and running-light durations. Add the sprites (cow, farmer, bird, yacht, sailboat, container vessel, acacia, rock, tower with beacon) and a square variant of LevelCard. Do this in `docs/design-system` and regenerate the theme with `tools/gen-kotlin.cjs` before using them.
- Rejected: reusing NightColors for dawn. The issue asks for dim light that still reveals the field, which is not night.

### Gentle lights
Brightness is capped by the `light-max-opacity` token and checked in a test against the config. Beacon and running-light cycles are at least 0.5 s (at most 2 per second); defaults: beacon 1.6 s cycle, running light 1.2 s sweep along the runway. Lights have a soft fade, not a hard flash.

### Reduced motion
With `AppHost.reduceMotion`: cows stand, no birds or boats are spawned, runway lights are fully lit and static, beacon steadily on at the same capped opacity.

### Selection page
`LevelSelectPage` shows a `Row` of square tiles inside a horizontal scroll container; the row scrolls only when its content exceeds the width. Tile edge length is a `FaDimens` value (a 160dp default, assumed), with name in `titleMd`-style text under or over the miniature, `radius-lg`, `line-3` outline and `shadow-lip-lg` like LevelCard. The miniature is drawn by a function that takes a `LevelConfig` and a size, reused by nothing in the engine. Selecting a tile calls `onStart(levelId)`; `App` keeps the chosen level id and the game screen reads the config from the catalogue. `Screen` gains `LevelSelect`. Back from selection goes to Main, back from the game goes to the selection page (assumption: the player will most likely try another level).
- Rejected: a pager. The issue asks for scrolling tiles.
- Rejected: the 220dp-wide LevelCard as is. The issue asks for square tiles.

### Names
Tile names: Meadow, Island, Savanna (assumed; the issue names themes, not titles). Kept as constants in one place.

## Risks

- Overlap with issues #5, #6 and #7 (level one, the config model, landing zones). The accepted-aircraft set must be agreed with the landing rules; keep it small.
- The sprite work is the largest part; the layouts and the selection page can ship first.

## Assumptions

- Level one runway accepts all airplanes; the second-level diagonal and the third-level crossing runway refuse only the jumbo (the issue states the first, and the second for level three is inferred).
- World is 1000 by 1000 units and layouts are shared by all orientations.
- Level names are Meadow, Island and Savanna; tiles show no best score, lock or stars.
- "Loaded and started" means the game screen, still a placeholder until the engine exists, opens on the chosen airfield with its scenery.
- Two cows ("a couple"); one bird and one boat at a time; the farmer stands still.
- Reduced motion freezes all ambient motion.
- Back from the game returns to level selection.
- The selection is not remembered between sessions.
