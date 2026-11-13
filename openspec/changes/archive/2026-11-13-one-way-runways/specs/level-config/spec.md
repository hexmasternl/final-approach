## MODIFIED Requirements

### Requirement: The first level is a level config
The first level (issue #5, Meadow Field) SHALL be defined as a valid `LevelConfig`: one one-way runway (runway 27) lying left to right in the middle of the viewport, one helipad on the right side clear of the runway, and a difficulty curve whose first phase allows only light props and helicopters and whose maximum aircraft in the air is three.

#### Scenario: First level is valid
- **WHEN** the first level config is validated
- **THEN** validation succeeds

#### Scenario: First level opening phase
- **WHEN** the first level starts
- **THEN** its first phase allows only light props and helicopters

#### Scenario: First level aircraft cap
- **WHEN** any phase of the first level is inspected
- **THEN** its maximum number of aircraft in the air is at most three
