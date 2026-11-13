## ADDED Requirements

### Requirement: Colliding aircraft get red rings
When the level ends by a collision, each of the two colliding aircraft SHALL show a dashed ring in the `danger` colour at full size and full opacity, whether or not it was showing a proximity warning ring just before.

#### Scenario: Warned aircraft collide
- **WHEN** two aircraft with yellow proximity warning rings collide
- **THEN** both rings turn red at once and stay dashed

#### Scenario: Aircraft that were not warned
- **WHEN** two aircraft collide in a frame in which neither was showing a warning ring
- **THEN** both show a full-size red dashed ring

#### Scenario: Other warned aircraft
- **WHEN** a third aircraft was showing a yellow warning ring at the moment of the collision
- **THEN** its ring stays yellow and fades out with the rest of the level

### Requirement: Collision rings freeze with the game
The red collision rings SHALL NOT rotate during the collision sequence or behind the game ended dialog.

#### Scenario: Rings stand still
- **WHEN** the collision sequence plays
- **THEN** the dashes of both red rings stay at the angle they had at the moment of the collision

### Requirement: Everything else fades to black
Over the collision fade duration (1000 ms), a blackout SHALL cover everything on the game screen except the two colliding aircraft and their red rings: field, runways, helipads, zone glows, paths, other aircraft and their rings, clouds and the HUD. At the end of the fade the blackout SHALL be fully opaque, and the two aircraft and their red rings SHALL be drawn on top of it at full opacity throughout.

#### Scenario: Halfway through the fade
- **WHEN** 500 ms have passed since the collision
- **THEN** the field, other aircraft and HUD are partly covered by the blackout, and the two colliding aircraft and their red rings are fully visible

#### Scenario: Fade finished
- **WHEN** 1000 ms have passed since the collision
- **THEN** only the two colliding aircraft and their red rings are visible on the blackout

### Requirement: The faded view holds before the dialog
After the fade, the blacked-out view SHALL stay on screen for the collision hold duration (1000 ms). The game ended dialog SHALL appear when the hold ends, 2000 ms after the collision, over the blacked-out view.

#### Scenario: During the hold
- **WHEN** 1500 ms have passed since the collision
- **THEN** the blacked-out view with the two aircraft and red rings is shown and no dialog is visible

#### Scenario: Dialog after the hold
- **WHEN** 2000 ms have passed since the collision
- **THEN** the game ended dialog, with the ad banner under it, is shown over the blacked-out view

### Requirement: Input is blocked during the sequence
During the collision sequence the player SHALL NOT be able to select aircraft, draw paths, open the options panel or skip the sequence by tapping. A system back press SHALL return to the main page, as it does while the game ended dialog is open.

#### Scenario: Tap during the sequence
- **WHEN** the player taps or drags anywhere on the screen during the sequence
- **THEN** nothing is selected, no path is drawn, the options panel does not open, and the dialog still appears 2000 ms after the collision

#### Scenario: Back during the sequence
- **WHEN** the player presses the system back button during the sequence
- **THEN** the main page is shown

### Requirement: Reduced motion skips the fade
When the device's reduced-motion setting is on, the blacked-out end state SHALL be shown at once on collision, without a fade, and the game ended dialog SHALL appear after the collision hold duration (1000 ms).

#### Scenario: Reduced motion on
- **WHEN** reduced motion is on and two aircraft collide
- **THEN** the blackout with the two aircraft and their red rings appears in the same frame, and the dialog appears 1000 ms later

### Requirement: Only collisions play the sequence
The collision sequence SHALL play only when a level ends by a collision. A level that ends because an aircraft left the viewport SHALL show no blackout and no red rings. A collision in the tutorial, which restarts the step instead of ending the level, SHALL NOT play it.

#### Scenario: Aircraft leaves the viewport
- **WHEN** an aircraft leaves the viewport without landing
- **THEN** the game ended dialog appears at once, without a blackout

#### Scenario: Collision in the tutorial
- **WHEN** two aircraft collide during the tutorial
- **THEN** no blackout is shown and the tutorial step restarts as before

### Requirement: The sequence resets on restart
Restarting the level from the game ended dialog SHALL remove the blackout and red rings, and a later collision SHALL play the full sequence again.

#### Scenario: Try again
- **WHEN** the player taps Try again after a collision and the level restarts
- **THEN** the field is shown normally with no blackout or red rings
