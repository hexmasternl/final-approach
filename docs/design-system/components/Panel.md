A paper container for grouped content in menus, with an optional header band.

**You provide:** the content, and optionally a header with a title.

**Construction.** Class `fa-panel`. A moulded paper surface that does not press: a `paper-100` face with a `paper-0` `highlight` along its top, a `lip-panel` lip of `paper-400` with a `lip-edge` line of `paper-500`, one `line-ui` `outline`, `radius-panel`, on a flat `contact` shadow showing `contact-offset` below it. The lip is paper, never navy (U1).

- **Title** (`fa-panel__title`): `title-md` in `ink`. Body text is `body`; secondary text is `ink-muted`.
- **Header band** (`fa-panel__head`): a `paper-200` band across the top with a dashed rule of `paper-500` under it (`rule-dash`, `rule-gap`). It holds a title and, at the end, a chip or a small icon button.
- **Rows** (`fa-rows`): a list in `row`, `space-3` apart. An icon in a row sits on an icon tile (`fa-icontile`): an `icon-tile` square, `radius-strip`, `sky-tint` with a `sky-band` edge and a `sky-900` icon.

Every line inside a panel is a paper step (U4). Panels stay light in Night and Dawn.

**States.** None: a panel is a surface, not a control. Its lip does not move.

**Do** separate groups inside a panel with `space-4`, or with a `line-detail` keyline in `paper-300`. **Don't** nest panels, and don't draw a divider in `outline`.

**Compose.** `FaPanel` in `ui/components/FaPanel.kt`, with an optional `header` slot. It uses `Modifier.faMouldedSurface` with `tonePaper` and clips its content with `faFaceShape`.
