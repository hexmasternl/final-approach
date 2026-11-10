The chunky toy-block button for every text action in menus and dialogs.

**You provide:** a label of one to three words, optionally one leading icon, a variant and a size.

| Variant | Class | Fill / lip | Use for |
|---|---|---|---|
| Primary | `fa-btn` | `sun` / `sun-deep` | The one action that moves the player forward: Take off, Resume, Try again. One per screen. |
| Secondary | `fa-btn fa-btn--secondary` | `sky-500` / `sky-700` | A second path: Choose airport. |
| Ghost | `fa-btn fa-btn--ghost` | `surface` / `surface-sunken` | Everything else: Settings, Restart, Main menu. |
| Danger | `fa-btn fa-btn--danger` | `cherry` / `cherry-deep` | Leaving a running game. Never for Retry. |

Sizes: `fa-btn--sm` (`button-sm`, 40px), default (`button-md`, 52px), `fa-btn--lg` (`button-lg`, 64px, main call to action only). `fa-btn--block` fills the dialog width.

**Construction.** Fill, `line-3` `outline` border, `radius-md`, an inset 5px lip in the deep colour and `shadow-lip` below. Label in `button-label`, colour `on-accent` (ghost: `ink`).

**States.** Pressed: move down 2px and swap `shadow-lip` for `shadow-lip-sm`, in `dur-press` with `ease-snap`. Focus: the two-tone ring, 3px `focus` then 3px `outline`. Disabled: `opacity-disabled`, no press movement.

**Do** start labels with a verb. **Don't** put two primary buttons side by side, and don't use a button narrower than `touch-min`.

**Compose.** A `Box` with `Modifier.offset` driven by press state; draw the lip as a second rounded rect `5.dp` below the face in `outline`.
