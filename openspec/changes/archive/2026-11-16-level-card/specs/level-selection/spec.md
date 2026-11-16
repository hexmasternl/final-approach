# level-selection Specification

## REMOVED Requirements

### Requirement: One square tile per level
**Reason**: The tile becomes the full level card, which is not square.
**Migration**: See "One card per level".

## ADDED Requirements

### Requirement: One card per level
The page SHALL show one card for each level, in level order. A card SHALL hold, from top to bottom, the art, a `line-ui` outline line, and a body with the level's name and its tags. The art SHALL be the miniature of the level's own field as before: drawn in the level's own theme from the level's layout data, holding every runway and helipad, without aircraft, flight paths, clouds or moving ambient life. The card SHALL be a moulded paper card that presses like a button when the level is unlocked.

#### Scenario: Three cards
- **WHEN** the level selection page is shown
- **THEN** three cards are shown, in level order, each with art, an outline between art and body, and the level name

#### Scenario: Art matches the level
- **WHEN** a card is shown
- **THEN** its art shows the level's own runways, helipads and scenery, and every runway and helipad is in view

#### Scenario: Island sea stays water
- **WHEN** the island card is shown while the app uses the Night or Dawn palette
- **THEN** its sea is drawn in the water colours of the level's own theme

### Requirement: Best-score chip
When the level has a best score above zero, its card SHALL show a sun chip with the `star` icon and the best score in the top-right corner of the art. When the level has no best score the card SHALL show no chip.

#### Scenario: Level with a best score
- **WHEN** a level has a best score of 18200
- **THEN** its card shows a sun chip with a star and the score in the top-right corner of the art

#### Scenario: Level never played
- **WHEN** a level has no best score
- **THEN** its card shows no best-score chip

### Requirement: Tag chips
The body of an unlocked card SHALL show up to two tag chips that describe the level's landing zones, one for runways and one for helipads, each only when the level has any, with a count: "1 runway", "2 runways", "1 helipad".

#### Scenario: Runway and helipad
- **WHEN** a level has one runway and one helipad
- **THEN** its card shows the chips "1 runway" and "1 helipad"

#### Scenario: No helipad
- **WHEN** a level has two runways and no helipad
- **THEN** its card shows only the chip "2 runways"

### Requirement: Locked card
A card shown as locked SHALL wash out its art, show a round lock badge over the art (a `paper-0` disc with a `line-ui` outline and the `lock` icon), show the level name in the muted ink, and replace the tags by one plain chip with the unlock condition and progress text it is given. It SHALL be drawn flat in paper tones at full opacity, without lip or shine, SHALL NOT show the best-score chip, and SHALL NOT start the level when tapped. The locked state SHALL be conveyed by the lock badge and the words, not by the washed-out art alone.

#### Scenario: Locked look
- **WHEN** a card is shown as locked with the text "Land 50 to unlock"
- **THEN** its art is washed out, a lock badge is on the art, and the body shows the name and one chip reading "Land 50 to unlock" instead of the tag chips

#### Scenario: Locked is flat
- **WHEN** a card is shown as locked
- **THEN** it has no lip and is not faded: its surfaces are paper tones at full opacity

#### Scenario: Locked does not press
- **WHEN** the player taps a locked card
- **THEN** no level starts, and the card does not move

#### Scenario: Locked is announced
- **WHEN** a screen reader focuses a locked card
- **THEN** it reads the level name and the unlock text, and the card is reported as disabled

### Requirement: Cards stay accessible
Each card SHALL be at least 48dp in both directions and identified by its level name, not by its art alone. Every chip SHALL have text, so no state relies on colour alone.

#### Scenario: Smallest screen
- **WHEN** the cards are shown on the smallest supported screen
- **THEN** each card is at least 48dp wide and high and shows its name
