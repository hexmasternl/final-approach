# Visuals

Social visuals and the Play Store feature graphics, built from the design kit. Each PNG is rendered from an HTML file in `_source/`, and every HTML file is written by `_source/build.cjs`. Do not edit a PNG or an HTML file by hand.

| File | Size | What |
|---|---|---|
| `01-hero-wordmark.png` | 1080 x 1080 | The wordmark in the menu sky |
| `02-sunset-splash.png` | 1080 x 1080 | The splash page: sunset, airport silhouette, towed banner |
| `03-gameplay-field.png` | 1080 x 1080 | A busy field with the HUD |
| `04-meet-the-fleet.png` | 1080 x 1080 | The five aircraft on the apron |
| `05-how-to-play.png` | 1080 x 1080 | Tap, drag, land |
| `06-streak-combo.png` | 1080 x 1080 | Streaks and score popups |
| `07-keep-your-distance.png` | 1080 x 1080 | The proximity warning |
| `08-choose-your-airport.png` | 1080 x 1080 | The three level cards |
| `09-night-shift.png` | 1080 x 1080 | The Night theme |
| `10-beat-the-best.png` | 1080 x 1080 | The game-over dialog as a score challenge |
| `11-pov-cleared-to-land.png` | 1080 x 1080 | Approach view: a jumbo on final to Meadow Field |
| `12-pov-from-the-cockpit.png` | 1080 x 1080 | Approach view: from the cockpit of the light prop |
| `13-pov-island-in-sight.png` | 1080 x 1080 | Approach view: a turboprop far out from the island |
| `14-pov-land-on-the-h.png` | 1080 x 1080 | Approach view: the helicopter over the helipad |
| `15-pov-follow-the-lights.png` | 1080 x 1080 | Approach view: a jet at night |
| `16-pov-one-way-in.png` | 1080 x 1080 | Approach view: the one-way runway on the savanna at dawn |
| `play-store-feature-graphic.png` | 1024 x 500 | Feature graphic: wordmark and a top-down field |
| `play-store-feature-graphic-approach.png` | 1024 x 500 | Feature graphic: wordmark and the approach view |

## How they are made

- Every ground is a version 2 scene: a mown airfield, a runway or helipad with its shoulder and fringe, and scenery in clusters, composed with the kit's `Scene` builder in `bundle.js`.
- The approach-view visuals (11 to 16 and the second feature graphic) are drawn by `approachSvg` in `bundle.js`. See `components/ApproachView.md`.
- Captions are Panels, taglines are Stickers and readouts are Flight Strips, at poster size. The build script draws no scenery, runway or aircraft of its own.

## Building

From the repository root, with Chrome or Edge installed:

```
node docs/design-system/visuals/_source/build.cjs
```

Pass the browser's path as an argument when it is not in the default place. Set `ONLY` to a part of a name to render a subset, for example `ONLY=pov`.
