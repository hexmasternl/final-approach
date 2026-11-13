An ambient cloud that drifts across the field above the aircraft. Airborne: it keeps the soft shadow.

**You provide:** a width and a drift speed. The element is `fa-cloud`.

**Construction.** One lobed silhouette in three tones: `cloud` on top, a `cloud-shade` underside, and one `glass-shine` highlight on the lit lobe (W1). The silhouette is `outline` at `line-zone`, the weight of a landing zone and lighter than any aircraft (W4). Two short inner lines are `cloud-shade`, not navy.

Drawn at `opacity-cloud` on `z-clouds` so aircraft stay visible underneath. A cloud is in the air, so its shadow is soft, in `shadow-ground`, and falls further away than an aircraft's, because clouds fly higher. In a scene the shadow is blurred by `shadow-cloud-blur` at `opacity-shadow-cloud`. One crossing takes `dur-drift`.

**States.** None. With reduced motion a cloud stands still.

Clouds are decoration. **Do** keep at most three on screen. **Don't** spawn one over a landing zone threshold, and don't give a cloud a hard ground shadow.

**Compose.** Clouds are drawn with the app's world and menu backdrop, which still render version 1 until the port.
