# level-failure Specification

## Purpose
Defines how a level is lost: through a collision or by an aircraft escaping the viewport, and what state the engine reports when that happens.

## Requirements

### Requirement: Leaving the viewport loses the level
An aircraft that leaves the viewport without having landed SHALL end the level, with game-over reason `left area` and the aircraft involved.

#### Scenario: Aircraft flies out
- **WHEN** an aircraft that has been inside the viewport leaves it without having landed
- **THEN** the game state is game over with reason `left area` naming that aircraft, and the simulation stops

### Requirement: Entering aircraft are not lost
An aircraft SHALL only be able to leave the viewport after it has been inside the viewport at least once.

#### Scenario: Aircraft still entering
- **WHEN** an aircraft has spawned outside the viewport and has not yet been inside it
- **THEN** it does not end the level

### Requirement: Edge touches are tolerated
An aircraft SHALL be counted as having left only when it is beyond the viewport edge by more than a configured margin.

#### Scenario: Touching the edge
- **WHEN** an aircraft briefly crosses the viewport edge by less than the margin and comes back
- **THEN** the level continues

### Requirement: Landed aircraft never trigger leaving
Aircraft that have landed and been removed SHALL NOT cause a level failure.

#### Scenario: Landed aircraft removed
- **WHEN** an aircraft has landed and is removed
- **THEN** it cannot end the level by leaving the viewport

### Requirement: Game over reports its reason
The game-over state SHALL carry the reason (`collision` or `left area`) and the aircraft involved, so that the UI can describe what happened.

#### Scenario: Reason available to the UI
- **WHEN** the level is lost
- **THEN** the game state exposes the reason and the one or two aircraft involved
