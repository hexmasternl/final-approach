# Final Approach

A cartoon-style, top-down air traffic game. Aircraft enter the viewport from the edges; the player taps one, drags a path, and lands it. Airplanes land on runways, helicopters on helipads. Differing speeds are the core challenge: fast aircraft catch up with slow ones, so the player must sequence landings.

The repo currently holds documentation and the design system only. No code is scaffolded yet; the architecture below is the proposed one.

## Where things are

| Path | What |
|---|---|
| `docs/game-design.md` | Gameplay, aircraft roster, rules, scoring. The source of truth for game rules. |
| `docs/architecture.md` | Tech stack, module layout, engine design. |
| `docs/art-style.md` | Visual and audio style brief. |
| `docs/roadmap.md` | Milestones 1 to 9. |
| `docs/design-system/showcase.html` | The design system on one page (tokens, type, components, screens). Also as `showcase.pdf`. |
| `docs/design-system/assets/` | SVG sources: `aircraft/`, `icons/`, `app-icon/`. |
| `.github/copilot-instructions.md` | The same conventions for Copilot. Keep it consistent with this file. |

Open work is tracked as GitHub issues (`gh issue list`).

## Core rules

Keep these consistent with `docs/game-design.md`; update the doc when a rule changes.

- Tap an aircraft to select it, then drag to draw its path. A new path replaces the old one.
- The aircraft follows the path at its own speed. When the path ends, it flies straight on in its last heading.
- A path ending on a valid zone lands the aircraft: runway for airplanes (roughly aligned with the runway axis), helipad for helicopters (within the pad radius). The wrong zone type does nothing.
- Failures: two aircraft closer than the minimum separation (collision), or an aircraft leaving the viewport without landing (miss).
- Five aircraft types: light prop, turboprop, jet, jumbo, helicopter. Each has its own speed and turn rate.

## Proposed architecture

Kotlin Multiplatform with Compose Multiplatform. Android first; iOS later from the same shared module.

```
final-approach/
├─ shared/            # KMP module: all game code
│  └─ src/commonMain/kotlin/nl/hexmaster/finalapproach/
│     ├─ domain/      # Aircraft, AircraftType, Airport, Runway, Helipad, FlightPath
│     ├─ engine/      # GameEngine, Spawner, PathFollower, LandingRules, Collisions
│     ├─ input/       # Gesture handling (tap-select, drag-draw)
│     └─ ui/          # Screens, Canvas renderer, theme, HUD
├─ androidApp/        # Thin Android host (Activity, manifest)
├─ iosApp/            # Added later
└─ docs/
```

Layers:

1. **Domain:** plain data types and configuration (aircraft specs, airport layouts).
2. **Engine:** a deterministic simulation. `update(deltaSeconds)` advances aircraft and detects landings, collisions and misses. No UI dependencies. A single `GameEngine` owns the state and exposes `StateFlow<GameState>`.
3. **UI:** a Compose `Canvas` renders engine state; a frame clock (`withFrameNanos`) calls `engine.update` with delta time.
4. **Input:** pointer events converted to world coordinates. Tap picks the nearest aircraft; drag appends points to its `FlightPath`.

Rules that follow from this:

- Never put game rules in composables. Simulation, rendering and input stay separate.
- `commonMain` uses only `kotlin.*` and `kotlinx.*`. No `android.*` or `java.*`. Platform concerns (back handling, closing the app, version, ads) go behind a small interface implemented in the host app.
- Units: positions in world units, speeds in world units per second, angles in radians. Convert to pixels only at the render boundary.
- Tunable values (speeds, radii, spawn rates, timings) live in config or data classes, not as magic numbers.
- Engine and domain logic get `kotlin.test` unit tests in `commonTest`: path following, landing detection, collisions. Keep the Compose UI thin.

Conventions: Kotlin official code style, 4-space indent, `val` over `var`, no `!!`. Gradle Kotlin DSL with a version catalog (`gradle/libs.versions.toml`).

Build, once scaffolded:

```
./gradlew :androidApp:assembleDebug
./gradlew :shared:allTests
```

## Design system

The design system defines how everything looks. Open `docs/design-system/showcase.html` before building or changing any UI. The full system, with per-component guidelines, is published at https://claude.ai/artifact/WPS86AMwfU8Ph1ehK5kdpL (private to the owner unless shared).

The HTML and CSS in the design system are the visual reference. The game implements the same values in a Compose theme under `ui/theme`.

### Principles

1. **Outline everything.** Every drawn shape has a thick dark-navy outline (4dp by default) with round joins. Never pure black.
2. **Flat and bright.** Single flat fills. No gradients or textures. Only airborne things get a soft shadow.
3. **Toy scale.** Sizes are exaggerated. The jumbo is nearly twice the light prop.
4. **Silhouette before colour.** Types, zones and states differ by shape first. Colour never carries meaning alone.
5. **The field stays clear.** UI lives in small outlined chips at the edges. Nothing covers a landing zone.

### Using it in code

- **No literals.** No colour, stroke width, radius, size, duration or easing literal in a composable or in engine code. Everything comes from the theme.
- **Colours:** one `FaColors` data class with a property per colour token in camelCase (`grass500`, `onAccent`, `path1`), with `DayColors` and `NightColors`, provided through a `CompositionLocal`.
- **Type:** DynaPuff for headers, titles, button labels and the score. Inconsolata for regular text, labels and readouts, never lighter than weight 500. One `TextStyle` per design-system style, same names (`displayLg`, `buttonLabel`, `callsign`).
- **Dimensions:** spacing, radius, line, size and flight-path values as `dp` in `FaDimens`. UI sizes are fixed; world positions scale with the viewport.
- **Motion:** durations in milliseconds and easings as `CubicBezierEasing` from the `duration` and `easing` tokens. Arrivals bounce, controls snap.
- **Layers:** draw in the fixed order field, zone glows, paths, ground shadows, aircraft, effects, clouds, HUD, overlays.
- **Themes:** Day is the default; Night is for night levels. Panels, chips and aircraft stay light in Night. Do not invert the UI.
- **Aircraft livery and path colour** are the same token: each aircraft on screen takes the next free one of `path1` to `path6`.
- **Assets:** prefer vector drawing. Port sprites and icons from the SVGs in `docs/design-system/assets/`; do not redraw them or mix in another icon set.
- **Accessibility:** controls are at least 48dp; an aircraft's pick area is at least 72dp. Every status colour is paired with an icon or a word. Respect the device's reduced-motion setting.

### Copy

Write like a friendly tower controller: short, calm, specific. Sentence case. Buttons start with a verb (Take off, Resume, Try again). Say what happened without blame ("Two aircraft got too close over runway 27."). No emoji.

### When the design system lacks something

Do not improvise a one-off. Add the token or component to the design system first (for example the sunset palette the splash page needs), then use it from the theme.

## Working in this repo

- Update `docs/` when game rules or architecture change.
- Work is planned and tracked as OpenSpec changes (`openspec/`), each tied to a GitHub issue.
- Use the `triage` skill (`.claude/skills/triage/SKILL.md`) to turn an issue into a change: it fleshes out the issue, proposes the change, links the two, and labels the issue `triaged` or `unclear`.
- Follow the `git-workflow` skill (`.claude/skills/git-workflow/SKILL.md`) for every commit, push, branch, worktree and pull request, and whenever an OpenSpec change is proposed, applied or archived. In short: never touch `main`; propose and archive on `development`; implement each change on its own `change/<change-name>` branch in a worktree and merge it by pull request.
