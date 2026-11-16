## ADDED Requirements

### Requirement: System bars hidden while a level runs

While a level is running, the game screen SHALL hide the status bar and the navigation bar, so that a swipe in from the top or bottom edge only reveals the bars for a moment and a second swipe is needed to open the notification drawer or go home. A level is running when it is neither paused (options panel open), waiting for the interstitial ad before a level or restart, nor over (game-ended dialog shown).

#### Scenario: Bars hidden during play

- **WHEN** a level is running
- **THEN** neither the status bar nor the navigation bar is visible

#### Scenario: Swipe down from the top during play

- **WHEN** the player swipes down once from the top edge during a running level
- **THEN** the status bar shows briefly over the game, the notification drawer does not open, and the bar hides again by itself

#### Scenario: Swipe up from the bottom during play

- **WHEN** the player swipes up once from the bottom edge during a running level
- **THEN** the navigation bar shows briefly, the app stays open on the level, and the bar hides again by itself

#### Scenario: Bars hidden again after returning to the app

- **WHEN** the player leaves the app during a running level and comes back to it while the level is still running
- **THEN** the system bars are hidden again

### Requirement: System bars shown outside a running level

Whenever no level is running, the app SHALL show the system bars as usual.

#### Scenario: Paused level

- **WHEN** the player opens the options panel during a level
- **THEN** the system bars are visible

#### Scenario: Waiting for the interstitial

- **WHEN** the level waits for the interstitial ad before it starts or restarts
- **THEN** the system bars are visible

#### Scenario: Level over

- **WHEN** the level ends and the game-ended dialog shows
- **THEN** the system bars are visible

#### Scenario: Resuming hides the bars again

- **WHEN** the player taps Continue in the options panel
- **THEN** the system bars are hidden again

#### Scenario: Leaving the level

- **WHEN** the player quits the level to the main page
- **THEN** the system bars are visible on the main page

### Requirement: Back-gesture exclusion around aircraft near a side edge

On Android 10 and later with gesture navigation, while a level is running, the game screen SHALL exclude from the system back gesture a zone on the left or right edge around every pickable aircraft whose pick area reaches into that edge's back-gesture strip. A zone SHALL be centred vertically on its aircraft and SHALL move with it, so it is in place before the finger touches down. Aircraft rolling out or landing, which cannot be picked, get no zone.

#### Scenario: Tap an aircraft near the left edge

- **WHEN** a pickable aircraft flies near the left edge during a running level and the player touches down on it inside the back-gesture strip
- **THEN** the aircraft is selected and the system back gesture does not start

#### Scenario: Drag from an aircraft near the right edge

- **WHEN** the player touches down inside the right back-gesture strip on a pickable aircraft and drags inward
- **THEN** a path is drawn for the aircraft and the level neither pauses nor closes

#### Scenario: Zone follows the aircraft

- **WHEN** an aircraft near a side edge moves along that edge
- **THEN** its exclusion zone moves with it, centred on the aircraft

#### Scenario: Aircraft away from the edges

- **WHEN** no pickable aircraft's pick area reaches a side edge's back-gesture strip
- **THEN** that edge has no exclusion zone and a swipe in from it acts as the system back gesture

### Requirement: Exclusion height stays within the system cap

The total exclusion height on each side edge SHALL NOT exceed the system cap of 200dp. When the zones an edge needs do not fit, the selected aircraft SHALL get its zone first, then the aircraft closest to that edge; zones that overlap SHALL count once, and a zone that does not fit in the remaining height SHALL be left out.

#### Scenario: Many aircraft near one edge

- **WHEN** more aircraft are near the left edge than fit within 200dp of exclusion height
- **THEN** the zones on the left edge add up to at most 200dp

#### Scenario: Selected aircraft first

- **WHEN** the selected aircraft and other aircraft are near the same edge and not all their zones fit
- **THEN** the selected aircraft has a zone

### Requirement: No exclusion zones outside a running level

Whenever no level is running, and on devices without the system back gesture or older than Android 10, the app SHALL NOT exclude any part of the screen edges from system gestures.

#### Scenario: Paused level

- **WHEN** the options panel is open with aircraft near a side edge
- **THEN** a swipe in from that edge acts as the system back gesture

#### Scenario: Level over

- **WHEN** the game-ended dialog shows
- **THEN** no part of the side edges is excluded from system gestures

#### Scenario: Leaving the level

- **WHEN** the player quits the level to the main page
- **THEN** no part of the side edges is excluded from system gestures

#### Scenario: Older Android version

- **WHEN** a level runs on Android 8 or 9
- **THEN** the level plays normally and no exclusion is requested

### Requirement: Back during a running level pauses it

A system back gesture or back press that reaches the app while a level is running SHALL pause the level and open the options panel, and SHALL NOT leave the level or the app.

#### Scenario: Back gesture gets through

- **WHEN** the player swipes in from a side edge where no zone is excluded during a running level
- **THEN** the level pauses, the options panel opens and the game screen stays shown

#### Scenario: Back while waiting for the interstitial

- **WHEN** the player presses back while the level waits for the interstitial ad
- **THEN** the options panel opens and the game screen stays shown
