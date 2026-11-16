# Spec Delta

## ADDED Requirements

### Requirement: Windows light up in Night and Dawn
The windows of buildings and the control tower SHALL be drawn in the design system's window colour: glass in the Day theme and a warm light in the Night and Dawn themes.

#### Scenario: Day
- **WHEN** a level is drawn in the Day theme
- **THEN** building windows are glass

#### Scenario: Dawn
- **WHEN** level three is shown
- **THEN** the windows of its buildings and control tower are lit in a warm light

### Requirement: Levels look like their reference screens
Each level, in its theme, SHALL show the ground, the landing-zone construction and the surroundings of its game screen in the design system showcase. The place, size and direction of its runways and helipads SHALL follow the level's own layout, not the screen.

#### Scenario: Meadow
- **WHEN** level one is shown in the Day theme next to the Meadow game screen
- **THEN** it shows the same kinds of things in the same style: a mown airfield with apron, taxiways, tower, terminal, hangar and windsock, and around it a road, a farm with a farmhouse, barn and silo, crop fields with hay bales, a hedge row, a pond, the paddock, and woods of trees and pines

#### Scenario: Island
- **WHEN** level two is shown in the Day theme next to the Island game screen
- **THEN** it shows the same kinds of things in the same style: sea with darker patches, a beach with shallows and foam, a mown airfield with apron, tower and terminal, palms, bushes and rocks, a jetty and waves

#### Scenario: Savanna
- **WHEN** level three is shown in the Dawn theme next to the Savanna game screen
- **THEN** it shows the same kinds of things in the same style: dry ground with sand patches, a mown airfield with apron, tower and hangar, a dirt track, a waterhole, acacias, bushes, rocks, zebras and an elephant, with tufts and no flowers

#### Scenario: Layout is the level's own
- **WHEN** a level is compared with its game screen
- **THEN** its runways and helipads are where its level layout puts them, and its landing rules are unchanged
