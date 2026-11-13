## MODIFIED Requirements

### Requirement: Zoomed-out worlds
Every level SHALL use a world 1.5 times as wide and as high as the former 1280 x 720 world units, so 1920 x 1080 world units, and the zoom factor SHALL be one configuration value shared by all levels.

#### Scenario: World size
- **WHEN** the world size of each of the three levels is read
- **THEN** it is 1920 x 1080 world units, derived from the single zoom factor

#### Scenario: More surroundings on screen
- **WHEN** a level is shown on any screen
- **THEN** the runways and helipads take a smaller share of the screen than before, and there is visible ground or water between the airfield and every screen edge

#### Scenario: Middle ground
- **WHEN** the zoom factor is compared with the original (1) and with a doubled world (2)
- **THEN** it lies exactly halfway between them

### Requirement: Aircraft keep their on-screen motion
Aircraft speeds, the warning distance, the spawn separation, the spawn and leave margins, the path reach radius and the minimum path point spacing SHALL be multiplied by the zoom factor. Turn rates, timings and the landing alignment tolerance SHALL stay unchanged.

#### Scenario: Same crossing time
- **WHEN** an aircraft of any type flies straight across the screen
- **THEN** it takes the same time as it would at zoom factor 1

#### Scenario: Speed order kept
- **WHEN** the default speeds are read
- **THEN** each type's speed equals its zoom-1 default times the zoom factor, and the order and gaps required by the aircraft types stay as they are at zoom factor 1

#### Scenario: Same warning on screen
- **WHEN** two aircraft get close
- **THEN** the proximity warning starts at the same on-screen distance as at zoom factor 1
