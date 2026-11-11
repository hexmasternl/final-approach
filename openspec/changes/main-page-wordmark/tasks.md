# Tasks

## 1. Design system first

- [ ] 1.1 Update `docs/design-system/components/MainMenu.md`: remove "The page carries no wordmark", add a **Wordmark** bullet (regular Wordmark centred above the menu, `space-7` between, the two centred as one group) and the compact-height rule (when the stacked layout does not fit the height inside the system bars, the `--sm` Wordmark to the left of the menu, `space-7` between, both vertically centred). Verify by reading it against the `main-page` spec delta.
- [ ] 1.2 Add the wordmark to the *Main Menu* reference in `docs/design-system/showcase.html` (`#c-MainMenu`): `<h1 class="fa-wordmark">Final<span>Approach</span></h1>` above the button grid, `space-7` between, the group centred in the 960 by 560 stage. Verify by opening the showcase in a browser: nothing overlaps and the wordmark is not clipped.
- [ ] 1.3 Add one line to `docs/design-system/components/Wordmark.md` naming the Compose component (`FaWordmark` in `ui/components`, regular and small). Verify the file still matches `bundle.css`.

## 2. FaWordmark component

- [ ] 2.1 Create `ui/components/FaWordmark.kt` with `FaWordmarkSize { Regular, Small }`, the private `FaWordmarkDefaults` (design decision 1) and the two-paragraph `AnnotatedString` with the 0.35em indent on "Approach". Verify it builds with `./gradlew :androidApp:assembleDebug` and that a search of the file finds no `Color(` and no `dp`/`sp`/`em` literal outside `FaWordmarkDefaults`.
- [ ] 2.2 Draw the three layers (drop as fill plus stroke, stroke, fill) per design decision 2, with padding for half the stroke and the drop. Verify on a device or preview at both sizes: `fuselage` "Final", `sun` "Approach", outline behind the fill, hard drop below, no clipped strokes.
- [ ] 2.3 Add the tilt with `graphicsLayer` and the rotated-bounds `Modifier.layout` (design decision 3). Verify by placing the wordmark in a `Column` with a bordered box directly below: the tilted corners do not cross into the box.
- [ ] 2.4 Set one semantics node (`contentDescription = "Final Approach"`, `heading()`) and expose the clear space (cap height from `capHeightRatio`) for layouts. Check `capHeightRatio` against the DynaPuff font metrics. Verify with TalkBack: "Final Approach, heading", read once.

## 3. Main page layout

- [ ] 3.1 Add `MainMenuArrangement` and `chooseMainMenuArrangement` in `ui/menu`, with a `kotlin.test` test in `commonTest` covering: fits exactly, fits with room, one dp short, zero height. Verify with `./gradlew :shared:allTests`.
- [ ] 3.2 Replace the centred button column in `MainPage` with the `SubcomposeLayout` from design decision 5: measure the regular wordmark and the menu, reserve the version label plus `space4` at top and bottom, choose the arrangement, place `Stacked` (regular above, `space7`) or `Beside` (small left, `space7`), group centred. Keep the wordmark inside the inset box whose semantics the quit dialog clears. Verify the app builds and `./gradlew :shared:allTests` passes.
- [ ] 3.3 Update the `MainPage` KDoc and the main page description in `docs/architecture.md` (Navigation / main page paragraph) to mention the wordmark and the two arrangements. Verify both read correctly against the spec.

## 4. Check on devices

- [ ] 4.1 (Human) Run on a phone or emulator at 360 by 640dp in portrait and in landscape (about 360dp tall with system bars). Verify: portrait shows the regular wordmark 48dp above Take off; landscape shows the small wordmark left of the menu; in both, wordmark, buttons and version label are fully visible and do not overlap; aircraft and clouds pass behind the wordmark; TalkBack reads "Final Approach" heading, then Take off, then Settings, and does not reach the title while the quit dialog is open. Take a portrait and a landscape screenshot.
- [ ] 4.2 Run the `design-critic` agent on the diff with the two screenshots from 4.1 and fix every violation it reports. Verify a second run reports none.
