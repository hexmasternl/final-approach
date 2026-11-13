# Spec Delta

## Purpose

The player's preferences: what they are, their defaults, how they are kept on the device, and how they change the rest of the game.

## ADDED Requirements

### Requirement: Default settings
On a fresh install, Sound effects SHALL be on and Reduce motion SHALL be Device.

#### Scenario: First start
- **WHEN** the game starts for the first time after installation
- **THEN** Sound effects is on and Reduce motion is Device

### Requirement: Settings survive restarts
The player's settings SHALL be stored on the device and SHALL be the same after the app is closed and started again.

#### Scenario: Restart keeps choices
- **WHEN** the player switches Sound effects off, selects Reduce motion On, and restarts the app
- **THEN** Sound effects is still off and Reduce motion is still On

### Requirement: Settings stay on the device
The player's settings SHALL NOT be sent anywhere and SHALL NOT be included in device backups.

#### Scenario: Not backed up
- **WHEN** the app is reinstalled from a device backup or restored on a new device
- **THEN** the settings are back at their defaults

### Requirement: Sound effects off mutes all sound effects
While Sound effects is off, the game SHALL play no sound effect. Switching it back on SHALL let later sound effects play again.

#### Scenario: Muted clearance
- **WHEN** Sound effects is off and the player clears an aircraft to land
- **THEN** no sound plays

#### Scenario: Unmuted again
- **WHEN** the player switches Sound effects back on and then clears an aircraft to land
- **THEN** the success sound plays

### Requirement: Effective reduced motion
The game SHALL use one effective reduced-motion value. With Reduce motion set to Device it SHALL equal the device's reduced-motion setting; with On it SHALL be true and with Off it SHALL be false, whatever the device says.

#### Scenario: Device follows the device
- **WHEN** Reduce motion is Device and the device has reduced motion enabled
- **THEN** the effective value is on

#### Scenario: Device with no device setting
- **WHEN** Reduce motion is Device and the device does not have reduced motion enabled
- **THEN** the effective value is off

#### Scenario: On overrides the device
- **WHEN** Reduce motion is On and the device does not have reduced motion enabled
- **THEN** the effective value is on

#### Scenario: Off overrides the device
- **WHEN** Reduce motion is Off and the device has reduced motion enabled
- **THEN** the effective value is off

### Requirement: Reduced-motion behaviour follows the effective value
Every behaviour any spec describes for reduced motion, or for a device with reduced motion enabled, SHALL apply exactly when the effective reduced-motion value is on. This covers the splash page, main page, flight paths, proximity ring, scenery, HUD, dialogs and landing.

#### Scenario: Override on the main page
- **WHEN** Reduce motion is On on a device without reduced motion and the main page is shown
- **THEN** the stripes and clouds stand still and no aircraft fly past

#### Scenario: Override in the game
- **WHEN** Reduce motion is Off on a device with reduced motion enabled and a level is played
- **THEN** the proximity ring spins and the paths animate as they do without reduced motion

#### Scenario: Change takes effect without restart
- **WHEN** the player changes Reduce motion on the settings page and returns to the main page
- **THEN** the main page follows the new effective value without restarting the app
