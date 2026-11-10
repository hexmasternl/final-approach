An ambient cloud that drifts across the field above the aircraft.

**You provide:** a width (92 to 176px) and a drift speed.

Seven overlapping lobes in `cloud` with an 8-unit `outline` silhouette and one `cloud-shade` belly stroke. Drawn at `opacity-cloud` on `z-clouds` so aircraft stay visible underneath, with a ground shadow in `shadow-ground` offset further than an aircraft's, because clouds fly higher. One crossing takes `dur-drift`.

Clouds are decoration. Keep at most three on screen, never spawn one over a landing zone threshold, and remove them entirely when reduced motion is on if they distract.
