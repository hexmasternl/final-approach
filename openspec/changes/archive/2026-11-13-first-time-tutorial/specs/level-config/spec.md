## MODIFIED Requirements

### Requirement: Optional level settings with defaults
A level config SHALL carry these optional fields, each with a default that changes nothing: an aircraft speed multiplier, scoring (lives and a points multiplier), wind (direction and strength), an emergency-aircraft flag and a tutorial script. All tunable values SHALL have defaults defined in config, not as magic numbers in logic.

#### Scenario: Defaults change nothing
- **WHEN** a level config sets none of the optional fields
- **THEN** the speed multiplier is 1, there is no wind, no emergency aircraft and no tutorial

#### Scenario: Speed multiplier
- **WHEN** a level config sets a speed multiplier
- **THEN** the multiplier is available to the engine without any change to the aircraft roster

#### Scenario: Only Meadow Field has a tutorial
- **WHEN** the built-in levels are inspected
- **THEN** Meadow Field carries a tutorial script and no other level does
