Decoration that makes a level read as a place: the airfield's buildings and vehicles, and the fields, water, plants and animals around it. It has no effect on play.

**You provide:** level data. Each item is a `SceneryItem` with a kind, a centre, a width and a height in world units, and a rotation. Items scale with the world, like runways and helipads. Cows, the farmer, birds and boats are ambient life: the level only says where the paddock and the island are.

**Construction.** Every sprite comes from `assets/scenery/` (shown in the Scenery section of the showcase), drawn top-down with its front up. Flat fills, a `line-2` `outline`, details in `line-fine`, round joins, at most four fill colours. No shadows: scenery is on the ground. The full rules are in World and rendering.

| Kind | Look |
|---|---|
| Taxiway | `asphalt-500` strip. Flat. |
| Apron | `asphalt-300` slab with a fine `marking` edge line. Flat. |
| Field | `soil-500` with `soil-700` furrows and `grass-300` crop rows along its length. Flat. |
| Road | `asphalt-300` with a dashed `marking` centre line; on the savanna a `sand-300` dirt road with `soil-700` ruts. Flat. |
| Pond, Waterhole | `sky-500` water, like the sea, with `foam` ripples; the waterhole has a `sand-300` bank. Flat. |
| Jetty | `soil-500` planks on `soil-700` posts, reaching into the sea. Flat. |
| Terminal | `surface-raised` block with a roof edge and roof units, a `sky-300` glass front and two jet bridges. |
| Tower | `surface-raised` base, an eight-sided `sky-300` window ring and a `cherry` cab roof. |
| Hangar | `fuselage-shade` vaulted roof with curved ribs; a dark door mouth with `surface-raised` doors at the front. |
| Windsock | `tangerine` and `surface-raised` stripes on a pole (see Windsock). |
| Car, FuelTruck, BaggageCart | Parked on an apron: body, windows and wheels, at least 50 world units long. |
| ParkedAircraft | A small generic airplane in `fuselage-shade`, on an apron only. |
| Tree, ForestEdge | `grass-300` canopy of leaf clusters on a `grass-900` shade; a forest edge is a staggered row of trees. |
| Hedge, Fence | One piece repeated along the item's length: `grass-700` shrubs, `soil-500` posts and rail. |
| Farmhouse, Barn | `roof-500` roofs with a ridge; the farmhouse has a hipped roof, a chimney and a doorstep, the barn doors and a round `stone-300` silo. |
| Palm | `grass-300` and `grass-700` fronds around `soil-500` coconuts. |
| Acacia, Bush, Rock | A wide, flat-topped `grass-900` canopy with its trunk and branches showing; a low `grass-300` shrub; `stone-300` boulders. |
| Zebra, Elephant | Seen from above, standing still: stripes and a mane; ears, trunk and tusks. |

**Layering.** Flat kinds lie under the runway, in the order they are listed in the level. All other kinds are drawn after the runway and helipad, then the ambient life, and all of it before flight paths. Aircraft always fly over scenery.

**Rules.**
- Scenery never covers a runway, a helipad or a landing zone; the level validator rejects a level where it does.
- Only flat kinds may lie under the HUD band at the top of the world; the validator rejects anything else there.
- A parked aircraft lies entirely on an apron; the validator rejects one that does not.
- Keep the field clear: put buildings beside the runway, never on its extended centre line.

**Don't** use scenery to carry meaning. It never marks a landing zone, a hazard or a state. **Don't** give it a path colour, a livery or a shadow, and **don't** draw it heavier than an aircraft.
