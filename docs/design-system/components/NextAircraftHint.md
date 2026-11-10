A HUD chip that previews the next aircraft and the edge it will enter from.

**You provide:** the upcoming aircraft type (and optionally the one after), and the entry edge as an arrow direction.

The sprite is drawn at 40px, flat (no ground shadow), nose pointing in its direction of travel. The round `sky-500` badge holds an arrow that points the way the aircraft will fly, so an aircraft entering from the left edge shows `arrow-right`.

Show it from the first spawn. When the queue is empty, hide the chip rather than showing it blank.
