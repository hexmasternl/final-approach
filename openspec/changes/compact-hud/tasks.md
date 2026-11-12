# Tasks

## 1. Theme and formatting

- [x] 1.1 Add `scoreCompact` (DynaPuff 700, 24sp/24sp, tabular figures) to `FaTypography` next to `score`, with a KDoc naming `.fa-score--compact`; verify it builds and no literal size appears outside `ui/theme`
- [x] 1.2 Add `formatScore(value: Int): String` grouping thousands with U+2009; verify with `commonTest` cases 0, 450, 1000, 12450 and 1234567

## 2. Score chips

- [x] 2.1 Change `hudChipFace()` horizontal padding to `space4` and replace `FaStatChip` with `FaScoreCounter(label, value, compact, icon, popOnRise)` (label above value, upper-cased `label` style in `inkMuted`, value in `score` or `scoreCompact` via `formatScore`); verify in a preview that Score and the compact Best (star in `sunDeep`) look like the Score Counter examples in the showcase
- [x] 2.2 Keep the pop on the Score value only; verify in a preview or on a device that it still scales to `SCORE_POP_SCALE` on a rise and does not animate with reduced motion

## 3. HUD layout

- [x] 3.1 Rewrite `Hud(best, score, onOptions, modifier)` as one row (`spacedBy(space3)`, top-aligned) at top start plus the cog wheel at top end, add `hudBottom(topInset)` used by the layout, and update the call in `GameScreen`; verify the app builds and a preview shows Best, Score and the cog wheel in one row
- [x] 3.2 Delete `FaSpawnCounter` and `HudConfig.COUNTER_SPRITE_FRACTION`; verify with a search that neither name remains and `./gradlew :shared:allTests` passes
- [x] 3.3 Raise `LevelDefaults.HUD_BAND` to 160 and add a `commonTest` that, for every level, maps `hudBottom` into world units with `WorldViewport` for 640 x 360 dp (inset 0) and 360 x 640 dp (inset 48 dp) and asserts it is at most `HUD_BAND`; verify it and `LevelLayoutsTest` pass
- [x] 3.4 Update the HUD bullet in `docs/game-design.md` (Best and Score top left, cog wheel top right, no per-type counters; landed per type stays in the game-ended dialog); verify the doc no longer mentions a counter per aircraft type in the HUD

## 4. Review

- [ ] 4.1 Run the `design-critic` agent on the HUD previews against the Hud Bar and Score Counter, and fix what it finds; verify its report has no open findings on size, spacing or type
- [ ] 4.2 Human check: play every level on a small phone in portrait and landscape and confirm the HUD is one row, covers no runway or helipad, and the cog wheel is easy to hit
