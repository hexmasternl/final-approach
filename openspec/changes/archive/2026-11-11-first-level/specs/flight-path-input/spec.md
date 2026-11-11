## ADDED Requirements

### Requirement: Tap selects the nearest aircraft
A tap SHALL select the aircraft nearest to the tap point, if one is within the pick radius. The pick radius SHALL be at least 72dp on screen, converted to world units.

#### Scenario: Tap on an aircraft
- **WHEN** the player taps within the pick radius of an aircraft
- **THEN** that aircraft is selected

#### Scenario: Two aircraft near
- **WHEN** the tap is within the pick radius of two aircraft
- **THEN** the nearer one is selected

#### Scenario: Tap on empty field
- **WHEN** the player taps where no aircraft is within the pick radius
- **THEN** no aircraft is selected

### Requirement: Drag draws the path
Dragging that starts on an aircraft SHALL draw a new path for it, which replaces its old path. Points SHALL be added only when they are at least the minimum point spacing from the last point. Points SHALL be converted from screen pixels to world units before they reach the engine.

#### Scenario: New path replaces old
- **WHEN** the player drags from an aircraft that already has a path
- **THEN** the old path is discarded and the new one is drawn

#### Scenario: Point spacing
- **WHEN** a drag moves less than the minimum spacing from the last point
- **THEN** no point is added

#### Scenario: Landing cut-off
- **WHEN** the path being drawn enters a valid landing zone
- **THEN** the path ends there and further drag input is ignored, as the landing-zone rules say
