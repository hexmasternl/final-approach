Final Approach is a top-down air traffic game that looks like a box of toys tipped onto a lawn. The player taps an aircraft, drags a path, and lands it. Everything on screen should look like you could pick it up.

## Principles

1. **Outline everything.** Every drawn shape carries a thick `outline` stroke (`line-3`, 4px) with round joins. No hairlines, no shape without an edge. Scenery is the one lighter voice: it is outlined in `line-2` and may carry details in `line-fine` (see World and rendering).
2. **Flat and bright.** Fills are single flat colours. No gradients, no textures, no blur except the soft ground shadow of things that fly.
3. **Toy scale.** Sizes are exaggerated, not realistic. A jumbo is nearly twice a light prop; a cockpit window is a third of the nose.
4. **Silhouette before colour.** Aircraft types, landing zones and states are told apart by shape first. Colour confirms; it never carries meaning alone.
5. **The field stays clear.** UI lives in small outlined chips at the edges. Nothing covers a landing zone.

## Colour

- The world is `grass-500` lawn striped with `grass-700`, `asphalt-500` runways with `marking` paint, and aircraft in `fuselage` white.
- The UI lives in the sky: `bg-sky` behind menus, `sky-500` for secondary actions.
- `sun` is the signature colour: the primary button, the score, the first flight path. Use one `sun` call to action per screen.
- `tangerine` and `cherry` are warm accents for streaks, lives and danger. `success` is a teal so it reads on grass and stays apart from `cherry`.
- Text on any bright fill is `on-accent`. Text on panels is `ink`, secondary text `ink-muted`. Text straight on the sky is `on-sky` and `on-sky-muted`. Text on an `outline` fill is `on-outline`.
- Each aircraft on screen takes the next free path colour, `path-1` to `path-6`, for both its livery and its flight path.
- Never use pure black or grey for an edge or shadow. Edges are `outline`; ground shadows are `shadow-ground`.
- Scenery has colours of its own: `sand-300`, `soil-500`, `soil-700`, `roof-500`, `foam` and `stone-300`. They are for decoration on the field only, never for UI. Scenery in turn never takes a path colour, `sun` or the `fuselage` white.

### Themes

There are two themes: **Day** (default) and **Night**, for the planned night levels. Night darkens the sky and moonlights the field. Panels, chips and aircraft stay paper-light and accent fills stay bright, so outlines and `on-accent` text hold in both. Do not invert the UI for Night.

### Dawn

Levels set before sunrise use the **Dawn** palette: the sky, field, strips and clouds are dim and warm, and the ground shadow is cooler. Every token without a Dawn value keeps its Day value, so panels, chips, text and aircraft stay light as in Night.

Dawn levels also have small lights: `light-nav-red` and `light-nav-green` on the wingtips, `light-white` on the tail, `light-beacon` for the tower beacon and landing lights, and `light-agl` for the airfield ground lights. Light is gentle: no light is brighter than `light-max-opacity` and no light cycles faster than `dur-light-min`. The tower beacon flashes once per `dur-beacon`; the running light sweeps a runway once per `dur-running-light`. Nothing outside Dawn levels uses these.

### Sunset

The splash page has a palette of its own: `sunset-sky-top`, `sunset-sky-upper`, `sunset-sky-lower` and `sunset-sky-horizon` as four flat sky bands, `sunset-sun`, `sunset-silhouette` for the airport, and `sunset-tower-light` for the tower window. These seven colours are for that one scene. They are not a theme, and nothing else may use them.

## Type

- Set headers, titles, button labels and the score in **DynaPuff** (`font-display`): styles `display-xl` to `button-label-sm`.
- Set regular text, labels and readouts in **Inconsolata** (`font-text`): `body-lg`, `body`, `body-sm`, `caption`, `readout`, `label`, `callsign`. Its fixed pitch gives the game its control-tower voice.
- Never set Inconsolata lighter than 500. Thin strokes look weak beside 4px outlines.
- Write `label` and `callsign` in capitals. Use tabular figures wherever a number changes.
- Group thousands with a thin space: `12 450`.
- Outline display type only on the wordmark. Everywhere else type is flat `ink` or `on-accent`.

## Edges, depth and shape

- Strokes: `line-1` for details inside a shape, `line-2` for small UI and for scenery outlines, `line-3` by default, `line-4` for dialogs.
- `line-fine` (1.5px) is the thinnest stroke and the one exception to "no hairlines". It is allowed only inside a scenery sprite, for details such as a cow's patches, a window or a roof rib. Never use it for an outline, for UI, or on an aircraft.
- Depth comes from a hard lip, not a blur: `shadow-lip-sm`, `shadow-lip`, `shadow-lip-lg` are solid `outline` offsets straight down. Pressing a control collapses its lip and moves it down by the difference.
- Only airborne things get a soft shadow: `shadow-float`, offset down-right as if the sun sits top-left.
- Wells are sunken with `shadow-well` on `surface-sunken`.
- Corners are always round: `radius-sm` chips, `radius-md` buttons and HUD chips, `radius-lg` panels and cards, `radius-xl` dialogs, `radius-pill` toggles and badges.

## Layout

- Spacing runs on `space-1` (4px) to `space-8` (64px). HUD items sit `space-4` from the screen edge and `space-3` apart.
- Every control is at least `touch-min` (48px). An aircraft's pick area is at least `touch-aircraft` (72px) however small it is drawn.
- World positions are in world units and scale with the viewport. UI sizes are fixed and do not scale with the field.
- Layer order is fixed by the `z-` tokens: field, zone glows, paths, ground shadows, aircraft, effects, clouds, HUD, overlays.

## States

- Focus: a two-tone ring, 3px `focus` then 3px `outline`. One band always contrasts with what is behind it.
- Disabled: `opacity-disabled`, no lip movement.
- Valid landing: a solid pulsing `zone-ok` halo. Wrong zone: a dotted `zone-no` halo. The halo's shape differs as well as its colour.
- Every status colour is paired with an icon or a word: `check` with `success`, `warning` with `danger`.

## Voice

- Speak like a friendly tower controller: short, calm, specific. "Two aircraft got too close over runway 27."
- Sentence case everywhere except `label` and `callsign`. No exclamation marks outside celebration popups.
- Buttons start with a verb: Take off, Resume, Try again, Choose airport.
- Use the game's own nouns: aircraft (both kinds together), airplane, helicopter, runway, helipad, path, streak, life.
- Say what happened, then what to do. Never blame: "Missed" not "You failed".
- No emoji. Icons come from the Icons group.

## Iconography

Icons are the 21 glyphs in the Icons asset group: a 24px grid, 3px round strokes, filled where the shape is solid. In UI use the `fa-icon` element so they take the text colour. Sizes are `icon-sm`, `icon-md`, `icon-lg`. Do not mix in another icon set; draw a new glyph on the same grid instead.

## Using the kit

- `components/bundle.css` holds every component class, prefixed `fa-`. It reads only token variables.
- `components/bundle.js` defines `window.FinalApproach` and the elements `fa-aircraft`, `fa-icon`, `fa-runway`, `fa-helipad`, `fa-cloud`, `fa-windsock`, `fa-burst` and `fa-scenery`. It has no dependencies.
- Component guidelines live in `components/<Name>/README.md`. Read one before using its component.
- There is no logo artwork yet. The Wordmark component is the name typeset in DynaPuff; use it wherever a logo would go.
- The game itself is Kotlin with Compose Multiplatform: see the section on building it in Compose for how tokens map to the theme object.
