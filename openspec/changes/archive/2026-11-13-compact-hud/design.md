# Design

## Context

The HUD came in with `player-hud` (#10) and is merged on `development`: `ui/game/Hud.kt` lays out a `Column` with a Best and a Score `FaStatChip` side by side (label beside the value, `titleMd`, `space2` apart) and a second row of `FaSpawnCounter` chips; the cog wheel `FaIconButton` sits top right. `GameScreen` passes `level.spawnedTypes` and `state.spawnedByType` into it. The design system's Hud Bar and Score Counter (`HudBar.md`, `ScoreCounter.md`, `.fa-hud*`, `.fa-hudchip*`, `.fa-score*` in `bundle.css`) describe a single row of 56px chips with the label stacked above the value.

The field is drawn through `WorldViewport` (uniform scale, centred, letterboxed). Levels are 1280 x 720 world units; `LevelDefaults.HUD_BAND` (140 units) is the top band that `LevelLayoutsTest` keeps clear of zones. The highest zone today is the helipad of level 2 (top edge at y = 174).

## Goals / Non-Goals

**Goals:** match the Hud Bar and Score Counter with theme tokens only; drop the counters; prove by a unit test that the HUD fits the reserved band on the smallest supported phone.

**Non-Goals:** the score count-up animation from `ScoreCounter.md`; removing `GameState.spawnedByType` from the engine (it is tested and harmless; a separate clean-up can drop it); new HUD items (streak, next-up, lives).

## Decisions

1. **Modify the `player-hud` and `pause-menu` capabilities, not a new one.** The HUD is the same durable behaviour; the counters requirement is REMOVED and the layout is ADDED. Because both capabilities only exist in the active `player-hud` change, that change must be archived first. Rejected: editing `player-hud`'s artifacts in place, since that change is tied to #10 and partly implemented.

2. **Score Counter composable.** Replace `FaStatChip` with `FaScoreCounter(label, value, compact, popOnRise)`: a HUD chip face holding an optional leading icon and a `Column` with the label above the value. Label: `FaTheme.type.label` in `inkMuted`, text upper-cased in code (the CSS uses `text-transform`). Value: `FaTheme.type.score` in `ink`, or the compact style when `compact`. Best uses `compact = true` with `FaIcon.Star` tinted `sunDeep` at `FaDimens.iconMd`; Score keeps the existing pop (`HudConfig.SCORE_POP_SCALE`, `FaMotion.durPopMs`, `easeSnap`/`easeBounce`, off under reduced motion). Rejected: keeping the label beside the value, which is what makes the chips wide and does not match the showcase.

3. **Compact score type token.** The theme has `score` (DynaPuff 700 32/32) but no style for `.fa-score--compact` (24/24). Add `scoreCompact` to `FaTypography` (same family, weight and tabular figures, 24sp/24sp). The design system already defines the value, so no design-system change is needed; the token is named after the CSS modifier. Rejected: `titleMd` (22sp, weight 600) as a stand-in, which is a different style.

4. **Chip face per the CSS.** `.fa-hudchip`: min-height `hudChip`, horizontal padding `space4`, gap `space3`, `line3` outline in `outline`, `radiusMd`, `lip` lip (`shadow-lip`, the HUD-chip lip), `surface` fill. The `hudChipFace()` modifier changes its horizontal padding from `space3` to `space4` and its lip from `lipSm` to `lip`; the cog wheel `FaIconButton` gets the same `lip`, as `.fa-iconbtn`.

5. **Layout.** `Hud(best, score, onOptions, modifier)`: a `Box` with `safeDrawing` insets and `space4` padding; a `Row` at `TopStart` with `Arrangement.spacedBy(space3)` and items centred vertically (`.fa-hud__group`); the icon button at `TopEnd`. `GameScreen` stops passing `types`/`spawned`. `FaSpawnCounter` and `HudConfig.COUNTER_SPRITE_FRACTION` are deleted (no other users). `AircraftType.displayName` stays: `GameEndedDialog` uses it.

6. **Thousands grouping.** A pure `formatScore(value: Int): String` in `ui/components` groups by three digits with U+2009 THIN SPACE, tested in `commonTest`. It feeds the visible value and the accessibility description stays `"$label $value"`.

7. **Band fit as a unit test, and `HUD_BAND` grows to 160.** `hudBottom(topInset: Dp): Dp = topInset + space4 + hudChip + lip` lives next to `Hud` and is what the layout uses. A `commonTest` maps that height into world units with `WorldViewport` for each level and asserts it is at most `HUD_BAND`, on the smallest supported phone: 640 x 360 dp landscape with top inset 0, and 360 x 640 dp portrait with top inset 48 dp. Dp and pixels cancel out because the viewport is uniform. Landscape gives 77 dp at 0.5 dp per unit = 154 units, above today's 140, so `HUD_BAND` becomes 160; every zone stays below it (highest top 174), which `LevelLayoutsTest` keeps checking. Portrait is letterboxed far below the HUD. Rejected: shrinking chips below `hudChip` (breaks the design system and the 48dp touch minimum) and a manual-only check (not repeatable when levels change).

## Assumptions

- Smallest supported phone is 360 x 640 dp; status-bar inset is at most 48 dp in portrait and 0 at the top in landscape.
- `HUD_BAND` is raised to 160 world units rather than shrinking the HUD.
- Thousands grouping with a thin space is in scope because it is part of the Score Counter; the count-up animation is not.
- The best chip's star uses `sunDeep` at `iconMd`, as in the showcase.
- The cog wheel keeps today's `FaIcon.Settings` glyph and `Options` label.
- `GameState.spawnedByType` stays in the engine.

## Risks / Trade-offs

- [The `levels` change may change world size or move zones up] → the fit test and `LevelLayoutsTest` run over `Levels.all` and fail if a zone enters the band.
- [Archive order with `player-hud`] → stated in the proposal; archiving `compact-hud` first fails because the REMOVED/MODIFIED requirements are not in the main specs yet.
- [Devices with a top display cutout in landscape] → `safeDrawing` moves the HUD down by the inset, which the unit test does not model. The human device check covers it.
