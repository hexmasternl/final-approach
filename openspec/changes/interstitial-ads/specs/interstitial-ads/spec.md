# Spec Delta

## Purpose

Full-screen ads shown in the pause between two games. This capability defines when an ad may appear, how often, and the consent and test-ad rules that every ad request must respect.

## ADDED Requirements

### Requirement: Ads appear only between games
An interstitial ad SHALL be shown only on the step from the game-over screen to the next game. An ad SHALL NOT be shown during gameplay, when the app starts, or when the app is being left.

#### Scenario: Play again
- **WHEN** the player chooses to play again on the game-over screen and an ad is due and ready
- **THEN** the ad is shown, and the next game starts when the ad is dismissed

#### Scenario: During a game
- **WHEN** a game is in progress
- **THEN** no ad is shown

#### Scenario: App start and exit
- **WHEN** the app starts or the player quits
- **THEN** no ad is shown

### Requirement: Frequency cap
An ad SHALL be due only after every third finished game. After an ad has been shown, the count SHALL start again.

#### Scenario: First and second game
- **WHEN** the player finishes the first or the second game since the last ad and chooses to play again
- **THEN** the next game starts with no ad

#### Scenario: Third game
- **WHEN** the player finishes the third game since the last ad and chooses to play again
- **THEN** an ad is due

#### Scenario: Count restarts
- **WHEN** an ad has been shown
- **THEN** two more games can be played without an ad before one is due again

### Requirement: No ad, no delay
If an ad is due but none is loaded, the next game SHALL start immediately. The player SHALL NOT wait for an ad to load.

#### Scenario: Ad not loaded
- **WHEN** an ad is due and no ad is ready
- **THEN** the next game starts at once, with no loading indicator and no pause

#### Scenario: Missed ad stays due
- **WHEN** an ad was due but none was ready
- **THEN** an ad is still due the next time the player chooses to play again

### Requirement: Consent before any ad request
The app SHALL NOT request an ad before the consent flow has resolved. Ads SHALL be requested only when the resolved consent state allows ad requests.

#### Scenario: Consent not resolved
- **WHEN** the consent flow has not finished
- **THEN** no ad is requested

#### Scenario: Consent does not allow ads
- **WHEN** the consent flow has resolved and does not allow ad requests
- **THEN** no ad is requested, and games follow one another with no ad

#### Scenario: Consent allows ads
- **WHEN** the consent flow has resolved and allows ad requests
- **THEN** ads may be requested and shown under the other requirements

### Requirement: Consent failure does not block play
If the consent flow fails or cannot be completed, the game SHALL remain fully playable and no ad SHALL be requested in that session.

#### Scenario: Consent flow error
- **WHEN** the consent flow ends in an error, for example with no network
- **THEN** the player can play as normal and no ad is requested

### Requirement: Test ads in debug builds
Debug builds SHALL request only test ads. Real ad units SHALL be used in release builds only.

#### Scenario: Debug build
- **WHEN** a debug build requests an ad
- **THEN** the request uses the test ad unit

#### Scenario: Release build
- **WHEN** a release build requests an ad
- **THEN** the request uses the app's own ad unit
