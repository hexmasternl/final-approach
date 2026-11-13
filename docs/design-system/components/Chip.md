A flat, tinted tag that names a property: landing zones, a best score, a lock reason, a status. Never pressable.

**You provide:** one to three words, an optional leading icon, and a tone.

| Tone | Class | Fill / edge / text | Meaning |
|---|---|---|---|
| Sky | `fa-chip` or `fa-chip fa-chip--sky` | `sky-100` / `sky-300` / `sky-900` | Facts and counts: 1 runway, 1 helipad. |
| Sun | `fa-chip fa-chip--sun` | `sun-200` / `sun-700` / `ink` | A best score, with `star`. |
| Success | `fa-chip fa-chip--success` | `mint-200` / `mint-700` / `mint-900` | Landed, valid, safe. With `check`. |
| Cherry | `fa-chip fa-chip--cherry` | `cherry-200` / `cherry-700` / `cherry-900` | Warnings: too close, wrong zone. Always with `warning`. |
| Tangerine | `fa-chip fa-chip--tangerine` | `tangerine-200` / `tangerine-700` / `ink` | Streaks. |
| Plain | `fa-chip fa-chip--plain` | `paper-200` / `paper-400` / `ink-muted` | Locked, unavailable, a count on a panel header. |

**Construction.** `chip-height` high, `radius-chip`, a tinted fill with a fine edge in a darker tone of the same colour (U4). It has no `outline`, no lip and no contact shadow (U2). The label is short, heavy Nunito; the icon is `icon-xs`.

The words on the chip carry the meaning; the tint only supports them.

**States.** None. A chip does not react to touch or focus.

**Do** pair every status tone with an icon or a word. **Don't** make a chip a button: a pressable tag is a small Button. **Don't** use a chip for a HUD readout: that is a Flight Strip.

In version 1 the chip was a small outlined tag and the HUD readouts were called HUD chips. The HUD readouts are now Flight Strips (`FlightStrip.md`).

**Compose.** `FaChip` in `ui/components/FaLabels.kt`, with `FaChipTone` (Sky, Sun, Mint, Cherry, Plain). The label is set in `caption`.
