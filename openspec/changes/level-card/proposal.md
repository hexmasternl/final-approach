# Proposal

GitHub issue: #86

## Why

The level-select tile is a stripped-down square: the level's field and its name. The design system describes a full level card (best score, tags, locked state), and the level unlocks (#83) need its locked state.

## What Changes

- Replace the square `LevelTile` with the full level card of `docs/design-system/components/LevelCard.md`: art on top, a `line-ui` outline between art and body, then the name and tags.
- A best-score chip (sun chip, `star` icon) in the top-right corner of the art, shown when the level has a best score.
- Up to two tag chips from the level's layout, such as "1 runway" and "1 helipad".
- A locked state: washed-out art, a round lock badge, one plain chip with the unlock condition and progress, a muted name, and a card that does not press.
- Previews in Day and Night: normal, with best score, locked.
- The card takes a locked flag and the unlock chip text as inputs. Deciding when a level is locked, and the progress text, belong to #83.

## Capabilities

### Modified Capabilities

- `level-selection`: the square tile becomes the level card, with best-score chip, tags and a locked presentation.

## Out of scope

- Unlock rules, progress counting and persistence (change `level-unlocks`, #83).
- The version 1 readers outside the world (#85).
- More than two tags, or other card variants.

## Depends on

- The best score store (`HighScoreStore`) and `LevelMiniature` (existing).
- Coordination with `level-unlocks`: it supplies the locked flag and the chip text and relies on this change for the locked look. Its task 2.1 becomes "pass state into the card".
