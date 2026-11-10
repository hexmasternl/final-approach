A reference composition of the splash page: the first screen of the app.

**You provide:** nothing: this is a showcase, not a component.

Three layers, back to front:

- **Sunset sky:** four flat bands from `sunset-sky-top` down to `sunset-sky-horizon`, and a low `sunset-sun` partly behind the airport. No gradient.
- **Airport silhouette:** one flat shape in `sunset-silhouette` along the bottom: hangar, terminal, parked tails, trees, and the control tower as the tallest shape, with its cab window lit in `sunset-tower-light`. The silhouette has no outline and no detail; it is the one place the outline rule does not apply.
- **Airplane and Banner:** a side-view light prop tows the Banner in from the left, 30% of the way down the screen. The Banner lets go at the centre and stays, waving; the airplane flies out on the right.

**Fitting the screen.** The silhouette is scaled as a whole and centred, and its ground runs on to both edges. It fills the width, except that the control tower is always between 24% and 44% of the screen height: on a tall, narrow screen the silhouette grows and its ends are cropped; on a wide, low screen it shrinks. The sky bands end at 30%, 52% and 72% of the height.

The page moves on to the main page 2 seconds after the banner has settled, or at once on a tap. With reduced motion on it shows the settled state from the start: banner centred and still, no airplane.

The sunset colours belong to this page only. Do not use them anywhere else.
