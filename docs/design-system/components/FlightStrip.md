The flat HUD readout: an optional labelled cell and a value. It replaces the version 1 HUD chip.

**You provide:** the value, and optionally a label of one word with an icon before it.

| Part | Class | Look |
|---|---|---|
| Strip | `fa-strip` | `paper-100`, `strip-height` high, `radius-strip`, `line-ui` `outline` |
| Label cell | `fa-strip__label` | `paper-200` with a `paper-400` keyline on its right; text in `label-sm` `ink-muted`, icon at `icon-xs` |
| Label cell, sun | `fa-strip__label fa-strip__label--sun` | `sun-200` with a `sun-700` keyline; text in `ink`. For the score only. |
| Value | `fa-strip__value` | `strip-value` in `ink`, tabular figures, `space-3` side padding |

**Construction.** A flat surface (U2): a fill inside one outline, standing on a flat `contact-field` shadow that shows `contact-offset-field` below it. It has no lip and never moves when touched. The keyline between the cells is a darker step of the cell it edges, never `outline` (U4). It is tower paperwork: a strip of paper with a printed heading (U6).

A strip without a label cell holds just a value, such as a timer or a row of hearts. A strip may hold a small aircraft sprite as its value; draw it flat, without its soft shadow.

**States.** None. A flight strip shows something and cannot be pressed.

**Do** group thousands with a thin space: `12 450`. **Do** keep every strip in a row `strip-height` high. **Don't** give a strip a lip, a shine or a pressed state, and don't put a control inside one.

Old name: the HUD chip of version 1 (56dp high, with a navy lip). The Score Counter, Spawn Counter, Lives Meter and Next Aircraft Hint are all flight strips now.

**Compose.** `FaFlightStrip` in `ui/components/FaHudParts.kt`: `label`, `accent` for the sun cell, `icon`, and the `value` as content. It is drawn with `faFlatSurface`.
