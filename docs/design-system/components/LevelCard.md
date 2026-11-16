A pressable card for one airport. Its art is the level itself, cropped, so a card always matches its airfield.

**You provide:** the airport name, the level whose picture it shows, the best score, one or two tags, and locked or unlocked.

**Construction.** Class `fa-levelcard`. A moulded paper card that presses like a button (U2): a `paper-100` face, a `lip-panel` lip of `paper-400` with a `lip-edge` line of `paper-500`, one `line-ui` `outline`, `radius-panel`, on a flat `contact` shadow.

- **Art** (`fa-levelcard__art`): the top of the card, a crop of the level's own scene without aircraft, paths or clouds (`fa-scene` with `ground` and `crop`). It is drawn from the level's ground pieces and scenery, so mown stripes, runway and surroundings are the real ones. A `line-ui` `outline` line divides it from the body.
- **Best score** (`fa-levelcard__badge`): a sun Chip with the `star` icon in the top-right corner of the art.
- **Body** (`fa-levelcard__body`): the name in DynaPuff (`fa-levelcard__title`), then Chip tags (`fa-levelcard__tags`) such as "1 runway" and "1 helipad". A meta line (`fa-levelcard__meta`) is `caption` in `ink-muted`.

**States.**
- Pressed (`.is-pressed`): the face drops onto its lip until `lip-pressed` is left, in `dur-press` with `ease-snap`.
- Focus: the two-tone ring, as on Button.
- Locked (`aria-disabled="true"`): the art is washed out under a round lock badge (`fa-levelcard__lock`: a `paper-0` disc with a `line-ui` `outline` and the `lock` icon), the name is `ink-muted`, and the tags become one plain Chip that states the unlock condition: "Land 50 to unlock". The card does not press. The lock badge and the words carry the state, not the washed-out art alone.

The airport names in the showcase are examples.

**Do** crop the art so the runway is in view. **Don't** draw a miniature that differs from the level, and don't put more than two tags on a card.

In version 1 the art was a schematic miniature on mown stripes, and a square variant existed for the level select page. Version 2 has one card.

**Compose.** `LevelCard` in `ui/game/LevelCard.kt`, shown by `LevelSelectPage`. An unlocked card is a `FaMoulded` paper card (`radiusPanel`, `lipPanel`) `level-tile` wide; a locked card is flat (`faFlatSurface`, paper tones, no lip, full opacity) and reports itself disabled with its name and unlock text. Its parts, top to bottom: the art (`level-tile-art` high), a `lineUi` line, then the name in `titleSm` and the tag chips. The art is a crop of the level's own field, drawn by the same code as the game (`drawFieldStatic` in `ui/game/LevelBackdrop.kt`) in the palette of the level's own theme, through `LevelMiniature` in `ui/game/LevelMiniature.kt`. `miniatureCrop` picks the smallest square around every runway and helipad with its fringe, kept inside the world; the art area shows that whole square and, on its longer side, a little more of the world around it. Only the crop is drawn, once, into a bitmap the size of the art, and the bitmap is kept until the size, level, palette or ground art changes. The art has no aircraft, paths, clouds or moving ambient life.

The card takes a `LevelCardState`: the best score (the sun chip with the star shows only above zero, and never on a locked card), up to two tags from `levelTags` ("1 runway", "2 runways", "1 helipad") and an optional `LockedInfo` with the text of the plain chip. Whether a level is locked, and the progress text, are decided by the caller, not by the card. The wash over a locked card's art is `paper-0` at `opacity-level-wash`; the lock badge is `lock-badge` wide.
