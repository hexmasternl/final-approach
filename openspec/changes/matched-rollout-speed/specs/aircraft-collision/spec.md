# Spec Delta

## MODIFIED Requirements

### Requirement: Only aircraft in play can collide
Aircraft rolling out on a runway SHALL be able to collide. Aircraft already removed after landing or fading out MUST NOT.

#### Scenario: Rolling aircraft collides
- **WHEN** an aircraft rolling out on a runway overlaps another landed aircraft that is not rolling out on the same runway in the same direction
- **THEN** a collision is reported

#### Scenario: Removed aircraft ignored
- **WHEN** an aircraft has landed and been removed, and another aircraft flies over where it was
- **THEN** no collision is reported

## ADDED Requirements

### Requirement: Same-direction rollouts on one runway do not collide
Two airplanes rolling out on the same runway in the same direction, including while either of them fades out, MUST NOT collide with each other, even when their shapes touch. Airplanes rolling out on the same runway in opposite directions, or on different runways, SHALL still collide when their shapes touch.

#### Scenario: Jet lands right behind a light prop
- **WHEN** a light prop touches down on a runway and a jet touches down on the same runway in the same direction right behind it, so that the jet catches up during its slowdown
- **THEN** no collision is reported and both complete their rollout

#### Scenario: Catching up with a fading airplane
- **WHEN** an airplane rolling out reaches an airplane that landed before it on the same runway in the same direction and is fading out
- **THEN** no collision is reported

#### Scenario: Opposite ends of one runway
- **WHEN** two airplanes land from opposite ends of the same runway and their shapes touch during rollout
- **THEN** a collision is reported

#### Scenario: Different runways
- **WHEN** two airplanes rolling out on two different runways touch where the runways cross
- **THEN** a collision is reported
