# Final Approach

A cartoon-style, top-down air traffic game. Aircraft enter the viewport from the edges; the player taps one, drags a path, and lands it. Airplanes land on runways, helicopters on helipads. Differing speeds are the core challenge: fast aircraft catch up with slow ones, so the player must sequence landings.

The app is scaffolded under `app/src` and has its main page. The game itself (engine, input, HUD) is not built yet; the architecture below is the one to follow.

## Where things are

| Path | What |
|---|---|
| `docs/game-design.md` | Gameplay, aircraft roster, rules, scoring. The source of truth for game rules. |
| `docs/architecture.md` | Tech stack, module layout, engine design. |
| `docs/art-style.md` | Visual and audio style brief. |
| `docs/roadmap.md` | Milestones 1 to 9. |
| `docs/design-system/showcase.html` | The design system on one page (tokens, type, components, screens). Also as `showcase.pdf`. |
| `docs/design-system/tools/` | Node tools: `build-showcase.cjs` builds the showcase, `build-app-icon.cjs` the app icon and the Android launcher layers, `gen-kotlin.cjs` the Compose theme and sprite data, `check-tokens.cjs` checks `tokens.json`. |
| `docs/design-system/assets/` | SVG sources: `aircraft/`, `scenery/`, `icons/`, `app-icon/`. |
| `.github/copilot-instructions.md` | The same conventions for Copilot. Keep it consistent with this file. |

Open work is tracked as GitHub issues (`gh issue list`).

## Core rules

Keep these consistent with `docs/game-design.md`; update the doc when a rule changes.

- Tap an aircraft to select it, then drag to draw its path. A new path replaces the old one.
- The aircraft follows the path at its own speed. When the path ends, it flies straight on in its last heading.
- A path that enters a valid landing zone clears the aircraft to land: the path is cut off at the zone and takes no more points. Airplanes need a runway threshold zone, entered within the alignment tolerance of that end's landing direction; helicopters need a helipad zone. The wrong zone type does nothing. A runway path is then snapped onto the runway centre line through a smooth lead-in, so airplanes touch down and roll out on the centre line, then fade out. All airplanes roll out at one shared landing speed, half the light prop's flight speed, so same-direction rollouts on one runway never collide.
- Failures: two aircraft whose collision shapes touch (collision), or an aircraft outside the level with no path (beyond the leave margin, including when its path ends there). Either ends the level. Aircraft closer than the warning distance only get a warning ring.
- Five aircraft types: light prop, turboprop, jet, jumbo, helicopter. Each has its own speed.

## Architecture

Kotlin Multiplatform with Compose Multiplatform. Android first; iOS later from the same shared module.

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

Layers:

1. **Domain:** plain data types and configuration (aircraft specs, airport layouts).
2. **Engine:** a deterministic simulation. `update(deltaSeconds)` advances aircraft and detects landings, collisions and misses. No UI dependencies. A single `GameEngine` owns the state and exposes `StateFlow<GameState>`.
3. **UI:** a Compose `Canvas` renders engine state; a frame clock (`withFrameNanos`) calls `engine.update` with delta time.
4. **Input:** pointer events converted to world coordinates. Tap picks the nearest aircraft; drag appends points to its `FlightPath`.

Rules that follow from this:

- Never put game rules in composables. Simulation, rendering and input stay separate.
- `commonMain` uses only `kotlin.*` and `kotlinx.*`. No `android.*` or `java.*`. Platform concerns (back handling, closing the app, version, reduced motion, ads) go behind `AppHost` (`platform/AppHost.kt`), implemented in the host app.
- Units: positions in world units, speeds in world units per second, angles in radians. Convert to pixels only at the render boundary.
- Tunable values (speeds, radii, spawn rates, timings) live in config or data classes, not as magic numbers.
- Engine and domain logic get `kotlin.test` unit tests in `commonTest`: path following, landing detection, collisions. Keep the Compose UI thin.

Conventions: Kotlin official code style, 4-space indent, `val` over `var`, no `!!`. Gradle Kotlin DSL with a version catalog (`gradle/libs.versions.toml`).

Build and test, from `app/src`:

```
./gradlew :androidApp:assembleDebug
./gradlew :shared:allTests
```

The Android SDK location goes in `app/src/local.properties`, which is not committed.

## Design system

The design system defines how everything looks. Open `docs/design-system/showcase.html` before building or changing any UI. The full system, with per-component guidelines, is published at https://claude.ai/artifact/WPS86AMwfU8Ph1ehK5kdpL (private to the owner unless shared).

The HTML and CSS in the design system are the visual reference. The game implements the same values in a Compose theme under `ui/theme`. The theme is generated from `tokens.json`; do not edit it by hand.

### Principles

Version 2 of the design system. Three general principles:

1. **Toy scale.** Sizes are exaggerated. The jumbo is nearly twice the light prop.
2. **Silhouette before colour.** Types, zones and states differ by shape first. Colour never carries meaning alone.
3. **The field stays clear.** UI lives in flat strips at the edges. Nothing covers a landing zone.

Nine world rules, for everything on the ground (`docs/design-system/world.md`):

- **W1** One sun, top-left: a lit tone north-west, a shade tone south-east, a hard shadow cast down-right.
- **W2** Three flat tones per material, never a gradient; at most three main materials in a sprite.
- **W3** Navy outside, tinted inside: the silhouette is `outline`, inner lines are a darker tone of their material.
- **W4** Line weight is rank: aircraft 4, landing zones 3, scenery 2.5, inner detail 1.25.
- **W5** Standing things show a south wall, in a height tier, and never rotate.
- **W6** Flat ground has no outline; only things with height and landing zones get a navy line.
- **W7** Edges are transitions: shoulder and rough grass, shallows and sand, kerb and verge.
- **W8** Detail clusters, open ground rests; only the airfield is mown in stripes.
- **W9** Aircraft own the brightest white and the most saturated colours; scenery whites are cream.

Seven UI rules, for controls and panels (`docs/design-system/brand-book.md`):

- **U1** Moulded, not boxed: a face, a highlight, a lip in the fill's own 700 tone, one thin outline.
- **U2** A lip means pressable; readouts, chips and stickers are flat.
- **U3** Thin line, thick form: the UI outline is 2.5dp, depth comes from the lip and a flat contact shadow.
- **U4** Tinted inside: dividers and keylines take a darker tone of their surface.
- **U5** Nested radii: an inner corner is the outer radius minus the gap.
- **U6** Tower paperwork: flight strips, boarding-pass dialogs, stamps.
- **U7** Disabled is flat, not faded: no colour, lip or shine, full opacity.

The app's UI components are version 2; the app's world (scenery sprites, runway and helipad drawing, level backdrops) still renders version 1 until it is ported in the follow-up to issue #76.

### Using it in code

- **No literals.** No colour, stroke width, radius, size, duration or easing literal in a composable or in engine code. Everything comes from the theme.
- **Colours:** one `FaColors` data class with a property per colour token in camelCase (`grass500`, `onAccent`, `path1`), with `DayColors`, `NightColors` and `DawnColors`, provided through a `CompositionLocal`. Materials and accents are ramps with fixed step roles: 100 or 200 highlight, 300 lit, 500 base, 700 shade or lip, 900 tinted inner line (`sun200`, `sun500`, `sun700`, `sun900`). UI surfaces are the `paper` ramp (`paper0` to `paper500`).
- **Type:** DynaPuff for headers, titles, button labels and the score. Nunito for running text, never lighter than weight 600. Inconsolata for labels, readouts and callsigns, never lighter than weight 700. One `TextStyle` per design-system style, same names (`displayLg`, `buttonLabel`, `row`, `callsign`).
- **Dimensions:** spacing, radius, line, size and flight-path values as `dp` in `FaDimens`. Use the version 2 values: the line ranks `lineHero`, `lineZone`, `lineUi`, `lineUiSm`, `lineDetail`; the radii `radiusChip`, `radiusStrip`, `radiusControl`, `radiusPanel`, `radiusDialog`; the lips `lipSm`, `lipMd`, `lipLg`, `lipPanel`, `lipDialog`; `stripHeight` for HUD readouts. UI sizes are fixed; world positions scale with the viewport.
- **A lip means pressable.** Build pressable controls on `FaMoulded` and readouts on the flat surface (`drawFlat`, `Modifier.faFlatSurface`) in `ui/components`. Do not draw a lip by hand, and never in `outline`.
- **Disabled is flat.** A disabled control is drawn in `paper` tones with its label in `ink3`, at full opacity. Do not fade it.
- **No version 1 tokens in new code.** Tokens marked `deprecated` in `tokens.json` (the three surface colours, the navy lip shadows, the numbered line widths, the old radii, the deep shades, the HUD chip height and the disabled opacity) exist only for the world that is not ported yet. Their KDoc names the replacement.
- **Motion:** durations in milliseconds and easings as `CubicBezierEasing` from the `duration` and `easing` tokens. Arrivals bounce, controls snap.
- **Layers:** draw in the fixed order field, zone glows, paths, ground shadows, aircraft, effects, clouds, HUD, overlays. Inside the field: flat ground, then the one layer of hard ground shadows (`shadowCast` at `opacity-shadow-cast`), then standing scenery. The ground-shadows layer above the paths holds only the soft shadows of aircraft.
- **Themes:** Day is the default; Night is for night levels, Dawn for levels before sunrise. Paper, strips and aircraft stay light in Night and Dawn. Do not invert the UI.
- **Aircraft livery and path colour** are the same token: each aircraft on screen takes the next free one of `path1` to `path6`.
- **Assets:** prefer vector drawing. Port sprites and icons from the design system (`bundle.js` and the SVGs in `docs/design-system/assets/`); do not redraw them or mix in another icon set.
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
