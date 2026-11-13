A dark speech bubble that teaches one gesture at a time.

**You provide:** the step count, a three-word instruction, one supporting sentence, an icon, and the thing it points at.

**Construction.** Class `fa-coach`. A flat surface in the outline colour (U2): `outline` fill with `on-outline` text, `radius-control`, a 10px tail pointing at its target (`fa-coach--up` puts the tail on top), on a flat `contact-field` shadow. It has no lip and no outline of its own; bubble and tail are one shape. Step line (`fa-coach__step`) in `label-sm`, instruction in `title-sm`, sentence in `row`, icon at `icon-lg`.

**Variants.**
- **Gesture** (default): teaches one gesture. Max width `coach-max-width`. Bobs 5px while waiting for the gesture. Dismissed the moment the gesture succeeds, never with a button.
- **Explanation** (`fa-coach--explain`): explains a rule without a gesture. Max width `coach-explain-max-width`, no bob. The step line, a title, the body, then a small primary Button "Got it" (`fa-btn--sm`, with a full `touch-min` touch area). It is dismissed only with Got it. "Got it" is the one exception to the rule that buttons start with a verb.
- **Tracking** (`fa-coach--track`): points at an aircraft that is on its way in. The tail points at the aircraft; the bubble sits `space-3` beyond the aircraft's pick radius (`touch-aircraft` / 2), above it by default and below it when it would cross the top band, clamped `space-4` inside the screen. No bob while tracking.

The tutorial has two kinds of coach mark: gesture marks, dismissed by the gesture succeeding and never with a button, and explanation marks, dismissed only with Got it.

**States.** The bubble itself does not react to touch. With reduced motion it does not bob.

**Do** point the tail at the thing the step is about. **Don't** cover a landing zone with the bubble, and don't give the bubble a lip: only its Got it button presses.

**Compose.** `FaCoachMark` in `ui/components/FaCoachMark.kt`, with `FaCoachTail` (Down, Up), a `tailOffset`, an optional `icon`, and `onGotIt` for the explanation variant. `TutorialOverlay` in `ui/game/TutorialOverlay.kt` places it.
