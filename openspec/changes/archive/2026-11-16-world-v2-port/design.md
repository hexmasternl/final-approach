# Design

## Context

- The design system is one pipeline: `tokens.json` holds values, `bundle.js` holds every sprite, and `tools/gen-kotlin.cjs` writes the Compose theme (`ui/theme`) and the sprite data (`ui/sprites`). Since `design-system-v2` the generator checks the v2 sprites in `WORLD` and writes their SVGs, but still generates `ui/sprites/ScenerySprites.kt` from the v1 copy in `tools/scenery-v1.js`.
- A v2 sprite is richer than a v1 one. `WORLD` holds 38 sprites built from `rect`, `path`, `circle`, `ellipse` and `polygon`, with paint keys `{{fill/stroke/width}}`, 21 clip groups, group transforms (`translate`, `scale`, `rotate`), an `opacity` on some shapes, one `vector-effect="non-scaling-stroke"`, and per sprite a tier, `rotates`, a shadow silhouette `sh` and its throw `off`. Stroke widths are part of the paint key, in world units.
- The runway, the helipad, the cloud and every ground piece exist only as markup functions in `bundle.js` (`runwayMarkup`, `helipadMarkup`, `cloudMarkup`, the `Scene` builder), with their sizes as numbers inside those functions. The three scenes in `bundle.js` are reference compositions; the game draws its levels from level data.
- The app's world code today: `ui/sprites/ScenerySpriteDrawing.kt` (fits a v1 sprite into an item's box, strokes from `line-2` and `line-fine`), `ui/game/AirfieldDrawing.kt` (runway, helipad and ground pieces, with `FaDimens` dp tokens read as world units), `ui/game/LevelBackdrop.kt` (ground, paddock, island, ambient life, Dawn lights), `ui/sprites/SceneryDrawing.kt` (cloud, lawn), `ui/game/LevelSelectPage.kt` (a schematic miniature). Buildings and windsocks carry rotations in `Levels.kt`.
- The showcase scenes do not share the levels' layouts: the Meadow screen has a two-way runway where level one is one-way, the Island screen one runway where level two has two, the Savanna screen one one-way runway where level three has three two-way ones. Layouts are gameplay and stay.
- Level worlds are 1920 by 1080 world units; one world unit is 0.333dp on the reference phone. `SceneryIsDecorationTest` already proves scenery has no effect on a seeded run.

## Goals / Non-Goals

**Goals:** the game's field layer is v2 in every level and in the level-select miniature; all shapes and sizes come from the design system through the generator; world code reads no v1 token; the engine does not change.

**Non-Goals:** removing the v1 tokens or moving non-world readers off them (follow-up issue); the level card's chip, tags and locked state (follow-up issue); soft, blurred airborne shadows; moving any landing zone; camera or zoom changes; new sprites in the design system.

## Decisions

### 1. The generator turns `WORLD` into a small shape tree

`gen-kotlin.cjs` parses each sprite's markup into data: shapes as path data (rectangles, circles, ellipses and polygons are converted, as the aircraft pass already does), each with a fill paint, a stroke paint and a stroke width in world units, an optional opacity and line cap; groups with a clip path or a transform matrix; and per sprite its tier, `rotates`, shadow path, throw and box. `non-scaling-stroke` becomes a flag on the shape. Paints are one generated enum with an entry per colour token the sprites use, mapped to `FaColors` at draw time. `DETAILS` and the cloud go through the same pass. The generator stops with the sprite's name when it meets an element or attribute it does not know, so nothing is dropped silently. `tools/scenery-v1.js` and the v1 pass are deleted in the same task.

Alternative considered: ship the SVG files and render them with an SVG library. Rejected: a new dependency, and SVGs carry Day colours instead of theme tokens, so Night and Dawn would need three files per sprite. Alternative considered: port sprites by hand. Rejected: `CLAUDE.md` forbids redrawing, and 38 sprites would drift.

### 2. Sprites are drawn in world units, strokes included

A sprite is drawn around its origin, scaled by world-to-pixel and by the item's scale. Stroke widths come from the sprite data and scale with it, as `world.md` says ("strokes shrink with the airfield"). `SceneryLines`, `SCENERY_UNITS_PER_DP` and the reading of `line-2`, `line-fine` and `radius-sm` go.

`SceneryItem.width` and `height` stay the item's footprint, which the level validator uses. The renderer scales a sprite uniformly so that the bounds of its own shapes (without the shadow) fit that footprint. The default footprints in `Levels.kt` are set to each v2 sprite's natural size, so the default scale is 1 and lines have their token width.

Alternative considered: replace `width` and `height` by a scale. Rejected: it changes the validator's geometry and every level entry for no visible gain.

### 3. One shadow pass, placed by a pure function

The field is drawn in this order (Ground.md): base ground, patches, tracks and roads, fields, water, the mown airfield, aprons, car parks and taxiways, helipads, runways, then the shadow layer, then everything with height from north to south (sorted by world y), then ambient life, then the Dawn lights.

The shadow layer is one `alphaLayer(FaOpacity.shadowCast)` in which every shadow silhouette is filled opaque in `shadowCast`: static scenery and ambient life (cows, farmer, birds, boats) alike. A shadow is the sprite's `sh` path under the sprite's own rotation and scale, with its centre moved by `off` times the scale in screen axes, outside the rotation. That placement is one pure function with a unit test. Flat-tier sprites are drawn in the ground pass and cast nothing.

Which pass a sprite is drawn in follows its tier. The HUD-band rule of the validator keeps following `SceneryKind.ground`.

Alternative considered: draw each shadow with its sprite. Rejected: overlapping shadows darken twice and fall on neighbouring crowns, which W1 and the spec forbid.

### 4. Standing kinds are marked in the domain

`SceneryKind` gets a `standing` flag for the kinds whose sprite has `rotates` off: terminal, tower, hangar, farmhouse, barn, silo, tree, oak, pine, palm, acacia and windsock. `LevelConfigValidator` reports a standing item whose rotation is not zero. The rotations that buildings and windsocks carry in `Levels.kt` today are removed. A test checks that for every kind with a sprite, `standing` is the opposite of the sprite's `rotates`, so the domain flag and the design system cannot drift.

Alternative considered: let the renderer ignore the rotation. Rejected: level data would lie, and the validator's footprint would be turned while the drawing is not.

### 5. Ground and landing-zone sizes become one table in `bundle.js`

The design system lacks a machine-readable source for the sizes of the runway, helipad, taxiway and ground bands: they are numbers inside `runwayMarkup`, `helipadMarkup` and the `Scene` builder, repeated as prose in `world.md`. Add one exported table to `bundle.js` (shoulder 9 and 10, fringe 22 and 24, threshold bars, aiming blocks, light spacing 58, the 460 and 560 length limits, helipad pad 138 and fringe 7, taxiway, road, track, apron, car park, pond, field, shore, mowing stripe 52, cluster sizes), make the markup functions read it, and generate a Kotlin object from it under `ui/sprites`. The showcase must come out unchanged: `build-showcase.cjs` is run and `showcase.html` shows no diff.

All drawing code takes sizes from that object and colours from `FaColors`. The trick of reading a dp token as world units (`FieldScale`) goes, and with it `line-3`, `line-4`, `radius-sm` and the `space-*` tokens in `AirfieldDrawing.kt`. Across a runway, sizes scale with `runway.width / 64`; along it they are world units.

Alternative considered: type the numbers into a Kotlin config object. Rejected: two copies of 60 numbers drift, and the rule is design system first.

### 6. The runway is planned by a pure function, then painted

`runwayPlan(length, mode, metrics)` returns what a runway shows: which ends are marked, the threshold bars, aiming blocks, touchdown marks and tyre rubber by the length limits, the centre line as dashes or chevrons, and the positions of edge, threshold and approach lights. It has no Compose types and gets unit tests (240, 459, 460, 559, 560 units; one-way and two-way). `drawRunway` paints the plan. Numbers stay text drawn with the `TextMeasurer`, in the marking colour, sized from the table.

The Dawn running light lights the plan's edge lights one after another, in place of the separate row of twelve lights per edge; brightness cap, cycle time and reduced-motion behaviour are unchanged, so the `level-themes` lighting requirements hold as written. The constants in `LevelBackdrop.Backdrop` that this replaces are removed.

### 7. The airfield outline is level data; decoration is seeded and pure

`LevelConfig` gets the airfield's outline as a polygon in world units and a decoration seed. Mowing stripes are drawn inside the outline, parallel to the first runway of the level (the main runway in all three levels), with a soft `grass-700` boundary. A test checks that every runway and helipad, with its fringe, lies inside the outline.

Patches, sea patches, clusters of tufts, flowers and reeds, and the trees of a wood are placed by one pure function of the level, with its own `Random(seed)`: never the engine's generator. It keeps everything out of the airfield, off roads, fields, water and landing zones, and keeps standing things out of the HUD band. It is computed once per level and has unit tests for determinism and for those rules on all three levels. Level three gets tufts and no flowers.

The island stays the ellipse of `Island`, which the validator and the boats use; beach, shallows and foam follow that ellipse. The paddock stays its rectangle.

Alternative considered: list every patch and tuft in `Levels.kt`. Rejected: hundreds of entries nobody can read.

### 8. Level scenery follows the showcase's surroundings table

Each level's scenery list is brought to the row of its landscape in `world.md`, Surroundings per landscape. Kinds are added only for sprites the design system has and the levels place directly: silo, hay bale, pine, oak, car park, dirt track, and the three crop variants in place of the one field kind. The small tree and pine variants are picked by the wood filler. Nothing is placed over a landing zone or, if it stands, under the HUD band; the existing validator rules decide.

### 9. The miniature is the game's field, cropped

`drawField` is split so its static part (everything except ambient life and animated lights) can be drawn for any world rectangle. The level tile draws that part through a viewport on the crop, with the palette of the level's own theme, chosen the way `GameScreen` chooses it. The crop is a pure function: the smallest square around every runway and helipad shape plus the fringe, kept inside the world; it has a unit test. The tile records the miniature once per size and palette instead of redrawing it each frame. `DAWN_ALPHA`, `RUNWAY_STROKE_FRACTION`, `sky-300` and `sky-700` leave `LevelSelectPage.kt`. The tile keeps `level-tile` and `level-tile-art`.

Alternative considered: a separate simplified miniature in v2 colours. Rejected: `LevelCard.md` says "don't draw a miniature that differs from the level".

### 10. The cloud is ported, its shadow is not changed

The cloud is generated from `cloudMarkup` (one silhouette in three tones, `line-zone` outline) and replaces the lobe list in `SceneryDrawing.kt`. Its shadow stays the offset silhouette in `shadowGround` it is today. This also changes the clouds of the menu backdrop, which share `drawCloud`. Aircraft shadows are not touched.

### 11. The engine is pinned before anything changes

The first task adds a test that plays a seeded run of each level and compares spawns, landings, collisions and score with values recorded on the base commit. It must pass unchanged at the end, and `git diff` of `engine/` against the base must be empty.

## Assumptions

Each is a default the author can overrule.

- **Split.** Removing the deprecated tokens, `tools/migrate-tokens-v2.cjs` and the v1 readers outside the world is a follow-up issue, not part of this change (see proposal.md).
- **Level card.** The best-score chip, tags and locked state are a follow-up issue; the tile stays square.
- **"Matches its game screen"** means the same construction, materials and kinds of surroundings, not the same layout. Runways and helipads stay where the levels have them.
- **Level data.** Scenery lists may gain items and kinds for sprites the design system already has; "new scenery kinds" in the issue's out-of-scope list is read as "no new sprites".
- **Miniature palette.** A tile is drawn in its level's own theme, so the Savanna tile is in Dawn colours whatever palette the page uses.
- **Island and paddock** keep their simple shapes (ellipse, rectangle) instead of the showcase's organic outlines.
- **Cloud** is ported; blurred airborne shadows are not.
- **Dawn running light** runs over the runway's v2 edge lights.

## Risks / Trade-offs

- Many more shapes per frame, some behind clip groups → Paths are parsed once; the static field (ground, and standing scenery) can be recorded into graphics layers per size and palette if the frame rate drops. The human check in task 8.4 measures it on a phone; recording is the fallback, not the first step.
- At 0.333dp per world unit most inner detail is under a pixel → accepted: the design system already leaves out detail under 1.5dp, and the design-critic review judges readability, not detail.
- More scenery could compete with aircraft → the review in task 8.2 puts aircraft and paths over the busiest scenery in every theme; W4 and W9 are enforced by the generator.
- The ground metrics table changes `bundle.js` internals → the showcase is rebuilt and must show no diff.
- `detailed-scenery` still lists open v1 drawing tasks and a v1 `scenery-art` spec → named task by task in the proposal; the owner drops or rewrites them. Applying them after this change would undo it.
- The issue's last step is deferred → the app still generates deprecated tokens after this change; `CLAUDE.md` keeps its rule against v1 tokens in new code until the follow-up lands.

## Migration Plan

1. This change merges: the world is v2; non-world code still reads some v1 tokens.
2. The follow-up issue moves those readers and removes the tokens and `migrate-tokens-v2.cjs`.

Rollback: revert the merge commit. Level data changes are decoration only; no stored player data depends on them.
