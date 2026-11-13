Remaining lives as a row of hearts in a flight strip. A lost life is a paper heart.

**You provide:** lives left and lives total (three by default), and an accessible label such as "2 of 3 lives".

**Construction.** A Flight Strip without a label cell; its value is the row of hearts (`fa-strip__value fa-lives`).
- **A life** (`fa-life`): the `heart` icon in `cherry-500` with a `line-ui-sm` `outline`.
- **A lost life** (`fa-life fa-life--lost`): the same heart, in place, in `paper-300` with a `paper-500` line. It has no navy outline, so the count reads by line and tone as well as by colour.

The strip is flat (U2). The whole meter is one image with one accessible label; the hearts are not separate elements for a screen reader.

**States.** On losing a life, shake the strip 6px sideways twice in `dur-pop`. With reduced motion the heart changes without the shake.

On the hardest difficulty a collision ends the game: show a single heart.

**Don't** remove a lost heart: the empty place is what shows how many there were. **Don't** let the hearts stand alone on the field without their strip.

**Compose.** A `FaFlightStrip` with the hearts as its value.
