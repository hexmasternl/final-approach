# Spec Delta

## MODIFIED Requirements

### Requirement: Leaving the viewport loses the level
An aircraft that has been inside the viewport, has not landed, is beyond the viewport edge by more than the leave margin and has no path SHALL end the level, with game-over reason `left area` and the aircraft involved. An aircraft beyond the leave margin that is following a path SHALL NOT end the level.

#### Scenario: Aircraft flies out
- **WHEN** an aircraft that has been inside the viewport and has no path flies beyond the leave margin without having landed
- **THEN** the game state is game over with reason `left area` naming that aircraft, and the simulation stops

#### Scenario: Path ended earlier
- **WHEN** an aircraft's path ended while it was inside the viewport, and it then flies straight on beyond the leave margin
- **THEN** the game state is game over with reason `left area` naming that aircraft

#### Scenario: Aircraft follows its path out and back in
- **WHEN** an aircraft that has been inside the viewport follows its path beyond the leave margin and back inside the viewport before the path ends
- **THEN** the level continues

#### Scenario: Aircraft outside while following its path
- **WHEN** an aircraft is beyond the leave margin and still has path left to follow
- **THEN** the level continues

## ADDED Requirements

### Requirement: Path ending outside loses the level
When an aircraft that has been inside the viewport reaches the end of its path while it is beyond the leave margin, the level SHALL end with game-over reason `left area` naming that aircraft, in the same simulation step in which the path ends.

#### Scenario: Path ends outside
- **WHEN** an aircraft follows its path beyond the leave margin and the path ends while it is still there
- **THEN** in the step in which the path ends the game state becomes game over with reason `left area` naming that aircraft

#### Scenario: Path removed outside
- **WHEN** the player removes the path of an aircraft that is beyond the leave margin, for example with a tap
- **THEN** at the next simulation step the game state becomes game over with reason `left area` naming that aircraft

#### Scenario: Path replaced before leaving
- **WHEN** the player draws a new path for an aircraft just before it leaves, and the aircraft follows that new path beyond the leave margin
- **THEN** the level continues while the new path lasts, and ends with reason `left area` if the new path ends while the aircraft is beyond the leave margin
