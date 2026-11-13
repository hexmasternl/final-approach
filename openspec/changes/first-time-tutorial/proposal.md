# Proposal

GitHub issue: #62

## Why

A first-time player is dropped into Meadow Field with no idea what the goal is, where airplanes and helicopters may land, or how to draw a path. A short, scripted tutorial on level 1 teaches the goal, the landing zones and the drag gesture with aircraft the tutorial sends in itself, and never shows again once it is done.

## What Changes

- **Design system first.** Extend the CoachMark guideline and showcase with: an *explanation* coach mark that is dismissed with a "Got it" button (replacing "never with a button" for steps that have no gesture), a *tracking* coach mark whose tail follows a moving aircraft, a *zone highlight* around a runway or helipad that never covers its landing zone, and a *drag demo* (a finger marker drawing a dotted path) with a still variant for reduced motion. Add the missing `finger` icon and the tokens these need, then port them to the Compose theme.
- **Tutorial flow in the engine.** On Meadow Field, while the tutorial has not been completed, the engine runs a scripted tutorial instead of the spawner, in six coach-marked steps: goal, runway 27, light prop (tracked in, touch off, then a held "draw its path" step with the drag demo), turboprop (tracked in, then explained), helipad, helicopter (tracked in, then explained). The simulation holds during each explanation.
- **Failure restarts the step.** A collision or an aircraft leaving the area during the tutorial does not end the level: the state returns to how it was when the current step began and the step runs again, with a calm line saying what happened.
- **Skip.** A "Skip tutorial" button at the top edge ends the tutorial at once; skipping counts as completed.
- **Completion and hand-over.** Dismissing the helicopter explanation completes the tutorial (no waiting for landings). Completion is stored on the device behind `AppHost`, and the level carries on as a normal Meadow Field run with the tutorial aircraft still in the air; the difficulty curve starts from that moment.
- **Level config.** Replace the unused `hints: List<TutorialHint>` with an optional tutorial script on `LevelConfig`, set only for Meadow Field.
- **Docs.** `docs/game-design.md` describes the tutorial; `docs/architecture.md` names the new types.

## Capabilities

### New Capabilities

- `level-tutorial`: when the tutorial runs, its steps and their order, scripted traffic, holds and touch gating, failure handling, skip, completion and hand-over to the normal level.
- `tutorial-overlays`: how the tutorial looks: explanation and tracking coach marks, the zone highlight, the drag demo and its reduced-motion still, the skip button, and their place in the design system.

### Modified Capabilities

- `first-level`: Take off opens Meadow Field with the tutorial for a player who has not completed it; otherwise straight into the normal level.
- `level-config`: the optional "tutorial hints" field becomes an optional tutorial script whose default is no tutorial.

## Out of scope

- A "Replay tutorial" option in Settings (a separate issue; the `settings-page` change for #66 already leaves it out).
- Tutorials on other levels, or a tutorial for wind, emergencies or scoring bonuses.
- Waiting for tutorial aircraft to land before moving on or completing.
- Sound for the tutorial.
- Changes to landing rules, collision rules or the spawner's fair-entry logic.

## Depends on

- `one-way-runways` (#61): Meadow Field's runway becomes one-way runway 27; the runway step points at its east (27) end. If this change lands first, the runway step still points at the 27 end of the two-way runway and the copy stays correct.
- The existing pause (`GameEngine.pause`/`resume`), `HighScoreStore` as the storage pattern, and the design-system CoachMark, Button and HudBar components.
- `larger-level-worlds`, `world-zoom-retune`, `detailed-scenery` and `one-way-runways` also edit `Levels.kt`; whichever lands later rebases. No requirement overlaps.

## Impact

- Design system: `docs/design-system/components/CoachMark.md`, `world.md`, `motion-audio.md`, `tokens.json`, `showcase.html` (and `showcase.pdf`), `assets/icons/finger.svg`.
- Shared code: `domain/LevelConfig.kt`, `domain/Levels.kt`, new `domain/Tutorial.kt`, `engine/GameEngine.kt`, new `engine/TutorialDirector.kt`, `platform/AppHost.kt`, new `platform/TutorialStore.kt`, `ui/theme/*`, `ui/sprites/FaIcons.kt`, `ui/game/GameScreen.kt`, new `ui/game/TutorialOverlay.kt`, `ui/components` (coach mark).
- Android host: `AndroidAppHost` implements `TutorialStore`.
- Tests in `commonTest`: tutorial flow, failure restart, skip, completion, level-config defaults.
- Docs: `docs/game-design.md`, `docs/architecture.md`.
