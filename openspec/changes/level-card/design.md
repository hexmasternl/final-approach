# Design

## Context

`LevelTile` in `ui/game/LevelSelectPage.kt` is a `FaMoulded` square (`FaDimens.levelTile`) with `LevelMiniature` and a title. `LevelCard.md` defines the full card. Existing parts: `FaMoulded`, `FaChip` (tones Sun and Plain, icons `FaIcon.Star` and `FaIcon.Lock`), `HighScoreStore`, and the score formatter in `ui/components/ScoreFormat.kt`.

## Decisions

1. **`LevelCard` replaces `LevelTile`** in `ui/game/`, built on `FaMoulded` with `tonePaper`, `radiusPanel`, `lipPanel`. Layout: art (the existing `LevelMiniature`, cropped to the card width), a divider of `lineUi` in `outline`, then the body. Rejected: keeping the square and squeezing chips into it, which does not fit the guideline body.
2. **Presentation state is an input, not logic.** The card takes `LevelCardState(bestScore: Int, tags: List<String>, locked: LockedInfo?)`, where `LockedInfo(text)` is the chip text. A pure function builds tags from the level layout (runway and helipad counts, singular and plural), unit tested in `commonTest`. Whether a level is locked and the progress text come from the caller; until #83 lands the page passes `locked = null`. Rejected: computing locks here (rules do not belong in composables, and #83 owns them).
3. **Best score** is read with `host.highScores.best(level.id)` when the page is composed (the page is recreated on return), formatted with the existing grouping helper, as `FaChip(tone = Sun, icon = Star)` in the art's top-right corner, offset by `FaDimens.space2`. The exact `AppHost` member name is to be confirmed when building.
4. **Locked.** Not pressable: use the flat surface (`drawFlat` / `Modifier.faFlatSurface`) in `paper` tones instead of `FaMoulded`, label `ink3`/`inkMuted`, full opacity (U7). The art wash is a flat `paper0` overlay at a token alpha, not a fade of the card. The badge is a `paper0` disc with a `lineUi` `outline` ring and `FaIcon.Lock`, centred over the art. The chip is `FaChip(tone = Plain, icon = Lock)`. Semantics: `disabled()` plus a content description of name and text.
5. **Sizes.** Card width stays `FaDimens.levelTile`; the height is art plus body. Any new size (art height, badge diameter, wash alpha) is an entry in `tokens.json`, regenerated with `gen-kotlin.cjs`, never a literal. If `tokens.json` already has the level-card values, use those.
6. **Previews** in `GamePreviews.kt`: normal, with best score, locked, each in Day and Night.
7. **Docs.** Update the Compose note in `LevelCard.md`: remove "Not yet the full card".

## Assumptions (the author can overrule)

- The card is portrait and taller than the old square once the body is added; the page still scrolls horizontally when cards do not fit.
- A best score of 0 shows no chip.
- Tags are derived from the layout: runway count, helipad count; at most two, by construction.
- The best score is not shown on a locked card (a locked level has none).
- Until #83 lands the page never passes a locked state; the locked card is only reachable in previews.
- Locked chip text is supplied by the caller; the example "Land 50 to unlock" from the guideline is only used in previews.
