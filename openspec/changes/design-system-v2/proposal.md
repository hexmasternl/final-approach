# Proposal

GitHub issue: #76

## Why

Aircraft look good, but everything else in a level reads as primitive, and the UI reads as coarse. The cause is in the design system's own rules: scenery is built from primitives under a four-fill budget, nothing on the ground has light or shadow, every inner line is navy, and every UI shape carries a 4dp outline plus a navy lip. A new direction, version 2, was drawn and approved on 2026-11-14; this change makes it the design system.

## What Changes

- **New principles.** "Toy scale", "silhouette before colour" and "the field stays clear" stay. "Outline everything" and "flat and bright" are replaced by nine world rules (W1 to W9) and seven UI rules (U1 to U7), written into `brand-book.md` and `world.md`.
- **World rules.** One sun from the top-left; three flat tones per material; navy silhouette with tinted inner lines; line weight as rank; standing things show a south wall in one of three height tiers; flat ground has no navy outline; edges are transitions; detail clusters while open ground rests; aircraft own the brightest white and the most saturated colours.
- **UI rules.** Moulded shapes (face, highlight, lip in the fill's own 700 tone, one thin outline); a lip means pressable; a 2.5dp outline; tinted inner lines; nested radii; tower paperwork (flight strips, boarding passes, stamps); disabled is flat, not faded.
- **Tokens.** Material and accent colours become ramps of 3 to 6 tones with Day, Night and Dawn values. One default outline becomes five line ranks. A `paper` ramp replaces the three `surface` tokens. Nunito becomes the body face; DynaPuff stays for display, Inconsolata for labels, readouts and callsigns. New tokens are added next to the existing ones; nothing the app uses today is removed in this change.
- **Scenery.** Every scenery sprite is redrawn to W1 to W9, for Meadow, Island and Savanna, with a cast-shadow silhouette and a height tier per sprite. The four-fill budget, the `line-fine` exception and the "no shadow on the ground" rule go.
- **Ground and landing zones.** The airfield is mown in stripes parallel to the runway and the countryside is not. Runways and helipads keep their footprint and world size and gain a shoulder, a rough-grass fringe, fuller markings and lights. Taxiways, aprons, roads, fields and water get their transition bands.
- **Components.** Button, IconButton, Toggle, SegmentedControl, Chip, HudBar and its readouts, StreakBadge, ScorePopup, Panel, Dialog (portrait and landscape), LevelCard and CoachMark are respecified and rebuilt in `bundle.css`. HUD readouts become flat 40dp strips; pause is the only HUD control with a lip.
- **Showcase.** `showcase.html` and `showcase.pdf` are regenerated for v2 in Day, Night and Dawn, the social visuals that show scenery or UI are rebuilt, and the published design system is updated.
- **Instructions.** The design-system sections of `CLAUDE.md` and `.github/copilot-instructions.md` are rewritten for v2.

The approved proposal page and its sources are kept in this change's `reference/` folder. They are the visual reference for every task.

Out of scope: any change to app code (Compose theme values beyond what regeneration adds, sprites, composables, level data), which is a follow-up change that ports the app and then removes the v1 tokens; new levels or scenery kinds; gameplay; sound; the splash page's sunset scene; the app icon; aircraft sprites, liveries, path colours, the flight path, the icon set, motion tokens, layer order, touch sizes and copy, all of which stay as they are.

Depends on: nothing. Overlaps with `detailed-scenery` (#33): that change's open scenery drawing tasks target the v1 look and are superseded by this change; its world-zoom, level-layout and ambient work is unaffected. Until the follow-up port lands, the app keeps rendering v1.

## Capabilities

### New Capabilities

- `design-system`: what the design system defines and guarantees: its principles, token families, line ranks, scenery and landing-zone construction rules, component construction rules, the showcase, and what must stay stable for the app that is generated from it.

### Modified Capabilities

None. No app behaviour changes in this change. The app-facing specs that describe the v1 look (`level-themes`, `runway-markings`, `player-hud`, `game-ended-dialog`, `pause-menu`, `level-selection`, `tutorial-overlays`) change in the follow-up port.

## Impact

- Design system: `tokens.json`, `brand-book.md`, `world.md`, `motion-audio.md` (unchanged values, new references), `compose.md`, every file in `components/`, `bundle.css`, `bundle.js` (scenery sprites, runway, helipad, cloud, windsock), `assets/scenery/*.svg`, `showcase.html`, `showcase.pdf`, `visuals/`, `tools/gen-kotlin.cjs` (reads ramps and line ranks; scenery budget check replaced by the tone and material checks).
- Generated code: running the generator adds the new tokens to `ui/theme`; existing names keep their values, so the app builds and looks as before.
- Docs: `CLAUDE.md`, `.github/copilot-instructions.md`, `docs/art-style.md`.
- Fonts: Nunito (SIL Open Font License) is added to the design system's fonts.
- No new runtime dependencies.
