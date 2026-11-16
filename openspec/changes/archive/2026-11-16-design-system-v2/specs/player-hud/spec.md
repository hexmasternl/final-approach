# Spec Delta

## MODIFIED Requirements

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
