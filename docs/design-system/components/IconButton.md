A square button that carries one icon and no text, used in the HUD and for close and back.

**You provide:** an icon name from the Icons group and an accessible label (`aria-label`, or `contentDescription` in Compose).

- `fa-iconbtn`: `hud-chip` square (56px), `surface` fill, `ink` icon at `icon-md`.
- `fa-iconbtn fa-iconbtn--sm`: `touch-min` square (48px) for close and back in panels.
- `fa-iconbtn fa-iconbtn--sun`: `sun` fill for the single positive action on a paused screen.

Same border, lip, press and focus behaviour as Button.

**Do** keep Pause in the top-right corner of the HUD, `space-4` from both edges. **Don't** use an icon button for anything destructive: quitting gets a labelled danger Button.
