# game-engine Specification

## Purpose
TBD - created by archiving change first-level. Update Purpose after archive.

## Requirements

### Requirement: One engine owns the game state
A single `GameEngine` SHALL own the game state and expose it as a `StateFlow<GameState>`. The state SHALL change only through `update(deltaSeconds)` and the engine's input calls. The engine SHALL use no UI or platform API.

#### Scenario: State is published
- **WHEN** the engine is updated
- **THEN** the new state is available on its state flow

#### Scenario: Deterministic
- **WHEN** two engines run the same level with the same seed and the same updates and input
- **THEN** they produce the same states

### Requirement: Aircraft enter from the spawner
The engine SHALL ask the spawner for new aircraft on every update, passing the aircraft in the air, and SHALL add each spawned aircraft with its type, position and heading.

#### Scenario: Spawned aircraft appears
- **WHEN** the spawner returns a spawn during an update
- **THEN** the state contains a new aircraft of that type at that position and heading

#### Scenario: Never more than the cap
- **WHEN** the first level runs for a long time without the player doing anything
- **THEN** the number of aircraft in the air never exceeds three

### Requirement: Aircraft follow their path
An aircraft with a path SHALL turn towards the next point of the path at no more than its type's turn rate and move at its type's speed. Points it reaches SHALL be removed from the path, so the path shortens from the aircraft's end. When no points are left, the aircraft SHALL fly straight on in its last heading.

#### Scenario: Turns towards the path
- **WHEN** an aircraft has a path point to its right
- **THEN** its heading turns right by at most its turn rate times the time step

#### Scenario: Path is consumed
- **WHEN** an aircraft reaches a path point
- **THEN** that point is removed from its path

#### Scenario: Straight on after the path
- **WHEN** an aircraft's path is used up
- **THEN** it keeps its last heading and speed

### Requirement: Aircraft out of view are removed
Until level failure exists, an aircraft that has been inside the viewport and is then beyond the viewport by more than the leave margin SHALL be removed, which frees its slot for a new aircraft.

#### Scenario: Flies out
- **WHEN** an aircraft that was inside the viewport flies out beyond the leave margin
- **THEN** it is removed from the state

### Requirement: The engine reports each landing clearance
The engine SHALL report a clearance event, naming the aircraft and the kind of zone it was cleared to, exactly once each time a path is cut off at a valid landing zone. The event SHALL appear in the events of the next update after the clearance, and in no later update. The engine SHALL NOT report a clearance for a path that does not enter a valid landing zone.

#### Scenario: One event per clearance
- **WHEN** a path is extended into a valid landing zone and the engine is then updated twice
- **THEN** the first update's events hold one clearance event for that aircraft and the second update's events hold none

#### Scenario: Points after the cut-off report nothing
- **WHEN** further points are added to a path that is already cut off at a landing zone
- **THEN** no further clearance event is reported

#### Scenario: Two aircraft cleared before one update
- **WHEN** two aircraft are cleared to land between two updates
- **THEN** the next update's events hold one clearance event for each of them

#### Scenario: Re-routed aircraft cleared again
- **WHEN** a cleared aircraft gets a new path that is cleared to land again
- **THEN** a second clearance event is reported for it

#### Scenario: No event without a valid zone
- **WHEN** a path ends in open air, enters a runway threshold zone outside the alignment tolerance, enters the non-landing end of a one-way runway, or enters a zone of the wrong type
- **THEN** no clearance event is reported
