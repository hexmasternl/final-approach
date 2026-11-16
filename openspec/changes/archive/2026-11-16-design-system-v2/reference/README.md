# Design system v2: the approved proposal

This folder holds the proposal that was approved on 2026-11-14 and that this change turns into the design system. It is the visual reference for every task in `../tasks.md`.

| File | What |
|---|---|
| `final-approach-v2.html` | The proposal page as approved. Self-contained apart from Google Fonts. It is the body the artifact was published from and has no document skeleton of its own. |
| `preview.html` | The same page wrapped in a document skeleton. Open this one in a browser. |
| `world.cjs` | Every redrawn sprite (trees, pines, bush, rock, hay bale, farmhouse, barn, silo, cow, car, fuel truck, windsock, tower, terminal, hangar, cloud), the runway, helipad, apron, taxiways, roads, fields and pond, and the Meadow Field scene. |
| `build.cjs` | The page itself, including the CSS of every v2 component (button, toggle, segmented control, chip, flight strip, sticker, panel, boarding-pass dialog, level card, coach mark). |
| `tokens.css` | Day and Night values of every colour ramp, and the font stacks. |

Published copy: https://claude.ai/artifact/8Wb5znU4NGe3DX5CLXvF6W (private to the owner unless shared).

## Rebuilding

From this folder:

```
node build.cjs --preview
```

This rewrites `final-approach-v2.html` and also writes `preview.html`, the same page wrapped in a document skeleton for a local browser. The script reads the current aircraft, icon and (for the before and after comparison) scenery SVGs from `docs/design-system/assets`; pass another design-system folder as the first argument to read them from there. Both files are committed.

Once the scenery SVGs in `docs/design-system/assets/scenery` are redrawn, the "Now" side of the comparisons will show the new sprites. The committed `final-approach-v2.html` keeps the comparison as it was approved.

## What was not drawn

Dawn values, the Island and Savanna landscapes and the sprites listed in `../tasks.md` 3.3 to 3.5. They follow the same rules.
