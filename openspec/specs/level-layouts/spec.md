# level-layouts Specification

## Purpose
TBD - created by archiving change levels. Update Purpose after archive.

## Requirements

### Requirement: Level one layout
Level one SHALL contain exactly one runway and one helipad. The runway SHALL accept every airplane type. The runway SHALL be one-way, runway 27: its only landing heading SHALL be west, so airplanes approach from the east, touch down at the east end and roll out to the west.

#### Scenario: Meadow zones
- **WHEN** level one is loaded
- **THEN** it has one runway and one helipad, and no other landing zone

#### Scenario: Meadow runway accepts all
- **WHEN** the level one runway is asked whether it accepts light prop, turboprop, jet and jumbo
- **THEN** the answer is yes for each

#### Scenario: Meadow runway is runway 27
- **WHEN** the landing headings of the level one runway are listed
- **THEN** there is exactly one, pointing west, and its runway number is `27`

#### Scenario: Landing on runway 27
- **WHEN** an airplane path enters the east end of the level one runway heading west, within the alignment tolerance
- **THEN** the airplane is cleared to land, touches down near the east end and rolls out towards the west

#### Scenario: The 09 end does nothing
- **WHEN** an airplane path enters the west end of the level one runway heading east, within the alignment tolerance of east
- **THEN** the airplane is not cleared to land and the path keeps taking points

### Requirement: Level two layout
Level two SHALL contain one main runway, one smaller runway that crosses the main runway diagonally, and one helipad. The main runway SHALL be longer than the smaller one, accept every airplane type and be two-way. The smaller runway SHALL accept every airplane type except the jumbo, and SHALL be one-way, runway 22: its only landing heading SHALL be south-west, so airplanes approach from the north-east and roll out to the south-west.

#### Scenario: Island zones
- **WHEN** level two is loaded
- **THEN** it has two runways and one helipad

#### Scenario: Diagonal crossing
- **WHEN** the two runways of level two are compared
- **THEN** their axes intersect within both runways, and the angle between them is neither 0 nor 90 degrees

#### Scenario: Main is bigger
- **WHEN** the two runways of level two are compared
- **THEN** the main runway is longer than the smaller runway

#### Scenario: Jumbo restricted on the smaller runway
- **WHEN** the smaller runway of level two is asked whether it accepts the jumbo
- **THEN** the answer is no, and the answer is yes for light prop, turboprop and jet

#### Scenario: Main accepts the jumbo
- **WHEN** the main runway of level two is asked whether it accepts the jumbo
- **THEN** the answer is yes

#### Scenario: Diagonal is runway 22
- **WHEN** the landing headings of the smaller runway of level two are listed
- **THEN** there is exactly one, pointing south-west, and its runway number is `22`

#### Scenario: Landing on runway 22
- **WHEN** an airplane path enters the north-east end of the smaller runway heading south-west, within the alignment tolerance
- **THEN** the airplane is cleared to land and rolls out towards the south-west

#### Scenario: The 04 end does nothing
- **WHEN** an airplane path enters the south-west end of the smaller runway heading north-east, within the alignment tolerance of north-east
- **THEN** the airplane is not cleared to land and the path keeps taking points

#### Scenario: Main runway stays two-way
- **WHEN** airplane paths enter each end of the main runway of level two, each heading along the runway into it within the alignment tolerance
- **THEN** both airplanes are cleared to land, one on runway 09 and one on runway 27

### Requirement: Level three layout
Level three SHALL contain two parallel main runways, one smaller runway that crosses both of them, and two helipads. The main runways SHALL accept every airplane type. The crossing runway SHALL accept every airplane type except the jumbo. Every runway of level three SHALL be two-way.

#### Scenario: Savanna zones
- **WHEN** level three is loaded
- **THEN** it has three runways and two helipads

#### Scenario: Parallel mains
- **WHEN** the two main runways of level three are compared
- **THEN** they have the same heading and do not touch each other

#### Scenario: Crossing runway
- **WHEN** the crossing runway of level three is compared with each main runway
- **THEN** its axis intersects within both runways

#### Scenario: Jumbo restricted on the crossing runway
- **WHEN** the crossing runway of level three is asked whether it accepts the jumbo
- **THEN** the answer is no, and the answer is yes for light prop, turboprop and jet

#### Scenario: Savanna stays two-way
- **WHEN** the landing headings of every runway of level three are listed
- **THEN** each runway has two, at opposite ends

### Requirement: Helicopters never use runways
A runway SHALL NOT accept the helicopter, and a helipad SHALL accept only the helicopter.

#### Scenario: Zone kinds
- **WHEN** any runway of any level is asked whether it accepts the helicopter, and any helipad is asked whether it accepts an airplane type
- **THEN** every answer is no

### Requirement: Layouts are data in world units
Each level layout SHALL be defined as data in world units (positions, headings in radians, lengths), with no pixel values, and every zone SHALL lie fully inside the level's world bounds and clear of the top edge band reserved for the HUD.

#### Scenario: Zones inside the world
- **WHEN** the zones of every level are checked against the world bounds
- **THEN** each runway and helipad lies fully inside them

#### Scenario: Helipads clear of runways
- **WHEN** every helipad is compared with every runway of the same level
- **THEN** no helipad overlaps a runway

### Requirement: Room for an aligned approach to a one-way runway
For every one-way runway, a straight approach on its landing heading SHALL fit inside the world: the line from the world edge to the runway's landing zone, along the centre line, SHALL stay inside the world and below the HUD band for at least the zone's depth before the threshold.

#### Scenario: Room for runway 27
- **WHEN** a straight line is drawn west along the centre line of the level one runway, from the world's east edge to its landing zone
- **THEN** it stays inside the world and below the HUD band, and is at least the zone's depth long before the threshold

#### Scenario: Room for runway 22
- **WHEN** a straight line is drawn south-west along the centre line of the level two diagonal, from the world edge to its landing zone
- **THEN** it stays inside the world and below the HUD band, and is at least the zone's depth long before the threshold
