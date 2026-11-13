## ADDED Requirements

### Requirement: Screen stays on while a level runs

While the Keep screen on setting is on and a level is running, the app SHALL keep the device screen on, so it neither dims nor goes to standby for lack of touches. A level is running when it is neither paused (options panel open), waiting for the interstitial ad before a level or restart, nor over (game-ended dialog shown).

#### Scenario: Whole level without touches

- **WHEN** Keep screen on is on and a level runs without any touch for longer than the device's screen timeout
- **THEN** the screen stays on and does not dim or lock

#### Scenario: Rule is on for a running level

- **WHEN** the setting is on, the level is not paused and the level is not over
- **THEN** the screen is kept on

### Requirement: Normal timeout outside a running level

Whenever no level is running, the app SHALL leave the device's normal screen timeout in effect, whatever the setting.

#### Scenario: Paused level

- **WHEN** Keep screen on is on and the options panel is open
- **THEN** the screen is not kept on and the device's normal timeout applies

#### Scenario: Game-ended dialog

- **WHEN** Keep screen on is on and the game-ended dialog shows
- **THEN** the screen is not kept on

#### Scenario: Waiting for the interstitial

- **WHEN** Keep screen on is on and the level waits for the interstitial ad before it starts or restarts
- **THEN** the screen is not kept on

#### Scenario: Other pages

- **WHEN** Keep screen on is on and the splash page, main page, level select page or settings page shows
- **THEN** the screen is not kept on

#### Scenario: Leaving the level

- **WHEN** the screen is kept on during a level and the player quits to the main page
- **THEN** the screen is no longer kept on

### Requirement: Setting off restores the normal timeout during play

While the Keep screen on setting is off, the app SHALL leave the device's normal screen timeout in effect during a running level.

#### Scenario: Setting off during play

- **WHEN** Keep screen on is off and a level is running
- **THEN** the screen is not kept on and the device dims and locks after its normal timeout

### Requirement: Keep screen on toggle on the settings page

The settings page SHALL show a Keep screen on toggle in a Gameplay section, with the label "Keep screen on" and the description "While a level is playing". It SHALL be on by default, and a change SHALL apply at once.

#### Scenario: Default on first launch

- **WHEN** the player opens the settings page on a fresh install
- **THEN** a Gameplay section shows the Keep screen on toggle, switched on


### Requirement: Keep screen on choice persists

The app SHALL save the Keep screen on choice on the device with the other settings and SHALL restore it after the app restarts.

#### Scenario: Choice survives a restart

- **WHEN** the player turns Keep screen on off, closes the app and opens it again
- **THEN** the settings page shows the toggle off and the normal timeout applies during a level

### Requirement: No wake lock permission

Keeping the screen on SHALL work without any extra permission: the app MUST NOT declare the `WAKE_LOCK` permission and MUST NOT use wake locks.

#### Scenario: Merged manifest

- **WHEN** the release build's merged Android manifest is inspected
- **THEN** it holds no `android.permission.WAKE_LOCK` entry
