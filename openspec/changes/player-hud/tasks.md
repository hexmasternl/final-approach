# Tasks

## 1. Engine state

- [ ] 1.1 Add `spawnedByType` and `score` to `GameState`, incremented on spawn and on landing (type's effective points), and verify with `commonTest` that a spawn raises only its type's count, a landing raises only the score by the type's points, and a landing leaves counts unchanged
- [ ] 1.2 Add `pause()`, `resume()` and `restart()` to `GameEngine` and verify with `commonTest` that `update` changes nothing while paused, that resume continues from the same state, and that restart gives score 0, counts 0, no aircraft and an unchanged stored best
- [ ] 1.3 Expose the aircraft types a level spawns from `LevelConfig` in roster order and verify with a test that a level with only light props and helicopters yields exactly those two

## 2. Best score storage

- [ ] 2.1 Declare `HighScoreStore` (`best(levelId)`, `save(levelId, score)`) in `platform` with an in-memory fake, and verify `commonMain` has no `android.*` or `java.*` import
- [ ] 2.2 Add the best-score logic (shown best is `max(stored, current)`, saved when current exceeds stored) and verify with `commonTest` that it starts at 0, follows a higher score, is saved once per gain, and is separate per level
- [ ] 2.3 Implement `HighScoreStore` in `androidApp` and verify on a device or emulator that a best score survives closing and reopening the app

## 3. Design system

- [ ] 3.1 Add the SpawnCounter chip to the design system and amend HudBar to allow a counter row of up to five chips; verify the showcase page shows both
- [ ] 3.2 Add `HudConfig` (score pop scale) and the SpawnCounter dimensions to the theme and verify no HUD literal for size, duration or scale appears in a composable

## 4. HUD

- [ ] 4.1 Build the HUD (best chip, score chip, per-type counters, cog wheel in the top-right at `space-4`) over a fake `GameState` and verify in a preview that only configured types show and nothing covers the field
- [ ] 4.2 Add the score pop and verify in a preview or on a device that the value grows to 1.15 and returns in `dur-pop` on each rise and does not animate with reduced motion
- [ ] 4.3 Wire the HUD to the engine and `HighScoreStore` and verify by running the app that score, best and counters update as aircraft spawn and land

## 5. Options panel

- [ ] 5.1 Build the options panel from the Dialog component with Continue, Restart and Quit, and verify in a preview that the primary action is first and Quit is a danger button
- [ ] 5.2 Wire the cog wheel to `pause()` and the panel; verify by running the app that aircraft freeze while the panel is open and Continue resumes exactly where it stopped
- [ ] 5.3 Wire Restart to `restart()` and Quit to the main page, and verify by running the app that Restart resets score and counters but keeps the best, and Quit shows the main page with a beaten best still stored

## 6. Integration check

- [ ] 6.1 Human check on a small and a large phone in landscape: the HUD stays readable, the counters fit, the pop feels small and pleasant, and the cog wheel is easy to hit
