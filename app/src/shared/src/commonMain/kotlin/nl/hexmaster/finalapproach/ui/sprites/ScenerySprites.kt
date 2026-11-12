// Generated from the design system scenery sprites (components/bundle.js). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.sprites

import androidx.compose.ui.graphics.Color
import nl.hexmaster.finalapproach.ui.theme.FaColors

/** The colour roles scenery is painted with. Colours come from the theme at draw time. */
internal enum class SceneryPaint { White, Shade, Pane, Glass, Blue, Leaf, Bush, Deep, Wood, Furrow, Roof, Sand, Stone, Foam, Hull, Cab, Sock, Dark }

/** The theme colour of a scenery paint role. */
internal fun FaColors.scenery(paint: SceneryPaint): Color = when (paint) {
    SceneryPaint.White -> surfaceRaised
    SceneryPaint.Shade -> fuselageShade
    SceneryPaint.Pane -> sky300
    SceneryPaint.Glass -> glass
    SceneryPaint.Blue -> sky500
    SceneryPaint.Leaf -> grass300
    SceneryPaint.Bush -> grass700
    SceneryPaint.Deep -> grass900
    SceneryPaint.Wood -> soil500
    SceneryPaint.Furrow -> soil700
    SceneryPaint.Roof -> roof500
    SceneryPaint.Sand -> sand300
    SceneryPaint.Stone -> stone300
    SceneryPaint.Foam -> foam
    SceneryPaint.Hull -> asphalt700
    SceneryPaint.Cab -> cherry
    SceneryPaint.Sock -> tangerine
    SceneryPaint.Dark -> outline
}

/** How a scenery shape is stroked: not at all, with the scenery outline (`line-2`) or with the detail line (`line-fine`). */
internal enum class SceneryStroke { None, Outline, Fine }

/** One shape of a scenery sprite, as SVG path data on the sprite's grid. A null [fill] is a stroke only. */
internal class SceneryPart(
    val fill: SceneryPaint?,
    val stroke: SceneryStroke,
    val pathData: String,
    val strokePaint: SceneryPaint = SceneryPaint.Dark,
)

/**
 * The scenery sprites of the design system: top-down, front up, each on its own grid of
 * [width] by [height] units. One unit is one world unit at the size the sprite is drawn for.
 */
internal enum class ScenerySprite(val width: Float, val height: Float, val parts: List<SceneryPart>) {
    Cow(
        36f, 60f,
        listOf(
            SceneryPart(null, SceneryStroke.Outline, "M 18 52 q 0 4 3 6"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 19.5 57.5 A 2 2 0 1 0 23.5 57.5 A 2 2 0 1 0 19.5 57.5 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 6 24 H 9 A 3 3 0 0 1 12 27 V 27 A 3 3 0 0 1 9 30 H 6 A 3 3 0 0 1 3 27 V 27 A 3 3 0 0 1 6 24 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 27 24 H 30 A 3 3 0 0 1 33 27 V 27 A 3 3 0 0 1 30 30 H 27 A 3 3 0 0 1 24 27 V 27 A 3 3 0 0 1 27 24 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 6 42 H 9 A 3 3 0 0 1 12 45 V 45 A 3 3 0 0 1 9 48 H 6 A 3 3 0 0 1 3 45 V 45 A 3 3 0 0 1 6 42 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 27 42 H 30 A 3 3 0 0 1 33 45 V 45 A 3 3 0 0 1 30 48 H 27 A 3 3 0 0 1 24 45 V 45 A 3 3 0 0 1 27 42 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 7 36 A 11 17 0 1 0 29 36 A 11 17 0 1 0 7 36 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 9 31 q 4 -7 9 -3 3 4 -2 7 -5 2 -7 -4 z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 20 40 q 6 -3 8 3 0 6 -5 6 -5 -2 -3 -9 z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 22 23 q 4 0 5 4 -3 2 -6 -1 z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 4 12 A 4.5 2.5 0 1 0 13 12 A 4.5 2.5 0 1 0 4 12 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 23 12 A 4.5 2.5 0 1 0 32 12 A 4.5 2.5 0 1 0 23 12 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 10.5 12 A 7.5 9 0 1 0 25.5 12 A 7.5 9 0 1 0 10.5 12 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 13 13 q 2 -5 5 -3 0 5 -5 3 z"),
            SceneryPart(SceneryPaint.Sand, SceneryStroke.Fine, "M 13 5.5 A 5 3.5 0 1 0 23 5.5 A 5 3.5 0 1 0 13 5.5 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 12.7 15 A 1.3 1.3 0 1 0 15.3 15 A 1.3 1.3 0 1 0 12.7 15 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 20.7 15 A 1.3 1.3 0 1 0 23.3 15 A 1.3 1.3 0 1 0 20.7 15 Z"),
        ),
    ),
    Farmer(
        42f, 46f,
        listOf(
            SceneryPart(SceneryPaint.Wood, SceneryStroke.Fine, "M 35 6 H 35 A 1.7 1.7 0 0 1 36.7 7.7 V 24.3 A 1.7 1.7 0 0 1 35 26 H 35 A 1.7 1.7 0 0 1 33.3 24.3 V 7.7 A 1.7 1.7 0 0 1 35 6 Z"),
            SceneryPart(null, SceneryStroke.Outline, "M 29.5 7 h 11 M 29.5 7 V 1.5 M 35 7 V 1.5 M 40.5 7 V 1.5"),
            SceneryPart(SceneryPaint.Blue, SceneryStroke.Outline, "M 6 16 H 6 A 3.5 3.5 0 0 1 9.5 19.5 V 28.5 A 3.5 3.5 0 0 1 6 32 H 6 A 3.5 3.5 0 0 1 2.5 28.5 V 19.5 A 3.5 3.5 0 0 1 6 16 Z"),
            SceneryPart(SceneryPaint.Blue, SceneryStroke.Outline, "M 35 16 H 35 A 3.5 3.5 0 0 1 38.5 19.5 V 28.5 A 3.5 3.5 0 0 1 35 32 H 35 A 3.5 3.5 0 0 1 31.5 28.5 V 19.5 A 3.5 3.5 0 0 1 35 16 Z"),
            SceneryPart(SceneryPaint.Blue, SceneryStroke.Outline, "M 2.5 31 A 18 9.5 0 1 0 38.5 31 A 18 9.5 0 1 0 2.5 31 Z"),
            SceneryPart(SceneryPaint.Sand, SceneryStroke.Fine, "M 3 16.5 A 3 3 0 1 0 9 16.5 A 3 3 0 1 0 3 16.5 Z"),
            SceneryPart(SceneryPaint.Sand, SceneryStroke.Fine, "M 32 16.5 A 3 3 0 1 0 38 16.5 A 3 3 0 1 0 32 16.5 Z"),
            SceneryPart(SceneryPaint.Sand, SceneryStroke.Outline, "M 11.5 31 A 9 9 0 1 0 29.5 31 A 9 9 0 1 0 11.5 31 Z"),
            SceneryPart(SceneryPaint.Sand, SceneryStroke.Fine, "M 16 31 A 4.5 4.5 0 1 0 25 31 A 4.5 4.5 0 1 0 16 31 Z"),
        ),
    ),
    BirdUp(
        44f, 30f,
        listOf(
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 19 9 Q 10 2 3 8 q -1 6 5 10 l 11 -1 z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 25 9 q 9 -7 16 -1 1 6 -5 10 l -11 -1 z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 4 8.5 q -0.5 4.5 4 8 l 1.5 -3 q -3 -2 -2.5 -6 z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 40 8.5 q 0.5 4.5 -4 8 l -1.5 -3 q 3 -2 2.5 -6 z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 22 20 l -5 8 h 10 z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 17 14 A 5 10 0 1 0 27 14 A 5 10 0 1 0 17 14 Z"),
            SceneryPart(SceneryPaint.Sand, SceneryStroke.Fine, "M 19.5 5.5 L 22 1 l 2.5 4.5 z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 18.9 9 A 1.1 1.1 0 1 0 21.1 9 A 1.1 1.1 0 1 0 18.9 9 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 22.9 9 A 1.1 1.1 0 1 0 25.1 9 A 1.1 1.1 0 1 0 22.9 9 Z"),
        ),
    ),
    BirdDown(
        44f, 30f,
        listOf(
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 19 10 l -8 2 q -5 6 -3 14 l 11 -8 z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 25 10 l 8 2 q 5 6 3 14 l -11 -8 z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 9 17 q -2 4 -0.5 8 l 4 -3 -0.5 -5.5 z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 35 17 q 2 4 0.5 8 l -4 -3 0.5 -5.5 z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 22 20 l -5 8 h 10 z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 17 14 A 5 10 0 1 0 27 14 A 5 10 0 1 0 17 14 Z"),
            SceneryPart(SceneryPaint.Sand, SceneryStroke.Fine, "M 19.5 5.5 L 22 1 l 2.5 4.5 z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 18.9 9 A 1.1 1.1 0 1 0 21.1 9 A 1.1 1.1 0 1 0 18.9 9 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 22.9 9 A 1.1 1.1 0 1 0 25.1 9 A 1.1 1.1 0 1 0 22.9 9 Z"),
        ),
    ),
    Tree(
        64f, 64f,
        listOf(
            SceneryPart(SceneryPaint.Deep, SceneryStroke.None, "M 17 40 A 20 17 0 1 0 57 40 A 20 17 0 1 0 17 40 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 12 22 A 13 13 0 1 0 38 22 A 13 13 0 1 0 12 22 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 26 24 A 12 12 0 1 0 50 24 A 12 12 0 1 0 26 24 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 8 34 A 11 11 0 1 0 30 34 A 11 11 0 1 0 8 34 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 18 37 A 13 13 0 1 0 44 37 A 13 13 0 1 0 18 37 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 33 35 A 10 10 0 1 0 53 35 A 10 10 0 1 0 33 35 Z"),
            SceneryPart(SceneryPaint.Leaf, SceneryStroke.None, "M 13.5 22 A 11.5 11.5 0 1 0 36.5 22 A 11.5 11.5 0 1 0 13.5 22 Z"),
            SceneryPart(SceneryPaint.Leaf, SceneryStroke.None, "M 27.5 24 A 10.5 10.5 0 1 0 48.5 24 A 10.5 10.5 0 1 0 27.5 24 Z"),
            SceneryPart(SceneryPaint.Leaf, SceneryStroke.None, "M 9.5 34 A 9.5 9.5 0 1 0 28.5 34 A 9.5 9.5 0 1 0 9.5 34 Z"),
            SceneryPart(SceneryPaint.Leaf, SceneryStroke.None, "M 19.5 37 A 11.5 11.5 0 1 0 42.5 37 A 11.5 11.5 0 1 0 19.5 37 Z"),
            SceneryPart(SceneryPaint.Leaf, SceneryStroke.None, "M 34.5 35 A 8.5 8.5 0 1 0 51.5 35 A 8.5 8.5 0 1 0 34.5 35 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 30 38 A 5 5 0 1 0 40 38 A 5 5 0 1 0 30 38 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 22.5 42 A 3.5 3.5 0 1 0 29.5 42 A 3.5 3.5 0 1 0 22.5 42 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 39 32 A 3 3 0 1 0 45 32 A 3 3 0 1 0 39 32 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 19 22 q 3 -4 7 -1 M 32 21 q 3 -4 7 -1 M 15 34 q 3 -4 7 -1 M 26 32 q 3 -4 7 -1"),
        ),
    ),
    Hedge(
        28f, 28f,
        listOf(
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 3 14 A 11 11 0 1 0 25 14 A 11 11 0 1 0 3 14 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 3 8 A 6 6 0 1 0 15 8 A 6 6 0 1 0 3 8 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 14 9 A 6 6 0 1 0 26 9 A 6 6 0 1 0 14 9 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 2 19 A 6 6 0 1 0 14 19 A 6 6 0 1 0 2 19 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 14 20 A 6 6 0 1 0 26 20 A 6 6 0 1 0 14 20 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 4.5 14 A 9.5 9.5 0 1 0 23.5 14 A 9.5 9.5 0 1 0 4.5 14 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 4.5 8 A 4.5 4.5 0 1 0 13.5 8 A 4.5 4.5 0 1 0 4.5 8 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 15.5 9 A 4.5 4.5 0 1 0 24.5 9 A 4.5 4.5 0 1 0 15.5 9 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 3.5 19 A 4.5 4.5 0 1 0 12.5 19 A 4.5 4.5 0 1 0 3.5 19 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 15.5 20 A 4.5 4.5 0 1 0 24.5 20 A 4.5 4.5 0 1 0 15.5 20 Z"),
            SceneryPart(SceneryPaint.Deep, SceneryStroke.None, "M 15 19 A 3.5 3.5 0 1 0 22 19 A 3.5 3.5 0 1 0 15 19 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 7 9.5 q 2.5 -3 5 -0.5 M 15 13.5 q 2.5 -3 5 -0.5 M 8 18.5 q 2.5 -3 5 -0.5"),
        ),
    ),
    Fence(
        12f, 48f,
        listOf(
            SceneryPart(SceneryPaint.Wood, SceneryStroke.Fine, "M 4.5 0 H 7.5 V 48 H 4.5 Z"),
            SceneryPart(SceneryPaint.Wood, SceneryStroke.Outline, "M 3.5 5.5 H 8.5 A 1.5 1.5 0 0 1 10 7 V 9 A 1.5 1.5 0 0 1 8.5 10.5 H 3.5 A 1.5 1.5 0 0 1 2 9 V 7 A 1.5 1.5 0 0 1 3.5 5.5 Z"),
            SceneryPart(SceneryPaint.Wood, SceneryStroke.Outline, "M 3.5 21.5 H 8.5 A 1.5 1.5 0 0 1 10 23 V 25 A 1.5 1.5 0 0 1 8.5 26.5 H 3.5 A 1.5 1.5 0 0 1 2 25 V 23 A 1.5 1.5 0 0 1 3.5 21.5 Z"),
            SceneryPart(SceneryPaint.Wood, SceneryStroke.Outline, "M 3.5 37.5 H 8.5 A 1.5 1.5 0 0 1 10 39 V 41 A 1.5 1.5 0 0 1 8.5 42.5 H 3.5 A 1.5 1.5 0 0 1 2 41 V 39 A 1.5 1.5 0 0 1 3.5 37.5 Z"),
        ),
    ),
    Farmhouse(
        84f, 72f,
        listOf(
            SceneryPart(SceneryPaint.Roof, SceneryStroke.Outline, "M 57 28 H 77 A 3 3 0 0 1 80 31 V 51 A 3 3 0 0 1 77 54 H 57 A 3 3 0 0 1 54 51 V 31 A 3 3 0 0 1 57 28 Z"),
            SceneryPart(null, SceneryStroke.Outline, "M 60 41 h 12"),
            SceneryPart(null, SceneryStroke.Fine, "M 72 41 l 8 -13 M 72 41 l 8 13"),
            SceneryPart(SceneryPaint.Roof, SceneryStroke.Outline, "M 7 8 H 57 A 3 3 0 0 1 60 11 V 61 A 3 3 0 0 1 57 64 H 7 A 3 3 0 0 1 4 61 V 11 A 3 3 0 0 1 7 8 Z"),
            SceneryPart(null, SceneryStroke.Outline, "M 32 24 v 24"),
            SceneryPart(null, SceneryStroke.Fine, "M 32 24 L 4 8 M 32 24 L 60 8 M 32 48 L 4 64 M 32 48 l 28 16"),
            SceneryPart(SceneryPaint.Stone, SceneryStroke.Outline, "M 13 31 H 19 A 2 2 0 0 1 21 33 V 39 A 2 2 0 0 1 19 41 H 13 A 2 2 0 0 1 11 39 V 33 A 2 2 0 0 1 13 31 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 14 36 h 4"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Fine, "M 25.5 64 H 38.5 A 1.5 1.5 0 0 1 40 65.5 V 67.5 A 1.5 1.5 0 0 1 38.5 69 H 25.5 A 1.5 1.5 0 0 1 24 67.5 V 65.5 A 1.5 1.5 0 0 1 25.5 64 Z"),
        ),
    ),
    Barn(
        104f, 72f,
        listOf(
            SceneryPart(SceneryPaint.Wood, SceneryStroke.Outline, "M 22 56 H 50 A 2 2 0 0 1 52 58 V 66 A 2 2 0 0 1 50 68 H 22 A 2 2 0 0 1 20 66 V 58 A 2 2 0 0 1 22 56 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 36 60 v 8"),
            SceneryPart(SceneryPaint.Roof, SceneryStroke.Outline, "M 7 6 H 65 A 3 3 0 0 1 68 9 V 57 A 3 3 0 0 1 65 60 H 7 A 3 3 0 0 1 4 57 V 9 A 3 3 0 0 1 7 6 Z"),
            SceneryPart(null, SceneryStroke.Outline, "M 4 33 h 64"),
            SceneryPart(null, SceneryStroke.Fine, "M 10 19.5 h 52 M 10 46.5 h 52"),
            SceneryPart(SceneryPaint.Stone, SceneryStroke.Outline, "M 70 25 A 15 15 0 1 0 100 25 A 15 15 0 1 0 70 25 Z"),
            SceneryPart(SceneryPaint.Stone, SceneryStroke.Fine, "M 76.5 25 A 8.5 8.5 0 1 0 93.5 25 A 8.5 8.5 0 1 0 76.5 25 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 82.8 25 A 2.2 2.2 0 1 0 87.2 25 A 2.2 2.2 0 1 0 82.8 25 Z"),
        ),
    ),
    CropField(
        48f, 48f,
        listOf(
            SceneryPart(SceneryPaint.Wood, SceneryStroke.None, "M 0 0 H 48 V 48 H 0 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 0 0 v 48 M 12 0 v 48 M 24 0 v 48 M 36 0 v 48 M 48 0 v 48", SceneryPaint.Furrow),
            SceneryPart(null, SceneryStroke.Outline, "M 6 3 v 42 M 18 3 v 42 M 30 3 v 42 M 42 3 v 42", SceneryPaint.Leaf),
        ),
    ),
    Terminal(
        140f, 96f,
        listOf(
            SceneryPart(SceneryPaint.Shade, SceneryStroke.Outline, "M 36 8 H 43 A 2 2 0 0 1 45 10 V 32 A 2 2 0 0 1 43 34 H 36 A 2 2 0 0 1 34 32 V 10 A 2 2 0 0 1 36 8 Z"),
            SceneryPart(SceneryPaint.Shade, SceneryStroke.Outline, "M 33 9 A 6.5 6.5 0 1 0 46 9 A 6.5 6.5 0 1 0 33 9 Z"),
            SceneryPart(SceneryPaint.Shade, SceneryStroke.Outline, "M 97 8 H 104 A 2 2 0 0 1 106 10 V 32 A 2 2 0 0 1 104 34 H 97 A 2 2 0 0 1 95 32 V 10 A 2 2 0 0 1 97 8 Z"),
            SceneryPart(SceneryPaint.Shade, SceneryStroke.Outline, "M 94 9 A 6.5 6.5 0 1 0 107 9 A 6.5 6.5 0 1 0 94 9 Z"),
            SceneryPart(SceneryPaint.Pane, SceneryStroke.Outline, "M 18 30 H 122 A 4 4 0 0 1 126 34 V 44 A 4 4 0 0 1 122 48 H 18 A 4 4 0 0 1 14 44 V 34 A 4 4 0 0 1 18 30 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 28 30 v 12 M 42 30 v 12 M 56 30 v 12 M 70 30 v 12 M 84 30 v 12 M 98 30 v 12 M 112 30 v 12"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 8 42 H 132 A 4 4 0 0 1 136 46 V 88 A 4 4 0 0 1 132 92 H 8 A 4 4 0 0 1 4 88 V 46 A 4 4 0 0 1 8 42 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Fine, "M 12.5 48 H 127.5 A 2.5 2.5 0 0 1 130 50.5 V 83.5 A 2.5 2.5 0 0 1 127.5 86 H 12.5 A 2.5 2.5 0 0 1 10 83.5 V 50.5 A 2.5 2.5 0 0 1 12.5 48 Z"),
            SceneryPart(SceneryPaint.Shade, SceneryStroke.Fine, "M 26 60 H 36 A 2 2 0 0 1 38 62 V 72 A 2 2 0 0 1 36 74 H 26 A 2 2 0 0 1 24 72 V 62 A 2 2 0 0 1 26 60 Z"),
            SceneryPart(SceneryPaint.Shade, SceneryStroke.Fine, "M 65 60 H 75 A 2 2 0 0 1 77 62 V 72 A 2 2 0 0 1 75 74 H 65 A 2 2 0 0 1 63 72 V 62 A 2 2 0 0 1 65 60 Z"),
            SceneryPart(SceneryPaint.Shade, SceneryStroke.Fine, "M 104 60 H 114 A 2 2 0 0 1 116 62 V 72 A 2 2 0 0 1 114 74 H 104 A 2 2 0 0 1 102 72 V 62 A 2 2 0 0 1 104 60 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 27 67 h 8 M 66 67 h 8 M 105 67 h 8"),
        ),
    ),
    Tower(
        56f, 56f,
        listOf(
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 35 34 H 50 A 3 3 0 0 1 53 37 V 50 A 3 3 0 0 1 50 53 H 35 A 3 3 0 0 1 32 50 V 37 A 3 3 0 0 1 35 34 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 38 47 h 9"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 8 3 H 42 A 5 5 0 0 1 47 8 V 42 A 5 5 0 0 1 42 47 H 8 A 5 5 0 0 1 3 42 V 8 A 5 5 0 0 1 8 3 Z"),
            SceneryPart(SceneryPaint.Pane, SceneryStroke.Outline, "M 41.6 31.9 L 31.9 41.6 L 18.1 41.6 L 8.4 31.9 L 8.4 18.1 L 18.1 8.4 L 31.9 8.4 L 41.6 18.1 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 25 8.5 v 4.5 M 25 37 v 4.5 M 8.5 25 h 4.5 M 37 25 h 4.5 M 12.3 12.3 l 3.9 3.9 M 33.8 33.8 l 3.9 3.9 M 37.7 12.3 l -3.9 3.9 M 16.2 33.8 l -3.9 3.9"),
            SceneryPart(SceneryPaint.Cab, SceneryStroke.Outline, "M 36.5 29.8 L 29.8 36.5 L 20.2 36.5 L 13.5 29.8 L 13.5 20.2 L 20.2 13.5 L 29.8 13.5 L 36.5 20.2 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 25 25 l 7 -7"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 22.8 25 A 2.2 2.2 0 1 0 27.2 25 A 2.2 2.2 0 1 0 22.8 25 Z"),
        ),
    ),
    Hangar(
        120f, 88f,
        listOf(
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 30 4 H 90 A 2 2 0 0 1 92 6 V 24 A 2 2 0 0 1 90 26 H 30 A 2 2 0 0 1 28 24 V 6 A 2 2 0 0 1 30 4 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 12 4 H 34 A 2 2 0 0 1 36 6 V 24 A 2 2 0 0 1 34 26 H 12 A 2 2 0 0 1 10 24 V 6 A 2 2 0 0 1 12 4 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 86 4 H 108 A 2 2 0 0 1 110 6 V 24 A 2 2 0 0 1 108 26 H 86 A 2 2 0 0 1 84 24 V 6 A 2 2 0 0 1 86 4 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 23 4 v 15 M 97 4 v 15"),
            SceneryPart(SceneryPaint.Shade, SceneryStroke.Outline, "M 12 20 H 108 A 8 8 0 0 1 116 28 V 76 A 8 8 0 0 1 108 84 H 12 A 8 8 0 0 1 4 76 V 28 A 8 8 0 0 1 12 20 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 4 36 q 56 -9 112 0 M 4 52 q 56 -9 112 0 M 4 68 q 56 -9 112 0"),
        ),
    ),
    Windsock(
        28f, 64f,
        listOf(
            SceneryPart(SceneryPaint.Sock, SceneryStroke.Outline, "M 6.5 54 L 10.5 5 h 7 l 4 49 z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.None, "M 7.3 44 h 13.4 l -0.8 -10 H 8.1 z M 8.9 24 h 10.2 l -0.8 -10 H 9.7 z"),
            SceneryPart(null, SceneryStroke.Outline, "M 6.5 54 L 10.5 5 h 7 l 4 49 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 7.3 44 h 13.4 M 8.1 34 h 11.8 M 8.9 24 h 10.2 M 9.7 14 h 8.6"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 8 56 A 6 6 0 1 0 20 56 A 6 6 0 1 0 8 56 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 11.5 56 A 2.5 2.5 0 1 0 16.5 56 A 2.5 2.5 0 1 0 11.5 56 Z"),
        ),
    ),
    Car(
        28f, 52f,
        listOf(
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 3.5 10 H 4.5 A 2 2 0 0 1 6.5 12 V 17 A 2 2 0 0 1 4.5 19 H 3.5 A 2 2 0 0 1 1.5 17 V 12 A 2 2 0 0 1 3.5 10 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 23.5 10 H 24.5 A 2 2 0 0 1 26.5 12 V 17 A 2 2 0 0 1 24.5 19 H 23.5 A 2 2 0 0 1 21.5 17 V 12 A 2 2 0 0 1 23.5 10 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 3.5 34 H 4.5 A 2 2 0 0 1 6.5 36 V 41 A 2 2 0 0 1 4.5 43 H 3.5 A 2 2 0 0 1 1.5 41 V 36 A 2 2 0 0 1 3.5 34 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 23.5 34 H 24.5 A 2 2 0 0 1 26.5 36 V 41 A 2 2 0 0 1 24.5 43 H 23.5 A 2 2 0 0 1 21.5 41 V 36 A 2 2 0 0 1 23.5 34 Z"),
            SceneryPart(SceneryPaint.Blue, SceneryStroke.Outline, "M 11 3 H 17 A 7 7 0 0 1 24 10 V 42 A 7 7 0 0 1 17 49 H 11 A 7 7 0 0 1 4 42 V 10 A 7 7 0 0 1 11 3 Z"),
            SceneryPart(SceneryPaint.Pane, SceneryStroke.Fine, "M 7.5 19 q 6.5 -4 13 0 l -1.5 5 h -10 z"),
            SceneryPart(SceneryPaint.Pane, SceneryStroke.Fine, "M 9 36 h 10 l 1.5 5 q -6.5 3 -13 0 z"),
            SceneryPart(SceneryPaint.Blue, SceneryStroke.Fine, "M 10 25 H 18 A 2 2 0 0 1 20 27 V 33 A 2 2 0 0 1 18 35 H 10 A 2 2 0 0 1 8 33 V 27 A 2 2 0 0 1 10 25 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 9 7.5 h 10"),
        ),
    ),
    FuelTruck(
        32f, 76f,
        listOf(
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 3.5 8 H 4.5 A 2 2 0 0 1 6.5 10 V 16 A 2 2 0 0 1 4.5 18 H 3.5 A 2 2 0 0 1 1.5 16 V 10 A 2 2 0 0 1 3.5 8 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 27.5 8 H 28.5 A 2 2 0 0 1 30.5 10 V 16 A 2 2 0 0 1 28.5 18 H 27.5 A 2 2 0 0 1 25.5 16 V 10 A 2 2 0 0 1 27.5 8 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 3.5 44 H 4.5 A 2 2 0 0 1 6.5 46 V 52 A 2 2 0 0 1 4.5 54 H 3.5 A 2 2 0 0 1 1.5 52 V 46 A 2 2 0 0 1 3.5 44 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 27.5 44 H 28.5 A 2 2 0 0 1 30.5 46 V 52 A 2 2 0 0 1 28.5 54 H 27.5 A 2 2 0 0 1 25.5 52 V 46 A 2 2 0 0 1 27.5 44 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 3.5 57 H 4.5 A 2 2 0 0 1 6.5 59 V 65 A 2 2 0 0 1 4.5 67 H 3.5 A 2 2 0 0 1 1.5 65 V 59 A 2 2 0 0 1 3.5 57 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 27.5 57 H 28.5 A 2 2 0 0 1 30.5 59 V 65 A 2 2 0 0 1 28.5 67 H 27.5 A 2 2 0 0 1 25.5 65 V 59 A 2 2 0 0 1 27.5 57 Z"),
            SceneryPart(SceneryPaint.Blue, SceneryStroke.Outline, "M 10 3 H 22 A 5 5 0 0 1 27 8 V 17 A 5 5 0 0 1 22 22 H 10 A 5 5 0 0 1 5 17 V 8 A 5 5 0 0 1 10 3 Z"),
            SceneryPart(SceneryPaint.Pane, SceneryStroke.Fine, "M 8.5 12 q 7.5 -4.5 15 0 v 4.5 h -15 z"),
            SceneryPart(SceneryPaint.Shade, SceneryStroke.Outline, "M 14 25 H 18 A 10 10 0 0 1 28 35 V 63 A 10 10 0 0 1 18 73 H 14 A 10 10 0 0 1 4 63 V 35 A 10 10 0 0 1 14 25 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 4 41 h 24 M 4 57 h 24"),
            SceneryPart(SceneryPaint.Shade, SceneryStroke.Fine, "M 12.5 33 A 3.5 3.5 0 1 0 19.5 33 A 3.5 3.5 0 1 0 12.5 33 Z"),
            SceneryPart(SceneryPaint.Shade, SceneryStroke.Fine, "M 12.5 49 A 3.5 3.5 0 1 0 19.5 49 A 3.5 3.5 0 1 0 12.5 49 Z"),
            SceneryPart(SceneryPaint.Shade, SceneryStroke.Fine, "M 12.5 65 A 3.5 3.5 0 1 0 19.5 65 A 3.5 3.5 0 1 0 12.5 65 Z"),
        ),
    ),
    BaggageCart(
        26f, 80f,
        listOf(
            SceneryPart(null, SceneryStroke.Outline, "M 13 18 v 40"),
            SceneryPart(SceneryPaint.Blue, SceneryStroke.Outline, "M 9 3 H 17 A 4 4 0 0 1 21 7 V 17 A 4 4 0 0 1 17 21 H 9 A 4 4 0 0 1 5 17 V 7 A 4 4 0 0 1 9 3 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Fine, "M 10.5 10 H 15.5 A 2 2 0 0 1 17.5 12 V 15 A 2 2 0 0 1 15.5 17 H 10.5 A 2 2 0 0 1 8.5 15 V 12 A 2 2 0 0 1 10.5 10 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 9 6.5 h 8"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 5.5 27 H 20.5 A 2.5 2.5 0 0 1 23 29.5 V 46.5 A 2.5 2.5 0 0 1 20.5 49 H 5.5 A 2.5 2.5 0 0 1 3 46.5 V 29.5 A 2.5 2.5 0 0 1 5.5 27 Z"),
            SceneryPart(SceneryPaint.Wood, SceneryStroke.Fine, "M 7.5 30 H 18.5 A 1.5 1.5 0 0 1 20 31.5 V 36 A 1.5 1.5 0 0 1 18.5 37.5 H 7.5 A 1.5 1.5 0 0 1 6 36 V 31.5 A 1.5 1.5 0 0 1 7.5 30 Z"),
            SceneryPart(SceneryPaint.Roof, SceneryStroke.Fine, "M 7.5 39 H 18.5 A 1.5 1.5 0 0 1 20 40.5 V 45 A 1.5 1.5 0 0 1 18.5 46.5 H 7.5 A 1.5 1.5 0 0 1 6 45 V 40.5 A 1.5 1.5 0 0 1 7.5 39 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 5.5 55 H 20.5 A 2.5 2.5 0 0 1 23 57.5 V 74.5 A 2.5 2.5 0 0 1 20.5 77 H 5.5 A 2.5 2.5 0 0 1 3 74.5 V 57.5 A 2.5 2.5 0 0 1 5.5 55 Z"),
            SceneryPart(SceneryPaint.Roof, SceneryStroke.Fine, "M 7.5 58 H 11.5 A 1.5 1.5 0 0 1 13 59.5 V 72.5 A 1.5 1.5 0 0 1 11.5 74 H 7.5 A 1.5 1.5 0 0 1 6 72.5 V 59.5 A 1.5 1.5 0 0 1 7.5 58 Z"),
            SceneryPart(SceneryPaint.Wood, SceneryStroke.Fine, "M 15.5 58 H 18.5 A 1.5 1.5 0 0 1 20 59.5 V 72.5 A 1.5 1.5 0 0 1 18.5 74 H 15.5 A 1.5 1.5 0 0 1 14 72.5 V 59.5 A 1.5 1.5 0 0 1 15.5 58 Z"),
        ),
    ),
    ParkedAircraft(
        72f, 72f,
        listOf(
            SceneryPart(SceneryPaint.Shade, SceneryStroke.Outline, "M 23 54 H 49 A 3 3 0 0 1 52 57 V 62 A 3 3 0 0 1 49 65 H 23 A 3 3 0 0 1 20 62 V 57 A 3 3 0 0 1 23 54 Z"),
            SceneryPart(SceneryPaint.Shade, SceneryStroke.Outline, "M 8.5 23 H 63.5 A 3.5 3.5 0 0 1 67 26.5 V 36.5 A 3.5 3.5 0 0 1 63.5 40 H 8.5 A 3.5 3.5 0 0 1 5 36.5 V 26.5 A 3.5 3.5 0 0 1 8.5 23 Z"),
            SceneryPart(SceneryPaint.Shade, SceneryStroke.Outline, "M 36 7 c 7 0 9.5 9 9.5 20 l -1.5 24 c -1 8 -4 13 -8 15 -4 -2 -7 -7 -8 -15 l -1.5 -24 c 0 -11 2.5 -20 9.5 -20 z"),
            SceneryPart(SceneryPaint.Glass, SceneryStroke.Fine, "M 29.5 23 q 6.5 -7 13 0 v -5 q -6.5 -6 -13 0 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 14 23 v 17 M 58 23 v 17 M 36 44 v 14"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 26 4 H 46 A 2 2 0 0 1 48 6 V 6 A 2 2 0 0 1 46 8 H 26 A 2 2 0 0 1 24 6 V 6 A 2 2 0 0 1 26 4 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 32.8 7.5 A 3.2 3.2 0 1 0 39.2 7.5 A 3.2 3.2 0 1 0 32.8 7.5 Z"),
        ),
    ),
    Palm(
        64f, 64f,
        listOf(
            SceneryPart(SceneryPaint.Bush, SceneryStroke.Outline, "M 32 32 Q 34.9 17.8 51.1 12.9 Q 46.2 29.1 32 32 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 34.3 29.7 L 47.3 16.7"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.Outline, "M 32 32 Q 46.2 34.9 51.1 51.1 Q 34.9 46.2 32 32 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 34.3 34.3 L 47.3 47.3"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.Outline, "M 32 32 Q 29.1 46.2 12.9 51.1 Q 17.8 34.9 32 32 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 29.7 34.3 L 16.7 47.3"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.Outline, "M 32 32 Q 17.8 29.1 12.9 12.9 Q 29.1 17.8 32 32 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 29.7 29.7 L 16.7 16.7"),
            SceneryPart(SceneryPaint.Leaf, SceneryStroke.Outline, "M 32 32 Q 25.5 17.4 36.2 2.3 Q 42.3 19.8 32 32 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 32.5 28.4 L 35.3 8.2"),
            SceneryPart(SceneryPaint.Leaf, SceneryStroke.Outline, "M 32 32 Q 46.6 25.5 61.7 36.2 Q 44.2 42.3 32 32 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 35.6 32.5 L 55.8 35.3"),
            SceneryPart(SceneryPaint.Leaf, SceneryStroke.Outline, "M 32 32 Q 38.5 46.6 27.8 61.7 Q 21.7 44.2 32 32 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 31.5 35.6 L 28.7 55.8"),
            SceneryPart(SceneryPaint.Leaf, SceneryStroke.Outline, "M 32 32 Q 17.4 38.5 2.3 27.8 Q 19.8 21.7 32 32 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 28.4 31.5 L 8.2 28.7"),
            SceneryPart(SceneryPaint.Wood, SceneryStroke.Fine, "M 26 30.5 A 3.5 3.5 0 1 0 33 30.5 A 3.5 3.5 0 1 0 26 30.5 Z"),
            SceneryPart(SceneryPaint.Wood, SceneryStroke.Fine, "M 31 30.5 A 3.5 3.5 0 1 0 38 30.5 A 3.5 3.5 0 1 0 31 30.5 Z"),
            SceneryPart(SceneryPaint.Wood, SceneryStroke.Fine, "M 28.5 35 A 3.5 3.5 0 1 0 35.5 35 A 3.5 3.5 0 1 0 28.5 35 Z"),
        ),
    ),
    Jetty(
        28f, 96f,
        listOf(
            SceneryPart(SceneryPaint.Wood, SceneryStroke.Outline, "M 7 3 H 21 A 2 2 0 0 1 23 5 V 91 A 2 2 0 0 1 21 93 H 7 A 2 2 0 0 1 5 91 V 5 A 2 2 0 0 1 7 3 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 5 13 h 18 M 5 23 h 18 M 5 33 h 18 M 5 43 h 18 M 5 53 h 18 M 5 63 h 18 M 5 73 h 18 M 5 83 h 18"),
            SceneryPart(SceneryPaint.Furrow, SceneryStroke.Fine, "M 2 8 A 3 3 0 1 0 8 8 A 3 3 0 1 0 2 8 Z"),
            SceneryPart(SceneryPaint.Furrow, SceneryStroke.Fine, "M 20 8 A 3 3 0 1 0 26 8 A 3 3 0 1 0 20 8 Z"),
            SceneryPart(SceneryPaint.Furrow, SceneryStroke.Fine, "M 2 38 A 3 3 0 1 0 8 38 A 3 3 0 1 0 2 38 Z"),
            SceneryPart(SceneryPaint.Furrow, SceneryStroke.Fine, "M 20 38 A 3 3 0 1 0 26 38 A 3 3 0 1 0 20 38 Z"),
            SceneryPart(SceneryPaint.Furrow, SceneryStroke.Fine, "M 2 68 A 3 3 0 1 0 8 68 A 3 3 0 1 0 2 68 Z"),
            SceneryPart(SceneryPaint.Furrow, SceneryStroke.Fine, "M 20 68 A 3 3 0 1 0 26 68 A 3 3 0 1 0 20 68 Z"),
        ),
    ),
    Sailboat(
        38f, 76f,
        listOf(
            SceneryPart(SceneryPaint.Wood, SceneryStroke.Outline, "M 14 3 q 10 14 10 34 v 28 q 0 8 -10 8 t -10 -8 V 37 q 0 -20 10 -34 z"),
            SceneryPart(SceneryPaint.Sand, SceneryStroke.Fine, "M 14 13 q 6 11 6 24 v 26 q 0 5 -6 5 t -6 -5 V 37 q 0 -13 6 -24 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 10 62 h 8"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 14 20 q 22 12 21 38 H 14 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 14 58 V 20"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 11.5 20 A 2.5 2.5 0 1 0 16.5 20 A 2.5 2.5 0 1 0 11.5 20 Z"),
        ),
    ),
    Yacht(
        32f, 92f,
        listOf(
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 16 3 q 12 16 12 40 v 36 q 0 10 -12 10 T 4 79 V 43 Q 4 19 16 3 z"),
            SceneryPart(SceneryPaint.Sand, SceneryStroke.Fine, "M 16 15 q 7 12 7 28 v 35 q 0 6 -7 6 t -7 -6 V 43 q 0 -16 7 -28 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 11 68 h 10 M 11 73 h 10 M 11 78 h 10"),
            SceneryPart(SceneryPaint.Blue, SceneryStroke.Outline, "M 13 36 H 19 A 4 4 0 0 1 23 40 V 58 A 4 4 0 0 1 19 62 H 13 A 4 4 0 0 1 9 58 V 40 A 4 4 0 0 1 13 36 Z"),
            SceneryPart(SceneryPaint.Pane, SceneryStroke.Fine, "M 10.5 35 q 5.5 -5 11 0 l -1 6 h -9 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 12 50 h 8 M 12 56 h 8"),
        ),
    ),
    ContainerVessel(
        48f, 176f,
        listOf(
            SceneryPart(SceneryPaint.Hull, SceneryStroke.Outline, "M 24 3 q 20 18 20 42 v 118 q 0 10 -10 10 H 14 q -10 0 -10 -10 V 45 Q 4 21 24 3 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 24 12 v 22"),
            SceneryPart(SceneryPaint.Roof, SceneryStroke.Fine, "M 10.5 44 H 21.5 A 1.5 1.5 0 0 1 23 45.5 V 58.5 A 1.5 1.5 0 0 1 21.5 60 H 10.5 A 1.5 1.5 0 0 1 9 58.5 V 45.5 A 1.5 1.5 0 0 1 10.5 44 Z"),
            SceneryPart(SceneryPaint.Sand, SceneryStroke.Fine, "M 26.5 44 H 37.5 A 1.5 1.5 0 0 1 39 45.5 V 58.5 A 1.5 1.5 0 0 1 37.5 60 H 26.5 A 1.5 1.5 0 0 1 25 58.5 V 45.5 A 1.5 1.5 0 0 1 26.5 44 Z"),
            SceneryPart(SceneryPaint.Sand, SceneryStroke.Fine, "M 10.5 63 H 21.5 A 1.5 1.5 0 0 1 23 64.5 V 77.5 A 1.5 1.5 0 0 1 21.5 79 H 10.5 A 1.5 1.5 0 0 1 9 77.5 V 64.5 A 1.5 1.5 0 0 1 10.5 63 Z"),
            SceneryPart(SceneryPaint.Roof, SceneryStroke.Fine, "M 26.5 63 H 37.5 A 1.5 1.5 0 0 1 39 64.5 V 77.5 A 1.5 1.5 0 0 1 37.5 79 H 26.5 A 1.5 1.5 0 0 1 25 77.5 V 64.5 A 1.5 1.5 0 0 1 26.5 63 Z"),
            SceneryPart(SceneryPaint.Roof, SceneryStroke.Fine, "M 10.5 82 H 21.5 A 1.5 1.5 0 0 1 23 83.5 V 96.5 A 1.5 1.5 0 0 1 21.5 98 H 10.5 A 1.5 1.5 0 0 1 9 96.5 V 83.5 A 1.5 1.5 0 0 1 10.5 82 Z"),
            SceneryPart(SceneryPaint.Sand, SceneryStroke.Fine, "M 26.5 82 H 37.5 A 1.5 1.5 0 0 1 39 83.5 V 96.5 A 1.5 1.5 0 0 1 37.5 98 H 26.5 A 1.5 1.5 0 0 1 25 96.5 V 83.5 A 1.5 1.5 0 0 1 26.5 82 Z"),
            SceneryPart(SceneryPaint.Sand, SceneryStroke.Fine, "M 10.5 101 H 21.5 A 1.5 1.5 0 0 1 23 102.5 V 115.5 A 1.5 1.5 0 0 1 21.5 117 H 10.5 A 1.5 1.5 0 0 1 9 115.5 V 102.5 A 1.5 1.5 0 0 1 10.5 101 Z"),
            SceneryPart(SceneryPaint.Roof, SceneryStroke.Fine, "M 26.5 101 H 37.5 A 1.5 1.5 0 0 1 39 102.5 V 115.5 A 1.5 1.5 0 0 1 37.5 117 H 26.5 A 1.5 1.5 0 0 1 25 115.5 V 102.5 A 1.5 1.5 0 0 1 26.5 101 Z"),
            SceneryPart(SceneryPaint.Roof, SceneryStroke.Fine, "M 10.5 120 H 21.5 A 1.5 1.5 0 0 1 23 121.5 V 134.5 A 1.5 1.5 0 0 1 21.5 136 H 10.5 A 1.5 1.5 0 0 1 9 134.5 V 121.5 A 1.5 1.5 0 0 1 10.5 120 Z"),
            SceneryPart(SceneryPaint.Sand, SceneryStroke.Fine, "M 26.5 120 H 37.5 A 1.5 1.5 0 0 1 39 121.5 V 134.5 A 1.5 1.5 0 0 1 37.5 136 H 26.5 A 1.5 1.5 0 0 1 25 134.5 V 121.5 A 1.5 1.5 0 0 1 26.5 120 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 12 142 H 36 A 3 3 0 0 1 39 145 V 161 A 3 3 0 0 1 36 164 H 12 A 3 3 0 0 1 9 161 V 145 A 3 3 0 0 1 12 142 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 12 147.5 h 24 M 18 156 h 12"),
        ),
    ),
    Wake(
        48f, 64f,
        listOf(
            SceneryPart(null, SceneryStroke.Outline, "M 19 4 Q 12 30 4 60 M 29 4 q 7 26 15 56", SceneryPaint.Foam),
            SceneryPart(null, SceneryStroke.Fine, "M 24 10 v 10 M 17 34 q 7 5 14 0 M 13 50 q 11 6 22 0", SceneryPaint.Foam),
            SceneryPart(SceneryPaint.Foam, SceneryStroke.None, "M 21.5 26 A 2.5 2.5 0 1 0 26.5 26 A 2.5 2.5 0 1 0 21.5 26 Z"),
            SceneryPart(SceneryPaint.Foam, SceneryStroke.None, "M 15.5 43 A 2.5 2.5 0 1 0 20.5 43 A 2.5 2.5 0 1 0 15.5 43 Z"),
            SceneryPart(SceneryPaint.Foam, SceneryStroke.None, "M 27.5 43 A 2.5 2.5 0 1 0 32.5 43 A 2.5 2.5 0 1 0 27.5 43 Z"),
        ),
    ),
    Wave(
        44f, 20f,
        listOf(
            SceneryPart(null, SceneryStroke.Outline, "M 3 8 q 6.5 -9 13 0 t 13 0 12 0", SceneryPaint.Foam),
            SceneryPart(null, SceneryStroke.Fine, "M 12 17 q 5 -6 10 0 t 10 0", SceneryPaint.Foam),
        ),
    ),
    Acacia(
        92f, 64f,
        listOf(
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 4 35 A 13 13 0 1 0 30 35 A 13 13 0 1 0 4 35 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 15 28 A 18 18 0 1 0 51 28 A 18 18 0 1 0 15 28 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 32 26 A 20 20 0 1 0 72 26 A 20 20 0 1 0 32 26 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 54 30 A 16 16 0 1 0 86 30 A 16 16 0 1 0 54 30 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 69 38 A 10 10 0 1 0 89 38 A 10 10 0 1 0 69 38 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 29 42 A 16 16 0 1 0 61 42 A 16 16 0 1 0 29 42 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 16 44 A 11 11 0 1 0 38 44 A 11 11 0 1 0 16 44 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 52 44 A 12 12 0 1 0 76 44 A 12 12 0 1 0 52 44 Z"),
            SceneryPart(SceneryPaint.Deep, SceneryStroke.None, "M 5.5 35 A 11.5 11.5 0 1 0 28.5 35 A 11.5 11.5 0 1 0 5.5 35 Z"),
            SceneryPart(SceneryPaint.Deep, SceneryStroke.None, "M 16.5 28 A 16.5 16.5 0 1 0 49.5 28 A 16.5 16.5 0 1 0 16.5 28 Z"),
            SceneryPart(SceneryPaint.Deep, SceneryStroke.None, "M 33.5 26 A 18.5 18.5 0 1 0 70.5 26 A 18.5 18.5 0 1 0 33.5 26 Z"),
            SceneryPart(SceneryPaint.Deep, SceneryStroke.None, "M 55.5 30 A 14.5 14.5 0 1 0 84.5 30 A 14.5 14.5 0 1 0 55.5 30 Z"),
            SceneryPart(SceneryPaint.Deep, SceneryStroke.None, "M 70.5 38 A 8.5 8.5 0 1 0 87.5 38 A 8.5 8.5 0 1 0 70.5 38 Z"),
            SceneryPart(SceneryPaint.Deep, SceneryStroke.None, "M 30.5 42 A 14.5 14.5 0 1 0 59.5 42 A 14.5 14.5 0 1 0 30.5 42 Z"),
            SceneryPart(SceneryPaint.Deep, SceneryStroke.None, "M 17.5 44 A 9.5 9.5 0 1 0 36.5 44 A 9.5 9.5 0 1 0 17.5 44 Z"),
            SceneryPart(SceneryPaint.Deep, SceneryStroke.None, "M 53.5 44 A 10.5 10.5 0 1 0 74.5 44 A 10.5 10.5 0 1 0 53.5 44 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 19 27 A 7 7 0 1 0 33 27 A 7 7 0 1 0 19 27 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 47 20 A 8 8 0 1 0 63 20 A 8 8 0 1 0 47 20 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 66 33 A 6 6 0 1 0 78 33 A 6 6 0 1 0 66 33 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 30 46 A 6 6 0 1 0 42 46 A 6 6 0 1 0 30 46 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 54 45 A 6 6 0 1 0 66 45 A 6 6 0 1 0 54 45 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 46 34 L 30 30 M 46 34 l -7 9 M 46 34 l 5 -11 M 46 34 l 18 -2 M 46 34 l 11 8"),
            SceneryPart(SceneryPaint.Wood, SceneryStroke.Fine, "M 41.5 34 A 4.5 4.5 0 1 0 50.5 34 A 4.5 4.5 0 1 0 41.5 34 Z"),
        ),
    ),
    Bush(
        40f, 36f,
        listOf(
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 3 21 A 10 10 0 1 0 23 21 A 10 10 0 1 0 3 21 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 13 13 A 10 10 0 1 0 33 13 A 10 10 0 1 0 13 13 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.Outline, "M 19 24 A 9 9 0 1 0 37 24 A 9 9 0 1 0 19 24 Z"),
            SceneryPart(SceneryPaint.Leaf, SceneryStroke.None, "M 4.5 21 A 8.5 8.5 0 1 0 21.5 21 A 8.5 8.5 0 1 0 4.5 21 Z"),
            SceneryPart(SceneryPaint.Leaf, SceneryStroke.None, "M 14.5 13 A 8.5 8.5 0 1 0 31.5 13 A 8.5 8.5 0 1 0 14.5 13 Z"),
            SceneryPart(SceneryPaint.Leaf, SceneryStroke.None, "M 20.5 24 A 7.5 7.5 0 1 0 35.5 24 A 7.5 7.5 0 1 0 20.5 24 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 19 12 A 3 3 0 1 0 25 12 A 3 3 0 1 0 19 12 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 9 20 A 3 3 0 1 0 15 20 A 3 3 0 1 0 9 20 Z"),
            SceneryPart(SceneryPaint.Bush, SceneryStroke.None, "M 26 24 A 3 3 0 1 0 32 24 A 3 3 0 1 0 26 24 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 15 15 q 2.5 -3 5 -0.5 M 19 24 q 2.5 -3 5 -0.5"),
        ),
    ),
    Rock(
        48f, 36f,
        listOf(
            SceneryPart(SceneryPaint.Stone, SceneryStroke.Outline, "M 6 22 q -3 -11 8 -15 l 10 -3 q 11 -1 13 9 3 11 -8 14 l -12 2 q -9 1 -11 -7 z"),
            SceneryPart(null, SceneryStroke.Fine, "M 15 11 l 6 6 -2 7 M 27 8 l 2 7"),
            SceneryPart(SceneryPaint.Stone, SceneryStroke.Outline, "M 33 28 q 0 -6 6 -6 t 6 5 -6 6 -6 -5 z"),
        ),
    ),
    Zebra(
        32f, 68f,
        listOf(
            SceneryPart(null, SceneryStroke.Outline, "M 16 59 q 0 4 -2 6"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 11.7 65 A 1.8 1.8 0 1 0 15.3 65 A 1.8 1.8 0 1 0 11.7 65 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 4.5 32 H 8.5 A 3 3 0 0 1 11.5 35 V 35 A 3 3 0 0 1 8.5 38 H 4.5 A 3 3 0 0 1 1.5 35 V 35 A 3 3 0 0 1 4.5 32 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 23.5 32 H 27.5 A 3 3 0 0 1 30.5 35 V 35 A 3 3 0 0 1 27.5 38 H 23.5 A 3 3 0 0 1 20.5 35 V 35 A 3 3 0 0 1 23.5 32 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 4.5 50 H 8.5 A 3 3 0 0 1 11.5 53 V 53 A 3 3 0 0 1 8.5 56 H 4.5 A 3 3 0 0 1 1.5 53 V 53 A 3 3 0 0 1 4.5 50 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 23.5 50 H 27.5 A 3 3 0 0 1 30.5 53 V 53 A 3 3 0 0 1 27.5 56 H 23.5 A 3 3 0 0 1 20.5 53 V 53 A 3 3 0 0 1 23.5 50 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 6.5 44 A 9.5 16 0 1 0 25.5 44 A 9.5 16 0 1 0 6.5 44 Z"),
            SceneryPart(null, SceneryStroke.Outline, "M 9.5 36 q 6.5 3 13 0 M 7.5 42 q 8.5 3 17 0 M 7.5 48 q 8.5 3 17 0 M 9.5 54 q 6.5 3 13 0"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 15 14 H 17 A 4 4 0 0 1 21 18 V 28 A 4 4 0 0 1 17 32 H 15 A 4 4 0 0 1 11 28 V 18 A 4 4 0 0 1 15 14 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Fine, "M 7.8 14 A 2.2 3.5 0 1 0 12.2 14 A 2.2 3.5 0 1 0 7.8 14 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Fine, "M 19.8 14 A 2.2 3.5 0 1 0 24.2 14 A 2.2 3.5 0 1 0 19.8 14 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Outline, "M 10.5 11 A 5.5 9 0 1 0 21.5 11 A 5.5 9 0 1 0 10.5 11 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 16 12 H 16 A 1.5 1.5 0 0 1 17.5 13.5 V 28.5 A 1.5 1.5 0 0 1 16 30 H 16 A 1.5 1.5 0 0 1 14.5 28.5 V 13.5 A 1.5 1.5 0 0 1 16 12 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 12.2 4.5 A 3.8 2.8 0 1 0 19.8 4.5 A 3.8 2.8 0 1 0 12.2 4.5 Z"),
        ),
    ),
    Elephant(
        64f, 84f,
        listOf(
            SceneryPart(null, SceneryStroke.Outline, "M 32 74 q 0 5 3 7"),
            SceneryPart(SceneryPaint.Stone, SceneryStroke.Outline, "M 9 44 A 6 6 0 1 0 21 44 A 6 6 0 1 0 9 44 Z"),
            SceneryPart(SceneryPaint.Stone, SceneryStroke.Outline, "M 43 44 A 6 6 0 1 0 55 44 A 6 6 0 1 0 43 44 Z"),
            SceneryPart(SceneryPaint.Stone, SceneryStroke.Outline, "M 10 64 A 6 6 0 1 0 22 64 A 6 6 0 1 0 10 64 Z"),
            SceneryPart(SceneryPaint.Stone, SceneryStroke.Outline, "M 42 64 A 6 6 0 1 0 54 64 A 6 6 0 1 0 42 64 Z"),
            SceneryPart(SceneryPaint.Stone, SceneryStroke.Outline, "M 14 54 A 18 23 0 1 0 50 54 A 18 23 0 1 0 14 54 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 22 60 q 10 4 20 0 M 24 68 q 8 3 16 0"),
            SceneryPart(SceneryPaint.Stone, SceneryStroke.Outline, "M 3 30 A 11 14 0 1 0 25 30 A 11 14 0 1 0 3 30 Z"),
            SceneryPart(SceneryPaint.Stone, SceneryStroke.Outline, "M 39 30 A 11 14 0 1 0 61 30 A 11 14 0 1 0 39 30 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 10 24 q 5 6 2 13 M 54 24 q -5 6 -2 13"),
            SceneryPart(SceneryPaint.Stone, SceneryStroke.Outline, "M 32 3 H 32 A 4 4 0 0 1 36 7 V 21 A 4 4 0 0 1 32 25 H 32 A 4 4 0 0 1 28 21 V 7 A 4 4 0 0 1 32 3 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Fine, "M 22.3 14 A 2.2 6 0 1 0 26.7 14 A 2.2 6 0 1 0 22.3 14 Z"),
            SceneryPart(SceneryPaint.White, SceneryStroke.Fine, "M 37.3 14 A 2.2 6 0 1 0 41.7 14 A 2.2 6 0 1 0 37.3 14 Z"),
            SceneryPart(SceneryPaint.Stone, SceneryStroke.Outline, "M 19 28 A 13 13 0 1 0 45 28 A 13 13 0 1 0 19 28 Z"),
            SceneryPart(null, SceneryStroke.Fine, "M 29 8 h 6 M 29 12.5 h 6 M 29 17 h 6"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 24.5 25 A 1.5 1.5 0 1 0 27.5 25 A 1.5 1.5 0 1 0 24.5 25 Z"),
            SceneryPart(SceneryPaint.Dark, SceneryStroke.None, "M 36.5 25 A 1.5 1.5 0 1 0 39.5 25 A 1.5 1.5 0 1 0 36.5 25 Z"),
        ),
    ),
}
