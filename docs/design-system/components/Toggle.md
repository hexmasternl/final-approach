An on/off switch for settings that apply immediately.

**You provide:** a visible label next to it, an optional one-line description, and the checked state (`role="switch"`, `aria-checked`).

Track 68 x 40px, `radius-pill`, `line-2` `outline`, `surface-sunken` with `shadow-well` when off and `sky-500` when on. The knob is 28px, `surface-raised` when off and `sun` when on, and slides 28px in `dur-quick` with `ease-bounce`. State is carried by knob position and knob colour together, never colour alone.

Lay settings out as `fa-setting` rows: label left in `title-sm`, description under it in `body-sm` `ink-muted`, toggle right.

**Don't** use a toggle for a choice among three or more: use SegmentedControl.
