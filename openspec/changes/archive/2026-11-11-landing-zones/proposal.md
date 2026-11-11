# Proposal

GitHub issue: #7

## Why

Today a landing is decided only when a path ends on a zone. The game needs landing zones that turn a drawn path into a landing the moment it enters the right zone, with the right direction for airplanes, and a believable rollout after touchdown.

## What Changes

- Give each helipad an invisible, round landing zone at its centre. A helicopter path that enters it is cut off there, flashes white, and the helicopter is cleared to land and is removed on touchdown.
- Give each runway one landing zone at its threshold (one-way) or one per end (two-way), each with its own landing direction. An airplane path that crosses a zone roughly in that direction is cut off there, flashes white, and the airplane is cleared to land. A crossing in the wrong direction does nothing.
- Make an airplane roll out along the runway for two thirds of the runway length after touchdown, then fade out and be removed. Rolling airplanes still collide.
- Show the flash with a steady, non-flashing variant when reduced motion is on.
- Update the landing rules in `docs/game-design.md`, `docs/architecture.md` and `CLAUDE.md`: a landing is triggered by a path entering a zone, not by where it ends.

Out of scope: go-arounds, missed approaches, landing on a closed runway, visible threshold markings beyond the existing runway art, score awarding and effects after landing (star burst, popup), and the helicopter landing animation art beyond the existing squash and stretch.

Depends on: the level configuration (issue #6: runways with mode and heading, helipads) and the game engine, path following and collision detection from the milestones before landing (roadmap 1 to 3).

## Capabilities

### New Capabilities

- `landing-zones`: where and how aircraft are cleared to land: helipad zones, directional runway zones, the cut-off path with its feedback, and the rollout and removal of landed airplanes.

### Modified Capabilities

None.

## Impact

- `domain/`: landing-zone types derived from the level config; aircraft type config gains a rollout speed.
- `engine/`: `LandingRules` (pure Kotlin), path truncation, a "cleared to land" and a "rolling out" aircraft state, collision checks that include rolling aircraft.
- `ui/`: renders the white flash on the path and a steady variant for reduced motion; reads the reduced-motion flag through `AppHost`.
- `ui/theme`: a path-flash duration and a colour use of existing tokens.
- Docs: `docs/game-design.md`, `docs/architecture.md`, `CLAUDE.md` core rules, the FlightPath guideline in the design system.
- No new third-party dependencies.
