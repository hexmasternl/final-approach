How many aircraft of one type have spawned in the running level, in a HUD chip.

**You provide:** the aircraft type and the count.

A `hud-chip` tall chip with the same face as every HUD chip: `surface` fill, `line-3` `outline`, `radius-md`, `shadow-lip-sm`, `space-3` side padding. Inside, `space-2` apart: the aircraft silhouette at `icon-lg` (40px), nose up and in the `fuselage` colour, then the count in `title-md` `ink` with tabular figures. No label: the silhouette names the type, so colour never carries it alone.

The accessible name is the type and the count: "Jet 3".

One counter per aircraft type the level spawns, in a row under the score chips (see Hud Bar). Types the level never spawns get no counter. The count starts at 0 when the level starts or restarts and only ever goes up; landings do not change it. It does not animate.

**Don't** show a counter for a type the level does not spawn, or add a sixth counter.
