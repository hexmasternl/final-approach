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
- **Path following**: aircraft advances `speed * dt` along the polyline; heading is the segment direction; turn rate limits sharp corners.
- **Path end**: path cleared; aircraft keeps its last heading.
- **Landing check**: when the path ends, test the endpoint against runways (planes) or helipads (helicopters).
- **Coordinates**: world units independent of screen size; the viewport scales to fit.

## Testing
`kotlin.test` in `commonTest` for engine/domain and for UI logic such as the menu backdrop; keep Compose UI thin. Tests run on the JVM through the Android host test target.

## Platform boundary
`commonMain` never calls a platform API. Everything it needs from the platform goes through `AppHost` (`platform/AppHost.kt`), which the host app implements:

- `versionName`: the version of the installed build, shown on the main page.
- `reduceMotion`: true when the device asks apps to reduce animation. On Android this is the animator duration scale set to 0.
- `back`: a `BackDispatcher`. The host forwards every system back press to `back.dispatch()`; screens register with the `BackHandler` composable. The most recently composed handler wins. A back press nobody handles leaves the app.
- `quit()`: closes the app.

`AndroidAppHost` in `androidApp` is the Android implementation. Tests use `FakeAppHost` from `commonTest`.

## Screen flow
`App` (`ui/App.kt`) holds the current `Screen` and shows one page at a time. The app opens on the splash page, which hands over to the main page. The main page is home: the game and the settings page return to it. No navigation library is used.

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
