# Tasks

## 1. Design system

- [x] 1.1 Add `finger.svg` to `docs/design-system/assets/icons/` in the icon set's stroke style and add it to the showcase icon grid. Verify it renders next to `tap` at `icon-sm`, `icon-md` and `icon-lg` with the same stroke weight and round joins.
- [x] 1.2 Update `docs/design-system/components/CoachMark.md` with the gesture, explanation (`fa-coach--explain`, Got it) and tracking (`fa-coach--track`) variants and the new dismissal rule, as design decision 10; record "Got it" as the one exception to the verb rule. Verify the file no longer says "never with a button" without the gesture qualifier.
- [x] 1.3 Add tokens to `tokens.json` and the showcase tables: `coach-max-width` (300px), `coach-explain-max-width` (360px), `highlight-margin` (`space-3`), `dur-demo-drag` (1800ms). Verify every new token appears in both and the existing `.fa-coach` CSS uses `coach-max-width` instead of the literal.
- [x] 1.4 Describe the zone highlight and the drag demo (with its reduced-motion still) in `docs/design-system/world.md` and `motion-audio.md`, and add one line for the Skip tutorial button to `components/HudBar.md`. Verify the layers named match decision 10.
- [x] 1.5 Add showcase examples (`showcase.html`, `bundle.css`/`bundle.js` as needed): explanation coach mark with Got it, tracking coach mark beside an aircraft sprite, highlight around a runway (with arrow) and a helipad, drag demo animated and still. Verify by opening the showcase in a browser, with and without `prefers-reduced-motion`. Human task: regenerate `showcase.pdf`. **Autocoder: examples added; not opened in a browser and showcase.pdf not regenerated (human).**

## 2. Domain and storage

- [x] 2.1 Add `domain/Tutorial.kt` (`TutorialScript`, `ScriptedEntry`), `LevelDefaults.TUTORIAL_WELL_IN_VIEW_INSET`, replace `hints`/`TutorialHint` in `LevelConfig` with `tutorial: TutorialScript? = null`, and update the defaults test so no tutorial is the default. Verify `./gradlew :shared:allTests` passes and `grep -rn TutorialHint app/src` finds nothing.
- [x] 2.2 Give Meadow Field a script (decision 5) and extend `LevelConfigValidator`: tutorial entries lie outside the viewport. Add tests: only Meadow Field has a script; each airplane entry has an aligned westbound approach into runway 27's zone that fits in the viewport; the helicopter line reaches the helipad. Verify the tests pass.
- [x] 2.3 Add `platform/TutorialStore.kt` with `InMemoryTutorialStore`, `AppHost.tutorials`, and `engine/TutorialProgress.kt` that marks completion on `GameEvent.TutorialCompleted`, with a test that it writes once. Implement the store in `AndroidAppHost` with a private `tutorials` preferences file. Verify `allTests` and `:androidApp:assembleDebug` pass and `commonMain` has no `android.` import.

## 3. Engine flow

- [x] 3.1 Add `engine/TutorialDirector.kt` and `GameState.tutorial` with the step and phase model of decision 3; wire it into `GameEngine` (constructor flag, `acknowledgeTutorial()`, holds and touch gating per decision 4, no spawner while it runs). Tests: steps run in order 1 to 6; nothing moves during an explanation; input is ignored while the light prop is not well in view and accepted in its Drawing phase; a non-landing path keeps the Drawing phase; a landing path starts the turboprop step. Verify the tests pass.
- [x] 3.2 Add the rest of the flow: turboprop explanation on well in view, helipad step only after the turboprop is cleared, helicopter explanation, completion on its Got it with `GameEvent.TutorialCompleted`. Tests for each, including that completion does not wait for landings. Verify the tests pass.
- [x] 3.3 Add failure restart from a step snapshot (decision 7) with `retryReason`. Tests: a turboprop departure and a collision during the helicopter step restore aircraft and score to the step start, re-spawn the step's aircraft, set the reason, and never set `GameOver`. Verify the tests pass.
- [x] 3.4 Add `skipTutorial()` and the hand-over (decision 8): tutorial cleared, aircraft kept, spawner started with level time counted from hand-over, normal failures from then on, tutorial landings scored. Tests for skip on step 1, skip with aircraft in the air, first spawn after the first spawn delay in phase 1, and a collision after hand-over ending the level. Verify the tests pass.
- [x] 3.5 Make `restart()` start the tutorial from step 1 when it was active, and keep pause/resume independent of holds. Tests: Restart during step 4 returns to step 1; pause then resume during an explanation stays held. Verify the tests pass.
- [x] 3.6 Describe the tutorial in `docs/game-design.md` (when it runs, the six steps, holds, failure restart, skip, completion and hand-over) and name `Tutorial.kt`, `TutorialDirector`, `TutorialStore` in `docs/architecture.md` in place of `TutorialHint`. Verify `grep -rn "TutorialHint\|hints" docs/` finds no stale mention.

## 4. UI

- [ ] 4.1 Port the design-system additions to the theme: `FaDimens` (`coachMaxWidth`, `coachExplainMaxWidth`, `highlightMargin`), `FaMotion.durDemoDragMs`, `FaIcon.Finger`. Verify `assembleDebug` passes and the values match `tokens.json`.
- [ ] 4.2 Add `ui/components/FaCoachMark.kt` (tail up or down, step line, title, body, optional Got it with a 48dp touch target) with previews for each variant. Verify the previews against the showcase.
- [ ] 4.3 Add `ui/game/TutorialOverlay.kt` and wire it into `GameScreen`: engine constructed with the completion check, coach mark placement and tracking, highlight and demo drawn at their layers, reduced-motion still, Skip button in the HUD row, `TutorialText` copy (decision 12), `TutorialProgress` fed with events. Verify `assembleDebug` passes and a `GamePreviews` preview shows each step.
- [ ] 4.4 Human task: on a device with a fresh install, play the tutorial through, fail a step on purpose, quit and return mid-tutorial, skip on a second fresh install, and repeat once with reduced motion on. Check every scenario in `specs/level-tutorial` and `specs/tutorial-overlays`, and that after completion a restart of the app goes straight to the normal level.

