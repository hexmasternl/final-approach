The celebratory pills: the streak badge and the score popup. Tilted, with a paper rim. They are the only tilted shapes, and they are flat.

This file replaces the version 1 components Streak Badge (`StreakBadge.md`) and Score Popup (`ScorePopup.md`).

**You provide:** a short text (a multiplier, a signed number, or two words), an optional icon and a tone.

**Construction.** `sticker-height` high, `radius-pill`. A tinted face with a `highlight` line along the top and a darker line of its 700 tone along the bottom, one `line-ui` `outline`, then a `sticker-rim` rim of `paper-0` outside the outline, so the sticker reads on any ground. It stands on a flat `contact-field` shadow. It is tilted a few degrees. Text is `on-accent` in DynaPuff with tabular figures; a caption after it is in `label-sm`; the icon is `icon-xs`.

A sticker has no lip and does not press (U2). The line along its bottom is a shade, not a lip.

### Streak badge

Shows the current landing streak multiplier, next to the score strip.

| Variant | Class | Face / highlight / shade |
|---|---|---|
| Streak | `fa-streak` | `tangerine-500` / `tangerine-200` / `tangerine-700` |
| Hot | `fa-streak fa-streak--hot` | `cherry-500` / `cherry-200` / `cherry-700` |

The `bolt` icon, then the value as `x3`. Hidden below x2. From x5 it turns hot. Each increase pops to 1.1 scale in `dur-pop` with `ease-bounce`. A miss or collision removes the badge with no animation: losing a streak should feel abrupt.

### Score popup

A short-lived sticker that rises from the place something happened on the field. It tilts the other way from the streak badge.

| Tone | Class | Shown when |
|---|---|---|
| Plain | `fa-popup` | Points for a landing: `+100`. A `paper-0` face. |
| Bonus | `fa-popup fa-popup--bonus` | Streak or tight-sequencing bonus: `+250` with the caption Streak x3. `sun-500`. |
| Success | `fa-popup fa-popup--success` | First landings in the tutorial: Landed, with `check`. `mint-500`. |
| Danger | `fa-popup fa-popup--danger` | Missed or Too close, with `warning`. `cherry-500`. |

Appears at the landing point on `z-fx`, pops in over `dur-pop`, rises 26px and fades with `ease-out-soft`, 1.8s in total. Never more than one popup per event.

**States.** None beyond the motion above. With reduced motion a sticker appears and disappears without popping or rising.

**Do** pair the success and danger tones with their icon or a word. **Don't** tilt anything else, and don't use a sticker for a lasting readout: that is a Flight Strip.

**Compose.** `FaSticker` in `ui/components/FaLabels.kt`, with `FaStickerTone` (Tangerine, Sun), an optional `icon` and an optional `caption`.
