# World and rendering

How the airfield is drawn, bottom to top. Each layer names its `z-` token.

| Layer | Token | What is on it |
|---|---|---|
| Field | `z-field` | Lawn in `grass-500` with `grass-700` mowing stripes 64px wide, tilted 12 degrees. Ground scenery, runways, helipads, then standing scenery and ambient life (see Scenery). |
| Zone glows | `z-zone` | `zone-ok` and `zone-no` halos around landing zones. |
| Paths | `z-path` | Flight paths, end dots, straight-on projections. |
| Ground shadows | `z-ground-shadow` | Every aircraft's shadow, so no shadow ever falls on another aircraft. |
| Aircraft | `z-aircraft` | Sprites, selection ring, callsign tag. |
| Effects | `z-fx` | Star burst, poof, score popups. |
| Clouds | `z-clouds` | Ambient clouds at `opacity-cloud`. |
| HUD | `z-hud` | Score, streak, next aircraft, lives, pause, coach marks. |
| Overlay | `z-overlay` | `scrim` and dialogs. |

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
- 4-unit `outline` stroke, round joins and caps, on every shape.
- Body `fuselage`; engines and nacelles `fuselage-shade`; wings, tail or a body stripe in the livery colour.
- One cockpit window in `glass`, at least a quarter of the nose width, with one `glass-shine` streak.
- Propellers are a single dark bar that flicks in width; a rotor is a `prop-blur` disc with a spinning dark cross.
- No text, numbers or logos on an aircraft.

## Livery and path colour

Assign `path-1` to `path-6` in spawn order and free a colour when its aircraft lands or leaves. With more than six aircraft on screen colours repeat; the path still starts at its own aircraft, and the selected aircraft has its ring, so the link never rests on colour.

## Landing zones

- A runway is 64px wide at 1x. Its numbers follow real convention: heading divided by ten, the two ends 18 apart (`09/27`).
- A one-way runway marks only its landing end (threshold bars, touchdown patch, number) and draws its centre line as chevrons pointing the landing way; see `components/Runway.md`.
- A helipad is 112px across, with a `marking-warn` ring and a `marking` H.
- Zone feedback appears only while an aircraft is selected and its path ends on the zone.

## World zoom

A level's world is 1920 by 1080 world units: the 1280 by 720 playfield times one zoom factor of 1.5 (the middle of 1 and 2), the same for every level. Runways, helipads, landing zones and scenery keep their world sizes, so the airfield takes a smaller share of the screen in the middle, with open surroundings on every side. Runway and helipad markings (threshold bars, centre line, touchdown patches, numbers, the helipad ring and H) shrink with the airfield: each is drawn as its token's value in world units, the proportions of the 64px runway and 112px helipad here. Everything that belongs to an aircraft or to the player's finger is multiplied by the same factor, so it is unchanged on screen: aircraft size and speed, the proximity warning, spawn spacing and margins, the path reach and point spacing, the HUD band, and the touchdown point the runway lead-in ends at. Path strokes, the pick area and all UI are in dp and do not change.

On the reference phone (800 by 360dp, landscape) one world unit is 0.333dp: a runway is 21.3dp wide and a helipad 37.3dp across.

## Scenery

Scenery is everything on the ground that is not a landing zone: buildings, vehicles, plants, animals, people, boats, fields, roads and water. It makes the airfield a place and has no effect on play. It must be recognisable at a glance and must never compete with aircraft, paths or landing zones.

### How detailed scenery may be

- **Lighter than aircraft.** Scenery is outlined in `line-2`; an aircraft keeps `line-3`. Inner details (patches, windows, roof ribs, crop rows, leaf clusters) are strokes in `line-fine`, the thinnest stroke in the system, used nowhere else.
- **Strokes shrink with the airfield.** Scenery strokes are measured in world units, 0.8 units per dp of their token, so they scale with the field like the sprite itself and are never wider on screen than their token. On the reference phone the outline is about 0.5dp, against 1.2dp for an aircraft.
- **No busier than an aircraft.** A scenery sprite uses at most four fill colours, counting `outline` used as a fill. That is what the light prop uses.
- **Water is `sky-500`** everywhere: the sea, a pond and a waterhole, so it reads as water in Day, Night and Dawn.
- **Its own colours.** Scenery never uses `path-1` to `path-6`, `sun` or the `fuselage` white, so nothing on the ground looks like an aircraft or its path. Two older parts keep an accent: the tower cab is `cherry` and the windsock is `tangerine`. Sand, soil, roofs, foam and stone have their own tokens: `sand-300`, `soil-500`, `soil-700`, `roof-500`, `foam`, `stone-300`.
- **On the ground.** Scenery never casts the airborne shadow (`shadow-ground`, `shadow-float`). A tree may lie on a flat `grass-900` shade, drawn as part of the sprite, touching its canopy and without an outline of its own.
- **Flat and outlined.** Single flat fills, round joins, no gradients or textures. Crop rows and wave lines are flat stripes, like the mowing stripes of the lawn.

### Minimum sizes

Measured on the reference phone (800 by 360dp), where one world unit is 0.333dp:

| What | At least | In world units |
|---|---|---|
| Animals, people and vehicles, longest side | 10dp | 30 |
| A bird, wing tip to wing tip | 8dp | 24 |
| Any inner detail (ear, leg, window, crop row) | 1.5dp | 4.5 |

Sizes are level data or ambient configuration in world units, never numbers in drawing code.

### Sprite construction

- Draw top-down with the front pointing up, on a grid whose unit is one world unit at the size the sprite is placed at. The outline is 3 units, a detail stroke 1.5.
- Sources are `assets/scenery/*.svg`; the same shapes live in `bundle.js` (`SCENERY`) with a paint key on every shape. The generator checks the fill budget and the colour rule, and refuses a sprite that breaks them.
- Repeating scenery is one piece of a row: a hedge and a fence are drawn piece after piece along their length, a forest edge is the tree in a staggered row, and a crop field repeats its tile.

### Parked aircraft

One generic sprite, on aprons only. It is airfield scenery, 67.5 world units square, and shrinks with the airfield, so it is far smaller than any aircraft in play; painted `fuselage-shade` with a `glass` window, has no livery colour and no shadow, squared wing tips and a propeller that stands still. It is scenery: it cannot be picked and no rule reads it.

### Where scenery may go

- Never over a runway, a helipad or a landing zone. The level validator rejects a level where it does.
- Under the HUD band only flat ground: lawn, fields, roads, water and sand. No building, tree, vehicle, animal or person stands there, and no bird or boat passes there.
- Layering inside the field layer: ground (lawn or sea and island), flat scenery (fields, roads, ponds, aprons, taxiways, jetties), runways and helipads, standing scenery, ambient life.

### Surroundings per landscape

| Landscape | Around the airfield |
|---|---|
| Meadow | Crop fields with rows, hedges, fences, a farmhouse and a barn, a road, a pond, a forest edge, and a paddock with cows and a farmer. A flock of three birds crosses now and then. |
| Island | A wide sea, a `sand-300` beach around the island with `foam` waves along the shore, palms, a jetty, and boats with a deck and a wake. |
| Savanna | Acacias, bushes, rocks, a dirt road in `sand-300`, a waterhole, a small herd of zebras and an elephant. |

### Motion

Birds flap between two wing frames and boats trail a wake; cows wander inside their fence. Waves and the savanna animals stand still. With reduced motion nothing moves: no bird or boat appears and cows stand where they are.

## Screen edges

Aircraft enter from the viewport edges. Keep the top 88px free of entries so nothing spawns under the HUD, and announce each entry in the next-aircraft chip before it appears.
