A small outlined tag that names a property: aircraft type, speed class, landing zone, lock state.

**You provide:** one or two words, an optional leading icon at `icon-sm`, and a tone.

| Tone | Class | Meaning |
|---|---|---|
| Neutral | `fa-chip` | Facts: Runway, Helipad, Locked. |
| Success | `fa-chip fa-chip--success` | Slow, valid, safe. |
| Sun | `fa-chip fa-chip--sun` | Medium, new. |
| Tangerine | `fa-chip fa-chip--tangerine` | Fast. |
| Cherry | `fa-chip fa-chip--cherry` | Warnings: wide turns, wrong zone. Always with the warning icon. |
| Sky | `fa-chip fa-chip--sky` | Counts: 2 runways. |

28px tall, `radius-sm`, `line-2` `outline`, label in `button-label-sm`. Chips are not interactive and have no lip.
