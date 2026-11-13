The moulded button for every text action in menus and dialogs.

**You provide:** a label of one to three words, optionally one leading icon, a variant and a size.

| Variant | Class | Face / highlight / lip / lip edge | Use for |
|---|---|---|---|
| Primary | `fa-btn` | `sun-500` / `sun-200` / `sun-700` / `sun-900` | The one action that moves the player forward: Take off, Resume, Try again. One per screen. |
| Secondary | `fa-btn fa-btn--secondary` | `sky-500` / `sky-300` / `sky-700` / `sky-900` | A second path: Choose airport. |
| Ghost | `fa-btn fa-btn--ghost` | `paper-0` / `paper-0` / `paper-300` / `paper-500` | Everything else: Settings, Restart, Main menu. |
| Danger | `fa-btn fa-btn--danger` | `cherry-500` / `cherry-200` / `cherry-700` / `cherry-900` | Leaving a running game. Never for Retry. |

| Size | Class | Height | Lip | Radius | Label |
|---|---|---|---|---|---|
| Small | `fa-btn--sm` | `button-sm` | `lip-sm` | `radius-control-sm` | `button-label-sm`, icon `icon-sm` |
| Medium | (default) | `button-md` | `lip-md` | `radius-control` | `button-label` |
| Large | `fa-btn--lg` | `button-lg` | `lip-lg` | `radius-control-lg` | `button-label-lg`, icon `icon-md` |

The large button is for the main call to action on a menu only. `fa-btn--block` fills the width of a dialog or a column.

**Construction.** A moulded control (U1). Back to front: a flat `contact` shadow showing `contact-offset` below it; one `outline` at `line-ui`; the lip in the variant's 700 tone with a `lip-edge` line of its 900 tone along the bottom; the face in the base tone with a `highlight` line along its top edge. The height token measures the button from outline to outline, lip included. The face's bottom corners are the radius minus the lip (U5). A coloured face carries one small shine, `shine-width` by `shine-height` at `opacity-shine`; the ghost button has none. The label is `on-accent`, on the ghost button `ink`.

**States.**
- Pressed (`.is-pressed`): the face drops onto its lip until `lip-pressed` is left, in `dur-press` with `ease-snap`. The bottom of the button stays where it is.
- Focus (`.is-focus`): the two-tone ring outside the outline, a `line-ui` band of `focus` then a `line-ui` band of `outline`.
- Disabled: flat, not faded (U7). `paper-200` face on a `lip-pressed` sliver of `paper-300`, outline and label in `ink-3`, no shine, no contact shadow, full opacity. It does not move.

**Do** start labels with a verb. **Do** keep a full `touch-min` touch area around a small button. **Don't** put two primary buttons side by side, don't draw the lip in `outline`, and don't use a button narrower than `touch-min`.

**Compose.** `FaButton` in `ui/components/FaButton.kt`, with `FaButtonVariant` (Primary, Secondary, Ghost, Danger) and `FaButtonSize` (Small, Medium, Large). It is built on `FaMoulded`, which draws the surface with `drawMoulded` and handles press, focus, reduced motion and the disabled state.
