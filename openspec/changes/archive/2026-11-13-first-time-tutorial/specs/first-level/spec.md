## MODIFIED Requirements

### Requirement: Take off opens the first level
Tapping Take off on the main page SHALL open the game screen with the Meadow Field level. For a player who has not completed the tutorial, the level SHALL start with the tutorial's first step; otherwise aircraft SHALL start to arrive as usual. Back SHALL return to the main page.

#### Scenario: Take off
- **WHEN** a player who has completed the tutorial taps Take off
- **THEN** the game screen shows Meadow Field and aircraft start to arrive

#### Scenario: Take off for the first time
- **WHEN** a player who has not completed the tutorial taps Take off
- **THEN** the game screen shows Meadow Field with the tutorial's goal step and no aircraft

#### Scenario: Back
- **WHEN** the player presses back on the game screen
- **THEN** the main page is shown
