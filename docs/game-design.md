# Game Design – Final Approach

## Concept
A top-down, cartoon-style view of an airport. Aircraft enter the viewport from the edges and the player guides each one to a safe landing by drawing flight paths with a finger.

## Airport
- One or more **runways** (landing strips for airplanes).
- One or more **helipads** (landing spots for helicopters).
- Layouts are defined per level as data (positions, orientation, size).

## Aircraft
| Type | Kind | Speed (units/s) | Turn rate (lead-in length only) | Landing points | Lands on |
|------|------|-----------------|-----------|----------------|----------|
| Helicopter | Helicopter | 30 (slowest) | Very high | 10 | Helipad |
| Light prop | Airplane | 55 | High | 20 | Runway |
| Turboprop | Airplane | 75 | Medium | 30 | Runway |
| Jet | Airplane | 100 | Low | 50 | Runway |
| Jumbo | Airplane | 97.5 | Very low | 80 | Runway |

Values are defaults in `DefaultAircraftSpecs` and are tunable. The default flight speeds live in one parameter file, `domain/AircraftSpeeds.kt`, at zoom factor 1: helicopter 20, light prop 36.67, turboprop 50, jet 66.67 and jumbo 65. `DefaultAircraftSpecs` multiplies them by the world zoom of 1.5 (see World size and zoom). Speed rises from helicopter to jet, each type at least 15% faster than the one before, so the gaps show in play; the jumbo (half the former size and half the former speed) is about as fast as the jet and is not part of that ordering. Differing speeds are the core challenge: faster planes catch up with slower ones, so the player must sequence landings.

Every aircraft of a type flies at exactly the same speed, with no random variation, and its speed never changes during a run.

### Level overrides
A level can set a speed multiplier and per-type speeds, and a points multiplier and per-type points. A per-type value replaces the default; the multiplier then applies to the result (points are rounded, minimum 1). The effective values are resolved once when the level starts and stay fixed for the run. Multipliers and per-type values must be greater than zero; a level with a zero or negative value is rejected with the field named. Overrides need not keep the speed order.

## Controls
1. **Tap** an aircraft to select it. The pick area is at least 72dp, and the nearest aircraft wins.
2. **Drag** to draw a path; the path is shown as a dashed cartoon line.
3. The aircraft follows the path at its own speed.
4. Touching an aircraft removes its old path at once. A tap without a drag leaves it without a path, so it flies straight on in its current heading (cancel); a drag draws the replacement (redraw). This holds for an aircraft cleared to land too: touching it cancels the clearance, and it can be re-routed until it has landed (an airplane at touchdown, a helicopter when its landing sequence starts).

## Flight behaviour
- When the player lifts their finger (or the path enters a landing zone), the drawn path is smoothed: finger jitter is removed and corners are rounded. There is no turn limit. A gentle path stays within a few world units of what was drawn; a sharp corner becomes a short curve. The end of the path stays exactly where it was drawn, so a path ending on a landing zone still lands.
- The aircraft follows its path exactly: it moves along it at its type's speed with its heading always along the path, so it stays on the line through every corner. Progress along the path only moves forward. When the path ends it flies straight on in its last heading.
- On path end the aircraft flies straight on in its last heading (no auto-landing, no auto-turn).
- A path that enters a valid landing zone (runway threshold for airplanes, helipad for helicopters) clears the aircraft to land: the path is cut off where it entered the zone, flashes white once, and takes no more drag points. At a helipad the path ends at that entry point. At a runway the end of the path is snapped onto the runway centre line: a smooth lead-in leaves the drawn path about two turning radii before the zone, without a corner, and joins the centre line in the landing direction, so an entry near a side edge still lands in the middle of the runway. The shown path is the snapped path. Score is awarded at touchdown.
- A path over the wrong zone type, or over the middle of a runway, does nothing; the path continues as drawn.
- An aircraft outside the level with no path counts as a miss. Only an aircraft that has been on screen can leave it: once it is beyond the leave margin and has no path, the level ends. An aircraft on a path may fly beyond the margin and back; if its path ends while it is out there, the level ends at that moment. Aircraft still entering are never counted.

## Landing rules
- Landing zones are invisible. A helipad's zone is a circle on the pad (radius configurable per helipad). A runway has a zone at each threshold it can be landed from: one for a one-way runway, two for a two-way runway, with opposite landing directions.
- Airplane: the path must enter a runway threshold zone with its direction within the alignment tolerance (default 30 degrees) of that zone's landing direction. A steep crossing or the wrong direction on a one-way runway does nothing.
- Helicopter: the path enters the helipad zone; no alignment is needed. At the end of its path it stands on the pad and lands through a landing sequence of 3 seconds (`helicopterLandingSeconds`, configurable), descending onto its shadow. During the sequence it cannot collide with any other aircraft, has no proximity ring, and cannot be picked or re-routed. When the sequence ends the landing is counted and scored once, and the helicopter is removed.
- Airplane touchdown and rollout: the airplane touches down at the end of its snapped path, on the centre line a short distance (default 32 times the world zoom, 48 world units) past the threshold, or level with the point where the path entered the zone when that is farther in. It then rolls on along the centre line in the zone's landing direction for two thirds of the runway length, then fades out and is removed. Every airplane rolls out at the same landing speed, half the flight speed of the light prop (the landing speed factor, 0.5): the light prop slows down too; every airplane touches down at its own flight speed and slows to the landing speed within 1 second, so a faster airplane landing behind a slower one does not catch up with it on the runway. On a two-way runway, the zone that was entered decides the direction. Airplanes rolling out on one runway in the same direction never collide with each other; rolling airplanes can still collide with anything else on the ground.
- With reduced motion the path does not flash; it turns solid white until the aircraft lands.
- A success sound plays once for every clearance, at the moment the path is cut off and flashes white: an airplane path entering a runway threshold zone within the alignment tolerance, or a helicopter path entering a helipad zone. It does not play for points dragged after the cut-off, for a path that ends in open air, misaligned, at the wrong end of a one-way runway or in a zone of the wrong type. A re-routed aircraft that is cleared again plays it again. The sound is kept with reduced motion; it follows the device media volume and does not score.

## Failure and scoring
- **Collision**: two aircraft collide when their shapes touch: each type has a collision shape traced from its sprite (a few circles along fuselage and wings), turned with the aircraft. Flying close is allowed; touching ends the level. The test is swept over each simulation step, so fast aircraft cannot pass through each other. Every aircraft is either In Flight or Landed (a state, no numeric altitude). It spawns In Flight and becomes Landed at touchdown, just after the start of the runway or on the pad; it stays Landed during rollout. Only aircraft with the same state collide: a flying aircraft passes over one that has landed, and two airplanes rolling out on one runway in the same direction (fading ones included) never collide, because all airplanes reach one shared landing speed (half the light prop's flight speed). Rollouts in opposite directions, or on different runways, still collide. Removed aircraft never collide.
- **Left the area**: an aircraft that has been on screen and is beyond the leave margin with no path ends the level, including when its path ends out there.
- **Game over**: the first collision or departure ends the level at once. The game freezes on that frame and remembers why and which aircraft; input and spawning stop. After a collision the two aircraft get red dashed rings that stand still, everything else (field, other aircraft, HUD) fades to black over one second, and the blacked-out view holds for another second; input is ignored meanwhile and Back goes to the main page. With reduced motion the blackout is instant and the hold remains. After an aircraft left the viewport there is no such sequence: the dialog shows at once. The game-ended dialog then shows what happened, how many aircraft of each type landed, the total and the points, with Try again and Main page. A full-width ad banner sits directly under the dialog.
- **Edge gestures**: while a level runs, the system bars are hidden (a swipe from their edge shows them briefly) and the left and right edges are protected from the system back gesture around aircraft near them, so a touch on an aircraft there selects it. The HUD keeps its place when the bars show or hide. Back during play pauses and opens the options panel; paused, over (after a collision, once its blackout has ended) or on any other page the bars are visible and gestures work as usual.
- **HUD**: one compact row along the top edge, as the design system's Hud Bar. Top left the best score for the level (compact, with a star) and the current score; scores of 1000 or more group thousands with a thin space (`12 450`). The HUD stays inside the top band of the world that no landing zone enters. Top right a cog wheel pauses the game (no movement, spawning or input) and opens the options panel: Continue, Restart, Quit. Back opens the same panel. The best score is stored per level on the device.
- **Proximity warning**: while two aircraft are closer than the warning distance (centre to centre), both get a spinning dashed ring in the warning colour. It is only a warning; nothing fails until the shapes touch.
- Score: each safe landing adds the landed type's effective points (helicopter least, jumbo most). Collisions and misses add nothing. Bonuses for streaks and tight sequencing may come later.
- Difficulty rises over time: spawn rate and aircraft variety increase. After the last phase a difficulty ramp keeps raising the pressure in steps until per-level limits are reached; then it stays at that final pressure for the rest of the level.

## World size and zoom
Every level is played in a world of 1920 by 1080 world units: the former 1280 by 720 world times one zoom factor, `LevelDefaults.WORLD_ZOOM` = 1.5, the same for all levels. The factor is the middle between the original size (1) and a doubled world (2), which was too far out. The camera is pulled back: runways, helipads, landing zones and scenery keep their world sizes, so the airfield takes a smaller share of the screen in the middle, with open surroundings on every side for aircraft to fly and line up in. Each level's layout sits in the middle of the world, and its scenery reaches out to the edges.

What belongs to an aircraft or to the player's finger is multiplied by the same factor, so it is unchanged on screen:

| Multiplied by the zoom | Base | Now |
|---|---|---|
| Aircraft speed | 20 to 66.67 | 30 to 100 |
| Aircraft radius, and with it sprite size and collision shape | 22 to 30 | 33 to 45 |
| Warning distance | 150 | 225 |
| Spawn separation | 160 | 240 |
| Spawn margin and leave margin | 60 | 90 |
| HUD band at the top of the world | 160 | 240 |
| Path reach radius, minimum point spacing, smoothing tolerance | 6, 12, 4 | 9, 18, 6 |
| Touchdown distance past the threshold | 32 | 48 |

The turn rate (it only sets the runway lead-in length), all timings, the landing tolerance and every fraction stay; the lead-in length (speed divided by turn rate) therefore grows with the zoom and looks the same on screen. So the touchdown point it ends at follows the zoom too. Runway width (64), helipad radius (56), runway lengths, the landing zone sizes and all scenery, the parked aircraft included, stay, so on screen they are smaller than in the 1280 by 720 world: on an 800 by 360dp phone a world unit is 0.333dp, a runway is 21.3dp wide and a helipad 37.3dp across. Runway and helipad markings (bars, numbers, the helipad ring and H) shrink with them. The pick area and the path strokes are in dp and do not change. Any new world-unit distance that describes an aircraft or the finger must be written as its base value times `WORLD_ZOOM`.

## Levels
A level is a `LevelConfig` in code (`domain/Levels.kt`), validated before it is played. It holds:
- **World:** the viewport size in world units (1920 by 1080 for every level), the spawn margin, the leave margin, the spawn separation, the warning distance and the longest simulation step.
- **Zones:** runways (position, axis orientation, length, width, mode) and helipads (position, radius). Zones lie inside the viewport and never overlap.
- **Runway mode:** *one-way* accepts landings on one heading only; *two-way* accepts both ends, and the approach picks the end. Approaches within the landing tolerance (default 30 degrees) of a landing heading count as aligned. The runway number is the landing heading in tens of degrees (`09`, `27`).
- **Difficulty curve:** a list of phases. Each phase starts at a level time and sets the spawn interval range, the weight of each allowed aircraft type and the maximum number of aircraft in the air. The active phase is the last one that has started; phases step, they do not blend. After the last phase, a **difficulty ramp** takes over: every step (60 seconds by default) both spawn intervals drop by 0.5 seconds (down to the level's floor, the range keeping its width), the weight of the next type in the rotation (jet up to 4, then jumbo up to 3, alternating) goes up by 1 unless it is at its cap, and every second step the maximum in the air goes up by 1 (up to the level's cap). No new types appear. When every limit is reached the active phase stays the same. Times count from the end of the tutorial, and the values are a starting point for playtesting.
- **Run rules:** an optional seed (none means a fresh one per run), an optional duration (none means endless) and an optional landing goal.
- **Scoring, wind, emergencies, hints:** lives, a points multiplier, and slots for later features that default to off.

### Build-up
The ramp per level (step times count from the start of the last configured phase):

| Level | Ramp starts | Step | Floor | Cap in the air | All limits reached |
|---|---|---|---|---|---|
| Meadow Field | 2:00 | 60 s | 2.5 s | 5 | 6:00 |
| Island | 1:00 | 60 s | 2.0 s | 6 | 5:00 |
| Savanna | 1:00 | 45 s | 1.5 s | 7 | 4:45 |

The spawner draws every random number from one seeded generator, so a seed replays a run exactly. The first spawn comes after a short delay; after each spawn the next interval is drawn from the active phase's range, and the type is picked by weight. At the phase's aircraft cap the spawn waits until an aircraft lands or leaves.

Level 1, **Meadow Field**, is played in landscape. One one-way runway, 27 (approached from the east, flying west), lies left to right in the middle; the 09 end is plain asphalt and landings from the west do nothing, with a helipad on the right, on a striped lawn. A taxiway, an apron with parked aircraft and vehicles, a terminal with a control tower, a hangar, a windsock and trees make it read as a small airfield. Around it lies farmland: crop fields with rows, hedges, a fence, a farmhouse and a barn, two roads, a pond, a forest edge and a paddock with cows and a farmer. All of this decoration is level data and never covers a landing zone. It opens with only light props and helicopters (two at a time), adds turboprops after 45 seconds and all five types after two minutes, with at most three aircraft in the air.

Levels 2 and 3 are set on an island (a beach with waves, palms and a jetty, and open sea all round) and a savanna at dawn (acacias, bushes, rocks, dirt roads, a waterhole, zebras and an elephant). Runways can carry an accepted-types list, and a level may allow crossing runways.

### Scenery
Scenery is decoration only: no game rule reads it, and a seeded run plays out the same with a level's scenery removed. It is level data (`SceneryItem`: a kind, a centre, a size and a rotation, in world units) and is validated with the level:
- No scenery covers a runway, a helipad or a landing zone.
- Only flat ground (taxiways, aprons, fields, roads, ponds, waterholes, jetties) may lie under the HUD band; buildings, plants, vehicles, animals and people stay below it, and so do the ambient birds and boats.
- A parked aircraft stands on an apron. It is airfield scenery and shrinks with the airfield (67.5 world units square, less than half the flying light prop), has no livery and no shadow, and cannot be selected.

Ambient life is decoration too: cows wander inside the paddock beside a farmer, a flock of three birds with flapping wings crosses now and then, a boat with a wake sails past the island, and at dawn the tower beacon and the aircraft lights glow. With reduced motion no bird or boat appears, wings and cows hold still, and the lights are steady. How scenery looks is set by the design system (`docs/design-system/world.md`).

### Fair entry
Aircraft enter just outside a permitted edge (by the spawn margin) and head for a random point in the central safe region (the middle half of each axis), plus a small random jitter (up to 15 degrees). An entry is only used when its straight line crosses at least half of the viewport's shorter side and stays on screen for at least four seconds at the aircraft's speed, and when it lies at least the spawn separation from every aircraft, including those not yet on screen. Otherwise the spawner tries again, up to eight times, and then delays the spawn briefly.

## First-time tutorial
Meadow Field starts with a tutorial until it has been completed (or skipped) on this device. Quitting or restarting mid-tutorial does not complete it; it starts again from the first step. The normal spawner is off while it runs; only the tutorial sends in a light prop, a turboprop and a helicopter.

Six steps, each with one coach mark numbered "Step n of 6": (1) the goal: guide every aircraft to a safe landing and keep them apart; (2) runway 27 is highlighted with its landing end and the direction to come in from; it says the approach must be lined up and that some runways take landings from one end only and others from both; (3) the light prop flies in; touch is off until it is well in view (at least 240 world units inside every edge), then the game holds, the drag demo plays and the player draws its path; the step ends when the path clears it to land; (4) the turboprop flies in, the game holds when it is well in view while its coach mark explains the four airplane types and their speeds, and the step ends when it is cleared to land; (5) the helipad is highlighted and explained; (6) the helicopter flies in, the game holds when it is well in view, and Got it completes the tutorial without waiting for a landing.

Explanations hold the game (nothing moves, spawns or scores) and end only with Got it. A collision or an aircraft leaving the area does not end the level during the tutorial: the game returns to the state at the start of the current step, runs the step again and says what happened. Skip tutorial ends it at once and stores it as completed. On completion or skip the level continues as normal: aircraft stay, the spawner starts, the difficulty curve and first spawn delay count from that moment, tutorial landings score, and normal failures apply.

## Settings
The settings page (opened from the main page) holds the player's choices, applied at once and kept on the device. **Reduce motion** has three choices: Device follows the device's setting, On always reduces motion, Off never does. The page also has a **Sound effects** toggle (on by default; off silences every effect), a **Privacy policy** button that opens the published policy in the browser, and the app version. A **Keep screen on** toggle in a Gameplay section (on by default) stops the screen from timing out while a level is running, and only then: the options panel, the game-ended dialog and every other screen use the normal timeout. The page scrolls when it does not fit and has no Save button. The effective value of reduced motion is what every screen reads: the main page backdrop, the splash page, and in game the rings, paths and ambient life.

## Look and feel
Cartoon style; see `art-style.md`.

## Future ideas
Weather/wind, night levels, emergency aircraft (priority landing), more airports, iOS release.
