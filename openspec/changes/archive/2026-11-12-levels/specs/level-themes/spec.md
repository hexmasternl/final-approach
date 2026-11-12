## ADDED Requirements

### Requirement: Meadow theme
Level one SHALL be drawn with green surroundings: trees around the airfield and grassy fields in the near proximity of the airfield. It SHALL contain a fenced paddock with a farmer and two cows, and the cows SHALL stroll around inside the fence without leaving it.

#### Scenario: Green surroundings
- **WHEN** level one is shown
- **THEN** trees surround the airfield and the ground near the airfield is grass

#### Scenario: Paddock
- **WHEN** level one is shown
- **THEN** a fenced area holds a farmer and two cows, clear of every runway and helipad

#### Scenario: Cows stay fenced
- **WHEN** level one runs for any length of time
- **THEN** the cows move slowly and casually and never cross the fence

### Requirement: Birds in the meadow
Level one SHALL occasionally send a bird across the screen. A bird SHALL fly lower than aircraft, so it is drawn beneath them, and SHALL NOT take part in collisions or any other game rule.

#### Scenario: Bird crosses
- **WHEN** level one runs for a while
- **THEN** a bird now and then enters at one edge and leaves at another, and no more than one bird is on screen at a time

#### Scenario: Bird is below aircraft
- **WHEN** a bird and an aircraft overlap on screen
- **THEN** the aircraft is drawn over the bird

#### Scenario: Bird is not an obstacle
- **WHEN** a bird passes an aircraft at any distance
- **THEN** nothing happens to either

### Requirement: Island theme
Level two SHALL be drawn as water with an island centred on the screen, and the airport SHALL cover the island: every runway and helipad lies on the island.

#### Scenario: Centred island
- **WHEN** level two is shown
- **THEN** water fills the screen around the island and the island's centre is the centre of the world

#### Scenario: Airport on the island
- **WHEN** level two is shown
- **THEN** every runway and helipad lies fully on the island

### Requirement: Boats on the island level
Level two SHALL now and then show a boat floating by on the water. There SHALL be three kinds of boat: a yacht, a sailboat and a larger container vessel. A boat SHALL stay on the water, SHALL be drawn beneath aircraft, and SHALL NOT take part in collisions or any other game rule.

#### Scenario: Boat floats by
- **WHEN** level two runs for a while
- **THEN** boats pass now and then, each one a yacht, a sailboat or a container vessel, and over many passes all three kinds appear

#### Scenario: Boat stays on water
- **WHEN** a boat is on screen
- **THEN** it does not overlap the island

#### Scenario: Container vessel is larger
- **WHEN** a container vessel and a yacht are drawn
- **THEN** the container vessel is drawn larger than the yacht and the sailboat

#### Scenario: Boat is not an obstacle
- **WHEN** a boat and an aircraft overlap on screen
- **THEN** the aircraft is drawn over the boat and nothing else happens

### Requirement: Savanna dawn lighting
Level three SHALL be drawn as a savanna at dawn, in dim light that reveals the airfield, using a dawn palette defined in the design system. Panels, chips and aircraft SHALL stay light, as in the Night theme.

#### Scenario: Dim dawn
- **WHEN** level three is shown
- **THEN** the ground and sky use the dawn palette and are visibly dimmer than levels one and two

#### Scenario: HUD stays light
- **WHEN** level three is shown
- **THEN** panels and chips keep their light surfaces

### Requirement: Airfield lights
Every runway of level three SHALL have airfield ground lighting animated as a running light: lights along the runway edges that light up one after another in the direction of the runway, repeating. The control tower SHALL have a red light that flashes.

#### Scenario: Running light
- **WHEN** level three runs
- **THEN** the lights along each runway light up in sequence along its length and repeat

#### Scenario: Tower beacon
- **WHEN** level three runs
- **THEN** the red light on the control tower flashes on and off

### Requirement: Gentle lights
Every light SHALL stay below a maximum brightness set in the design system, and no light SHALL flash or pulse more than twice per second, so lights never annoy the player.

#### Scenario: Brightness cap
- **WHEN** any light of level three is at its brightest
- **THEN** its opacity does not exceed the maximum light opacity token

#### Scenario: Flash rate cap
- **WHEN** the tower beacon and the running light are configured
- **THEN** neither has a cycle shorter than half a second

### Requirement: Aircraft lights in the savanna
Aircraft drawn in level three SHALL show navigation lights (red left, green right, white tail) and a headlight at the nose. Aircraft in levels one and two SHALL NOT show them.

#### Scenario: Lit aircraft at dawn
- **WHEN** an aircraft is drawn in level three
- **THEN** it shows a red light on its left, a green light on its right, a white tail light and a headlight

#### Scenario: Unlit in daylight
- **WHEN** an aircraft is drawn in level one or two
- **THEN** it shows no navigation lights or headlight

### Requirement: Decorated savanna
Level three SHALL be decorated so the airfield looks pleasant: at least acacia trees, bushes and rocks outside the landing zones, and a control tower.

#### Scenario: Decorations clear of zones
- **WHEN** level three is shown
- **THEN** decorations are present and none of them overlaps a runway or helipad

### Requirement: Reduced motion
When the device asks for reduced motion, cows, birds, boats, the running light and the beacon SHALL hold still: cows stand in place, no bird or boat appears, the running light is shown fully lit and the beacon is shown steadily on.

#### Scenario: Reduced motion on
- **WHEN** reduced motion is on and any level is shown
- **THEN** nothing in the scenery moves or blinks, and the level can still be started and played

### Requirement: Scenery never covers landing zones
Scenery SHALL be drawn beneath zone glows, paths and aircraft, and no scenery SHALL cover a runway or helipad.

#### Scenario: Draw order
- **WHEN** a level is drawn
- **THEN** the field and scenery are below the zone glows, paths, aircraft, and the HUD, in the layer order of the design system
