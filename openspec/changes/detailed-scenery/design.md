# Design

## Context

- Levels are `WorldConfig(1280 × 720)`. `WorldViewport` fits the world to the screen uniformly, so on an 800 × 360dp landscape phone one world unit is 0.5dp. Aircraft are sized in world units (`AircraftTypeSpec.radius × 2.4`), so a light prop is about 29dp there, already about half its `sprite-light-prop` token. Zooming out with everything else equal would shrink it further.
- The pick radius is `touchAircraft` (72dp) converted to world units at input time, so it is already independent of the zoom.
- Scenery is `SceneryItem` level data drawn by `drawScenery` in `ui/game/AirfieldDrawing.kt` as single shapes. The meadow paddock, cows, farmer, bird, boats and the savanna acacias, bushes and rocks are drawn in `ui/game/LevelBackdrop.kt` with sizes in a private `Backdrop` object, and the savanna decorations are hard-coded lists there, not level data.
- Aircraft sprites and icons come from `docs/design-system/tools/gen-kotlin.cjs`, which turns the SVG sources in `bundle.js` into `SpritePart` lists with paint roles. The design system has no Scenery section in the showcase and no scenery SVGs (task 1.2 of the `levels` change is still open).
- `line-1` (2dp) is the design system's thinnest stroke; the brand book forbids hairlines.

## Goals / Non-Goals

**Goals:** one tunable zoom factor; aircraft, paths and the HUD unchanged on screen; every scenery object built from a design-system SVG through the generator; scenery placement as validated level data.

**Non-Goals:** a camera that moves, per-level zoom, changing aircraft or runway art, making scenery interactive.

## Decisions

### 1. Grow the world, scale what belongs to aircraft and the finger

The world becomes 1600 × 900 (zoom factor `Z = 1.25`). Values that describe aircraft or the player's finger are multiplied by `Z`; values that describe the airfield are not.

| Multiplied by `Z` | Kept in world units |
|---|---|
| Aircraft radius (sprite size, collision shape follow), speed | Runway length and width, helipad radius |
| `WARNING_DISTANCE`, `SPAWN_SEPARATION`, `SPAWN_MARGIN`, `LEAVE_MARGIN` | Landing zone depth, width and helipad zone radius |
| `HUD_BAND` | Island radii, paddock size, scenery item sizes |
| `EngineConfig` reach radius and minimum point spacing | |

Turn rates (rad/s), all timings, the landing tolerance and fractions stay; turning radius (`speed / turnRate`) then scales with `Z`, so aircraft turn the same on screen. On the reference phone (800 × 360dp, 0.4dp per world unit) runways become 25.6dp wide and helipads 44.8dp across.

Rejected: zooming everything uniformly (a light prop drops to about 23dp on a phone, against the toy-scale principle and the issue's readability constraint); a render-only camera around an unchanged 1280 × 720 playfield (aircraft would leave the play area while still on screen, and input, spawn edges and the miss rule would need a second coordinate space); shrinking only scenery in the old world (the airfield would keep its share of the screen, so there would be no room for surroundings).

### 2. One named factor, applied to defaults

`LevelDefaults.WORLD_ZOOM = 1.25`. Each scaled default is written as its old base value times `WORLD_ZOOM` (`HUD_BAND = 140.0 * WORLD_ZOOM`, `DefaultAircraftSpecs` speeds and radii, `EngineConfig` defaults), and each level's world is `WorldConfig(1280.0 * WORLD_ZOOM, 720.0 * WORLD_ZOOM)`. The engine still sees plain numbers. Resulting default speeds: helicopter 50, light prop 68.75, turboprop 93.75, jet 125, jumbo 162.5; ratios, and so the 15% rule, are unchanged.

Rejected: a runtime multiplier applied in the engine or viewport (hides real values from tests and from `game-design.md`, and every new distance would have to remember to go through it); hand-editing each number to its new value (loses the link to the factor, so the next re-tune repeats the work).

### 3. Reference phone and minimum drawn sizes

The reference screen is 800 × 360dp landscape. On it, animals, people and vehicles are at least 20dp on their longest side (50 world units), birds at least 16dp wingspan (40 world units), and no inner detail (ear, leg, window, row) is smaller than 3dp. These rules go into `world.md`. Sizes stay in world units in level data (`SceneryItem`) and in `LevelAmbienceConfig` / the ambient size config, never in the drawing code.

Rejected: sizing scenery in dp like UI (it would no longer sit at fixed places on the ground relative to runways across screen sizes).

### 4. Strokes: a lighter outline and a fine detail line for scenery

Scenery outlines use `line-2` (3dp); inner details (patches, windows, roof ribs, crop rows, leaf clusters) use a new token `line-fine` = 1.5dp. Aircraft keep `line-3`. A scenery sprite uses at most four fill tokens, counting `outline` used as a fill, the same as the light prop. The brand book's "no hairlines" rule is amended: `line-fine` is the thinnest stroke and is allowed only inside scenery sprites.

Rejected: `line-3` on scenery (swamps 20dp sprites and gives scenery the same weight as aircraft, which blurs the hierarchy); `line-1` for details (the issue asks for a finer token, and 2dp patches on a 22dp cow read as blobs).

### 5. New colour tokens

Add, each with Day and Night values and a Dawn value in `DawnColors`:

| Token | Use |
|---|---|
| `sand-300` | Beach, dirt roads, savanna tracks |
| `soil-500` | Ploughed field, trunks, fence and jetty wood |
| `soil-700` | Crop-row furrows |
| `roof-500` | Farmhouse and barn roofs |
| `foam` | Waves and boat wakes |
| `stone-300` | Rocks, elephant |

Existing tokens cover the rest: `grass-300/700/900` for canopy, hedges, crops; `sky-500` sea, `sky-300` pond and windows; `surface-raised` buildings, cows, zebras; `outline` cow patches and zebra stripes; `cherry` tower cab; `asphalt-300/500` apron and roads; `tangerine` and `fuselage` windsock. Scenery never uses `path-1` to `path-6`, `sun` (the signature colour) or `fuselage` for whole bodies (it is the aircraft colour; the parked aircraft uses `fuselage-shade`). Today's use of `tangerine-deep` for fences and trunks moves to `soil-500`.

Rejected: reusing `sun` for sand and `tangerine-deep` for wood (both carry meaning in the UI and would compete with the score and danger accents).

### 6. Sprites flow through the generator

Each scenery SVG goes in `docs/design-system/assets/scenery/` and in `bundle.js` (so the showcase renders it), drawn top-down, front up, on a grid in design units equal to dp at its reference size, with paint roles as classes like the aircraft. `gen-kotlin.cjs` gains a scenery pass that emits `ui/sprites/ScenerySprites.kt`: a `SceneryPaint` role enum mapped to tokens at draw time and a `List<SpritePart>`-style list per sprite. A small `drawScenerySprite(sprite, rect, colors)` in `ui/sprites` scales the paths into the item's rectangle and strokes with `line-2` / `line-fine` in dp.

Rejected: hand-written Compose shapes (the rule is to port from the SVG sources, and the current hand drawing is exactly what made scenery abstract); bitmaps (vector drawing is preferred and bitmaps blur on large screens).

### 7. Scenery placement is level data

`SceneryKind` gains the new kinds (`Field`, `Road`, `Pond`, `Hedge`, `Fence`, `Farmhouse`, `Barn`, `ForestEdge`, `Car`, `FuelTruck`, `BaggageCart`, `ParkedAircraft`, `Palm`, `Jetty`, `Acacia`, `Bush`, `Rock`, `Waterhole`, `Zebra`, `Elephant`). The hard-coded savanna lists move into `Levels.kt`. Flat kinds (taxiway, apron, field, road, pond, waterhole, jetty) are ground scenery and draw under runways. The validator gains two rules: non-ground scenery must not intersect the HUD band, and a `ParkedAircraft` must lie inside an `Apron`. Fields draw their rows procedurally along the item's rotation; the beach is the existing shore ring in `sand-300` with static `foam` wave strokes.

Rejected: keeping placements in the renderer (the validator cannot check what is not data).

### 8. Ambient detail

The bird becomes a flock of three in a V with fixed offsets, sharing one position; `AmbientLife` exposes a wing phase from its clock (two frames, up and down, at least 0.25 s each). Wakes are drawn from the boat's direction and kind, with no extra state. Waves and animals other than cows are static. Under reduced motion no flock or boat appears (as today) and cows stand still.

Rejected: animated waves and grazing animals (more motion than the issue asked for, and more reduced-motion branches).

### 9. Parked aircraft

One generic small top-down airplane sprite in `fuselage-shade` with a `glass` window, drawn at 0.75 of the flying light prop's on-screen size, no shadow, `line-2` outline, only on aprons, never pickable (it is scenery, not engine state).

Rejected: reusing a flying aircraft sprite scaled down (same silhouette as an aircraft in play).

### 10. Night review without a Night level

No level uses Night yet. For the design review, a preview/debug entry draws each level with `NightColors` (and the savanna also with `DawnColors`). It is not a player-facing setting.

## Risks / Trade-offs

- Landing zones are 20% smaller on screen → harder landings. Mitigation: human check on a phone; if needed, raise the landing zone defaults in `LandingConfig` (config only, no spec change).
- Many more paths per frame → frame drops on low-end phones. Mitigation: parse each sprite once (lazy), and draw static scenery into a cached layer (`drawWithCache`) if profiling shows a cost.
- Parallel work on `EngineConfig`, rollout and path snapping (`smooth-flight-paths`, `redraw-flight-path`, issues #29 and #30) → a new world-unit distance may miss the factor. Mitigation: whichever lands second applies `WORLD_ZOOM`; the task list includes a grep for world-unit defaults.
- Scenery may still read as busier than aircraft at a glance. Mitigation: the sprite budget (fill-colour count, `line-fine` details) plus the design-critic review on screenshots.

## Assumptions

Each can be overruled by the issue author.

- Zoom factor 1.25 (1600 × 900 worlds), one value for all levels.
- Aircraft keep their on-screen size and speed; runways, helipads and landing zones shrink on screen with the world.
- Reference phone 800 × 360dp landscape; minimum sizes as in decision 3.
- Scenery outline `line-2`; new detail token `line-fine` = 1.5dp.
- New colour tokens as in decision 5.
- Birds fly as a flock of three; waves are static; savanna animals are a small zebra herd and an elephant, standing still.
- Parked aircraft: one generic sprite, rules as in decision 9.
- Under the HUD band only flat ground scenery is allowed.
- The design review uses a debug Night rendering, since no level is a Night level.
