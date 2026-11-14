A reference composition of a game in progress, showing how every world and HUD piece layers.

**You provide:** nothing: this is a showcase, not a component. The picture of each level is the element `fa-scene` (`level="meadow"`, `"island"` or `"savanna"`); the HUD lies over it.

Read it bottom to top as the render order:

1. Flat ground: lawn, patches, roads, fields, water, the mown airfield, aprons, taxiways, the helipad and the runway with its valid-landing halo.
2. The one layer of hard ground shadows.
3. Standing scenery, from north to south.
4. Flight paths, end dots and the straight-on projection.
5. The soft shadows of aircraft, then the aircraft, the selection ring and the callsign tag.
6. Clouds over their own soft shadows.
7. The HUD: the score strip and the streak sticker on the left; the Next Aircraft Hint, the Lives Meter and the pause button on the right.

The showcase has three game screens:

- **Meadow Field**, Day. A jet is selected and cleared onto runway 27; a helicopter's idle path ends on the helipad; a jumbo has no path and shows its straight-on projection.
- **Island**, Day. The airfield fills the island; the helipad shows the valid-landing halo.
- **Savanna**, Dawn. A one-way runway: only the landing end is marked.

In each, aircraft, paths and landing zones read before any scenery: they carry the heaviest lines and the most saturated colours (W4, W9).

Scenes are reference compositions for the showcase and the level cards. The game draws its levels from level data.
