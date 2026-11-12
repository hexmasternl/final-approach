## ADDED Requirements

### Requirement: Level one layout
Level one SHALL contain exactly one runway and one helipad. The runway SHALL accept every airplane type.

#### Scenario: Meadow zones
- **WHEN** level one is loaded
- **THEN** it has one runway and one helipad, and no other landing zone

#### Scenario: Meadow runway accepts all
- **WHEN** the level one runway is asked whether it accepts light prop, turboprop, jet and jumbo
- **THEN** the answer is yes for each

### Requirement: Level two layout
Level two SHALL contain one main runway, one smaller runway that crosses the main runway diagonally, and one helipad. The main runway SHALL be longer than the smaller one and accept every airplane type. The smaller runway SHALL accept every airplane type except the jumbo.

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

### Requirement: Level three layout
Level three SHALL contain two parallel main runways, one smaller runway that crosses both of them, and two helipads. The main runways SHALL accept every airplane type. The crossing runway SHALL accept every airplane type except the jumbo.

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
