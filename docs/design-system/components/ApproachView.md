The airfield as an aircraft on final approach sees it: the one picture that leaves the top-down view.

**You provide:** a `landscape` (`meadow`, `island` or `savanna`), the aircraft's `type` and `livery`, a `view` (`chase`, `cockpit` or `none`), the landing end's `number`, and optionally `target="helipad"`, a `zone` (`ok` or `no`), `one-way`, and the `width` and `height` of the picture.

**Where it is used.** In illustrations only: the social visuals and the store listing. It is never drawn in the game, no rule reads it, and the Compose theme does not port it. The game stays top-down.

**The picture.** One-point perspective along the runway centre line. The eye is on the centre line, so the runway runs straight to the vanishing point on the horizon. Back to front:

- **Sky:** the menu sky. `bg-sky` with `sky-300` rays that fan out of the vanishing point, and Clouds low over the horizon.
- **Far edge:** a treeline in `leaf-700` on the meadow, low hills in `sand-500` on the savanna, a band of `water-700` at sea.
- **Flat ground:** the landscape's ground with patches of tone; a road and crop fields on the meadow, a dirt track on the savanna, a sea with an island (beach, shallows, foam) on the island. Then the mown airfield, an apron with its taxiway, the Runway and the Helipad.
- **Shadows:** one layer of hard shadows in `shadow-cast` at `opacity-shadow-cast`.
- **Standing scenery:** front-view sprites, far to near.
- **Aircraft:** a rear-view sprite over its soft shadow (`chase`), or the cockpit around the picture (`cockpit`).

**The world rules, in perspective.**

| Rule | Here |
|---|---|
| W1 One sun | The sun is on the left. Lit tones face left, shade tones face right. A standing thing throws a hard shadow to the right and towards the viewer. |
| W2 Flat tones | No gradient anywhere. The sky's depth comes from its rays, the ground's from its stripes. |
| W3 Navy outside | Front-view sprites have an `outline` silhouette and tinted lines inside. |
| W4 Line weight | The aircraft's `line-hero` sets the unit. The runway's outline at the threshold is `line-zone` in that unit, scenery `line-ui`, and the line of a standing thing thins with distance, down to 30% of its weight. The runway and helipad outlines lie on the ground, so they thin all the way to the horizon. |
| W6 Flat ground | Ground pieces have no outline. Only the Runway and the Helipad are edged in `outline`. |
| W7 Transitions | The runway keeps its shoulder and fringe, the helipad its pad and fringe, the island its beach, shallows and foam. |
| W8 Mown airfield | Only the airfield is striped. The stripes are parallel to the runway, so they run to the vanishing point. |
| W9 Aircraft own the top | Scenery uses no aircraft colour. Whites are cream. |

**Runway and Helipad.** Laid out from the same numbers as the top-down ones (`METRICS` in `bundle.js`): threshold bars, the number, aiming blocks, touchdown marks, the centre line or the one-way chevrons, edge lines, tyre rubber, edge lights and threshold lights. Seen from the landing end, the number reads upright. The four approach lights stand side by side in the fringe, left of the landing end: two `cherry-500`, two `foam`. A `zone` draws the halo around the target: a solid `zone-ok` band, or `zone-no` as dots, so the two differ by shape as well as colour.

**Rear-view aircraft** (`fa-rear-aircraft`). One sprite per type on a 160 by 96 grid, seen from behind with the gear down, in the same paint as the top-down sprites: `fuselage` body (the helicopter's pod is its livery colour, as from above), `fuselage-shade` engines, wings or tail in the livery colour, a `line-hero` outline. They keep their size ratio: the picture sets the width of a jumbo and every other type is smaller by its `sprite-` size. The tell-tale shapes stay:

| Type | From behind |
|---|---|
| Light prop | A straight high wing on struts, a propeller disc, fixed gear |
| Turboprop | A long straight wing, two nacelles with propeller discs |
| Jet | A swept low wing, two tail engines, a T-tail |
| Jumbo | A wide swept wing, four engines under it, a tall fin |
| Helicopter | A round pod under a rotor, skids, a tail rotor |

A propeller or rotor is a `prop-blur` disc with one dark blade.

**Front-view scenery.** The control tower, terminal, hangar, barn, silo, tree, pine, palm, acacia, bush, windsock and sailboat, drawn standing on their base (`FRONT` in `bundle.js`). They use the materials of their top-down sprites, so the tower keeps its `cherry` cab and the windsock its `tangerine` sock. A window takes `window`, so it lights up in Night and Dawn.

**Views.**

- **Chase** (default): the aircraft from behind, low and centred, with the threshold in view above it.
- **Cockpit:** the window frame in `fuselage-shade` with a `line-hero` outline, and the glare shield in `outline`, like every dark part of an aircraft. The light prop also shows its cowl with the livery stripe and its propeller disc.
- **None:** the airfield alone.

**Themes.** Day, Night and Dawn come from the tokens, as everywhere else. On the Dawn sky, set a title in `on-outline`: `on-sky` has no Dawn value.

**Do** keep the aircraft on the centre line, and leave the runway's landing end free of text. **Don't** use this view for a screen of the game, tilt the horizon, add a gradient or a haze to the sky, or draw a sun: the light comes from the left, outside the picture.

**Compose.** Not ported. `approachSvg(options)` in `bundle.js` returns the picture as SVG; the `fa-approach` element wraps it.
