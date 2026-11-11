# Proposal

GitHub issue: #32

## Why

Once the splash page is gone, nothing on the home screen says which game this is. The main page should carry the game's name as the design system's Wordmark, which today exists only in HTML and CSS and is not built in Compose.

## What Changes

- New reusable `FaWordmark` composable in `ui/components`: "Final" over "Approach" in DynaPuff, outlined, with a hard drop and a -3° tilt, in a regular (`display-xl`, 72) and a small (36) size, exactly as `.fa-wordmark` and `.fa-wordmark--sm` in `bundle.css`. All values from the theme or named component defaults. Screen readers hear it once, as the heading "Final Approach".
- The main page shows the wordmark above the Take off and Settings buttons, centred, `space-7` from the menu.
- When the available height cannot hold the wordmark stacked above the menu (a landscape phone), the main page switches to the small wordmark to the left of the menu. Nothing overlaps or clips in either layout.
- Design system: `MainMenu.md` drops "no wordmark", describes where the wordmark sits and the compact-height rule; the *Main Menu* reference in `showcase.html` shows the wordmark. `docs/architecture.md` and the `MainPage` KDoc mention it.

## Capabilities

### New Capabilities
- `wordmark`: how the game's name is drawn wherever a logo would go: lines, colours, outline, drop, tilt, sizes and how it is announced.

### Modified Capabilities
- `main-page`: the page now shows the wordmark above the menu, with a layout rule for compact heights, and the design system reference describes it.

## Out of scope

- Using the wordmark on the splash page. The splash page shows the name on the towed Banner, which stays as it is.
- The pause screen and credits, which the small variant is meant for. The variant is built here; using it there is later work.
- Any entrance or idle animation of the wordmark. It is static.
- Logo artwork. The wordmark stays typeset, as the brand book says.
- New tokens in `tokens.json`; see the design for where the wordmark's own values live.

## Dependencies

- None. Builds on the archived `main-page` change and the existing theme (`FaTypography.displayXl`, `FaColors.fuselage`, `sun`, `outline`, `FaDimens.space7`).

## Impact

- Code: new `ui/components/FaWordmark.kt`; `ui/menu/MainPage.kt` (layout); a small pure layout-choice function with a unit test in `commonTest`.
- Docs: `docs/design-system/components/MainMenu.md`, `docs/design-system/components/Wordmark.md` (Compose note), `docs/design-system/showcase.html` (*Main Menu*), `docs/architecture.md`.
