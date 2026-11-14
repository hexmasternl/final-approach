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

**Compose.** `LevelTile` in `ui/game/LevelSelectPage.kt`: a `level-tile` square on `FaMoulded` with `tonePaper`, holding a `level-tile-art` miniature and the name in `title-sm`. Its miniature still draws the version 1 layout until the app's world is ported.
