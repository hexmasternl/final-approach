# landing-zones Specification

## Purpose
TBD - created by archiving change landing-zones. Update Purpose after archive.

## Requirements

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
A helicopter that is cleared to land SHALL follow its path to the end and then perform a landing sequence of 3 seconds (configurable) on the spot, in which it descends and lands. When the sequence ends the helicopter SHALL count as landed and be removed from the game, with no rollout.

#### Scenario: Helicopter touches down
- **WHEN** a cleared helicopter reaches the end of its path
- **THEN** a 3 second landing sequence starts on the helipad, and when it ends the helicopter has landed and is removed

#### Scenario: Descent is visible
- **WHEN** a helicopter is in its landing sequence
- **THEN** it is drawn descending towards its ground shadow, and with reduced motion it is drawn in a still landing pose for the whole sequence

#### Scenario: Landing counts at the end
- **WHEN** the landing sequence of a helicopter ends
- **THEN** the landing is counted and scored once, and not before

#### Scenario: Duration comes from configuration
- **WHEN** the landing sequence duration in the level configuration is changed
- **THEN** the sequence lasts that long and no other value is used

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
On a valid crossing, the path SHALL end on the runway centre line instead of at the zone entry point, further drag input for that path SHALL be ignored, and the airplane SHALL be marked as cleared to land. The last point of the path SHALL lie on the centre line of the zone that was entered, and the path's final direction SHALL be that zone's landing direction.

#### Scenario: Path stops at the zone
- **WHEN** an airplane path validly crosses a landing zone
- **THEN** the path ends at the zone and later drag points are ignored

#### Scenario: Off-centre entry ends on the centre line
- **WHEN** an airplane path validly enters a runway landing zone at any point across its width, including near either side edge
- **THEN** the last point of the path lies on the runway centre line

#### Scenario: Final heading matches the landing direction
- **WHEN** an airplane path validly enters a runway landing zone at any angle within the alignment tolerance
- **THEN** the last segment of the path points along that zone's landing direction

#### Scenario: Two-way runway uses the entered end
- **WHEN** an airplane path validly enters either end zone of a two-way runway
- **THEN** the path ends on the centre line pointing in the landing direction of the zone it entered

#### Scenario: Invalid crossing is not snapped
- **WHEN** an airplane path crosses a runway landing zone outside the alignment tolerance, or crosses the middle of a runway
- **THEN** the path is not snapped, the airplane is not cleared to land, and the path continues as drawn

### Requirement: The cut-off is shown with a white flash
When a path is cut off by a landing zone, the path SHALL flash white briefly. The path stopping SHALL be the second cue, so the signal does not rely on colour alone. The flash duration and colour SHALL come from design-system tokens.

#### Scenario: Flash on landing clearance
- **WHEN** a path is cut off by a landing zone
- **THEN** the path flashes white for the configured duration and stays ended

#### Scenario: Reduced motion
- **WHEN** the device has reduced motion enabled and a path is cut off by a landing zone
- **THEN** the path does not flash and instead shows a steady changed state

### Requirement: Airplanes roll out after touchdown
After touchdown an airplane SHALL keep moving along the runway in its landing direction for two thirds of the runway length, then fade out and be removed. The rollout fraction and the fade duration SHALL come from configuration. The rollout speed SHALL follow the shared landing speed rules. In a two-way runway, the zone that was hit SHALL decide the rollout direction.

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
- **WHEN** two airplanes land at the same time from opposite ends of a two-way runway and their collision shapes touch
- **THEN** a collision failure occurs

### Requirement: The snapped path ends with a smooth lead-in
The end of a snapped path SHALL be a lead-in that leaves the drawn path without a corner and joins the runway centre line in the landing direction. The lead-in SHALL replace the end of the drawn path, and the stored and shown path SHALL be the snapped path.

#### Scenario: Lead-in has no kink
- **WHEN** an airplane path is snapped to the centre line
- **THEN** the direction of the path changes gradually where the lead-in begins and along the lead-in, with no sharp corner

#### Scenario: Lead-in is flyable
- **WHEN** the drawn path runs straight into a runway landing zone for at least the lead-in length, at any point across the zone width and any angle within the alignment tolerance
- **THEN** no turn on the snapped path is tighter than that airplane type's turning radius (its speed divided by its turn rate)

#### Scenario: Short drawn path
- **WHEN** the drawn path before the zone is shorter than the lead-in length
- **THEN** the lead-in starts at the airplane's current position and heading, and the path still ends on the centre line in the landing direction

#### Scenario: Player sees the snapped end
- **WHEN** an airplane path is snapped to the centre line
- **THEN** the path drawn on screen is the snapped path, including the lead-in, and the cut-off flash plays on it

### Requirement: Airplanes touch down and roll out on the centre line
A cleared airplane SHALL touch down at the last point of its snapped path, on the runway centre line, at the configured touchdown distance from the threshold, or level with the point where the path entered the zone when that is farther in. Its rollout SHALL run along the centre line in the landing direction.

#### Scenario: Touchdown on the centre line near the threshold
- **WHEN** a cleared airplane reaches the end of its snapped path
- **THEN** it touches down on the runway centre line, within the landing zone's depth from the threshold

#### Scenario: Rollout stays on the centre line
- **WHEN** an airplane rolls out after touchdown
- **THEN** every position of its rollout lies on the runway centre line

### Requirement: Speed changes to the landing speed over the deceleration time
At touchdown an airplane SHALL move at its flight speed, and its speed SHALL change steadily to the landing speed over the deceleration time, which comes from configuration and defaults to 1 second. This SHALL hold for every airplane type, the light prop included.

#### Scenario: Speed at touchdown
- **WHEN** a jet touches down
- **THEN** its speed at that moment is its flight speed

#### Scenario: Landing speed reached after 1 second
- **WHEN** a light prop, a turboprop, a jet or a jumbo has been rolling out for 1 second, with the default configuration
- **THEN** its speed equals half of the light prop's flight speed

#### Scenario: Rollout ends during the slowdown
- **WHEN** the rollout distance is covered before the deceleration time has passed
- **THEN** the airplane stops moving and fades out as usual

### Requirement: All airplanes share one landing speed, scaled by a factor
Every airplane SHALL roll out at one shared landing speed: the effective flight speed, in the current run, of a reference aircraft type, multiplied by a landing speed factor. Both the reference type and the factor SHALL come from configuration. The reference type SHALL default to the light prop and the factor SHALL default to one half. The rollout length SHALL NOT depend on the landing speed.

#### Scenario: Light prop rolls out at half its flight speed
- **WHEN** a light prop has been rolling out for more than the deceleration time, with the default configuration
- **THEN** it moves at half of the light prop's flight speed and keeps that speed until the rollout distance is covered

#### Scenario: Faster airplanes roll out at half the light prop's speed
- **WHEN** a turboprop, a jet or a jumbo has been rolling out for more than the deceleration time, with the default configuration
- **THEN** it moves at half of the light prop's flight speed and keeps that speed until the rollout distance is covered

#### Scenario: Reference type from configuration
- **WHEN** a level configures the turboprop as the landing speed reference
- **THEN** every airplane rolls out at half of the turboprop's flight speed once the deceleration time has passed

#### Scenario: Factor from configuration
- **WHEN** a level configures a landing speed factor of 1
- **THEN** every airplane rolls out at the reference type's full flight speed once the deceleration time has passed

#### Scenario: Rollout length unchanged
- **WHEN** an airplane touches down with the default configuration
- **THEN** it still rolls two thirds of the runway length before it fades out, and takes longer to do so than at the reference type's full flight speed
