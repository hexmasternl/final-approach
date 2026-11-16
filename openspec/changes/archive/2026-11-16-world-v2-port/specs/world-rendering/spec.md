# Spec Delta

## Purpose

Defines how the app draws the ground, the surrounds of landing zones and the scenery of a level from the design system, so the world looks like the showcase, stays behind aircraft, paths and landing zones, and never affects play.

## ADDED Requirements

### Requirement: Scenery is drawn from the design system's sprites
Every scenery sprite the game draws SHALL be generated from the design system's current world sprites, with their three-tone materials, tinted inner lines, clip shapes and height tier. The app SHALL hold no other source of scenery shapes.

#### Scenario: Generated sprite data
- **WHEN** the design-system generator is run
- **THEN** the app's scenery sprite data is written from the `WORLD` sprites of `bundle.js`, and running it a second time changes nothing

#### Scenario: Version 1 source is gone
- **WHEN** the repository is searched for `tools/scenery-v1.js`
- **THEN** the file does not exist and nothing refers to it

#### Scenario: Sprite matches the showcase
- **WHEN** a scenery sprite is drawn in the game next to the same sprite in the showcase, in the same theme
- **THEN** its shapes, tones, inner lines and outline are the same

### Requirement: One layer of hard ground shadows
Every hard ground shadow SHALL be drawn in one layer between flat ground and standing scenery, in the `shadow-cast` colour at the `opacity-shadow-cast` opacity. Flat sprites SHALL cast no shadow.

#### Scenario: Shadow order
- **WHEN** a level is drawn
- **THEN** every scenery shadow lies above roads, fields, water, aprons, runways and helipads, and below every building, tree, vehicle, animal and person

#### Scenario: Overlapping shadows
- **WHEN** the shadows of two neighbouring trees overlap
- **THEN** the overlap is no darker than either shadow alone, and no shadow is drawn on the other tree's crown

#### Scenario: Flat sprites
- **WHEN** a wave, a wake or a crop tile is drawn
- **THEN** it has no shadow

### Requirement: Shadows fall down-right whatever way a sprite faces
A top-down sprite (vehicle, animal, person, boat, rock, bush, hay bale, hedge or fence piece) SHALL be drawn facing the way its level data or its motion says, and its shadow SHALL fall down-right of it by the throw of its height tier.

#### Scenario: Rotated vehicle
- **WHEN** a car is placed facing east and another facing south
- **THEN** each car faces its own way, and both shadows are offset to the lower right by the same distance

#### Scenario: Moving ambient life
- **WHEN** a cow turns while it walks
- **THEN** its shadow stays to its lower right

### Requirement: Standing sprites do not rotate
Buildings, trees, pines, palms, acacias, the silo, the control tower and the windsock SHALL be drawn upright with their south wall, trunk or post towards the bottom of the screen. A level that gives one of them a rotation SHALL be rejected by level validation.

#### Scenario: Buildings face south
- **WHEN** any level is shown
- **THEN** every building shows its roof above a south-facing wall, and none is turned

#### Scenario: Rotated building rejected
- **WHEN** a level config places a terminal with a rotation other than zero
- **THEN** level validation reports that item as a problem

### Requirement: Flat ground has no navy outline
Roads, tracks, taxiways, aprons, car parks, fields, ponds, waterholes, paddock ground and an island's shore SHALL be edged in a darker tone of their own material and SHALL NOT be stroked in the `outline` colour. Runways and helipads SHALL be the only ground shapes stroked in `outline`.

#### Scenario: Ground edges
- **WHEN** each level is shown
- **THEN** the only navy lines on the ground are the outlines of runways and helipads

### Requirement: Ground pieces have their transition bands
Where two ground materials meet, the band the design system gives for that piece SHALL lie between them: kerb and verge along a road, an edge along a track, edge and verge around a taxiway, an apron, a car park and a field, shore, sand and damp grass around a pond or waterhole, and beach, shallows and foam around an island.

#### Scenario: A road
- **WHEN** a road crosses grass
- **THEN** a kerb in the road's darker tone and a rough-grass verge lie between the road surface and the grass on both sides

#### Scenario: A pond
- **WHEN** a pond lies in grass
- **THEN** shallows, a shore line, a sand band and a damp-grass band lie between open water and the grass

#### Scenario: The island shore
- **WHEN** the island level is shown
- **THEN** a sand beach, lighter shallows and a broken foam line lie between the island's grass and the open sea

### Requirement: Mown airfield, unmown countryside
The ground inside a level's airfield SHALL be mown in stripes parallel to its main runway. Ground outside the airfield SHALL NOT be striped: it is one flat tone with irregular patches, and small details (tufts, flowers, reeds) SHALL appear only in clusters outside the airfield, off roads, fields and landing zones.

#### Scenario: Stripes follow the runway
- **WHEN** a level whose main runway is turned is shown
- **THEN** the stripes inside the airfield run parallel to that runway

#### Scenario: Countryside is not striped
- **WHEN** each level is shown
- **THEN** no stripe lies outside the airfield, and no patch, tuft or flower lies inside it

#### Scenario: Details keep clear
- **WHEN** each level is shown
- **THEN** no tuft, flower or reed lies on a runway, a helipad, a road or a field

### Requirement: Helipad construction
A helipad SHALL keep its disc at the size and place of its level data and SHALL be drawn on a square concrete pad with slab joints and a light in each corner, inside a rough-grass fringe. The disc SHALL show a worn ring, the warning-colour ring and the H.

#### Scenario: Same footprint
- **WHEN** a helipad is drawn
- **THEN** its outlined disc has the radius and centre of its level data

#### Scenario: Pad around the disc
- **WHEN** a helipad is drawn
- **THEN** a square concrete pad with joints, four corner lights and a rough-grass fringe surround the disc

### Requirement: Taxiways do not look like runways
A taxiway SHALL be drawn in a lighter asphalt than a runway, with a yellow centre line, a concrete edge and a rough-grass verge, and without a navy outline.

#### Scenario: Taxiway beside a runway
- **WHEN** a taxiway meets a runway
- **THEN** the taxiway is the lighter of the two, carries a yellow centre line and has no navy outline

### Requirement: Aircraft, paths and landing zones read first
Scenery SHALL NOT use an aircraft colour or a path colour, SHALL NOT carry a line as heavy as an aircraft's outline, and SHALL NOT draw attention away from aircraft, flight paths or landing zones in any level.

#### Scenario: Design review of each level
- **WHEN** the design-critic agent reviews screenshots of each level in its theme, with aircraft and flight paths over the busiest scenery
- **THEN** its report confirms that aircraft, flight paths and landing zones read before any scenery, and names no violation of the world rules W1 to W9

### Requirement: The world never affects play
Porting the world SHALL NOT change the simulation: runways, helipads, landing zones and every engine value stay as they are.

#### Scenario: Same seeded run
- **WHEN** a seeded run of each level is played before and after this change
- **THEN** spawns, landings, collisions and score are the same

#### Scenario: Engine untouched
- **WHEN** the change is compared with its base
- **THEN** no file of the engine differs

### Requirement: World drawing uses current tokens only
The code that draws the world (scenery sprites, ground, runways, helipads, level backdrops, ambient life and level miniatures) SHALL read no design token that is marked deprecated.

#### Scenario: No version 1 token in the world
- **WHEN** the world-drawing files are searched for tokens whose documentation says "v1. Use ... in new code."
- **THEN** none is read
