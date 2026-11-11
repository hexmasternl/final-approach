# Tasks

## 1. Design system

- [ ] 1.1 Add the size tokens `dialog-max-width` (400px), `dialog-compact-max-width` and `dialog-compact-breakpoint` to `docs/design-system/tokens.json` with usage notes; verify the file still parses (`node -e "JSON.parse(require('fs').readFileSync('docs/design-system/tokens.json','utf8'))"`)
- [ ] 1.2 Add `.fa-dialog--compact` (two-pane with stats, button row without) and `.fa-stat--compact` to `bundle.css` and to the inline CSS in `showcase.html`, using only token variables; verify by `grep` that the new rules contain no raw colour, radius or line values
- [ ] 1.3 Add the paused and game over examples (game over with five type wells, Total landed and Points) in the compact variant to the showcase Dialog section, inside a 640×360 frame with a 50px banner placeholder under the card; verify in a browser that both fit the frame without overflow, Day and Night. If the game over example cannot fit with the levers in design.md, stop and raise it on issue #31
- [ ] 1.4 Describe the variant and when it applies in `docs/design-system/components/Dialog.md`; verify it names the tokens from 1.1
- [ ] 1.5 Regenerate the theme with `node docs/design-system/tools/gen-kotlin.cjs`; verify `FaDimens` gains the three values and `git diff` shows no other unexpected generated changes

## 2. FaDialog

- [ ] 2.1 Add `FaDialogLayout` and the pure `dialogLayoutFor(width, height)` in `ui/components`, with `commonTest` tests for portrait, short landscape (640×360, 915×412 inside bars) and tall landscape (above the breakpoint); verify `./gradlew :shared:allTests` passes
- [ ] 2.2 Replace the `actions` slot with a list of `FaDialogAction` and render them stacked or in a weighted row per design.md; move the 400dp literal to `FaDimens.dialogMaxWidth`; verify `./gradlew :androidApp:assembleDebug` builds and the portrait dialogs look unchanged
- [ ] 2.3 Build the compact layout in `FaDialog` from theme tokens (padding, `titleLg`, `body`, two panes) and provide `LocalFaDialogLayout` to `content`; keep `verticalScroll` and the reduced-motion path; verify by `grep` that `FaDialog.kt` has no `dp`, colour or duration literals
- [ ] 2.4 Move `MainPage`'s quit dialog to `FaDialogAction`; verify it opens and both buttons work in portrait and landscape

## 3. Dialogs

- [ ] 3.1 Move `OptionsPanel` to `FaDialogAction` (Continue primary, Restart secondary with restart icon, Quit danger); verify on an emulator at 640×360 and 915×412 landscape that all three buttons show without scrolling and each still works
- [ ] 3.2 Move `GameEndedDialog` to `FaDialogAction` and, in the compact layout, use the compact wells and the column count from the showcase; verify on an emulator at 640×360 and 915×412 landscape, in a level with all five types and a test banner loaded, that every well and both buttons show without scrolling

## 4. Checks

- [ ] 4.1 Verify on emulators that in portrait (360×640 and 412×915) both dialogs match the design-system Dialog, and that at the largest font size in landscape the buttons are reachable by scrolling
- [ ] 4.2 Take screenshots of both dialogs in landscape and portrait, Day and Night, and review them with the `design-critic` agent; resolve its findings
- [ ] 4.3 Human: check both dialogs on a real phone in landscape, with reduced motion on and off
