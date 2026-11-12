A selectable card for one airport layout.

**You provide:** the airport name, a miniature of its layout (runways and helipads at their real angles), the best score, zone-count chips, and locked or unlocked.

220px wide: a 124px art area with mown-grass stripes and the layout miniature, then a body with the name in `title-md`, best score in `caption`, and Chip tags. `radius-lg`, `line-3` `outline`, `shadow-lip-lg`. Lifts 3px on hover.

Locked: art turns `surface-sunken`, the miniature fades to `opacity-disabled`, a round lock badge sits on top, and the meta line states the unlock condition. The lock badge and the Locked chip carry the state, not the fade alone.

The airport names in the preview are examples.

### Square variant

`fa-levelcard--square` is the tile of the level select page: a miniature and a name, nothing else.

**You provide:** the level name and its layout.

A `level-tile` (168px) square with the same face as the card: `surface` fill, `radius-lg`, `line-3` `outline`, `shadow-lip-lg`, `space-3` padding. At the top a `level-tile-art` (120px) square miniature of the layout: the ground (`grass-500`, or `sky-300` water with a `grass-300` island), runways as `asphalt-500` strips at their real angles and helipads as `asphalt-300` dots, all scaled from the level world. The miniature has no outline of its own. Dawn levels are dimmed with `sky-700` over the miniature. Under it, the name in `title-sm`, centred.

Tapping a tile starts that level. Tiles sit in one row, `space-4` apart, and scroll sideways when they do not fit.

**Don't** add a best score, tags or a lock to the square variant; use the full card for those.
