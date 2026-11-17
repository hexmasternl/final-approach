# level-unlocks Specification

## Purpose
TBD - created by archiving change level-unlocks. Update Purpose after archive.

## Requirements

### Requirement: Meadow field is always open
The first level, Meadow field, SHALL always be unlocked.

#### Scenario: Fresh install
- **WHEN** the app is used for the first time
- **THEN** Meadow field can be started

### Requirement: Island airport needs 500 landings on Meadow field
Island airport SHALL be locked until the total number of aircraft landed on Meadow field, summed over all runs, is 500 or more.

#### Scenario: 499 landings
- **WHEN** the total landings on Meadow field are 499
- **THEN** Island airport is locked

#### Scenario: 500 landings
- **WHEN** the total landings on Meadow field are 500
- **THEN** Island airport is unlocked

#### Scenario: Summed over runs
- **WHEN** the player has landed 300 aircraft in one run and 200 in another on Meadow field
- **THEN** Island airport is unlocked

#### Scenario: Already past the new threshold
- **WHEN** the device holds 750 total landings on Meadow field from before this change, and Island airport was never marked unlocked
- **THEN** Island airport is unlocked when the level selection page is opened, without another run

### Requirement: Savanna dawn needs 30,000 points on Island airport
Savanna dawn SHALL be locked until the total points earned on Island airport, summed over all runs, are 30,000 or more.

#### Scenario: 29,999 points
- **WHEN** the total points on Island airport are 29,999
- **THEN** Savanna dawn is locked

#### Scenario: 30,000 points
- **WHEN** the total points on Island airport are 30,000
- **THEN** Savanna dawn is unlocked

### Requirement: Totals are counted when a run ends
When a run ends, its landed aircraft and its points SHALL be added once to the totals of its level. A run ends when the level is lost, or when the player leaves it through Restart or Quit in the options panel. Totals SHALL be kept on the device and survive a restart of the app.

#### Scenario: Lost run counts
- **WHEN** a run on Meadow field ends in a collision after 12 landings
- **THEN** the Meadow field total landings rise by 12

#### Scenario: Quit counts
- **WHEN** the player quits a run on Island airport that has scored 800 points
- **THEN** the Island airport total points rise by 800

#### Scenario: Counted once
- **WHEN** a run has ended and the game-ended dialog stays open
- **THEN** the totals do not rise again

#### Scenario: Survives restart
- **WHEN** the app is closed and opened again
- **THEN** the totals are unchanged

### Requirement: An unlocked level stays unlocked
Once a level is unlocked it SHALL stay unlocked, whatever happens to the totals. A level SHALL also count as unlocked when the player already has a best score on it or on a later level, so players from before this rule lose nothing.

#### Scenario: Stays unlocked
- **WHEN** Island airport was unlocked and the app is opened again
- **THEN** Island airport is unlocked

#### Scenario: Existing player
- **WHEN** the device holds a best score for Savanna dawn but no totals
- **THEN** Island airport and Savanna dawn are unlocked

### Requirement: Thresholds are configuration
The landings and points thresholds SHALL be defined in one config, not in rule or screen code.

#### Scenario: Changed threshold
- **WHEN** the configured landings threshold is changed in config
- **THEN** the unlock rule follows without any other code change
