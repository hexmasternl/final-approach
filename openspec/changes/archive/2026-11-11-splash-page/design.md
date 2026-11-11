# Design

## Context

No code is scaffolded yet; this change assumes the scaffold from roadmap milestone 1 exists (`shared/` with `commonMain`, `androidApp` as a thin host). The architecture in `CLAUDE.md` applies: logic separate from rendering, no Android APIs in `commonMain`, no colour or timing literals in composables.

The design system has Day and Night themes and top-down aircraft sprites. This scene is a side-on view of a sky at sunset, which neither covers.

## Goals / Non-Goals

**Goals:**

- A splash animation whose timing and positions are plain, unit-testable logic.
- A scene that is drawn from vectors and scales to any screen.
- Sunset colours and the side-view airplane living in the theme and design system, not as one-off literals.

**Non-Goals:**

- A navigation library. Two screens do not need one.
- Sound, music, or loading work behind the splash page.

## Decisions

**The animation is a pure function of elapsed time.** A `SplashTimeline` in `commonMain` takes elapsed seconds and a `SplashConfig` and returns the scene state: phase (towing, released, settled), airplane position, banner position, whether the tow line is attached, and the wave phase. Positions are fractions of screen width and height. The composable only reads this state and draws. This keeps the release rule ("banner centre reaches screen centre") and the 2-second hand-over testable in `commonTest`. Rejected: Compose animation APIs chained in the composable, which would put the sequence rules in UI code and make them untestable.

**The frame clock drives it.** The screen advances elapsed time with `withFrameNanos` and delta time, the same pattern the game loop uses.

**Everything is drawn on one `Canvas` from vectors.** Sky bands and sun first, then the silhouette, then tow line, airplane and banner. The silhouette is a single path defined in a normalised coordinate space, anchored to the bottom edge and scaled uniformly by screen width, so it keeps its proportions; the sky fills whatever height remains. Rejected: a bitmap background, which would need per-density assets and would stretch.

**The sky is flat bands, not a smooth gradient.** Four horizontal bands from deep red-orange at the top to glowing orange at the horizon, plus a sun disc partly behind the silhouette. This follows the design system's "flat and bright" principle.

**A new side-view airplane is drawn for this scene.** The existing sprites are top-down and would read wrongly against a side-on horizon. The new drawing is a light prop in side view, in the design-system style: 4dp `outline` stroke, round joins, `fuselage` body, livery accent, one large `glass` window. It is added to the design system's Aircraft assets. Rejected: reusing the top-down sprite, which mixes two viewpoints in one picture.

**The banner waves by drawing it in vertical slices.** The banner (an outlined pennant on `surface` with the name in the `display` font) is drawn once into an offscreen layer and then painted as narrow vertical slices, each offset vertically by a sine of its horizontal position and the wave phase. Amplitude stays small relative to the text height so the name remains readable. While towed, the same mechanism runs with a faster phase so the banner ripples. Rejected: positioning each glyph separately, which is more code and breaks kerning.

**The release has one small bounce.** On release the banner eases to the exact centre with `ease-bounce`; the wave then uses `dur-sway` and `ease-sway`, as the windsock does.

**Sunset colours are a separate colour set in the theme.** `SunsetColors` holds `sunsetSkyTop`, `sunsetSkyUpper`, `sunsetSkyLower`, `sunsetSkyHorizon`, `sunsetSun`, `sunsetSilhouette` and `sunsetTowerLight`. The same tokens are added to the design system. Banner text and outlines keep using the existing `ink`, `surface` and `outline` tokens. Rejected: a third full theme, which would force sunset values for sixty tokens that this one scene never uses.

**Screen flow is a small state in the UI layer.** An app-level screen state (`Splash`, then the next screen) decides what is shown. The splash screen calls one `onFinished` callback for both the automatic hand-over and the tap.

**Reduced motion comes from the host.** `commonMain` declares a small interface that reports whether the device asks for reduced motion; `androidApp` implements it from the system animation scale setting. With reduced motion on, the timeline is created in its settled state with the wave amplitude at zero.

**Timings are configuration.** Airplane speed, banner length, tow-line length, wave amplitude and period, and the 2-second hand-over delay live in `SplashConfig`.

## Risks / Trade-offs

- [Offscreen layer support differs between Compose Multiplatform targets] → Keep the slice drawing behind one function so it can fall back to drawing the banner flat with a waving outline if a target lacks the API.
- [A long animation delays every app start] → The tap-to-skip is always available, and the whole sequence is tuned through `SplashConfig`; keep the tow under about 4 seconds.
- [The silhouette is too small or too large on extreme aspect ratios] → Cap its height at a fraction of the screen height in addition to scaling by width.
- [The main page (issue #3) may not exist when this is built] → `onFinished` is the only coupling; it can point at any screen.

## Open Questions

- Whether a later launch in the same day should shorten or skip the animation. The tap already covers impatience; this can be decided after playing with it.
