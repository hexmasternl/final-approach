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
A level config SHALL define a difficulty curve as an ordered list of phases over level time. Each phase SHALL have a start time, a spawn interval range (minimum and maximum seconds), the allowed aircraft types with a weight each, and the maximum number of aircraft in the air at once. A phase stays active until the next phase starts. Without a difficulty ramp, the last phase stays active until the level ends; with a ramp, the ramp steps follow the last phase.

#### Scenario: Phase fields
- **WHEN** a phase is defined
- **THEN** it has a start time, a spawn interval range, weighted aircraft types and a maximum number of aircraft in the air

#### Scenario: Active phase
- **WHEN** the level time is between the start of a phase and the start of the next
- **THEN** that phase is the active phase

#### Scenario: Last phase continues
- **WHEN** a level has no difficulty ramp and the level time passes the start of the last phase
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
A level config SHALL be validated, and an invalid config SHALL be rejected with a message that names the problem. A config is valid only when all of these hold: it has at least one landing zone; every landing zone lies inside the viewport; no two landing zones overlap; it has at least one phase and the first phase starts at time 0; phase start times strictly increase; each spawn interval range is positive and its minimum is not above its maximum; each phase has at least one aircraft type and every weight is positive; each phase's maximum aircraft is at least 1; every aircraft type allowed in a phase has a matching landing zone (an airplane type needs a runway, a helicopter needs a helipad); the spawn separation is positive. When a level has a difficulty ramp, also: the step time is positive; the interval drop is positive; the interval floor is positive and not above the last phase's minimum interval; the aircraft cap is not below the last phase's maximum aircraft; the aircraft step count is at least 1; every type in the weight caps is allowed in the last phase, appears only once, and has a cap not below its weight in the last phase.

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

#### Scenario: Bad difficulty ramp
- **WHEN** a ramp has a non-positive step time or interval drop, a floor above the last phase's minimum interval, an aircraft cap below the last phase's maximum, an aircraft step count below 1, or a weight cap for a type the last phase does not allow, listed twice, or below that type's last-phase weight
- **THEN** validation fails with a message naming the ramp and the problem

#### Scenario: Aircraft without a place to land
- **WHEN** a phase allows helicopters and the config has no helipad
- **THEN** validation fails with a message saying helicopters need a helipad

#### Scenario: Valid config
- **WHEN** a config meets all the rules
- **THEN** validation succeeds

### Requirement: The first level is a level config
The first level (issue #5, Meadow Field) SHALL be defined as a valid `LevelConfig`: one one-way runway (runway 27) lying left to right in the middle of the viewport, one helipad on the right side clear of the runway, and a difficulty curve whose first phase allows only light props and helicopters and whose configured phases allow at most three aircraft in the air. Its difficulty ramp SHALL raise that maximum to at most five.

#### Scenario: First level is valid
- **WHEN** the first level config is validated
- **THEN** validation succeeds

#### Scenario: First level opening phase
- **WHEN** the first level starts
- **THEN** its first phase allows only light props and helicopters

#### Scenario: First level aircraft cap
- **WHEN** any configured phase of the first level is inspected
- **THEN** its maximum number of aircraft in the air is at most three

#### Scenario: First level ramp cap
- **WHEN** the first level's active phase is inspected at any level time
- **THEN** its maximum number of aircraft in the air is at most five

### Requirement: Optional difficulty ramp
A level config MAY carry a difficulty ramp. The ramp SHALL have a step time, an interval drop, an interval floor (the lowest minimum spawn interval), an aircraft cap (the most aircraft in the air the ramp can reach), an aircraft step count (raise the maximum every this many steps) and an ordered list of weight caps (a type and its highest weight). Step time, interval drop, aircraft step count and weight caps SHALL default to values in config: 60 s, 0.5 s, 2, and jet 4 then jumbo 3. A level without a ramp SHALL behave exactly as before.

#### Scenario: Ramp fields and defaults
- **WHEN** a ramp is created with only an interval floor and an aircraft cap
- **THEN** its step time is 60 s, its interval drop 0.5 s, its aircraft step count 2, and its weight caps are jet 4 then jumbo 3

#### Scenario: No ramp, no change
- **WHEN** a level without a ramp is asked for its active phase long after its last phase started
- **THEN** the last configured phase is returned unchanged

### Requirement: Ramp steps
With a ramp, ramp step n (n = 1, 2, ...) SHALL start at the last phase's start time plus n times the step time and stay active until the next step starts. Each step SHALL derive from the step before it (step 1 from the last phase) as follows: the minimum and maximum spawn interval both drop by the interval drop, but the minimum never goes below the floor and the range keeps the width of the last phase; the type at position (n - 1) modulo the list length in the weight caps gains 1 weight unless it is already at its cap, and other weights stay; on every step whose number is a multiple of the aircraft step count, the maximum number of aircraft in the air goes up by 1, but never above the aircraft cap. A ramp step SHALL never allow a type that the last phase does not allow and SHALL never lower a value. Once every limit is reached, the active phase SHALL stay the same for the rest of the level.

#### Scenario: Before the first step
- **WHEN** the level time is after the last phase started but before one step time has passed
- **THEN** the last configured phase is active

#### Scenario: Interval reaches the floor
- **WHEN** a ramp's steps would take the minimum spawn interval below the floor
- **THEN** the minimum stays at the floor and the maximum stays one range width above it

#### Scenario: Weights alternate up to their caps
- **WHEN** a level with default weight caps reaches ramp steps 1, 2, 3 and 4 from a last phase with jet weight 2 and jumbo weight 1
- **THEN** the jet weight is 3 after step 1 and 4 after step 3, the jumbo weight is 2 after step 2 and 3 after step 4, and no later step raises either

#### Scenario: Limits respected
- **WHEN** a ramp's level time is far beyond the step where every limit is reached
- **THEN** the maximum in the air equals the aircraft cap, the minimum interval equals the floor, every capped weight equals its cap, and the active phase equals the one at the final step

#### Scenario: No new types
- **WHEN** any ramp step is inspected
- **THEN** its allowed types are exactly those of the last configured phase

### Requirement: Level ramps
Every level SHALL have a ramp. Meadow Field: floor 2.5 s, cap 5, step 60 s. Island: floor 2.0 s, cap 6, step 60 s. Savanna: floor 1.5 s, cap 7, step 45 s. All three use a 0.5 s drop, an aircraft step count of 2 and the default weight caps (jet 4, jumbo 3). Weights in the scenarios are light prop / turboprop / jet / jumbo / helicopter.

#### Scenario: Meadow Field timeline
- **WHEN** the active phase of Meadow Field is inspected at 3:00, 4:00, 5:00 and 6:00
- **THEN** it is 3.5-6.5 s, max 3, weights 3/2/3/1/2 at 3:00; 3-6 s, max 4, 3/2/3/2/2 at 4:00; 2.5-5.5 s, max 4, 3/2/4/2/2 at 5:00; and 2.5-5.5 s, max 5, 3/2/4/3/2 at 6:00 and at every later time

#### Scenario: Island timeline
- **WHEN** the active phase of Island is inspected at 2:00, 3:00, 4:00 and 5:00
- **THEN** it is 3.5-6.5 s, max 4, weights 3/2/3/1/2 at 2:00; 3-6 s, max 5, 3/2/3/2/2 at 3:00; 2.5-5.5 s, max 5, 3/2/4/2/2 at 4:00; and 2-5 s, max 6, 3/2/4/3/2 at 5:00 and at every later time

#### Scenario: Savanna timeline
- **WHEN** the active phase of Savanna is inspected at 1:45, 2:30, 3:15, 4:00 and 4:45
- **THEN** it is 3.5-6.5 s, max 5, weights 3/2/3/1/2 at 1:45; 3-6 s, max 6, 3/2/3/2/2 at 2:30; 2.5-5.5 s, max 6, 3/2/4/2/2 at 3:15; 2-5 s, max 7, 3/2/4/3/2 at 4:00; and 1.5-4.5 s, max 7, 3/2/4/3/2 at 4:45 and at every later time

#### Scenario: Opening phases unchanged
- **WHEN** the configured phases of the three levels are compared with the phases before this change
- **THEN** they are identical
