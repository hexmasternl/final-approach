# Tasks

## 1. Card content

- [x] 1.1 Add a pure `levelTags(level)` and `LevelCardState`, with `kotlin.test` tests for "1 runway", "2 runways", "1 helipad", none. Verify: `./gradlew :shared:allTests` passes.
- [x] 1.2 Add any new level-card dimens to `tokens.json` and regenerate the theme (`check-tokens.cjs`, `gen-kotlin.cjs`). Verify: the check passes and no literal remains in the composable.

## 2. Card UI

- [x] 2.1 Build `LevelCard` (art, `lineUi` divider, name, tag chips, best-score chip) and use it in `LevelSelectPage` in place of `LevelTile`; read the best score from the host. Verify: the three cards show; a level with a best score shows the chip; tapping still starts the level.
- [x] 2.2 Add the locked state (washed art, lock badge, plain chip, flat, no press, disabled semantics), driven by `LevelCardState.locked`. Verify: a locked preview does not react to a tap and is reported as disabled.
- [ ] 2.3 Add Day and Night previews: normal, with best score, locked. Verify: `./gradlew :androidApp:assembleDebug` passes and the previews render.

## 3. Docs and review

- [ ] 3.1 Update `docs/design-system/components/LevelCard.md` (Compose note) and, if the showcase changes, rebuild it with `build-showcase.cjs`. Verify: the note no longer says "Not yet the full card".
- [ ] 3.2 Human check: run the design-critic review on the previews against `LevelCard.md`; it names no violation. Tell #83 that the locked state is in.
