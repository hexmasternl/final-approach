Final Approach is a top-down air traffic game that looks like a box of toys tipped onto a lawn. The player taps an aircraft, drags a path, and lands it. Everything on screen should look like you could pick it up.

This is version 2 of the design system. The world is lit and shaded, and the UI is moulded. Aircraft, icons, flight paths, motion, layer order, touch sizes and voice are the same as in version 1.

## Principles

Three general principles, nine rules for everything on the ground (W1 to W9) and seven rules for controls and panels (U1 to U7). Cite a rule by its identifier.

### General

1. **Toy scale.** Sizes are exaggerated, not realistic. A jumbo is nearly twice a light prop; a cockpit window is a third of the nose.
2. **Silhouette before colour.** Aircraft types, landing zones and states are told apart by shape first. Colour confirms; it never carries meaning alone.
3. **The field stays clear.** UI lives in flat strips at the edges. Nothing covers a landing zone.

### World

- **W1. One sun, top-left.** Every object is lit from the top-left: a lit tone to the north-west, a shade tone to the south-east, and a hard, unblurred shadow cast down-right. Only airborne things keep a soft shadow, further away, so altitude reads from the difference.
- **W2. Three tones per material.** A material is drawn as light, base and shade in flat shapes from its ramp. Never a gradient, a texture or a blur. A sprite uses at most three main materials.
- **W3. Navy outside, tinted inside.** The silhouette of a thing is stroked in `outline`. A line inside the silhouette is a darker step of the material it lies on. Only a small part that stands on the sprite, such as a chimney, a window frame or a door, keeps a thin `outline` of its own at `line-ui-sm` or less.
- **W4. Line weight is rank.** Aircraft 4, landing zones 3, scenery 2.5, inner detail 1.25: the five line ranks from `line-hero` to `line-detail`. The thickest line on screen is always an aircraft.
- **W5. Standing things show a south wall.** Buildings, trees and the tower lean a little: roof or crown on top, wall or trunk below, in a height tier that sets the shadow. They never rotate. Things that move or turn are drawn strictly top-down.
- **W6. Flat ground has no outline.** Roads, fields, water, sand and aprons are edged in their own darker tone. Only things with height, and the two landing zones, earn a navy line.
- **W7. Edges are transitions.** Two ground materials never meet on a bare edge. A runway has a shoulder and rough grass, water has shallows and sand, a road has a kerb and a verge.
- **W8. Detail clusters, open ground rests.** Tufts, flowers and props gather around objects and edges, and stay off runways, helipads and their approach ends. Only the airfield is mown in stripes; the countryside is not.
- **W9. Aircraft own the top of the range.** The brightest white and the most saturated colours belong to aircraft and paths. Scenery never uses `fuselage`, a path colour or `sun-500`; its whites are cream (`wall-100`, `hide-100`).

`world.md` works these nine out for scenery, ground and landing zones.

### UI

- **U1. Moulded, not boxed.** A control is a face in its base tone, a highlight along its top edge and a lip below it in its own 700 tone, wrapped by one thin outline. The lip is never navy.
- **U2. A lip means pressable.** Buttons, icon buttons, level cards, the toggle knob and the selected segment have a lip. Readouts, chips, stickers and coach marks are flat and never move when touched.
- **U3. Thin line, thick form.** The UI outline is `line-ui`, 2.5dp. Depth comes from the lip and a flat contact shadow, not from a heavier line.
- **U4. Tinted inside.** Dividers, keylines, perforations and chip edges take a darker tone of the surface they lie on. `outline` is for the outer edge of an element and for text and icons.
- **U5. Nested radii.** An inner corner is the outer radius minus the gap between them, so a shape inside another follows its curve.
- **U6. Tower paperwork.** HUD readouts are flight strips, dialogs are boarding passes with a perforated stub, verdicts are stamps.
- **U7. Disabled is flat, not faded.** A disabled control loses its colour, lip and shine and keeps full opacity, so it stays readable on any ground.

## Colour

Colours come in ramps. A ramp is one material or accent in three to six flat tones, and each step has a fixed role:

| Step | Role |
|---|---|
| 100 / 200 | Highlight: the lit top edge of a control, the brightest band of a roof or of metal |
| 300 | Lit tone (W1) |
| 500 | Base tone |
| 700 | Shade tone; the lip of a control |
| 900 | Tinted inner line (W3, U4); the bottom edge of a lip |

Short ramps start lighter or skip a step; the usage note of each step in `tokens.json` says what it is for.

- **Accents** are `sun`, `sky`, `cherry`, `tangerine` and `mint`. `sun-500` is the signature colour: the primary button, the score cell, the first flight path. Use one `sun-500` call to action per screen. `sky-500` is for secondary actions and information, `cherry-500` for lives and danger, `tangerine-500` for streaks. `mint-500` is the `success` teal, so it reads on grass and stays apart from `cherry-500`.
- **Surfaces** are the `paper` ramp: `paper-0` for the lightest faces (a ghost button, the toggle knob), `paper-100` for panels, dialogs, flight strips and level cards, `paper-200` for a header band, a strip's label cell and a disabled face, `paper-300` for wells and keylines, `paper-400` for the lip of a panel, `paper-500` for tinted lines on paper.
- **The UI lives in the sky:** `bg-sky` behind menus, with `sky-100` and `sky-300` as backdrop tints. They go dark in Night and Dawn, so nothing on paper uses them: a sky chip, an icon tile and the header of a neutral dialog take `sky-tint` and `sky-band`, which keep their value in every theme.
- **Text.** On paper: `ink`, secondary text `ink-muted`, a disabled label `ink-3`. On any bright fill: `on-accent`. Straight on the sky: `on-sky` and `on-sky-muted`. On an `outline` fill: `on-outline`. Status text on paper is `success-ink` and `danger-ink`.
- **Scenery materials** are for the field only, never for UI: `grass`, `leaf`, `pine`, `wood`, `soil`, `sand`, `straw`, `roof`, `wall`, `tin`, `stone`, `concrete`, `asphalt`, `road`, `water`, `hide` and `denim`, plus the single tokens `window`, `beacon`, `muzzle`, `foam` and `glass-700`. Paint on the ground is `marking` and `marking-warn`.
- **Aircraft own** the `fuselage` white, `fuselage-shade`, `glass` with its `glass-shine`, and the path colours `path-1` to `path-6`. Each aircraft on screen takes the next free path colour for both its livery and its flight path. Nothing on the ground looks like an aircraft or its path (W9).
- **Edges and shadows.** Never use pure black or grey. Silhouettes are `outline`; hard ground shadows are `shadow-cast`; the soft shadow of airborne things is `shadow-ground`; contact shadows under UI are `contact`, `contact-field` and `contact-overlay`.

### Themes

There are two themes: **Day** (default) and **Night**, for the planned night levels. Night darkens the sky and moonlights the field. Paper, strips and aircraft stay light and accent fills stay bright, so outlines and `on-accent` text hold in both. Do not invert the UI for Night.

Night and Dawn come from token values alone: no sprite is drawn differently. A window of a building takes `window`, which is `glass` by day and a warm light in Night and Dawn.

### Dawn

Levels set before sunrise use the **Dawn** palette: the sky, field, strips and clouds are dim and warm, and the ground shadow is cooler. Every token without a Dawn value keeps its Day value, so panels, strips, text and aircraft stay light as in Night.

Dawn levels also have small lights: `light-nav-red` and `light-nav-green` on the wingtips, `light-white` on the tail, `light-beacon` for the tower beacon and landing lights, and `light-agl` for the airfield ground lights. Light is gentle: no light is brighter than `light-max-opacity` and no light cycles faster than `dur-light-min`. The tower beacon flashes once per `dur-beacon`; the running light sweeps a runway once per `dur-running-light`. Nothing outside Dawn levels uses these.

### Sunset

The splash page has a palette of its own: `sunset-sky-top`, `sunset-sky-upper`, `sunset-sky-lower` and `sunset-sky-horizon` as four flat sky bands, `sunset-sun`, `sunset-silhouette` for the airport, and `sunset-tower-light` for the tower window. These seven colours are for that one scene. They are not a theme, and nothing else may use them.

## Type

Three faces, each with one job.

- **DynaPuff** (`font-display`) for headers, titles, button labels and the score: `display-xl`, `display-lg`, `display-md`, `dialog-title`, `score`, `score-compact`, `strip-value`, `title-lg`, `title-md`, `title-sm`, `button-label-lg`, `button-label`, `button-label-sm`.
- **Nunito** (`font-body`) for running text: `body-lg`, `body`, `body-sm`, `row`, `caption`. Never lighter than weight 600.
- **Inconsolata** (`font-text`) for labels, readouts and callsigns: `label`, `label-sm`, `readout`, `callsign`. Never lighter than weight 700. Its fixed pitch gives the game its control-tower voice.
- Write `label`, `label-sm` and `callsign` in capitals. Use tabular figures wherever a number changes.
- Group thousands with a thin space: `12 450`.
- Outline display type only on the wordmark. Everywhere else type is flat `ink` or `on-accent`.

## Edges, depth and shape

### Line ranks

Five line weights, heaviest first (W4). A lighter thing never takes a heavier line.

| Rank | Token | Used for |
|---|---|---|
| 1 | `line-hero` | The outline of an aircraft, and of nothing else. |
| 2 | `line-zone` | The outline of a runway, a helipad and a cloud. |
| 3 | `line-ui` | The outline of every control, panel, flight strip and dialog, and the silhouette of scenery. |
| 4 | `line-ui-sm` | Small parts: the selected segment, a heart, a chimney, a post. |
| 5 | `line-detail` | Lines inside a shape: keylines, dividers, tile joints, mullions. Always a darker tone of the surface, never `outline`. Scenery may go up to 1.5. |

All strokes have round joins and caps.

### Moulded controls

A pressable control is built back to front from five parts (U1):

1. a flat contact shadow, showing `contact-offset` below it;
2. one outline in `outline` at `line-ui`;
3. the lip in the fill's own 700 tone, with a `lip-edge` line of its 900 tone along the bottom;
4. the face in the base tone, with a `highlight` line of the light tone along its top edge;
5. on a coloured button, one small shine (`shine-width` by `shine-height`, at `opacity-shine`).

Lip heights are `lip-sm` (small button, HUD pause button), `lip-md` (button, icon button), `lip-lg` (large button), `lip-panel` (panel, level card) and `lip-dialog`. Small round parts carry their lip inside their own shape: `lip-inset` on the toggle knob and the selected segment. Pressing a control lowers its face until `lip-pressed` is left of the lip; the face moves down by the difference.

Panels and dialogs are moulded from `paper` tones but do not press. Only pressable things and these paper surfaces have a lip; everything else is flat (U2).

Wells are sunken: a `paper-300` fill with a `well-inset` band of `paper-400` along the inner top edge. The toggle track and the segmented trough are wells.

### Shadows

- UI sits on a flat, unblurred contact shadow straight below it: `contact` under controls, panels and cards on a menu, `contact-field` under everything in the HUD, `contact-overlay` under a dialog on the scrim. Their offsets are `contact-offset`, `contact-offset-field` and `contact-offset-overlay`.
- Everything standing on the ground casts a hard shadow down-right in `shadow-cast`. All of them share one layer drawn at `opacity-shadow-cast`, so overlapping shadows never darken twice (W1).
- Only airborne things keep a soft shadow: aircraft and clouds, in `shadow-ground`, offset down-right and further than any ground shadow (`shadow-float`).

### Radii

Corners are always round, and nested corners follow U5: an inner radius is the outer radius minus the gap. The selected segment (`radius-strip`) inside the trough (`radius-control`) is the model: the gap between them is `space-1`.

| Token | Used for |
|---|---|
| `radius-chip` | Chips and tags, the callsign tag, a stamp. |
| `radius-strip` | Flight strips, the HUD pause button, icon tiles, the selected segment. |
| `radius-control-sm` | Small buttons. |
| `radius-control` | Buttons, icon buttons, the segmented trough, coach marks. |
| `radius-control-lg` | The large button. |
| `radius-panel` | Panels and level cards. |
| `radius-dialog` | Dialogs. |
| `radius-pill` | Toggles, stickers, path dash caps. |

## Layout

- Spacing runs on `space-1` (4px) to `space-8` (64px). HUD items sit `space-4` from the screen edge and `space-3` apart.
- Every control is at least `touch-min` (48px). An aircraft's pick area is at least `touch-aircraft` (72px) however small it is drawn. A control drawn lower than `touch-min` (a small button, the HUD pause button, the toggle) keeps the full touch area around its drawing.
- World positions are in world units and scale with the viewport. UI sizes are fixed and do not scale with the field.
- Layer order is fixed by the `z-` tokens: field, zone glows, paths, ground shadows, aircraft, effects, clouds, HUD, overlays. Inside the field layer the hard ground shadows lie between flat ground and standing scenery (see `world.md`).

## States

- **Pressed:** the face drops onto its lip in `dur-press` with `ease-snap`. Flat things have no pressed state.
- **Focus:** a two-tone ring outside the control's own outline: a `line-ui` band of `focus`, then a `line-ui` band of `outline`. One band always contrasts with what is behind it.
- **Disabled:** flat, not faded (U7). The face is `paper-200` on a `lip-pressed` sliver of `paper-300`, the outline and the label are `ink-3`, there is no shine and no contact shadow, and the control keeps full opacity and does not move.
- **Valid landing:** a solid pulsing `zone-ok` halo. **Wrong zone:** a dotted `zone-no` halo. The halo's shape differs as well as its colour.
- Every status colour is paired with an icon or a word: `check` with `success`, `warning` with `danger`.

## Voice

- Speak like a friendly tower controller: short, calm, specific. "Two aircraft got too close over runway 27."
- Sentence case everywhere except `label`, `label-sm` and `callsign`. No exclamation marks outside celebration popups.
- Buttons start with a verb: Take off, Resume, Try again, Choose airport.
- Use the game's own nouns: aircraft (both kinds together), airplane, helicopter, runway, helipad, path, streak, life.
- Say what happened, then what to do. Never blame: "Missed" not "You failed".
- No emoji. Icons come from the Icons group.

## Iconography

Icons are the 22 glyphs in the Icons asset group: a 24px grid, 3px round strokes, filled where the shape is solid. In UI use the `fa-icon` element so they take the text colour. Sizes are `icon-xs`, `icon-sm`, `icon-md` and `icon-lg`. Do not mix in another icon set; draw a new glyph on the same grid instead.

## Using the kit

- `tokens.json` holds every token with its usage note. A token with a `deprecated` field is a version 1 leftover, kept only until the app's world is ported; never use one in new work. The field names its replacement.
- `bundle.css` holds every component class, prefixed `fa-`. It reads only token variables.
- `bundle.js` defines `window.FinalApproach` and the elements `fa-aircraft`, `fa-side-aircraft`, `fa-silhouette`, `fa-icon`, `fa-runway`, `fa-helipad`, `fa-cloud`, `fa-windsock`, `fa-burst`, `fa-scenery` (one scenery sprite with its cast shadow) and `fa-scene` (the picture of a whole level). It has no dependencies.
- Fonts are in `fonts/`: DynaPuff, Nunito and Inconsolata as variable `woff2` files.
- Component guidelines live in `components/<Name>.md`. Read one before using its component.
- The tools in `tools/` run with Node from the repository root:
  - `gen-kotlin.cjs` generates the Compose theme and sprite data, checks the scenery sprites against the world rules, and writes `assets/scenery/*.svg`.
  - `build-showcase.cjs` builds `showcase.html` from the tokens, `bundle.css`, `bundle.js` and the fonts.
  - `build-app-icon.cjs` builds the app icon SVGs, their PNG exports and the Android launcher layers.
  - `check-tokens.cjs` checks `tokens.json`: usage notes, replacements of deprecated tokens, ramp sizes, line ranks and type weights; with a git ref it also checks that no earlier token was removed or changed.
- There is no logo artwork yet. The Wordmark component is the name typeset in DynaPuff; use it wherever a logo would go.
- The game itself is Kotlin with Compose Multiplatform: see `compose.md` for how tokens map to the theme object.
