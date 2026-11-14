# Building it in Compose

The game renders with Compose Multiplatform. The HTML and CSS in this system are the visual reference; the game reads the same values from a theme object in `shared/src/commonMain/kotlin/nl/hexmaster/finalapproach/ui/theme`. That theme is generated from `tokens.json` by `tools/gen-kotlin.cjs`. Do not edit the generated files by hand.

## Where the app stands

The app's UI components are version 2. The app's world is not yet: scenery sprites, runway and helipad drawing and level backdrops still render version 1. Until that port lands, the generator builds the app's scenery sprite data from the version 1 sprites kept in `tools/scenery-v1.js`, not from the version 2 sprites in `bundle.js`. Everything in `world.md` describes the design system; the port brings the app to it and then removes the version 1 tokens.

## Mapping tokens to the theme

- **Colours:** one `FaColors` class with a property per colour token, in camelCase. It is built from a `FaColorTokens` holder instead of a constructor with a parameter per token: a method takes at most 255 argument slots and a `Color` takes two, so such a constructor stops loading once there are more than 126 tokens. Every step of a ramp is its own property: `sun200`, `sun500`, `sun700`, `sun900`, `paper0` to `paper500`, `roof300`, `shadowCast`. `DayColors`, `NightColors` and `DawnColors` are exposed through a `CompositionLocal` and read as `FaTheme.colors`. The `sunset-` tokens go into `SunsetColors` and the `light-` tokens into `FaLights`, together with `light-max-opacity` and the light cycles; neither is part of `FaColors`.
- **Type:** `DynaPuff`, `Nunito` and `Inconsolata` are bundled as Compose resources, in the weights the styles use. `FaTypography` has one `TextStyle` per type style, named as here (`displayLg`, `buttonLabel`, `row`, `labelSm`, `callsign`), read as `FaTheme.type`. Running text is Nunito; no style in Nunito is lighter than 600 and none in Inconsolata lighter than 700.
- **Dimensions:** `FaDimens` holds spacing, radius, line, size and path values in `dp`. The version 2 values are there under their token names: the line ranks (`lineHero`, `lineZone`, `lineUi`, `lineUiSm`, `lineDetail`), the radii (`radiusChip`, `radiusStrip`, `radiusControl`, `radiusPanel`, `radiusDialog`), the lips (`lipSm`, `lipMd`, `lipLg`, `lipPanel`, `lipDialog`, `lipPressed`, `lipInset`, `lipEdge`), `highlight`, `wellInset`, the contact offsets and `stripHeight`. Sprite sizes are `dp` at 1x viewport scale and multiply by the world-to-screen factor.
- **Opacity and motion:** `FaOpacity` holds the unitless factors. `FaMotion` holds durations in milliseconds and easings as `CubicBezierEasing` with the four numbers from the `easing` tokens.
- **Layers:** draw in `z-` order inside a single `Canvas`; the tokens are an ordering, not a Compose `zIndex`.

```kotlin
object FaMotion {
    const val durPressMs = 90
    const val durLandMs = 420
    val easeBounce = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
    val easeSnap = CubicBezierEasing(0.2f, 0.9f, 0.3f, 1f)
}
```

### Version 1 tokens

Deprecated tokens are still generated, because the app's version 1 world reads them. Each carries a KDoc note that names its replacement, such as "v1. Use `paper100` in new code.". They are not marked `@Deprecated`, so the world keeps building without warnings. New code never reads one.

## Surfaces

Two drawing primitives in `ui/components/FaSurface.kt` enforce U1 and U2. Which one a composable calls decides whether it has a lip.

- **`FaTone`** is the four tones of one moulded surface: `light` (the highlight), `base` (the face), `lip` and `edge` (the line along the bottom of the lip). `FaColors` offers the ready-made sets `tonePrimary`, `toneSecondary`, `toneDanger`, `toneGhost`, `tonePaper`, `toneDisabled`, `toneKnob` and `toneSegment`.
- **`drawMoulded`** draws the moulded surface back to front: contact shadow, outline, lip with its edge line, face with its highlight. Inner corners follow the nested-radius rule (U5). Its `insetLip` mode cuts the lip out of the piece's own shape, for the toggle knob and the selected segment.
- **`drawFlat`** draws the flat surface of every readout: a fill inside the outline on a contact shadow. It has no lip.
- **`drawWell`** draws a sunken well; `drawDashedRule` a dashed rule on paper.
- **`Modifier.faMouldedSurface`** and **`Modifier.faFlatSurface`** apply them to an element that is not pressed, such as a panel or a flight strip. `faFaceShape` clips content to the face.
- **`FaMoulded`** (`FaMoulded.kt`) is the pressable moulded surface. Buttons, icon buttons and level tiles are all this. It handles the press travel in `dur-press` with `ease-snap` (at once with reduced motion), the two-tone focus ring, the shine, and the flat disabled state (U7).

Build a new pressable control on `FaMoulded` and a new readout on `faFlatSurface`. Do not draw a lip by hand.

## Drawing rules

- Art constants live in the theme. No colour, stroke width or radius literal in a composable or in engine code.
- Convert world units to pixels only at the render boundary; UI stroke widths and aircraft sprite sizes are in `dp` and do not scale with world distance.
- An aircraft is fill, then `Stroke(width = lineHero, join = StrokeJoin.Round, cap = StrokeCap.Round)` in `outline`. UI outlines are `lineUi`.
- A flight path is two `drawPath` calls on the same `Path` with `PathEffect.dashPathEffect(floatArrayOf(pathDash, pathGap), phase)`: casing first, colour second. Animate `phase` for marching.
- The soft shadow of an aircraft is the sprite's silhouette in `shadowGround`, offset down-right, drawn in the shadow pass before any aircraft.
- Hard ground shadows go in one layer in `shadowCast`, drawn once at `opacity-shadow-cast`, between flat ground and standing scenery.
- Prefer vector drawing over bitmaps. The Aircraft and Icons asset groups are the shape sources.
