# Spec Delta

## Purpose

The published privacy policy of Final Approach: what it must tell players and parents about the game's data handling, that it stays true to what the app does, and where it can be read.

## ADDED Requirements

### Requirement: Policy matches the app
The privacy policy SHALL describe the data handling of the current release and SHALL NOT contain a statement that contradicts it. When a release changes what the app or its SDKs collect, send or store, the policy and its "Last updated" date SHALL be updated before that release.

#### Scenario: Ads in the app
- **WHEN** the release shows ads
- **THEN** the policy does not say the game has no advertising software, makes no network connections or shares no data with third parties

#### Scenario: Data handling changes
- **WHEN** a release adds or removes an SDK, permission or kind of stored data
- **THEN** the policy and its "Last updated" date are updated in the same release

### Requirement: Ads and consent are explained
The policy SHALL state that the game shows ads from Google AdMob, before a level starts and as a banner during play. It SHALL state that a consent message from Google may be shown before any ad, and that the game plays as normal with no ads when consent is not given or the message cannot load.

#### Scenario: Reading about ads
- **WHEN** a player reads the policy
- **THEN** it names Google AdMob, the two places ads appear, and the consent message

### Requirement: Data Google may collect is named
The policy SHALL name the kinds of information Google may collect to serve ads, such as IP address, device and app details and ad interactions, and what it is used for. It SHALL say ads are never personalised and that the advertising ID is not used. It SHALL link to Google's explanation of how it uses data from partner apps.

#### Scenario: Reading about data collection
- **WHEN** a player reads the policy
- **THEN** it lists what Google may collect, says ads are not personalised and the advertising ID is not used, and links to Google's partner-apps page

### Requirement: Children are addressed
The policy SHALL state that the game is meant for all ages, including children, that the game itself collects no personal information, that every ad request is treated as coming from a child, and that ads are limited to content suitable for general audiences.

#### Scenario: A parent reads the policy
- **WHEN** a parent reads the children section
- **THEN** it says the game collects no personal information, ads are child-directed, non-personalised and rated for general audiences

### Requirement: Permissions and on-device data are listed
The policy SHALL state that the game uses the internet only to load ads and the consent message, that it requests no other permissions, and that scores and settings stay on the device, are not backed up and are deleted on uninstall.

#### Scenario: Reading about permissions
- **WHEN** a player reads the policy
- **THEN** it says the internet is used only for ads and consent, and that scores and settings stay on the device

### Requirement: One public URL
The policy SHALL be published at https://github.com/hexmasternl/final-approach/blob/main/docs/privacy-policy.md. That URL SHALL be the one used in the Play listing and anywhere the app links to the policy.

#### Scenario: Opening the policy
- **WHEN** someone opens the published URL after a release
- **THEN** they see the policy that belongs to that release
