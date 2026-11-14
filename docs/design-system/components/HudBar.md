The gameplay overlay: score and streak on the left; next aircraft, lives and pause on the right. Pause is its only control.

**You provide:** the current score, streak multiplier (hidden below x2), the next aircraft type and its entry edge, lives left and lives total.

**Layout.** Class `fa-hud`: one row pinned to the top of the viewport, `space-4` from every edge, with two `fa-hud__group` groups, `space-3` between items, both groups top-aligned. The bar has no background of its own: each item stands alone on the field, so the field stays visible between them.

**Construction.**
- Every readout is a Flight Strip, `strip-height` high: flat paper on a `contact-field` shadow (U2, U6).
- The streak is a Sticker next to the score strip.
- The pause button is an Icon Button in its HUD variant (`fa-iconbtn--hud`), as high as a strip. It is the only element in the HUD with a lip, because it is the only one that can be pressed (U2).

Draw order is `z-hud`, above clouds. Aircraft can fly under the HUD; the spawner should avoid the top 88px for entries so nothing appears beneath a strip.

Counter row: a level may add a second row under the score strips (`fa-hud__stack`, then `fa-hud__counters`), `space-3` below them and left-aligned with them, holding one Spawn Counter per aircraft type the level spawns: at most five, one per type, in roster order: light prop, turboprop, jet, jumbo, helicopter. The row does not count towards the six items of the first row.

While the first-time tutorial runs, a ghost small Button "Skip tutorial" sits in the first row, left of the HUD button. It is a seventh item only for as long as the tutorial runs; the six-item limit counts the permanent items: best score, score, streak, next aircraft, lives and the button. It stands on `contact-field` like everything in the HUD.

**Do** keep every item the height of a strip, with a full `touch-min` touch area around the button. **Don't** add more than six items to the first row, or more than five strips to the counter row. Anything else belongs in the pause dialog. **Don't** give a readout a lip.

In version 1 every HUD item was a 56dp HUD chip with a navy lip. See `FlightStrip.md`.

**Compose.** `Hud` in `ui/game/Hud.kt`, built from `FaScoreCounter`, `FaFlightStrip` and a compact `FaIconButton`. The app's bar shows the best score and the score as strips on the left and one button on the right. `hudBottom` gives the height of the HUD band.
