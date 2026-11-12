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
| Helicopter | Helicopter | 50 (slowest) | Very high | 10 | Helipad |
| Light prop | Airplane | 68.75 | High | 20 | Runway |
| Turboprop | Airplane | 93.75 | Medium | 30 | Runway |
| Jet | Airplane | 125 | Low | 50 | Runway |
| Jumbo | Airplane | 162.5 (fastest) | Very low | 80 | Runway |

Values are defaults in `DefaultAircraftSpecs` and are tunable. Each speed is its value in the former 1280 by 720 world (40, 55, 75, 100, 130) times the world zoom of 1.25 (see World size and zoom), so every type crosses the screen in the same time as before. Speed rises from helicopter to jumbo, each type at least 15% faster than the one before, so the gaps show in play. Differing speeds are the core challenge: faster planes catch up with slower ones, so the player must sequence landings. The jumbo is the fastest, balanced by its very low turn rate.

Every aircraft of a type flies at exactly the same speed, with no random variation, and its speed never changes during a run.

### Level overrides
A level can set a speed multiplier and per-type speeds, and a points multiplier and per-type points. A per-type value replaces the default; the multiplier then applies to the result (points are rounded, minimum 1). The effective values are resolved once when the level starts and stay fixed for the run. Multipliers and per-type values must be greater than zero; a level with a zero or negative value is rejected with the field named. Overrides need not keep the speed order.

## Controls
1. **Tap** an aircraft to select it. The pick area is at least 72dp, and the nearest aircraft wins.
2. **Drag** to draw a path; the path is shown as a dashed cartoon line.
3. The aircraft follows the path at its own speed.
4. Touching an aircraft removes its old path at once. A tap without a drag leaves it without a path, so it flies straight on in its current heading (cancel); a drag draws the replacement (redraw). An aircraft already cleared to land keeps its locked path.

## Flight behaviour
- When the player lifts their finger (or the path enters a landing zone), the drawn path is smoothed for that aircraft's type: finger jitter is removed, corners are cut, and no corner is left tighter than a little more than the type's turning radius (speed / turn rate). A gentle path stays within a few world units of what was drawn; a sharp corner becomes a curve the aircraft can fly. The end of the path stays exactly where it was drawn, so a path ending on a landing zone still lands.
- The aircraft follows its path strictly: it turns towards a point a short lookahead further along the path, no faster than its turn rate. Progress along the path only moves forward, so it never turns back to a point it has passed and never circles.
- On path end the aircraft flies straight on in its last heading (no auto-landing, no auto-turn).
- A path that enters a valid landing zone (runway threshold for airplanes, helipad for helicopters) clears the aircraft to land: the path is cut off where it entered the zone, flashes white once, and takes no more drag points. At a helipad the path ends at that entry point. At a runway the end of the path is snapped onto the runway centre line: a smooth lead-in leaves the drawn path about two turning radii before the zone, without a corner, and joins the centre line in the landing direction, so an entry near a side edge still lands in the middle of the runway. The shown path is the snapped path. Score is awarded at touchdown.
- A path over the wrong zone type, or over the middle of a runway, does nothing; the path continues as drawn.
- Aircraft leaving the viewport without landing counts as a miss. Only an aircraft that has been on screen can leave it: once it is beyond the leave margin, the level ends. Aircraft still entering are never counted.

## Landing rules
- Landing zones are invisible. A helipad's zone is a circle on the pad (radius configurable per helipad). A runway has a zone at each threshold it can be landed from: one for a one-way runway, two for a two-way runway, with opposite landing directions.
- Airplane: the path must enter a runway threshold zone with its direction within the alignment tolerance (default 30 degrees) of that zone's landing direction. A steep crossing or the wrong direction on a one-way runway does nothing.
- Helicopter: the path enters the helipad zone; no alignment is needed. It lands at the end of its path and is removed.
- Airplane touchdown and rollout: the airplane touches down at the end of its snapped path, on the centre line a short distance (default 40 world units) past the threshold, or level with the point where the path entered the zone when that is farther in. It then rolls on along the centre line in the zone's landing direction for two thirds of the runway length, then fades out and is removed. Every airplane rolls out at the same landing speed, the flight speed of the light prop: it touches down at its own flight speed and slows to the landing speed within 1 second, so a faster airplane landing behind a slower one does not catch up with it on the runway. On a two-way runway, the zone that was entered decides the direction. Airplanes rolling out on one runway in the same direction never collide with each other; rolling airplanes can still collide with anything else on the ground.
- With reduced motion the path does not flash; it turns solid white until the aircraft lands.

## Failure and scoring
- **Collision**: two aircraft collide when their shapes touch: each type has a collision shape traced from its sprite (a few circles along fuselage and wings), turned with the aircraft. Flying close is allowed; touching ends the level. The test is swept over each simulation step, so fast aircraft cannot pass through each other. Every aircraft is either In Flight or Landed (a state, no numeric altitude). It spawns In Flight and becomes Landed at touchdown, just after the start of the runway or on the pad; it stays Landed during rollout. Only aircraft with the same state collide: a flying aircraft passes over one that has landed, and two airplanes rolling out on one runway in the same direction (fading ones included) never collide, because all airplanes roll out at the light prop's speed. Rollouts in opposite directions, or on different runways, still collide. Removed aircraft never collide.
- **Left the area**: an aircraft that has been on screen and leaves it, beyond the leave margin, without landing ends the level.
- **Game over**: the first collision or departure ends the level at once. The game freezes on that frame and remembers why and which aircraft; input and spawning stop. A game-ended dialog then shows what happened, how many aircraft of each type landed, the total and the points, with Try again and Main page. A full-width ad banner sits directly under the dialog.
- **HUD**: one compact row along the top edge, as the design system's Hud Bar. Top left the best score for the level (compact, with a star) and the current score; scores of 1000 or more group thousands with a thin space (`12 450`). The HUD stays inside the top band of the world that no landing zone enters. Top right a cog wheel pauses the game (no movement, spawning or input) and opens the options panel: Continue, Restart, Quit. Back opens the same panel. The best score is stored per level on the device.
- **Proximity warning**: while two aircraft are closer than the warning distance (centre to centre), both get a spinning dashed ring in the warning colour. It is only a warning; nothing fails until the shapes touch.
- Score: each safe landing adds the landed type's effective points (helicopter least, jumbo most). Collisions and misses add nothing. Bonuses for streaks and tight sequencing may come later.
- Difficulty rises over time: spawn rate and aircraft variety increase.

## World size and zoom
Every level is played in a world of 1600 by 900 world units: the former 1280 by 720 world times one zoom factor, `LevelDefaults.WORLD_ZOOM` = 1.25, the same for all levels. The camera is pulled back: runways, helipads, landing zones and scenery keep their world sizes, so the airfield takes a smaller share of the screen and its surroundings show on every side.

What belongs to an aircraft or to the player's finger is multiplied by the same factor, so it is unchanged on screen:

| Multiplied by the zoom | Before | Now |
|---|---|---|
| Aircraft speed | 40 to 130 | 50 to 162.5 |
| Aircraft radius, and with it sprite size and collision shape | 24 to 44 | 30 to 55 |
| Warning distance | 150 | 187.5 |
| Spawn separation | 160 | 200 |
| Spawn margin and leave margin | 60 | 75 |
| HUD band at the top of the world | 160 | 200 |
| Path reach radius, minimum point spacing, smoothing tolerance | 6, 12, 4 | 7.5, 15, 5 |

Turn rates, all timings, the landing tolerance and every fraction stay; turning circles (speed divided by turn rate) therefore grow with the zoom and look the same on screen. Runway width (64), helipad radius (56), the landing zone sizes and the touchdown distance stay too, so on screen they are a fifth smaller: on an 800 by 360dp phone a runway is 25.6dp wide and a helipad 44.8dp across. The pick area and the path strokes are in dp and do not change. Any new world-unit distance that describes an aircraft or the finger must be written as its base value times `WORLD_ZOOM`.

## Levels
A level is a `LevelConfig` in code (`domain/Levels.kt`), validated before it is played. It holds:
- **World:** the viewport size in world units (1600 by 900 for every level), the spawn margin, the leave margin, the spawn separation, the warning distance and the longest simulation step.
- **Zones:** runways (position, axis orientation, length, width, mode) and helipads (position, radius). Zones lie inside the viewport and never overlap.
- **Runway mode:** *one-way* accepts landings on one heading only; *two-way* accepts both ends, and the approach picks the end. Approaches within the landing tolerance (default 30 degrees) of a landing heading count as aligned. The runway number is the landing heading in tens of degrees (`09`, `27`).
- **Difficulty curve:** a list of phases. Each phase starts at a level time and sets the spawn interval range, the weight of each allowed aircraft type and the maximum number of aircraft in the air. The active phase is the last one that has started; phases step, they do not blend.
- **Run rules:** an optional seed (none means a fresh one per run), an optional duration (none means endless) and an optional landing goal.
- **Scoring, wind, emergencies, hints:** lives, a points multiplier, and slots for later features that default to off.

### Build-up
The spawner draws every random number from one seeded generator, so a seed replays a run exactly. The first spawn comes after a short delay; after each spawn the next interval is drawn from the active phase's range, and the type is picked by weight. At the phase's aircraft cap the spawn waits until an aircraft lands or leaves.

Level 1, **Meadow Field**, is played in landscape. One two-way runway (09 on the left end, 27 on the right) lies left to right in the middle, with a helipad on the right, on a striped lawn. A taxiway, an apron with parked aircraft and vehicles, a terminal with a control tower, a hangar, a windsock and trees make it read as a small airfield. Around it lies farmland: crop fields with rows, hedges, a fence, a farmhouse and a barn, two roads, a pond, a forest edge and a paddock with cows and a farmer. All of this decoration is level data and never covers a landing zone. It opens with only light props and helicopters (two at a time), adds turboprops after 45 seconds and all five types after two minutes, with at most three aircraft in the air.

Levels 2 and 3 are set on an island (a beach with waves, palms and a jetty, and open sea all round) and a savanna at dawn (acacias, bushes, rocks, dirt roads, a waterhole, zebras and an elephant). Runways can carry an accepted-types list, and a level may allow crossing runways.

### Scenery
Scenery is decoration only: no game rule reads it, and a seeded run plays out the same with a level's scenery removed. It is level data (`SceneryItem`: a kind, a centre, a size and a rotation, in world units) and is validated with the level:
- No scenery covers a runway, a helipad or a landing zone.
- Only flat ground (taxiways, aprons, fields, roads, ponds, waterholes, jetties) may lie under the HUD band; buildings, plants, vehicles, animals and people stay below it, and so do the ambient birds and boats.
- A parked aircraft stands on an apron. It is three quarters of the light prop's size, has no livery and no shadow, and cannot be selected.

Ambient life is decoration too: cows wander inside the paddock beside a farmer, a flock of three birds with flapping wings crosses now and then, a boat with a wake sails past the island, and at dawn the tower beacon and the aircraft lights glow. With reduced motion no bird or boat appears, wings and cows hold still, and the lights are steady. How scenery looks is set by the design system (`docs/design-system/world.md`).

### Fair entry
Aircraft enter just outside a permitted edge (by the spawn margin) and head for a random point in the central safe region (the middle half of each axis), plus a small random jitter (up to 15 degrees). An entry is only used when its straight line crosses at least half of the viewport's shorter side and stays on screen for at least four seconds at the aircraft's speed, and when it lies at least the spawn separation from every aircraft, including those not yet on screen. Otherwise the spawner tries again, up to eight times, and then delays the spawn briefly.

## Look and feel
Cartoon style; see `art-style.md`.

## Future ideas
Weather/wind, night levels, emergency aircraft (priority landing), more airports, iOS release.
