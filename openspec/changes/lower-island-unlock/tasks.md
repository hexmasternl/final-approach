## 1. Rule

- [x] 1.1 In `engine/LevelUnlocks.kt`, set `ISLAND_LANDINGS = 500`, make the Island rule `strict = false`, and update its KDoc to "this many or more"
- [x] 1.2 Update the comment in `ui/game/LevelUnlockCopy.kt` that mentions "More than 1,000"

## 2. Tests

- [x] 2.1 `LevelUnlocksTest`: Island is locked at 499 and open at 500 landings; open at 300 + 200 summed over runs
- [x] 2.2 `LevelUnlocksTest`: with 750 stored landings on Meadow field and no unlock flag, Island counts as open
- [x] 2.3 `LevelUnlockCopyTest`: the locked card reads "412 of 500 landings" and "Land 500 aircraft on Meadow Field to open Island."; progress never shows more than 500
- [ ] 2.4 `RunTotalsTest`: settling after 500 Meadow field landings opens Island
- [ ] 2.5 Check that Savanna dawn tests still pass unchanged, and run `./gradlew :shared:allTests`

## 3. Docs

- [ ] 3.1 `docs/game-design.md`, Level unlocks: Island opens once the player has landed 500 or more aircraft in total on Meadow Field; update the card copy examples to 500
