# Spec Delta

## Purpose

Defines how much of the world a level shows and which world-unit values follow that zoom, so the airfield gains surroundings while aircraft stay as large, as fast and as easy to pick on screen as before.

## ADDED Requirements

### Requirement: Zoomed-out worlds
Every level SHALL use a world 1.25 times as wide and as high as the former 1280 × 720 world units, so 1600 × 900 world units, and the zoom factor SHALL be one configuration value shared by all levels.

#### Scenario: World size
- **WHEN** the world size of each of the three levels is read
- **THEN** it is 1600 × 900 world units, derived from the single zoom factor

#### Scenario: More surroundings on screen
- **WHEN** a level is shown on any screen
- **THEN** the runways and helipads take a smaller share of the screen than before, and there is visible ground or water between the airfield and every screen edge

### Requirement: Aircraft keep their on-screen size
Every aircraft-sized world value (type radius, and with it sprite size and collision shape) SHALL be multiplied by the zoom factor, so each aircraft type is drawn at the same on-screen size as before the zoom-out on the same screen.

#### Scenario: Same drawn size
- **WHEN** an aircraft of any type is drawn on an 800 × 360dp screen
- **THEN** its sprite is as large on screen as it was in the 1280 × 720 world on that screen

#### Scenario: Collision shape follows the sprite
- **WHEN** the collision shape of any type is compared with its drawn sprite
- **THEN** it matches the sprite as closely as before the zoom-out

### Requirement: Aircraft keep their on-screen motion
Aircraft speeds, the warning distance, the spawn separation, the spawn and leave margins, the path reach radius and the minimum path point spacing SHALL be multiplied by the zoom factor. Turn rates, timings and the landing alignment tolerance SHALL stay unchanged.

#### Scenario: Same crossing time
- **WHEN** an aircraft of any type flies straight across the screen
- **THEN** it takes the same time as before the zoom-out

#### Scenario: Speed order kept
- **WHEN** the default speeds are read
- **THEN** speed still rises from helicopter to jumbo, each type at least 15% faster than the one before

#### Scenario: Same warning on screen
- **WHEN** two aircraft get close
- **THEN** the proximity warning starts at the same on-screen distance as before the zoom-out

### Requirement: Pick area stays finger-sized
The pick area of every aircraft SHALL stay at least 72dp on screen in every level after the zoom-out.

#### Scenario: Pick on a phone
- **WHEN** the player taps 36dp from the centre of the smallest aircraft on an 800 × 360dp screen
- **THEN** that aircraft is selected

### Requirement: HUD band keeps its screen height
The band at the top of the world kept clear of landing zones and spawns for the HUD SHALL be multiplied by the zoom factor, so it covers the same screen height as before.

#### Scenario: HUD band on screen
- **WHEN** a level is shown on any screen
- **THEN** no runway, helipad or landing zone lies under the HUD band, and the band is as tall on screen as before

### Requirement: Airfield readable on a phone
Runways, helipads and their numbers and markings SHALL stay readable on an 800 × 360dp screen, and flight paths SHALL keep their on-screen stroke, dash and dot sizes.

#### Scenario: Runway width on a phone
- **WHEN** a level is shown on an 800 × 360dp screen
- **THEN** every runway is drawn at least 24dp wide and every helipad at least 40dp across, with its number or H legible

#### Scenario: Paths unchanged
- **WHEN** a path is drawn after the zoom-out
- **THEN** its stroke, casing, dash, gap and end dot are the same on-screen sizes as before

### Requirement: Layouts fit the larger worlds
Each level SHALL keep its runways, helipads and their relative arrangement, placed in the middle of the larger world, with every zone inside the world and clear of the HUD band.

#### Scenario: Layouts validate
- **WHEN** each level config is validated
- **THEN** it has no problems, every zone lies inside the world and below the HUD band, and the runway and helipad counts, headings and accepted aircraft are unchanged
