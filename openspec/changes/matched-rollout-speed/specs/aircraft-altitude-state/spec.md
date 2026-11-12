# Spec Delta

## MODIFIED Requirements

### Requirement: Collisions only between equal states
Two aircraft SHALL be able to collide only when they have the same altitude state. Aircraft with different altitude states SHALL NOT collide, even when their shapes overlap. Two Landed airplanes rolling out on the same runway in the same direction are the one exception among equal states: they SHALL NOT collide with each other.

#### Scenario: Two aircraft In Flight
- **WHEN** two In Flight aircraft have overlapping shapes
- **THEN** they collide

#### Scenario: Two Landed aircraft
- **WHEN** two Landed aircraft that are not rolling out on the same runway in the same direction have overlapping shapes
- **THEN** they collide

#### Scenario: Crossing over a landed aircraft
- **WHEN** an In Flight aircraft passes over a Landed aircraft so that their shapes overlap
- **THEN** no collision occurs and the game continues
