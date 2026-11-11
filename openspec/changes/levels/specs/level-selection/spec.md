## ADDED Requirements

### Requirement: Selection page after Take off
Choosing **Take off** on the main page SHALL open the level selection page, not start a game directly. The system back button on that page SHALL return to the main page.

#### Scenario: Take off opens selection
- **WHEN** the player taps Take off on the main page
- **THEN** the level selection page is shown

#### Scenario: Back returns home
- **WHEN** the player presses the system back button on the level selection page
- **THEN** the main page is shown

### Requirement: One square tile per level
The page SHALL show one square tile for each level, each containing a miniature of that level's airfield: its runways and helipads at their real angles and relative positions, on the level's ground colour. Each tile SHALL also show the level's name as text.

#### Scenario: Three tiles
- **WHEN** the level selection page is shown
- **THEN** three tiles are shown, in level order, each with a miniature and the level name

#### Scenario: Miniature matches layout
- **WHEN** a tile is shown
- **THEN** its miniature is drawn from the same layout data the level is played with, so its runway count, helipad count and angles match

#### Scenario: Tiles are square
- **WHEN** a tile is shown at any screen size
- **THEN** its width equals its height

### Requirement: Horizontal scrolling when tiles do not fit
The tiles SHALL be laid out in one row. When the tiles do not all fit in the viewport width, the row SHALL scroll horizontally. When they fit, it SHALL NOT scroll.

#### Scenario: Narrow screen scrolls
- **WHEN** the viewport is too narrow to show all tiles
- **THEN** the player can drag the row sideways to reach every tile

#### Scenario: Wide screen fits
- **WHEN** the viewport is wide enough for all tiles
- **THEN** all tiles are visible at once

### Requirement: Tapping a tile loads and starts the level
Tapping a tile SHALL load that level and start it, showing the game screen with that level's airfield.

#### Scenario: Start level one
- **WHEN** the player taps the level one tile
- **THEN** the game screen opens showing the level one airfield and scenery

#### Scenario: Start another level
- **WHEN** the player returns to the selection page and taps the level three tile
- **THEN** the game screen shows the level three airfield, not level one

### Requirement: Tiles are accessible
Each tile SHALL be at least 48dp in both directions and SHALL be identifiable by its name, not by its picture alone.

#### Scenario: Tile size and name
- **WHEN** the tiles are shown on the smallest supported screen
- **THEN** each tile is at least 48dp wide and high and shows its name
