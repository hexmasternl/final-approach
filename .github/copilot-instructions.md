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

## Design system

The design system in `docs/design-system/` defines how everything looks. Open `docs/design-system/showcase.html` before building or changing any UI. The game implements the same values in a Compose theme under `ui/theme`, generated from `tokens.json`; do not edit it by hand. The tools in `docs/design-system/tools/` build the showcase (`build-showcase.cjs`), the app icon (`build-app-icon.cjs`) and the theme (`gen-kotlin.cjs`), and check the tokens (`check-tokens.cjs`). Keep this section the same as the design-system section of `CLAUDE.md`.

### Principles

Version 2 of the design system. Three general principles:

1. **Toy scale.** Sizes are exaggerated. The jumbo is nearly twice the light prop.
2. **Silhouette before colour.** Types, zones and states differ by shape first. Colour never carries meaning alone.
3. **The field stays clear.** UI lives in flat strips at the edges. Nothing covers a landing zone.

Nine world rules, for everything on the ground (`docs/design-system/world.md`):

- **W1** One sun, top-left: a lit tone north-west, a shade tone south-east, a hard shadow cast down-right.
- **W2** Three flat tones per material, never a gradient; at most three main materials in a sprite.
- **W3** Navy outside, tinted inside: the silhouette is `outline`, inner lines are a darker tone of their material; only a small part such as a chimney keeps a thin outline of its own.
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

## Git workflow
Follow the `git-workflow` skill (`.github/skills/git-workflow/SKILL.md`) for every commit, push, branch, worktree and pull request, and whenever an OpenSpec change is proposed, applied or archived.
- Never touch `main`: it represents production.
- Propose and archive OpenSpec changes on `development`; committing and pushing there directly is allowed for that.
- Implement a change only if it names an open GitHub issue and that issue names the change. Work in a new worktree on `change/<change-name>`, branched from `development`, commit per completed task, then push and open a pull request into `development`.
- Archiving closes the issue, moves it to Done on its GitHub project, and removes the worktree and the change branch (local and remote).
