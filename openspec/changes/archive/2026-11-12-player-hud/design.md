# Design

## Context

The game engine, level configuration and scoring are not built yet; this change assumes them (issues #5, #6, #8). The architecture in `CLAUDE.md` applies: game rules stay out of composables, `commonMain` has no Android APIs, platform concerns sit behind an interface, and no literals where a token or config value belongs.

The design system has HudBar (score and streak left, pause right, at most five items), ScoreCounter (pop to 1.15 and back in `dur-pop` with `ease-bounce`), IconButton (56px `hud-chip`, `settings` icon exists), Dialog (modal, simulation frozen, one to three actions, primary first) and the five aircraft sprites.

## Goals / Non-Goals

**Goals:**

- Score, best score and spawn counts as plain engine state the HUD only reads.
- Pause, resume and restart as engine operations that tests can drive.
- A HUD and options panel built from existing design-system parts, plus one new chip.

**Non-Goals:**

- Lives, streak, next-aircraft hint, bonus popups.
- Game-over handling (issue #12) and a quit confirmation.

## Decisions

**Spawn counts and score live in `GameState`.** The engine increments `spawnedByType: Map<AircraftType, Int>` when the spawner creates an aircraft and adds the type's effective points to `score` on a landing. The HUD reads both from `StateFlow<GameState>`. Rejected: counting in the composable, which would put game rules in the UI and break on recomposition.

**Shown types come from the level config.** The HUD lists the types in the active `LevelConfig`'s spawn configuration (the union over all difficulty phases, in roster order: light prop, turboprop, jet, jumbo, helicopter), not types seen so far. So a type that spawns later in the build-up is shown from the start at 0. Rejected: showing a type only after its first spawn, because the issue says types configured for the level.

**Counter means spawned, not landed.** The issue says "how many aircrafts are spawned"; the counter increments at spawn and landings do not touch it.

**Pause is an engine flag.** `GameEngine.pause()` and `resume()` make `update(deltaSeconds)` a no-op while paused, so nothing moves, spawns or scores. The frame loop keeps running or stops, either way the delta of the paused period is never applied. `restart()` rebuilds state from the `LevelConfig` with a fresh seed (per `level-config`) and a score of 0. Rejected: tearing down and recreating the engine, which would drop subscribers.

**Best score is a platform concern.** `platform/HighScoreStore` has `best(levelId): Int` and `save(levelId, score)`, implemented in `androidApp` (for example on SharedPreferences or DataStore; the host chooses). The HUD shows `max(stored best, current score)` and the store is written the moment the current score exceeds the stored best, so Restart, Quit and a killed app all keep it. Rejected: saving only when the level ends, which loses the score if the app is killed and depends on the game-over flow from #12.

**Score pop is a pure trigger plus a Compose animation.** The pop fires when `score` increases (a value-change effect), scaling the score text to a `HudConfig.scorePopScale` of 1.15 and back over `dur-pop` with `ease-bounce`. The count-up from ScoreCounter is not used; the issue only asks for the pop. With reduced motion (the `AppHost` setting from the splash page change) the value changes with no animation.

**Layout follows HudBar.** One row, `space-4` from the edges. Left group: best chip (ScoreCounter compact, label Best) and score chip (ScoreCounter, label Score). Right: the cog wheel as IconButton with the `settings` icon, content description "Options", in the top-right corner. Spawn counters sit as a row of small chips below or beside the left group, one per type: aircraft sprite thumbnail plus count, `hud-chip` height or less. Rejected: the `pause` icon, because the issue names a cog wheel.

**The design system gets a spawn-counter chip first.** HudBar caps at five items and has no per-type counter. Add a SpawnCounter component (sprite icon and number, built on Chip styling) to the design system and amend the HudBar guideline to allow a counter row of up to five chips, before using it in code, per `CLAUDE.md`. Type is shown by sprite silhouette, so colour never carries meaning alone.

**The options panel is the Dialog component.** Headline "Paused", body "The tower is holding traffic.", on `scrim`. Actions: Continue (primary, first), Restart (secondary), Quit (danger, labelled). Quit has no confirmation because the issue does not ask for one. Opening it calls `pause()`; Continue calls `resume()`; Restart calls `restart()` then closes; Quit ends the level (the engine stays paused) and navigates to the main page.

**Tunable values.** `HudConfig` holds the pop scale; durations and easing come from the `dur-pop` and `ease-bounce` tokens. Chip sizes and spacing come from `FaDimens`.

## Assumptions

- The issue's "At this time, the options panel." is read as "At this time, the options panel only has these three actions".
- "Spawned" means created by the spawner, so aircraft still on screen are counted.
- The best score shown is live: it follows the current score once the current score beats it.
- Best score is stored per level id and only on this device.
- Restart and Quit need no confirmation.
- Opening the cog wheel while an aircraft is selected keeps the selection and paths; Continue restores them.
- The system back button is not specified by the issue and is left out; add it with a later change if wanted.

## Risks / Trade-offs

- [Up to five counters plus best, score and cog crowd the top edge on small phones] → Counters are compact and wrap into a second row under the left group; verify on a small phone in landscape.
- [Dependencies not built yet: engine, level config, points per type] → Tasks that need them are ordered after them; the HUD is written against `GameState` so it can be tested with a fake state.
- [Writing the store on every score gain] → Only written when the score exceeds the stored best, at most once per landing.

## Open Questions

- Whether the HUD should later show a "New best" cue when the best is beaten.
