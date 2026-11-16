# Proposal

GitHub issue: #48

## Why

The levels are still too small: there is not enough space around the airports for aircraft to fly, turn and line up. The airfield has to take a much smaller share of the screen, with far more open airspace around it.

## What Changes

- Double the world zoom: `LevelDefaults.WORLD_ZOOM` goes from 1.25 to 2.5, so every level's world is 3200 × 1800 world units. Runways, helipads, landing zones and scenery keep their world sizes, so the airfield is half as large on screen as before.
- Everything that already follows the zoom (aircraft speeds and sizes, warning distance, spawn separation and margins, HUD band, path reach, point spacing and smoothing tolerance) follows it again, so aircraft look, move, warn and spawn on screen as before.
- The runway touchdown distance follows the zoom too (32 × zoom, so 80): the lead-in to it is flown on the aircraft's turning circle, which grows with the zoom, and stays flyable.
- Every level layout is moved to the middle of the larger world; roads and tracks run on to the world edges and new fields, hedges, forest edges, trees, bushes, rocks and animals fill the new surroundings.
- The parked aircraft becomes plain airfield scenery (67.5 world units square) that shrinks with the airfield, so it still fits its apron.
- Runway and helipad markings (threshold bars, centre line, touchdown patches, numbers, ring, H) and scenery strokes are drawn at the airfield's scale, so they still fit a runway that is 12.8dp wide on the reference phone.
- Update `docs/game-design.md`, `docs/architecture.md` and `docs/design-system/world.md`.

Out of scope: per-level zoom, camera panning or pinch zoom, new levels, changes to the landing zone sizes, the pick area or path strokes.

Depends on: `detailed-scenery` (#33), which introduced the world zoom and the `world-scale` capability this change modifies. `detailed-scenery` must be archived before this change is archived.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `world-scale`: the zoom factor is 2.5 (worlds of 3200 × 1800), the touchdown distance follows the zoom, and the airfield and its markings are half as large on screen as before.

## Impact

- `shared/` `commonMain`: `domain/LevelDefaults.kt` (zoom, touchdown distance), `domain/Levels.kt` (layouts), `domain/Scenery.kt` (parked aircraft size), `ui/game/AirfieldDrawing.kt` (markings at field scale), `ui/sprites/ScenerySpriteDrawing.kt` (scenery strokes).
- `commonTest`: world zoom, layout, viewport, collision, engine and lead-in tests updated to the new zoom.
- Docs: `docs/game-design.md`, `docs/architecture.md`, `docs/design-system/world.md`.
