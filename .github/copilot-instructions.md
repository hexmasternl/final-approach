# Copilot Instructions – Final Approach

Final Approach is a cartoon-style air-traffic game. The player draws paths for incoming aircraft to guide them to a safe landing. Full design: see `docs/`.

## Tech stack
- Kotlin Multiplatform (KMP) + Compose Multiplatform. Android first; iOS later, so keep game logic platform-independent.
- Game code lives in `shared/` (commonMain). Platform code (`androidApp/`, later `iosApp/`) is only a thin host.
- Do not use Android/JVM-only APIs in `commonMain` (no `android.*`, `java.*`); use `kotlin.*`/`kotlinx.*`.
- Rendering: Compose `Canvas`; game loop driven by a frame clock (`withFrameNanos`) with delta-time updates.

## Architecture
- Separate **simulation** (pure Kotlin, deterministic, unit-testable) from **rendering** (Compose) and **input** (gestures).
- Simulation state is owned by a single `GameEngine`; the UI observes it via `StateFlow`.
- Never put game rules in composables.
- Packages: `domain` (aircraft, airport, paths), `engine` (loop, spawning, landing rules), `ui` (screens, canvas, theme), `input`.

## Core rules (keep consistent with docs/game-design.md)
- Tap an aircraft, then drag to draw its path; the aircraft follows the path.
- When the path ends, the aircraft continues straight in its last heading.
- Planes land only on runways, helicopters only on helipads; landing removes the aircraft.
- Each aircraft type has its own speed (and turn rate); helicopters are slower and can turn tighter.
- Collisions (too-close aircraft) and leaving the viewport without landing are failures.

## Conventions
- Kotlin official code style; 4-space indent; `val` over `var`; no `!!`.
- Units: positions in world units (not pixels), speeds in world units/second, angles in radians. Convert at the render boundary.
- Tunable values (speeds, radii, spawn rates) go in config/data classes, not magic numbers.
- Add unit tests (`kotlin.test`) for engine/domain logic: path following, landing detection, collision.
- Update `docs/` when changing game rules or architecture.

## Style / art
- Cartoon look: thick outlines, bright flat colours, rounded shapes, bouncy animations. Art constants live in the theme.
