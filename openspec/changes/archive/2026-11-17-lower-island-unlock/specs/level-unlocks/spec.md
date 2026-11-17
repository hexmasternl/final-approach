## REMOVED Requirements

### Requirement: Island airport needs more than 1,000 landings on Meadow field
**Reason**: The bar is too high; players stay on the first level too long.
**Migration**: Replaced by "Island airport needs 500 landings on Meadow field". No data migration: unlocks are judged from the stored totals, so players with 500 or more landings find Island open.

## ADDED Requirements

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
