# Spec Delta

## Purpose

When a level ends in failure, the game ended dialog tells the player how many aircraft they landed and how many points they scored, and lets them try again or go back to the main page.

## ADDED Requirements

### Requirement: Dialog appears when the game has ended
The game screen SHALL show the game ended dialog as a modal when the game has ended because two aircraft collided or because an aircraft left the viewport without landing. It SHALL NOT be shown while the game is running.

#### Scenario: Collision
- **WHEN** two aircraft collide
- **THEN** the game ended dialog is shown

#### Scenario: Aircraft not landed
- **WHEN** an aircraft leaves the viewport without having landed
- **THEN** the game ended dialog is shown

#### Scenario: Game running
- **WHEN** the game is running and no failure has happened
- **THEN** no game ended dialog is shown

### Requirement: Simulation is frozen behind the dialog
The simulation SHALL stay stopped while the game ended dialog is open, and the player SHALL NOT be able to select aircraft or draw paths.

#### Scenario: Aircraft stand still
- **WHEN** the dialog is open
- **THEN** no aircraft moves, no aircraft spawns, and the score does not change

#### Scenario: No input on the field
- **WHEN** the player taps or drags on the field behind the dialog
- **THEN** no aircraft is selected and no path is drawn

### Requirement: Dialog reports landings per aircraft type
The dialog SHALL show the number of aircraft landed for each aircraft type that can appear in the level.

#### Scenario: Counts per type
- **WHEN** the dialog is shown after a run in which 3 light props and 1 helicopter landed, in a level that spawns light props, jets and helicopters
- **THEN** the dialog shows 3 for the light prop, 0 for the jet and 1 for the helicopter

#### Scenario: Types that do not spawn
- **WHEN** the level does not spawn jumbos
- **THEN** the dialog has no jumbo entry

#### Scenario: Nothing landed
- **WHEN** the game ends before any aircraft has landed
- **THEN** every shown type has a count of 0

### Requirement: Dialog reports totals
The dialog SHALL show the total number of aircraft landed, which equals the sum of the per-type counts, and the total points of the run, which equals the score at the moment the game ended.

#### Scenario: Total landed
- **WHEN** the dialog shows 3 light props and 1 helicopter
- **THEN** the total landed is 4

#### Scenario: Total points
- **WHEN** the score was 250 when the game ended
- **THEN** the dialog shows 250 points

### Requirement: Dialog actions
The dialog SHALL offer exactly two actions: restart the level, shown as the primary action, and return to the main page. Each action SHALL be at least 48dp high and wide, and have a text label.

#### Scenario: Two actions
- **WHEN** the dialog is shown
- **THEN** a restart button and a return-to-main-page button are visible, and the restart button is the primary one

#### Scenario: Restart
- **WHEN** the player taps restart
- **THEN** the dialog closes and the same level starts again from scratch, with score and landed counts back at 0

#### Scenario: Return to main page
- **WHEN** the player taps return to the main page
- **THEN** the dialog closes and the main page is shown

### Requirement: Dialog cannot be dismissed without choosing
The dialog SHALL stay open until the player taps one of its two actions. Tapping outside the dialog SHALL NOT close it. A system back press SHALL do the same as the return-to-main-page action.

#### Scenario: Tap outside
- **WHEN** the player taps on the scrim outside the dialog
- **THEN** the dialog stays open

#### Scenario: Back press
- **WHEN** the player presses the system back button while the dialog is open
- **THEN** the main page is shown

### Requirement: Dialog follows the design system
The dialog SHALL be built from the design-system Dialog component and theme tokens, with copy that names what happened in plain words, and SHALL respect the reduced-motion setting.

#### Scenario: Copy names the cause
- **WHEN** the game ended by a collision
- **THEN** the dialog body says that two aircraft got too close

#### Scenario: Copy for a missed aircraft
- **WHEN** the game ended because an aircraft left the viewport without landing
- **THEN** the dialog body says that an aircraft left without landing

#### Scenario: Reduced motion
- **WHEN** the device has reduced motion enabled and the dialog opens
- **THEN** the dialog appears without the drop-in animation
