# level-selection Specification

## ADDED Requirements

### Requirement: Locked levels show a locked tile
The tile of a locked level SHALL show a lock icon, the unlock condition in words, and the player's progress towards it (for example "412 of 1,000 landings"). It SHALL be drawn in the disabled flat style and SHALL NOT start the level when tapped. Colour SHALL NOT be the only sign that it is locked.

#### Scenario: Locked tile content
- **WHEN** Island airport is locked and the player has landed 412 aircraft on Meadow field
- **THEN** its tile shows a lock icon and "Land 1,000 aircraft on Meadow field to open Island airport." with "412 of 1,000 landings"

#### Scenario: Tap does nothing
- **WHEN** the player taps a locked tile
- **THEN** no level starts and the selection page stays

#### Scenario: Points progress
- **WHEN** Savanna dawn is locked and the player has earned 12,500 points on Island airport
- **THEN** its tile shows "12,500 of 30,000 points"

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
