# Spec Delta

## MODIFIED Requirements

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

## ADDED Requirements

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
