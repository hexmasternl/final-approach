# Spec Delta

## Purpose

Defines what the scenery and surroundings of each level look like and how detailed they may be, so every object reads at a glance while staying decoration that never competes with aircraft, paths or landing zones.

## ADDED Requirements

### Requirement: Every scenery element is recognisable
Every scenery element of every level SHALL be recognisable without explanation, in the cartoon style of the design system: dark-navy outlines with round joins, flat single fills, no gradients or textures.

#### Scenario: Design review per level and theme
- **WHEN** screenshots of each level in Day and in Night, and of the savanna in Dawn, are reviewed by the design-critic agent
- **THEN** the review names every scenery element correctly without being told what it is, and reports no gradient, texture or unoutlined shape

### Requirement: Cows
Each cow SHALL be drawn seen from the top with a body with patches, a head with ears, legs and a tail.

#### Scenario: Cow in the paddock
- **WHEN** level one is shown
- **THEN** each cow in the paddock shows a patched body, a head with ears, legs and a tail

### Requirement: Farmer
The farmer SHALL be drawn seen from the top with a hat, arms and a pitchfork or a bucket.

#### Scenario: Farmer in the paddock
- **WHEN** level one is shown
- **THEN** the farmer shows a hat, two arms and a pitchfork or a bucket

### Requirement: Birds fly as a flock
A bird SHALL be drawn with a body and two wings that flap, and birds SHALL cross the screen as a small flock of three.

#### Scenario: Flock crosses
- **WHEN** a bird crossing happens in level one
- **THEN** three birds fly together, each with a body and two wings that move up and down

### Requirement: Detailed airfield buildings
The terminal SHALL show windows and a roof edge, the control tower a cab, and the hangar doors and roof ribbing.

#### Scenario: Buildings in every level
- **WHEN** any level with a terminal, tower or hangar is shown
- **THEN** the terminal shows windows and a roof edge, the tower a cab, and the hangar doors and roof ribs

### Requirement: Windsock
The windsock SHALL be drawn as a pole with a striped sock, as in the design system's Windsock component.

#### Scenario: Windsock beside the runway
- **WHEN** a level with a windsock is shown
- **THEN** the windsock shows a pole and a sock with alternating stripes

### Requirement: Parked vehicles and aircraft on the apron
Aprons SHALL hold parked vehicles and parked aircraft. A parked aircraft SHALL be drawn smaller than the smallest flying aircraft, without livery colour, without a shadow, and only on an apron, so it is never taken for an aircraft in play.

#### Scenario: Apron life
- **WHEN** a level with an apron is shown
- **THEN** the apron shows at least one parked vehicle and one parked aircraft

#### Scenario: Parked aircraft stays apart
- **WHEN** a parked aircraft and a flying light prop are on screen together
- **THEN** the parked aircraft is smaller, has no path colour and no shadow, and cannot be selected

### Requirement: Detailed trees and plants
Trees SHALL be drawn with a canopy of leaf clusters and a ground shadow; palms with fronds; acacias with a flat-topped canopy; bushes with leaf clusters.

#### Scenario: Trees
- **WHEN** a level with trees, palms, acacias or bushes is shown
- **THEN** each tree has leaf clusters and a shadow, each palm has fronds, and each acacia has a flat-topped canopy

### Requirement: Meadow surroundings
Level one SHALL surround its airfield with crop fields with rows, hedges, fences, a farmhouse, a road, water and a forest edge, besides the paddock.

#### Scenario: Meadow place
- **WHEN** level one is shown
- **THEN** crop fields with visible rows, hedges, fences, a farmhouse, a road, water and a forest edge lie around the airfield

### Requirement: Island surroundings
Level two SHALL show a beach around the island, waves along the shore and palms, and its boats SHALL show a deck and a wake.

#### Scenario: Island place
- **WHEN** level two is shown
- **THEN** the island has a beach, waves along its shore and palms

#### Scenario: Boat detail
- **WHEN** a boat passes in level two
- **THEN** it shows a deck and a wake behind it in its direction of travel

### Requirement: Savanna surroundings
Level three SHALL surround its airfield with acacias, bushes, rocks and animals at the same level of detail as level one.

#### Scenario: Savanna place
- **WHEN** level three is shown
- **THEN** acacias, bushes, rocks and animals lie around the airfield, each with recognisable detail

### Requirement: Scenery stays apart from aircraft
Scenery SHALL NOT use the path colours, SHALL NOT cast the airborne shadow, and SHALL draw its outline thinner than an aircraft's outline, so it can never be mistaken for an aircraft, a path or a landing zone.

#### Scenario: Colour and outline
- **WHEN** any scenery element is drawn
- **THEN** it uses no `path-1` to `path-6` colour, has no airborne shadow, and its outline is thinner than the `line-3` aircraft outline

### Requirement: Scenery is no busier than aircraft
A scenery sprite SHALL use no more fill colours than an aircraft sprite, and its inner detail strokes SHALL use the design system's finer detail line.

#### Scenario: Sprite budget
- **WHEN** any scenery sprite source is compared with the aircraft sprites
- **THEN** it uses no more fill colours than the light prop, and every inner detail stroke uses the finer detail line token

### Requirement: The field stays clear
Scenery SHALL NOT cover a runway, a helipad or a landing zone, and only flat ground (lawn, fields, water, sand) SHALL lie under the HUD band.

#### Scenario: Zones clear
- **WHEN** each level config is validated
- **THEN** no scenery item overlaps a runway, a helipad or a landing zone

#### Scenario: HUD band clear
- **WHEN** each level is shown
- **THEN** no building, tree, vehicle, animal or person is drawn under the HUD band

### Requirement: Scenery comes from the design system
Every scenery sprite SHALL have an SVG source in the design system and appear in the showcase in Day and Night, and every colour, stroke width and size used to draw it SHALL come from theme tokens or level configuration, not literals in drawing code.

#### Scenario: Source and showcase
- **WHEN** a scenery sprite is drawn in the game
- **THEN** a matching SVG exists in `docs/design-system/assets/scenery/`, the showcase shows it in Day and Night, and `world.md` states the rules for how detailed scenery may be compared with aircraft

#### Scenario: No literals
- **WHEN** the scenery drawing code is reviewed
- **THEN** it contains no colour, stroke-width or size literal

### Requirement: Night stays readable
Every new scenery colour SHALL have a Night value, and scenery SHALL stay recognisable in the Night theme.

#### Scenario: Night screenshots
- **WHEN** each level is drawn in the Night theme
- **THEN** every scenery element is still recognisable and its outline still contrasts with the ground

### Requirement: Animated details respect reduced motion
When the device asks for reduced motion, animated scenery details (wing flaps, birds, boats and wakes, cows walking) SHALL hold still or not appear, as for the other ambient life.

#### Scenario: Reduced motion
- **WHEN** reduced motion is on and any level is shown
- **THEN** no wing flaps, no bird or boat appears, and no scenery detail moves

### Requirement: Scenery is decoration only
Scenery and ambient life SHALL have no effect on the game: no game rule reads them.

#### Scenario: Same run without scenery
- **WHEN** the same seeded run is played once with a level's scenery and once with it removed
- **THEN** spawns, landings, collisions and score are identical
