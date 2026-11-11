# Spec Delta

## Purpose

Full-screen ads shown in the pause before a level starts. This capability defines when an ad may appear, what happens when none is available, and the consent and test-ad rules that every ad request, banner included, must respect.

## ADDED Requirements

### Requirement: An ad appears before a level starts
An interstitial ad SHALL be offered before a level starts, both when it is started from level select and when it is restarted. The level SHALL begin only when the ad is dismissed. AdMob SHALL control how long the ad shows and when it can be closed; the app SHALL NOT enforce its own display time. An ad SHALL NOT be shown during gameplay, when the app starts, or when the app is being left.

#### Scenario: Starting a level
- **WHEN** the player starts a level and an ad is ready
- **THEN** the ad is shown, and the level begins when the ad is dismissed

#### Scenario: Restarting a level
- **WHEN** the player restarts the level and an ad is ready
- **THEN** the ad is shown, and the level restarts when the ad is dismissed

#### Scenario: During a game
- **WHEN** a level is in progress
- **THEN** no ad is shown

#### Scenario: App start and exit
- **WHEN** the app starts or the player quits
- **THEN** no ad is shown

### Requirement: No ad, no delay
If no ad is loaded, the level SHALL start immediately. The player SHALL NOT wait for an ad to load, and a failed ad SHALL NOT block the level.

#### Scenario: Ad not loaded
- **WHEN** a level starts and no ad is ready
- **THEN** the level starts at once, with no loading indicator and no pause

#### Scenario: Ad fails to show
- **WHEN** an ad is ready but fails to show
- **THEN** the level starts at once

### Requirement: Consent before any ad request
The app SHALL NOT request an ad, interstitial or banner, before the consent flow has resolved. Ads SHALL be requested only when the resolved consent state allows ad requests.

#### Scenario: Consent not resolved
- **WHEN** the consent flow has not finished
- **THEN** no ad is requested

#### Scenario: Consent does not allow ads
- **WHEN** the consent flow has resolved and does not allow ad requests
- **THEN** no ad is requested, and levels start with no ad

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
- **THEN** the request uses a test ad unit

#### Scenario: Release build
- **WHEN** a release build requests an ad
- **THEN** the request uses the app's own ad unit for that format

### Requirement: Advertising ID permission declared
The Android manifest SHALL declare `com.google.android.gms.permission.AD_ID`.

#### Scenario: Merged manifest
- **WHEN** the app is built
- **THEN** the merged manifest contains the `com.google.android.gms.permission.AD_ID` permission
