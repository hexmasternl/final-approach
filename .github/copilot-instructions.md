# Copilot Instructions – Final Approach

Final Approach is a cartoon-style air-traffic game. The player draws paths for incoming aircraft to guide them to a safe landing. Full design: see `docs/`.

## Tech stack
- Kotlin Multiplatform (KMP) + Compose Multiplatform. Android first; iOS later, so keep game logic platform-independent.
- The Gradle project lives in `app/src`. Game code lives in `app/src/shared/` (commonMain). Platform code (`app/src/androidApp/`, later `iosApp/`) is only a thin host; everything shared code needs from the platform goes through `AppHost`.
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
- Planes land only on runways, helicopters only on helipads. A path that enters a valid landing zone (runway thresholds need an aligned approach) is cut off there and clears the aircraft to land; a runway path is snapped onto the centre line with a smooth lead-in. Landing removes the aircraft, airplanes after a rollout along the centre line. All airplanes roll out at one shared landing speed, half the light prop's flight speed, so same-direction rollouts on one runway never collide.
- Each aircraft type has its own speed; helicopters are the slowest.
- Collisions (aircraft collision shapes touching) and an aircraft outside the level with no path (beyond the leave margin, including when its path ends there) are failures that end the level; aircraft closer than the warning distance only get a warning ring.

## Conventions
- Kotlin official code style; 4-space indent; `val` over `var`; no `!!`.
- Units: positions in world units (not pixels), speeds in world units/second, angles in radians. Convert at the render boundary.
- Tunable values (speeds, radii, spawn rates) go in config/data classes, not magic numbers.
- Add unit tests (`kotlin.test`) for engine/domain logic: path following, landing detection, collision.
- Update `docs/` when changing game rules or architecture.

## Style / art
- Cartoon look: thick outlines, bright flat colours, rounded shapes, bouncy animations. Art constants live in the theme.

## Git workflow
Follow the `git-workflow` skill (`.github/skills/git-workflow/SKILL.md`) for every commit, push, branch, worktree and pull request, and whenever an OpenSpec change is proposed, applied or archived.
- Never touch `main`: it represents production.
- Propose and archive OpenSpec changes on `development`; committing and pushing there directly is allowed for that.
- Implement a change only if it names an open GitHub issue and that issue names the change. Work in a new worktree on `change/<change-name>`, branched from `development`, commit per completed task, then push and open a pull request into `development`.
- Archiving closes the issue, moves it to Done on its GitHub project, and removes the worktree and the change branch (local and remote).
