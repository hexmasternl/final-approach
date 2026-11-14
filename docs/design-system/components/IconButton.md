A square moulded button that carries one icon and no text: pause in the HUD, close and back in menus.

**You provide:** an icon name from the Icons group and an accessible label (`aria-label`, or the description in Compose).

| Variant | Class | Size | Use for |
|---|---|---|---|
| Default | `fa-iconbtn` | `button-md` square, icon at `icon-md` | Restart, home and other actions on a menu. |
| Small | `fa-iconbtn fa-iconbtn--sm` | `touch-min` square | Close and back in panels. |
| HUD | `fa-iconbtn fa-iconbtn--hud` | As high as a flight strip (`strip-height`), `lip-sm`, `radius-strip` | Pause, the only control in the HUD. It stands on `contact-field`. |
| Sun | `fa-iconbtn fa-iconbtn--sun` | As default | The single positive action on a paused screen. |

**Construction.** The same moulded build as Button (U1): contact shadow, `line-ui` `outline`, lip, face with a `highlight`. The default tone is paper, like a ghost button: `paper-0` face, `paper-300` lip with a `paper-500` edge, `ink` icon, `lip-md`, `radius-control`. The sun variant takes the primary button's tones with an `on-accent` icon. An icon button has no shine.

**States.** Pressed, focus and disabled work exactly as on Button. Disabled is flat and paper, with the icon in `ink-3` (U7).

In the HUD the button is drawn `strip-height` high and keeps a full `touch-min` touch area around it.

**Do** keep Pause in the top-right corner of the HUD, `space-4` from both edges. **Don't** use an icon button for anything destructive: quitting gets a labelled danger Button. **Don't** give any other HUD element a lip (U2).

**Compose.** `FaIconButton` in `ui/components/FaButton.kt`: a `button-md` square, or with `compact` a `strip-height` square for the HUD in the middle of a full touch target. Built on `FaMoulded` with `toneGhost`.
