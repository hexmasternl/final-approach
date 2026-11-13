# Design

## Context

- `GameEngine` owns `StateFlow<GameState>`, advances in `update(delta)`, and has `pause()`/`resume()`; while `paused`, `update` does nothing and input calls are ignored (`inputBlocked`). The spawner is created in the constructor and in `restart`, and is driven by `levelTime`.
- `LevelConfig.hints: List<TutorialHint>` (`TutorialHint(atSeconds, text)`) exists but nothing reads it. A time-based hint does not fit a step-based tutorial that waits for the player.
- `HighScoreStore` is the storage precedent: an interface in `platform/`, `InMemoryHighScoreStore` for tests, a private `SharedPreferences` file in `AndroidAppHost`; `engine/BestScore.kt` is the small helper `GameScreen` uses to read and write it.
- Design system: `CoachMark.md` describes a two-step gesture tutorial dismissed "never with a button"; the showcase has `.fa-coach` (outline fill, `on-outline` text, `radius-md`, 10px tail, max width 300px, bob loop on `ease-sway`). There is no finger icon (`tap` shows a tap with ripples), no zone highlight and no drag demo. The HUD is one row in the top band; Chip is not interactive, Button has a small ghost variant (`fa-btn--ghost fa-btn--sm`, 40px with a 48px touch target).
- `one-way-runways` (#61) makes Meadow Field's runway one-way 27 (landing heading west); the helipad is at (1430, 630), east of the runway on its centre line extension.

## Goals / Non-Goals

**Goals:**
- The whole tutorial flow (step order, holds, touch gating, scripted spawns, failure restart, skip, completion) is engine logic with `kotlin.test` tests; the UI only renders `GameState.tutorial` and forwards Got it and Skip.
- Design-system additions land first; Compose uses only theme tokens.
- No `android.*` in `commonMain`; completion is stored behind `AppHost`.

**Non-Goals:**
- A general scripting language for tutorials on other levels.
- Replay from Settings, sound, localisation.

## Decisions

### 1. One change, design system first
Keep one change with the design-system tasks as group 1. The design-system additions exist only for this tutorial, and the tutorial cannot be checked without them; two changes would add a dependency and a second issue link for no review benefit. *Alternative:* a separate design-system change, as the first triage suggested. Rejected: one reference block per issue can only name one change, and the pieces are small once scoped (one coach mark variant, one tracking rule, one highlight, one demo, one icon).

### 2. The tutorial lives inside the engine
New `engine/TutorialDirector.kt`: a plain state machine the `GameEngine` owns and calls from `update` and from the input and new tutorial calls. Its state is exposed as `GameState.tutorial: TutorialState?` (null when no tutorial runs), so there is still a single state owner. `GameEngine` gains `acknowledgeTutorial()` (Got it) and `skipTutorial()`. The engine is constructed with `tutorialActive: Boolean`, which the screen computes as `level.tutorial != null && !host.tutorials.isCompleted(level.id)`.
*Alternative:* a separate controller in `ui/game` that drives the engine through `pause`, spawn and input calls. Rejected: game rules would sit next to composables, and two objects would own overlapping state.

### 3. Step model
`TutorialStep { Goal, Runway, LightProp, Turboprop, Helipad, Helicopter }` and a phase per step:

| Step | Phases, in order | Ends on |
|---|---|---|
| Goal | Explaining | Got it |
| Runway | Explaining (highlight runway) | Got it |
| LightProp | Tracking (touch off) -> Drawing (held, touch on, demo) | Light prop cleared to land |
| Turboprop | Tracking (touch on) -> Explaining -> AwaitingClearance (running) | Turboprop cleared to land |
| Helipad | Explaining (highlight helipad) | Got it |
| Helicopter | Tracking (touch on) -> Explaining | Got it: tutorial completed |

`TutorialState` carries `step`, `phase`, `stepNumber` (1 to 6), `trackedAircraftId`, `holding`, `touchEnabled`, `retryReason: GameOverReason?` and `highlight` (runway id or helipad id, or none). "Cleared to land" is read from the aircraft's existing cleared status after `endPath`/`extendPath`. Each scripted spawn happens on entering a Tracking phase.

### 4. Tutorial hold is separate from the player's pause
`update` does nothing while `paused || tutorial?.holding == true`; input is blocked while `paused || gameOver || tutorial?.touchEnabled == false`. In the LightProp Drawing phase the simulation holds but touch is on, so the player draws while nothing moves; drag points still go through the normal path and landing rules.
*Alternative:* reuse `pause()`. Rejected: pause blocks input, and Continue in the options panel would release a held explanation.

### 5. Scripted traffic as level data
Replace `hints: List<TutorialHint>` with `tutorial: TutorialScript? = null` in `LevelConfig`, in new `domain/Tutorial.kt`: `TutorialScript(lightProp: ScriptedEntry, turboprop: ScriptedEntry, helicopter: ScriptedEntry, wellInViewInset: Double = LevelDefaults.TUTORIAL_WELL_IN_VIEW_INSET)` with `ScriptedEntry(position: Vec2, heading: Double)`; types are fixed per field. Remove `TutorialHint`. Meadow Field sets a script; no other level does. Entries start outside the viewport by the spawn margin. Suggested starting points, to be tuned by test: light prop at the top edge right of the runway heading south-west, turboprop at the bottom edge right of the runway heading north-west, helicopter at the left edge heading east above the runway. The validator checks entries lie outside the viewport; a test checks each airplane's straight line reaches a point east of runway 27 from which an aligned westbound approach fits inside the viewport, and the helicopter's straight line passes within reach of the helipad.
*Alternative:* keep `TutorialHint` and add the script beside it. Rejected: an unused, misleading type.

### 6. "Well in view"
An aircraft is well in view when its centre is at least `wellInViewInset` world units inside every viewport edge. Default `TUTORIAL_WELL_IN_VIEW_INSET = 160.0 * LevelDefaults.WORLD_ZOOM` (240 world units at the current zoom) in `LevelDefaults`. *Alternative:* a fixed time after spawn. Rejected: depends on speed and entry.

### 7. Failure restarts the step from a snapshot
When a step starts, the director keeps a snapshot of the engine state it needs to restore (aircraft with paths, score, landings, per-type counts, next aircraft id). When `failureOf` reports a collision or a departure while the tutorial runs, the engine does not set `GameOver`; it restores the snapshot, restarts the step from its first phase (re-spawning the step's aircraft) and sets `retryReason`, which clears when the step moves on. *Alternative:* clear all aircraft and replay from step 1. Rejected: the author chose "restart the current step".

### 8. Hand-over to the normal level
On completion or skip the director reports `completed`, `GameState.tutorial` becomes null, holds and touch gating end, and the engine creates the spawner then. The spawner's level time is `levelTime - handoverTime`, so the first spawn delay and phase 1 start at hand-over; `levelTime` itself keeps running for animation. The engine emits `GameEvent.TutorialCompleted` once (both for Got it and Skip). Tutorial landings score through the normal `Scoring`. *Alternative:* restart the level fresh after the tutorial. Rejected: the author chose "the level continues as normal".

### 9. Completion storage behind `AppHost`
New `platform/TutorialStore.kt`: `interface TutorialStore { fun isCompleted(tutorialId: String): Boolean; fun markCompleted(tutorialId: String) }` and `InMemoryTutorialStore`. `AppHost` gains `val tutorials: TutorialStore`. The tutorial id is the level id (`meadow-field`). `AndroidAppHost` implements it with a private preferences file `tutorials`, one boolean key per id. A small `engine/TutorialProgress.kt` (like `BestScore`) marks completion when it sees `GameEvent.TutorialCompleted`; `GameScreen` feeds it the events. *Alternative:* store it in `PlayerSettings` from `settings-page`. Rejected: that change may land later, and completion is progress, not a setting; a later "Replay tutorial" can clear this store.

### 10. Design-system additions (group 1)
In `CoachMark.md`, `showcase.html` (and `bundle.css`), `tokens.json`, `world.md`, `motion-audio.md`:
- **Explanation coach mark** (`fa-coach--explain`): same bubble, step line in `label`, title in `title-sm`, body in `body`, then a small primary Button "Got it" (`fa-btn--sm`, 40px drawn, 48px touch). Max width token `coach-explain-max-width` (360px; the existing 300px becomes `coach-max-width`). No bob. The guideline changes to: a gesture coach mark is dismissed when the gesture succeeds and never has a button; an explanation coach mark is dismissed only with Got it.
- **Tracking coach mark** (`fa-coach--track`): tail points at the aircraft; it sits `space-3` beyond the aircraft's pick radius (`touch-aircraft` / 2), above by default, below when it would cross the top band, clamped `space-4` inside the screen. No bob while tracking.
- **Zone highlight**: a dashed outline `space-3` (`highlight-margin`) outside the runway rectangle or helipad circle, `line-3` `sun` over a `line-5` `outline` casing, dashes from `path-dash`/`path-gap`, marching on `dur-march` (static with reduced motion); a chunky arrow in the same style outside the landing end, pointing along the landing direction. Layer `z-zone`. It never fills.
- **Drag demo**: a dotted path in `on-outline` with `outline` casing (`path-dot`) from the aircraft to the landing end, and a `finger` icon at `icon-lg` that travels along it in `dur-demo-drag` (new, 1800ms) with `ease-out-soft`, then pauses `dur-pop` and repeats. Reduced motion: the dotted path with the finger still at its end. Path layer for the dots, `z-fx` for the finger.
- **`finger` icon**: a pointing finger in the icon set's stroke style, added to `assets/icons/finger.svg` and the icon grid. *Alternative:* reuse `tap`. Rejected: its ripples mean a tap, not a drag.
- **Skip**: a ghost small Button "Skip tutorial" in the HUD row, left of the cog. The HUD Bar doc gets one line for it.
- "Got it" breaks the "buttons start with a verb" copy rule; it is the author's explicit choice and the guideline records it as the one exception.

### 11. Compose side
- Theme: new `FaDimens` (`coachMaxWidth`, `coachExplainMaxWidth`, `highlightMargin`), `FaMotion.durDemoDragMs`, `FaIcon.Finger`. No literal in composables.
- `ui/components/FaCoachMark.kt`: bubble with tail direction, step line, title, body, optional Got it.
- `ui/game/TutorialOverlay.kt`: renders `state.tutorial`: places the coach mark (tracking position from `WorldViewport.offsetOf`), draws the highlight and the demo in the Canvas at their layers, shows Skip, calls `engine.acknowledgeTutorial()` / `engine.skipTutorial()`. Copy in a private `TutorialText` object keyed by step and phase.
- The demo's start and end are computed in the engine state (aircraft position and the runway's landing-end point), so the overlay only converts to pixels.

### 12. Copy (draft, editable)
- Goal: "Welcome to the tower" / "Guide every aircraft to a safe landing. Keep them apart: if two touch, the level ends."
- Runway: "This is runway 27" / "Airplanes land here. Come in from the right, lined up with the runway. This runway takes landings from one end only; others take them from both ends."
- Light prop tracking: "Light prop inbound" / "It needs to land."
- Light prop drawing: "Draw its path" / "Drag from the plane to the runway. End lined up with the arrow."
- Turboprop tracking: "Turboprop inbound" / "It needs to land too."
- Turboprop explaining: "Faster traffic" / "This plane needs to land as well. There are four types of airplane, each with its own speed. This one is faster than the light prop, so it catches up with slower traffic."
- Helipad: "The helipad" / "Helicopters land here, and only here."
- Helicopter tracking: "Helicopter inbound" / "It needs to land."
- Helicopter explaining: "Land it on the helipad" / "Draw its path to the pad. No need to line up."
- Retry lines: "Two aircraft got too close. Let's try that again." / "An aircraft left the area. Let's try that again."
- Buttons: "Got it", "Skip tutorial". Step line: "Step 3 of 6".

## Risks / Trade-offs

- [Restoring a snapshot can look like a jump] -> the retry line explains it; the step's aircraft re-enter from their entries.
- [Highlighting the helipad seems to contradict "zones are not drawn"] -> the highlight is a temporary teaching outline around the pad and runway shapes, not the zone; the landing-zones requirement is unchanged outside the tutorial.
- [Entries on the 27 side pass near the helipad] -> the wrong zone type does nothing; the entry test keeps approaches clear.
- [Several active changes edit `Levels.kt` and `GameScreen.kt`] -> rebase on whichever lands first.

## Assumptions

- The second airplane is the turboprop.
- The runway step points at runway 27 and describes two-way runways in words only.
- Skipping counts as completed (the posted default; the author confirmed skip but not this part).
- Tutorial landings score normally, because the level simply continues (not explicitly answered).
- The light prop step ends only when its path clears it to land; a path that does not land leaves the step waiting.
- The helipad step waits until the turboprop is cleared to land, mirroring how the turboprop step waits for the light prop, so the player handles one aircraft at a time.
- There is no separate "done" message; dismissing the helicopter explanation completes the tutorial.
- Quitting or restarting during the tutorial does not complete it; it shows again next time.
- Failure restarts the step from the state at the step's start; the retry line names what happened.
- Well in view means at least 240 world units inside every edge.
- The difficulty curve and first spawn delay start at hand-over, not at level start.
- Skip needs no confirmation.
