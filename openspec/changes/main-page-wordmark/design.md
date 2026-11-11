# Design

## Context

- `MainPage` (`ui/menu/MainPage.kt`) draws `MenuBackdrop`, then, inside a `safeDrawing` inset box, a 280dp button column centred on the screen and the version label bottom-right. The quit dialog clears the semantics of that box.
- The design system defines the wordmark only in CSS: `.fa-wordmark` and `.fa-wordmark--sm` in `bundle.css` (stroke 12px / 8px, drop 7px / 4px, `rotate(-3deg)`, second line `padding-left: .35em`, small size 36px/34px). `FaTypography.displayXl` (72/68, weight 700, -0.01em) exists and is unused. There is no Compose text stroke in CSS terms; Compose 1.12 has `TextStyle.drawStyle` (`Fill` or `Stroke`).
- `FaDimens` and `FaTypography` are generated from `tokens.json` by `tools/gen-kotlin.cjs` and must not be edited by hand. Component values that are not tokens already live in a private `<Component>Defaults` object next to the component (`FaButtonDefaults` in `FaButton.kt`).
- The splash page shows the name on the towed Banner, not as a wordmark, so there is nothing there to replace.

## Goals / Non-Goals

**Goals:** a `FaWordmark` that matches `.fa-wordmark` at both sizes and reports its true, rotated size to layout; a main page layout that picks stacked or beside from measured sizes, not from orientation.

**Non-Goals:** animating the wordmark; using it on other screens; new tokens.

## Decisions

### 1. Wordmark values live in `FaWordmarkDefaults`, not in `tokens.json`

`FaWordmark.kt` holds a private `FaWordmarkDefaults` with the values from `bundle.css`: `stroke = 12.dp`, `drop = 7.dp`, `strokeSmall = 8.dp`, `dropSmall = 4.dp`, `tiltDegrees = -3f`, `secondLineIndent = 0.35.em`, the small font size and line height (36sp / 34sp, applied as `displayXl.copy(...)`), and `capHeightRatio` for the clear space (decision 5). Colours come from `FaTheme.colors` (`fuselage`, `sun`, `outline`), the font from `FaTheme.type.displayXl`.

Rejected: adding `wordmark-*` tokens to `tokens.json` and the generator. The design system treats these as component values in `bundle.css`, like the button's deep band, and the repo already has a pattern for those. If the author wants them as tokens, add them to `tokens.json` first and regenerate; the component code barely changes.

### 2. Draw the same text three times with `drawStyle`

The text is one `AnnotatedString` with two paragraphs, "Final" and "Approach"; the second paragraph has `ParagraphStyle(textIndent = TextIndent(firstLine = secondLineIndent))`. Three `BasicText` layers in one `Box`, identical in style and layout, drawn back to front:

1. **Drop:** all text in `outline`, drawn as fill and as `Stroke(stroke, join = StrokeJoin.Round)`, offset down by `drop`. Both are needed: a stroke alone is hollow and the sky would show through the dropped counters.
2. **Stroke:** all text in `outline`, `Stroke(stroke, join = StrokeJoin.Round)`.
3. **Fill:** "Final" in `fuselage`, "Approach" in `sun` (span styles), `Fill`.

A Compose `Stroke` is centred on the glyph edge, like `-webkit-text-stroke`, so the widths carry over unchanged. The layers sit inside padding of half the stroke on every side plus the drop at the bottom, so nothing is clipped.

Rejected: drawing with `TextMeasurer` in a `Canvas`. It needs hand-written sizing and baseline handling for the same result. Rejected: one `BasicText` per line with a start padding. The em indent then has to be converted by hand and the lines laid out three times.

### 3. Report the rotated size to layout

The tilt is applied with `graphicsLayer { rotationZ = tiltDegrees }`, which does not change the measured size. A `Modifier.layout` on the wordmark measures the unrotated content and reports the bounding box of the rotated rectangle (`w·cos θ + h·sin θ` by `w·sin θ + h·cos θ`), placing the content centred in it. Neighbours then keep their distance from the visible corners.

Rejected: rotation without a layout adjustment. The corners of "Approach" would cross the 48dp gap and could leave the safe area in landscape.

### 4. One semantics node

The wordmark root uses `clearAndSetSemantics { contentDescription = "Final Approach"; heading() }`. The string is a constant next to the component, as `MainPageText` does for the page. The layers carry no semantics of their own. Because the wordmark sits inside the box whose semantics are cleared while the quit dialog is open, it is hidden then too.

Rejected: leaving semantics on the text layers. The name would be read three times, split over two lines.

### 5. Choose the layout by measuring, in a pure function

`MainPage` replaces the centred button column with a `SubcomposeLayout` (in `ui/menu`) that measures the regular wordmark and the menu column against the safe-area constraints, then calls a pure function:

```kotlin
enum class MainMenuArrangement { Stacked, Beside }
fun chooseMainMenuArrangement(availableHeight: Dp, stackedHeight: Dp): MainMenuArrangement
```

- `stackedHeight` = clear space + wordmark height + `space7` + menu height.
- `availableHeight` = safe-area height minus, at top and at bottom, the version label's height plus `space4`. Reserving it on both sides keeps the group centred and clear of the label.
- `Stacked` when `stackedHeight <= availableHeight`: the regular wordmark centred above the menu, `space7` between them, the group centred.
- `Beside` otherwise: the small wordmark (subcomposed only in this case) to the left of the menu, `space7` between them, both vertically centred, the group centred.
- Clear space is one cap height: `capHeightRatio` × the size's font size. DynaPuff's cap height is about 0.7 em; check the ratio against the font file when implementing.

`chooseMainMenuArrangement` gets a `kotlin.test` test in `commonTest`. The composable stays thin.

Rejected: switching on orientation (`width > height`). A landscape tablet has room for the regular wordmark, and a short portrait window (split screen) would clip. Rejected: a fixed dp threshold. It breaks as soon as the user's font scale grows the sp-sized wordmark and buttons.

### 6. The wordmark is part of the UI layer

The wordmark goes in the same inset box as the menu, so it draws in front of the backdrop: passing aircraft and clouds go behind it, as they do behind the buttons.

### Design system

- The wordmark component and the tokens it needs already exist (`fuselage`, `sun`, `outline`, `display-xl`, `space-7`). Nothing is missing.
- `MainMenu.md` gets the layout rule before the code depends on it: wordmark centred above the menu, `space-7` between; when the stacked layout does not fit the height inside the system bars, the small wordmark to the left of the menu, `space-7` between. Remove "The page carries no wordmark".
- The *Main Menu* reference in `showcase.html` (960 by 560) shows the stacked layout: an `<h1 class="fa-wordmark">Final<span>Approach</span></h1>` above the button grid, `space-7` between, the group re-centred. The showcase is edited by hand; `showcase.pdf` goes stale until someone re-exports it.

## Assumptions

- The wordmark and the menu are centred as one group, so the buttons move below the screen centre. The alternative, keeping the buttons where they are and adding the wordmark above, would put the group off-centre.
- `space-7` between wordmark and menu stands in for the clear space on that side, as the issue asks, although it is slightly less than DynaPuff's cap height at 72sp.
- At compact heights the small wordmark goes beside the menu, not stacked above it: stacked small barely fits 360dp minus system bars once clear space is counted, and beside leaves room.
- The wordmark is static: no entrance or idle animation, so reduced motion needs nothing extra.
- The wordmark draws in front of passing aircraft and clouds.

## Risks / Trade-offs

- [Very small or very large-font screens may fit neither layout] → Common phones (360dp wide portrait, 360dp tall landscape) at default font scale are the target. The `Beside` arrangement is the fallback; if it overflows, accept it and file a follow-up rather than shrinking type below the design system.
- [Rotated text renders slightly differently from the browser] → Compare the screenshots with the showcase in the design-critic review.
- [Showcase PDF drifts from the HTML] → Note it in the pull request; re-exporting is a human step.
