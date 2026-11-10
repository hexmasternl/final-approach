The five flying toys, drawn top-down with the nose pointing up at heading 0.

**You provide:** `type`, `livery` (1 to 6, matching the path colour), `heading` in degrees clockwise from up, and optionally `selected` and a `callsign`.

| Type | `type` | Drawn size | Speed | Turn rate | Lands on | Tell-tale shape |
|---|---|---|---|---|---|---|
| Light prop | `light-prop` | `sprite-light-prop` 56px | Slow | High | Runway | Straight high wing, one nose propeller |
| Turboprop | `turboprop` | `sprite-turboprop` 68px | Medium | Medium | Runway | Long straight wing, two engine nacelles |
| Jet | `jet` | `sprite-jet` 72px | Fast | Low | Runway | Swept wing, two tail engines |
| Jumbo | `jumbo` | `sprite-jumbo` 104px | Medium-fast | Very low | Runway | Wide swept wing, four engines, fat body |
| Helicopter | `helicopter` | `sprite-helicopter` 64px | Slow | Very high | Helipad | Round pod, skids, rotor disc |

**Construction.** Every sprite is built on a 96-unit square. All shapes carry a 4-unit `outline` stroke with round joins. Body `fuselage`, engines `fuselage-shade`, wings or body accents in the livery colour, one big `glass` cockpit window with a single `glass-shine` streak. Types differ by silhouette first, so they stay readable for colour-blind players and when the livery repeats.

**Shadow.** `shadow-float` in `shadow-ground`, always offset down-right regardless of heading. Draw all shadows on `z-ground-shadow`, below every aircraft.

**Selected.** A dashed ring, 148% of the sprite, `select-ring` over an `outline` casing, rotating once every 7s, plus the callsign tag below in `callsign` on `outline`.

**Motion.** Propellers flick (`dur-rotor`), rotors spin (`dur-rotor`, linear). While turning, bank by squeezing the sprite to 86% across its wingspan. On touchdown, squash and stretch for `dur-land`, then remove.

**Don't** recolour the fuselage, mirror a sprite, or scale the jumbo down to match the others: the size difference is the point.

**Compose.** Port each sprite as an `ImageVector` or draw it in a `DrawScope` at 96 units, then `rotate(heading)` and `scale` at the render boundary. SVG sources are in the Aircraft asset group.
