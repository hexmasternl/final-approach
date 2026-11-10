# Architecture

## Technology
- **Kotlin Multiplatform** with **Compose Multiplatform** UI. Android first; iOS added later from the same shared module.
- Gradle Kotlin DSL with a version catalog (`gradle/libs.versions.toml`).

## Planned layout
```
final-approach/
├─ shared/            # KMP module: all game code
│  └─ src/commonMain/kotlin/nl/hexmaster/finalapproach/
│     ├─ domain/      # Aircraft, AircraftType, Airport, Runway, Helipad, FlightPath
│     ├─ engine/      # GameEngine, Spawner, PathFollower, LandingRules, Collisions
│     ├─ input/       # Gesture handling (tap-select, drag-draw)
│     └─ ui/          # GameScreen, Canvas renderer, theme, HUD
├─ androidApp/        # Android host (Activity, manifest)
├─ iosApp/            # Added later
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
`kotlin.test` in `commonTest` for engine/domain; keep Compose UI thin.

## Build (once scaffolded)
```
./gradlew :androidApp:assembleDebug
./gradlew :shared:allTests
```
