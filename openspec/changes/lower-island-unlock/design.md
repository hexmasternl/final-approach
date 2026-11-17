# Design

## Context

The unlock rules live in `engine/LevelUnlocks.kt`. `LevelUnlockRules.Default` holds one `UnlockRule` per locked level; the Island rule is `UnlockRule("island-airport", "meadow-field", Landings, ISLAND_LANDINGS = 1_000, strict = true)`, so it opens at 1,001. The locked card copy (`ui/game/LevelUnlockCopy.kt`) derives its target from the rule (`threshold + 1` when strict), so it follows any rule change.

`LevelUnlocks.isUnlocked` judges a level from the stored totals every time it is asked, and `LevelSelectPage` asks on every build. A stored flag only keeps a level open; its absence never keeps a level locked.

## Decisions

- **Threshold 500, not strict.** Set `ISLAND_LANDINGS = 500` and `strict = false`. This reads as the issue asks ("500 landings"), matches the Savanna rule's form, and makes the copy target equal the constant. Alternative: `499` with `strict = true`; rejected, it hides the number players see.
- **No migration.** Because unlocks are recomputed from totals, players with 500 to 1,000 stored landings see Island open on the next visit to the level select page. `settle` marks it unlocked after their next run. A test pins this down.
- **Copy follows the rule.** No change to `LevelUnlockCopy.kt` beyond its comment, which mentions "More than 1,000".

## Risks

- Tests that rely on 1,000 or 1,001 landings fail until updated. They are listed in tasks.
