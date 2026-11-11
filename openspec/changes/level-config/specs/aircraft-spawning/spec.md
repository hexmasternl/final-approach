## ADDED Requirements

### Requirement: Spawning is driven by the level config
The spawner SHALL take all its decisions from the active level config and a seeded random source. It SHALL contain no level-specific constants.

#### Scenario: Same seed, same sequence
- **WHEN** the spawner runs twice with the same config, the same seed and the same time steps
- **THEN** it produces the same spawns (time, type, edge, position and heading) both times

#### Scenario: Different seed, different sequence
- **WHEN** the spawner runs with two different seeds
- **THEN** the spawn sequences differ

### Requirement: Build-up over time
The spawner SHALL spawn only aircraft types allowed in the active phase, picked by the phase weights. The next spawn time SHALL be chosen at random within the active phase's spawn interval range. Across the phases of a level, the spawn interval SHALL be able to shorten and the allowed types to grow, as the config says.

#### Scenario: First phase types
- **WHEN** the level is in its first phase
- **THEN** every spawned aircraft is of a type allowed in the first phase

#### Scenario: Interval within the phase range
- **WHEN** an aircraft spawns in a phase
- **THEN** the time until the next spawn lies within that phase's minimum and maximum interval

#### Scenario: Pace grows
- **WHEN** a level whose phases shorten the interval is simulated over many seeds
- **THEN** the average number of spawns per minute in a later phase is higher than in an earlier phase

#### Scenario: Variety grows
- **WHEN** a later phase allows more types than the first
- **THEN** over many seeds more than the first phase's types are spawned in that later phase

#### Scenario: Weights
- **WHEN** a phase gives one type a higher weight than another
- **THEN** over many spawns the heavier type is spawned more often

### Requirement: Maximum aircraft in the air
The spawner SHALL NOT spawn an aircraft while the number of aircraft in the air is at or above the active phase's maximum. Spawning resumes when an aircraft lands or leaves.

#### Scenario: At the cap
- **WHEN** the aircraft in the air equal the phase maximum and a spawn is due
- **THEN** no aircraft spawns

#### Scenario: Room again
- **WHEN** an aircraft lands and a spawn is due
- **THEN** an aircraft spawns

### Requirement: Spawn position and edges
An aircraft SHALL spawn on a chosen edge of the viewport, outside it by the spawn margin. A level MAY restrict the edges that are used, and the spawner SHALL use only the permitted edges.

#### Scenario: Outside the viewport
- **WHEN** an aircraft spawns
- **THEN** it is at the spawn margin distance outside the viewport

#### Scenario: Restricted edges
- **WHEN** a level permits only the left and top edges
- **THEN** every spawn is on the left or top edge

### Requirement: Fair entry trajectories
The heading of a spawned aircraft SHALL be aimed at a random target point inside a central safe region of the viewport, with a bounded angle jitter. A spawn whose straight trajectory stays on screen for less than the minimum time on screen, or crosses the viewport with a chord shorter than the minimum chord length (a fraction of the viewport), SHALL be rejected and re-rolled. If no fair entry is found within the allowed number of tries, the spawn SHALL be delayed instead of used.

#### Scenario: Heads into the viewport
- **WHEN** an aircraft spawns
- **THEN** its heading takes it into the viewport

#### Scenario: No corner clipping
- **WHEN** a drawn entry would only clip a corner of the viewport
- **THEN** it is rejected and another entry is rolled

#### Scenario: Minimum time on screen
- **WHEN** an aircraft spawns and flies straight on at its speed
- **THEN** it stays in the viewport for at least the minimum time on screen

#### Scenario: Minimum chord
- **WHEN** an aircraft spawns and flies straight on
- **THEN** its path through the viewport is at least the minimum chord length

#### Scenario: Property over many seeds
- **WHEN** many seeds and levels are simulated
- **THEN** no spawned trajectory breaks the fair entry rule

#### Scenario: No fair entry found
- **WHEN** every try within the allowed number fails the fair entry rule
- **THEN** no aircraft spawns now and the spawner tries again later

### Requirement: Separation at spawn
The spawner SHALL NOT spawn an aircraft within the level's spawn separation of another aircraft, including one that is still entering the viewport.

#### Scenario: Entry point is occupied
- **WHEN** an entry point lies within the spawn separation of another aircraft
- **THEN** another entry is rolled, or the spawn is delayed

#### Scenario: Entering aircraft counts
- **WHEN** another aircraft is outside the viewport but within the spawn separation of the entry point
- **THEN** it blocks that entry point
