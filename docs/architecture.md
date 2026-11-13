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
- **Path smoothing** (`PathSmoother`): when a drag ends (`endPath`) or a path is locked at a landing zone, the drawn points are simplified (Douglas-Peucker within `tolerance`), resampled evenly at `minPointSpacing` and corner-cut (Chaikin). There is no turn limit; the last point stays exactly where it was drawn. Tunables live in `PathSmoothingConfig`.
- **Path following** (`PathFollower`): each step the aircraft covers `speed * dt` of arc length along the remaining points, with its heading along the segment it is on, and drops the points it reaches. With no points left it flies straight on in its last heading. The type's `turnRate` only sizes the runway lead-in. The minimum drag point spacing lives in `EngineConfig`.
- **Path end**: path consumed; aircraft keeps its last heading.
- **Landing check**: while a path is drawn, each new segment is tested against the landing zones (`LandingRules`). Zones are derived from the level (`LandingZones`): a circle per helipad and a threshold rectangle per landing direction of each runway. The first valid entry cuts and locks the path (`PathDrawing`) and sets the aircraft `ClearedToLand`. At a runway, `RunwayLeadIn` then replaces the end of the smoothed path by a cubic Bezier lead-in from a join point `leadInTurningRadii` turning radii back along it to the touchdown point on the centre line (`touchdownDistance` past the threshold, or level with the entry when deeper), leaving along the path and arriving in the landing direction; a path shorter than that starts the lead-in at the aircraft. `AircraftMotion` then flies it to the path end: helicopters are removed there, airplanes become `RollingOut` along the zone's direction for a fraction of the runway length, fade, and are removed. Rollouts start at the airplane's flight speed and slow to the landing speed (the flight speed of `landingSpeedReference`) over `rolloutDecelerationSeconds`. Rolling airplanes still count for collisions, except two rolling out on the same runway in the same direction. Tunables live in `LandingConfig`.
- **Path flash**: the engine only records when a path was locked (`FlightPath.lockedAt`); the renderer draws the white flash from that.
- **Coordinates**: world units independent of screen size. `input/WorldViewport` scales the world uniformly to fit the screen and centres it; it converts screen pixels to world units and back. The renderer converts at the boundary; the engine only sees world units.

## Game engine
- `engine/GameEngine.kt`: `GameEngine(level, seed, specs, config)` owns the run and exposes `state: StateFlow<GameState>`. `GameState` holds the level time, the aircraft in play, the score, the number of landings, the selected aircraft and the events of the last update. The state changes only through `update(deltaSeconds)` and the input calls, so two engines with the same level, seed, updates and input produce the same states.
- `update` moves every aircraft (`AircraftMotion`), adds landing points (`Scoring`), and asks the `Spawner` for a new aircraft, passing the positions of the aircraft in the air. A new aircraft takes the next free livery (`path1` to `path6`). Each frame is split into steps of at most `WorldConfig.maxStepSeconds`. After every step the engine checks for failure: first a collision (`Collisions.find`: the aircraft's `CollisionShape` circles, placed by position and heading, tested swept over the step), then an aircraft that has been inside the viewport and is now beyond it by more than `WorldConfig.leaveMargin` (`Aircraft.hasEnteredViewport`). Either sets `GameState.status` to `GameOver(reason, aircraftIds)`; from then on `update` and the input calls change nothing. `GameState.warnedAircraft` holds the aircraft closer than `WorldConfig.warningDistance` to another.
- Input: `select(point, pickRadius)` picks the nearest aircraft within the radius (or clears the selection); `beginPath(id)` starts a new path that replaces the old one; `extendPath(id, point)` adds points at least `EngineConfig.minPointSpacing` apart and lets `PathDrawing` cut the path off at a landing zone; `clearPath(id)` removes a free-flying aircraft's path (used on touch-down, so a tap cancels it); `endPath(id)` ends the gesture and smooths the path. `input/PathGesture` maps a pointer gesture onto these calls.
- `ui/game/GameScreen.kt`: the frame clock (`withFrameNanos`) calls `engine.update` with the frame time, capped so a pause does not make aircraft jump. Pointer input is converted to world units with `WorldViewport`; the pick radius is `touch-aircraft` (72dp) converted to world units. A tap selects; a drag that starts on an aircraft and moves past the touch slop draws its path. Drawing order: ground (lawn, or sea and island), flat scenery (fields, roads, ponds, aprons, taxiways), runways with numbers, helipads, standing scenery (buildings, vehicles, plants, animals), ambient life, flight paths, all ground shadows, all aircraft, the selection ring, the proximity rings (`ProximityRing.kt`: a dashed ring that pops in, spins and fades out; static with reduced motion). The screen locks the app in landscape through `AppHost.lockLandscape` while it is shown.

## Level config and spawner
- `domain/Geometry.kt`: `Vec2`, `Angles` (headings in radians clockwise from up, world y down), `Circle`, `OrientedRect`, `Bounds` and the `Overlap` tests.
- `domain/LevelConfig.kt`: immutable data classes (`LevelConfig`, `WorldConfig`, `RunwayConfig` with `RunwayMode`, `HelipadConfig`, `DifficultyPhase`, `SpawnRules`, `RunRules`, `ScoringConfig`, `Wind`, `TutorialHint`). Global defaults live in `LevelDefaults`; no tunable appears as a literal in logic.
- `domain/LevelConfigValidator.kt`: returns every problem as a sentence; `LevelConfig.requireValid()` throws with all of them.
- `domain/Levels.kt`: the levels in play order.
- `domain/LevelDefaults.kt` also holds the world zoom: `WORLD_ZOOM` (1.5) and the world size derived from it (1920 by 1080). Every default that describes an aircraft or the player's finger is written as its former value times `WORLD_ZOOM`: `HUD_BAND`, `SPAWN_SEPARATION`, `WARNING_DISTANCE`, `SPAWN_MARGIN`, `LEAVE_MARGIN`, `TOUCHDOWN_DISTANCE` (the runway lead-in is flown on the aircraft's turning circle), the speeds and radii in `DefaultAircraftSpecs`, and in `EngineConfig` the reach radius, the minimum point spacing and the smoothing tolerance. The engine sees plain numbers; there is no runtime multiplier. Values that describe the airfield (runway width and length, helipad radius, landing zone sizes, scenery sizes including the parked aircraft) do not follow the zoom, and runway and helipad markings are drawn at that same airfield scale (a token's dp value as world units). A new world-unit distance for an aircraft or the finger must follow it too.
- `domain/Scenery.kt`: `SceneryItem` decoration as level data, in a `SceneryKind`. Flat kinds (`ground`: taxiway, apron, field, road, pond, waterhole, jetty) are drawn under the runways and may lie under the HUD band; all other kinds (buildings, vehicles, parked aircraft, plants, animals) stand on the ground. `SceneryDefaults` holds the world-unit sizes the drawing needs (crop row spacing, road dashes, beach width, wave spacing, the parked aircraft size). The validator rejects scenery that covers a runway, a helipad or a landing zone, standing scenery in the HUD band, and a parked aircraft that is not on an apron.
- `engine/Spawner.kt`: `Spawner(config, seed, speedOf)` is stepped with `update(deltaSeconds, levelTime, aircraftPositions)` and returns at most one `Spawn` (type, position, heading, edge). It never reads a clock; whoever starts a run draws the seed when the level has none.

## Testing
`kotlin.test` in `commonTest` for engine/domain and for UI logic such as the menu backdrop; keep Compose UI thin. Tests run on the JVM through the Android host test target.

## Platform boundary
`commonMain` never calls a platform API. Everything it needs from the platform goes through `AppHost` (`platform/AppHost.kt`), which the host app implements:

- `versionName`: the version of the installed build, shown on the main page.
- `reduceMotion`: true when the device asks apps to reduce animation. On Android this is the animator duration scale set to 0.
- `back`: a `BackDispatcher`. The host forwards every system back press to `back.dispatch()`; screens register with the `BackHandler` composable. The most recently composed handler wins. A back press nobody handles leaves the app.
- `quit()`: closes the app.
- `openUrl(url)`: opens a web address in the device browser (Android: `ACTION_VIEW`; a device without a browser does nothing). The privacy policy address is `AppLinks.PRIVACY_POLICY_URL`.
- `sounds`: a `SoundPlayer` (`platform/SoundPlayer.kt`) with `play(SoundEffect)`; it defaults to `SilentSoundPlayer` on a platform without a player (iOS until its host has one). `GameScreen` plays `SoundCues.of(events)` through it after each `engine.update`; the engine reports a `GameEvent.ClearedToLand` for each clearance and knows nothing about audio. Sound effects play through one `GatedSoundPlayer` (`platform/GatedSoundPlayer.kt`) that `App` builds around `host.sounds` and hands to the game screen; it checks the sound-effects setting at play time, so no sound reads the setting itself and no call site outside `App` uses `host.sounds` directly. On Android, `AndroidAppHost` plays through a `SoundPool`.
- `lockLandscape(locked)`: locks the app in landscape, or releases the lock. The game screen locks it while shown. On Android this sets the activity's requested orientation; the activity handles orientation changes itself, so it is not recreated.

- `settings`: a `SettingsStore` (`platform/SettingsStore.kt`, `load()` and `save(PlayerSettings)`; `InMemorySettingsStore` for tests). `SettingsController` (`platform/SettingsController.kt`) loads it once, exposes `StateFlow<PlayerSettings>` and writes every `update` through. `PlayerSettings` and `ReduceMotionChoice` live in `domain/PlayerSettings.kt`. Android keeps them in a private `settings` preferences file.

Reduced motion: `App` computes `settings.reduceMotion.resolve(host.reduceMotion)` and passes it to `FaTheme`; every screen reads `FaTheme.reduceMotion`. `host.reduceMotion` is read only in `App`. Device follows the device; On and Off override it.

`AndroidAppHost` in `androidApp` is the Android implementation. Tests use `FakeAppHost` from `commonTest`.

Ads go through `AppHost.ads` (`platform/Ads.kt`): `preload()`, `showInterstitial(onDismissed)` and a composable `Banner(modifier)`. `onDismissed` is called exactly once whether the ad was shown, failed or was not ready, so a level never waits on an ad. `NoOpAds` is the default (tests, iOS). `GameScreen` pauses the engine until the interstitial before a level is dismissed, and a restart waits for it too. The Android implementation (AdMob and the UMP consent flow) lives only in `androidApp`; no ads SDK type reaches `commonMain`. Ad IDs come from the build type: test units in debug, the real units in release.

## Screen flow
`App` (`ui/App.kt`) holds the current `Screen` and shows one page at a time. The app opens on the splash page, which hands over to the main page. The main page is home: the game and the settings page return to it. Take off opens the level select page (`ui/game/LevelSelectPage.kt`): one square tile per level in `Levels.all`, in a row that scrolls only when needed. A tile starts that level; back returns to the main page, and so do Quit and the game-ended dialog's Main page action. No navigation library is used.

Level scenery (meadow, island, savanna) is drawn by `ui/game/LevelBackdrop.kt` and `ui/game/AirfieldDrawing.kt` from the level data. Every scenery object is a sprite from the design system: the SVG sources in `docs/design-system/assets/scenery/` live as `SCENERY` in `bundle.js`, and `docs/design-system/tools/gen-kotlin.cjs` turns them into `ui/sprites/ScenerySprites.kt` (a `ScenerySprite` per object, each shape with a `SceneryPaint` colour role and a `SceneryStroke`), refusing a sprite that uses more than four fill colours or a colour scenery may not use. `ui/sprites/ScenerySpriteDrawing.kt` draws a sprite into an item's rectangle (`drawScenerySprite`) or piece after piece along a strip (`drawSceneryRow`, for hedges, fences and forest edges), with the scenery outline `line-2` and the detail line `line-fine`; these strokes are world lengths (0.8 world units per dp of their token), capped at their tokens (`sceneryLines`). Fields, roads, water, aprons, the beach and the waves are drawn from tokens and `SceneryDefaults`. No colour, stroke width or size is a literal in the drawing code. Ambient life (cows and farmer, a flock of three birds with a wing phase, a boat with its wake, and at dawn the beacon and aircraft lights) is simulated by the pure `AmbientLife` class in `ui/game/ambient`, with its sizes and speeds in `LevelAmbienceConfig`, and only drawn by the UI; it never touches the engine and stands still with reduced motion. `ui/game/GamePreviews.kt` has debug previews of every level in Day and Night (and the savanna in Dawn) and of the sprite sheet, for design review. The Dawn palette and light tokens are generated from the design-system tokens into `FaColors` (`DawnColors`) and `FaLights` in `ui/theme`.

The splash page (`ui/splash`) is a pure function of time: `SplashTimeline.frameAt(elapsedSeconds)` returns where the airplane and the banner are, whether the tow line is attached, the state of the wave, and whether the page should hand over. `SplashPage` advances the time from the frame clock and draws the frame; it calls `onFinished` once, either when the timeline says so or on a tap. All its timings and sizes live in `SplashConfig`. With `AppHost.reduceMotion` the timeline starts settled: banner centred and still, no airplane.

The main page shows the `FaWordmark` (`ui/components`) above the menu, centred with it as one group; when that stacked layout does not fit the height inside the system bars (a landscape phone), the small wordmark sits to the left of the menu instead. `chooseMainMenuArrangement` makes that choice from measured sizes, not from orientation. The main page backdrop (rotating stripes, drifting clouds, one passing aircraft at a time) is advanced by `MenuBackdropSimulation`, a pure class that is unit tested and separate from the game engine. `MenuBackdrop` only draws its state. All its timings live in `MenuBackdropConfig`.

## Theme
`ui/theme` holds the design system as Kotlin: `FaColors` (Day, Night and Dawn), `FaLights`, `FaTypography`, `FaDimens`, `FaShadows`, `FaMotion` and `FaOpacity`, plus sprite and icon path data in `ui/sprites`. These files are generated from the design-system tokens and carry a header saying so; change the design system first, then regenerate.

## Build
Run from `app/src`:
```
./gradlew :androidApp:assembleDebug
./gradlew :shared:allTests
```
The Android SDK location goes in `app/src/local.properties` (`sdk.dir=...`), which is not committed.
