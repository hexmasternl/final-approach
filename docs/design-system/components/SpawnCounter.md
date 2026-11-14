How many aircraft of one type have spawned in the running level, in a flight strip.

**You provide:** the aircraft type and the count.

**Construction.** A Flight Strip without a label cell (`fa-strip fa-spawncounter`). In its value, `space-2` apart: the aircraft sprite, nose up, flat (no soft shadow) and painted in the `fuselage` colour instead of a livery, then the count in `strip-value` `ink` with tabular figures. No label: the silhouette names the type, so colour never carries it alone.

The strip is flat, like every readout (U2).

The accessible name is the type and the count: "Jet 3".

One counter per aircraft type the level spawns, in a row under the score strips (`fa-hud__counters`, see Hud Bar). Types the level never spawns get no counter. The count starts at 0 when the level starts or restarts and only ever goes up; landings do not change it. It does not animate.

**Don't** show a counter for a type the level does not spawn, or add a sixth counter. **Don't** give the sprite a path colour: a livery belongs to one aircraft on screen.

In version 1 this was a HUD chip. See `FlightStrip.md`.

**Compose.** A `FaFlightStrip` with the sprite and the count as its value.
