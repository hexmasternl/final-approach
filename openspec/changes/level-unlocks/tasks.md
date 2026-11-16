# Tasks

## 1. Rules and storage

- [x] 1.1 Add `LevelUnlockRules` config and `LevelUnlocks` (pure) in `engine/`, with `kotlin.test` tests at 1,000/1,001 landings, 29,999/30,000 points, summing over runs and the existing-player rule. Verify: `./gradlew :shared:allTests` passes.
- [ ] 1.2 Add `LevelProgressStore`, `InMemoryLevelProgressStore`, `AppHost.levelProgress`, the Android implementation and the `FakeAppHost` and `PreviewHost` members. Verify: `./gradlew :androidApp:assembleDebug` and `:shared:allTests` pass.
- [ ] 1.3 Add `RunTotals`, counting once per run, and call it from `GameScreen` at game over, Restart and Quit. Verify: tests for lost run, quit and counted once.

## 2. Level selection

- [ ] 2.1 Make the selection page read the unlock state and show the locked tile (lock icon, condition, progress, disabled flat style, no start), with theme tokens only. If the design system lacks the level card locked state, add it to the showcase and tokens first. Verify: tapping a locked tile starts nothing; the tile shows the copy of the spec.
- [ ] 2.2 Update `docs/game-design.md` with the unlock rules. Verify: the text matches the specs.

## 3. Check on a device

- [ ] 3.1 Human check: a new install shows two locked tiles with progress; an upgraded install with a Savanna dawn best score shows all open; totals persist after closing the app.
