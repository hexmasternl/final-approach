# Spec Delta

## MODIFIED Requirements

### Requirement: Zoomed-out worlds
Every level SHALL use a world 2.5 times as wide and as high as the former 1280 × 720 world units, so 3200 × 1800 world units, and the zoom factor SHALL be one configuration value shared by all levels.

#### Scenario: World size
- **WHEN** the world size of each of the three levels is read
- **THEN** it is 3200 × 1800 world units, derived from the single zoom factor

#### Scenario: More surroundings on screen
- **WHEN** a level is shown on any screen
- **THEN** the runways and helipads are half as large on screen as at the former zoom of 1.25, and there is wide open ground or water between the airfield and every screen edge

### Requirement: Aircraft keep their on-screen motion
Aircraft speeds, the warning distance, the spawn separation, the spawn and leave margins, the path reach radius, the minimum path point spacing, the path smoothing tolerance and the runway touchdown distance SHALL be multiplied by the zoom factor. Turn rates, timings and the landing alignment tolerance SHALL stay unchanged.

#### Scenario: Same crossing time
- **WHEN** an aircraft of any type flies straight across the screen
- **THEN** it takes the same time as before the zoom-out

#### Scenario: Speed order kept
- **WHEN** the default speeds are read
- **THEN** speed still rises from helicopter to jumbo, each type at least 15% faster than the one before

#### Scenario: Same warning on screen
- **WHEN** two aircraft get close
- **THEN** the proximity warning starts at the same on-screen distance as before the zoom-out

#### Scenario: Lead-in stays flyable
- **WHEN** an airplane of any type enters a runway zone at its edge, at the limit of the alignment tolerance
- **THEN** its lead-in to the touchdown point turns no tighter than its turning radius and ends on the centre line

### Requirement: Airfield readable on a phone
Runways, helipads and their markings SHALL shrink with the airfield: threshold bars, centre line, touchdown patches, runway numbers, the helipad ring and the H SHALL be drawn at the airfield's scale so they always fit their runway or helipad. Flight paths SHALL keep their on-screen stroke, dash and dot sizes.

#### Scenario: Runway width on a phone
- **WHEN** a level is shown on an 800 × 360dp screen
- **THEN** every runway is drawn 12.8dp wide and every helipad 22.4dp across, half their size at the former zoom, with their markings inside them

#### Scenario: Paths unchanged
- **WHEN** a path is drawn after the zoom-out
- **THEN** its stroke, casing, dash, gap and end dot are the same on-screen sizes as before

### Requirement: Layouts fit the larger worlds
Each level SHALL keep its runways, helipads and their relative arrangement, placed in the middle of the larger world, with every zone inside the world and clear of the HUD band. Its scenery SHALL reach out into the new surroundings, with roads and tracks running on to the world edges.

#### Scenario: Layouts validate
- **WHEN** each level config is validated
- **THEN** it has no problems, every zone lies inside the world and below the HUD band, and the runway and helipad counts, headings and accepted aircraft are unchanged

#### Scenario: Surroundings are not bare
- **WHEN** a land level is shown
- **THEN** scenery stands in the surroundings on every side of the airfield, not only next to it
