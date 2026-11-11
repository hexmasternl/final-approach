A short-lived pill that rises from the place something happened on the field.

**You provide:** the text (a signed number, or two words) and a tone.

| Tone | Class | Shown when |
|---|---|---|
| Plain | `fa-popup` | Points for a landing: `+100`. |
| Bonus | `fa-popup fa-popup--bonus` | Streak or tight-sequencing bonus: `+250 STREAK X3`. |
| Success | `fa-popup fa-popup--success` | First landings in the tutorial: Safe landing, with `check`. |
| Danger | `fa-popup fa-popup--danger` | Missed or Too close, with `warning`. |

Appears at the landing point on `z-fx`, pops in over `dur-pop`, rises 26px and fades with `ease-out-soft`, 1.8s in total. Carries `shadow-float` because it is airborne. Never more than one popup per event.
