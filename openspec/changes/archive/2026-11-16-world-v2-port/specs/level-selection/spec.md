# Spec Delta

## MODIFIED Requirements

### Requirement: One square tile per level
The page SHALL show one square tile for each level, each containing a miniature of that level's airfield: a crop of the level's own field as the game draws it, with its ground, runways, helipads and scenery, and without aircraft, flight paths, clouds or moving ambient life. The crop SHALL hold every runway and helipad of the level, at their real angles and relative positions. The miniature SHALL be drawn in the level's own theme. Each tile SHALL also show the level's name as text.

#### Scenario: Three tiles
- **WHEN** the level selection page is shown
- **THEN** three tiles are shown, in level order, each with a miniature and the level name

#### Scenario: Miniature matches layout
- **WHEN** a tile is shown
- **THEN** its miniature is drawn from the same layout data the level is played with, so its runway count, helipad count and angles match

#### Scenario: Miniature matches the game
- **WHEN** a tile's miniature is compared with the same part of the level in the game
- **THEN** ground, runway and helipad construction and scenery look the same, and the miniature shows no aircraft, path, cloud, bird, boat, cow or farmer

#### Scenario: Every landing zone is in view
- **WHEN** a tile is shown
- **THEN** every runway and helipad of the level lies inside the miniature

#### Scenario: Island sea stays water
- **WHEN** the island tile is shown while the app uses the Night or Dawn palette
- **THEN** its sea is drawn in the water colours of the level's own theme, not in a sky backdrop tint that goes dark

#### Scenario: Tiles are square
- **WHEN** a tile is shown at any screen size
- **THEN** its width equals its height
