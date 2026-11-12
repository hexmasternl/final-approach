# Architecture

## Technology
- **Kotlin Multiplatform** with **Compose Multiplatform** UI. Android first; iOS added later from the same shared module.
- Gradle Kotlin DSL with a version catalog (`gradle/libs.versions.toml`).

## Layout
```
final-approach/
├─ app/src/              # the Gradle project (run ./gradlew from here)
│  ├─ shared/            # KMP module: all game code
│  │  └─ src/commonMain/kotlin/nl/hexmaster/finalapproach/
│  │     ├─ domain/      # Aircraft, AircraftType, Airport, Runway, Helipad, FlightPath
│  │     ├─ engine/      # GameEngine, Spawner, PathFollower, LandingRules, Collisions
│  │     ├─ input/       # Gesture handling (tap-select, drag-draw)
│  │     ├─ platform/    # AppHost: what shared code needs from the platform
│  │     └─ ui/          # Screens, Canvas renderer, theme, HUD
│  ├─ androidApp/        # Thin Android host (Activity, manifest), appId nl.hexmaster.final_approach
│  └─ iosApp/            # Added later
└─ docs/
```

## Layers
1. **Domain** – plain data types and config (aircraft specs, airport layouts).
2. **Engine** – deterministic simulation: `update(deltaSeconds)` advances aircraft, detects landings, collisions and misses. No UI dependencies; unit tested.
3. **UI** – Compose `Canvas` renders engine state; a frame clock calls `engine.update`. Engine exposes `StateFlow<GameState>`.
4. **Input** – pointer events converted to world coordinates; tap picks the nearest aircraft, drag appends points to its `FlightPath` (simplified/smoothed).

## Key mechanics
- **Path smoothing** (`PathSmoother`): when a drag ends (`endPath`) or a path is locked at a landing zone, the drawn points are simplified (Douglas-Peucker within `tolerance`), resampled evenly and corner-cut (Chaikin). A virtual aircraft of the same speed, limited to `minRadiusFactor` times the turning radius (`speed / turnRate`), then follows that line from the real aircraft's pose; its track, ending exactly on the drawn end point, is the stored path. Tunables live in `PathSmoothingConfig`.
- **Path following** (`PathFollower`): progress along the path is monotonic. The first point is dropped once the aircraft is past the first segment, or level with that point and within one segment of it; the last point within one step, or when it lies inside the circle the aircraft would fly turning towards it. Each step the aircraft turns, by at most `turnRate * dt`, towards the point `lookaheadTurnFraction` turning radii further along the path from its place on it, then moves `speed * dt` along its heading. With no points left it flies straight on. Reach radius and the minimum drag point spacing live in `EngineConfig`.
- **Path end**: path consumed; aircraft keeps its last heading.
- **Landing check**: while a path is drawn, each new segment is tested against the landing zones (`LandingRules`). Zones are derived from the level (`LandingZones`): a circle per helipad and a threshold rectangle per landing direction of each runway. The first valid entry cuts and locks the path (`PathDrawing`) and sets the aircraft `ClearedToLand`. `AircraftMotion` then flies it to the path end: helicopters are removed there, airplanes become `RollingOut` along the zone's direction for a fraction of the runway length, fade, and are removed. Rolling airplanes still count for collisions. Tunables live in `LandingConfig`; the rollout speed fraction is part of `AircraftTypeSpec`.
- **Path flash**: the engine only records when a path was locked (`FlightPath.lockedAt`); the renderer draws the white flash from that.
- **Coordinates**: world units independent of screen size. `input/WorldViewport` scales the world uniformly to fit the screen and centres it; it converts screen pixels to world units and back. The renderer converts at the boundary; the engine only sees world units.

## Game engine
- `engine/GameEngine.kt`: `GameEngine(level, seed, specs, config)` owns the run and exposes `state: StateFlow<GameState>`. `GameState` holds the level time, the aircraft in play, the score, the number of landings, the selected aircraft and the events of the last update. The state changes only through `update(deltaSeconds)` and the input calls, so two engines with the same level, seed, updates and input produce the same states.
- `update` moves every aircraft (`AircraftMotion`), adds landing points (`Scoring`), and asks the `Spawner` for a new aircraft, passing the positions of the aircraft in the air. A new aircraft takes the next free livery (`path1` to `path6`). Each frame is split into steps of at most `WorldConfig.maxStepSeconds`. After every step the engine checks for failure: first a collision (`Collisions.find`: the aircraft's `CollisionShape` circles, placed by position and heading, tested swept over the step), then an aircraft that has been inside the viewport and is now beyond it by more than `WorldConfig.leaveMargin` (`Aircraft.hasEnteredViewport`). Either sets `GameState.status` to `GameOver(reason, aircraftIds)`; from then on `update` and the input calls change nothing. `GameState.warnedAircraft` holds the aircraft closer than `WorldConfig.warningDistance` to another.
- Input: `select(point, pickRadius)` picks the nearest aircraft within the radius (or clears the selection); `beginPath(id)` starts a new path that replaces the old one; `extendPath(id, point)` adds points at least `EngineConfig.minPointSpacing` apart and lets `PathDrawing` cut the path off at a landing zone; `clearPath(id)` removes a free-flying aircraft's path (used on touch-down, so a tap cancels it); `endPath(id)` ends the gesture and smooths the path. `input/PathGesture` maps a pointer gesture onto these calls.
- `ui/game/GameScreen.kt`: the frame clock (`withFrameNanos`) calls `engine.update` with the frame time, capped so a pause does not make aircraft jump. Pointer input is converted to world units with `WorldViewport`; the pick radius is `touch-aircraft` (72dp) converted to world units. A tap selects; a drag that starts on an aircraft and moves past the touch slop draws its path. Drawing order: lawn, ground scenery (taxiways, aprons), runways with numbers, helipads, buildings and trees, flight paths, all ground shadows, all aircraft, the selection ring, the proximity rings (`ProximityRing.kt`: a dashed ring that pops in, spins and fades out; static with reduced motion). The screen locks the app in landscape through `AppHost.lockLandscape` while it is shown.

## Level config and spawner
- `domain/Geometry.kt`: `Vec2`, `Angles` (headings in radians clockwise from up, world y down), `Circle`, `OrientedRect`, `Bounds` and the `Overlap` tests.
- `domain/LevelConfig.kt`: immutable data classes (`LevelConfig`, `WorldConfig`, `RunwayConfig` with `RunwayMode`, `HelipadConfig`, `DifficultyPhase`, `SpawnRules`, `RunRules`, `ScoringConfig`, `Wind`, `TutorialHint`). Global defaults live in `LevelDefaults`; no tunable appears as a literal in logic.
- `domain/LevelConfigValidator.kt`: returns every problem as a sentence; `LevelConfig.requireValid()` throws with all of them.
- `domain/Levels.kt`: the levels in play order.
- `domain/Scenery.kt`: `SceneryItem` decoration (taxiway, apron, terminal, tower, hangar, windsock, tree) as level data. The validator rejects scenery that covers a runway, a helipad or a landing zone.
- `engine/Spawner.kt`: `Spawner(config, seed, speedOf)` is stepped with `update(deltaSeconds, levelTime, aircraftPositions)` and returns at most one `Spawn` (type, position, heading, edge). It never reads a clock; whoever starts a run draws the seed when the level has none.

## Testing
`kotlin.test` in `commonTest` for engine/domain and for UI logic such as the menu backdrop; keep Compose UI thin. Tests run on the JVM through the Android host test target.

## Platform boundary
`commonMain` never calls a platform API. Everything it needs from the platform goes through `AppHost` (`platform/AppHost.kt`), which the host app implements:

- `versionName`: the version of the installed build, shown on the main page.
- `reduceMotion`: true when the device asks apps to reduce animation. On Android this is the animator duration scale set to 0.
- `back`: a `BackDispatcher`. The host forwards every system back press to `back.dispatch()`; screens register with the `BackHandler` composable. The most recently composed handler wins. A back press nobody handles leaves the app.
- `quit()`: closes the app.
- `lockLandscape(locked)`: locks the app in landscape, or releases the lock. The game screen locks it while shown. On Android this sets the activity's requested orientation; the activity handles orientation changes itself, so it is not recreated.

`AndroidAppHost` in `androidApp` is the Android implementation. Tests use `FakeAppHost` from `commonTest`.

Ads go through `AppHost.ads` (`platform/Ads.kt`): `preload()`, `showInterstitial(onDismissed)` and a composable `Banner(modifier)`. `onDismissed` is called exactly once whether the ad was shown, failed or was not ready, so a level never waits on an ad. `NoOpAds` is the default (tests, iOS). `GameScreen` pauses the engine until the interstitial before a level is dismissed, and a restart waits for it too. The Android implementation (AdMob and the UMP consent flow) lives only in `androidApp`; no ads SDK type reaches `commonMain`. Ad IDs come from the build type: test units in debug, the real units in release.

## Screen flow
`App` (`ui/App.kt`) holds the current `Screen` and shows one page at a time. The app opens on the splash page, which hands over to the main page. The main page is home: the game and the settings page return to it. Take off opens the level select page (`ui/game/LevelSelectPage.kt`): one square tile per level in `Levels.all`, in a row that scrolls only when needed. A tile starts that level; back returns to the main page, and so do Quit and the game-ended dialog's Main page action. No navigation library is used.

Level scenery (meadow, island, savanna) is drawn by `ui/game/LevelBackdrop.kt` from the level data. Ambient life (cows, boat, bird, and at dawn the beacon and aircraft lights) is simulated by the pure `AmbientLife` class in `ui/game/ambient` and only drawn by the UI; it never touches the engine and stands still with reduced motion. The Dawn palette and light tokens live in `ui/theme/DawnTheme.kt`.

The splash page (`ui/splash`) is a pure function of time: `SplashTimeline.frameAt(elapsedSeconds)` returns where the airplane and the banner are, whether the tow line is attached, the state of the wave, and whether the page should hand over. `SplashPage` advances the time from the frame clock and draws the frame; it calls `onFinished` once, either when the timeline says so or on a tap. All its timings and sizes live in `SplashConfig`. With `AppHost.reduceMotion` the timeline starts settled: banner centred and still, no airplane.

The main page backdrop (rotating stripes, drifting clouds, one passing aircraft at a time) is advanced by `MenuBackdropSimulation`, a pure class that is unit tested and separate from the game engine. `MenuBackdrop` only draws its state. All its timings live in `MenuBackdropConfig`.

## Theme
`ui/theme` holds the design system as Kotlin: `FaColors` (Day and Night), `FaTypography`, `FaDimens`, `FaShadows`, `FaMotion` and `FaOpacity`, plus sprite and icon path data in `ui/sprites`. These files are generated from the design-system tokens and carry a header saying so; change the design system first, then regenerate.

## Build
Run from `app/src`:
```
./gradlew :androidApp:assembleDebug
./gradlew :shared:allTests
```
The Android SDK location goes in `app/src/local.properties` (`sdk.dir=...`), which is not committed.
