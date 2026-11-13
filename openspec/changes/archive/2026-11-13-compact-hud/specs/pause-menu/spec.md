# Spec Delta

## MODIFIED Requirements

### Requirement: Continue
Continue SHALL close the options panel and resume the game exactly where it was paused.

#### Scenario: Continue
- **WHEN** the player taps Continue
- **THEN** the panel closes, and aircraft, paths and score are as they were when the panel opened and the game runs again

### Requirement: Restart
Restart SHALL reset the level and start it from scratch.

#### Scenario: Restart
- **WHEN** the player taps Restart
- **THEN** the panel closes, all aircraft are removed, the current score is 0 and the level runs from its start

#### Scenario: Best score kept
- **WHEN** the player restarts
- **THEN** the best score for the level is unchanged
