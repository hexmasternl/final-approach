## MODIFIED Requirements

### Requirement: A cleared helicopter lands and is removed
A helicopter that is cleared to land SHALL follow its path to the end and then perform a landing sequence of 3 seconds (configurable) on the spot, in which it descends and lands. When the sequence ends the helicopter SHALL count as landed and be removed from the game, with no rollout.

#### Scenario: Helicopter touches down
- **WHEN** a cleared helicopter reaches the end of its path
- **THEN** a 3 second landing sequence starts on the helipad, and when it ends the helicopter has landed and is removed

#### Scenario: Descent is visible
- **WHEN** a helicopter is in its landing sequence
- **THEN** it is drawn descending towards its ground shadow, and with reduced motion it is drawn in a still landing pose for the whole sequence

#### Scenario: Landing counts at the end
- **WHEN** the landing sequence of a helicopter ends
- **THEN** the landing is counted and scored once, and not before

#### Scenario: Duration comes from configuration
- **WHEN** the landing sequence duration in the level configuration is changed
- **THEN** the sequence lasts that long and no other value is used
