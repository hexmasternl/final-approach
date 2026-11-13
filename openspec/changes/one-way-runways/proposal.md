# Proposal

GitHub issue: #61

## Why

With every runway two-way, any approach lands, so the first levels ask little planning. Making the Meadow Field runway and the Island diagonal one-way makes the player bring airplanes in from one side, which fits the core challenge of sequencing landings.

## What Changes

- **Meadow Field (level 1):** runway `09-27` becomes one-way, **runway 27**: landing heading west (`3 * PI / 2`). Airplanes approach from the east, touch down at the right end and roll out to the left. A path that enters the 09 (west) end no longer clears an airplane to land. The runway keeps its id, position, length and accepted types; the helipad stays where it is.
- **Island (level 2):** the diagonal runway `04-22` becomes one-way, **runway 22**: landing heading south-west (`5 * PI / 4`). Airplanes approach from the north-east, touch down at the north-east end and roll out to the south-west. It keeps excluding the jumbo. The main runway `09-27` stays two-way.
- **Savanna (level 3):** unchanged; all its runways stay two-way.
- **One-way runway marking:** the runway drawing shows which way a one-way runway works by shape, not only by its single number: threshold bars, touchdown patch and number only at the landing end, and the centre line drawn as chevrons pointing in the landing direction. The design system gets this one-way variant first (`docs/design-system/components/Runway.md`, `world.md`, showcase), then the game uses it.
- **Docs and tests:** `docs/game-design.md` describes the new runway modes; tests that expect every runway to be two-way are updated.

## Capabilities

### New Capabilities

- `runway-markings`: how a runway's markings show its landing ends, including the one-way variant.

### Modified Capabilities

- `level-layouts`: level one's runway and level two's diagonal runway are one-way with a stated landing heading; level three's requirement now states that its runways stay two-way; a new requirement checks there is room for an aligned approach to each one-way runway.
- `level-config`: the first-level requirement no longer says its runway is two-way.
- `first-level`: the Meadow Field airport shows only runway 27, with the one-way marking.

## Out of scope

- Any change to the engine's landing rules, landing zones, lead-in or rollout: `RunwayMode.OneWay` already works end to end.
- Moving the Meadow Field helipad or any runway, or changing level 3.
- Changing the spawner or its fair-entry rules (only a check that aligned approaches still fit).
- Telling the player in text (coach marks, hints) that a runway is one-way.

## Depends on

- The existing `RunwayMode.OneWay` support in `domain/LevelConfig.kt` and the validator's axis check.
- Active changes `larger-level-worlds`, `world-zoom-retune` and `detailed-scenery` also edit `Levels.kt` and its tests; whichever lands later rebases on the others. No requirement overlaps.

## Impact

- `app/src/shared/src/commonMain/kotlin/nl/hexmaster/finalapproach/domain/Levels.kt` (two runway modes).
- `app/src/shared/src/commonMain/kotlin/nl/hexmaster/finalapproach/ui/game/AirfieldDrawing.kt` (`drawRunway`).
- Tests in `commonTest`: `LevelsTest`, `LevelLayoutsTest`, new level-landing tests.
- Docs: `docs/game-design.md`, `docs/design-system/world.md`, `docs/design-system/components/Runway.md`, `docs/design-system/showcase.html` (and `showcase.pdf` when regenerated).
