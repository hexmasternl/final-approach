The banner an airplane tows across the splash page, carrying the name of the game.

**You provide:** nothing: the text is always "Final Approach".

A long pennant: `surface` fill, `line-3` `outline`, `radius-md`, with the name in DynaPuff 700 (`display-md`) in `ink`. It is at most 420px wide and never wider than 62% of the screen, 5.6 times as wide as it is high; on a narrow screen the type scales down with it.

**Towed.** The banner trails behind a side-view light prop on a tow line: a `line-2` `outline` stroke with a little sag. While towed it ripples quickly.

**Loose.** When its centre reaches the centre of the screen it lets go, dips once with `ease-bounce`, and from then on waves slowly like a flag: one wave every `dur-sway`, with `ease-sway`. The wave is small enough that the name stays readable at every point.

The side-view airplane exists only for this scene. Gameplay is seen from above and uses the top-down sprites.
