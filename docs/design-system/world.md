# World and rendering

How the airfield is drawn, bottom to top. Each layer names its `z-` token. Everything on the ground follows the nine world rules W1 to W9 of `brand-book.md`; this file works them out.

| Layer | Token | What is on it |
|---|---|---|
| Field | `z-field` | Bottom to top: flat ground (lawn or sea, patches, roads, fields, water, aprons, taxiways, runways and helipads), then the one layer of hard ground shadows, then standing scenery from north to south and ambient life (see Scenery). |
| Zone glows | `z-zone` | `zone-ok` and `zone-no` halos around landing zones. |
| Paths | `z-path` | Flight paths, end dots, straight-on projections. |
| Ground shadows | `z-ground-shadow` | Every aircraft's soft shadow, so no shadow ever falls on another aircraft. |
| Aircraft | `z-aircraft` | Sprites, selection ring, callsign tag. |
| Effects | `z-fx` | Star burst, poof, score popups. |
| Clouds | `z-clouds` | Ambient clouds at `opacity-cloud`, each over its own soft shadow. |
| HUD | `z-hud` | Flight strips, stickers, the pause button, coach marks. |
| Overlay | `z-overlay` | `scrim` and dialogs. |

There are two kinds of shadow, and they never share a layer (W1). Things on the ground cast a hard shadow inside the field layer. Things in the air cast a soft shadow on `z-ground-shadow`, further from their sprite. Altitude reads from the difference.

## Aircraft roster

| Type | Kind | Speed | Turn rate | Lands on | Drawn size |
|---|---|---|---|---|---|
| Light prop | Airplane | Slow | High | Runway | `sprite-light-prop` |
| Turboprop | Airplane | Medium | Medium | Runway | `sprite-turboprop` |
| Jet | Airplane | Fast | Low | Runway | `sprite-jet` |
| Jumbo | Airplane | Medium-fast | Very low | Runway | `sprite-jumbo` |
| Helicopter | Helicopter | Slow | Very high | Helipad | `sprite-helicopter` |

Drawn size grows with how hard the aircraft is to handle. Speed and turn values are gameplay configuration and are not design tokens.

## Sprite construction

- Draw on a 96-unit square, nose up, centred on the aircraft's pivot.
- 4-unit `outline` stroke (`line-hero`), round joins and caps, on every shape. No other thing on screen takes this weight (W4).
- Body `fuselage`; engines and nacelles `fuselage-shade`; wings, tail or a body stripe in the livery colour.
- One cockpit window in `glass`, at least a quarter of the nose width, with one `glass-shine` streak.
- Propellers are a single dark bar that flicks in width; a rotor is a `prop-blur` disc with a spinning dark cross.
- No text, numbers or logos on an aircraft.

## Livery and path colour

Assign `path-1` to `path-6` in spawn order and free a colour when its aircraft lands or leaves. With more than six aircraft on screen colours repeat; the path still starts at its own aircraft, and the selected aircraft has its ring, so the link never rests on colour.

## Landing zones

Runways and helipads keep the footprint and world size they had in version 1, so no landing rule changes. They are the only ground shapes stroked in `outline` (W6), at `line-zone` (W4). Around each lies a transition (W7). Sizes below are in world units.

### Runway

- **Asphalt.** `asphalt-500`, 64 units wide, any length from 240 units. `outline` at `line-zone`.
- **Shoulder.** `concrete-500` with a `concrete-700` edge line, reaching 9 units beyond each side and 10 beyond each end.
- **Fringe.** Rough grass in `grass-700`, reaching 22 units beyond each side of the asphalt and 24 beyond each end. It has no line of its own.
- **Markings**, all in `marking`: an edge line along both sides; six threshold bars at each marked end; the runway number, read from its own approach end; two aiming blocks and, on a long runway, touchdown marks behind them; a dashed centre line. Tyre rubber is `asphalt-700`. Numbers follow real convention: heading divided by ten, the two ends 18 apart (`09/27`).
- **Lights.** `beacon` edge lights on the shoulder every 58 units along both sides, four `mint-500` threshold lights just outside each marked end, and four approach lights in the fringe beside one end, on a one-way runway the landing end: two `cherry-500`, two `foam`.
- **Short runways.** A runway shorter than 460 units keeps its threshold bars, number and lights and drops the aiming blocks and tyre rubber. Touchdown marks need 560 units.
- **One-way runway.** Only the landing end is marked (threshold bars, number, aiming blocks, lights); the far end is plain asphalt. The centre line is a row of chevrons pointing the landing way. The silhouette differs from a two-way runway, so the difference does not rest on colour. See `components/Runway.md`.

### Helipad

- **Disc.** `asphalt-500`, 112 units across, `outline` at `line-zone`, with a worn `asphalt-700` ring, the `marking-warn` ring and the `marking` H.
- **Pad.** A `concrete-300` square 138 units across with a `concrete-700` edge line, `concrete-500` slab joints and a `beacon` light in each corner.
- **Fringe.** `grass-700`, 7 units beyond the pad on every side.

### Taxiways

A taxiway is flat ground, not a landing zone: `asphalt-300`, lighter than a runway, 29 units wide, with a `marking-warn` centre line, so it never looks like a place to land. It is edged in `concrete-700` (2 units on each side) inside a `grass-700` verge (5.5 units on each side) and has no `outline` (W6). A hold-short bar is two solid and two dashed `marking-warn` lines across it.

### Zone halos

Zone feedback appears only while an aircraft is selected and its path ends on the zone. A valid landing is a solid, pulsing `zone-ok` halo; the wrong zone is a dotted `zone-no` halo. The shape differs as well as the colour. A runway's halo follows its rectangle just outside the asphalt; a helipad's is a circle just outside the disc.

## Tutorial overlays

- **Zone highlight** (layer `z-zone`): a dashed outline `highlight-margin` (`space-3`) outside the runway rectangle or the helipad circle, a 4px `sun` dash over a 6px `outline` casing, dashes from `path-dash` and `path-gap`, marching on `dur-march` (still with reduced motion). A chunky arrow in the same style stands outside the landing end and points along the landing direction. It never fills the zone.
- **Drag demo**: a dotted path in `on-outline` with an `outline` casing (`path-dot`) from the aircraft to the landing end, and a `finger` icon at `icon-lg` travelling along it. See `motion-audio.md`.

## World zoom

A level's world is 1920 by 1080 world units: the 1280 by 720 playfield times one zoom factor of 1.5 (the middle of 1 and 2), the same for every level. Runways, helipads, landing zones and scenery keep their world sizes, so the airfield takes a smaller share of the screen in the middle, with open surroundings on every side. Runway and helipad markings (threshold bars, centre line, aiming blocks, numbers, the helipad ring and H) shrink with the airfield: each is drawn in world units, in the proportions of the 64-unit runway and the 112-unit helipad here. Everything that belongs to an aircraft or to the player's finger is multiplied by the same factor, so it is unchanged on screen: aircraft size and speed, the proximity warning, spawn spacing and margins, the path reach and point spacing, the HUD band, and the touchdown point the runway lead-in ends at. Path strokes, the pick area and all UI are in dp and do not change.

On the reference phone (800 by 360dp, landscape) one world unit is 0.333dp: a runway is 21.3dp wide and a helipad 37.3dp across.

## Scenery

Scenery is everything on the ground that is not a landing zone: buildings, vehicles, plants, animals, people, boats, fields, roads and water. It makes the airfield a place and has no effect on play. It must be recognisable at a glance and must never compete with aircraft, paths or landing zones.

### Light and tone

- **One sun (W1).** Every sprite is lit from the top-left: the lit tone of a material faces north-west, the shade tone south-east.
- **Three flat tones (W2).** A material is drawn in flat shapes of its ramp: a lit step, a base step and a shade step. No gradients, textures or blurs. Crop rows and wave lines are flat stripes.
- **Night and Dawn from the same sprites.** Only token values change between themes. A window takes `window`, glass by day and a warm light in Night and Dawn.

### Materials

- **Three main materials (W2).** A sprite uses at most three main materials (`WORLD_MAX_MATERIALS`). `wood` and `soil` count as one material; so do `leaf`, `pine` and `grass`.
- **Trim.** On top of those a sprite may name up to two trim materials in its `trim` field, for small parts such as a chimney or an awning. The farmhouse names `stone`; the terminal names `roof` and `wall`.
- **Carriers.** A sprite that only carries other things sets `carries` and is exempt from the count. The container vessel is the one that does.
- **What is not a material (`WORLD_TRIM`).** Glass, lamps, paint and small darks do not count: `outline`, `glass`, `glass-700`, `glass-shine`, `window`, `beacon`, `muzzle`, `foam`, `marking`, `marking-warn`, `shadow-cast`, `asphalt-900` and `mint-500`.
- **Aircraft colours are banned (W9).** No sprite uses `fuselage`, `fuselage-shade`, `sun-500` or a path colour (`WORLD_BANNED`), and none uses a UI ramp (`sun`, `sky`, `cherry`, `tangerine`, `mint`, `paper`). Whites are cream: `wall-100` or `hide-100`. Three accents remain (`WORLD_ACCENT`): the tower cab and the runway's approach lights are `cherry`, the windsock is `tangerine`.
- **Water** is the `water` ramp everywhere: the sea, a pond and a waterhole.

### Lines

- **Navy silhouette (W3).** The outer shape of everything with height is stroked in `outline` at `line-ui`, 2.5 units; small sprites and small parts take `line-ui-sm`. An aircraft keeps `line-hero`, so it always carries the heaviest line (W4).
- **Tinted inner lines (W3).** A line inside the silhouette is the 700 or 900 step of the material it lies on, at `line-detail` (up to 1.5 units): tile joints in `roof-700`, a ridge in `roof-900`, mullions in `glass-700`, plank joints in `wood-900`.
- **Strokes shrink with the airfield.** Sprite strokes are measured in world units, the same number as their token in dp, so they scale with the field like the sprite itself.

### Height tiers

Every sprite carries a height tier, the silhouette of its cast shadow and whether it may rotate (W5). The tier sets how far the shadow is thrown down-right, in world units (`TIER_SHADOW`).

| Tier | What | Shadow throw | Rotates |
|---|---|---|---|
| flat | Waves, wakes, crop tiles, and the small ground details (tufts, flowers, reeds, lilies, ducks) | None | Yes |
| S | Low things: bush, hedge piece, rock, hay bale, fence piece, car, fuel truck, baggage cart, parked aircraft, cow, zebra, elephant, farmer, bird, sailboat, yacht, container vessel, jetty | 3 by 4 | Yes |
| M | Buildings: farmhouse, barn, terminal, hangar | 12 by 14 | No |
| L | Tall things: tree, pine, acacia, palm, silo, control tower, windsock | 11 by 13, from a raised crown | No |

- The tier's throw is the default. A sprite sets its own `off` near it: a larger crown or animal throws a little further, a fence piece a little less.
- **Top-down sprites rotate.** Vehicles, animals, people and boats are drawn strictly top-down with the front up, so they can face any way. Their shadow is their silhouette, offset by the throw outside the rotation, so it always falls down-right.
- **Standing sprites never rotate (W5).** Their `rotates` flag is off, and a level may not give them a rotation. Buildings and the control tower have their shadow baked in place (`off` is 0, 0) as the shadow of a block.

### The south wall

A building, a tree and the control tower show their top face above a visible south-facing wall or trunk (W5). A building's wall carries at least one door or window; the east end of a wall and the east side of a roof are in the shade tone (W1). Tier M and tier L differ in what stands on the ground: a building is a roof over a wall, a tall thing is a crown or a top over a narrow trunk, post or drum.

### The shadow layer

All hard ground shadows are drawn in one layer, between flat ground and standing scenery, in `shadow-cast` at `opacity-shadow-cast` (W1). Because the layer is filled once and then made translucent as a whole, overlapping shadows never darken twice and no shadow falls on a neighbour's canopy. Flat sprites cast no shadow. Shadows have a hard edge; only aircraft and clouds have a soft one.

### Flat ground and transition bands

Flat ground has no `outline` (W6): it is edged in a darker step of its own material. Where two ground materials meet, a band lies between them (W7). The pieces and their bands, from the inside out, in world units:

| Piece | Surface | Bands |
|---|---|---|
| Runway | `asphalt-500`, 64 wide | `concrete-500` shoulder, 9 at the sides and 10 at the ends; `grass-700` fringe to 22 at the sides and 24 at the ends |
| Helipad | `asphalt-500` disc, 112 across | `concrete-300` pad, 138 square; `grass-700` fringe, 7 |
| Taxiway | `asphalt-300`, 29 wide | `concrete-700` edge, 2 per side; `grass-700` verge, 5.5 per side |
| Apron | `concrete-300` slab with `concrete-500` joints | `concrete-700` edge line; `grass-700` verge, 6 |
| Car park | `concrete-300` with `foam` bay lines | `concrete-700` edge line; `grass-700` verge, 5 |
| Road | `road-500`, 25 wide, `foam` centre dashes | `road-700` kerb, 2 per side; `grass-700` verge, 5.5 per side |
| Dirt track | `sand-300`, 11 wide by default, one `sand-500` rut | `sand-500` edge, 2 per side |
| Pond, waterhole | `water-500` open water inside `water-300` shallows | `water-700` shore line; `sand-300` band, 4.5; damp `grass-700` band to 11 from the shore |
| Crop field | `straw-300` or `soil-500` with rows every 9 | Edge line in `straw-700` or `soil-700`; `grass-700` verge, 6 |
| Paddock | `grass-400` | `grass-700` edge line under the fence |
| Shore of an island | `sand-300` beach | `sand-500` edge line; `water-300` shallows, 22; a broken line of `foam` just off the beach |

`components/Ground.md` describes each piece.

### Mown airfield, unmown countryside

Mowing stripes appear only inside the airfield, parallel to its main runway (W8). They are two neighbouring grass steps, `grass-500` and `grass-600`, each stripe 52 units wide, so they stay quiet. The airfield's boundary is a soft `grass-700` line. Ground outside the airfield is never striped: it is one flat tone with irregular patches in `grass-400` and `grass-600`, and no patch lies inside the airfield.

### Detail in clusters

Tufts, flowers, reeds and props gather in small clusters around objects and edges, and open ground rests between them (W8). A cluster is three tufts or five flowers. No detail lies on a runway, a helipad, an approach end, a road or a field, or inside the airfield.

### Minimum sizes

Measured on the reference phone (800 by 360dp), where one world unit is 0.333dp:

| What | At least | In world units |
|---|---|---|
| Animals, people and vehicles, longest side | 10dp | 30 |
| A bird, wing tip to wing tip | 8dp | 24 |
| Any inner detail (ear, leg, window, crop row) | 1.5dp | 4.5 |

Detail that would fall under 1.5dp is left out of the sprite rather than drawn. Sizes are level data or ambient configuration in world units, never numbers in drawing code.

### Sprite format

- A sprite is drawn around its own origin, in world units, top-down with the front pointing up.
- Sources live in `bundle.js` (`WORLD`), with a paint key on every shape: a fill token, a fill with a stroke token and a width, or a stroke only. `assets/scenery/*.svg` are generated from them, shadow included, in Day values.
- Each sprite has a `tier`, `rotates`, a shadow silhouette (`sh`) and its throw (`off`), and optionally `trim` and `carries`.
- Repeating scenery is one piece of a row: a hedge and a fence are drawn piece after piece along their length and share one outline, a wood is its trees placed with a minimum spacing, and a crop field repeats its tile or stretches its construction.
- The small ground details (`DETAILS`) are drawn straight on the ground, without an outline or a shadow.

### What the generator checks

`tools/gen-kotlin.cjs` refuses a sprite that breaks one of these, and names it:

- every shape has a paint key, and every paint key names a colour token that exists and is not a version 1 token;
- no banned colour and no UI ramp, except the sprite's own accent (W9);
- at most three main materials, at most two trim materials that the sprite really uses, unless it `carries` (W2);
- a sprite with height has an `outline` stroke (W3) and a cast shadow (W1);
- a flat sprite has no `outline` stroke (W6);
- the tier is one of flat, S, M or L, and a tier M sprite does not rotate (W5).

Everything else is checked by review, with the design-critic agent and the showcase: the light direction and the flat tones (W1, W2), that inner lines are tinted and not navy (W3), line weights (W4), the south wall and that tier L sprites do not rotate (W5), the transition bands of ground pieces (W7), stripes and clusters (W8), and the minimum sizes.

### Parked aircraft

One generic sprite, on aprons only. It is airfield scenery in `tin` tones with a `glass` window: no livery colour, no cockpit shine, and a propeller that stands still. It is tier S and shrinks with the airfield, so it is far smaller than any aircraft in play. It cannot be picked and no rule reads it.

### Where scenery may go

- Never over a runway, a helipad or a landing zone. The level validator rejects a level where it does.
- Under the HUD band only flat ground: lawn, fields, roads, water and sand. No building, tree, vehicle, animal or person stands there, and no bird or boat passes there.
- Layering inside the field layer: ground (lawn or sea and island), flat scenery (patches, tracks, roads, fields, ponds, aprons, taxiways), runways and helipads, the shadow layer, standing scenery from north to south, ambient life.

### Surroundings per landscape

What the three reference scenes in `bundle.js` contain:

| Landscape | On the airfield | Around it |
|---|---|---|
| Meadow | A two-way runway, a helipad, an apron with two parked aircraft and a fuel truck, two taxiways, the control tower, the terminal, a hangar, two windsocks, a car park | A road with a side road and cars, a dirt track to a farm with a farmhouse, a barn, a silo, an old oak and a farmer, four crop fields with hay bales, a hedge row, a pond with reeds, lilies and ducks, a paddock with cows, rocks and bushes, woods of trees and pines |
| Island | A two-way runway, a helipad, an apron with a parked aircraft and a baggage cart, two taxiways, the control tower, the terminal, a windsock | A wide sea in `water-500` with darker patches, a beach with shallows and foam around the island, palms, bushes and rocks, a jetty, two sailboats, a yacht and a container vessel with their wakes, and waves |
| Savanna | A one-way runway, a helipad, an apron with a parked aircraft and a fuel truck, two taxiways, the control tower, a hangar, a windsock | Dry ground in `grass-400` with `sand-300` patches, a wide dirt track with a branch, a waterhole with reeds, acacias, bushes and rocks, a small herd of zebras and an elephant. Tufts, no flowers |

### Motion

Birds flap between two wing frames and boats trail a wake; cows wander inside their fence. Waves and the savanna animals stand still. With reduced motion nothing moves: no bird or boat appears and cows stand where they are.

## Screen edges

Aircraft enter from the viewport edges. Keep the top 88px free of entries so nothing spawns under the HUD, and announce each entry in the Next Aircraft Hint before it appears.
