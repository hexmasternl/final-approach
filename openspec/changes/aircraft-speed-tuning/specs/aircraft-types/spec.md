## RENAMED Requirements

- FROM: `### Requirement: Speed increases from helicopter to jumbo`
- TO: `### Requirement: Speed increases from helicopter to jet`

## MODIFIED Requirements

### Requirement: Speed increases from helicopter to jet
With the default configuration, speed SHALL increase in this order: helicopter, light prop, turboprop, jet. Each of these types SHALL be at least 15% faster than the one before it, so the gaps are visible in play. The jumbo is not part of this ordering.

#### Scenario: Speed ordering
- **WHEN** the default speeds are compared
- **THEN** helicopter < light prop < turboprop < jet

#### Scenario: Gaps are visible
- **WHEN** the default speed of each of light prop, turboprop and jet is divided by the default speed of the next slower type
- **THEN** every ratio is at least 1.15

## ADDED Requirements

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
