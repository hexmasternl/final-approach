# Design

## Context

`FaDialog` (`ui/components/FaDialog.kt`) draws every modal: the game ended dialog (`ui/game/GameEndedDialog.kt`), the options panel (`OptionsPanel` in `ui/game/Hud.kt`, titled "Paused") and the main page's quit dialog. It always uses the portrait Dialog from the design system (`.fa-dialog` in `bundle.css`): `space-6` padding, `display-lg` title (48sp), `body-lg` body, stats, then full-width buttons stacked `space-4` apart, at most 400dp wide (a private literal today), inside `space-5` outer padding and the `safeDrawing` insets. When that is too tall it falls back to `verticalScroll`. The game ended dialog also puts a full-width anchored adaptive banner (`host.ads.Banner`, about 50dp in landscape) under the card, through the `below` slot. Orientation is not locked and system bars are shown (`enableEdgeToEdge`).

Height budget on the smallest target, 640×360dp landscape: about 312dp inside the system bars, about 258dp after the banner. The portrait game ended dialog with five types (seven wells) is well over 500dp.

The repository has no Compose UI test or preview tooling; layout is checked by unit tests on pure functions and by running the app.

## Goals / Non-Goals

**Goals:**
- A compact Dialog variant, defined in the design system first, that the theme and `FaDialog` implement from tokens.
- `FaDialog` chooses the layout itself, so callers do not branch on screen shape.
- Both target dialogs fit the target phones in landscape, banner included.

**Non-Goals:**
- Changing portrait, dialog copy, the set of wells or buttons, or the banner.
- Making the portrait game ended dialog fit a 360×640 phone without scrolling (see Open Questions).

## Decisions

**1. The layout follows the available space, not the device orientation.** `FaDialog` measures the area inside the `safeDrawing` insets (before the banner) and uses the compact layout when that area is wider than it is tall and its height is below a new size token `dialog-compact-breakpoint`. The rule is a pure function in `ui/components` (`dialogLayoutFor(width, height): FaDialogLayout`) so it can be unit-tested. Rejected: reading orientation through `AppHost`, which adds a platform call for something Compose already measures, and gets split screen and tall landscape tablets wrong. Rejected: laying out the portrait dialog first and switching when it overflows (`SubcomposeLayout`), which costs a double measure and flips layout when the banner loads.

**2. Compact structure.** Same card (`surface`, `line-4` `outline`, `radius-xl`, `shadow-lip-lg` plus soft lip), `space-4` padding, `space-2` outer padding, at most `dialog-compact-max-width` wide, title in `title-lg`, body in `body` (Inconsolata 500) `ink-muted`.
- Without stats (options panel, quit dialog): title, body, then the actions in one row of equal-width buttons, primary at the start.
- With stats (game ended): two panes side by side. The start pane holds title, body and the actions stacked; the end pane holds the stats grid. This gives the stats the full card height, which is the scarce dimension.
Rejected: one column with only the buttons in a row; with seven wells it still needs about 250dp of card height on 640×360, more than the roughly 200dp left inside the card.

**3. Compact stats wells.** A design-system sub-variant of the stat well (`fa-stat--compact` in CSS): `icon-md` aircraft sprite at the start, `label` over the value in `title-sm`, `space-2` padding. The number of columns is fixed in the design system mockup by fitting seven wells into the end pane at 640×360; the starting point is three columns. If that does not fit, the levers in order are: wider card up to the available width, fewer columns with a smaller well, `button-sm` buttons (still 48dp touch height). Not allowed: buttons under 48dp, Inconsolata under 500, dropping a well, shortening copy.

**4. Design system first, then tokens to code.** New size tokens in `tokens.json`: `dialog-max-width` (400px, replacing the literal in `FaDialogDefaults`), `dialog-compact-max-width` and `dialog-compact-breakpoint`. `FaDimens` is regenerated with `node docs/design-system/tools/gen-kotlin.cjs`, never edited by hand. `.fa-dialog--compact` and `.fa-stat--compact` go into `bundle.css` and the inline copy in `showcase.html`; the Dialog section shows both examples in a 640×360 frame; `components/Dialog.md` says when the variant applies. Spacing and type reuse existing tokens. Drop distance and start scale stay as they are.

**5. `FaDialog` owns action placement.** The `actions` slot becomes a list of `FaDialogAction(text, onClick, variant, icon)`; `FaDialog` renders them stacked full width (portrait, compact with stats) or in a weighted row (compact without stats). The current layout is exposed to the `content` slot through a `LocalFaDialogLayout` composition local, so `GameEndedDialog` picks the grid columns and well style without measuring. Rejected: keeping a `ColumnScope` slot and letting each caller branch, which spreads the layout rule over every dialog.

**6. Scrolling stays as a last resort.** `verticalScroll` remains on the card for both layouts. On the target phones the content fits, so there is nothing to scroll; on smaller screens or large font scale content stays reachable. Rejected: removing it, which would cut off buttons at large font scale.

**7. Banner unchanged.** The banner keeps its place under the card at full width; the compact budget counts it. All values come from `FaTheme` and `FaDimens`; reduced motion keeps using `FaTheme.reduceMotion`.

## Assumptions

- `dialog-compact-breakpoint` is 600dp of available height and `dialog-compact-max-width` is 720dp; the mockup may tune both.
- The banner shown in the test is a standard-height test banner; with no ad loaded the dialog only gets more room.
- "Common phones" means 360×640dp and 412×915dp at the default font size, with gesture or three-button navigation.
- The quit dialog on the main page inherits the compact layout; it has no separate acceptance criteria.

## Risks / Trade-offs

- [Seven wells, two stacked buttons and a banner may not fit 640×360 even with the levers] → The design-system mockup at 640×360 is the first task; if it cannot fit, stop and raise it on the issue before building the Compose side.
- [Layout switches when the window is resized, for example entering split screen] → The rule is a pure function of the measured size; the card simply re-lays out, with no state lost.
- [No UI test tooling] → The selection rule is unit-tested; fit is checked on emulators at both sizes with screenshots, reviewed by the `design-critic` agent.

## Open Questions

- The portrait game ended dialog with five types likely scrolls on a 360×640 phone with the banner. The issue keeps portrait as it is, so this change does not address it; the owner can open a follow-up.
