# Proximity ring

A slowly spinning dashed red ring around every aircraft that is too close to another one. It is a warning, not a failure: the aircraft have not touched yet. A collision happens only when their shapes touch.

**You provide:** the warned aircraft (from the engine's game state), each with its position and collision bounding radius.

| Part | Token |
|---|---|
| Radius | the aircraft's collision bounding radius plus `warn-ring-margin` |
| Casing | `outline`, 6px |
| Dash | `warning`, 3px, `warn-ring-dash` on, `warn-ring-gap` off |
| Spin | one turn per `dur-ring-spin`, linear, endless |
| Appear | scale and fade in over `dur-pop` with `ease-bounce` |
| Disappear | fade out over `dur-quick` with `ease-out-soft` |

Draw it on the effects layer, above the aircraft and below clouds and the HUD. Every aircraft of a close pair or group gets its own ring.

Colour does not carry the meaning alone: the dashes and the spin set it apart from the selection ring (finer dashes in `select-ring`, slower) and from flight paths. With reduced motion on, the ring is drawn dashed but does not spin; it still appears and disappears with a plain fade.

## Collision variant

When a level ends by a collision, each of the two colliding aircraft gets the same ring in the `danger` colour instead of `warning`, at full size and full opacity, drawn at once (no appear animation). It does not spin: the dashes stay at the angle they had at the collision, with or without reduced motion. The ring is still dashed and sits with a warning icon and a word in the game ended dialog, so colour does not carry the meaning alone. The two aircraft and their rings are drawn above a `blackout` layer that fades in over `dur-collision-fade` with `ease-sway` and holds for `dur-collision-hold` before the dialog appears.
