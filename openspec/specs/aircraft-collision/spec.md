# aircraft-collision Specification

## Purpose
Defines when two aircraft count as colliding: only when their real shapes touch or overlap. Distance alone never causes a collision.

## Requirements

### Requirement: Each aircraft type has a collision shape
Every aircraft type SHALL define a collision shape in world units that closely matches its sprite, and the shape SHALL rotate with the aircraft's heading.

#### Scenario: Shape follows heading
- **WHEN** an aircraft's heading changes
- **THEN** its collision shape in world space is rotated by the same angle around the aircraft position

#### Scenario: Rotation changes the outcome
- **WHEN** two long aircraft lie side by side with a gap between their wingtips, and one is then rotated so its wing reaches into the other
- **THEN** the first pose is not a collision and the second is

### Requirement: Collision requires touching shapes
Two aircraft SHALL collide if and only if their collision shapes touch or overlap. Being within some distance of each other MUST NOT count as a collision.

#### Scenario: Close but not touching
- **WHEN** two aircraft are close to each other and their shapes have a gap between them
- **THEN** no collision is reported

#### Scenario: Touching shapes
- **WHEN** the shapes of two aircraft touch or overlap
- **THEN** a collision is reported for that pair

### Requirement: Fast aircraft cannot tunnel through each other
Collision detection SHALL consider the movement of each aircraft over the whole frame, so that two aircraft whose shapes met during a frame collide even if they do not overlap at the end of it.

#### Scenario: Head-on pass in one frame
- **WHEN** two aircraft fly head-on and a single update with a large delta time carries them past each other
- **THEN** a collision is reported

### Requirement: A collision loses the level
When a collision is detected, the engine SHALL stop the simulation and enter the game-over state with reason `collision` and the two aircraft involved.

#### Scenario: Collision ends the level
- **WHEN** an update detects a collision between two aircraft
- **THEN** the game state is game over with reason `collision` naming both aircraft, and further updates do not move any aircraft

### Requirement: Only aircraft in play can collide
Aircraft rolling out on a runway SHALL be able to collide. Aircraft already removed after landing or fading out MUST NOT.

#### Scenario: Rolling aircraft collides
- **WHEN** an aircraft rolling out on a runway overlaps another aircraft
- **THEN** a collision is reported

#### Scenario: Removed aircraft ignored
- **WHEN** an aircraft has landed and been removed, and another aircraft flies over where it was
- **THEN** no collision is reported
