# flight-path-drawing Specification

## Purpose
Defines how the player selects an aircraft and clears, draws or redraws its flight path.

## Requirements

### Requirement: Tap removes the path
The system SHALL remove an aircraft's path when the player taps the aircraft, and the aircraft SHALL keep its current heading.

#### Scenario: Cancel path
- **WHEN** an aircraft has a path and the player taps it without dragging
- **THEN** the path disappears and the aircraft flies straight on in its current heading

### Requirement: Touch hides the old path
The system SHALL hide an aircraft's previous path as soon as the player touches the aircraft.

#### Scenario: Touch starts redraw
- **WHEN** the player touches an aircraft that has a path
- **THEN** the old path is no longer shown

### Requirement: Drag draws a replacement path
The system SHALL let the player tap and drag an aircraft to give it a new path that replaces any previous one.

#### Scenario: Redraw directly
- **WHEN** the player touches an aircraft with a path and drags
- **THEN** the aircraft follows the newly drawn path only

### Requirement: Aircraft can be re-routed until they have landed
The system SHALL let the player give any aircraft a new path until it has landed, including an aircraft that is cleared to land. An airplane has landed at touchdown; a helicopter has landed when its landing sequence starts.

#### Scenario: Re-route a cleared airplane
- **WHEN** an airplane is cleared to land on its path and the player touches it and drags a new path
- **THEN** the old path disappears, the clearance is cancelled and the airplane follows the newly drawn path only

#### Scenario: Re-route a cleared helicopter
- **WHEN** a helicopter is cleared to land on a helipad, has not reached it yet, and the player drags a new path from it
- **THEN** the helicopter follows the new path and does not land on the helipad

#### Scenario: Tap cancels the landing
- **WHEN** an aircraft is cleared to land and the player taps it without dragging
- **THEN** its path disappears, its clearance is cancelled and it flies straight on in its current heading

#### Scenario: New path can clear it again
- **WHEN** the player re-routes a cleared aircraft with a path that enters a valid landing zone
- **THEN** the path ends at the zone and the aircraft is cleared to land again

### Requirement: Landed aircraft cannot be re-routed
An airplane that has touched down SHALL NOT be pickable or re-routed, and a helicopter in its landing sequence SHALL NOT be re-routed.

#### Scenario: Airplane rolling out
- **WHEN** the player touches an airplane that is rolling out on a runway
- **THEN** it is not selected and its movement does not change
