# Spec Delta

## ADDED Requirements

### Requirement: Single-row HUD layout
The HUD SHALL be a single row at the top of the screen: the best score and then the current score at the top left, and the cog wheel button at the top right. It SHALL show no other items.

#### Scenario: Items and positions
- **WHEN** the HUD is shown
- **THEN** the best score chip and the score chip sit side by side at the top left, best first, the cog wheel button sits at the top right, and all three are top-aligned in one row

#### Scenario: No aircraft-type counters
- **WHEN** a level that spawns several aircraft types is played
- **THEN** the HUD shows no counter, silhouette or chip per aircraft type

### Requirement: HUD spacing and size
Every HUD item SHALL be as tall as the design-system HUD chip; the row SHALL be inset from the screen edges by `space-4` and its items SHALL be `space-3` apart, as in the design-system Hud Bar.

#### Scenario: Matches the Hud Bar
- **WHEN** the HUD is compared with the Hud Bar in the design-system showcase
- **THEN** the edge inset, the gap between chips and the chip height are the same

### Requirement: Score chip
The current score SHALL be shown as the design-system Score Counter: the label `Score` above the value, the label in the HUD chip label style and the value in the score style.

#### Scenario: Score chip content
- **WHEN** the HUD is shown
- **THEN** the score chip shows `SCORE` in small uppercase text above the current score in the large display font

### Requirement: Compact best score chip
The best score SHALL be shown as the compact Score Counter: a star icon, the label `Best` above the value, and a value smaller than the current score value.

#### Scenario: Best chip content
- **WHEN** the HUD is shown
- **THEN** the best chip shows a star icon and `BEST` above the best score, and its value is visibly smaller than the score value

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

## REMOVED Requirements

### Requirement: Spawn counter per aircraft type
**Reason**: The per-type counters filled a second HUD row and covered too much of the field (issue #28).
**Migration**: None in the HUD. How many aircraft of each type landed is still shown in the game-ended dialog.
