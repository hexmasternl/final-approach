# Proposal

GitHub issue: #28

## Why

The HUD covers too much of the field: next to the Best and Score chips it shows a second row with a spawn counter per aircraft type. That breaks the design-system principle "The field stays clear" and does not match the Hud Bar component.

## What Changes

- **BREAKING (UI):** remove the per-type spawn counters from the HUD. Landed-per-type totals stay in the game-ended dialog.
- Lay the HUD out as the design-system Hud Bar: one row, `space-4` from the screen edges, `space-3` between items, every item `hud-chip` tall.
- Left group: the best score as the compact Score Counter (star icon, `Best` label, 24px value), then the full Score Counter (`Score` label above the 32px score value).
- Right group: the existing cog wheel icon button.
- All sizes, type, outline, radius and lip shadow come from theme tokens; add the token the theme lacks for the compact score value.
- Make sure the HUD stays inside the top band reserved for it on the smallest supported phone, portrait and landscape, so it covers no landing zone on any level.
- Update the HUD bullet in `docs/game-design.md`.

Out of scope: streak badge, next-aircraft chip and lives in the HUD; the score count-up animation; the options panel content; removing the engine's per-type spawn tally (`GameState.spawnedByType`); the game-ended dialog.

Depends on: `player-hud` (#10), which introduced the HUD and the `player-hud` and `pause-menu` capabilities this change modifies. `player-hud` must be archived before this change is archived.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `player-hud`: spawn counters removed; the HUD's layout, best-score and score presentation, and its fit in the top band are specified.
- `pause-menu`: Continue and Restart no longer mention spawn counters.

## Impact

- `shared/` `commonMain`: `ui/game/Hud.kt` (layout, call signature), `ui/components/FaHudParts.kt` (score chips; `FaSpawnCounter` and `HudConfig.COUNTER_SPRITE_FRACTION` removed), `ui/game/GameScreen.kt` (HUD call), `ui/theme/FaTypography.kt` (compact score style).
- `domain/LevelDefaults.HUD_BAND` may grow so the band covers the HUD (see design).
- `commonTest`: a HUD-fit test.
- Docs: `docs/game-design.md`; design system `HudBar.md`/`ScoreCounter.md` unchanged.
