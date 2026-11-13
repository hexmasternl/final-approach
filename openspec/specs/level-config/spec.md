# level-config Specification

## Purpose
TBD - created by archiving change level-config. Update Purpose after archive.

## Requirements

### Requirement: A level is one configuration object
The game SHALL describe a level as a single `LevelConfig` in the `domain` package, holding everything the engine needs to run it. The engine, the spawner and the UI SHALL read level data only from it; no level data is hard-coded elsewhere.

#### Scenario: Config holds the level data
- **WHEN** a `LevelConfig` is created
- **THEN** it holds identity and presentation, airfield, world, run rules and difficulty curve

#### Scenario: Plain data
- **WHEN** a `LevelConfig` is built in `commonMain`
- **THEN** it consists of plain data classes and needs no Android or platform API

### Requirement: Identity and presentation
A level config SHALL have an id, a name, an order position and a theme that is Day or Night. It MAY set ambience (sky, clouds on or off).

#### Scenario: Theme choice
- **WHEN** a level config has the Night theme
- **THEN** the level reads as a night level through its theme field

#### Scenario: Ambience is optional
- **WHEN** a level config does not set ambience
- **THEN** the default ambience applies

### Requirement: Airfield with runways and helipads
A level config SHALL list runways and helipads in world units, with angles in radians. A runway SHALL have a position, an orientation, a length, a width and a mode, either one-way or two-way. A one-way runway SHALL carry its single allowed landing heading. A helipad SHALL have a position and a radius.

#### Scenario: Runway fields
- **WHEN** a runway is defined
- **THEN** it has position, orientation, length, width and mode

#### Scenario: One-way runway has a heading
- **WHEN** a one-way runway is defined
- **THEN** it carries one allowed landing heading

### Requirement: Runway landing direction
A one-way runway SHALL accept a landing only when the approach heading is within the landing tolerance of its allowed heading. A two-way runway SHALL accept a landing when the approach heading is within the landing tolerance of either end's heading, and the landing direction SHALL follow the approach.

#### Scenario: One-way runway, correct heading
- **WHEN** an airplane approaches a one-way runway on its allowed heading
- **THEN** the landing direction is valid

#### Scenario: One-way runway, opposite heading
- **WHEN** an airplane approaches a one-way runway on the opposite heading
- **THEN** the landing direction is not valid

#### Scenario: Two-way runway, either end
- **WHEN** an airplane approaches a two-way runway on either of its two headings
- **THEN** the landing direction is valid, and it follows the approach

#### Scenario: Across the runway
- **WHEN** an airplane approaches a runway at a right angle to its axis
- **THEN** the landing direction is not valid for either mode

### Requirement: World settings
A level config SHALL define the world size (the viewport in world units), the spawn margin (the distance outside the viewport where aircraft appear) and the spawn separation (the least distance between a new aircraft and any other aircraft). The spawn separation SHALL default to the global config value and MAY be overridden per level.

#### Scenario: Default separation
- **WHEN** a level config does not set a spawn separation
- **THEN** the global default spawn separation applies

#### Scenario: Overridden separation
- **WHEN** a level config sets a spawn separation
- **THEN** that value applies in that level

### Requirement: Run rules
A level config SHALL define a seed, a duration or an endless flag, and an optional goal such as a number of landings. A level without a fixed seed SHALL get a fresh seed for each run, so that runs of one level differ. A fixed seed SHALL make the level reproducible.

#### Scenario: Fixed seed
- **WHEN** a level config has a fixed seed
- **THEN** every run of the level uses that seed

#### Scenario: Fresh seed per run
- **WHEN** a level config has no fixed seed and is started twice
- **THEN** each run uses its own fresh seed

#### Scenario: Endless level
- **WHEN** a level config is endless
- **THEN** it has no duration

### Requirement: Difficulty curve
A level config SHALL define a difficulty curve as an ordered list of phases over level time. Each phase SHALL have a start time, a spawn interval range (minimum and maximum seconds), the allowed aircraft types with a weight each, and the maximum number of aircraft in the air at once. A phase stays active until the next phase starts; the last phase stays active until the level ends.

#### Scenario: Phase fields
- **WHEN** a phase is defined
- **THEN** it has a start time, a spawn interval range, weighted aircraft types and a maximum number of aircraft in the air

#### Scenario: Active phase
- **WHEN** the level time is between the start of a phase and the start of the next
- **THEN** that phase is the active phase

#### Scenario: Last phase continues
- **WHEN** the level time passes the start of the last phase
- **THEN** the last phase stays active

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

### Requirement: Validation
A level config SHALL be validated, and an invalid config SHALL be rejected with a message that names the problem. A config is valid only when all of these hold: it has at least one landing zone; every landing zone lies inside the viewport; no two landing zones overlap; it has at least one phase and the first phase starts at time 0; phase start times strictly increase; each spawn interval range is positive and its minimum is not above its maximum; each phase has at least one aircraft type and every weight is positive; each phase's maximum aircraft is at least 1; every aircraft type allowed in a phase has a matching landing zone (an airplane type needs a runway, a helicopter needs a helipad); the spawn separation is positive.

#### Scenario: No landing zone
- **WHEN** a config has no runway and no helipad
- **THEN** validation fails with a message saying a landing zone is required

#### Scenario: Zone outside the viewport
- **WHEN** a runway or helipad extends outside the viewport
- **THEN** validation fails with a message naming that zone

#### Scenario: Overlapping zones
- **WHEN** two landing zones overlap
- **THEN** validation fails with a message naming both zones

#### Scenario: Bad difficulty curve
- **WHEN** a phase has a minimum interval above its maximum, a non-positive weight, or a start time that does not increase
- **THEN** validation fails with a message naming the phase and the problem

#### Scenario: Aircraft without a place to land
- **WHEN** a phase allows helicopters and the config has no helipad
- **THEN** validation fails with a message saying helicopters need a helipad

#### Scenario: Valid config
- **WHEN** a config meets all the rules
- **THEN** validation succeeds

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
