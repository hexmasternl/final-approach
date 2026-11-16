## ADDED Requirements

### Requirement: Pressure keeps rising after the last phase
On a level with a difficulty ramp, the spawner SHALL use the active ramp step after the last configured phase exactly as it uses a phase: the spawn interval range, the type weights and the maximum in the air come from that step. Level time for the ramp SHALL count from the end of the tutorial when one ran, as for the phases. Seeded replay, fair entry and spawn separation SHALL keep applying unchanged.

#### Scenario: More aircraft allowed later
- **WHEN** Meadow Field has 3 aircraft in the air and a spawn is due, once at 3:30 and once at 4:30
- **THEN** no aircraft spawns at 3:30, and at 4:30 the cap no longer holds the spawn back

#### Scenario: Interval follows the ramp
- **WHEN** an aircraft spawns during a ramp step
- **THEN** the time until the next spawn lies within that step's minimum and maximum interval

#### Scenario: Pace keeps growing
- **WHEN** a level with a ramp is simulated over many seeds with room for every spawn
- **THEN** the average number of spawns per minute after the final ramp step is higher than in the last configured phase

#### Scenario: Seeded replay
- **WHEN** a level with a ramp is run twice with the same seed and the same inputs past its final ramp step
- **THEN** both runs spawn the same aircraft types at the same times, positions and headings

#### Scenario: Fair entry and separation at full pressure
- **WHEN** aircraft spawn after the final ramp step
- **THEN** every spawn meets the fair entry rule and the spawn separation

#### Scenario: Ramp counts from the end of the tutorial
- **WHEN** the Meadow Field tutorial ends at level time T
- **THEN** the last configured phase is still active at T plus 2:59, and ramp step 1 is active from T plus 3:00
