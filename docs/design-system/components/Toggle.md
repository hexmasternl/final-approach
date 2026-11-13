An on/off switch for settings that apply immediately.

**You provide:** a visible label next to it, an optional one-line description, and the checked state (`role="switch"`, `aria-checked`).

**Construction.** Class `fa-toggle`.
- **Track:** a well, `toggle-width-v2` by `toggle-height-v2`, `radius-pill`, `line-ui` `outline`. Off: `paper-300` with a `well-inset` band of `paper-400` along the inner top. On: `mint-500` with the band in `mint-700`, and a `mint-900` check mark at the left end.
- **Knob:** the one moulded piece (U2), `toggle-knob-v2` across: `paper-0` with a `line-ui` `outline` and a `lip-inset` lip of `paper-400` inside its own circle.

On is told three ways together: the knob's position, the mint track and the check mark. It never rests on colour alone.

Lay settings out as `fa-setting` rows: label left in `row`, description under it in `body-sm` `ink-muted`, toggle right. Rows are divided by a `line-detail` keyline in `paper-300` (U4).

**States.**
- Switching: the knob slides in `dur-quick` with `ease-bounce`; the track changes colour with `ease-snap`. With reduced motion the knob jumps.
- Focus: the two-tone ring, as on Button.
- Disabled: flat, not faded (U7). `paper-200` track and `paper-100` knob, outlines in `ink-3`, full opacity.

The drawing is `toggle-height-v2` high but the touch area is `touch-min`: pad the hit area, not the drawing.

**Don't** use a toggle for a choice among three or more: use SegmentedControl. **Don't** use it for an action that needs confirming.

**Compose.** `FaToggle` in `ui/components/FaToggle.kt`. The track is drawn with `drawWell`, the knob with `drawMoulded` and `toneKnob`. It keeps the switch role and a state description.
