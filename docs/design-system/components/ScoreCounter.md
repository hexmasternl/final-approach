The live score and the best score, each in a flight strip.

**You provide:** an integer score and a label (Score or Best).

**Construction.** A Flight Strip (`fa-strip`) with a label cell and a value.
- **Score:** the label cell is the sun cell (`fa-strip__label--sun`): `sun-200` with a `sun-700` keyline, the `star` icon and the word in `label-sm`.
- **Best:** the plain label cell, `paper-200` with a `paper-400` keyline.
- **Value:** `strip-value` in `ink`, with tabular figures. Group thousands with a thin space: `12 450`.

The strip is flat: it has no lip and cannot be pressed (U2).

**States.** When the score rises, scale the value to 1.15 and back in `dur-pop`, and count up over at most 400ms. With reduced motion the value changes without the pop.

**Do** give the strip an accessible name of the label and the value: "Score 12450". **Don't** use the sun cell for anything but the live score.

In version 1 this was a HUD chip with the value in `score`; on a strip the value is `strip-value`.

**Compose.** `FaScoreCounter` in `ui/components/FaHudParts.kt`: `label`, `value`, `accent` for the sun cell, `icon` and `popOnRise`. It is a `FaFlightStrip`.
