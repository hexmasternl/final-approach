# Spec Delta

## ADDED Requirements

### Requirement: Wordmark on the main page
The main page SHALL show the game's wordmark above the Take off and Settings buttons, horizontally centred on the menu, with `space-7` (48dp) between the bottom of the wordmark, including its drop, and the top of the Take off button. The wordmark and the menu SHALL be centred on the screen as one group.

#### Scenario: Wordmark above the menu
- **WHEN** the main page is shown on a phone in portrait
- **THEN** the regular wordmark "Final Approach" is visible, centred above the Take off button, 48dp above it

#### Scenario: Wordmark in front of the backdrop
- **WHEN** a passing aircraft or a cloud crosses the wordmark
- **THEN** the wordmark stays fully visible in front of it

### Requirement: Wordmark beside the menu at compact heights
When the space inside the system bars is too low to hold the regular wordmark stacked above the menu with its spacing, the main page SHALL show the small wordmark to the left of the menu instead, `space-7` (48dp) apart, both vertically centred and centred on the screen as one group.

#### Scenario: Landscape phone
- **WHEN** the main page is shown on a phone in landscape, about 360dp tall including its system bars
- **THEN** the small wordmark sits to the left of the Take off and Settings buttons, 48dp from them, and both are vertically centred

#### Scenario: Tall enough for the stacked layout
- **WHEN** the space inside the system bars is high enough for the regular wordmark, 48dp and the menu
- **THEN** the regular wordmark is shown above the menu, whatever the orientation

### Requirement: Main page content never overlaps or clips
In every layout, the wordmark, the Take off and Settings buttons and the version label SHALL stay inside the system bars, fully visible, and SHALL NOT overlap each other. The wordmark SHALL keep at least one cap height of clear space from the screen edges and the version label.

#### Scenario: Portrait phone
- **WHEN** the main page is shown on a 360 by 640dp phone in portrait with its system bars
- **THEN** the wordmark, both buttons and the version label are fully visible and none overlaps another

#### Scenario: Landscape phone
- **WHEN** the main page is shown on a 640 by 360dp phone in landscape with its system bars
- **THEN** the wordmark, both buttons and the version label are fully visible and none overlaps another

### Requirement: Main page title is announced once
A screen reader on the main page SHALL announce the title once, as the heading "Final Approach", before the Take off button. While the quit dialog is open, the title SHALL be hidden from screen readers along with the rest of the page behind the dialog.

#### Scenario: Reading the main page
- **WHEN** a screen reader moves through the main page
- **THEN** it reads the heading "Final Approach" once, then Take off, then Settings

#### Scenario: Quit dialog open
- **WHEN** the quit dialog is open
- **THEN** a screen reader does not reach the title

### Requirement: Design system describes the main page with the wordmark
The design system's Main Menu guideline and its *Main Menu* reference composition SHALL show the wordmark above the menu, and SHALL describe the compact-height rule with the small wordmark beside the menu.

#### Scenario: Main Menu guideline
- **WHEN** a reader opens the Main Menu guideline in the design system
- **THEN** it says where the wordmark sits, at which size, and when it moves beside the menu, and no longer says the page carries no wordmark

#### Scenario: Main Menu reference
- **WHEN** a reader opens the *Main Menu* reference in the design system showcase
- **THEN** the wordmark is shown above the Take off and Settings buttons
