# pause-menu Specification

## Purpose
The options panel lets the player pause a level and then carry on, start over or leave.

## Requirements

### Requirement: Options panel actions
The options panel SHALL show exactly three actions: Continue, Restart and Quit. The game SHALL be frozen while the panel is open.

#### Scenario: Panel content
- **WHEN** the options panel is open
- **THEN** Continue, Restart and Quit are shown, and nothing else can be acted on

#### Scenario: Game frozen
- **WHEN** the options panel is open
- **THEN** aircraft do not move, nothing spawns and the score does not change

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

### Requirement: Quit
Quit SHALL end the level and show the main page.

#### Scenario: Quit
- **WHEN** the player taps Quit
- **THEN** the level ends and the main page is shown

#### Scenario: Best score kept on quit
- **WHEN** the player quits after beating the stored best score
- **THEN** the new best score is still stored
