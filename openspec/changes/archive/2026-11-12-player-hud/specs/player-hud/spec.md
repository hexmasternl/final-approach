# Spec Delta

## Purpose

The HUD is the overlay shown while a level is played. It tells the player their best score for the level, their current score and how many aircraft of each type have appeared, and holds the way into the options panel.

## ADDED Requirements

### Requirement: HUD is shown during play
The game screen SHALL show the HUD for as long as a level is being played, drawn above the field and aircraft.

#### Scenario: Level starts
- **WHEN** a level starts
- **THEN** the HUD is visible

#### Scenario: Field stays clear
- **WHEN** the HUD is shown
- **THEN** its items are small outlined chips at the edges and nothing covers a runway or helipad

### Requirement: Personal best for the level
The HUD SHALL show the player's personal best score for the level being played, stored per level on the device.

#### Scenario: Best score known
- **WHEN** a level starts and the player has a stored best score for it
- **THEN** the HUD shows that score as the best

#### Scenario: No best score yet
- **WHEN** a level starts and the player has no stored best score for it
- **THEN** the HUD shows a best score of 0

#### Scenario: Best is per level
- **WHEN** the player has different best scores on two levels
- **THEN** each level's HUD shows only its own best score

#### Scenario: Score beats the best
- **WHEN** the current score rises above the stored best score
- **THEN** the best score shown equals the current score, and the new best is stored

#### Scenario: Best survives a restart of the app
- **WHEN** the player has stored a best score, closes the app and plays the same level again
- **THEN** the HUD shows that best score

### Requirement: Current score
The HUD SHALL show the current score of the running level, starting at 0.

#### Scenario: Level starts
- **WHEN** a level starts
- **THEN** the current score shows 0

#### Scenario: Aircraft lands
- **WHEN** an aircraft lands
- **THEN** the current score increases by the points of that aircraft's type

### Requirement: Score pop
When the current score is updated by a landing, the score value SHALL briefly grow and shrink back to its original size.

#### Scenario: Score rises
- **WHEN** the current score rises
- **THEN** the score value scales up a little and returns to its original size within a short time

#### Scenario: Reduced motion
- **WHEN** the device has reduced motion enabled and the score rises
- **THEN** the score value changes without the grow-and-shrink animation

### Requirement: Spawn counter per aircraft type
The HUD SHALL show, for each aircraft type the level spawns, a counter of how many aircraft of that type have spawned in the running level. Types the level does not spawn SHALL NOT be shown. Each counter SHALL identify its type by the aircraft's silhouette, not by colour alone.

#### Scenario: Only configured types
- **WHEN** a level that spawns only light props and helicopters is played
- **THEN** the HUD shows a counter for light prop and for helicopter, and none for turboprop, jet or jumbo

#### Scenario: Counters start at zero
- **WHEN** a level starts
- **THEN** every shown counter reads 0

#### Scenario: Aircraft spawns
- **WHEN** an aircraft of a shown type spawns
- **THEN** the counter of that type increases by 1 and no other counter changes

#### Scenario: Landing does not change counters
- **WHEN** an aircraft lands
- **THEN** no spawn counter changes

### Requirement: Cog wheel button
The HUD SHALL show a cog wheel button in the top-right corner. Tapping it SHALL pause the game and open the options panel.

#### Scenario: Button position
- **WHEN** the HUD is shown
- **THEN** a cog wheel button with an accessible label is in the top-right corner of the screen

#### Scenario: Tap
- **WHEN** the player taps the cog wheel button
- **THEN** the game pauses and the options panel opens
