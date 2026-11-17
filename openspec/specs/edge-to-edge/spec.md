# edge-to-edge Specification

## Purpose
TBD - created by archiving change edge-to-edge-deprecated-apis. Update Purpose after archive.

## Requirements

### Requirement: Flagged call is located and recorded
The change SHALL record, for every deprecated edge-to-edge call or attribute found in the release dependencies, our code and the theme, where it lives (library and version, class, or theme item) and how it was resolved.

#### Scenario: Findings recorded
- **WHEN** the investigation is finished
- **THEN** the findings are written in the change's design notes, each with its location and resolution

### Requirement: Findings fixed at their source
Each finding SHALL be fixed where it lives: a dependency bump, a change in our code, or a change in the theme. A finding in a library with no fixed release SHALL be recorded with what is done instead.

#### Scenario: Our code or theme
- **WHEN** a finding is in our code or theme
- **THEN** the deprecated call or attribute is replaced or removed and no longer appears in the app's sources

#### Scenario: Dependency with a fixed release
- **WHEN** a finding is in a dependency that has a fixed release
- **THEN** the release classpath resolves that dependency to the fixed release

#### Scenario: Dependency without a fixed release
- **WHEN** a finding is in a dependency with no fixed release
- **THEN** the design notes name the library, its version and the call, and state what is done instead

### Requirement: Lint is clean for our code
`./gradlew :androidApp:lint` SHALL report no deprecated-API or edge-to-edge finding in the app's own code or theme.

#### Scenario: Lint run
- **WHEN** `./gradlew :androidApp:lint` is run
- **THEN** it reports no deprecated-API or edge-to-edge finding in the app's own code or theme

### Requirement: Content clear of bars and cutouts on Android 15 and newer
On Android 15 or newer, every screen SHALL keep its content and controls out from under the status bar, navigation bar and display cutout, with 3-button navigation, gesture navigation and in landscape, while the background still draws edge to edge. Nothing SHALL cover a landing zone.

#### Scenario: Navigation modes
- **WHEN** a screen is opened with 3-button navigation and then with gesture navigation
- **THEN** no content or control is hidden by a system bar

#### Scenario: Display cutout in landscape
- **WHEN** a screen is opened in landscape on a device with a display cutout
- **THEN** no content or control is hidden by the cutout

#### Scenario: Immersive mode in game
- **WHEN** a level is running and the player swipes in from an edge
- **THEN** the bars show transiently and hide again, as before

### Requirement: Play Console warnings gone
The next release SHALL no longer trigger the "deprecated APIs or parameters for edge-to-edge" warning, and the "Edge-to-edge may not display for all users" message SHALL be gone, or each screen it concerns is confirmed to handle insets.

#### Scenario: Next release processed
- **WHEN** the next release is uploaded and processed by Google Play
- **THEN** the deprecated edge-to-edge warning is not reported and the "may not display" message is gone or answered per screen
