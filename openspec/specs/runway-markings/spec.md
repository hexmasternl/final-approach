# runway-markings Specification

## Purpose
Defines how a runway's painted markings show the player where it can be landed on, so a one-way runway reads as one-way by its shape before anyone tries it.

## Requirements

### Requirement: Markings at each landing end
A runway SHALL show six threshold bars and its runway number at each end it can be landed from, and at no other end. A runway long enough for them SHALL also show two aiming blocks, and a longer one touchdown marks behind them, at each landing end. The number SHALL be the landing heading in tens of degrees, read from the approach.

#### Scenario: Two-way runway
- **WHEN** a two-way runway is drawn
- **THEN** both ends show six threshold bars and a number, and the two numbers differ by 18

#### Scenario: One-way runway
- **WHEN** a one-way runway is drawn
- **THEN** only the landing end shows threshold bars, a number and aiming blocks, and the far end is plain asphalt

#### Scenario: Short runway
- **WHEN** a runway shorter than 460 world units is drawn
- **THEN** its landing ends show threshold bars and a number, and no aiming blocks

#### Scenario: Long runway
- **WHEN** a runway of 560 world units or longer is drawn
- **THEN** its landing ends show threshold bars, a number, aiming blocks and touchdown marks
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

### Requirement: Runway surround
A runway SHALL keep its asphalt rectangle at the size and place of its level data, outlined in the `outline` colour, with an edge line along both sides. Around it SHALL lie a concrete shoulder and, around that, a fringe of rough grass.

#### Scenario: Same footprint
- **WHEN** a runway is drawn
- **THEN** its outlined asphalt rectangle has the length, width, centre and heading of its level data

#### Scenario: Shoulder and fringe
- **WHEN** a runway lies in the mown airfield
- **THEN** a concrete shoulder and a rough-grass fringe lie between the asphalt and the lawn on every side

### Requirement: Runway lights
A runway SHALL show edge lights on its shoulder at even spacing along both sides, threshold lights just outside each landing end, and approach lights in the fringe beside one landing end; on a one-way runway that is the landing end.

#### Scenario: Edge and threshold lights
- **WHEN** a two-way runway is drawn
- **THEN** edge lights line both sides on the shoulder, and four threshold lights lie outside each end

#### Scenario: One-way runway lights
- **WHEN** a one-way runway is drawn
- **THEN** threshold lights and approach lights lie at the landing end only
