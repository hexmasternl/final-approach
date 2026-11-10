The live score in a HUD chip.

**You provide:** an integer score and a label (Score or Best).

Value in `score` (DynaPuff 32px, tabular figures), label above it in `label` `ink-muted`. Group thousands with a thin space: `12 450`. The compact variant (`fa-score--compact`) drops to 24px for Best on menus and level cards.

When the score rises, scale the value to 1.15 and back in `dur-pop` with `ease-bounce`, and count up over at most 400ms.
