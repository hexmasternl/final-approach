## ADDED Requirements

### Requirement: Explanation coach mark with Got it
A tutorial step without a gesture SHALL show an explanation coach mark: the design system's dark coach mark bubble with a step line ("Step 2 of 6"), a short title, one to three short sentences and a Got it button of at least the minimum touch size. Tapping anywhere else SHALL NOT dismiss it.

#### Scenario: Got it dismisses
- **WHEN** an explanation coach mark is shown and the player taps Got it
- **THEN** the coach mark disappears and the tutorial moves on

#### Scenario: Tapping elsewhere
- **WHEN** an explanation coach mark is shown and the player taps the field
- **THEN** the coach mark stays

#### Scenario: Touch target
- **WHEN** the Got it button is measured
- **THEN** its touch area is at least 48dp in both directions

### Requirement: Gesture coach mark has no button
The coach mark of the light prop's draw step SHALL have no button; it SHALL disappear when the player's path clears the light prop to land.

#### Scenario: Gesture dismisses
- **WHEN** the player's path clears the light prop to land
- **THEN** the draw-step coach mark disappears

### Requirement: Tracking coach mark follows its aircraft
While a tutorial aircraft flies in, a coach mark SHALL follow it every frame with its tail pointing at the aircraft and SHALL stay fully on screen, flipping above or below the aircraft as needed. It SHALL NOT cover the aircraft's pick area. While the aircraft is still outside the viewport, the coach mark SHALL sit at the screen edge nearest to it.

#### Scenario: Follows
- **WHEN** the tracked aircraft moves
- **THEN** the coach mark moves with it and its tail keeps pointing at the aircraft

#### Scenario: Near the top edge
- **WHEN** the tracked aircraft is near the top of the screen
- **THEN** the coach mark is drawn below it with its tail pointing up, fully on screen

### Requirement: Zone highlight leaves the zone clear
The runway and helipad steps SHALL highlight their target with a dashed outline drawn outside the runway or helipad shape, never filling or covering the runway, the helipad or a landing zone. The runway highlight SHALL add an arrow outside the 27 end pointing along the landing direction. The highlight SHALL be distinguishable by shape, not only by colour.

#### Scenario: Runway highlight
- **WHEN** the runway step is shown
- **THEN** a dashed outline surrounds runway 27 outside its edges, and an arrow east of the runway points west into its 27 end

#### Scenario: Helipad highlight
- **WHEN** the helipad step is shown
- **THEN** a dashed outline surrounds the helipad outside its edge and the pad stays fully visible

### Requirement: Drag demo with a reduced-motion still
The light prop's draw step SHALL show a drag demo: a finger marker moving along a dotted path from the light prop to the 27 end of the runway, repeating until the player starts to drag. With reduced motion on, it SHALL show the dotted path with the finger marker standing still at its end instead.

#### Scenario: Animated
- **WHEN** the draw step is shown with reduced motion off
- **THEN** the finger marker moves along the dotted path from the light prop to runway 27 and repeats

#### Scenario: Reduced motion
- **WHEN** the draw step is shown with reduced motion on
- **THEN** the dotted path is shown with the finger marker still at its runway end, and nothing moves

#### Scenario: Demo stops when the player drags
- **WHEN** the player starts dragging a path
- **THEN** the drag demo disappears

### Requirement: Skip tutorial button at the top edge
While the tutorial runs, a small "Skip tutorial" button SHALL sit in the top band of the screen, next to the HUD, where it covers no landing zone. It SHALL disappear when the tutorial ends.

#### Scenario: Shown during the tutorial
- **WHEN** any tutorial step is shown
- **THEN** a "Skip tutorial" button is visible in the top band

#### Scenario: Gone afterwards
- **WHEN** the tutorial is completed or skipped
- **THEN** the button is no longer shown

### Requirement: Calm, specific copy
Tutorial text SHALL follow the tower controller voice: short, calm, specific, sentence case, no emoji, and failure lines SHALL say what happened without blame.

#### Scenario: Failure line
- **WHEN** a step restarts after a collision
- **THEN** the coach mark says "Two aircraft got too close. Let's try that again."

### Requirement: Tutorial pieces come from the design system
The explanation coach mark, tracking coach mark, zone highlight, drag demo and its still variant SHALL be described in the design system before they are built, and the game SHALL draw them with theme tokens only. The tutorial SHALL be described in the game design document.

#### Scenario: In the showcase
- **WHEN** the design system showcase is opened
- **THEN** it shows the explanation coach mark with Got it, the tracking coach mark, the zone highlight around a runway and a helipad, and the drag demo with its reduced-motion still

#### Scenario: Documented
- **WHEN** `docs/game-design.md` is read
- **THEN** it describes the tutorial's steps, when it runs, failure restart, skip and completion
