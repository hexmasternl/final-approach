## MODIFIED Requirements

### Requirement: Drawn paths are smoothed
The system SHALL smooth a drawn path by removing finger jitter and rounding corners, and SHALL NOT limit how sharply the path may turn: no turn radius or turn rate constrains the smoothed path.

#### Scenario: Sharp corner is eased
- **WHEN** the player draws a path with a near-reversal (for example a 150 degree turn)
- **THEN** the stored path replaces the corner with a short smooth curve that stays close to the drawn corner, and not with a wide loop

#### Scenario: Gentle path stays close
- **WHEN** the player draws a path with only gentle turns
- **THEN** the smoothed path stays within a small tolerance of the drawn line

### Requirement: Aircraft strictly follows its path
The aircraft SHALL move along its path at its type's speed with its heading along the path, SHALL stay on the line, SHALL NOT overshoot corners, and SHALL NOT turn back to the start of the path. No aircraft type SHALL be limited by a turn radius.

#### Scenario: Sharp turn is followed
- **WHEN** an aircraft of any type follows a smoothed path through a tight corner
- **THEN** it stays within a small tolerance of the line and does not circle or cut the corner

#### Scenario: Slow and fast types alike
- **WHEN** a jumbo and a helicopter follow the same path with the same corners
- **THEN** both follow the same line

#### Scenario: Path end
- **WHEN** the aircraft reaches the end of the path
- **THEN** it continues straight in its last heading
