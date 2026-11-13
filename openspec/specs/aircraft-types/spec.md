# aircraft-types Specification

## Purpose
TBD - created by archiving change aircraft-types. Update Purpose after archive.

## Requirements

### Requirement: Each aircraft type has a configured speed and landing points
Each of the five aircraft types (helicopter, light prop, turboprop, jet, jumbo) SHALL carry a speed in world units per second and a landing point value, taken from configuration with defaults. The turn rate SHALL stay part of the same per-type configuration.

#### Scenario: Every type has speed and points
- **WHEN** the default aircraft type configuration is read
- **THEN** each of the five types has a speed greater than zero and a landing point value greater than zero

### Requirement: Speed increases from helicopter to jet
With the default configuration, speed SHALL increase in this order: helicopter, light prop, turboprop, jet. Each of these types SHALL be at least 15% faster than the one before it, so the gaps are visible in play. The jumbo is not part of this ordering.

#### Scenario: Speed ordering
- **WHEN** the default speeds are compared
- **THEN** helicopter < light prop < turboprop < jet

#### Scenario: Gaps are visible
- **WHEN** the default speed of each of light prop, turboprop and jet is divided by the default speed of the next slower type
- **THEN** every ratio is at least 1.15

### Requirement: Speed is the same for every aircraft of a type
All aircraft of one type SHALL move at exactly the same speed. There SHALL be no per-instance variation, random or otherwise.

#### Scenario: Same type, same speed
- **WHEN** two aircraft of the same type are created, in the same or in different runs of the same level
- **THEN** both have exactly the same speed

### Requirement: Speed is constant during a run
An aircraft's speed SHALL NOT change while a level runs: no acceleration, no difficulty ramp and no slowing on approach.

#### Scenario: Speed over time
- **WHEN** an aircraft flies for any length of time, on a path or straight on after its path ends
- **THEN** it covers the same distance in every equal time step

### Requirement: A level can override speed and landing points
A level SHALL be able to override aircraft speeds with a speed multiplier and per-type speed values, and landing points with a points multiplier and per-type point values. The effective speed and points of every type SHALL be resolved once when the level starts and SHALL NOT change during that run. The engine SHALL read effective values from the level, never from mutable state.

#### Scenario: Speed multiplier
- **WHEN** a level has a speed multiplier of 1.5 and no per-type speed overrides
- **THEN** the effective speed of every type is 1.5 times its default speed

#### Scenario: Per-type speed override
- **WHEN** a level overrides the speed of the jet only
- **THEN** the jet has the overridden speed and every other type keeps its default speed

#### Scenario: Points override
- **WHEN** a level has a points multiplier of 2 and overrides the points of the helicopter
- **THEN** the effective helicopter points are the override multiplied by 2 and every other type has twice its default points

#### Scenario: Overrides stay fixed during a run
- **WHEN** a level has started and time passes
- **THEN** the effective speed and points of every type are unchanged

#### Scenario: No overrides
- **WHEN** a level carries no speed or points overrides
- **THEN** the effective values equal the defaults

#### Scenario: Invalid override
- **WHEN** a level has a multiplier or per-type value that is zero or negative
- **THEN** the level configuration is rejected with a message naming the invalid field

### Requirement: Landing points increase from helicopter to jumbo
With the default configuration, landing points SHALL increase in this order: helicopter, light prop, turboprop, jet, jumbo.

#### Scenario: Points ordering
- **WHEN** the default landing points are compared
- **THEN** helicopter < light prop < turboprop < jet < jumbo

### Requirement: A successful landing scores the type's points
A successful landing SHALL add the landed aircraft's effective landing points to the score.

#### Scenario: Helicopter and jumbo
- **WHEN** a helicopter lands and then a jumbo lands, with default configuration
- **THEN** the score rises by the helicopter's points and then by the jumbo's points, and the helicopter's award is the smaller

#### Scenario: Overridden points are used
- **WHEN** a jet lands in a level that overrides the jet's points
- **THEN** the score rises by the overridden value

### Requirement: Failures award no points
A collision or an aircraft leaving the viewport without landing SHALL NOT add to the score.

#### Scenario: Collision
- **WHEN** two aircraft collide
- **THEN** the score does not change

#### Scenario: Miss
- **WHEN** an aircraft leaves the viewport without landing
- **THEN** the score does not change

### Requirement: Default speeds
The default speeds SHALL be, at zoom factor 1, in world units per second: helicopter 20, light prop 36.67, turboprop 50, jet 66.67 and jumbo 65. These are one half (helicopter, jumbo) or two thirds (light prop, turboprop, jet) of the previous defaults.

#### Scenario: Default speeds
- **WHEN** the default aircraft type configuration is read at zoom factor 1
- **THEN** the speeds are 20, 36.67, 50, 66.67 and 65 for helicopter, light prop, turboprop, jet and jumbo

### Requirement: All default speeds are in one file
The default speed of every aircraft type SHALL be defined in a single speed parameter file, and no other file SHALL hold a default speed.

#### Scenario: One place to tune
- **WHEN** a default speed in the speed parameter file is changed
- **THEN** the effective speed of that type changes in every level that does not override it, and no other source file needs an edit

### Requirement: Smaller jumbo
The jumbo's type radius SHALL be half of its previous value, so its drawn sprite and its collision shape are half the previous size in each dimension.

#### Scenario: Jumbo size
- **WHEN** the jumbo is drawn and its collision shape is read
- **THEN** both are half the previous width and length, and the shape still follows the sprite

#### Scenario: Other types unchanged
- **WHEN** the radius of helicopter, light prop, turboprop and jet is read
- **THEN** each equals its previous value
