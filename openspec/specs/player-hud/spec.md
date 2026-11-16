# player-hud Specification

## Purpose
The HUD is the overlay shown while a level is played. It tells the player their best score for the level, their current score and how many aircraft of each type have appeared, and holds the way into the options panel.

## Requirements

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

### Requirement: Cog wheel button
The HUD SHALL show a cog wheel button in the top-right corner. Tapping it SHALL pause the game and open the options panel.

#### Scenario: Button position
- **WHEN** the HUD is shown
- **THEN** a cog wheel button with an accessible label is in the top-right corner of the screen

#### Scenario: Tap
- **WHEN** the player taps the cog wheel button
- **THEN** the game pauses and the options panel opens

### Requirement: Single-row HUD layout
The HUD SHALL be a single row at the top of the screen: the best score and then the current score at the top left, and the cog wheel button at the top right. It SHALL show no other items.

#### Scenario: Items and positions
- **WHEN** the HUD is shown
- **THEN** the best score chip and the score chip sit side by side at the top left, best first, the cog wheel button sits at the top right, and all three are top-aligned in one row

#### Scenario: No aircraft-type counters
- **WHEN** a level that spawns several aircraft types is played
- **THEN** the HUD shows no counter, silhouette or chip per aircraft type

### Requirement: HUD spacing and size
Every HUD item SHALL be as tall as the design-system flight strip; the row SHALL be inset from the screen edges by `space-4` and its items SHALL be `space-3` apart, as in the design-system Hud Bar.

#### Scenario: Matches the Hud Bar
- **WHEN** the HUD is compared with the Hud Bar in the design-system showcase
- **THEN** the edge inset, the gap between strips and the strip height are the same

### Requirement: Score chip
The current score SHALL be shown as a design-system flight strip: the label `Score` in the sun label cell left of the value, the label in the strip label style and the value in the strip value style.

#### Scenario: Score chip content
- **WHEN** the HUD is shown
- **THEN** the score strip shows `SCORE` in small uppercase text in a yellow cell, left of the current score in the display font

### Requirement: Compact best score chip
The best score SHALL be shown as a design-system flight strip: a star icon and the label `Best` in the paper label cell left of the value. The star and the paper cell, not a smaller value, set it apart from the current score.

#### Scenario: Best chip content
- **WHEN** the HUD is shown
- **THEN** the best strip shows a star icon and `BEST` in a paper cell left of the best score, and the score strip has no star and a yellow cell

### Requirement: Score grouping
Score and best score values of 1000 or more SHALL group thousands with a thin space.

#### Scenario: Large score
- **WHEN** the current score is 12450
- **THEN** the score chip shows `12 450` with a thin space between the groups

#### Scenario: Small score
- **WHEN** the current score is 450
- **THEN** the score chip shows `450`

### Requirement: HUD clear of landing zones
On the smallest supported phone screen, in portrait and in landscape, the HUD SHALL lie entirely within the top band of the screen that is kept clear of landing zones, so it covers no runway, runway landing zone or helipad on any level.

#### Scenario: Landscape
- **WHEN** any level is played on the smallest supported phone in landscape
- **THEN** no HUD chip overlaps a runway, runway landing zone or helipad

#### Scenario: Portrait
- **WHEN** any level is played on the smallest supported phone in portrait
- **THEN** no HUD chip overlaps a runway, runway landing zone or helipad
