A modal that stops the game: paused, game over, quit confirmation.

**You provide:** a headline of one or two words, one sentence of body copy, optional stats, and one to three actions with the primary first.

`surface` fill, `line-4` `outline`, `radius-xl`, `shadow-lip-lg` plus `shadow-soft`, `space-6` padding, at most `dialog-max-width` (400px) wide, centred on a `scrim` with `space-5` around it. Headline in `display-lg`, body in `body-lg` `ink-muted`. Stats sit in a two-column grid of `surface-sunken` wells: label in `label`, value in `score`.

**Compact variant** (`.fa-dialog--compact`). Used when the space inside the system bars is wider than it is tall and less high than `dialog-compact-breakpoint` (600px): a phone in landscape. The layout follows that measured space, not the device orientation, so split screen and tall landscape tablets keep the default layout. Same card, outline, `radius-xl`, lip and soft shadow, and the same drop-in; at most `dialog-compact-max-width` (720px) wide, `space-4` padding and `space-2` around it. Headline in `title-lg`, body in `body` `ink-muted`.

- Without stats (paused, quit): headline, body, then the actions in one row of equal-width buttons, primary at the start, `space-3` apart.
- With stats (game over): two panes, one third and two thirds of the width. The start pane holds headline, body and the actions stacked as `button-sm` buttons, each in a full `touch-min` row (the room under the face is the gap). The end pane holds the stats in two columns of compact wells (`.fa-stat--compact`), `space-1` apart in height: an `icon-md` aircraft at the start when the stat is per type, the `label` in capitals, and the value in `title-sm` at the end, all on one line. A compact well is sunken like any well: no lip, `space-2` padding including its `line-2` border.

On a 640×360 phone with its system bars and a 50px banner under it, the game over dialog with all five types fits without scrolling. The card still scrolls as a last resort, on smaller screens or at a large font size, so the actions are never cut off.

Drops in from 40px above over `dur-dialog` with `ease-bounce`. The simulation is frozen while a dialog is open.

**Copy.** Say what happened in plain words and name the place: "Two aircraft got too close over runway 27." The primary action after a loss is always Try again.
