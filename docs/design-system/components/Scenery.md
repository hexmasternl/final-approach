Decoration that makes a level read as a place: the airfield's buildings and vehicles, and the plants, animals, people and boats around it. It has no effect on play.

**You provide:** a sprite name and a size. The element is `fa-scenery` (`name`, `size`): one sprite with its cast shadow. In a level, each item has a kind, a centre, a size in world units and, where its kind allows, a rotation. Flat ground (roads, fields, water, aprons) is described in `Ground.md`.

**Construction.** Every sprite follows W1 to W9; the full rules are in `world.md`.

- Lit from the top-left, in flat tones of at most three main materials (W1, W2).
- An `outline` silhouette at `line-ui`; inner lines in a darker step of their own material at `line-detail` (W3, W4).
- A height tier that sets its shadow, and a flag that says whether it may rotate (W5).
- A hard shadow down-right, drawn in the one shared shadow layer in `shadow-cast` at `opacity-shadow-cast` (W1).
- No `fuselage`, no path colour, no `sun-500`. Whites are `wall-100` or `hide-100` (W9).

| Sprite | Tier | Rotates | Look |
|---|---|---|---|
| `tree`, `tree-small`, `tree-oak` | L | No | A lobed `leaf` canopy over a `wood` trunk. |
| `pine`, `pine-small` | L | No | Three star-shaped tiers in `pine` tones. |
| `acacia` | L | No | A wide, flat `leaf` crown on a bare `wood` trunk. |
| `palm` | L | No | Seven `leaf` fronds around the head of a leaning `wood` trunk. |
| `bush`, `hedge` | S | Yes | A low `leaf` shrub; the hedge is one piece of a row. |
| `rock` | S | Yes | A faceted `stone` boulder with a pebble. |
| `hay-bale` | S | Yes | A round `straw` bale with its spiral. |
| `fence` | S | Yes | One piece of a row: a `wood-300` rail between two posts. |
| `farmhouse` | M | No | A tiled `roof` over a cream `wall` with a door, shutters and windows; a `stone` chimney and doorstep. |
| `barn` | M | No | A curved `tin` roof over a `roof`-red wall with a cross-braced door. |
| `silo` | L | No | A round `tin` drum seen from above. |
| `tower` | L | No | A `wall` base, a `tin` block, a `wall` shaft and an eight-sided `glass` cab under a `cherry` roof with a `beacon`. |
| `terminal` | M | No | A `tin` roof with a skylight over a glazed front in `window`, with an awning. |
| `hangar` | M | No | A vaulted `tin` roof over an open door with a nose showing. |
| `windsock` | L | No | See `Windsock.md`. |
| `car`, `fuel-truck`, `baggage-cart` | S | Yes | Top-down, front up: a `denim` car, a `tin` tanker behind a `roof`-red cab, a tug with two carts. |
| `parked-aircraft` | S | Yes | A small generic airplane in `tin` tones, on an apron only. No livery. |
| `cow`, `zebra`, `elephant` | S | Yes | Top-down, front up: `hide` coats with patches or stripes; the elephant is `stone`. |
| `farmer` | S | Yes | A `straw` hat over `pine`-green shoulders, with a fork. |
| `bird-up`, `bird-down` | S | Yes | The two wing frames of one bird. |
| `sailboat`, `yacht`, `container-vessel`, `jetty` | S | Yes | Cream hulls with `wood` decks; the vessel carries containers; the jetty is `wood` planks on posts. |
| `wave`, `wake` | flat | Yes | `foam` strokes on water. No outline, no shadow. |
| `crop-field`, `crop-wheat`, `crop-plough` | flat | Yes | One tile of a field. See `Ground.md`. |

**Layering.** Flat sprites lie with the ground. All others stand above the shadow layer, drawn from north to south so a nearer thing overlaps a further one, then the ambient life, and all of it before flight paths. Aircraft always fly over scenery.

**Rules.**
- Scenery never covers a runway, a helipad or a landing zone; the level validator rejects a level where it does.
- Only flat ground may lie under the HUD band at the top of the world; the validator rejects anything else there.
- A parked aircraft lies entirely on an apron; the validator rejects one that does not.
- Keep the field clear: put buildings beside the runway, never on its extended centre line.
- A standing sprite (tier M or L) is never rotated.

**Do** let trees overlap in a wood, and scale a sprite a little to vary a row. **Don't** use scenery to carry meaning: it never marks a landing zone, a hazard or a state. **Don't** give it a path colour, a livery or a soft shadow, and **don't** draw it heavier than an aircraft.

**Compose.** In the app each item is a `SceneryItem` of a `SceneryKind`. The app's sprites (`ui/sprites/ScenerySprites.kt`) are still generated from the version 1 sprites in `tools/scenery-v1.js`; they move to these sprites when the app's world is ported.
