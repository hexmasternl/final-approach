# Spec Delta

## Purpose

Aircraft are either In Flight or Landed. The state decides which aircraft can collide, so a flying aircraft can cross over one that has already touched down.

## ADDED Requirements

### Requirement: Altitude state
Every aircraft SHALL have exactly one altitude state, either In Flight or Landed. The state SHALL NOT carry a numeric altitude.

#### Scenario: Aircraft spawns In Flight
- **WHEN** an aircraft spawns
- **THEN** its altitude state is In Flight

### Requirement: Touchdown makes an aircraft Landed
An aircraft SHALL become Landed when it touches the runway, which is just after the beginning of the runway. Until then it SHALL stay In Flight.

#### Scenario: Touching the runway
- **WHEN** an airplane touches the runway at the start of the runway
- **THEN** its altitude state changes from In Flight to Landed

#### Scenario: Still approaching
- **WHEN** an airplane is cleared to land but has not yet touched the runway
- **THEN** its altitude state is still In Flight

#### Scenario: State is kept during rollout
- **WHEN** a landed airplane rolls out along the runway
- **THEN** its altitude state stays Landed until it is removed

### Requirement: Collisions only between equal states
Two aircraft SHALL be able to collide only when they have the same altitude state. Aircraft with different altitude states SHALL NOT collide, even when their shapes overlap.

#### Scenario: Two aircraft In Flight
- **WHEN** two In Flight aircraft have overlapping shapes
- **THEN** they collide

#### Scenario: Two Landed aircraft
- **WHEN** two Landed aircraft have overlapping shapes
- **THEN** they collide

#### Scenario: Crossing over a landed aircraft
- **WHEN** an In Flight aircraft passes over a Landed aircraft so that their shapes overlap
- **THEN** no collision occurs and the game continues

### Requirement: Two aircraft on one bi-directional runway
On a bi-directional runway, two airplanes that have landed on it SHALL collide when their shapes overlap, because both are Landed.

#### Scenario: Landing from opposite ends
- **WHEN** two airplanes land from opposite ends of a bi-directional runway and their shapes overlap during rollout
- **THEN** they collide
