# Proposal

GitHub issue: #14

## Why

The game has one placeholder airfield. Variety is the point of the roadmap's "Levels" milestone: three airfields with different layouts, scenery and light give the player a reason to come back, and a selection page lets them choose where to play.

## What Changes

- Add three levels as `LevelConfig` data (the model from issue #6), each with runways and helipads:
  1. **Meadow**: one runway and one helipad.
  2. **Island**: one main runway and a smaller runway that crosses it diagonally (the jumbo cannot use it), and one helipad.
  3. **Savanna**: two parallel main runways, a smaller runway crossing both (the jumbo cannot use it), and two helipads.
- Add per-runway rules for which aircraft types may land on it, as data on the runway.
- Add a level selection page: **Take off** on the main page opens it; it shows one square tile per level with a miniature of the airfield, scrolls horizontally when the tiles do not fit, and tapping a tile loads that level and starts it.
- Theme each level:
  - Meadow: green, trees around the field, grass near the airfield, a fenced paddock with a farmer and two strolling cows, an occasional bird.
  - Island: water around a centred island that the airport covers, with a yacht, a sailboat or a container vessel floating by now and then.
  - Savanna: dawn light, animated runway lights, a flashing red beacon on the control tower, decorations, and aircraft navigation lights and a headlight.
- Keep every light and blink gentle so it never annoys the player.

Out of scope: the game engine, spawning, landing and scoring (the game screen stays the placeholder that shows the chosen airfield), unlocking, best scores and star ratings on tiles, remembering the last chosen level, sound, any level beyond these three, and collisions with birds, boats or cows (they are scenery only).

Depends on: the main page (issue #3) and the game screen placeholder from it, and the `LevelConfig` model (issue #6, change `level-config`), which this change instantiates three times and extends with a per-runway accepted-aircraft set. Level one is the same airfield as issue #5 (first level); if that issue defines it differently, the two must be reconciled. The rules that use runway aircraft restrictions arrive with the landing work (roadmap milestone 4); this change only provides the data and a pure `accepts` check.

## Capabilities

### New Capabilities

- `level-selection`: the page that lists the levels as tiles and starts the chosen one.
- `level-layouts`: the three airfield layouts and which aircraft each runway accepts.
- `level-themes`: the look and ambient life of each level, including dawn lighting.

### Modified Capabilities

- `main-page`: **Take off** opens the level selection page instead of starting a game directly. (`main-page` is still an active change with no published spec, so this is recorded here and in `level-selection`, not as a spec delta.)

## Impact

- `shared/` `domain`: the three `LevelConfig` definitions and a `Levels` catalogue; `accepts(type)` on runways and helipads.
- `shared/` `ui`: `LevelSelectPage`, level tile miniature, level scenery renderer, ambient life state (pure, unit tested), screen flow in `App`.
- `ui/theme`: dawn palette, light-intensity and blink tokens, ambient timing config.
- Design system: dawn palette, lights, farm and boat sprites and the LevelCard square tile variant are added first, then regenerated into the theme.
- `docs/game-design.md` and `docs/architecture.md`: levels and screen flow.
- No new third-party dependencies.
