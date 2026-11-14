A round landing spot for helicopters on a concrete pad.

**You provide:** position and a `zone` state. The element is `fa-helipad`.

**Construction.** The disc has the same footprint as in version 1: 112 world units across. From the outside in (W7):

- **Fringe:** `grass-700`, 7 units beyond the pad. No line.
- **Pad:** a `concrete-300` square, 138 units across, with a `concrete-700` edge line, `concrete-500` slab joints and a `beacon` light in each corner.
- **Disc:** `asphalt-500` with an `outline` at `line-zone` (W6), a worn `asphalt-700` ring, the `marking-warn` ring and the `marking` H.

**States.** As on Runway: `zone="ok"` is a solid pulsing `zone-ok` halo just outside the disc, `zone="no"` a dotted `zone-no` halo (an airplane is selected and its path ends here). At rest there is no halo.

The landing rule is distance-based: the path end must fall within the disc's radius. Keep the visual radius and the rule radius equal.

**Don't** outline the pad, and don't put scenery or detail on the pad or its fringe.

**Compose.** The app's helipad is drawn in `ui/game/AirfieldDrawing.kt` and still renders version 1 until the app's world is ported.
