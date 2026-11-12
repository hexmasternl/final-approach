# flight-path-following Specification

## Purpose
Defines how a player-drawn flight path is smoothed into a flyable line and how an aircraft stays on that line until it ends.

## Requirements

### Requirement: Drawn paths are smoothed
The system SHALL smooth a drawn path so that no corner is sharper than the aircraft's turn rate allows at its speed.

#### Scenario: Sharp corner is eased
- **WHEN** the player draws a path with a near-reversal (for example a 150° turn)
- **THEN** the stored path replaces the corner with a curve the aircraft can fly

#### Scenario: Gentle path stays close
- **WHEN** the player draws a path with only gentle turns
- **THEN** the smoothed path stays within a small tolerance of the drawn line

### Requirement: Smoothing preserves endpoints
The system SHALL keep the first and last points of a path at their drawn positions.

#### Scenario: Landing zone end
- **WHEN** a path ends on a valid landing zone
- **THEN** the smoothed path still ends on that zone and the aircraft can land

### Requirement: Aircraft strictly follows its path
The aircraft SHALL stay on its path, SHALL NOT overshoot corners, and SHALL NOT turn back to the start of the path.

#### Scenario: Sharp turn is followed
- **WHEN** an aircraft follows a smoothed path through a tight corner
- **THEN** it stays within a small tolerance of the line and does not circle

#### Scenario: Path end
- **WHEN** the aircraft reaches the end of the path
- **THEN** it continues straight in its last heading
