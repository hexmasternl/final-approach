# World and rendering

How the airfield is drawn, bottom to top. Each layer names its `z-` token.

| Layer | Token | What is on it |
|---|---|---|
| Field | `z-field` | Lawn in `grass-500` with `grass-700` mowing stripes 64px wide, tilted 12 degrees. Runways, helipads, windsock, bushes. |
| Zone glows | `z-zone` | `zone-ok` and `zone-no` halos around landing zones. |
| Paths | `z-path` | Flight paths, end dots, straight-on projections. |
| Ground shadows | `z-ground-shadow` | Every aircraft's shadow, so no shadow ever falls on another aircraft. |
| Aircraft | `z-aircraft` | Sprites, selection ring, callsign tag. |
| Effects | `z-fx` | Star burst, poof, score popups. |
| Clouds | `z-clouds` | Ambient clouds at `opacity-cloud`. |
| HUD | `z-hud` | Score, streak, next aircraft, lives, pause, coach marks. |
| Overlay | `z-overlay` | `scrim` and dialogs. |

## Aircraft roster

| Type | Kind | Speed | Turn rate | Lands on | Drawn size |
|---|---|---|---|---|---|
| Light prop | Airplane | Slow | High | Runway | `sprite-light-prop` |
| Turboprop | Airplane | Medium | Medium | Runway | `sprite-turboprop` |
| Jet | Airplane | Fast | Low | Runway | `sprite-jet` |
| Jumbo | Airplane | Medium-fast | Very low | Runway | `sprite-jumbo` |
| Helicopter | Helicopter | Slow | Very high | Helipad | `sprite-helicopter` |

Drawn size grows with how hard the aircraft is to handle. Speed and turn values are gameplay configuration and are not design tokens.

## Sprite construction

- Draw on a 96-unit square, nose up, centred on the aircraft's pivot.
- 4-unit `outline` stroke, round joins and caps, on every shape.
- Body `fuselage`; engines and nacelles `fuselage-shade`; wings, tail or a body stripe in the livery colour.
- One cockpit window in `glass`, at least a quarter of the nose width, with one `glass-shine` streak.
- Propellers are a single dark bar that flicks in width; a rotor is a `prop-blur` disc with a spinning dark cross.
- No text, numbers or logos on an aircraft.

## Livery and path colour

Assign `path-1` to `path-6` in spawn order and free a colour when its aircraft lands or leaves. With more than six aircraft on screen colours repeat; the path still starts at its own aircraft, and the selected aircraft has its ring, so the link never rests on colour.

## Landing zones

- A runway is 64px wide at 1x. Its numbers follow real convention: heading divided by ten, the two ends 18 apart (`09/27`).
- A helipad is 112px across, with a `marking-warn` ring and a `marking` H.
- Zone feedback appears only while an aircraft is selected and its path ends on the zone.

## Screen edges

Aircraft enter from the viewport edges. Keep the top 88px free of entries so nothing spawns under the HUD, and announce each entry in the next-aircraft chip before it appears.
