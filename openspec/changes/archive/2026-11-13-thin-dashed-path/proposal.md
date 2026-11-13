## Why

The animated flight path the player draws is far too thick, with far too thick a border. It hides the field and looks heavy next to the aircraft. The author wants a thin, fine dashed line with a thin border, and a marching animation at half the speed.

GitHub issue: #55

## What Changes

- The flight path is drawn as a thin, fine dashed line: coloured dash 3px wide, outline casing 7px wide (a 2px border on each side), dash 10px, gap 8px. Today they are 8px, 14px, 18px and 16px.
- The end dot shrinks to match (12px with a `line-2` outline instead of 22px with `line-3`).
- The dashes march at half the current animation speed: `dur-march` goes from 900ms to 1800ms.
- The values change in the design system first (tokens, FlightPath component guideline, showcase) and then in the generated theme.

Out of scope: path colour and livery rules, the cleared-to-land flash, the straight-on projection, the proximity ring, and the pick area of aircraft.

Depends on: nothing.

## Capabilities

### New Capabilities

- `flight-path-look`: how a flight path is drawn on screen (stroke, border, dash and march speed).

### Modified Capabilities

None.

## Impact

- Design system: `docs/design-system/tokens.json` (`path-stroke`, `path-casing`, `path-dash`, `path-gap`, `path-dot`, `dur-march`), `components/FlightPath.md`, `showcase.html` and `showcase.pdf`, regenerated `ui/theme` (`FaDimens`, `FaMotion`).
- `shared` `ui/game`: `FlightPathDrawing.kt` (no new literals).
