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
