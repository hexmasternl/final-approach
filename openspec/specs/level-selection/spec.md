# level-selection Specification

## Purpose
TBD - created by archiving change levels. Update Purpose after archive.

## Requirements

### Requirement: Selection page after Take off
Choosing **Take off** on the main page SHALL open the level selection page, not start a game directly. The system back button on that page SHALL return to the main page.

#### Scenario: Take off opens selection
- **WHEN** the player taps Take off on the main page
- **THEN** the level selection page is shown

#### Scenario: Back returns home
- **WHEN** the player presses the system back button on the level selection page
- **THEN** the main page is shown

### Requirement: Horizontal scrolling when tiles do not fit
The tiles SHALL be laid out in one row. When the tiles do not all fit in the viewport width, the row SHALL scroll horizontally. When they fit, it SHALL NOT scroll.

#### Scenario: Narrow screen scrolls
- **WHEN** the viewport is too narrow to show all tiles
- **THEN** the player can drag the row sideways to reach every tile

#### Scenario: Wide screen fits
- **WHEN** the viewport is wide enough for all tiles
- **THEN** all tiles are visible at once

### Requirement: Tapping a tile loads and starts the level
Tapping a tile SHALL load that level and start it, showing the game screen with that level's airfield.

#### Scenario: Start level one
- **WHEN** the player taps the level one tile
- **THEN** the game screen opens showing the level one airfield and scenery

#### Scenario: Start another level
- **WHEN** the player returns to the selection page and taps the level three tile
- **THEN** the game screen shows the level three airfield, not level one

### Requirement: Tiles are accessible
Each tile SHALL be at least 48dp in both directions and SHALL be identifiable by its name, not by its picture alone.

#### Scenario: Tile size and name
- **WHEN** the tiles are shown on the smallest supported screen
- **THEN** each tile is at least 48dp wide and high and shows its name

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

### Requirement: Locked levels show a locked tile
The tile of a locked level SHALL show a lock icon, the unlock condition in words, and the player's progress towards it (for example "412 of 1,000 landings"). It SHALL be drawn in the disabled flat style and SHALL NOT start the level when tapped. Colour SHALL NOT be the only sign that it is locked.

#### Scenario: Locked tile content
- **WHEN** Island airport is locked and the player has landed 412 aircraft on Meadow field
- **THEN** its tile shows a lock icon and "Land 1 001 aircraft on Meadow Field to open Island." with "412 of 1 001 landings" (the target is the first count that opens the level, in the HUD's thin-space grouping)

#### Scenario: Tap does nothing
- **WHEN** the player taps a locked tile
- **THEN** no level starts and the selection page stays

#### Scenario: Points progress
- **WHEN** Savanna dawn is locked and the player has earned 12,500 points on Island airport
- **THEN** its tile shows "12 500 of 30 000 points"

### Requirement: Unlocked tiles are unchanged
The tile of an unlocked level SHALL look and behave as before, and the level name SHALL stay readable on every tile.

#### Scenario: Unlocked tile
- **WHEN** a level is unlocked
- **THEN** its tile shows its miniature and name without lock, and tapping it starts the level

### Requirement: A level that unlocks is open on return
After a run that reaches an unlock threshold, the selection page SHALL show the newly unlocked level as open.

#### Scenario: Unlock after a run
- **WHEN** a run on Meadow field takes the total landings to 1,001 and the player returns to the selection page
- **THEN** the Island airport tile is open
