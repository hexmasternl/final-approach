# Design

## Context

`detailed-scenery` introduced one world zoom factor, `LevelDefaults.WORLD_ZOOM` (1.25). Level worlds are the 1280 × 720 base world times it; every world-unit value that belongs to an aircraft or the finger is written as its base value times it, so the engine sees plain numbers and aircraft are unchanged on screen. Airfield values (runways, helipads, zones, scenery) do not follow it. Issue #48 asks to zoom out by a further factor two.

## Decisions

- **One constant.** `WORLD_ZOOM` becomes 2.5. All values that already follow it change with it; no runtime multiplier is added.
- **Layouts in the middle.** Each level's layout is shifted by half the growth of the world (800, 450), so the island stays centred on the world and every airfield stays in the middle below the HUD band. Roads and tracks that ran to an edge are lengthened to the new edge, and the new ring of surroundings gets its own scenery, below the HUD band for standing items.
- **Touchdown follows the zoom.** The lead-in starts two turning radii back and must never turn tighter than one turning radius. With turning circles doubled against a fixed touchdown point, an approach from the edge of the zone at the alignment tolerance no longer fits. Writing `TOUCHDOWN_DISTANCE` as 32 × zoom (40 at the former zoom, 80 now) keeps it flyable; touchdown plus rollout still ends on the shortest runway (80 + 2/3 × 340 < 340).
- **Parked aircraft is airfield scenery.** It was three quarters of the flying light prop, which grows with the zoom and would no longer fit an apron. It is now a fixed 67.5 units (its former size) and stays far smaller than any aircraft in play.
- **Markings at field scale.** Runway and helipad markings were dp tokens; on a 12.8dp runway the threshold insets overlap and the helipad ring radius would turn negative. Markings now take their token's value in world units (the design system's runway is 64 units wide with exactly these markings), and the runway number and H text scale with them. The outline stays `line-3` in dp. Scenery strokes become 0.8 world units per dp of their token (what they were at zoom 1.25) instead of dividing by the zoom.

## Risks / Trade-offs

- Runway numbers and the H get small on a phone (proportional to a 12.8dp runway) → accepted; it is what makes the airfield significantly smaller. The landing zones and the pick area are unchanged in world units and dp respectively.
- Scenery and ambient life shrink to half their on-screen size → accepted; world units in `world.md` stay, the dp equivalents are updated.
