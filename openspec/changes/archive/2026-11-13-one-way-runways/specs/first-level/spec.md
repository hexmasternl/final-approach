## MODIFIED Requirements

### Requirement: Meadow Field airport
The level SHALL show one runway in the middle of the screen, marked as one-way runway 27, and one helipad on the right, on the striped lawn. It SHALL show decoration that makes it read as a small airfield: a taxiway, an apron, a terminal with a control tower, a hangar, a windsock and trees. Decoration SHALL be level data and SHALL NOT cover a runway, a helipad or their landing zones.

#### Scenario: Layout
- **WHEN** the level is shown
- **THEN** the runway lies left to right in the middle and the helipad is on the right

#### Scenario: Runway 27 marking
- **WHEN** the level is shown
- **THEN** the runway shows the number 27 and threshold bars only at its right end, nothing of the kind at its left end, and chevrons on its centre line pointing left

#### Scenario: Decoration is clear of zones
- **WHEN** the Meadow Field config is validated
- **THEN** no scenery item overlaps a runway, a helipad or a landing zone
