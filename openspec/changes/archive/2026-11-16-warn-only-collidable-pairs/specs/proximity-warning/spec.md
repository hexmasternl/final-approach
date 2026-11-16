## MODIFIED Requirements

### Requirement: Engine exposes the proximity warning
The engine SHALL expose, as part of the game state, which aircraft are warned. An aircraft SHALL be warned when at least one other aircraft that it can collide with is within the warning distance of it. The warning distance SHALL come from the level configuration.

#### Scenario: Warning switches on
- **WHEN** the distance between two In Flight aircraft drops below the warning distance
- **THEN** both aircraft are marked as warned in the game state

#### Scenario: Warning switches off
- **WHEN** two warned aircraft separate to more than the warning distance
- **THEN** neither is marked as warned any more

#### Scenario: Warning covers every aircraft involved
- **WHEN** three In Flight aircraft are within the warning distance of each other
- **THEN** all three are marked as warned

### Requirement: Warned aircraft get a spinning dashed red ring
The UI SHALL draw a dashed ring in the `warning` colour around every warned aircraft, in the effects layer, with the dashes slowly rotating. The ring SHALL appear and disappear with a transition, not abruptly. An aircraft that is not warned SHALL NOT show a ring.

#### Scenario: Ring on both aircraft
- **WHEN** two aircraft are warned because of each other
- **THEN** each of them shows a dashed red ring that spins

#### Scenario: Ring removed on separation
- **WHEN** the aircraft separate beyond the warning distance
- **THEN** both rings transition away

#### Scenario: No ring for aircraft that are close but not warned
- **WHEN** two aircraft are within the warning distance and neither is warned
- **THEN** neither shows a ring

### Requirement: Reduced motion shows a static ring
When the device's reduced-motion setting is on, the warning ring SHALL be drawn dashed but not rotating.

#### Scenario: Reduced motion on
- **WHEN** reduced motion is on and two aircraft are warned
- **THEN** each shows a dashed ring whose dashes do not move

## ADDED Requirements

### Requirement: Only pairs that can collide are warned
Two aircraft within the warning distance SHALL count towards each other's warning only when the collision rules allow that pair to collide. Two aircraft that cannot collide with each other MUST NOT cause a warning for each other, however close they are.

#### Scenario: Both airborne
- **WHEN** two In Flight aircraft are within the warning distance
- **THEN** both are warned

#### Scenario: Same runway, same direction
- **WHEN** two airplanes roll out on the same runway in the same direction within the warning distance of each other, and no other aircraft is near
- **THEN** neither is warned

#### Scenario: Rolling behind a fading airplane
- **WHEN** an airplane rolling out comes within the warning distance of an airplane that is fading out on the same runway after a rollout in the same direction, and no other aircraft is near
- **THEN** neither is warned

#### Scenario: Flying past a landed aircraft
- **WHEN** an In Flight aircraft is within the warning distance of a Landed airplane on a runway, and no other aircraft is near
- **THEN** neither is warned

#### Scenario: Same runway, opposite directions
- **WHEN** two airplanes roll out on the same runway in opposite directions within the warning distance of each other
- **THEN** both are warned

#### Scenario: Crossing runways
- **WHEN** two airplanes roll out on two different runways within the warning distance of each other
- **THEN** both are warned

#### Scenario: Warning starts at touchdown
- **WHEN** an airplane touches down within the warning distance of an airplane rolling out on the same runway in the opposite direction
- **THEN** neither is warned before the touchdown and both are warned from the touchdown on

#### Scenario: Helicopter in its landing sequence
- **WHEN** an aircraft is within the warning distance of a helicopter in its landing sequence, and no other aircraft is near
- **THEN** neither is warned

### Requirement: A warning needs only one pair that can collide
An aircraft SHALL be warned when at least one aircraft it can collide with is within the warning distance, also when other aircraft it cannot collide with are near. An aircraft near a warned aircraft SHALL NOT be warned for that reason alone.

#### Scenario: Mixed group
- **WHEN** two airplanes roll out on the same runway in the same direction within the warning distance of each other, and a third airplane rolling out on a different runway is within the warning distance of the first only
- **THEN** the first and the third are warned and the second is not

#### Scenario: Flying over a warned pair
- **WHEN** an In Flight aircraft is within the warning distance of two warned airplanes rolling out in opposite directions, and of no other In Flight aircraft
- **THEN** the two rolling airplanes are warned and the In Flight aircraft is not
