# Design

## Context

Best scores are stored per level through `HighScoreStore` on `AppHost` (`platform/`), wrapped by `engine/BestScore`; `GameScreen` updates it. Levels have ids `meadow-field`, `island-airport`, `savanna-dawn` (`domain/Levels.kt`). The level selection page lists them as tiles.

## Decisions

1. **New store `LevelProgressStore` on `AppHost`.** `totals(levelId): LevelTotals(landings, points)`, `add(levelId, landings, points)`, `isUnlocked(levelId)`, `markUnlocked(levelId)`; plus `InMemoryLevelProgressStore` for tests and `PreviewHost`, and a host implementation in `androidApp` next to the high score store. Rejected: reusing `HighScoreStore` (a different meaning, and it would bloat its interface) and keeping totals in the engine (not persistent).
2. **Rules in `engine/LevelUnlocks`, pure and tested.** `LevelUnlockRules` holds the config: per locked level, the required level, the metric (landings or points) and the threshold; defaults 1,000 landings (strictly more than) on Meadow field and 30,000 points (at least) on Island airport. Rejected: literals in the selection screen.
3. **Counting happens once per run** in an engine-level `RunTotals` helper that `GameScreen` calls when the status becomes `GameOver` and when Restart or Quit leaves a run; a flag per run prevents double counting. Landings are `GameState.totalLanded`, points are `GameState.score`. Tutorial landings count, as they score.
4. **Unlocked stays unlocked** by persisting a flag when the rule holds (checked after counting and when the selection page opens). Rejected: recomputing from totals only, which breaks if thresholds are raised later.
5. **UI.** The locked tile uses the disabled flat face (`paper` tones, `ink3` label, no lip, full opacity, U7), the `lock` icon from `docs/design-system/assets/icons/` and a flat progress readout. The tile keeps a content description naming the condition. Copy: "Land 1,000 aircraft on Meadow field to open Island airport." and "Earn 30,000 points on Island airport to open Savanna dawn." The level card's locked state belongs to the follow-up of #77; if it is not in the design system when this is built, add it to the showcase and tokens first, do not improvise.
6. **Numbers** use the same thousands grouping helper as the HUD.

## Assumptions (the author can overrule)

- Existing players: a level counts as unlocked when the device has a best score for it or for a later level. Their totals start at zero.
- No reset of progress in this change.
- Quitting or restarting a run counts it, as a run that ends; a killed app does not.
- Tapping a locked tile does nothing (no toast or dialog); the tile text explains.
- "More than 1,000" is a strict comparison, "30,000 or more" inclusive, as the issue words them.
- Savanna dawn requires only the points threshold, since earning those points implies Island airport was open.
- Totals are per device, not synced.
