A modal that stops the game, built as a boarding pass: a tinted header, the message, a perforation, and a stub with stats and actions.

**You provide:** an eyebrow of a few words, a title of one or two words, one sentence of body copy, a header tone, optionally a stamp and stats, and one to three actions with the primary first.

**Construction.** Class `fa-dialog`, centred on a `fa-scrim` in `scrim` with `space-5` around it. A moulded paper surface that does not press: a `paper-100` face, a `lip-dialog` lip of `paper-400` with a `lip-edge` line of `paper-500`, one `line-ui` `outline`, `radius-dialog`, on a flat `contact-overlay` shadow showing `contact-offset-overlay` below it. At most `dialog-max-width` wide. From top to bottom (U6):

1. **Header band** (`fa-dialog__head`): a tinted band with a rule in a darker step of the same colour under it. It holds the eyebrow and the title.
2. **Eyebrow** (`fa-dialog__eyebrow`): a short line in `label-sm`, in capitals, in `ink` at `opacity-eyebrow`. It names the source or the place: "Tower · holding", "Tower log · runway 27".
3. **Title** (`fa-dialog__title`): `dialog-title` in `ink`.
4. **Body** (`fa-dialog__body`): one sentence in `body` `ink-muted`, with the optional stamp under it.
5. **Perforation** (`fa-perf`): a dashed line in `paper-500` (`rule-dash`, `rule-gap`) with a punched half-hole of `perforation-dot` where it meets each edge of the pass.
6. **Stub:** the stats (`fa-dialog__stats`), then the actions (`fa-dialog__actions`) as block buttons, `space-3` apart.

| Header tone | Class | Band / rule | Use for |
|---|---|---|---|
| Neutral | `fa-dialog__head` | `sky-300` / `sky-500` | Paused, a confirmation. |
| Failure | `fa-dialog__head fa-dialog__head--danger` | `cherry-200` / `cherry-500` | Game over after a collision or a miss. |
| Success | `fa-dialog__head fa-dialog__head--success` | `mint-200` / `mint-500` | A level cleared. |

The title and the body say what happened; the tint only supports them.

**Stats as cells.** Stats stand in two columns of cells (`fa-stat`) divided by keylines in `paper-300`, not in boxes (U4). A cell has its label in `label-sm` `ink-muted` above its value in `strip-value`.

**Stamp** (`fa-stamp`). The verdict in two or three words, pressed on at an angle: a double border in `cherry-700`, text in `label-sm` in `cherry-900`, `radius-chip`. `fa-stamp--success` is `mint-700` with `mint-900` text. A stamp always stands next to the same words as text; it never carries the message alone, and a screen reader skips it.

**Compact variant** (`fa-dialog--compact`). Used when the space inside the system bars is wider than it is tall and less high than `dialog-compact-breakpoint`: a phone in landscape. The layout follows that measured space, not the device orientation, so split screen and tall landscape tablets keep the upright layout. The pass lies on its side: header and body on the left (`fa-dialog__lead`), a vertical perforation, and the stub on the right (`fa-dialog__stub`) with the stats and small buttons. At most `dialog-compact-max-width` wide, with a smaller title and tighter padding.

On a 640 by 360 phone with its system bars and a 50px banner under it, the game over dialog fits without scrolling.

**States.** Drops in from 40px above over `dur-dialog` with `ease-bounce`; with reduced motion it is in place at once. The simulation is frozen while a dialog is open, and everything behind the scrim cannot be touched.

**Copy.** Say what happened in plain words and name the place: "Two aircraft got too close over runway 27." The primary action after a loss is always Try again.

**Do** keep one primary button, first. **Don't** draw a line inside the pass in `outline`, don't put stats in wells or boxes, and don't use a stamp without the sentence it repeats.

**Compose.** `FaDialog` in `ui/components/FaDialog.kt`: `title`, `body`, `actions` (`FaDialogAction`), `eyebrow`, `tone` (`FaDialogTone`: Neutral, Failure, Success), `stamp`, stats as `content` (`FaDialogStats` with `FaDialogStat`) and a `below` slot for an ad banner. `dialogLayoutFor` in `FaDialogLayout.kt` picks `FaDialogLayout.Portrait` or `Compact`. In the compact layout with stats the buttons stand side by side and a stat cell has label and value on one line. The stamp is `FaStamp` in `FaLabels.kt`. The pause dialog and `GameEndedDialog` are built on it.
