# runway-markings Specification

## Purpose
Defines how a runway's painted markings show the player where it can be landed on, so a one-way runway reads as one-way by its shape before anyone tries it.

## Requirements

### Requirement: Markings at each landing end
A runway SHALL show threshold bars, a touchdown patch and its runway number at each end it can be landed from, and at no other end. The number SHALL be the landing heading in tens of degrees, read from the approach.

#### Scenario: Two-way runway
- **WHEN** a two-way runway is drawn
- **THEN** both ends show threshold bars, a touchdown patch and a number, and the two numbers differ by 18

#### Scenario: One-way runway
- **WHEN** a one-way runway is drawn
- **THEN** only the landing end shows threshold bars, a touchdown patch and a number, and the far end shows none of them

### Requirement: One-way direction by shape
A one-way runway SHALL draw its centre line as a row of chevrons pointing in its landing direction, in place of the dashes. A two-way runway SHALL keep the dashed centre line. The chevrons SHALL use the runway marking colour and design-system tokens for stroke, size and spacing.

#### Scenario: Chevrons point the landing way
- **WHEN** a one-way runway with landing heading west is drawn
- **THEN** its centre line is a row of chevrons, each pointing west

#### Scenario: Two-way centre line unchanged
- **WHEN** a two-way runway is drawn
- **THEN** its centre line is the dashed line, with no chevrons

#### Scenario: Readable without colour
- **WHEN** a one-way and a two-way runway are shown in greyscale
- **THEN** the one-way runway is still told apart by its chevrons and its single marked end
