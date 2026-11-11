# main-page Specification

## Purpose
The main page is the home screen of the app. From it the player starts a game, opens settings, or quits, in front of an animated sky that keeps the screen alive.

## Requirements

### Requirement: Main page actions
The main page SHALL show a Take off button and a Settings button, and no other actions. Take off SHALL start a game. Settings SHALL open the settings page.

#### Scenario: Buttons shown
- **WHEN** the main page is shown
- **THEN** a Take off button and a Settings button are visible, and there is no Quit button on the page

#### Scenario: Take off
- **WHEN** the player taps Take off
- **THEN** a new game starts

#### Scenario: Settings
- **WHEN** the player taps Settings
- **THEN** the settings page opens

### Requirement: Main page is the home screen
The app SHALL show the main page after the splash page, and whenever the player returns from a game or from the settings page.

#### Scenario: After the splash page
- **WHEN** the splash page hands over
- **THEN** the main page is shown

#### Scenario: Returning from settings
- **WHEN** the player goes back from the settings page
- **THEN** the main page is shown

### Requirement: Version label
The main page SHALL show the current version of the app in its bottom-right corner. The version shown SHALL be the version of the installed build.

#### Scenario: Version visible
- **WHEN** the main page is shown
- **THEN** the app version is displayed in the bottom-right corner

#### Scenario: Version matches the build
- **WHEN** the installed build has version 1.2.0
- **THEN** the label shows 1.2.0

### Requirement: Back asks to quit
Pressing the system back button on the main page SHALL open a confirmation dialog that asks whether the player wants to quit, with a Quit button and a Cancel button.

#### Scenario: Back on the main page
- **WHEN** the player presses back on the main page with no dialog open
- **THEN** a quit confirmation dialog with a Quit and a Cancel button is shown, and the app does not close

### Requirement: Quit closes the app
Choosing Quit in the confirmation dialog SHALL close the app.

#### Scenario: Quit
- **WHEN** the player taps Quit in the confirmation dialog
- **THEN** the app closes

### Requirement: Cancel stays on the main page
Choosing Cancel in the confirmation dialog SHALL close the dialog and leave the player on the main page. Pressing back while the dialog is open SHALL do the same.

#### Scenario: Cancel
- **WHEN** the player taps Cancel in the confirmation dialog
- **THEN** the dialog closes and the main page is shown

#### Scenario: Back while the dialog is open
- **WHEN** the player presses back while the confirmation dialog is open
- **THEN** the dialog closes and the main page is shown

### Requirement: Rotating striped background
The background of the main page SHALL show alternating dark and light stripes arranged in a circular pattern around one centre point. The pattern SHALL rotate continuously and very slowly.

#### Scenario: Stripes shown
- **WHEN** the main page is shown
- **THEN** dark and light stripes fan out in a circle pattern behind everything else

#### Scenario: Slow rotation
- **WHEN** the main page stays open
- **THEN** the stripe pattern rotates without stopping, taking minutes rather than seconds for one full turn

### Requirement: Three drifting clouds
The main page SHALL show exactly three clouds. Each cloud SHALL move a little around its own position and SHALL never leave the screen.

#### Scenario: Clouds present
- **WHEN** the main page is shown
- **THEN** three clouds are visible

#### Scenario: Clouds stay put
- **WHEN** the main page stays open for a long time
- **THEN** each cloud is still near where it started and fully on screen

### Requirement: Passing aircraft
Airplanes and helicopters SHALL fly through the main page one at a time. Each SHALL be of a random type, enter from a random location on a screen edge, cross the screen, and leave it.

#### Scenario: One at a time
- **WHEN** an aircraft is in view
- **THEN** no second aircraft is in view

#### Scenario: Variety
- **WHEN** many aircraft have passed
- **THEN** both airplanes and helicopters have appeared, and they have entered from different locations

### Requirement: Three seconds between aircraft
A new aircraft SHALL appear 3 seconds after the previous aircraft has completely left the screen.

#### Scenario: Next aircraft
- **WHEN** an aircraft has fully left the screen
- **THEN** the next aircraft comes into view 3 seconds later, and not before

### Requirement: Aircraft pass behind the buttons
Passing aircraft SHALL be drawn behind the buttons and the version label, and SHALL NOT react to touch. The buttons SHALL stay readable and tappable whatever passes behind them.

#### Scenario: Aircraft crosses a button
- **WHEN** an aircraft's route crosses the Take off button
- **THEN** the aircraft is hidden where the button covers it, and the button still responds to a tap

#### Scenario: Tapping an aircraft
- **WHEN** the player taps a passing aircraft
- **THEN** nothing happens

### Requirement: Background keeps moving behind the dialog
While the quit confirmation dialog is open, the background animation SHALL continue, dimmed behind the dialog.

#### Scenario: Dialog open
- **WHEN** the quit confirmation dialog is open
- **THEN** the stripes, clouds and aircraft keep moving behind a dimmed overlay

### Requirement: Reduced motion
When the device has reduced motion enabled, the stripes and clouds SHALL stand still and no aircraft SHALL fly past. All actions SHALL work as normal.

#### Scenario: Reduced motion enabled
- **WHEN** the main page is shown on a device with reduced motion enabled
- **THEN** the stripes and the three clouds are shown without movement, no aircraft appear, and Take off, Settings and the quit flow work as normal
