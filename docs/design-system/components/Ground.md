The flat ground of a level: lawn, airfield, roads, fields, water and hard surfaces, each with the band that joins it to its neighbour.

**You provide:** level data: the shape of the airfield, the lines of roads and tracks, and the place and size of fields, ponds, aprons and paddocks. In the kit the pieces are methods of the `Scene` builder in `bundle.js`; `fa-scene` with `ground` shows a level's ground and scenery without aircraft.

**Construction.** Two rules hold for every piece. Flat ground has no `outline`: it is edged in a darker step of its own material (W6). Where two materials meet, a transition band lies between them (W7). Widths are in world units.

| Piece | Surface | Edge and band |
|---|---|---|
| Lawn | One flat tone: `grass-500`, or `grass-400` on dry ground. On an island the base is `water-500`. | None: it is the base everything lies on. |
| Mown airfield | `grass-500` with `grass-600` stripes, 52 wide, parallel to the runway (W8). | A soft `grass-700` boundary line. |
| Meadow patches | Irregular patches in `grass-400` and `grass-600`, outside the airfield only. | None: they are tone, not objects. |
| Tufts and flowers | `grass-700` and `grass-300` tufts; `wall-100` and `muzzle` flowers; `leaf-700` reeds and `leaf-500` lilies by water. | None. They come in clusters, on open ground only (W8). |
| Road | `road-500`, 25 wide, with `foam` centre dashes. Lighter than a runway, so it never looks like a place to land. | `road-700` kerb, 2 on each side; `grass-700` verge, 5.5 on each side. |
| Dirt track | `sand-300`, 11 wide by default, with one `sand-500` rut. | `sand-500` edge, 2 on each side. |
| Taxiway | `asphalt-300`, 29 wide, with a `marking-warn` centre line and hold-short bars. | `concrete-700` edge, 2 on each side; `grass-700` verge, 5.5 on each side. |
| Apron | A `concrete-300` slab with `concrete-500` joints and `marking-warn` stand marks. | `concrete-700` edge line; `grass-700` verge, 6. |
| Car park | `concrete-300` with `foam` bay lines. | `concrete-700` edge line; `grass-700` verge, 5. |
| Pond | `water-500` open water inside `water-300` shallows, with `foam` ripples. | `water-700` shore line; a `sand-300` band, 4.5; a damp `grass-700` band reaching 11 from the shore. |
| Crop field | Wheat: `straw-300` with `straw-500` rows. Green: `soil-500` with `leaf-300` rows. Ploughed: `soil-500` with `soil-700` furrows. Rows every 9. | An edge line in `straw-700` or `soil-700`; `grass-700` verge, 6. |
| Paddock | `grass-400` ground inside a fence. | A `grass-700` edge line under the fence. The fence is scenery: `wood-300` rail and posts with an `outline`, and a shadow. |
| Hedge row | Not ground, but laid like it: `leaf` pieces every 13 along a line. | The pieces share one `outline`, and one shadow in the shadow layer. |
| Shore | A `sand-300` beach around an island. | `sand-500` edge line; `water-300` shallows, 22; a broken line of `foam` just off the beach. Inland, grass with a `grass-700` edge. |

Runways and helipads are flat ground too, and the only pieces with an `outline`; see `Runway.md` and `Helipad.md`.

**Order.** Ground is drawn first: lawn, patches, tracks and roads, fields, water, the airfield, then aprons, taxiways, helipads and runways. The shadow layer and standing scenery come after it.

**States.** None. Ground does not change during play, and nothing on it moves.

**Do** keep patches, tracks and detail out of the airfield, so its stripes stay clean. **Do** keep tufts and flowers off roads, fields and landing zones. **Don't** stroke any of these pieces in `outline`, don't let two materials meet on a bare edge, and don't stripe ground outside the airfield.

**Compose.** The app's ground is drawn in `ui/game/LevelBackdrop.kt` and `ui/game/AirfieldDrawing.kt`, which still render version 1 until the app's world is ported.
