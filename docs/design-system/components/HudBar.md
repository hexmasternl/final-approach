The gameplay overlay: score and streak on the left, next aircraft, lives and pause on the right.

**You provide:** the current score, streak multiplier (hidden below x2), the next aircraft type and its entry edge, lives left and lives total.

Layout: one row pinned to the top of the viewport, `space-4` from every edge, `space-3` between items, both groups top-aligned. All items are `hud-chip` (56px) tall. The bar has no background of its own: each item is its own outlined chip so the field stays visible between them.

Draw order is `z-hud`, above clouds. Aircraft can fly under the HUD; the spawner should avoid the top 88px for entries so nothing appears beneath a chip.

Counter row: a level may add a second row under the score chips, `space-2` below them and left-aligned with them, holding one Spawn Counter chip per aircraft type the level spawns: at most five, one per type, in roster order: light prop, turboprop, jet, jumbo, helicopter. The row does not count towards the five items of the first row.

**Don't** add more than five items to the first row, or more than five chips to the counter row. Anything else belongs in the pause dialog.
