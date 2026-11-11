# proximity-warning Specification

## Purpose
Warns the player when aircraft get close to each other, before they collide, using a dashed red ring that does not rely on colour alone.

## Requirements

### Requirement: Engine exposes the proximity warning
The engine SHALL expose, as part of the game state, which aircraft are within the warning distance of another aircraft. The warning distance SHALL come from the level configuration.

#### Scenario: Warning switches on
- **WHEN** the distance between two aircraft drops below the warning distance
- **THEN** both aircraft are marked as warned in the game state

#### Scenario: Warning switches off
- **WHEN** two warned aircraft separate to more than the warning distance
- **THEN** neither is marked as warned any more

#### Scenario: Warning covers every aircraft involved
- **WHEN** three aircraft are within the warning distance of each other
- **THEN** all three are marked as warned

### Requirement: Warned aircraft get a spinning dashed red ring
The UI SHALL draw a dashed ring in the `warning` colour around every warned aircraft, in the effects layer, with the dashes slowly rotating. The ring SHALL appear and disappear with a transition, not abruptly.

#### Scenario: Ring on both aircraft
- **WHEN** two aircraft are within the warning distance
- **THEN** each of them shows a dashed red ring that spins

#### Scenario: Ring removed on separation
- **WHEN** the aircraft separate beyond the warning distance
- **THEN** both rings transition away

### Requirement: Warning does not rely on colour alone
The ring SHALL be recognisable by its dashed shape and its spin as well as its colour.

#### Scenario: Shape carries the meaning
- **WHEN** the ring is shown in greyscale
- **THEN** it is still distinguishable from the selection ring and flight paths by its dashes and rotation

### Requirement: Reduced motion shows a static ring
When the device's reduced-motion setting is on, the warning ring SHALL be drawn dashed but not rotating.

#### Scenario: Reduced motion on
- **WHEN** reduced motion is on and two aircraft are within the warning distance
- **THEN** each shows a dashed ring whose dashes do not move
