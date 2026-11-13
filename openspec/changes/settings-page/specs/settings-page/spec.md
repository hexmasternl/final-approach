# Spec Delta

## Purpose

The settings page is where the player changes the game's preferences, reads which version is installed and finds the privacy policy. It is reached from the main page.

## ADDED Requirements

### Requirement: Settings page layout
The settings page SHALL show a header with a back icon button and the title "Settings", followed by three groups in this order: Sound, Accessibility and About. Each group SHALL have an uppercase group label.

#### Scenario: Page opens
- **WHEN** the player taps Settings on the main page
- **THEN** the settings page shows the back icon button, the title "Settings", and the group labels SOUND, ACCESSIBILITY and ABOUT in that order

### Requirement: Sound effects row
The Sound group SHALL hold one row, "Sound effects" with the description "Engines, dings and boings", and a toggle that shows and changes the Sound effects setting. The page SHALL NOT show a Music row.

#### Scenario: Sound row shown
- **WHEN** the settings page is shown
- **THEN** the Sound group shows "Sound effects", "Engines, dings and boings" and a toggle, and no Music row is present

#### Scenario: Toggle reflects the setting
- **WHEN** the settings page opens while Sound effects is off
- **THEN** the toggle is shown in its off position

### Requirement: Reduce motion row
The Accessibility group SHALL hold a "Reduce motion" row with a segmented control of three options, Device, On and Off, in that order. Exactly one option SHALL be selected, and it SHALL be the stored Reduce motion choice.

#### Scenario: Reduce motion control shown
- **WHEN** the settings page is shown with the default settings
- **THEN** the Reduce motion control shows Device, On and Off, with Device selected

#### Scenario: Selecting an option
- **WHEN** the player taps On in the Reduce motion control
- **THEN** On is selected and Device and Off are not

### Requirement: Changes apply at once
A change to any control on the settings page SHALL take effect and be stored immediately. The page SHALL NOT have a Save button.

#### Scenario: No save step
- **WHEN** the player switches Sound effects off and goes straight back to the main page
- **THEN** Sound effects is off, and the page never showed a Save button

#### Scenario: Reduce motion applies on the page
- **WHEN** the player selects On in the Reduce motion control on a device without reduced motion
- **THEN** the settings page's own controls change state without animation from then on

### Requirement: Privacy policy link
The About group SHALL show a "Privacy policy" button. Tapping it SHALL open https://github.com/hexmasternl/final-approach/blob/main/docs/privacy-policy.md in the device's browser.

#### Scenario: Open the policy
- **WHEN** the player taps Privacy policy
- **THEN** the browser opens the published privacy policy URL

#### Scenario: No browser available
- **WHEN** the player taps Privacy policy on a device that cannot open the URL
- **THEN** the app stays on the settings page and does not crash

### Requirement: App name and version
The About group SHALL show the text "Final Approach" followed by the version of the installed build.

#### Scenario: Version shown
- **WHEN** the installed build has version 1.4.0
- **THEN** the About group shows "Final Approach 1.4.0"

### Requirement: Back returns to the main page
Pressing the system back button or tapping the back icon button on the settings page SHALL return to the main page.

#### Scenario: System back
- **WHEN** the player presses the system back button on the settings page
- **THEN** the main page is shown

#### Scenario: Back icon button
- **WHEN** the player taps the back icon button
- **THEN** the main page is shown

### Requirement: Touch targets
Every control on the settings page SHALL have a touch target of at least 48dp in each direction, including each segment of the Reduce motion control.

#### Scenario: Control sizes
- **WHEN** the settings page is measured
- **THEN** the back icon button, the toggle, each Reduce motion segment and the Privacy policy button each accept taps within an area at least 48dp high and 48dp wide

### Requirement: Page fits small screens
The settings page SHALL keep every control reachable on the smallest supported screen in portrait and in landscape, scrolling vertically when the content does not fit.

#### Scenario: Short landscape screen
- **WHEN** the settings page is shown in landscape on a screen too short for all groups
- **THEN** the page scrolls vertically and the About group can be reached
