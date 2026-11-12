# Building it in Compose

The game renders with Compose Multiplatform. The HTML and CSS in this system are the visual reference; the game reads the same values from a theme object in `shared/src/commonMain/kotlin/nl/hexmaster/finalapproach/ui/theme`.

## Mapping tokens to the theme

- Colours: one `FaColors` data class with a property per colour token, in camelCase (`grass500`, `onAccent`, `path1`). Provide `DayColors`, `NightColors` and `DawnColors` and expose them through a `CompositionLocal`. The `sunset-` tokens go into `SunsetColors` and the `light-` tokens into `FaLights`, together with `light-max-opacity` and the light cycles in seconds; neither is part of `FaColors`.
- Type: load `DynaPuff` and `Inconsolata` from the font files in `fonts/` as Compose resources. One `TextStyle` per type style, named as here (`displayLg`, `buttonLabel`, `callsign`).
- Dimensions: `FaDimens` with spacing, radius, line, size and path values in `dp`. Sprite sizes are `dp` at 1x viewport scale and multiply by the world-to-screen factor.
- Motion: durations in milliseconds and easings as `CubicBezierEasing` with the four numbers from the `easing` tokens.
- Layers: draw in `z-` order inside a single `Canvas`; the tokens are an ordering, not a Compose `zIndex`.

```kotlin
object FaMotion {
    val Bounce = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
    val Snap = CubicBezierEasing(0.2f, 0.9f, 0.3f, 1f)
    const val PressMs = 90
    const val LandMs = 420
}
```

## Drawing rules

- Art constants live in the theme. No colour, stroke width or radius literal in a composable or in engine code.
- Convert world units to pixels only at the render boundary; stroke widths and sprite sizes are in `dp` and do not scale with world distance.
- Draw each outlined shape as fill, then `Stroke(width = line3, join = StrokeJoin.Round, cap = StrokeCap.Round)` in `outline`.
- A flight path is two `drawPath` calls on the same `Path` with `PathEffect.dashPathEffect(floatArrayOf(pathDash, pathGap), phase)`: casing first, colour second. Animate `phase` for marching.
- A hard lip is the same rounded rect drawn again in `outline`, offset down by the lip height, before the face.
- The ground shadow is the sprite's silhouette in `shadowGround`, offset down-right, drawn in the shadow pass before any aircraft.
- Prefer vector drawing over bitmaps. The Aircraft and Icons asset groups are the shape sources.
