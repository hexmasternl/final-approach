A landing strip for airplanes, defined by position, heading, length and its two numbers.

**You provide:** `length` in px at 1x (the strip is always 64px wide), `numbers` such as `09/27`, and a `state`.

**Construction.** `asphalt-500` slab, `radius` 10px, `line-3` `outline`. Two `asphalt-700` touchdown patches, four `marking` threshold bars at each end, `marking` centre dashes 24 x 6px every 40px, and the runway numbers in DynaPuff 22px, each read from its own approach end. Numbers follow real convention: heading divided by ten, and the two ends differ by 18.

**One-way variant.** `mode="one-way"` (default `two-way`). The landing end is the one whose number is shown. Threshold bars, touchdown patch and number are drawn at that end only; the far end is plain `asphalt-500`. The centre line is a row of chevrons pointing along the landing direction: open V strokes in `marking`, `line-4` stroke with round caps and joins, arm `space-5`, spacing `space-4`. The silhouette differs from a two-way runway (one marked end, arrows), so it does not depend on colour.

**States.**
- Idle: no glow.
- `state="ok"`: a 9px `zone-ok` halo outside the outline, pulsing. Shown while the selected airplane's path ends on the strip within the alignment tolerance.
- `state="no"`: a dotted `zone-no` halo. Shown while a helicopter's path ends here, or an airplane's path ends here out of alignment.

The halo's shape differs between the two states (solid versus dotted), so it does not depend on telling teal from red.

**Don't** place two runways closer than 96px apart without crossing them, and don't put text other than the numbers on the asphalt.
