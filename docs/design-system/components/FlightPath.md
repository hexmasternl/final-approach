The dashed line a player draws from an aircraft to where it should go.

**You provide:** the polyline (smoothed), the livery index of its aircraft, and whether that aircraft is selected.

**Construction.** Two strokes on the same geometry, round caps and joins:
1. Casing: `outline`, width `path-casing` (14px).
2. Dash: the livery colour (`path-1` to `path-6`), width `path-stroke` (8px).

Both use dash `path-dash` (18px) and gap `path-gap` (16px). The path ends in a dot of `path-dot` diameter (22px) in the livery colour with a `line-3` outline. Paths render on `z-path`: over the runway, under every aircraft.

**States.**
- Selected: full opacity, dashes march towards the end dot, one dash length per `dur-march`.
- Idle: `opacity-path-idle`, no marching.
- Straight-on projection: when a path ends away from a valid zone, draw a thin dotted `outline` line at `opacity-ghost` from the end dot along the last heading, so players see where the aircraft will go.
- Valid end: the landing zone under the dot glows `zone-ok`. Wrong zone type: `zone-no`.

Consume the path from the aircraft's end as it flies: the line gets shorter, it does not fade.

**Don't** draw a path without its casing: yellow on grass disappears. Colour is never the only link between an aircraft and its path, because the path always starts at its aircraft.
