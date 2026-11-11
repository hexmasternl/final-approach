# Spec Delta

## Purpose

The splash page is the opening screen of the app. It introduces the game by name with a short animated sunset scene and then hands over to the main page.

## ADDED Requirements

### Requirement: Splash page is shown first
The app SHALL show the splash page as its first screen every time it is started.

#### Scenario: Cold start
- **WHEN** the player starts the app
- **THEN** the splash page is the first screen shown, before the main page

### Requirement: Sunset scene
The splash page SHALL show a sunset sky in deep orange tints as its background, with a dark silhouette of an airport in the foreground along the bottom of the screen. The silhouette SHALL include an air traffic control tower that is recognisable as one.

#### Scenario: Scene is visible from the first frame
- **WHEN** the splash page appears
- **THEN** the sunset sky fills the screen and the airport silhouette with its control tower stands along the bottom edge

#### Scenario: Tower stands out
- **WHEN** the player looks at the silhouette
- **THEN** the control tower is the tallest shape in it

### Requirement: Airplane tows the banner in
An airplane SHALL enter the screen from the left edge, flying to the right, towing a banner behind it that reads "Final Approach".

#### Scenario: Entry
- **WHEN** the splash page animation starts
- **THEN** an airplane comes into view from the left edge with the banner trailing behind it on a tow line

#### Scenario: Banner text
- **WHEN** the banner is in view
- **THEN** it shows the text "Final Approach"

### Requirement: Banner breaks loose at the centre
When the banner reaches the horizontal centre of the screen it SHALL detach from the airplane and remain in view there. The airplane SHALL keep flying to the right and leave the screen completely.

#### Scenario: Release
- **WHEN** the centre of the banner reaches the horizontal centre of the screen
- **THEN** the banner detaches and stays at the centre of the screen

#### Scenario: Airplane exits
- **WHEN** the banner has detached
- **THEN** the airplane continues at the same heading and leaves through the right edge until no part of it is visible

### Requirement: Banner waves like a flag
After detaching, the banner SHALL wave slowly and continuously for as long as the splash page is shown. Its text SHALL remain readable throughout the wave.

#### Scenario: Continuous wave
- **WHEN** the banner has settled at the centre
- **THEN** it keeps waving slowly without stopping

#### Scenario: Readable text
- **WHEN** the banner is at any point of its wave
- **THEN** the text "Final Approach" can still be read

### Requirement: Leaving the splash page
The splash page SHALL hand over to the next screen automatically 2 seconds after the banner has settled at the centre. A tap anywhere on the splash page SHALL hand over immediately, at any moment of the animation.

#### Scenario: Automatic hand-over
- **WHEN** 2 seconds have passed since the banner settled
- **THEN** the app shows the next screen

#### Scenario: Tap to skip
- **WHEN** the player taps the splash page before the hand-over
- **THEN** the app shows the next screen immediately

### Requirement: Scene fits any screen
The splash scene SHALL fill the screen on every supported screen size and orientation without stretching the silhouette out of proportion and without clipping the settled banner.

#### Scenario: Narrow and wide screens
- **WHEN** the splash page is shown on a narrow phone and on a wide one
- **THEN** on both the sky fills the screen, the silhouette keeps its proportions, and the whole banner is visible once settled

### Requirement: Reduced motion
When the device has reduced motion enabled, the splash page SHALL show the settled end state from the start: the banner centred and still, and no airplane. The automatic hand-over SHALL still apply.

#### Scenario: Reduced motion enabled
- **WHEN** the app starts on a device with reduced motion enabled
- **THEN** the splash page shows the sunset scene with the banner centred and not moving, no airplane appears, and the next screen is shown 2 seconds later
