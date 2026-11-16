# Proposal

GitHub issue: #77

## Why

Design system v2 (#76, `design-system-v2`) redrew everything on the ground and ported the app's UI components, but the app's world still draws version 1: flat single-fill sprites generated from `tools/scenery-v1.js`, navy outlines on roads, fields and water, no light or shadow, a bare runway and helipad. The game now shows v2 controls over a v1 field. This change ports the world, so each level looks like its game screen in the showcase.

## What Changes

- **Sprite data from v2.** `tools/gen-kotlin.cjs` generates `ui/sprites/ScenerySprites.kt` from `WORLD` in `bundle.js` instead of `tools/scenery-v1.js`: every shape with its fill and stroke tokens and stroke width, clip shapes, the height tier, the rotates flag, the shadow silhouette and its throw. The small ground details (`DETAILS`) and the cloud are generated too. `tools/scenery-v1.js` is deleted.
- **One shadow layer.** Every hard ground shadow, of scenery and of ambient life, is drawn in one layer between flat ground and standing scenery, in `shadow-cast` at `opacity-shadow-cast`. Top-down sprites rotate and keep their shadow down-right; standing sprites do not rotate, and a level that rotates one is rejected by the level validator.
- **Runways and helipads** keep their footprint and gain the v2 construction: shoulder, rough-grass fringe, edge lines, six threshold bars, number, aiming blocks, touchdown marks and tyre rubber where the length allows, edge, threshold and approach lights. Helipads get their concrete pad, slab joints, corner lights and fringe. Taxiways are lighter asphalt with a yellow centre line.
- **Ground pieces** follow the v2 rules: the airfield is mown in stripes parallel to its main runway and the countryside is one flat tone with patches; roads, tracks, fields, ponds, aprons, the paddock and the island shore are edged in their own material with their transition bands and no navy outline; tufts, flowers and reeds come in clusters outside the airfield.
- **Level scenery data** is brought to the surroundings each landscape has in the showcase (`world.md`, Surroundings per landscape), using only sprites the design system already has. Runways, helipads, landing zones and every engine value stay exactly as they are.
- **Windows** take the `window` token and light up in Night and Dawn.
- **Level-select miniatures** are a crop of the level's own v2 field, drawn by the same code as the game, in the level's own palette. This replaces the `sky-300` sea that goes dark in Night and Dawn.
- **World code leaves the v1 tokens.** No world-drawing file reads a token marked `deprecated` in `tokens.json`.
- **Specs and docs.** `level-themes`, `runway-markings` and `level-selection` describe v2; `CLAUDE.md`, `.github/copilot-instructions.md`, `compose.md`, `docs/architecture.md` and the Compose notes of the component guidelines no longer say the world renders version 1.

### Split: the removal of the v1 tokens is a follow-up

The issue asks, as its last step, to remove the tokens marked `deprecated`, and `tools/migrate-tokens-v2.cjs`. That step is not in this change. Seven files that are not the world still read v1 tokens: the flight path's end dot, the proximity ring, the tutorial highlight and drag demo, the selection ring, and the splash banner. Their replacements have other values (`line-4` is 6px, its replacement `line-ui` 2.5px), and the design system has no token for some of the widths its guidelines give in pixels. Moving them is a design-system change of its own, on files that other active changes are working in. This change creates the follow-up issue for it (task 8.3).

### Out of scope

- Removing the deprecated tokens, `tools/migrate-tokens-v2.cjs`, the v1 values in `FaShadows`, and moving the non-world readers off v1 tokens (follow-up issue).
- The level card's best-score chip, tags and locked state. A locked state needs level-unlock rules, which is gameplay; the chip and tags change the card, not the world. This change creates a follow-up issue for them (task 8.3). The tile stays the square tile of `level-selection`.
- New levels, new sprites in the design system, gameplay changes, moving a runway or helipad to match a showcase screen.
- The splash page's sunset scene, aircraft sprites and their soft shadows, flight paths, the HUD.

### Depends on

- `design-system-v2` (#76), merged: the v2 tokens, `WORLD`, the runway, helipad and scene sources in `bundle.js`, and the showcase are the reference for every task.
- `detailed-scenery` (#33), `larger-level-worlds` and `world-zoom-retune`: this change builds on the code they merged (1920 by 1080 worlds, `SceneryKind`, `AmbientLife`, the sprite pass in the generator) and changes none of their world-scale values.

### Overlap with `detailed-scenery`

Every open task of `detailed-scenery` is superseded and should not be applied:

| Open task | Superseded by |
|---|---|
| 2.1, 2.2, 2.3 (draw the v1 SVGs) | `design-system-v2` tasks 3.2 to 3.6, already done |
| 2.4 (sprites in `bundle.js`, Scenery section in the showcase) | `design-system-v2` tasks 3.1, 6.1 and 6.3, already done |
| 2.5 (scenery pass in `gen-kotlin.cjs`) | task 2.2 of this change |
| 6.1 (`drawScenerySprite` on `line-2` and `line-fine`) | tasks 2.3 and 3.1 |
| 6.2 (scenery and ground through sprites in `AirfieldDrawing.kt`) | task groups 4 and 5, and task 6.1 |
| 6.3 (ambient life from sprites, draw order) | tasks 3.1 and 6.2 |
| 6.4 (preview of each level per palette) | tasks 2.4 and 6.4 |
| 7.1 (design-critic review of v1 screenshots) | task 8.2 |
| 7.2, 7.3 (human checks: play on a phone, reduced motion) | tasks 8.4 and 8.5, done once on the v2 world |

Its `scenery-art` spec delta also states v1 rules that v2 replaces (single flat fills, a fill budget, a parked aircraft without a shadow, `line-3` as the aircraft outline). It must be dropped or rewritten before `detailed-scenery` is archived; `world-rendering` below takes its place. This change does not edit `detailed-scenery`: the owner decides.

## Capabilities

### New Capabilities

- `world-rendering`: how the app draws the ground, landing-zone surrounds and scenery of a level: sprites generated from the design system, light and the single shadow layer, which sprites rotate, flat ground and its transition bands, the mown airfield, and that the world stays behind aircraft, paths and landing zones and never affects play.

### Modified Capabilities

- `runway-markings`: a landing end shows six threshold bars, its number, and aiming blocks where the runway is long enough, in place of the touchdown patch; a runway gains its shoulder, fringe, edge lines and lights around an unchanged footprint.
- `level-themes`: windows are lit in Night and Dawn; each level in its theme shows the ground and surroundings of its showcase screen.
- `level-selection`: a tile's miniature is a crop of the level's own v2 field in the level's palette, not a schematic on a ground colour.

## Impact

- Design system: `tools/gen-kotlin.cjs` (scenery pass reads `WORLD`, `DETAILS`, the cloud and a new metrics table), `bundle.js` (the ground and landing-zone sizes become one exported table that the markup functions read; drawn output unchanged), `tools/scenery-v1.js` (deleted), `compose.md`, the Compose notes in `components/Ground.md`, `Runway.md`, `Helipad.md`, `Scenery.md`, `Cloud.md`, `LevelCard.md`.
- Generated code: `ui/sprites/ScenerySprites.kt` (new shape), a new generated metrics file under `ui/sprites`.
- `shared` `domain`: `Scenery.kt` (kinds for the v2 sprites the levels use, which kinds stand), `Levels.kt` (scenery lists, airfield outline, no rotation on standing kinds), `LevelConfig.kt` (airfield outline and decoration seed), `LevelConfigValidator.kt` (standing scenery is not rotated).
- `shared` `ui`: `ui/sprites/ScenerySpriteDrawing.kt`, `SceneryDrawing.kt`, `ui/game/AirfieldDrawing.kt`, `LevelBackdrop.kt`, `LevelSelectPage.kt`, `GamePreviews.kt`, `ui/game/ambient` (drawing only), and through the cloud `ui/menu/MenuBackdrop.kt`.
- `shared` `engine`: none. No file under `engine/` changes.
- Tests: `LevelConfigValidatorTest`, `LevelsTest`, `SceneryIsDecorationTest`, new tests for the runway marking plan, shadow placement, decoration placement and the miniature crop, and a seeded-run fingerprint per level.
- Docs: `CLAUDE.md`, `.github/copilot-instructions.md`, `docs/architecture.md`.
- No new dependencies.
