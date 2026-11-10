The gameplay overlay: score and streak on the left, next aircraft, lives and pause on the right.

**You provide:** the current score, streak multiplier (hidden below x2), the next aircraft type and its entry edge, lives left and lives total.

Layout: one row pinned to the top of the viewport, `space-4` from every edge, `space-3` between items, both groups top-aligned. All items are `hud-chip` (56px) tall. The bar has no background of its own: each item is its own outlined chip so the field stays visible between them.

Draw order is `z-hud`, above clouds. Aircraft can fly under the HUD; the spawner should avoid the top 88px for entries so nothing appears beneath a chip.

**Don't** add more than five items. Anything else belongs in the pause dialog.
