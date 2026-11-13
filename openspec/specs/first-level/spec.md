# first-level Specification

## Purpose
TBD - created by archiving change first-level. Update Purpose after archive.

## Requirements

### Requirement: Take off opens the first level
Tapping Take off on the main page SHALL open the game screen with the Meadow Field level. For a player who has not completed the tutorial, the level SHALL start with the tutorial's first step; otherwise aircraft SHALL start to arrive as usual. Back SHALL return to the main page.

#### Scenario: Take off
- **WHEN** a player who has completed the tutorial taps Take off
- **THEN** the game screen shows Meadow Field and aircraft start to arrive

#### Scenario: Take off for the first time
- **WHEN** a player who has not completed the tutorial taps Take off
- **THEN** the game screen shows Meadow Field with the tutorial's goal step and no aircraft

#### Scenario: Back
- **WHEN** the player presses back on the game screen
- **THEN** the main page is shown

### Requirement: Landscape while playing
The game screen SHALL lock the app in landscape for as long as it is shown, through `AppHost`, and SHALL release the lock when it is left.

#### Scenario: Enter the level
- **WHEN** the game screen opens while the phone is held upright
- **THEN** the app switches to landscape

#### Scenario: Leave the level
- **WHEN** the player leaves the game screen
- **THEN** the orientation lock is released

### Requirement: The airport fits every screen
The world SHALL be scaled uniformly to fit the screen and centred, so the whole airport is visible on small and large phones. Areas outside the world SHALL show the lawn.

#### Scenario: Wide screen
- **WHEN** the screen is wider than the world's aspect ratio
- **THEN** the full world height is shown and the world is centred horizontally

#### Scenario: Round trip
- **WHEN** a world point is converted to screen pixels and back
- **THEN** the result is the original point

### Requirement: Meadow Field airport
The level SHALL show one runway in the middle of the screen, marked as one-way runway 27, and one helipad on the right, on the striped lawn. It SHALL show decoration that makes it read as a small airfield: a taxiway, an apron, a terminal with a control tower, a hangar, a windsock and trees. Decoration SHALL be level data and SHALL NOT cover a runway, a helipad or their landing zones.

#### Scenario: Layout
- **WHEN** the level is shown
- **THEN** the runway lies left to right in the middle and the helipad is on the right

#### Scenario: Runway 27 marking
- **WHEN** the level is shown
- **THEN** the runway shows the number 27 and threshold bars only at its right end, nothing of the kind at its left end, and chevrons on its centre line pointing left

#### Scenario: Decoration is clear of zones
- **WHEN** the Meadow Field config is validated
- **THEN** no scenery item overlaps a runway, a helipad or a landing zone

### Requirement: Drawn in the design-system layer order
The game screen SHALL draw field, paths, ground shadows, aircraft and selection ring in that order, using theme colours, dimensions and the aircraft sprites, with no literal colours or stroke widths.

#### Scenario: Paths under aircraft
- **WHEN** a path passes under an aircraft
- **THEN** the aircraft is drawn over the path
