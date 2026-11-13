## ADDED Requirements

### Requirement: Helicopters in a landing sequence cannot collide
A helicopter in its landing sequence SHALL NOT collide with any other aircraft, and no other aircraft SHALL collide with it, even when their shapes overlap.

#### Scenario: Another aircraft overlaps a landing helicopter
- **WHEN** an aircraft flies over a helipad while a helicopter is in its landing sequence there and their shapes overlap
- **THEN** no collision is reported

#### Scenario: Collisions resume for others
- **WHEN** two other aircraft touch while a helicopter is in its landing sequence
- **THEN** a collision is reported for that pair

#### Scenario: Before the sequence
- **WHEN** a helicopter on its way to the helipad touches another aircraft before the sequence starts
- **THEN** a collision is reported
