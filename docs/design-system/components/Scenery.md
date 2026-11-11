Decoration that makes a level read as an airfield: taxiways, aprons, a terminal, a control tower, hangars, a windsock and trees. It has no effect on play.

**You provide:** level data. Each item is a `SceneryItem` with a kind, a centre, a width and a height in world units, and a rotation. Items scale with the world, like runways and helipads.

**Construction.** Flat fills with a `line-3` `outline` and round joins, corners `radius-sm`. No shadows: scenery is on the ground.
- Taxiway: `asphalt-500` strip.
- Apron: `asphalt-300` slab.
- Terminal: `surface-raised` block with a `sky-300` glass front.
- Control tower: `surface-raised` base with a round `cherry` cab.
- Hangar: `sky-500` block with an `outline` ridge line.
- Windsock: `tangerine` sock on an `outline` pole (see Windsock).
- Tree: round `grass-300` crown.

**Layering.** Taxiways and aprons lie under the runway; buildings, the windsock and trees are drawn after the runway and helipad, and before flight paths. Aircraft always fly over scenery.

**Rules.** Scenery never covers a runway, a helipad or a landing zone; the level validator rejects a level where it does. Keep the field clear: put buildings near the edges and away from the approach ends of runways.

**Don't** use scenery to carry meaning. It never marks a landing zone, a hazard or a state.
