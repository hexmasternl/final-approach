A dark speech bubble that teaches one gesture at a time.

**You provide:** the step count, a three-word instruction, one supporting sentence, an icon, and the thing it points at.

`outline` fill with `on-outline` text, `radius-md`, a 10px tail pointing at its target (`fa-coach--up` puts the tail on top). Instruction in `title-sm`, sentence in `body`, step line in `label`. Bobs 5px while waiting for the gesture.

**Variants.**
- **Gesture** (default): teaches one gesture. Max width `coach-max-width` (300px). Bobs while waiting. Dismissed the moment the gesture succeeds, never with a button.
- **Explanation** (`fa-coach--explain`): explains a rule without a gesture. Max width `coach-explain-max-width` (360px), no bob. The step line in `label`, a title in `title-sm`, the body in `body`, then a small primary Button "Got it" (`fa-btn--sm`, drawn 40px with a 48px touch target). It is dismissed only with Got it. "Got it" is the one exception to the rule that buttons start with a verb.
- **Tracking** (`fa-coach--track`): points at an aircraft that is on its way in. The tail points at the aircraft; the bubble sits `space-3` beyond the aircraft's pick radius (`touch-aircraft` / 2), above it by default and below it when it would cross the top band, clamped `space-4` inside the screen. No bob while tracking.

The tutorial has two kinds of coach mark: gesture marks, dismissed by the gesture succeeding and never with a button, and explanation marks, dismissed only with Got it.
