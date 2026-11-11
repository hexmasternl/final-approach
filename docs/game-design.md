# Game Design – Final Approach

## Concept
A top-down, cartoon-style view of an airport. Aircraft enter the viewport from the edges and the player guides each one to a safe landing by drawing flight paths with a finger.

## Airport
- One or more **runways** (landing strips for airplanes).
- One or more **helipads** (landing spots for helicopters).
- Layouts are defined per level as data (positions, orientation, size).

## Aircraft
| Type | Kind | Speed (units/s) | Turn rate | Landing points | Lands on |
|------|------|-----------------|-----------|----------------|----------|
| Helicopter | Helicopter | 40 (slowest) | Very high | 10 | Helipad |
| Light prop | Airplane | 55 | High | 20 | Runway |
| Turboprop | Airplane | 75 | Medium | 30 | Runway |
| Jet | Airplane | 100 | Low | 50 | Runway |
| Jumbo | Airplane | 130 (fastest) | Very low | 80 | Runway |

Values are defaults in `DefaultAircraftSpecs` and are tunable. Speed rises from helicopter to jumbo, each type at least 15% faster than the one before, so the gaps show in play. Differing speeds are the core challenge: faster planes catch up with slower ones, so the player must sequence landings. The jumbo is the fastest, balanced by its very low turn rate.

Every aircraft of a type flies at exactly the same speed, with no random variation, and its speed never changes during a run.

### Level overrides
A level can set a speed multiplier and per-type speeds, and a points multiplier and per-type points. A per-type value replaces the default; the multiplier then applies to the result (points are rounded, minimum 1). The effective values are resolved once when the level starts and stay fixed for the run. Multipliers and per-type values must be greater than zero; a level with a zero or negative value is rejected with the field named. Overrides need not keep the speed order.

## Controls
1. **Tap** an aircraft to select it.
2. **Drag** to draw a path; the path is shown as a dashed cartoon line.
3. The aircraft follows the path at its own speed.
4. Drawing a new path replaces the old one.

## Flight behaviour
- On path end the aircraft flies straight on in its last heading (no auto-landing, no auto-turn).
- Path ends on a valid landing zone (runway for airplanes, helipad for helicopters) → aircraft lands and disappears; score awarded.
- Path ends on the wrong zone type → no landing; aircraft keeps flying straight.
- Aircraft leaving the viewport without landing counts as a miss.

## Landing rules
- Airplane: path must end on the runway, roughly aligned with its axis (angle tolerance configurable).
- Helicopter: path end within the helipad radius.

## Failure and scoring
- **Collision**: two aircraft closer than a minimum separation → lose a life / game over (per difficulty).
- **Miss**: an aircraft leaves the screen → lose a life.
- Score: each safe landing adds the landed type's effective points (helicopter least, jumbo most). Collisions and misses add nothing. Bonuses for streaks and tight sequencing may come later.
- Difficulty rises over time: spawn rate and aircraft variety increase.

## Levels
A level is a `LevelConfig` in code (`domain/Levels.kt`), validated before it is played. It holds:
- **World:** the viewport size in world units, the spawn margin and the minimum separation.
- **Zones:** runways (position, axis orientation, length, width, mode) and helipads (position, radius). Zones lie inside the viewport and never overlap.
- **Runway mode:** *one-way* accepts landings on one heading only; *two-way* accepts both ends, and the approach picks the end. Approaches within the landing tolerance (default 30 degrees) of a landing heading count as aligned. The runway number is the landing heading in tens of degrees (`09`, `27`).
- **Difficulty curve:** a list of phases. Each phase starts at a level time and sets the spawn interval range, the weight of each allowed aircraft type and the maximum number of aircraft in the air. The active phase is the last one that has started; phases step, they do not blend.
- **Run rules:** an optional seed (none means a fresh one per run), an optional duration (none means endless) and an optional landing goal.
- **Scoring, wind, emergencies, hints:** lives, a points multiplier, and slots for later features that default to off.

### Build-up
The spawner draws every random number from one seeded generator, so a seed replays a run exactly. The first spawn comes after a short delay; after each spawn the next interval is drawn from the active phase's range, and the type is picked by weight. At the phase's aircraft cap the spawn waits until an aircraft lands or leaves.

Level 1, **Meadow Field**, opens with only light props and helicopters (two at a time), adds turboprops after 45 seconds and all five types after two minutes, with at most three aircraft in the air.

### Fair entry
Aircraft enter just outside a permitted edge (by the spawn margin) and head for a random point in the central safe region (the middle half of each axis), plus a small random jitter (up to 15 degrees). An entry is only used when its straight line crosses at least half of the viewport's shorter side and stays on screen for at least four seconds at the aircraft's speed, and when it lies at least the minimum separation from every aircraft, including those not yet on screen. Otherwise the spawner tries again, up to eight times, and then delays the spawn briefly.

## Look and feel
Cartoon style; see `art-style.md`.

## Future ideas
Weather/wind, night levels, emergency aircraft (priority landing), more airports, iOS release.
