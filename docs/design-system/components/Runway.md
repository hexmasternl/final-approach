A landing strip for airplanes, defined by position, heading, length and its two numbers.

**You provide:** `length` in world units (the asphalt is always 64 units wide, and at least 240 long), `numbers` such as `09/27`, a `mode` and a `zone` state. The element is `fa-runway`.

**Construction.** The asphalt has the same footprint as in version 1. From the outside in (W7):

- **Fringe:** rough grass in `grass-700`, 22 units beyond each side of the asphalt and 24 beyond each end. No line.
- **Shoulder:** `concrete-500` with a `concrete-700` edge line, 9 units beyond each side and 10 beyond each end.
- **Asphalt:** `asphalt-500` with an `outline` at `line-zone`. With the helipad it is the only ground shape that takes a navy line (W6).
- **Markings** in `marking`: an edge line along both sides, six threshold bars at each end, the runway number in DynaPuff read from its own approach end, two aiming blocks, touchdown marks behind them, and a dashed centre line. Tyre rubber is `asphalt-700`.
- **Lights:** `beacon` edge lights on the shoulder along both sides, four `mint-500` threshold lights just outside each marked end, and four approach lights in the fringe: two `cherry-500`, two `foam`.

Numbers follow real convention: heading divided by ten, and the two ends differ by 18.

A runway shorter than 460 units keeps its threshold bars, number and lights and drops the aiming blocks and tyre rubber. Touchdown marks need 560 units.

**One-way variant.** `mode="one-way"` (default two-way). The landing end is the one whose number is shown. Threshold bars, number, aiming blocks and threshold lights are drawn at that end only; the far end is plain asphalt. The centre line is a row of chevrons pointing along the landing direction. The silhouette differs from a two-way runway (one marked end, arrows), so it does not depend on colour.

**States.**
- Idle: no halo.
- `zone="ok"`: a solid `zone-ok` halo just outside the asphalt, pulsing. Shown while the selected airplane's path ends on the strip within the alignment tolerance.
- `zone="no"`: a dotted `zone-no` halo. Shown while a helicopter's path ends here, or an airplane's path ends here out of alignment.

The halo's shape differs between the two states (solid versus dotted), so it does not depend on telling teal from red.

**Do** keep the fringe clear of scenery and detail. **Don't** place two runways closer than 96 units apart without crossing them, don't put text other than the numbers on the asphalt, and don't outline a taxiway or an apron: they are flat ground (see `Ground.md`).

**Compose.** The app's runway is drawn in `ui/game/AirfieldDrawing.kt` and still renders version 1 until the app's world is ported.
