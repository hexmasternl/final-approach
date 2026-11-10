The three feedback animations that tell the player what just happened.

**You provide:** the event and its position on the field.

| Effect | Element | When | Timing |
|---|---|---|---|
| Star burst | `fa-burst kind="star"` | Safe landing | `dur-burst`, `ease-bounce` |
| Poof | `fa-burst kind="poof"` | Collision | `dur-burst`, `ease-bounce` |
| Squash and stretch | `data-fx="land"` on the aircraft | Touchdown, before the star burst | `dur-land`, `ease-bounce` |
| Bank | `data-fx="bank"` on the aircraft | While turning | Follows turn rate |

Star burst: one large `star` star, two small ones and eight `outline` rays, popping from 30% to 112% then settling. Poof: seven `poof` lobes with a single `outline` silhouette and four loose puffs. Both render on `z-fx` and are 120px at 1x.

Pair every effect with its sound: ding for the star burst, boing for the poof. With reduced motion on, show the final frame for 400ms instead of animating.
