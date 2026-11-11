# Design

## Context

The engine, input and game screen do not exist yet. This change adds the minimum of each so that the first level is playable. It builds on `level-config` (#6: `LevelConfig`, `Spawner`, `Levels.meadowField`), `aircraft-types` (#8: `EffectiveAircraftSpecs`) and `landing-zones` (#7: `LandingRules`, path locking, rollout). The rules in `CLAUDE.md` apply: no game rules in composables, `commonMain` uses only `kotlin.*`, units are world units, and tunables live in config.

## Decisions

**One `GameEngine` owns the state.** It holds a mutable working copy and publishes an immutable `GameState` snapshot to a `MutableStateFlow` after each update or input call. The UI reads the flow; it never mutates state. Rejected: Compose state inside the engine, which would tie the engine to Compose.

**Path following steers, it does not snap.** Each update, an aircraft turns towards the next path point at most `turnRate * dt` and moves `speed * dt` along its heading. Points within the reach radius (config) are consumed, so the path gets shorter from the aircraft's end. With no points left, it flies straight on. Rejected: moving along the polyline exactly, which ignores the turn rate that makes types differ.

**Input is engine API, gestures are UI.** `GameEngine.select(worldPoint)`, `beginPath(id)`, `extendPath(id, worldPoint)` and `endPath(id)` hold the rules (nearest aircraft within the pick radius, minimum point spacing, landing cut-off). The composable only converts pointer pixels to world units with the viewport transform and calls them. The pick radius is `touchAircraft` (72dp) converted to world units, so it stays at least 72dp on any screen.

**Viewport fit.** The world is scaled uniformly to fit inside the screen and centred. The lawn fills the whole screen, so the bands outside the world are lawn too. `WorldViewport(worldSize, screenSize)` does the conversion both ways and is unit tested.

**Scenery is level data.** `LevelConfig.scenery` is a list of `SceneryItem(kind, position, size, rotation)` with kinds Taxiway, Apron, Terminal, Tower, Hangar, Windsock and Tree. It has no effect on play. Validation rejects scenery that overlaps a landing zone, so decoration never covers a runway or helipad.

**Drawing.** Every drawn shape has a flat fill and a `line3` outline with round joins, in this order: field (lawn, taxiway, apron, runway, helipad, buildings, windsock, trees), paths (casing and dash, end dot), ground shadows, aircraft, selection ring. Colours come from `FaColors`, strokes and sizes from `FaDimens`. Sprite sizes are `dp` and do not scale with the world, as the design system says.

**Landscape through `AppHost`.** `AppHost.lockLandscape(locked: Boolean)`: the game screen locks on entry and unlocks on leaving, using a `DisposableEffect`. Android sets `requestedOrientation` to sensor landscape, then back to unspecified. Rejected: locking in the manifest, which would affect the splash and main pages too.

**Leaving before #9.** An aircraft that has been inside the viewport and is then beyond it by the leave margin is removed. #9 replaces removal with game over.

**Seeds.** The game screen draws a fresh seed with `Random.nextLong()` when the level has none, so the engine and spawner stay deterministic in tests.

## Assumptions

Each was posted on the issue as an assumption.

- Steering (tap-select and drag-draw) is part of this change (option A of the blocking question).
- Alignment: the direction rule from #7 applies, since #7 is implemented first.
- Back returns to the main page and restores the orientation.
- No collisions, lives, misses or score display here; those are #9 and #10.
- All five types appear, following the Meadow Field phases from #6.
- An aircraft that flies out of view is removed until #9 makes it a failure.
- Scenery drawings are added to the design system as one Scenery component.

## Risks

- One branch carries five changes. Mitigation: every change keeps its own commits and tasks.
- Turn-rate steering can orbit a point it cannot reach. Mitigation: the reach radius grows with the turning circle (`speed / turnRate`), so unreachable points are consumed.
