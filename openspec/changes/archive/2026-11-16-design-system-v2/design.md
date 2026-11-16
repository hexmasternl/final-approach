# Design

## Context

- The design system is one pipeline. `tokens.json` holds the values; `bundle.js` holds every sprite as shapes with paint keys; `tools/gen-kotlin.cjs` turns both into the Compose theme (`ui/theme`), the sprite data (`ui/sprites`) and the scenery SVGs in `assets/scenery`. `showcase.html` inlines the tokens, `bundle.css` and `bundle.js`.
- The generator enforces the v1 scenery rules: at most four fills per sprite and no path colour, `sun` or `fuselage` white.
- The approved v2 direction exists as a page with working sources in `reference/`: `world.cjs` draws 16 scenery sprites, the runway, helipad, apron, taxiways, roads, fields, pond and the Meadow Field scene; `build.cjs` holds the v2 component CSS; `tokens.css` holds the Day and Night values of every ramp. Dawn values, Island and Savanna were not drawn.
- The app renders v1 and must keep building from the generated theme while this change is in progress and after it merges.

## Goals / Non-Goals

**Goals:** the design system describes and shows v2 only; the app's UI components are v2; the app's world still draws as v1 from the same generated theme; the reference sources are carried over rather than redrawn.

**Non-Goals:** porting scenery sprite drawing, runway and helipad drawing, level backdrops or level data; removing v1 tokens; redesigning aircraft, icons, the flight path or motion; changing what any screen contains or does.

## Decisions

### 1. Tokens are added, not replaced

Every v2 token is new or keeps an existing name with its existing value. Ramps are named `<material>-<step>` (`roof-300`, `roof-500`, `roof-700`, `roof-900`); where v1 has a colour, it is the ramp's 500 step with the same value (`sun` and `sun-500` are the same colour; `roof-500`, `grass-500`, `asphalt-500` keep their values). v1-only tokens (`surface*`, `shadow-lip*`, `line-fine`, `line-1` to `line-4`, `sun-deep` and the other `-deep` shades, `hud-chip`) stay in `tokens.json` with `"deprecated": "<replacement>"` and are left out of the showcase.

Why: the generator feeds the app. Replacing names would break the build or force the port into this change. Alternative considered: a separate `tokens.v2.json`; rejected because two sources of truth drift, and the generator and showcase would need a switch.

### 2. Ramp steps have fixed roles

| Step | Role |
|---|---|
| 100 / 200 | Highlight: the lit edge of a control, the lightest roof or metal band |
| 300 | Lit tone (W1) |
| 500 | Base tone |
| 700 | Shade tone; the lip of a control |
| 900 | Tinted inner line (W3, U4); the bottom edge of a lip |

Materials: `grass`, `leaf`, `pine`, `wood`, `soil`, `sand`, `straw`, `roof`, `wall`, `tin`, `stone`, `concrete`, `asphalt`, `road`, `water`, `hide`, plus single tokens `window`, `beacon`, `muzzle`, `shadow-cast` and `opacity-shadow-cast`. Accents: `sun`, `sky`, `cherry`, `tangerine`, `mint` (the ramp around `success`). Surfaces: `paper-0` to `paper-500`. Day and Night values come from `reference/tokens.css`; Dawn values are derived the way v1 derived them (dim and warm, cooler shadow) and checked in the showcase.

### 3. Five line ranks

`line-hero` 4, `line-zone` 3, `line-ui` 2.5, `line-ui-sm` 2, `line-detail` 1.25 (scenery inner lines may go to 1.5). Scenery strokes stay world-scaled as in v1. Aircraft keep 4 units on their 96-unit grid.

### 4. Height tiers and shadows are sprite data

Each scenery sprite carries a height tier (`flat`, `S`, `M`, `L`), a shadow silhouette and whether it may rotate. Standing sprites (tier M or L with a south wall) may not rotate and bake their shadow; top-down sprites (vehicles, animals, boats, hay bales, rocks) rotate, and their shadow is their silhouette offset down-right by the tier's distance, applied outside the rotation. All ground shadows are drawn in one layer between flat ground and standing scenery, in `shadow-cast` at `opacity-shadow-cast`, so overlapping shadows never darken twice. Airborne shadows stay soft and further offset, so altitude still reads.

Why: the rule "the shadow always falls down-right" cannot hold if a baked shadow rotates with its sprite. Alternative considered: per-sprite shadows drawn with the sprite; rejected because neighbouring shadows would fall on canopies and double up.

### 5. The generator's checks change with the rules

The four-fill and colour checks are replaced by: at most three main materials per sprite, plus at most two trim materials the sprite names (a sprite that carries other things is exempt); every paint is a live colour token; no scenery sprite uses `fuselage`, a path colour or `sun-500`; every sprite with height has an `outline` silhouette and a shadow silhouette; flat ground has no `outline`; a tier M sprite does not rotate. Whether an inner line avoids `outline` is left to review: small parts that stand on a sprite, such as a chimney, keep an outline of their own. The tower cab (`cherry`) and the windsock (`tangerine`) keep their accent, as in v1.

### 6. Carry the reference over, shape by shape

`reference/world.cjs` is the source for the sprites it draws: its shapes move into `bundle.js` in that file's sprite format, with ramp steps as paint keys. Sprites it does not draw (farmer, birds, baggage cart, palm, acacia, jetty, boats, zebra, elephant, fence, hedge piece, crop-field tile, wave, wake) are drawn new on the same rules. `reference/build.cjs` is the source for `bundle.css`: its component classes are renamed to the `fa-` names and read token variables only.

### 7. HUD readouts are a new component, the chip stays a chip

`HudBar` readouts become `FlightStrip`: a 40dp flat strip with an optional labelled cell and a value. `Chip` becomes the flat tinted tag. The streak badge and score popup become `Sticker`. Old component files are rewritten in place where the name still fits and renamed where it does not, with the old name noted so the port can map it.

### 8. The app port stops at the UI layer

Everything under `ui/components`, plus the screens that compose them, moves to v2. Everything that draws the world (`ui/sprites`, `AirfieldDrawing.kt`, `LevelBackdrop.kt`, ambient life) is left alone and keeps reading the v1 token names, which is why those names must survive (Decision 1). Level tiles keep their v1 miniature inside the v2 card.

One moulded-surface primitive draws face, highlight, lip, outline and contact shadow from a tone set (light, base, lip, edge) and a press state; button, icon button, level tile, toggle knob and selected segment all use it, so "a lip means pressable" is enforced by which composables call it. Flat surfaces (strip, chip, coach mark) use a second primitive with no lip. The dialog composable keeps its slots (title, body, stats, actions) and gains a header tone and an eyebrow, so the pause and game-ended dialogs change by passing those, not by restructuring.

Why in this change: the owner asked for the UI components, including both dialogs, to move with the design system. Alternative considered: port the world too; rejected because it collides with the open `detailed-scenery` work and triples the size of the pull request.

## Risks / Trade-offs

- v2 UI over a v1 world looks mixed until the world is ported → accepted by the owner; the follow-up issue is created in this change.

- More detail could hurt readability of aircraft and paths → W4, W8 and W9 exist for this; the showcase game screens are reviewed by the design-critic agent with aircraft over the busiest scenery, in Day, Night and Dawn.
- The app's world and the design system disagree until the world port lands → `CLAUDE.md` says so in one line, and the deprecated tokens name their replacements.
- `detailed-scenery` has open tasks that draw v1 scenery → flagged in the proposal and the issue; the owner decides whether to drop them before either change is applied.
- Token count roughly doubles → fixed step roles (Decision 2) keep choosing a tone mechanical.
- Sprites at 0.33dp per world unit show little of their detail → minimum sizes from v1 stay, and detail that falls under 1.5dp is left out of the sprite rather than drawn.

## Migration Plan

1. This change merges: design system and app UI are v2, the app's world is v1.
2. Follow-up change ports sprite drawing and level drawing, and updates the world-facing specs.
3. A last task of that follow-up removes the deprecated tokens.

Rollback: revert the merge commit; the app never depended on a v2-only token.

## Open Questions

- Whether Dawn needs its own `window` light value or shares Night's. Decide when the Dawn showcase is reviewed.
