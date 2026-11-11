## ADDED Requirements

### Requirement: Helipads have an invisible landing zone
Each helipad SHALL have a round landing zone centred on the pad. The zone SHALL NOT be drawn or hinted at. Its radius SHALL come from configuration, per helipad, with a default.

#### Scenario: Zone is not visible
- **WHEN** a level with a helipad is shown
- **THEN** nothing is drawn for the landing zone besides the helipad graphic

#### Scenario: Radius comes from configuration
- **WHEN** a helipad has its own zone radius configured
- **THEN** that radius is used, and a helipad without one uses the default radius

### Requirement: A helicopter path entering a helipad zone clears it to land
When the path being drawn for a helicopter enters a helipad landing zone, the path SHALL end at the point where it entered, further drag input for that path SHALL be ignored, and the helicopter SHALL be marked as cleared to land.

#### Scenario: Path through the zone lands the helicopter
- **WHEN** the player drags a helicopter path that enters a helipad zone
- **THEN** the path ends at the zone entry point and the helicopter is cleared to land

#### Scenario: Path that misses the zone
- **WHEN** a helicopter path passes the helipad outside the zone
- **THEN** the helicopter is not cleared to land and the path continues as drawn

#### Scenario: Further drag is ignored
- **WHEN** a helicopter path has entered a helipad zone and the player keeps dragging
- **THEN** no further points are added to that path

### Requirement: A cleared helicopter lands and is removed
A helicopter that is cleared to land SHALL follow its path to the end, land there, and be removed from the game.

#### Scenario: Helicopter touches down
- **WHEN** a cleared helicopter reaches the end of its path
- **THEN** it lands with the landing animation and is removed, with no rollout

### Requirement: Zones only accept their own aircraft kind
A helipad zone SHALL only clear helicopters, and a runway zone SHALL only clear airplanes.

#### Scenario: Airplane over a helipad
- **WHEN** an airplane path enters a helipad zone
- **THEN** nothing happens and the path continues

#### Scenario: Helicopter over a runway
- **WHEN** a helicopter path crosses a runway landing zone
- **THEN** nothing happens and the path continues

### Requirement: Runways have landing zones at their thresholds
A runway SHALL have landing zones only at its thresholds, not along the strip. A one-way runway SHALL have one zone, at the start of the runway in its landing direction. A two-way runway SHALL have two zones, one at each end, whose landing directions are opposite along the runway axis. Zone depth along the runway and width across it SHALL come from configuration.

#### Scenario: One-way runway
- **WHEN** a level has a one-way runway
- **THEN** it has exactly one landing zone, at the start of the runway in its landing direction

#### Scenario: Two-way runway
- **WHEN** a level has a two-way runway
- **THEN** it has exactly two landing zones, one at each end, with opposite landing directions

#### Scenario: Mid-runway is not a landing zone
- **WHEN** an airplane path crosses the middle of a runway
- **THEN** no landing is triggered

### Requirement: Runway landings need an aligned approach
A path that crosses a runway landing zone SHALL trigger a landing only if the path's direction at the zone is within a configurable angle tolerance, in radians, of that zone's landing direction. Otherwise no landing is triggered and the path continues as a normal path.

#### Scenario: Aligned crossing lands
- **WHEN** an airplane path crosses a zone with a direction within the tolerance of the zone's landing direction
- **THEN** the airplane is cleared to land

#### Scenario: Opposite direction on a one-way runway
- **WHEN** an airplane path crosses the zone of a one-way runway in the opposite direction
- **THEN** no landing is triggered and the path continues

#### Scenario: Crossing at a steep angle
- **WHEN** an airplane path crosses a zone at an angle larger than the tolerance
- **THEN** no landing is triggered and the path continues

#### Scenario: Two-way runway, both directions
- **WHEN** an airplane path crosses either end zone of a two-way runway in that zone's landing direction
- **THEN** the airplane is cleared to land at that end

### Requirement: A valid runway crossing cuts the path and clears the airplane
On a valid crossing, the path SHALL end at the zone entry point, further drag input for that path SHALL be ignored, and the airplane SHALL be marked as cleared to land.

#### Scenario: Path stops at the zone
- **WHEN** an airplane path validly crosses a landing zone
- **THEN** the path ends at the zone and later drag points are ignored

### Requirement: The cut-off is shown with a white flash
When a path is cut off by a landing zone, the path SHALL flash white briefly. The path stopping SHALL be the second cue, so the signal does not rely on colour alone. The flash duration and colour SHALL come from design-system tokens.

#### Scenario: Flash on landing clearance
- **WHEN** a path is cut off by a landing zone
- **THEN** the path flashes white for the configured duration and stays ended

#### Scenario: Reduced motion
- **WHEN** the device has reduced motion enabled and a path is cut off by a landing zone
- **THEN** the path does not flash and instead shows a steady changed state

### Requirement: Airplanes roll out after touchdown
After touchdown an airplane SHALL keep moving along the runway in its landing direction for two thirds of the runway length, then fade out and be removed. The rollout fraction, the fade duration and the rollout speed (per aircraft type) SHALL come from configuration. In a two-way runway, the zone that was hit SHALL decide the rollout direction.

#### Scenario: Rollout distance
- **WHEN** an airplane touches down on a runway
- **THEN** it travels two thirds of the runway length along the landing direction

#### Scenario: Fade and removal
- **WHEN** the rollout distance is covered
- **THEN** the airplane fades out over the configured duration and is then removed

#### Scenario: Two-way rollout direction
- **WHEN** airplanes land on the two ends of a two-way runway
- **THEN** each rolls out in the landing direction of the zone it landed in

### Requirement: Rolling airplanes can collide
An airplane that is rolling out SHALL count as an aircraft for collisions, and a collision then SHALL be a failure like any other.

#### Scenario: Opposite landings meet mid-runway
- **WHEN** two airplanes land at the same time from opposite ends of a two-way runway and come closer than the minimum separation
- **THEN** a collision failure occurs
