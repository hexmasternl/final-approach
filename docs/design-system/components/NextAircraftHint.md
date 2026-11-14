A flight strip that previews the next aircraft and the edge it will enter from.

**You provide:** the upcoming aircraft type and its livery, and the entry edge as an arrow direction.

**Construction.** A Flight Strip (`fa-strip fa-next`).
- **Label cell:** the word Next in `label-sm`, on `paper-200`.
- **Value:** the aircraft sprite, drawn small and flat (no soft shadow), nose pointing in its direction of travel, in the livery it will fly in. Then the edge badge (`fa-next__edge`): a round `sky-500` badge with a `line-ui-sm` `outline` holding an arrow at `icon-xs` in `on-accent`.

The arrow points the way the aircraft will fly, so an aircraft entering from the left edge shows `arrow-right`. The strip is flat (U2); the badge is a mark, not a button.

**States.** None. Show it from the first spawn. When the queue is empty, hide the strip rather than showing it blank.

**Do** keep the sprite's shape readable: the type is told by silhouette first. **Don't** add a lip to the badge or make the strip pressable.

In version 1 this was a HUD chip. See `FlightStrip.md`.

**Compose.** A `FaFlightStrip` with the label Next and the sprite and badge as its value.
