# Proposal

GitHub issue: #33

## Why

Aircraft and runways look good, but the rest of the airfield is flat ovals, circles and rectangles: a cow is a white oval with a dot, the farmer two circles, a bird a single V. Players cannot tell what they are, and the airfield does not feel like part of a place. Pulling the camera back gives room for surroundings, and detailed scenery makes every object recognisable at a glance.

## What Changes

- **Zoom out every level by one factor (1.25).** Each world grows from 1280 × 720 to 1600 × 900 world units, so the screen shows more space around the airfield. Runways, helipads, landing zones and scenery keep their world sizes and so are drawn smaller on screen.
- **Aircraft keep their on-screen look and feel.** Everything that is about aircraft and the player's finger is multiplied by the same factor: aircraft radii (and with them sprite size and collision shape), speeds, warning distance, spawn separation, spawn and leave margins, the HUD band, path reach radius and minimum point spacing. Turn rates, timings and landing tolerance stay. The pick area stays at least 72dp.
- **Re-lay the three levels** (`Meadow Field`, `Island`, `Savanna`) in the larger worlds: the airfield stays in the middle, clear of the HUD band, and the new border holds surroundings:
  - Meadow: crop fields with rows, hedges, fences, a farmhouse with a barn, a road, a pond, a forest edge, and the paddock with cows and farmer.
  - Island: a wider sea, a beach with waves along the shore, palms, a jetty, and boats with decks and wakes.
  - Savanna: acacias, bushes, rocks, a dirt road, a waterhole, a small herd of zebras and an elephant.
- **Detailed scenery art, design system first.** New SVG sprites in `docs/design-system/assets/scenery/` for every scenery object (cows, farmer, birds, terminal, tower, hangar, windsock, parked vehicles, parked aircraft, trees, hedges, fences, farmhouse, barn, palms, boats, acacias, bushes, rocks, animals), new colour tokens with Day and Night values, a finer `line-fine` detail stroke, rules in `world.md`, `brand-book.md` and `components/Scenery.md` for how detailed scenery may be, and a Scenery section in the showcase (Day and Night). Then ported to Compose under `ui/sprites` with no literals.
- **Birds fly as a small flock** of three with flapping wings.
- Scenery stays decoration: no engine rule reads it, ambient life stays in `ui/game/ambient`, reduced motion still stops all scenery motion.

Out of scope: new gameplay (birds, boats or animals as obstacles), new levels, camera pan or pinch zoom, a Night level, sound, wind as a mechanic, changes to aircraft sprites or runway and helipad art, and the level-select tile design (its miniatures follow the new worlds without a redesign).

Depends on: the `levels` change (three levels, `LevelScenery`, `AmbientLife`, Dawn palette in code). This change takes over that change's open task 1.2 (scenery sprites in the design system) and the look of its drawing tasks 4.1 to 4.3. It touches the same engine distances as `smooth-flight-paths` and `redraw-flight-path` (path reach and point spacing) and as the issues #29 and #30 (runway snap, rollout speed); whichever lands second must apply the zoom factor to any new world-unit distance.

## Capabilities

### New Capabilities

- `world-scale`: how large a level's world is relative to the screen, and which world-unit values follow the zoom factor so aircraft keep their on-screen size, speed and pick area.
- `scenery-art`: what the airfield scenery and surroundings of each level look like, how detailed they may be, and how they stay apart from aircraft, paths and landing zones.

### Modified Capabilities

None. The level themes live in the unarchived `levels` change; the existing `first-level` and `flight-path-input` requirements (runway in the middle, 72dp pick radius) still hold unchanged.

## Impact

- Design system: `tokens.json` (colours, `line-fine`), `assets/scenery/*.svg`, `world.md`, `brand-book.md`, `components/Scenery.md`, `showcase.html` (+ PDF), regenerated `ui/theme` (`FaColors`, `FaDimens`, `DawnColors`).
- `shared` `domain`: `LevelDefaults` (zoom factor, HUD band, margins, distances), `DefaultAircraftSpecs` (radius, speed), `Levels.kt` (worlds and layouts), `SceneryKind` (new kinds), `engine/EngineConfig` (reach radius, point spacing).
- `shared` `ui`: `ui/sprites/SceneryDrawing.kt` and new scenery sprite files, `ui/game/AirfieldDrawing.kt` and `LevelBackdrop.kt` drawing through the sprites, `ui/game/ambient/AmbientLife.kt` (bird flock, wing phase, sizes).
- Tests: `AircraftTypeSpecTest`, `LevelsTest`, `LevelLayoutsTest`, `LevelConfigValidatorTest`, `AmbientLifeTest`, `SpawnerTest` where they pin values.
- Docs: `docs/game-design.md` (speed table, world size), `docs/architecture.md` (zoom factor, scenery sprites).
- No new dependencies.
