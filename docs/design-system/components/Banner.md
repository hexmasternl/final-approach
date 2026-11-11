The banner an airplane tows across the splash page, carrying the name of the game.

**You provide:** nothing: the text is always "Final Approach".

A long pennant: `surface` fill, `line-3` `outline`, `radius-md`, with the name in DynaPuff 700 (`display-md`) in `ink`. It is at most 420px wide and never wider than 62% of the screen, 5.6 times as wide as it is high; on a narrow screen the type scales down with it.

**Inside.** The name has `space-5` of clear room on each side, inside the outline.

**Towed.** The banner trails behind a side-view light prop (`sprite-light-prop-side` long) on a tow line: a `line-2` `outline` stroke, `tow-line` long, hanging 18% of its length at the middle. The airplane crosses a third of the screen width per second. While towed the banner ripples: a wave every 0.9 seconds at 60% of the full height.

**Loose.** When its centre reaches the centre of the screen it lets go and dips once, 10px, over `dur-burst` with `ease-bounce`. From then on it waves slowly like a flag: a sine wave that travels along the banner, one wave every `dur-sway`, 5px from rest to crest and 190px from crest to crest. That is small enough that the name stays readable at every point.

The side-view airplane exists only for this scene. Gameplay is seen from above and uses the top-down sprites.
