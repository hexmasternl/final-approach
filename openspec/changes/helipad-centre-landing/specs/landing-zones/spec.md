## MODIFIED Requirements

### Requirement: A helicopter path entering a helipad zone clears it to land
When the path being drawn for a helicopter enters a helipad landing zone, the helicopter SHALL be marked as cleared to land and further drag input for that path SHALL be ignored, at the moment the path enters the zone. The path SHALL then continue from the point where it entered the zone to the centre of the helipad and end exactly on that centre, whatever side the path came from. The join between the drawn path and this final leg SHALL be smoothed like the rest of the path, without a sharp corner. A path that starts inside the zone SHALL go straight to the centre. The shown path SHALL be this finished path.

#### Scenario: Path through the zone lands the helicopter
- **WHEN** the player drags a helicopter path that enters a helipad zone
- **THEN** the helicopter is cleared to land and its path ends exactly on the helipad centre

#### Scenario: Entry from any side ends on the centre
- **WHEN** helicopter paths enter the same helipad zone from the north, east, south and west
- **THEN** each path ends exactly on the helipad centre

#### Scenario: Final leg has no sharp corner
- **WHEN** a helicopter path enters a helipad zone at an angle to the line towards the centre
- **THEN** the path turns from the drawn line onto the leg to the centre smoothly, without a sharp corner

#### Scenario: Path that starts inside the zone
- **WHEN** a helicopter that is already over the helipad zone is touched and a path is dragged from it
- **THEN** the helicopter is cleared to land and its path goes straight to the helipad centre

#### Scenario: Clearance moment is unchanged
- **WHEN** a helicopter path enters a helipad zone
- **THEN** the path flashes white and the success sound plays at that moment, once

#### Scenario: Path that misses the zone
- **WHEN** a helicopter path passes the helipad outside the zone
- **THEN** the helicopter is not cleared to land and the path continues as drawn

#### Scenario: Further drag is ignored
- **WHEN** a helicopter path has entered a helipad zone and the player keeps dragging
- **THEN** no further points are added to that path

### Requirement: A cleared helicopter lands and is removed
A helicopter that is cleared to land SHALL follow its path to the end, on the helipad centre, and then perform a landing sequence of 3 seconds (configurable) there, in which it descends and lands. When the sequence ends the helicopter SHALL count as landed and be removed from the game, with no rollout.

#### Scenario: Helicopter touches down
- **WHEN** a cleared helicopter reaches the end of its path
- **THEN** a 3 second landing sequence starts on the helipad centre, and when it ends the helicopter has landed and is removed

#### Scenario: Descent is visible
- **WHEN** a helicopter is in its landing sequence
- **THEN** it is drawn descending towards its ground shadow, and with reduced motion it is drawn in a still landing pose for the whole sequence

#### Scenario: Landing counts at the end
- **WHEN** the landing sequence of a helicopter ends
- **THEN** the landing is counted and scored once, and not before

#### Scenario: Duration comes from configuration
- **WHEN** the landing sequence duration in the level configuration is changed
- **THEN** the sequence lasts that long and no other value is used
