A row of two to four mutually exclusive choices, one always selected.

**You provide:** the options (one word each), which one is pressed (`aria-pressed="true"`), and a group label.

**Construction.**
- **Trough** (`fa-segmented`): a well. `paper-300` fill with a `well-inset` band of `paper-400` along the inner top, `line-ui` `outline`, `radius-control`, `space-1` padding and gap.
- **Segment** (`fa-segment`): `segment-height` high, `radius-strip`, plain text in `button-label-sm` `ink-muted`.
- **Selected segment:** the one moulded piece in the trough (U2). `sun-500` face with a `sun-200` `highlight`, a `lip-inset` lip of `sun-700` inside its own shape, a `line-ui-sm` `outline` and an `on-accent` label. Its radius is the trough's radius minus the padding (U5).

The selected segment is raised and outlined, so the choice never depends on colour alone.

Used for difficulty. The game design ties difficulty to what a collision costs, so show the consequence under the control in `body`, and update it when the selection changes.

**States.** Focus shows the two-tone ring on the segment. Each segment is drawn `segment-height` high but takes touches over a `touch-min` area.

**Don't** exceed four segments or wrap them to two lines. **Don't** give the unselected segments an outline or a lip.

**Compose.** `FaSegmentedControl` in `ui/components/FaSegmentedControl.kt`. The trough is drawn with `drawWell`, the selected segment with `drawMoulded` and `toneSegment`.
