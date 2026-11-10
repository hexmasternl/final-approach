# Game Design – Final Approach

## Concept
A top-down, cartoon-style view of an airport. Aircraft enter the viewport from the edges and the player guides each one to a safe landing by drawing flight paths with a finger.

## Airport
- One or more **runways** (landing strips for airplanes).
- One or more **helipads** (landing spots for helicopters).
- Layouts are defined per level as data (positions, orientation, size).

## Aircraft
| Type | Kind | Speed | Turn rate | Lands on |
|------|------|-------|-----------|----------|
| Light prop | Airplane | Slow | High | Runway |
| Turboprop | Airplane | Medium | Medium | Runway |
| Jet | Airplane | Fast | Low | Runway |
| Jumbo | Airplane | Medium-fast | Very low | Runway |
| Helicopter | Helicopter | Slow | Very high | Helipad |

Values are tunable and live in configuration. Differing speeds are the core challenge: faster planes catch up with slower ones, so the player must sequence landings.

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
- Score: points per safe landing, bonus for streaks and tight sequencing.
- Difficulty rises over time: spawn rate and aircraft variety increase.

## Look and feel
Cartoon style; see `art-style.md`.

## Future ideas
Weather/wind, night levels, emergency aircraft (priority landing), more airports, iOS release.
