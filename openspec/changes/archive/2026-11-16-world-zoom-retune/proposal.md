## Why

Levels are too small: there is not enough space around the airports for aircraft to fly. The author first asked to zoom out by a factor of two, then found that too much and asked for the middle ground between the size before and the size after that zoom.

GitHub issue: #48
GitHub issue: #56

## What Changes

- Every level world is zoomed out by one factor of 1.5, the middle of the original size (factor 1) and the doubled size (factor 2). The world grows from 1280 x 720 to 1920 x 1080 world units.
- Runways, helipads, landing zones and scenery keep their world sizes, so the airfield is drawn smaller on screen and there is more room around it.
- The factor is one configuration value (`LevelDefaults.WORLD_ZOOM`), shared by all levels, and everything that belongs to aircraft or the finger scales with it as `detailed-scenery` already defines (aircraft radius, speed, warning distance, spawn separation and margins, reach radius, point spacing, HUD band).
- This replaces the factor 1.25 in the `world-scale` capability of `detailed-scenery`. One change to that value, not two zooms on top of each other.

Out of scope: camera pan or pinch zoom, per-level zoom, and any new scenery; scenery for the extra border is covered only by the check that the larger border is filled with ground or water.

Depends on: `detailed-scenery`, which introduces `WORLD_ZOOM` and the `world-scale` capability. Apply this change after `detailed-scenery` is applied, or fold the value into it; archive after it, because this change modifies requirements that `detailed-scenery` adds. Related: `aircraft-speed-tuning` sets the zoom-1 speeds that are multiplied by this factor.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `world-scale` (added by `detailed-scenery`): the zoom factor is 1.5 and the world 1920 x 1080; the speed-order scenario follows `aircraft-speed-tuning`.

## Impact

- `shared` `domain`: `LevelDefaults.WORLD_ZOOM`, `Levels.kt` worlds and layouts, scaled defaults.
- `shared` `ui/game`: backdrop fill of the larger border.
- Docs: `docs/game-design.md`, `docs/architecture.md` (world size, zoom factor).
