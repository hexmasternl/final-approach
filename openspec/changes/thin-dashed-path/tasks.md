## 1. Design system first

- [x] 1.1 Set `path-stroke` 3px, `path-casing` 7px, `path-dash` 10px, `path-gap` 8px, `path-dot` 12px and `dur-march` 1800ms in `docs/design-system/tokens.json`, and update `components/FlightPath.md` and the Flight Path section of `showcase.html` (and `showcase.pdf`); verify the doc, showcase and tokens show the same numbers
- [x] 1.2 Regenerate the theme (`FaDimens`, `FaMotion`) with the design-system tool; verify the generated values and that the build passes

## 2. Drawing

- [x] 2.1 Change the end dot outline in `ui/game/FlightPathDrawing.kt` from `line3` to `line2` and check no stroke, dash or timing literal is in the file; verify with a search and a Compose preview of a selected path
- [x] 2.2 Check the reduced-motion path still does not march and the cleared-to-land flash still reads on the thin line; verify in a preview
- [x] 2.3 Run `./gradlew :shared:allTests` and `./gradlew :androidApp:assembleDebug`; verify both pass
- [ ] 2.4 Human check: draw paths on a phone and confirm they are thin and fine, and that the march is half as fast
