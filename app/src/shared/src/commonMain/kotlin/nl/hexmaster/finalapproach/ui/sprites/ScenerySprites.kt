// Generated from the design system scenery sprites (docs/design-system/bundle.js: WORLD, DETAILS and the cloud). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.sprites

import androidx.compose.ui.graphics.Color
import nl.hexmaster.finalapproach.ui.theme.FaColors

/** The colour tokens scenery is painted with. Colours come from the theme at draw time. */
internal enum class SceneryPaint { Asphalt300, Asphalt500, Asphalt700, Asphalt900, Beacon, Cherry200, Cherry500, Cloud, CloudShade, Concrete300, Concrete700, Denim300, Denim500, Foam, Glass, Glass700, GlassShine, Grass300, Grass700, Hide100, Hide300, Hide900, Leaf300, Leaf500, Leaf700, Leaf900, Muzzle, Outline, Pine300, Pine500, Pine700, Pine900, Roof300, Roof500, Roof700, Roof900, Soil500, Soil700, Stone300, Stone500, Stone700, Straw300, Straw500, Straw700, Tangerine500, Tangerine900, Tin100, Tin300, Tin500, Tin700, Tin900, Wall100, Wall300, Wall500, Water700, Window, Wood300, Wood500, Wood700, Wood900 }

/** The theme colour of a scenery paint. */
internal fun FaColors.scenery(paint: SceneryPaint): Color = when (paint) {
    SceneryPaint.Asphalt300 -> asphalt300
    SceneryPaint.Asphalt500 -> asphalt500
    SceneryPaint.Asphalt700 -> asphalt700
    SceneryPaint.Asphalt900 -> asphalt900
    SceneryPaint.Beacon -> beacon
    SceneryPaint.Cherry200 -> cherry200
    SceneryPaint.Cherry500 -> cherry500
    SceneryPaint.Cloud -> cloud
    SceneryPaint.CloudShade -> cloudShade
    SceneryPaint.Concrete300 -> concrete300
    SceneryPaint.Concrete700 -> concrete700
    SceneryPaint.Denim300 -> denim300
    SceneryPaint.Denim500 -> denim500
    SceneryPaint.Foam -> foam
    SceneryPaint.Glass -> glass
    SceneryPaint.Glass700 -> glass700
    SceneryPaint.GlassShine -> glassShine
    SceneryPaint.Grass300 -> grass300
    SceneryPaint.Grass700 -> grass700
    SceneryPaint.Hide100 -> hide100
    SceneryPaint.Hide300 -> hide300
    SceneryPaint.Hide900 -> hide900
    SceneryPaint.Leaf300 -> leaf300
    SceneryPaint.Leaf500 -> leaf500
    SceneryPaint.Leaf700 -> leaf700
    SceneryPaint.Leaf900 -> leaf900
    SceneryPaint.Muzzle -> muzzle
    SceneryPaint.Outline -> outline
    SceneryPaint.Pine300 -> pine300
    SceneryPaint.Pine500 -> pine500
    SceneryPaint.Pine700 -> pine700
    SceneryPaint.Pine900 -> pine900
    SceneryPaint.Roof300 -> roof300
    SceneryPaint.Roof500 -> roof500
    SceneryPaint.Roof700 -> roof700
    SceneryPaint.Roof900 -> roof900
    SceneryPaint.Soil500 -> soil500
    SceneryPaint.Soil700 -> soil700
    SceneryPaint.Stone300 -> stone300
    SceneryPaint.Stone500 -> stone500
    SceneryPaint.Stone700 -> stone700
    SceneryPaint.Straw300 -> straw300
    SceneryPaint.Straw500 -> straw500
    SceneryPaint.Straw700 -> straw700
    SceneryPaint.Tangerine500 -> tangerine500
    SceneryPaint.Tangerine900 -> tangerine900
    SceneryPaint.Tin100 -> tin100
    SceneryPaint.Tin300 -> tin300
    SceneryPaint.Tin500 -> tin500
    SceneryPaint.Tin700 -> tin700
    SceneryPaint.Tin900 -> tin900
    SceneryPaint.Wall100 -> wall100
    SceneryPaint.Wall300 -> wall300
    SceneryPaint.Wall500 -> wall500
    SceneryPaint.Water700 -> water700
    SceneryPaint.Window -> window
    SceneryPaint.Wood300 -> wood300
    SceneryPaint.Wood500 -> wood500
    SceneryPaint.Wood700 -> wood700
    SceneryPaint.Wood900 -> wood900
}

/** A node of a sprite: one shape, or a group that clips and transforms its children. */
internal sealed class SceneryNode

/**
 * One shape, as SVG path data. A null [fill] is a stroke only, a null [stroke] a fill only.
 * [strokeWidth] is in the sprite's own units, so it grows and shrinks with the sprite. A
 * [nonScalingStroke] keeps its width when the shape's own [matrix] squashes it. [matrix] is
 * `[a, b, c, d, e, f]` as in SVG.
 */
internal class SceneryShape(
    val fill: SceneryPaint?,
    val stroke: SceneryPaint?,
    val strokeWidth: Float,
    val pathData: String,
    val opacity: Float = 1f,
    val nonScalingStroke: Boolean = false,
    val matrix: List<Float>? = null,
) : SceneryNode()

/** Children drawn inside an optional clip (path data in the group's own space) and matrix. */
internal class SceneryGroup(val clip: String?, val matrix: List<Float>?, val children: List<SceneryNode>) : SceneryNode()

/** The silhouette of a cast shadow, filled in one colour; a [strokeWidth] above zero makes it a line of that width. */
internal class ShadowShape(val pathData: String, val strokeWidth: Float = 0f, val matrix: List<Float>? = null)

/** What a sprite is drawn from. */
internal class SceneryArt(val nodes: List<SceneryNode>)

/** How high a sprite stands (world.md, W5). Flat things lie on the ground and cast no shadow. */
internal enum class SceneryTier { Flat, Small, Medium, Large }

/**
 * The scenery sprites of the design system, each drawn around its own origin in world units.
 * [width] and [height] are the size of its picture including the shadow; [bodyWidth] and
 * [bodyHeight] are the size of its own shapes, strokes and shadow left out, which is the size a
 * level gives it as a footprint (the renderer fits these bounds into the footprint); [rotates] is false
 * for everything that stands (buildings, trees, the silo, the windsock). The shadow is
 * [shadow] thrown by ([shadowX], [shadowY]) world units, down and to the right.
 */
internal enum class ScenerySprite(
    val label: String,
    val tier: SceneryTier,
    val rotates: Boolean,
    val width: Float,
    val height: Float,
    val bodyWidth: Float,
    val bodyHeight: Float,
    val shadowX: Float,
    val shadowY: Float,
    val shadow: List<ShadowShape>,
    artOf: () -> SceneryArt,
) {
    Tree(
        "Tree", SceneryTier.Large, false, 86f, 86f, 60.9f, 67.3f, 10.9f, 13f,
        listOf(
            ShadowShape("M -26 -2 A 26 24.4 0 1 0 26 -2 A 26 24.4 0 1 0 -26 -2 Z"),
        ),
        ::artTree,
    ),
    TreeSmall(
        "Small tree", SceneryTier.Large, false, 76f, 76f, 48.4f, 54.7f, 8.8f, 10.5f,
        listOf(
            ShadowShape("M -21 -2 A 21 19.7 0 1 0 21 -2 A 21 19.7 0 1 0 -21 -2 Z"),
        ),
        ::artTreeSmall,
    ),
    TreeOak(
        "Old oak", SceneryTier.Large, false, 94f, 94f, 71.9f, 75.3f, 12.6f, 15f,
        listOf(
            ShadowShape("M -30 -2 A 30 28.2 0 1 0 30 -2 A 30 28.2 0 1 0 -30 -2 Z"),
        ),
        ::artTreeOak,
    ),
    Pine(
        "Pine", SceneryTier.Large, false, 82f, 82f, 44.8f, 45.3f, 11.5f, 13.8f,
        listOf(
            ShadowShape("M 21.4 8.3 L 12.8 11.2 L 11.1 20.2 L 2.6 16.8 L -4.5 22.6 L -8.8 14.6 L -17.9 14.4 L -16.1 5.5 L -23 -0.5 L -15.9 -6.2 L -17.3 -15.1 L -8.2 -14.9 L -3.5 -22.7 L 3.3 -16.7 L 11.9 -19.7 L 13.3 -10.7 L 21.8 -7.4 L 17 0.4 Z"),
        ),
        ::artPine,
    ),
    PineSmall(
        "Small pine", SceneryTier.Large, false, 72f, 72f, 35.2f, 35.4f, 9f, 10.8f,
        listOf(
            ShadowShape("M 13.3 12.1 L 6.2 11.8 L 2.4 17.8 L -2.9 13 L -9.6 15.2 L -10.6 8.1 L -17.2 5.4 L -13.3 -0.6 L -16.6 -6.9 L -9.8 -9 L -8.3 -16 L -1.8 -13.2 L 3.9 -17.6 L 7.1 -11.2 L 14.3 -11 L 12.7 -4 L 18 0.8 L 12.3 5.1 Z"),
        ),
        ::artPineSmall,
    ),
    Bush(
        "Bush", SceneryTier.Small, true, 56f, 56f, 27.3f, 27f, 3f, 4f,
        listOf(
            ShadowShape("M -11 0 A 11 11 0 1 0 11 0 A 11 11 0 1 0 -11 0 Z"),
        ),
        ::artBush,
    ),
    Hedge(
        "Hedge (one piece of a row)", SceneryTier.Small, true, 44f, 44f, 19f, 19f, 4f, 5f,
        listOf(
            ShadowShape("M -9.5 0 A 9.5 9.5 0 1 0 9.5 0 A 9.5 9.5 0 1 0 -9.5 0 Z"),
        ),
        ::artHedge,
    ),
    Acacia(
        "Acacia", SceneryTier.Large, false, 100f, 96f, 68.6f, 70.9f, 14f, 16f,
        listOf(
            ShadowShape("M -31 -6 A 31 23 0 1 0 31 -6 A 31 23 0 1 0 -31 -6 Z"),
        ),
        ::artAcacia,
    ),
    Palm(
        "Palm", SceneryTier.Large, false, 96f, 96f, 49.5f, 51.4f, 13f, 15f,
        listOf(
            ShadowShape("M 0 -14 Q 9 -22.5 4.2 -33.4 Q -4.8 -24.9 0 -14 Z"),
            ShadowShape("M 0 -14 Q 15.2 -13.5 23.4 -25.1 Q 8.2 -25.6 0 -14 Z"),
            ShadowShape("M 0 -14 Q 9.8 -4.7 23.2 -8.9 Q 13.4 -18.2 0 -14 Z"),
            ShadowShape("M 0 -14 Q -0.6 -1 11.4 6.1 Q 12 -6.9 0 -14 Z"),
            ShadowShape("M 0 -14 Q -11.4 -8 -10.1 3.8 Q 1.3 -2.1 0 -14 Z"),
            ShadowShape("M 0 -14 Q -14.9 -17.9 -26.1 -8.3 Q -11.2 -4.4 0 -14 Z"),
            ShadowShape("M 0 -14 Q -5.8 -25.4 -19.7 -25.3 Q -13.8 -13.9 0 -14 Z"),
        ),
        ::artPalm,
    ),
    Rock(
        "Rock", SceneryTier.Small, true, 64f, 56f, 31.5f, 25.5f, 3f, 4f,
        listOf(
            ShadowShape("M -13 2 L -9 -9 L 3 -12 L 12 -5 L 14 6 L 5 11 L -7 10 Z"),
            ShadowShape("M 9 9 A 5 5 0 1 0 19 9 A 5 5 0 1 0 9 9 Z"),
        ),
        ::artRock,
    ),
    HayBale(
        "Hay bale", SceneryTier.Small, true, 48f, 48f, 20f, 20.5f, 3f, 4f,
        listOf(
            ShadowShape("M -9 0 A 9 9 0 1 0 9 0 A 9 9 0 1 0 -9 0 Z"),
        ),
        ::artHayBale,
    ),
    Fence(
        "Fence (one piece of a row)", SceneryTier.Small, true, 40f, 40f, 22f, 6f, 2.5f, 3.5f,
        listOf(
            ShadowShape("M -8 0 h 16", strokeWidth = 3f),
        ),
        ::artFence,
    ),
    Farmhouse(
        "Farmhouse", SceneryTier.Medium, false, 150f, 126f, 92f, 73f, 0f, 0f,
        listOf(
            ShadowShape("M 46 -38 L 58 -24 L 58 44 L -34 44 L -46 30 Z"),
        ),
        ::artFarmhouse,
    ),
    Barn(
        "Barn", SceneryTier.Medium, false, 138f, 126f, 80f, 74f, 0f, 0f,
        listOf(
            ShadowShape("M 40 -44 L 54 -28 L 54 46 L -26 46 L -40 30 Z"),
        ),
        ::artBarn,
    ),
    Silo(
        "Silo", SceneryTier.Large, false, 76f, 76f, 31f, 31.5f, 13f, 15f,
        listOf(
            ShadowShape("M -14 0 A 14 14 0 1 0 14 0 A 14 14 0 1 0 -14 0 Z"),
        ),
        ::artSilo,
    ),
    Tower(
        "Control tower", SceneryTier.Large, false, 132f, 150f, 48f, 99.3f, 0f, 0f,
        listOf(
            ShadowShape("M 24 -6 L 36 8 L 36 56 L -12 56 L -24 42 Z"),
            ShadowShape("M 50.3 8.4 L 38.4 20.3 L 21.6 20.3 L 9.7 8.4 L 9.7 -8.4 L 21.6 -20.3 L 38.4 -20.3 L 50.3 -8.4 Z"),
            ShadowShape("M 9 -30 L 39 0 L 21 18 L -9 -12 Z"),
        ),
        ::artTower,
    ),
    Terminal(
        "Terminal", SceneryTier.Medium, false, 206f, 126f, 164f, 76f, 0f, 0f,
        listOf(
            ShadowShape("M 76 -34 L 88 -20 L 88 50 L -64 50 L -76 36 Z"),
        ),
        ::artTerminal,
    ),
    Hangar(
        "Hangar", SceneryTier.Medium, false, 166f, 140f, 112f, 84f, 0f, 0f,
        listOf(
            ShadowShape("M 56 -40 L 69 -25 L 69 59 L -43 59 L -56 44 Z"),
        ),
        ::artHangar,
    ),
    Windsock(
        "Windsock", SceneryTier.Large, false, 84f, 76f, 38f, 35.8f, 9f, 11f,
        listOf(
            ShadowShape("M 0 0 v -26", strokeWidth = 3f),
            ShadowShape("M 0 -31 L 34 -27.5 L 34 -22.5 L 0 -21 Z", matrix = listOf(0.97f, 0.242f, -0.242f, 0.97f, -6.29f, -0.772f)),
        ),
        ::artWindsock,
    ),
    Car(
        "Car", SceneryTier.Small, true, 56f, 72f, 25f, 38f, 3f, 4f,
        listOf(
            ShadowShape("M -3 -19 H 3 A 7 7 0 0 1 10 -12 V 12 A 7 7 0 0 1 3 19 H -3 A 7 7 0 0 1 -10 12 V -12 A 7 7 0 0 1 -3 -19 Z"),
        ),
        ::artCar,
    ),
    FuelTruck(
        "Fuel truck", SceneryTier.Small, true, 60f, 92f, 26f, 56f, 4f, 5f,
        listOf(
            ShadowShape("M -5 -28 H 5 A 6 6 0 0 1 11 -22 V 22 A 6 6 0 0 1 5 28 H -5 A 6 6 0 0 1 -11 22 V -22 A 6 6 0 0 1 -5 -28 Z"),
        ),
        ::artFuelTruck,
    ),
    BaggageCart(
        "Baggage cart", SceneryTier.Small, true, 52f, 96f, 21f, 68f, 3f, 4f,
        listOf(
            ShadowShape("M -4 -34 H 4 A 4 4 0 0 1 8 -30 V -22 A 4 4 0 0 1 4 -18 H -4 A 4 4 0 0 1 -8 -22 V -30 A 4 4 0 0 1 -4 -34 Z"),
            ShadowShape("M -6 -12 H 6 A 2 2 0 0 1 8 -10 V 6 A 2 2 0 0 1 6 8 H -6 A 2 2 0 0 1 -8 6 V -10 A 2 2 0 0 1 -6 -12 Z"),
            ShadowShape("M -6 14 H 6 A 2 2 0 0 1 8 16 V 32 A 2 2 0 0 1 6 34 H -6 A 2 2 0 0 1 -8 32 V 16 A 2 2 0 0 1 -6 14 Z"),
        ),
        ::artBaggageCart,
    ),
    ParkedAircraft(
        "Parked aircraft", SceneryTier.Small, true, 92f, 88f, 60f, 60f, 4f, 5f,
        listOf(
            ShadowShape("M -24 -12 H 24 A 6 6 0 0 1 30 -6 V -5 A 6 6 0 0 1 24 1 H -24 A 6 6 0 0 1 -30 -5 V -6 A 6 6 0 0 1 -24 -12 Z"),
            ShadowShape("M 0 -29 c 5 0 6 10 6 20 l -2 32 h -8 l -2 -32 c 0 -10 1 -20 6 -20 z"),
            ShadowShape("M -8 22 H 8 A 4 4 0 0 1 12 26 V 26 A 4 4 0 0 1 8 30 H -8 A 4 4 0 0 1 -12 26 V 26 A 4 4 0 0 1 -8 22 Z"),
        ),
        ::artParkedAircraft,
    ),
    Cow(
        "Cow", SceneryTier.Small, true, 64f, 86f, 24.9f, 56.8f, 4f, 5f,
        listOf(
            ShadowShape("M -11.5 2 A 11.5 19 0 1 0 11.5 2 A 11.5 19 0 1 0 -11.5 2 Z"),
            ShadowShape("M -7 -19 A 7 8 0 1 0 7 -19 A 7 8 0 1 0 -7 -19 Z"),
        ),
        ::artCow,
    ),
    Zebra(
        "Zebra", SceneryTier.Small, true, 64f, 90f, 24.9f, 61.2f, 4f, 5f,
        listOf(
            ShadowShape("M -11.5 2 A 11.5 19 0 1 0 11.5 2 A 11.5 19 0 1 0 -11.5 2 Z"),
            ShadowShape("M -7 -19 A 7 8 0 1 0 7 -19 A 7 8 0 1 0 -7 -19 Z"),
        ),
        ::artZebra,
    ),
    Elephant(
        "Elephant", SceneryTier.Small, true, 104f, 132f, 63.2f, 99.9f, 6f, 7f,
        listOf(
            ShadowShape("M -19 4 A 19 28 0 1 0 19 4 A 19 28 0 1 0 -19 4 Z"),
            ShadowShape("M -32 -24 A 13 15 0 1 0 -6 -24 A 13 15 0 1 0 -32 -24 Z"),
            ShadowShape("M 6 -24 A 13 15 0 1 0 32 -24 A 13 15 0 1 0 6 -24 Z"),
            ShadowShape("M -11 -30 A 11 13 0 1 0 11 -30 A 11 13 0 1 0 -11 -30 Z"),
        ),
        ::artElephant,
    ),
    Farmer(
        "Farmer", SceneryTier.Small, true, 60f, 64f, 35.3f, 32f, 3f, 4f,
        listOf(
            ShadowShape("M -12 2 A 12 7 0 1 0 12 2 A 12 7 0 1 0 -12 2 Z"),
            ShadowShape("M -8.5 -1 A 8.5 8.5 0 1 0 8.5 -1 A 8.5 8.5 0 1 0 -8.5 -1 Z"),
        ),
        ::artFarmer,
    ),
    BirdUp(
        "Bird, wings out", SceneryTier.Small, true, 48f, 40f, 30f, 19.2f, 7f, 9f,
        listOf(
            ShadowShape("M 0 -2 q -8 -6 -15 -2 q 4 1 6 5 q 5 -1 9 2 q 4 -3 9 -2 q 2 -4 6 -5 q -7 -4 -15 2 z"),
            ShadowShape("M -3.2 0 A 3.2 6.5 0 1 0 3.2 0 A 3.2 6.5 0 1 0 -3.2 0 Z"),
        ),
        ::artBirdUp,
    ),
    BirdDown(
        "Bird, wings in", SceneryTier.Small, true, 48f, 40f, 18f, 19.2f, 7f, 9f,
        listOf(
            ShadowShape("M 0 -2 q -6 -1 -9 5 q 3 0 5 3 q 2 -3 4 -2 q 2 -1 4 2 q 2 -3 5 -3 q -3 -6 -9 -5 z"),
            ShadowShape("M -3.2 0 A 3.2 6.5 0 1 0 3.2 0 A 3.2 6.5 0 1 0 -3.2 0 Z"),
        ),
        ::artBirdDown,
    ),
    Sailboat(
        "Sailboat", SceneryTier.Small, true, 60f, 92f, 22f, 62f, 3f, 4f,
        listOf(
            ShadowShape("M 0 -30 C 8 -22 10 -8 10 6 V 24 q 0 4 -4 4 H -6 q -4 0 -4 -4 V 6 C -10 -8 -8 -22 0 -30 Z"),
        ),
        ::artSailboat,
    ),
    Yacht(
        "Yacht", SceneryTier.Small, true, 72f, 110f, 30f, 80f, 4f, 5f,
        listOf(
            ShadowShape("M 0 -40 C 11 -30 14 -12 14 6 V 30 q 0 6 -6 6 H -8 q -6 0 -6 -6 V 6 C -14 -12 -11 -30 0 -40 Z"),
        ),
        ::artYacht,
    ),
    ContainerVessel(
        "Container vessel", SceneryTier.Small, true, 96f, 176f, 46f, 152f, 6f, 7f,
        listOf(
            ShadowShape("M -22 -70 q 22 -16 44 0 V 62 q 0 8 -8 8 H -14 q -8 0 -8 -8 Z"),
        ),
        ::artContainerVessel,
    ),
    Jetty(
        "Jetty", SceneryTier.Small, true, 60f, 120f, 24f, 88f, 3f, 4f,
        listOf(
            ShadowShape("M -7 -44 H 7 A 2 2 0 0 1 9 -42 V 42 A 2 2 0 0 1 7 44 H -7 A 2 2 0 0 1 -9 42 V -42 A 2 2 0 0 1 -7 -44 Z"),
        ),
        ::artJetty,
    ),
    Wave(
        "Wave", SceneryTier.Flat, true, 48f, 24f, 28f, 6f, 0f, 0f,
        listOf(

        ),
        ::artWave,
    ),
    Wake(
        "Wake", SceneryTier.Flat, true, 56f, 64f, 28f, 40f, 0f, 0f,
        listOf(

        ),
        ::artWake,
    ),
    CropField(
        "Crop field, green (one tile)", SceneryTier.Flat, true, 84f, 68f, 72f, 56f, 0f, 0f,
        listOf(

        ),
        ::artCropField,
    ),
    CropWheat(
        "Crop field, wheat (one tile)", SceneryTier.Flat, true, 84f, 68f, 72f, 56f, 0f, 0f,
        listOf(

        ),
        ::artCropWheat,
    ),
    CropPlough(
        "Crop field, ploughed (one tile)", SceneryTier.Flat, true, 84f, 68f, 72f, 56f, 0f, 0f,
        listOf(

        ),
        ::artCropPlough,
    ),
    ;

    /** The sprite's shapes. Built on first use. */
    val art: SceneryArt by lazy(artOf)
}

/** Small things scattered on open ground: tufts, flowers, reeds, a lily pad, a duck. */
internal enum class SceneryDetail(artOf: () -> SceneryArt) {
    Tuft(::artDetailTuft),
    TuftLight(::artDetailTuftLight),
    Flower(::artDetailFlower),
    FlowerPink(::artDetailFlowerPink),
    Reed(::artDetailReed),
    Lily(::artDetailLily),
    Duck(::artDetailDuck),
    ;

    val art: SceneryArt by lazy(artOf)
}

/** The cloud, a flat silhouette in three tones with its outline, drawn around its origin. */
internal val sceneryCloud: SceneryArt by lazy(::artCloud)

/** The cloud's silhouette as path data: one lobed outline in a piece. Its shadow is made from it. */
internal const val sceneryCloudSilhouette = "M -58 20 a 22 22 0 0 1 4 -43 a 34 34 0 0 1 62 -12 a 26 26 0 0 1 44 16 a 20 20 0 0 1 4 39 z"

/** The width of the cloud's outline, in the cloud's own units. */
internal const val sceneryCloudOutlineWidth = 3f

private fun artTree() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Wood500, SceneryPaint.Outline, 2.2f, "M -1 14 H 1 A 3 3 0 0 1 4 17 V 27 A 3 3 0 0 1 1 30 H -1 A 3 3 0 0 1 -4 27 V 17 A 3 3 0 0 1 -1 14 Z"),
    SceneryShape(SceneryPaint.Wood700, null, 0f, "M 1.7 15 H 1.8 A 1.2 1.2 0 0 1 3 16.2 V 27.8 A 1.2 1.2 0 0 1 1.8 29 H 1.7 A 1.2 1.2 0 0 1 0.5 27.8 V 16.2 A 1.2 1.2 0 0 1 1.7 15 Z"),
    SceneryShape(SceneryPaint.Leaf700, null, 0f, "M 0.1 -30.2 A 11.6 11.6 0 0 1 17.6 -23.8 A 12.2 12.2 0 0 1 26.1 -6 A 7.8 7.8 0 0 1 24 6.3 A 13.2 13.2 0 0 1 8.1 20.4 A 10.9 10.9 0 0 1 -9.4 22.2 A 11.3 11.3 0 0 1 -23 10 A 12 12 0 0 1 -23.6 -9.4 A 8.4 8.4 0 0 1 -17.6 -21.5 A 12.2 12.2 0 0 1 0.1 -30.2 Z"),
    SceneryGroup("M 0.1 -30.2 A 11.6 11.6 0 0 1 17.6 -23.8 A 12.2 12.2 0 0 1 26.1 -6 A 7.8 7.8 0 0 1 24 6.3 A 13.2 13.2 0 0 1 8.1 20.4 A 10.9 10.9 0 0 1 -9.4 22.2 A 11.3 11.3 0 0 1 -23 10 A 12 12 0 0 1 -23.6 -9.4 A 8.4 8.4 0 0 1 -17.6 -21.5 A 12.2 12.2 0 0 1 0.1 -30.2 Z", null, listOf(
        SceneryShape(SceneryPaint.Leaf500, null, 0f, "M 0.1 -30.2 A 11.6 11.6 0 0 1 17.6 -23.8 A 12.2 12.2 0 0 1 26.1 -6 A 7.8 7.8 0 0 1 24 6.3 A 13.2 13.2 0 0 1 8.1 20.4 A 10.9 10.9 0 0 1 -9.4 22.2 A 11.3 11.3 0 0 1 -23 10 A 12 12 0 0 1 -23.6 -9.4 A 8.4 8.4 0 0 1 -17.6 -21.5 A 12.2 12.2 0 0 1 0.1 -30.2 Z", matrix = listOf(1f, 0f, 0f, 1f, -4.4f, -5.2f)),
        SceneryShape(SceneryPaint.Leaf300, null, 0f, "M -8.4 -24.1 A 8.1 8.1 0 0 1 2.1 -16.1 A 7.8 7.8 0 0 1 -1.7 -4.1 A 7.7 7.7 0 0 1 -14.2 -3.8 A 9.4 9.4 0 0 1 -18 -18.5 A 6.9 6.9 0 0 1 -8.4 -24.1 Z"),
    )),
    SceneryShape(null, SceneryPaint.Leaf900, 1.3f, "M -3.1 2.6 q 3.6 -5.2 9.4 -1 M 4.7 -11.4 q 3.1 -4.2 7.8 -0.5 M -14.6 0.5 q 2.6 -4.2 6.8 -0.8"),
    SceneryShape(null, SceneryPaint.Outline, 2.5f, "M 0.1 -30.2 A 11.6 11.6 0 0 1 17.6 -23.8 A 12.2 12.2 0 0 1 26.1 -6 A 7.8 7.8 0 0 1 24 6.3 A 13.2 13.2 0 0 1 8.1 20.4 A 10.9 10.9 0 0 1 -9.4 22.2 A 11.3 11.3 0 0 1 -23 10 A 12 12 0 0 1 -23.6 -9.4 A 8.4 8.4 0 0 1 -17.6 -21.5 A 12.2 12.2 0 0 1 0.1 -30.2 Z"),
))

private fun artTreeSmall() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Wood500, SceneryPaint.Outline, 2.2f, "M -1 9 H 1 A 3 3 0 0 1 4 12 V 22 A 3 3 0 0 1 1 25 H -1 A 3 3 0 0 1 -4 22 V 12 A 3 3 0 0 1 -1 9 Z"),
    SceneryShape(SceneryPaint.Wood700, null, 0f, "M 1.7 10 H 1.8 A 1.2 1.2 0 0 1 3 11.2 V 22.8 A 1.2 1.2 0 0 1 1.8 24 H 1.7 A 1.2 1.2 0 0 1 0.5 22.8 V 11.2 A 1.2 1.2 0 0 1 1.7 10 Z"),
    SceneryShape(SceneryPaint.Leaf700, null, 0f, "M -1.8 -23.5 A 9.7 9.7 0 0 1 13.3 -18.9 A 9.8 9.8 0 0 1 20.3 -4.7 A 9.5 9.5 0 0 1 14.9 9.7 A 9 9 0 0 1 1.5 15 A 10.4 10.4 0 0 1 -14 8.7 A 9.7 9.7 0 0 1 -22 -4.8 A 11.6 11.6 0 0 1 -12.5 -20.8 A 6.8 6.8 0 0 1 -1.8 -23.5 Z"),
    SceneryGroup("M -1.8 -23.5 A 9.7 9.7 0 0 1 13.3 -18.9 A 9.8 9.8 0 0 1 20.3 -4.7 A 9.5 9.5 0 0 1 14.9 9.7 A 9 9 0 0 1 1.5 15 A 10.4 10.4 0 0 1 -14 8.7 A 9.7 9.7 0 0 1 -22 -4.8 A 11.6 11.6 0 0 1 -12.5 -20.8 A 6.8 6.8 0 0 1 -1.8 -23.5 Z", null, listOf(
        SceneryShape(SceneryPaint.Leaf500, null, 0f, "M -1.8 -23.5 A 9.7 9.7 0 0 1 13.3 -18.9 A 9.8 9.8 0 0 1 20.3 -4.7 A 9.5 9.5 0 0 1 14.9 9.7 A 9 9 0 0 1 1.5 15 A 10.4 10.4 0 0 1 -14 8.7 A 9.7 9.7 0 0 1 -22 -4.8 A 11.6 11.6 0 0 1 -12.5 -20.8 A 6.8 6.8 0 0 1 -1.8 -23.5 Z", matrix = listOf(1f, 0f, 0f, 1f, -3.6f, -4.2f)),
        SceneryShape(SceneryPaint.Leaf300, null, 0f, "M -5.5 -19.7 A 6.9 6.9 0 0 1 3 -12.5 A 6.1 6.1 0 0 1 -2.2 -4.1 A 5.7 5.7 0 0 1 -11.4 -5.1 A 5.4 5.4 0 0 1 -14.4 -13.2 A 6.8 6.8 0 0 1 -5.5 -19.7 Z"),
    )),
    SceneryShape(null, SceneryPaint.Leaf900, 1.3f, "M -2.5 2.1 q 2.9 -4.2 7.6 -0.8 M 3.8 -9.2 q 2.5 -3.4 6.3 -0.4 M -11.8 0.4 q 2.1 -3.4 5.5 -0.6"),
    SceneryShape(null, SceneryPaint.Outline, 2.5f, "M -1.8 -23.5 A 9.7 9.7 0 0 1 13.3 -18.9 A 9.8 9.8 0 0 1 20.3 -4.7 A 9.5 9.5 0 0 1 14.9 9.7 A 9 9 0 0 1 1.5 15 A 10.4 10.4 0 0 1 -14 8.7 A 9.7 9.7 0 0 1 -22 -4.8 A 11.6 11.6 0 0 1 -12.5 -20.8 A 6.8 6.8 0 0 1 -1.8 -23.5 Z"),
))

private fun artTreeOak() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Wood500, SceneryPaint.Outline, 2.2f, "M -1 18 H 1 A 3 3 0 0 1 4 21 V 31 A 3 3 0 0 1 1 34 H -1 A 3 3 0 0 1 -4 31 V 21 A 3 3 0 0 1 -1 18 Z"),
    SceneryShape(SceneryPaint.Wood700, null, 0f, "M 1.7 19 H 1.8 A 1.2 1.2 0 0 1 3 20.2 V 31.8 A 1.2 1.2 0 0 1 1.8 33 H 1.7 A 1.2 1.2 0 0 1 0.5 31.8 V 20.2 A 1.2 1.2 0 0 1 1.7 19 Z"),
    SceneryShape(SceneryPaint.Leaf700, null, 0f, "M 0.9 -31.6 A 10.5 10.5 0 0 1 17.6 -28.6 A 11.9 11.9 0 0 1 29.4 -13.6 A 12.6 12.6 0 0 1 28.6 6.7 A 8.7 8.7 0 0 1 20.6 18.3 A 11.6 11.6 0 0 1 2.6 23.3 A 11.7 11.7 0 0 1 -15.8 19.5 A 12 12 0 0 1 -28.5 4.9 A 12.3 12.3 0 0 1 -26.5 -14.9 A 11.1 11.1 0 0 1 -16.8 -29.9 A 11 11 0 0 1 0.9 -31.6 Z"),
    SceneryGroup("M 0.9 -31.6 A 10.5 10.5 0 0 1 17.6 -28.6 A 11.9 11.9 0 0 1 29.4 -13.6 A 12.6 12.6 0 0 1 28.6 6.7 A 8.7 8.7 0 0 1 20.6 18.3 A 11.6 11.6 0 0 1 2.6 23.3 A 11.7 11.7 0 0 1 -15.8 19.5 A 12 12 0 0 1 -28.5 4.9 A 12.3 12.3 0 0 1 -26.5 -14.9 A 11.1 11.1 0 0 1 -16.8 -29.9 A 11 11 0 0 1 0.9 -31.6 Z", null, listOf(
        SceneryShape(SceneryPaint.Leaf500, null, 0f, "M 0.9 -31.6 A 10.5 10.5 0 0 1 17.6 -28.6 A 11.9 11.9 0 0 1 29.4 -13.6 A 12.6 12.6 0 0 1 28.6 6.7 A 8.7 8.7 0 0 1 20.6 18.3 A 11.6 11.6 0 0 1 2.6 23.3 A 11.7 11.7 0 0 1 -15.8 19.5 A 12 12 0 0 1 -28.5 4.9 A 12.3 12.3 0 0 1 -26.5 -14.9 A 11.1 11.1 0 0 1 -16.8 -29.9 A 11 11 0 0 1 0.9 -31.6 Z", matrix = listOf(1f, 0f, 0f, 1f, -5.1f, -6f)),
        SceneryShape(SceneryPaint.Leaf300, null, 0f, "M -7.3 -26.5 A 8.4 8.4 0 0 1 3.8 -18.6 A 10.3 10.3 0 0 1 -3 -3.4 A 8.7 8.7 0 0 1 -16.7 -6.1 A 9.6 9.6 0 0 1 -21.1 -20.9 A 9.3 9.3 0 0 1 -7.3 -26.5 Z"),
    )),
    SceneryShape(null, SceneryPaint.Leaf900, 1.3f, "M -3.6 3 q 4.2 -6 10.8 -1.2 M 5.4 -13.2 q 3.6 -4.8 9 -0.6 M -16.8 0.6 q 3 -4.8 7.8 -0.9"),
    SceneryShape(null, SceneryPaint.Outline, 2.5f, "M 0.9 -31.6 A 10.5 10.5 0 0 1 17.6 -28.6 A 11.9 11.9 0 0 1 29.4 -13.6 A 12.6 12.6 0 0 1 28.6 6.7 A 8.7 8.7 0 0 1 20.6 18.3 A 11.6 11.6 0 0 1 2.6 23.3 A 11.7 11.7 0 0 1 -15.8 19.5 A 12 12 0 0 1 -28.5 4.9 A 12.3 12.3 0 0 1 -26.5 -14.9 A 11.1 11.1 0 0 1 -16.8 -29.9 A 11 11 0 0 1 0.9 -31.6 Z"),
))

private fun artPine() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Pine700, SceneryPaint.Outline, 2.5f, "M 21.4 8.3 L 12.5 10.9 L 11.1 20.2 L 2.5 16.4 L -4.5 22.6 L -8.6 14.2 L -17.9 14.4 L -15.7 5.3 L -23 -0.5 L -15.4 -6 L -17.3 -15.1 L -8 -14.5 L -3.5 -22.7 L 3.2 -16.2 L 11.9 -19.7 L 12.9 -10.4 L 21.8 -7.4 L 16.6 0.3 Z"),
    SceneryShape(SceneryPaint.Pine500, SceneryPaint.Pine900, 1.2f, "M 10 6.7 L 3.4 6.3 L 0.8 12.3 L -3.9 7.8 L -9.7 10.8 L -10.5 4.3 L -16.8 2.8 L -13.2 -2.7 L -17.1 -7.9 L -10.9 -9.8 L -10.5 -16.3 L -4.5 -13.7 L 0 -18.5 L 2.9 -12.6 L 9.4 -13.4 L 7.9 -7 L 13.3 -3.4 L 8.1 0.5 Z"),
    SceneryShape(SceneryPaint.Pine300, SceneryPaint.Pine900, 1.1f, "M 0.3 0.9 L -3.5 -0.6 L -6.8 1.9 L -8.1 -2 L -12.1 -2.9 L -9.8 -6.4 L -11.6 -10.1 L -7.5 -10.5 L -5.6 -14.2 L -2.8 -11.2 L 1.2 -12.1 L 0.7 -8 L 3.9 -5.4 L 0.4 -3.3 Z"),
))

private fun artPineSmall() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Pine700, SceneryPaint.Outline, 2.5f, "M 13.3 12.1 L 6 11.5 L 2.4 17.8 L -2.8 12.7 L -9.6 15.2 L -10.3 7.9 L -17.2 5.4 L -12.9 -0.5 L -16.6 -6.9 L -9.6 -8.7 L -8.3 -16 L -1.7 -12.8 L 3.9 -17.6 L 6.9 -10.9 L 14.3 -11 L 12.4 -3.9 L 18 0.8 L 12 4.9 Z"),
    SceneryShape(SceneryPaint.Pine500, SceneryPaint.Pine900, 1.2f, "M 4.4 8.2 L -0.3 6.1 L -3.8 9.7 L -6 5.1 L -11.1 5.6 L -9.8 0.6 L -14 -2.3 L -9.9 -5.2 L -11.2 -10.1 L -6.1 -9.7 L -4 -14.4 L -0.4 -10.8 L 4.2 -13 L 4.7 -7.9 L 9.7 -6.6 L 6.8 -2.4 L 9.7 1.8 L 4.8 3.1 Z"),
    SceneryShape(SceneryPaint.Pine300, SceneryPaint.Pine900, 1.1f, "M -1.9 1.6 L -4.3 -0.5 L -7.4 0.4 L -7.3 -2.8 L -9.9 -4.6 L -7.3 -6.5 L -7.5 -9.7 L -4.4 -8.8 L -2 -11 L -0.8 -8 L 2.4 -7.5 L 0.9 -4.7 L 2.4 -1.9 L -0.7 -1.4 Z"),
))

private fun artBush() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Leaf700, null, 0f, "M 0.9 -11.7 A 5.9 5.9 0 0 1 9.1 -6.8 A 6.7 6.7 0 0 1 10.6 3.9 A 8.4 8.4 0 0 1 -1 10.8 A 7 7 0 0 1 -9.6 3.6 A 6.5 6.5 0 0 1 -9.3 -7 A 7 7 0 0 1 0.9 -11.7 Z"),
    SceneryGroup("M 0.9 -11.7 A 5.9 5.9 0 0 1 9.1 -6.8 A 6.7 6.7 0 0 1 10.6 3.9 A 8.4 8.4 0 0 1 -1 10.8 A 7 7 0 0 1 -9.6 3.6 A 6.5 6.5 0 0 1 -9.3 -7 A 7 7 0 0 1 0.9 -11.7 Z", null, listOf(
        SceneryShape(SceneryPaint.Leaf500, null, 0f, "M 0.9 -11.7 A 5.9 5.9 0 0 1 9.1 -6.8 A 6.7 6.7 0 0 1 10.6 3.9 A 8.4 8.4 0 0 1 -1 10.8 A 7 7 0 0 1 -9.6 3.6 A 6.5 6.5 0 0 1 -9.3 -7 A 7 7 0 0 1 0.9 -11.7 Z", matrix = listOf(1f, 0f, 0f, 1f, -2.5f, -3f)),
        SceneryShape(SceneryPaint.Leaf300, null, 0f, "M -8 -5 A 4 4 0 1 0 0 -5 A 4 4 0 1 0 -8 -5 Z"),
    )),
    SceneryShape(null, SceneryPaint.Outline, 2.2f, "M 0.9 -11.7 A 5.9 5.9 0 0 1 9.1 -6.8 A 6.7 6.7 0 0 1 10.6 3.9 A 8.4 8.4 0 0 1 -1 10.8 A 7 7 0 0 1 -9.6 3.6 A 6.5 6.5 0 0 1 -9.3 -7 A 7 7 0 0 1 0.9 -11.7 Z"),
    SceneryShape(SceneryPaint.Wall100, null, 0f, "M 1.5 -1 A 1.5 1.5 0 1 0 4.5 -1 A 1.5 1.5 0 1 0 1.5 -1 Z"),
    SceneryShape(SceneryPaint.Wall100, null, 0f, "M -3.5 4 A 1.5 1.5 0 1 0 -0.5 4 A 1.5 1.5 0 1 0 -3.5 4 Z"),
    SceneryShape(SceneryPaint.Wall100, null, 0f, "M 4.7 5 A 1.3 1.3 0 1 0 7.3 5 A 1.3 1.3 0 1 0 4.7 5 Z"),
))

private fun artHedge() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Leaf700, SceneryPaint.Outline, 5f, "M -9.5 0 A 9.5 9.5 0 1 0 9.5 0 A 9.5 9.5 0 1 0 -9.5 0 Z"),
    SceneryShape(SceneryPaint.Leaf500, null, 0f, "M -9.5 0 A 9.5 9.5 0 1 0 9.5 0 A 9.5 9.5 0 1 0 -9.5 0 Z"),
    SceneryShape(SceneryPaint.Leaf300, null, 0f, "M -7.2 -3 A 5.2 5.2 0 1 0 3.2 -3 A 5.2 5.2 0 1 0 -7.2 -3 Z"),
))

private fun artAcacia() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Wood500, SceneryPaint.Outline, 2.2f, "M -3 12 q -2 12 -1 24 h 7 q 1 -12 -1 -24 z"),
    SceneryShape(null, SceneryPaint.Wood900, 1.2f, "M 0 16 q -1 8 0 16"),
    SceneryGroup(null, listOf(1f, 0f, 0f, 0.74f, 0f, 0f), listOf(
        SceneryShape(SceneryPaint.Leaf700, null, 0f, "M 0.3 -38.9 A 14.2 14.2 0 0 1 18.9 -32.5 A 8.3 8.3 0 0 1 24.8 -22.6 A 14.3 14.3 0 0 1 29.3 -3.3 A 11.3 11.3 0 0 1 23.2 11.1 A 14.2 14.2 0 0 1 6.7 21.8 A 11.9 11.9 0 0 1 -9.8 19.9 A 12 12 0 0 1 -23.5 10.2 A 12.6 12.6 0 0 1 -29.9 -6.1 A 10.7 10.7 0 0 1 -28.6 -21 A 14.8 14.8 0 0 1 -13.8 -35.2 A 10.6 10.6 0 0 1 0.3 -38.9 Z"),
        SceneryGroup("M 0.3 -38.9 A 14.2 14.2 0 0 1 18.9 -32.5 A 8.3 8.3 0 0 1 24.8 -22.6 A 14.3 14.3 0 0 1 29.3 -3.3 A 11.3 11.3 0 0 1 23.2 11.1 A 14.2 14.2 0 0 1 6.7 21.8 A 11.9 11.9 0 0 1 -9.8 19.9 A 12 12 0 0 1 -23.5 10.2 A 12.6 12.6 0 0 1 -29.9 -6.1 A 10.7 10.7 0 0 1 -28.6 -21 A 14.8 14.8 0 0 1 -13.8 -35.2 A 10.6 10.6 0 0 1 0.3 -38.9 Z", null, listOf(
            SceneryShape(SceneryPaint.Leaf500, null, 0f, "M 0.3 -38.9 A 14.2 14.2 0 0 1 18.9 -32.5 A 8.3 8.3 0 0 1 24.8 -22.6 A 14.3 14.3 0 0 1 29.3 -3.3 A 11.3 11.3 0 0 1 23.2 11.1 A 14.2 14.2 0 0 1 6.7 21.8 A 11.9 11.9 0 0 1 -9.8 19.9 A 12 12 0 0 1 -23.5 10.2 A 12.6 12.6 0 0 1 -29.9 -6.1 A 10.7 10.7 0 0 1 -28.6 -21 A 14.8 14.8 0 0 1 -13.8 -35.2 A 10.6 10.6 0 0 1 0.3 -38.9 Z", matrix = listOf(1f, 0f, 0f, 1f, -5f, -7f)),
            SceneryShape(SceneryPaint.Leaf300, null, 0f, "M -7.6 -29.4 A 6.7 6.7 0 0 1 2 -24.5 A 8.1 8.1 0 0 1 3.9 -11.5 A 11.2 11.2 0 0 1 -10.9 -1.3 A 8.9 8.9 0 0 1 -21.7 -10.8 A 7.9 7.9 0 0 1 -20.6 -23.5 A 8.9 8.9 0 0 1 -7.6 -29.4 Z"),
        )),
        SceneryShape(null, SceneryPaint.Leaf900, 1.3f, "M -4 -2 q 5 -6 12 -1 M 8 -18 q 4 -5 10 -1 M -20 -6 q 4 -5 9 -1"),
        SceneryShape(null, SceneryPaint.Outline, 2.5f, "M 0.3 -38.9 A 14.2 14.2 0 0 1 18.9 -32.5 A 8.3 8.3 0 0 1 24.8 -22.6 A 14.3 14.3 0 0 1 29.3 -3.3 A 11.3 11.3 0 0 1 23.2 11.1 A 14.2 14.2 0 0 1 6.7 21.8 A 11.9 11.9 0 0 1 -9.8 19.9 A 12 12 0 0 1 -23.5 10.2 A 12.6 12.6 0 0 1 -29.9 -6.1 A 10.7 10.7 0 0 1 -28.6 -21 A 14.8 14.8 0 0 1 -13.8 -35.2 A 10.6 10.6 0 0 1 0.3 -38.9 Z", nonScalingStroke = true),
    )),
))

private fun artPalm() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Wood500, SceneryPaint.Outline, 2.2f, "M -3 -12 q -4 14 1 30 h 6 q -4 -16 0 -30 z"),
    SceneryShape(null, SceneryPaint.Wood900, 1.1f, "M -2 -4 h 5 M -2 3 h 5.5 M -1 10 h 5.5"),
    SceneryShape(SceneryPaint.Leaf300, SceneryPaint.Outline, 2.2f, "M 0 -14 Q 9 -22.5 4.2 -33.4 Q -4.8 -24.9 0 -14 Z"),
    SceneryShape(null, SceneryPaint.Leaf900, 1.1f, "M 0 -14 L 3.6 -30.7"),
    SceneryShape(SceneryPaint.Leaf300, SceneryPaint.Outline, 2.2f, "M 0 -14 Q 15.2 -13.5 23.4 -25.1 Q 8.2 -25.6 0 -14 Z"),
    SceneryShape(null, SceneryPaint.Leaf900, 1.1f, "M 0 -14 L 20.1 -23.5"),
    SceneryShape(SceneryPaint.Leaf500, SceneryPaint.Outline, 2.2f, "M 0 -14 Q 9.8 -4.7 23.2 -8.9 Q 13.4 -18.2 0 -14 Z"),
    SceneryShape(null, SceneryPaint.Leaf900, 1.1f, "M 0 -14 L 20 -9.6"),
    SceneryShape(SceneryPaint.Leaf700, SceneryPaint.Outline, 2.2f, "M 0 -14 Q -0.6 -1 11.4 6.1 Q 12 -6.9 0 -14 Z"),
    SceneryShape(null, SceneryPaint.Leaf900, 1.1f, "M 0 -14 L 9.8 3.3"),
    SceneryShape(SceneryPaint.Leaf700, SceneryPaint.Outline, 2.2f, "M 0 -14 Q -11.4 -8 -10.1 3.8 Q 1.3 -2.1 0 -14 Z"),
    SceneryShape(null, SceneryPaint.Leaf900, 1.1f, "M 0 -14 L -8.7 1.3"),
    SceneryShape(SceneryPaint.Leaf500, SceneryPaint.Outline, 2.2f, "M 0 -14 Q -14.9 -17.9 -26.1 -8.3 Q -11.2 -4.4 0 -14 Z"),
    SceneryShape(null, SceneryPaint.Leaf900, 1.1f, "M 0 -14 L -22.4 -9.1"),
    SceneryShape(SceneryPaint.Leaf300, SceneryPaint.Outline, 2.2f, "M 0 -14 Q -5.8 -25.4 -19.7 -25.3 Q -13.8 -13.9 0 -14 Z"),
    SceneryShape(null, SceneryPaint.Leaf900, 1.1f, "M 0 -14 L -16.9 -23.7"),
    SceneryShape(SceneryPaint.Wood700, SceneryPaint.Outline, 1.6f, "M -5.6 -12 A 2.6 2.6 0 1 0 -0.4 -12 A 2.6 2.6 0 1 0 -5.6 -12 Z"),
    SceneryShape(SceneryPaint.Wood700, SceneryPaint.Outline, 1.6f, "M 0.4 -11 A 2.6 2.6 0 1 0 5.6 -11 A 2.6 2.6 0 1 0 0.4 -11 Z"),
))

private fun artRock() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Stone500, SceneryPaint.Outline, 2.2f, "M -13 2 L -9 -9 L 3 -12 L 12 -5 L 14 6 L 5 11 L -7 10 Z"),
    SceneryShape(SceneryPaint.Stone300, null, 0f, "M -9 -9 L 3 -12 L 5 -3 L -6 -1 Z"),
    SceneryShape(SceneryPaint.Stone700, null, 0f, "M 5 -3 L 12 -5 L 14 6 L 5 11 L 3 3 Z"),
    SceneryShape(null, SceneryPaint.Outline, 2.2f, "M -13 2 L -9 -9 L 3 -12 L 12 -5 L 14 6 L 5 11 L -7 10 Z"),
    SceneryShape(null, SceneryPaint.Stone700, 1.2f, "M -6 -1 l -3 6"),
    SceneryShape(SceneryPaint.Stone300, SceneryPaint.Outline, 2f, "M 9.5 9 A 4.5 4.5 0 1 0 18.5 9 A 4.5 4.5 0 1 0 9.5 9 Z"),
))

private fun artHayBale() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Straw500, null, 0f, "M -9 0 A 9 9 0 1 0 9 0 A 9 9 0 1 0 -9 0 Z"),
    SceneryGroup("M -9 0 A 9 9 0 1 0 9 0 A 9 9 0 1 0 -9 0 Z", null, listOf(
        SceneryShape(SceneryPaint.Straw300, null, 0f, "M -11 -2.5 A 9 9 0 1 0 7 -2.5 A 9 9 0 1 0 -11 -2.5 Z"),
    )),
    SceneryShape(null, SceneryPaint.Straw700, 1.2f, "M 0.5 0.5 a 2 2 0 1 1 -2.5 -2 a 4.5 4.5 0 1 1 5 6 a 6.5 6.5 0 0 1 -9 -5"),
    SceneryShape(null, SceneryPaint.Outline, 2f, "M -9 0 A 9 9 0 1 0 9 0 A 9 9 0 1 0 -9 0 Z"),
))

private fun artFence() = SceneryArt(listOf(
    SceneryShape(null, SceneryPaint.Outline, 4.6f, "M -8 0 h 16"),
    SceneryShape(null, SceneryPaint.Wood300, 2f, "M -8 0 h 16"),
    SceneryShape(SceneryPaint.Wood300, SceneryPaint.Outline, 1.6f, "M -11 0 A 3 3 0 1 0 -5 0 A 3 3 0 1 0 -11 0 Z"),
    SceneryShape(SceneryPaint.Wood300, SceneryPaint.Outline, 1.6f, "M 5 0 A 3 3 0 1 0 11 0 A 3 3 0 1 0 5 0 Z"),
))

private fun artFarmhouse() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Stone300, SceneryPaint.Stone700, 1.2f, "M -9.5 30 H 9.5 A 1.5 1.5 0 0 1 11 31.5 V 33.5 A 1.5 1.5 0 0 1 9.5 35 H -9.5 A 1.5 1.5 0 0 1 -11 33.5 V 31.5 A 1.5 1.5 0 0 1 -9.5 30 Z"),
    SceneryShape(SceneryPaint.Wall100, SceneryPaint.Outline, 2.5f, "M -42 10 H 42 V 30 H -42 Z"),
    SceneryShape(SceneryPaint.Wall300, null, 0f, "M 28 11.5 H 40.5 V 28.5 H 28 Z"),
    SceneryShape(SceneryPaint.Wall500, null, 0f, "M -40.7 14 H 40.7 V 17.5 H -40.7 Z"),
    SceneryShape(SceneryPaint.Wood500, SceneryPaint.Wood900, 1.3f, "M -4 17 H 4 A 2 2 0 0 1 6 19 V 28 A 2 2 0 0 1 4 30 H -4 A 2 2 0 0 1 -6 28 V 19 A 2 2 0 0 1 -4 17 Z"),
    SceneryShape(SceneryPaint.Beacon, null, 0f, "M 2 24 A 1 1 0 1 0 4 24 A 1 1 0 1 0 2 24 Z"),
    SceneryShape(SceneryPaint.Wood700, SceneryPaint.Wood900, 1f, "M -36 17 H -33 V 26 H -36 Z"),
    SceneryShape(SceneryPaint.Wood700, SceneryPaint.Wood900, 1f, "M -21 17 H -18 V 26 H -21 Z"),
    SceneryShape(SceneryPaint.Wood700, SceneryPaint.Wood900, 1f, "M 18 17 H 21 V 26 H 18 Z"),
    SceneryShape(SceneryPaint.Wood700, SceneryPaint.Wood900, 1f, "M 33 17 H 36 V 26 H 33 Z"),
    SceneryShape(SceneryPaint.Window, SceneryPaint.Wood900, 1.2f, "M -32 17 H -22 A 1 1 0 0 1 -21 18 V 25 A 1 1 0 0 1 -22 26 H -32 A 1 1 0 0 1 -33 25 V 18 A 1 1 0 0 1 -32 17 Z"),
    SceneryShape(null, SceneryPaint.Wood900, 1f, "M -27 17 v 9 M -33 21.5 h 12"),
    SceneryShape(SceneryPaint.Window, SceneryPaint.Wood900, 1.2f, "M 22 17 H 32 A 1 1 0 0 1 33 18 V 25 A 1 1 0 0 1 32 26 H 22 A 1 1 0 0 1 21 25 V 18 A 1 1 0 0 1 22 17 Z"),
    SceneryShape(null, SceneryPaint.Wood900, 1f, "M 27 17 v 9 M 21 21.5 h 12"),
    SceneryShape(SceneryPaint.Wood700, null, 0f, "M -33 26.5 H -21 A 1 1 0 0 1 -20 27.5 V 28.1 A 1 1 0 0 1 -21 29.1 H -33 A 1 1 0 0 1 -34 28.1 V 27.5 A 1 1 0 0 1 -33 26.5 Z"),
    SceneryShape(SceneryPaint.Muzzle, null, 0f, "M -32.3 26 A 1.3 1.3 0 1 0 -29.7 26 A 1.3 1.3 0 1 0 -32.3 26 Z"),
    SceneryShape(SceneryPaint.Wall100, null, 0f, "M -28.3 25.6 A 1.3 1.3 0 1 0 -25.7 25.6 A 1.3 1.3 0 1 0 -28.3 25.6 Z"),
    SceneryShape(SceneryPaint.Muzzle, null, 0f, "M -24.3 26 A 1.3 1.3 0 1 0 -21.7 26 A 1.3 1.3 0 1 0 -24.3 26 Z"),
    SceneryShape(SceneryPaint.Roof500, null, 0f, "M -43 -38 H 43 A 3 3 0 0 1 46 -35 V 11 A 3 3 0 0 1 43 14 H -43 A 3 3 0 0 1 -46 11 V -35 A 3 3 0 0 1 -43 -38 Z"),
    SceneryShape(SceneryPaint.Roof300, null, 0f, "M -46 -12 v -23 a 3 3 0 0 1 3 -3 h 86 a 3 3 0 0 1 3 3 v 23 z"),
    SceneryShape(null, SceneryPaint.Roof500, 1.2f, "M -46 -30 h 92 M -46 -21 h 92"),
    SceneryShape(null, SceneryPaint.Roof700, 1.2f, "M -46 -3 h 92 M -46 6 h 92"),
    SceneryShape(null, SceneryPaint.Roof500, 1f, "M -34 -38 v 8 M -12 -38 v 8 M 10 -38 v 8 M 32 -38 v 8 M -23 -30 v 9 M -1 -30 v 9 M 21 -30 v 9 M -34 -21 v 9 M -12 -21 v 9 M 10 -21 v 9 M 32 -21 v 9"),
    SceneryShape(null, SceneryPaint.Roof700, 1f, "M -23 -12 v 9 M -1 -12 v 9 M 21 -12 v 9 M -34 -3 v 9 M -12 -3 v 9 M 10 -3 v 9 M 32 -3 v 9 M -23 6 v 8 M -1 6 v 8 M 21 6 v 8"),
    SceneryShape(null, SceneryPaint.Roof900, 1.8f, "M -46 -12 h 92"),
    SceneryShape(SceneryPaint.Roof700, null, 0f, "M 30 -24 l 9 10 h -11 l -9 -10 z"),
    SceneryShape(SceneryPaint.Stone300, SceneryPaint.Outline, 2f, "M 20.5 -35 H 28.5 A 1.5 1.5 0 0 1 30 -33.5 V -25.5 A 1.5 1.5 0 0 1 28.5 -24 H 20.5 A 1.5 1.5 0 0 1 19 -25.5 V -33.5 A 1.5 1.5 0 0 1 20.5 -35 Z"),
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M 23 -32 H 26 A 1 1 0 0 1 27 -31 V -28 A 1 1 0 0 1 26 -27 H 23 A 1 1 0 0 1 22 -28 V -31 A 1 1 0 0 1 23 -32 Z"),
    SceneryShape(null, SceneryPaint.Outline, 2.5f, "M -43 -38 H 43 A 3 3 0 0 1 46 -35 V 11 A 3 3 0 0 1 43 14 H -43 A 3 3 0 0 1 -46 11 V -35 A 3 3 0 0 1 -43 -38 Z"),
    SceneryShape(SceneryPaint.Roof700, SceneryPaint.Outline, 2f, "M -10 9 H 10 A 2 2 0 0 1 12 11 V 14 A 2 2 0 0 1 10 16 H -10 A 2 2 0 0 1 -12 14 V 11 A 2 2 0 0 1 -10 9 Z"),
))

private fun artBarn() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Roof500, SceneryPaint.Outline, 2.5f, "M -36 8 H 36 V 30 H -36 Z"),
    SceneryShape(SceneryPaint.Roof700, null, 0f, "M 23 9.5 H 34.5 V 28.5 H 23 Z"),
    SceneryShape(SceneryPaint.Roof700, null, 0f, "M -34.7 12 H 34.7 V 15 H -34.7 Z"),
    SceneryShape(SceneryPaint.Wall100, SceneryPaint.Roof900, 1.3f, "M -12 14 H 12 A 1 1 0 0 1 13 15 V 29 A 1 1 0 0 1 12 30 H -12 A 1 1 0 0 1 -13 29 V 15 A 1 1 0 0 1 -12 14 Z"),
    SceneryShape(null, SceneryPaint.Roof700, 1.3f, "M 0 14 v 16 M -13 14 l 13 16 M 0 14 l -13 16 M 0 14 l 13 16 M 13 14 l -13 16"),
    SceneryGroup("M -37 -44 H 37 A 3 3 0 0 1 40 -41 V 9 A 3 3 0 0 1 37 12 H -37 A 3 3 0 0 1 -40 9 V -41 A 3 3 0 0 1 -37 -44 Z", null, listOf(
        SceneryShape(SceneryPaint.Tin500, null, 0f, "M -40 -44 H 40 V -31 H -40 Z"),
        SceneryShape(SceneryPaint.Tin100, null, 0f, "M -40 -31 H 40 V -14 H -40 Z"),
        SceneryShape(SceneryPaint.Tin300, null, 0f, "M -40 -14 H 40 V 2 H -40 Z"),
        SceneryShape(SceneryPaint.Tin700, null, 0f, "M -40 2 H 40 V 12 H -40 Z"),
        SceneryShape(null, SceneryPaint.Tin700, 1f, "M -28 -44 v 56 M -16 -44 v 56 M -4 -44 v 56 M 8 -44 v 56 M 20 -44 v 56 M 32 -44 v 56", opacity = 0.55f),
    )),
    SceneryShape(null, SceneryPaint.Tin700, 1.2f, "M -40 -31 h 80 M -40 2 h 80"),
    SceneryShape(null, SceneryPaint.Tin900, 1.8f, "M -40 -14 h 80"),
    SceneryShape(null, SceneryPaint.Outline, 2.5f, "M -37 -44 H 37 A 3 3 0 0 1 40 -41 V 9 A 3 3 0 0 1 37 12 H -37 A 3 3 0 0 1 -40 9 V -41 A 3 3 0 0 1 -37 -44 Z"),
))

private fun artSilo() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Tin700, null, 0f, "M -14 0 A 14 14 0 1 0 14 0 A 14 14 0 1 0 -14 0 Z"),
    SceneryGroup("M -14 0 A 14 14 0 1 0 14 0 A 14 14 0 1 0 -14 0 Z", null, listOf(
        SceneryShape(SceneryPaint.Tin300, null, 0f, "M -17 -3.5 A 14 14 0 1 0 11 -3.5 A 14 14 0 1 0 -17 -3.5 Z"),
        SceneryShape(SceneryPaint.Tin100, null, 0f, "M -15 -7 A 9 9 0 1 0 3 -7 A 9 9 0 1 0 -15 -7 Z"),
    )),
    SceneryShape(null, SceneryPaint.Tin700, 0.9f, "M 0 -14 v 28 M -14 0 h 28 M -10 -10 l 20 20 M 10 -10 l -20 20", opacity = 0.6f),
    SceneryShape(null, SceneryPaint.Outline, 2.5f, "M -14 0 A 14 14 0 1 0 14 0 A 14 14 0 1 0 -14 0 Z"),
    SceneryShape(SceneryPaint.Tin500, SceneryPaint.Tin900, 1.3f, "M -4.5 0 A 4.5 4.5 0 1 0 4.5 0 A 4.5 4.5 0 1 0 -4.5 0 Z"),
))

private fun artTower() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Wall100, SceneryPaint.Outline, 2.5f, "M -22 28 H 22 V 42 H -22 Z"),
    SceneryShape(SceneryPaint.Wall300, null, 0f, "M 10 29.5 H 20.5 V 40.5 H 10 Z"),
    SceneryShape(SceneryPaint.Wood500, SceneryPaint.Wood900, 1.3f, "M -3.5 31 H 3.5 A 1.5 1.5 0 0 1 5 32.5 V 40.5 A 1.5 1.5 0 0 1 3.5 42 H -3.5 A 1.5 1.5 0 0 1 -5 40.5 V 32.5 A 1.5 1.5 0 0 1 -3.5 31 Z"),
    SceneryShape(SceneryPaint.Window, SceneryPaint.Wood900, 1.1f, "M -16 32 H -11 A 1 1 0 0 1 -10 33 V 36 A 1 1 0 0 1 -11 37 H -16 A 1 1 0 0 1 -17 36 V 33 A 1 1 0 0 1 -16 32 Z"),
    SceneryShape(SceneryPaint.Tin300, SceneryPaint.Outline, 2.5f, "M -21 -6 H 21 A 3 3 0 0 1 24 -3 V 27 A 3 3 0 0 1 21 30 H -21 A 3 3 0 0 1 -24 27 V -3 A 3 3 0 0 1 -21 -6 Z"),
    SceneryShape(SceneryPaint.Tin500, null, 0f, "M 12 -4.7 H 22.7 V 28.7 H 12 Z"),
    SceneryShape(SceneryPaint.Tin500, SceneryPaint.Tin900, 1.2f, "M -16.5 14 H -10.5 A 1.5 1.5 0 0 1 -9 15.5 V 21.5 A 1.5 1.5 0 0 1 -10.5 23 H -16.5 A 1.5 1.5 0 0 1 -18 21.5 V 15.5 A 1.5 1.5 0 0 1 -16.5 14 Z"),
    SceneryShape(SceneryPaint.Tin700, null, 0f, "M -16 18.5 A 2.5 2.5 0 1 0 -11 18.5 A 2.5 2.5 0 1 0 -16 18.5 Z"),
    SceneryShape(SceneryPaint.Wall100, SceneryPaint.Outline, 2.5f, "M -9 -28 H 9 V 6 H -9 Z"),
    SceneryShape(SceneryPaint.Wall300, null, 0f, "M 2 -26.7 H 7.7 V 4.7 H 2 Z"),
    SceneryShape(SceneryPaint.Window, SceneryPaint.Wood900, 1f, "M -4.5 -10 H -2.5 A 1 1 0 0 1 -1.5 -9 V -5 A 1 1 0 0 1 -2.5 -4 H -4.5 A 1 1 0 0 1 -5.5 -5 V -9 A 1 1 0 0 1 -4.5 -10 Z"),
    SceneryShape(SceneryPaint.Glass700, SceneryPaint.Outline, 2.5f, "M 20.3 -21.6 L 8.4 -9.7 L -8.4 -9.7 L -20.3 -21.6 L -20.3 -38.4 L -8.4 -50.3 L 8.4 -50.3 L 20.3 -38.4 Z"),
    SceneryShape(SceneryPaint.Glass, SceneryPaint.Outline, 2.5f, "M 20.3 -28.6 L 8.4 -16.7 L -8.4 -16.7 L -20.3 -28.6 L -20.3 -45.4 L -8.4 -57.3 L 8.4 -57.3 L 20.3 -45.4 Z"),
    SceneryShape(null, SceneryPaint.Glass700, 1.3f, "M 12.9 -31.6 L 20.3 -28.6 M 5.4 -24.1 L 8.4 -16.7 M -5.4 -24.1 L -8.4 -16.7 M -12.9 -31.6 L -20.3 -28.6 M -12.9 -42.4 L -20.3 -45.4 M -5.4 -49.9 L -8.4 -57.3 M 5.4 -49.9 L 8.4 -57.3 M 12.9 -42.4 L 20.3 -45.4"),
    SceneryShape(null, SceneryPaint.GlassShine, 2.2f, "M -17 -43 l 5 -8"),
    SceneryShape(SceneryPaint.Cherry500, SceneryPaint.Outline, 2.2f, "M 12.9 -31.6 L 5.4 -24.1 L -5.4 -24.1 L -12.9 -31.6 L -12.9 -42.4 L -5.4 -49.9 L 5.4 -49.9 L 12.9 -42.4 Z"),
    SceneryShape(SceneryPaint.Cherry200, null, 0f, "M -12.9 -42.4 L -5.4 -49.9 L 5.4 -49.9 L 0 -37 Z"),
    SceneryShape(null, SceneryPaint.Outline, 2.2f, "M 12.9 -31.6 L 5.4 -24.1 L -5.4 -24.1 L -12.9 -31.6 L -12.9 -42.4 L -5.4 -49.9 L 5.4 -49.9 L 12.9 -42.4 Z"),
    SceneryShape(null, SceneryPaint.Outline, 2f, "M 0 -37 l 8 -14"),
    SceneryShape(SceneryPaint.Outline, null, 0f, "M 6.2 -51 A 1.8 1.8 0 1 0 9.8 -51 A 1.8 1.8 0 1 0 6.2 -51 Z"),
    SceneryShape(SceneryPaint.Beacon, SceneryPaint.Outline, 1.5f, "M -3.4 -37 A 3.4 3.4 0 1 0 3.4 -37 A 3.4 3.4 0 1 0 -3.4 -37 Z"),
))

private fun artTerminal() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Concrete300, SceneryPaint.Concrete700, 1.2f, "M -79 32 H 79 A 3 3 0 0 1 82 35 V 39 A 3 3 0 0 1 79 42 H -79 A 3 3 0 0 1 -82 39 V 35 A 3 3 0 0 1 -79 32 Z"),
    SceneryShape(SceneryPaint.Window, SceneryPaint.Outline, 2.5f, "M -72 12 H 72 V 34 H -72 Z"),
    SceneryShape(null, SceneryPaint.Glass700, 1.3f, "M -60 12 v 22 M -48 12 v 22 M -36 12 v 22 M -24 12 v 22 M 24 12 v 22 M 36 12 v 22 M 48 12 v 22 M 60 12 v 22"),
    SceneryShape(null, SceneryPaint.Glass700, 1.1f, "M -70 23 h 52 M 18 23 h 52"),
    SceneryShape(null, SceneryPaint.GlassShine, 1.8f, "M -56 30 l 8 -12 M -51 30 l 8 -12 M 40 30 l 8 -12", opacity = 0.7f),
    SceneryShape(SceneryPaint.Tin700, null, 0f, "M -70.7 30 H 70.7 V 33 H -70.7 Z"),
    SceneryShape(SceneryPaint.Glass700, SceneryPaint.Outline, 1.8f, "M -8 20 H 8 A 1 1 0 0 1 9 21 V 33 A 1 1 0 0 1 8 34 H -8 A 1 1 0 0 1 -9 33 V 21 A 1 1 0 0 1 -8 20 Z"),
    SceneryShape(null, SceneryPaint.Outline, 1.3f, "M 0 20 v 14"),
    SceneryShape(SceneryPaint.Roof500, SceneryPaint.Outline, 2f, "M -15 13 H 15 A 2 2 0 0 1 17 15 V 19 A 2 2 0 0 1 15 21 H -15 A 2 2 0 0 1 -17 19 V 15 A 2 2 0 0 1 -15 13 Z"),
    SceneryShape(null, SceneryPaint.Wall100, 2.2f, "M -10 13 v 8 M -3 13 v 8 M 4 13 v 8 M 11 13 v 8"),
    SceneryShape(null, SceneryPaint.Outline, 2f, "M -15 13 H 15 A 2 2 0 0 1 17 15 V 19 A 2 2 0 0 1 15 21 H -15 A 2 2 0 0 1 -17 19 V 15 A 2 2 0 0 1 -15 13 Z"),
    SceneryShape(SceneryPaint.Tin300, null, 0f, "M -72 -34 H 72 A 4 4 0 0 1 76 -30 V 10 A 4 4 0 0 1 72 14 H -72 A 4 4 0 0 1 -76 10 V -30 A 4 4 0 0 1 -72 -34 Z"),
    SceneryShape(SceneryPaint.Tin500, null, 0f, "M 60 -34 H 76 V 14 H 60 Z"),
    SceneryShape(SceneryPaint.Tin500, null, 0f, "M -76 5 H 76 V 14 H -76 Z"),
    SceneryShape(null, SceneryPaint.Tin500, 1.2f, "M -38 -34 v 39 M 0 -34 v 39 M 38 -34 v 39"),
    SceneryShape(null, SceneryPaint.Outline, 2.5f, "M -72 -34 H 72 A 4 4 0 0 1 76 -30 V 10 A 4 4 0 0 1 72 14 H -72 A 4 4 0 0 1 -76 10 V -30 A 4 4 0 0 1 -72 -34 Z"),
    SceneryShape(SceneryPaint.Glass, SceneryPaint.Outline, 2f, "M -45.5 -27 H 45.5 A 6.5 6.5 0 0 1 52 -20.5 V -20.5 A 6.5 6.5 0 0 1 45.5 -14 H -45.5 A 6.5 6.5 0 0 1 -52 -20.5 V -20.5 A 6.5 6.5 0 0 1 -45.5 -27 Z"),
    SceneryShape(null, SceneryPaint.Glass700, 1.2f, "M -39 -27 v 13 M -26 -27 v 13 M -13 -27 v 13 M 0 -27 v 13 M 13 -27 v 13 M 26 -27 v 13 M 39 -27 v 13"),
    SceneryShape(null, SceneryPaint.GlassShine, 1.8f, "M -46 -17 l 5 -7"),
    SceneryShape(SceneryPaint.Tin500, SceneryPaint.Tin900, 1.2f, "M -66 -8 H -58 A 2 2 0 0 1 -56 -6 V 0 A 2 2 0 0 1 -58 2 H -66 A 2 2 0 0 1 -68 0 V -6 A 2 2 0 0 1 -66 -8 Z"),
    SceneryShape(SceneryPaint.Tin700, null, 0f, "M -65 -3 A 3 3 0 1 0 -59 -3 A 3 3 0 1 0 -65 -3 Z"),
    SceneryShape(SceneryPaint.Tin700, SceneryPaint.Tin900, 1.2f, "M 54 -9 H 62 A 2 2 0 0 1 64 -7 V -1 A 2 2 0 0 1 62 1 H 54 A 2 2 0 0 1 52 -1 V -7 A 2 2 0 0 1 54 -9 Z"),
    SceneryShape(SceneryPaint.Outline, null, 0f, "M -24 -8 H 24 A 3 3 0 0 1 27 -5 V -1 A 3 3 0 0 1 24 2 H -24 A 3 3 0 0 1 -27 -1 V -5 A 3 3 0 0 1 -24 -8 Z"),
    SceneryShape(null, SceneryPaint.Wall100, 2.4f, "M -21 -3 h 5 M -13 -3 h 3 M -7 -3 h 6 M 2 -3 h 4 M 9 -3 h 5 M 17 -3 h 4"),
))

private fun artHangar() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Tin500, SceneryPaint.Outline, 2.5f, "M -54 22 H 54 V 44 H -54 Z"),
    SceneryShape(SceneryPaint.Tin700, null, 0f, "M 38 23.5 H 52.5 V 42.5 H 38 Z"),
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M -34 27 H 34 V 44 H -34 Z"),
    SceneryShape(SceneryPaint.Tin100, SceneryPaint.Outline, 1.6f, "M -3 44 C -3 33 15 33 15 44 Z"),
    SceneryShape(null, SceneryPaint.Glass, 2.2f, "M 2 39 q 4 -3.5 8 0"),
    SceneryShape(SceneryPaint.Tin300, SceneryPaint.Tin900, 1.2f, "M -34 27 H -7 V 44 H -34 Z"),
    SceneryShape(null, SceneryPaint.Tin700, 1f, "M -27 27 v 17 M -20 27 v 17 M -13 27 v 17"),
    SceneryShape(null, SceneryPaint.Outline, 1.8f, "M -34 27 H 34 V 44 H -34 Z"),
    SceneryGroup("M -46 -40 H 46 A 10 10 0 0 1 56 -30 V 16 A 10 10 0 0 1 46 26 H -46 A 10 10 0 0 1 -56 16 V -30 A 10 10 0 0 1 -46 -40 Z", null, listOf(
        SceneryShape(SceneryPaint.Tin300, null, 0f, "M -56 -40 H 56 V 26 H -56 Z"),
        SceneryShape(SceneryPaint.Tin100, null, 0f, "M -56 -40 H -30 V 26 H -56 Z"),
        SceneryShape(SceneryPaint.Tin500, null, 0f, "M 24 -40 H 56 V 26 H 24 Z"),
        SceneryShape(SceneryPaint.Tin700, null, 0f, "M -56 17 H 56 V 26 H -56 Z", opacity = 0.5f),
        SceneryShape(null, SceneryPaint.Tin700, 1.1f, "M -56 -29 h 112 M -56 -18 h 112 M -56 -7 h 112 M -56 4 h 112 M -56 15 h 112", opacity = 0.7f),
    )),
    SceneryShape(null, SceneryPaint.Outline, 2.5f, "M -46 -40 H 46 A 10 10 0 0 1 56 -30 V 16 A 10 10 0 0 1 46 26 H -46 A 10 10 0 0 1 -56 16 V -30 A 10 10 0 0 1 -46 -40 Z"),
    SceneryShape(SceneryPaint.Tin500, SceneryPaint.Tin900, 1.2f, "M -7.5 -24 A 3.5 3.5 0 1 0 -0.5 -24 A 3.5 3.5 0 1 0 -7.5 -24 Z"),
    SceneryShape(SceneryPaint.Tin500, SceneryPaint.Tin900, 1.2f, "M -7.5 2 A 3.5 3.5 0 1 0 -0.5 2 A 3.5 3.5 0 1 0 -7.5 2 Z"),
))

private fun artWindsock() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Concrete300, SceneryPaint.Outline, 1.8f, "M -4.5 0 A 4.5 4.5 0 1 0 4.5 0 A 4.5 4.5 0 1 0 -4.5 0 Z"),
    SceneryShape(null, SceneryPaint.Outline, 3.2f, "M 0 0 v -27"),
    SceneryShape(null, SceneryPaint.Tin300, 1f, "M -0.5 -2 v -22"),
    SceneryGroup(null, listOf(0.97f, 0.242f, -0.242f, 0.97f, -6.29f, -0.772f), listOf(
        SceneryShape(SceneryPaint.Tangerine500, null, 0f, "M 0 -31.5 L 34 -28 L 34 -23 L 0 -20.5 Z"),
        SceneryGroup("M 0 -31.5 L 34 -28 L 34 -23 L 0 -20.5 Z", null, listOf(
            SceneryShape(SceneryPaint.Foam, null, 0f, "M 7 -33 H 14 V -19 H 7 Z"),
            SceneryShape(SceneryPaint.Foam, null, 0f, "M 21 -33 H 28 V -19 H 21 Z"),
            SceneryShape(SceneryPaint.Tangerine900, null, 0f, "M 0 -25 H 34 V -19 H 0 Z", opacity = 0.2f),
        )),
        SceneryShape(null, SceneryPaint.Outline, 2f, "M 0 -31.5 L 34 -28 L 34 -23 L 0 -20.5 Z"),
    )),
    SceneryShape(SceneryPaint.Tin300, SceneryPaint.Outline, 1.4f, "M -2.2 -26 A 2.2 2.2 0 1 0 2.2 -26 A 2.2 2.2 0 1 0 -2.2 -26 Z"),
))

private fun artCar() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M -10.5 -13 H -9.5 A 2 2 0 0 1 -7.5 -11 V -7 A 2 2 0 0 1 -9.5 -5 H -10.5 A 2 2 0 0 1 -12.5 -7 V -11 A 2 2 0 0 1 -10.5 -13 Z"),
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M -10.5 6 H -9.5 A 2 2 0 0 1 -7.5 8 V 12 A 2 2 0 0 1 -9.5 14 H -10.5 A 2 2 0 0 1 -12.5 12 V 8 A 2 2 0 0 1 -10.5 6 Z"),
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M 9.5 -13 H 10.5 A 2 2 0 0 1 12.5 -11 V -7 A 2 2 0 0 1 10.5 -5 H 9.5 A 2 2 0 0 1 7.5 -7 V -11 A 2 2 0 0 1 9.5 -13 Z"),
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M 9.5 6 H 10.5 A 2 2 0 0 1 12.5 8 V 12 A 2 2 0 0 1 10.5 14 H 9.5 A 2 2 0 0 1 7.5 12 V 8 A 2 2 0 0 1 9.5 6 Z"),
    SceneryShape(SceneryPaint.Denim500, SceneryPaint.Outline, 2.2f, "M -3 -19 H 3 A 7 7 0 0 1 10 -12 V 12 A 7 7 0 0 1 3 19 H -3 A 7 7 0 0 1 -10 12 V -12 A 7 7 0 0 1 -3 -19 Z"),
    SceneryShape(SceneryPaint.Outline, null, 0f, "M 6.5 -15 H 6.5 A 2 2 0 0 1 8.5 -13 V 13 A 2 2 0 0 1 6.5 15 H 6.5 A 2 2 0 0 1 4.5 13 V -13 A 2 2 0 0 1 6.5 -15 Z", opacity = 0.16f),
    SceneryShape(SceneryPaint.Glass, SceneryPaint.Glass700, 1.1f, "M -7 -7 q 7 -4 14 0 l -1.5 5 h -11 z"),
    SceneryShape(SceneryPaint.Glass, SceneryPaint.Glass700, 1.1f, "M -6.5 9 h 13 l 1 4 q -7.5 3 -15 0 z"),
    SceneryShape(SceneryPaint.Denim300, null, 0f, "M -3.5 -2 H 3.5 A 3 3 0 0 1 6.5 1 V 6 A 3 3 0 0 1 3.5 9 H -3.5 A 3 3 0 0 1 -6.5 6 V 1 A 3 3 0 0 1 -3.5 -2 Z"),
    SceneryShape(SceneryPaint.Beacon, null, 0f, "M -7.1 -16.5 A 1.6 1.6 0 1 0 -3.9 -16.5 A 1.6 1.6 0 1 0 -7.1 -16.5 Z"),
    SceneryShape(SceneryPaint.Beacon, null, 0f, "M 3.9 -16.5 A 1.6 1.6 0 1 0 7.1 -16.5 A 1.6 1.6 0 1 0 3.9 -16.5 Z"),
    SceneryShape(null, SceneryPaint.GlassShine, 1.4f, "M -4 -12.5 h 3", opacity = 0.8f),
))

private fun artFuelTruck() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M -11 -22 H -10 A 2 2 0 0 1 -8 -20 V -16 A 2 2 0 0 1 -10 -14 H -11 A 2 2 0 0 1 -13 -16 V -20 A 2 2 0 0 1 -11 -22 Z"),
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M -11 10 H -10 A 2 2 0 0 1 -8 12 V 17 A 2 2 0 0 1 -10 19 H -11 A 2 2 0 0 1 -13 17 V 12 A 2 2 0 0 1 -11 10 Z"),
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M 10 -22 H 11 A 2 2 0 0 1 13 -20 V -16 A 2 2 0 0 1 11 -14 H 10 A 2 2 0 0 1 8 -16 V -20 A 2 2 0 0 1 10 -22 Z"),
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M 10 10 H 11 A 2 2 0 0 1 13 12 V 17 A 2 2 0 0 1 11 19 H 10 A 2 2 0 0 1 8 17 V 12 A 2 2 0 0 1 10 10 Z"),
    SceneryShape(SceneryPaint.Tin300, null, 0f, "M -1 -11 H 1 A 10 10 0 0 1 11 -1 V 18 A 10 10 0 0 1 1 28 H -1 A 10 10 0 0 1 -11 18 V -1 A 10 10 0 0 1 -1 -11 Z"),
    SceneryGroup("M -1 -11 H 1 A 10 10 0 0 1 11 -1 V 18 A 10 10 0 0 1 1 28 H -1 A 10 10 0 0 1 -11 18 V -1 A 10 10 0 0 1 -1 -11 Z", null, listOf(
        SceneryShape(SceneryPaint.Tin100, null, 0f, "M -11 -11 H -4 V 28 H -11 Z"),
        SceneryShape(SceneryPaint.Tin500, null, 0f, "M 5 -11 H 11 V 28 H 5 Z"),
        SceneryShape(null, SceneryPaint.Tin700, 1.1f, "M -11 2 h 22 M -11 15 h 22"),
    )),
    SceneryShape(null, SceneryPaint.Outline, 2.2f, "M -1 -11 H 1 A 10 10 0 0 1 11 -1 V 18 A 10 10 0 0 1 1 28 H -1 A 10 10 0 0 1 -11 18 V -1 A 10 10 0 0 1 -1 -11 Z"),
    SceneryShape(SceneryPaint.Tin500, SceneryPaint.Tin900, 1.1f, "M -2.6 -4 A 2.6 2.6 0 1 0 2.6 -4 A 2.6 2.6 0 1 0 -2.6 -4 Z"),
    SceneryShape(SceneryPaint.Tin500, SceneryPaint.Tin900, 1.1f, "M -2.6 9 A 2.6 2.6 0 1 0 2.6 9 A 2.6 2.6 0 1 0 -2.6 9 Z"),
    SceneryShape(SceneryPaint.Tin500, SceneryPaint.Tin900, 1.1f, "M -2.6 21 A 2.6 2.6 0 1 0 2.6 21 A 2.6 2.6 0 1 0 -2.6 21 Z"),
    SceneryShape(SceneryPaint.Roof500, SceneryPaint.Outline, 2.2f, "M -6.5 -28 H 6.5 A 4 4 0 0 1 10.5 -24 V -17 A 4 4 0 0 1 6.5 -13 H -6.5 A 4 4 0 0 1 -10.5 -17 V -24 A 4 4 0 0 1 -6.5 -28 Z"),
    SceneryShape(SceneryPaint.Glass, SceneryPaint.Glass700, 1.1f, "M -7 -24 q 7 -3 14 0 l -1 4 h -12 z"),
    SceneryShape(SceneryPaint.Roof300, null, 0f, "M -4.5 -19 H 4.5 A 1.5 1.5 0 0 1 6 -17.5 V -16 A 1.5 1.5 0 0 1 4.5 -14.5 H -4.5 A 1.5 1.5 0 0 1 -6 -16 V -17.5 A 1.5 1.5 0 0 1 -4.5 -19 Z"),
))

private fun artBaggageCart() = SceneryArt(listOf(
    SceneryShape(null, SceneryPaint.Outline, 2f, "M 0 -18 v 32"),
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M -8.5 -32 H -7.5 A 2 2 0 0 1 -5.5 -30 V -28 A 2 2 0 0 1 -7.5 -26 H -8.5 A 2 2 0 0 1 -10.5 -28 V -30 A 2 2 0 0 1 -8.5 -32 Z"),
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M -8.5 -9 H -7.5 A 2 2 0 0 1 -5.5 -7 V -5 A 2 2 0 0 1 -7.5 -3 H -8.5 A 2 2 0 0 1 -10.5 -5 V -7 A 2 2 0 0 1 -8.5 -9 Z"),
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M -8.5 17 H -7.5 A 2 2 0 0 1 -5.5 19 V 21 A 2 2 0 0 1 -7.5 23 H -8.5 A 2 2 0 0 1 -10.5 21 V 19 A 2 2 0 0 1 -8.5 17 Z"),
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M 7.5 -32 H 8.5 A 2 2 0 0 1 10.5 -30 V -28 A 2 2 0 0 1 8.5 -26 H 7.5 A 2 2 0 0 1 5.5 -28 V -30 A 2 2 0 0 1 7.5 -32 Z"),
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M 7.5 -9 H 8.5 A 2 2 0 0 1 10.5 -7 V -5 A 2 2 0 0 1 8.5 -3 H 7.5 A 2 2 0 0 1 5.5 -5 V -7 A 2 2 0 0 1 7.5 -9 Z"),
    SceneryShape(SceneryPaint.Asphalt900, null, 0f, "M 7.5 17 H 8.5 A 2 2 0 0 1 10.5 19 V 21 A 2 2 0 0 1 8.5 23 H 7.5 A 2 2 0 0 1 5.5 21 V 19 A 2 2 0 0 1 7.5 17 Z"),
    SceneryShape(SceneryPaint.Roof500, SceneryPaint.Outline, 2.2f, "M -4 -34 H 4 A 4 4 0 0 1 8 -30 V -22 A 4 4 0 0 1 4 -18 H -4 A 4 4 0 0 1 -8 -22 V -30 A 4 4 0 0 1 -4 -34 Z"),
    SceneryShape(SceneryPaint.Glass, SceneryPaint.Glass700, 1.1f, "M -3.5 -31 H 3.5 A 1.5 1.5 0 0 1 5 -29.5 V -27.5 A 1.5 1.5 0 0 1 3.5 -26 H -3.5 A 1.5 1.5 0 0 1 -5 -27.5 V -29.5 A 1.5 1.5 0 0 1 -3.5 -31 Z"),
    SceneryShape(SceneryPaint.Roof300, null, 0f, "M -4 -24 H 4 A 1 1 0 0 1 5 -23 V -21 A 1 1 0 0 1 4 -20 H -4 A 1 1 0 0 1 -5 -21 V -23 A 1 1 0 0 1 -4 -24 Z"),
    SceneryShape(SceneryPaint.Tin300, SceneryPaint.Outline, 2.2f, "M -6 -12 H 6 A 2 2 0 0 1 8 -10 V 6 A 2 2 0 0 1 6 8 H -6 A 2 2 0 0 1 -8 6 V -10 A 2 2 0 0 1 -6 -12 Z"),
    SceneryShape(SceneryPaint.Tin500, null, 0f, "M 3.5 -10.8 H 6.8 V 6.8 H 3.5 Z"),
    SceneryShape(SceneryPaint.Wood300, SceneryPaint.Wood900, 1.1f, "M -4.5 -9.5 H 0.5 A 1 1 0 0 1 1.5 -8.5 V -4.5 A 1 1 0 0 1 0.5 -3.5 H -4.5 A 1 1 0 0 1 -5.5 -4.5 V -8.5 A 1 1 0 0 1 -4.5 -9.5 Z"),
    SceneryShape(SceneryPaint.Wood700, SceneryPaint.Wood900, 1.1f, "M -3 -1.5 H 3 A 1 1 0 0 1 4 -0.5 V 4 A 1 1 0 0 1 3 5 H -3 A 1 1 0 0 1 -4 4 V -0.5 A 1 1 0 0 1 -3 -1.5 Z"),
    SceneryShape(SceneryPaint.Tin300, SceneryPaint.Outline, 2.2f, "M -6 14 H 6 A 2 2 0 0 1 8 16 V 32 A 2 2 0 0 1 6 34 H -6 A 2 2 0 0 1 -8 32 V 16 A 2 2 0 0 1 -6 14 Z"),
    SceneryShape(SceneryPaint.Tin500, null, 0f, "M 3.5 15.2 H 6.8 V 32.8 H 3.5 Z"),
    SceneryShape(SceneryPaint.Wood500, SceneryPaint.Wood900, 1.1f, "M -4.5 16.5 H 0.5 A 1 1 0 0 1 1.5 17.5 V 21.5 A 1 1 0 0 1 0.5 22.5 H -4.5 A 1 1 0 0 1 -5.5 21.5 V 17.5 A 1 1 0 0 1 -4.5 16.5 Z"),
    SceneryShape(SceneryPaint.Wood300, SceneryPaint.Wood900, 1.1f, "M -3 24.5 H 3 A 1 1 0 0 1 4 25.5 V 30 A 1 1 0 0 1 3 31 H -3 A 1 1 0 0 1 -4 30 V 25.5 A 1 1 0 0 1 -3 24.5 Z"),
))

private fun artParkedAircraft() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Tin500, SceneryPaint.Outline, 2.2f, "M -8 22 H 8 A 4 4 0 0 1 12 26 V 26 A 4 4 0 0 1 8 30 H -8 A 4 4 0 0 1 -12 26 V 26 A 4 4 0 0 1 -8 22 Z"),
    SceneryShape(SceneryPaint.Tin500, SceneryPaint.Outline, 2.2f, "M -24 -12 H 24 A 6 6 0 0 1 30 -6 V -5 A 6 6 0 0 1 24 1 H -24 A 6 6 0 0 1 -30 -5 V -6 A 6 6 0 0 1 -24 -12 Z"),
    SceneryShape(SceneryPaint.Tin300, null, 0f, "M -28 -10.5 H -2 V -6.5 H -28 Z"),
    SceneryShape(SceneryPaint.Tin100, SceneryPaint.Outline, 2.2f, "M 0 -29 c 5 0 6 10 6 20 l -2 32 h -8 l -2 -32 c 0 -10 1 -20 6 -20 z"),
    SceneryShape(SceneryPaint.Tin500, null, 0f, "M 2.5 -27 c 2 3 2.3 11 2.3 18 l -1.8 30 h -0.5 z"),
    SceneryShape(SceneryPaint.Glass, SceneryPaint.Glass700, 1.1f, "M -4.5 -17 q 4.5 -5 9 0 v 6 h -9 z"),
    SceneryShape(null, SceneryPaint.Outline, 2.4f, "M -9 -30 h 18"),
))

private fun artCow() = SceneryArt(listOf(
    SceneryShape(null, SceneryPaint.Outline, 2f, "M 0 17 q -1.5 6 3 9"),
    SceneryShape(SceneryPaint.Hide900, null, 0f, "M 1.1 26.5 A 2.3 2.3 0 1 0 5.7 26.5 A 2.3 2.3 0 1 0 1.1 26.5 Z"),
    SceneryShape(SceneryPaint.Hide100, SceneryPaint.Outline, 2.2f, "M -10 -8 C -12 -17 12 -17 10 -8 L 11 10 C 11 21 -11 21 -11 10 Z"),
    SceneryGroup("M -10 -8 C -12 -17 12 -17 10 -8 L 11 10 C 11 21 -11 21 -11 10 Z", null, listOf(
        SceneryShape(SceneryPaint.Hide300, null, 0f, "M 5.5 -16 H 12.5 V 22 H 5.5 Z"),
        SceneryShape(SceneryPaint.Hide900, null, 0f, "M -11 -3 q 6 -6 9 0 q 1 6 -4 7 q -6 0 -5 -7 z"),
        SceneryShape(SceneryPaint.Hide900, null, 0f, "M 1 7 q 7 -3 9 4 q -1 7 -7 6 q -5 -3 -2 -10 z"),
        SceneryShape(SceneryPaint.Hide900, null, 0f, "M 4 -13 q 5 -1 7 3 l -1 3 q -5 0 -6 -6 z"),
    )),
    SceneryShape(null, SceneryPaint.Outline, 2.2f, "M -10 -8 C -12 -17 12 -17 10 -8 L 11 10 C 11 21 -11 21 -11 10 Z"),
    SceneryShape(SceneryPaint.Hide300, SceneryPaint.Outline, 1.8f, "M -12.5 -19 A 4 2.4 0 1 0 -4.5 -19 A 4 2.4 0 1 0 -12.5 -19 Z", matrix = listOf(0.951f, -0.309f, 0.309f, 0.951f, 5.455f, -3.557f)),
    SceneryShape(SceneryPaint.Hide300, SceneryPaint.Outline, 1.8f, "M 4.5 -19 A 4 2.4 0 1 0 12.5 -19 A 4 2.4 0 1 0 4.5 -19 Z", matrix = listOf(0.951f, 0.309f, -0.309f, 0.951f, -5.455f, -3.557f)),
    SceneryShape(SceneryPaint.Hide100, SceneryPaint.Outline, 2.2f, "M -6.8 -19 A 6.8 8 0 1 0 6.8 -19 A 6.8 8 0 1 0 -6.8 -19 Z"),
    SceneryShape(SceneryPaint.Hide900, null, 0f, "M 0.5 -20 q 5 -3.5 5.5 1.5 q -2 3.5 -5.5 1.5 z"),
    SceneryShape(SceneryPaint.Muzzle, SceneryPaint.Hide900, 1.1f, "M -4.6 -25 A 4.6 3 0 1 0 4.6 -25 A 4.6 3 0 1 0 -4.6 -25 Z"),
    SceneryShape(SceneryPaint.Hide900, null, 0f, "M -2.3 -25.3 A 0.7 0.7 0 1 0 -0.9 -25.3 A 0.7 0.7 0 1 0 -2.3 -25.3 Z"),
    SceneryShape(SceneryPaint.Hide900, null, 0f, "M 0.9 -25.3 A 0.7 0.7 0 1 0 2.3 -25.3 A 0.7 0.7 0 1 0 0.9 -25.3 Z"),
    SceneryShape(SceneryPaint.Outline, null, 0f, "M -4 -20.5 A 1 1 0 1 0 -2 -20.5 A 1 1 0 1 0 -4 -20.5 Z"),
    SceneryShape(SceneryPaint.Outline, null, 0f, "M 2.2 -20.5 A 1 1 0 1 0 4.2 -20.5 A 1 1 0 1 0 2.2 -20.5 Z"),
))

private fun artZebra() = SceneryArt(listOf(
    SceneryShape(null, SceneryPaint.Outline, 2f, "M 0 17 q 1 6 -2 10"),
    SceneryShape(SceneryPaint.Hide900, null, 0f, "M -4.4 27.5 A 2.2 2.2 0 1 0 0 27.5 A 2.2 2.2 0 1 0 -4.4 27.5 Z"),
    SceneryShape(SceneryPaint.Hide100, SceneryPaint.Outline, 2.2f, "M -10 -8 C -12 -17 12 -17 10 -8 L 11 10 C 11 21 -11 21 -11 10 Z"),
    SceneryGroup("M -10 -8 C -12 -17 12 -17 10 -8 L 11 10 C 11 21 -11 21 -11 10 Z", null, listOf(
        SceneryShape(SceneryPaint.Hide300, null, 0f, "M 5.5 -16 H 12.5 V 22 H 5.5 Z"),
        SceneryShape(null, SceneryPaint.Hide900, 2.4f, "M -12 -13 q 6 3 12 0 t 12 0"),
        SceneryShape(null, SceneryPaint.Hide900, 2.4f, "M -12 -8 q 6 3 12 0 t 12 0"),
        SceneryShape(null, SceneryPaint.Hide900, 2.4f, "M -12 -3 q 6 3 12 0 t 12 0"),
        SceneryShape(null, SceneryPaint.Hide900, 2.4f, "M -12 2 q 6 -3 12 0 t 12 0"),
        SceneryShape(null, SceneryPaint.Hide900, 2.4f, "M -12 7 q 6 -3 12 0 t 12 0"),
        SceneryShape(null, SceneryPaint.Hide900, 2.4f, "M -12 12 q 6 -3 12 0 t 12 0"),
        SceneryShape(null, SceneryPaint.Hide900, 2.4f, "M -12 17 q 6 -3 12 0 t 12 0"),
    )),
    SceneryShape(null, SceneryPaint.Outline, 2.2f, "M -10 -8 C -12 -17 12 -17 10 -8 L 11 10 C 11 21 -11 21 -11 10 Z"),
    SceneryShape(SceneryPaint.Hide100, SceneryPaint.Outline, 1.8f, "M -12.5 -19 A 4 2.4 0 1 0 -4.5 -19 A 4 2.4 0 1 0 -12.5 -19 Z", matrix = listOf(0.951f, -0.309f, 0.309f, 0.951f, 5.455f, -3.557f)),
    SceneryShape(SceneryPaint.Hide100, SceneryPaint.Outline, 1.8f, "M 4.5 -19 A 4 2.4 0 1 0 12.5 -19 A 4 2.4 0 1 0 4.5 -19 Z", matrix = listOf(0.951f, 0.309f, -0.309f, 0.951f, -5.455f, -3.557f)),
    SceneryShape(SceneryPaint.Hide100, SceneryPaint.Outline, 2.2f, "M -6 -17 q 0 -9 6 -14 q 6 5 6 14 q 0 5 -6 5 t -6 -5 z"),
    SceneryShape(null, SceneryPaint.Hide900, 2f, "M -5 -19 h 10 M -4 -23.5 h 8"),
    SceneryShape(SceneryPaint.Hide900, SceneryPaint.Outline, 1.4f, "M -3.2 -29.5 q 3.2 -4 6.4 0 q -3.2 3 -6.4 0 z"),
    SceneryShape(null, SceneryPaint.Hide900, 2.6f, "M 0 -15 v 8"),
    SceneryShape(SceneryPaint.Outline, null, 0f, "M -4.7 -21.5 A 0.9 0.9 0 1 0 -2.9 -21.5 A 0.9 0.9 0 1 0 -4.7 -21.5 Z"),
    SceneryShape(SceneryPaint.Outline, null, 0f, "M 2.9 -21.5 A 0.9 0.9 0 1 0 4.7 -21.5 A 0.9 0.9 0 1 0 2.9 -21.5 Z"),
))

private fun artElephant() = SceneryArt(listOf(
    SceneryShape(null, SceneryPaint.Outline, 2.2f, "M 0 28 q 2 7 -2 12"),
    SceneryShape(SceneryPaint.Stone700, null, 0f, "M -4.9 41 A 2.4 2.4 0 1 0 -0.1 41 A 2.4 2.4 0 1 0 -4.9 41 Z"),
    SceneryShape(SceneryPaint.Stone500, SceneryPaint.Outline, 2.5f, "M -17 -12 C -20 -28 20 -28 17 -12 L 18 14 C 18 32 -18 32 -18 14 Z"),
    SceneryGroup("M -17 -12 C -20 -28 20 -28 17 -12 L 18 14 C 18 32 -18 32 -18 14 Z", null, listOf(
        SceneryShape(SceneryPaint.Stone700, null, 0f, "M 9 -26 H 21 V 34 H 9 Z"),
        SceneryShape(SceneryPaint.Stone300, null, 0f, "M -15 -6 A 7 13 0 1 0 -1 -6 A 7 13 0 1 0 -15 -6 Z"),
        SceneryShape(null, SceneryPaint.Stone700, 1.2f, "M -10 14 q 5 3 10 0 M -4 22 q 5 3 10 0"),
    )),
    SceneryShape(null, SceneryPaint.Outline, 2.5f, "M -17 -12 C -20 -28 20 -28 17 -12 L 18 14 C 18 32 -18 32 -18 14 Z"),
    SceneryShape(SceneryPaint.Stone500, SceneryPaint.Outline, 2.5f, "M -9 -22 C -24 -44 -36 -24 -30 -12 C -26 -4 -14 -8 -9 -14 Z"),
    SceneryShape(SceneryPaint.Stone500, SceneryPaint.Outline, 2.5f, "M 9 -22 C 24 -44 36 -24 30 -12 C 26 -4 14 -8 9 -14 Z"),
    SceneryShape(SceneryPaint.Stone300, null, 0f, "M -12 -24 C -22 -36 -30 -24 -27 -15 C -22 -12 -15 -15 -12 -18 Z"),
    SceneryShape(SceneryPaint.Stone700, null, 0f, "M 14 -22 C 22 -34 30 -24 27 -15 C 22 -12 17 -15 14 -18 Z"),
    SceneryShape(SceneryPaint.Stone500, SceneryPaint.Outline, 2.5f, "M -10 -28 q 0 -14 10 -14 t 10 14 q 0 7 -5 9 l -1.5 -14 h -7 l -1.5 14 q -5 -2 -5 -9 z"),
    SceneryShape(SceneryPaint.Stone500, SceneryPaint.Outline, 2.5f, "M -3.5 -36 q -1 -12 1.5 -19 q 2 -3 4 0 q 2.5 7 1.5 19 z"),
    SceneryShape(null, SceneryPaint.Stone700, 1.2f, "M -2 -42 h 4 M -1.5 -47 h 3.5 M -1 -52 h 2.5"),
    SceneryShape(SceneryPaint.Wall100, SceneryPaint.Outline, 1.6f, "M -7 -38 q -3 -7 0 -12 l 2.5 1 q -1.5 5 0.5 10 z"),
    SceneryShape(SceneryPaint.Wall100, SceneryPaint.Outline, 1.6f, "M 7 -38 q 3 -7 0 -12 l -2.5 1 q 1.5 5 -0.5 10 z"),
    SceneryShape(SceneryPaint.Outline, null, 0f, "M -6.6 -29 A 1.1 1.1 0 1 0 -4.4 -29 A 1.1 1.1 0 1 0 -6.6 -29 Z"),
    SceneryShape(SceneryPaint.Outline, null, 0f, "M 4.4 -29 A 1.1 1.1 0 1 0 6.6 -29 A 1.1 1.1 0 1 0 4.4 -29 Z"),
))

private fun artFarmer() = SceneryArt(listOf(
    SceneryShape(null, SceneryPaint.Outline, 4.4f, "M 17 -16 v 26"),
    SceneryShape(null, SceneryPaint.Wood300, 2f, "M 17 -16 v 26"),
    SceneryShape(null, SceneryPaint.Outline, 1.6f, "M 13.5 -22 v 6 h 7 v -6 M 17 -22 v 6"),
    SceneryShape(SceneryPaint.Pine500, SceneryPaint.Outline, 2.2f, "M -12 2 A 12 6.5 0 1 0 12 2 A 12 6.5 0 1 0 -12 2 Z"),
    SceneryShape(SceneryPaint.Pine700, null, 0f, "M 5 -3.5 q 6 1.5 6 5.5 q 0 3 -6 4.2 z"),
    SceneryShape(SceneryPaint.Muzzle, SceneryPaint.Outline, 1.6f, "M -14.8 -1 A 2.8 2.8 0 1 0 -9.2 -1 A 2.8 2.8 0 1 0 -14.8 -1 Z"),
    SceneryShape(SceneryPaint.Muzzle, SceneryPaint.Outline, 1.6f, "M 12.2 -3 A 2.8 2.8 0 1 0 17.8 -3 A 2.8 2.8 0 1 0 12.2 -3 Z"),
    SceneryShape(SceneryPaint.Straw500, SceneryPaint.Outline, 2.2f, "M -8.5 -1 A 8.5 8.5 0 1 0 8.5 -1 A 8.5 8.5 0 1 0 -8.5 -1 Z"),
    SceneryShape(SceneryPaint.Straw300, null, 0f, "M -7.5 -2.5 A 6 6 0 1 0 4.5 -2.5 A 6 6 0 1 0 -7.5 -2.5 Z"),
    SceneryShape(SceneryPaint.Straw300, SceneryPaint.Straw700, 1.2f, "M -4 -1 A 4 4 0 1 0 4 -1 A 4 4 0 1 0 -4 -1 Z"),
))

private fun artBirdUp() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Hide300, SceneryPaint.Outline, 1.8f, "M 0 -2 q -8 -6 -15 -2 q 4 1 6 5 q 5 -1 9 2 q 4 -3 9 -2 q 2 -4 6 -5 q -7 -4 -15 2 z"),
    SceneryShape(SceneryPaint.Hide100, SceneryPaint.Outline, 1.8f, "M -3 3 q -1 -9 3 -11 q 4 2 3 11 l -3 5 z"),
    SceneryShape(SceneryPaint.Straw500, SceneryPaint.Outline, 1.2f, "M -1.3 -8 h 2.6 l -1.3 -3.2 z"),
    SceneryShape(SceneryPaint.Outline, null, 0f, "M -1.8 -5.5 A 0.6 0.6 0 1 0 -0.6 -5.5 A 0.6 0.6 0 1 0 -1.8 -5.5 Z"),
    SceneryShape(SceneryPaint.Outline, null, 0f, "M 0.6 -5.5 A 0.6 0.6 0 1 0 1.8 -5.5 A 0.6 0.6 0 1 0 0.6 -5.5 Z"),
))

private fun artBirdDown() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Hide300, SceneryPaint.Outline, 1.8f, "M 0 -2 q -6 -1 -9 5 q 3 0 5 3 q 2 -3 4 -2 q 2 -1 4 2 q 2 -3 5 -3 q -3 -6 -9 -5 z"),
    SceneryShape(SceneryPaint.Hide100, SceneryPaint.Outline, 1.8f, "M -3 3 q -1 -9 3 -11 q 4 2 3 11 l -3 5 z"),
    SceneryShape(SceneryPaint.Straw500, SceneryPaint.Outline, 1.2f, "M -1.3 -8 h 2.6 l -1.3 -3.2 z"),
    SceneryShape(SceneryPaint.Outline, null, 0f, "M -1.8 -5.5 A 0.6 0.6 0 1 0 -0.6 -5.5 A 0.6 0.6 0 1 0 -1.8 -5.5 Z"),
    SceneryShape(SceneryPaint.Outline, null, 0f, "M 0.6 -5.5 A 0.6 0.6 0 1 0 1.8 -5.5 A 0.6 0.6 0 1 0 0.6 -5.5 Z"),
))

private fun artSailboat() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Wall100, SceneryPaint.Outline, 2.2f, "M 0 -30 C 8 -22 10 -8 10 6 V 24 q 0 4 -4 4 H -6 q -4 0 -4 -4 V 6 C -10 -8 -8 -22 0 -30 Z"),
    SceneryGroup("M 0 -30 C 8 -22 10 -8 10 6 V 24 q 0 4 -4 4 H -6 q -4 0 -4 -4 V 6 C -10 -8 -8 -22 0 -30 Z", null, listOf(
        SceneryShape(SceneryPaint.Wall300, null, 0f, "M 4 -32 H 12 V 30 H 4 Z"),
    )),
    SceneryShape(null, SceneryPaint.Outline, 2.2f, "M 0 -30 C 8 -22 10 -8 10 6 V 24 q 0 4 -4 4 H -6 q -4 0 -4 -4 V 6 C -10 -8 -8 -22 0 -30 Z"),
    SceneryShape(SceneryPaint.Wood300, SceneryPaint.Wood900, 1.2f, "M 0 -22 C 5 -16 6.5 -6 6.5 5 V 21 h -13 V 5 C -6.5 -6 -5 -16 0 -22 Z"),
    SceneryShape(null, SceneryPaint.Wood700, 1f, "M -6.5 2 h 13 M -6.5 10 h 13 M 0 -20 v 40"),
    SceneryShape(SceneryPaint.Wall100, SceneryPaint.Outline, 1.8f, "M 0 -14 q 12 8 3 30 z"),
    SceneryShape(SceneryPaint.Wall300, null, 0f, "M 1.5 -9 q 8 8 2.5 20 z"),
    SceneryShape(SceneryPaint.Wood700, SceneryPaint.Outline, 1.4f, "M -2 -14 A 2 2 0 1 0 2 -14 A 2 2 0 1 0 -2 -14 Z"),
))

private fun artYacht() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Wall100, SceneryPaint.Outline, 2.5f, "M 0 -40 C 11 -30 14 -12 14 6 V 30 q 0 6 -6 6 H -8 q -6 0 -6 -6 V 6 C -14 -12 -11 -30 0 -40 Z"),
    SceneryGroup("M 0 -40 C 11 -30 14 -12 14 6 V 30 q 0 6 -6 6 H -8 q -6 0 -6 -6 V 6 C -14 -12 -11 -30 0 -40 Z", null, listOf(
        SceneryShape(SceneryPaint.Wall300, null, 0f, "M 7 -42 H 16 V 38 H 7 Z"),
        SceneryShape(SceneryPaint.Wood300, null, 0f, "M -14 22 H 14 V 38 H -14 Z"),
        SceneryShape(null, SceneryPaint.Wood700, 1f, "M -14 27 h 28 M -14 32 h 28"),
    )),
    SceneryShape(null, SceneryPaint.Outline, 2.5f, "M 0 -40 C 11 -30 14 -12 14 6 V 30 q 0 6 -6 6 H -8 q -6 0 -6 -6 V 6 C -14 -12 -11 -30 0 -40 Z"),
    SceneryShape(SceneryPaint.Tin300, SceneryPaint.Outline, 2f, "M -4 -12 H 4 A 5 5 0 0 1 9 -7 V 13 A 5 5 0 0 1 4 18 H -4 A 5 5 0 0 1 -9 13 V -7 A 5 5 0 0 1 -4 -12 Z"),
    SceneryShape(SceneryPaint.Tin500, null, 0f, "M 5.5 -10 H 6 A 1.5 1.5 0 0 1 7.5 -8.5 V 14.5 A 1.5 1.5 0 0 1 6 16 H 5.5 A 1.5 1.5 0 0 1 4 14.5 V -8.5 A 1.5 1.5 0 0 1 5.5 -10 Z"),
    SceneryShape(SceneryPaint.Glass, SceneryPaint.Glass700, 1.1f, "M -6.5 -9 q 6.5 -4 13 0 v 5 h -13 z"),
    SceneryShape(SceneryPaint.Glass, SceneryPaint.Glass700, 1.1f, "M -5 8 H 5 A 1.5 1.5 0 0 1 6.5 9.5 V 12.5 A 1.5 1.5 0 0 1 5 14 H -5 A 1.5 1.5 0 0 1 -6.5 12.5 V 9.5 A 1.5 1.5 0 0 1 -5 8 Z"),
    SceneryShape(null, SceneryPaint.GlassShine, 1.6f, "M -4 -6 l 3 -2.5"),
))

private fun artContainerVessel() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Asphalt500, SceneryPaint.Outline, 2.5f, "M -22 -70 q 22 -16 44 0 V 62 q 0 8 -8 8 H -14 q -8 0 -8 -8 Z"),
    SceneryGroup("M -22 -70 q 22 -16 44 0 V 62 q 0 8 -8 8 H -14 q -8 0 -8 -8 Z", null, listOf(
        SceneryShape(SceneryPaint.Asphalt700, null, 0f, "M 12 -80 H 24 V 72 H 12 Z"),
        SceneryShape(SceneryPaint.Asphalt300, null, 0f, "M -22 -70 q 22 -16 44 0 v 6 q -22 -14 -44 0 z"),
    )),
    SceneryShape(null, SceneryPaint.Outline, 2.5f, "M -22 -70 q 22 -16 44 0 V 62 q 0 8 -8 8 H -14 q -8 0 -8 -8 Z"),
    SceneryShape(SceneryPaint.Roof500, SceneryPaint.Outline, 1.8f, "M -16 -44 H -3 A 1.5 1.5 0 0 1 -1.5 -42.5 V -23.5 A 1.5 1.5 0 0 1 -3 -22 H -16 A 1.5 1.5 0 0 1 -17.5 -23.5 V -42.5 A 1.5 1.5 0 0 1 -16 -44 Z"),
    SceneryShape(SceneryPaint.Roof300, null, 0f, "M -16 -42.5 H -11 V -23.5 H -16 Z"),
    SceneryShape(null, SceneryPaint.Roof700, 1f, "M -9.5 -43 v 20 M -5.5 -43 v 20"),
    SceneryShape(SceneryPaint.Tin500, SceneryPaint.Outline, 1.8f, "M 3 -44 H 16 A 1.5 1.5 0 0 1 17.5 -42.5 V -23.5 A 1.5 1.5 0 0 1 16 -22 H 3 A 1.5 1.5 0 0 1 1.5 -23.5 V -42.5 A 1.5 1.5 0 0 1 3 -44 Z"),
    SceneryShape(SceneryPaint.Tin300, null, 0f, "M 3 -42.5 H 8 V -23.5 H 3 Z"),
    SceneryShape(null, SceneryPaint.Tin700, 1f, "M 9.5 -43 v 20 M 13.5 -43 v 20"),
    SceneryShape(SceneryPaint.Denim500, SceneryPaint.Outline, 1.8f, "M -16 -18 H -3 A 1.5 1.5 0 0 1 -1.5 -16.5 V 2.5 A 1.5 1.5 0 0 1 -3 4 H -16 A 1.5 1.5 0 0 1 -17.5 2.5 V -16.5 A 1.5 1.5 0 0 1 -16 -18 Z"),
    SceneryShape(SceneryPaint.Denim300, null, 0f, "M -16 -16.5 H -11 V 2.5 H -16 Z"),
    SceneryShape(null, SceneryPaint.Water700, 1f, "M -9.5 -17 v 20 M -5.5 -17 v 20"),
    SceneryShape(SceneryPaint.Tin500, SceneryPaint.Outline, 1.8f, "M 3 -18 H 16 A 1.5 1.5 0 0 1 17.5 -16.5 V 2.5 A 1.5 1.5 0 0 1 16 4 H 3 A 1.5 1.5 0 0 1 1.5 2.5 V -16.5 A 1.5 1.5 0 0 1 3 -18 Z"),
    SceneryShape(SceneryPaint.Tin100, null, 0f, "M 3 -16.5 H 8 V 2.5 H 3 Z"),
    SceneryShape(null, SceneryPaint.Tin700, 1f, "M 9.5 -17 v 20 M 13.5 -17 v 20"),
    SceneryShape(SceneryPaint.Roof500, SceneryPaint.Outline, 1.8f, "M -16 8 H -3 A 1.5 1.5 0 0 1 -1.5 9.5 V 28.5 A 1.5 1.5 0 0 1 -3 30 H -16 A 1.5 1.5 0 0 1 -17.5 28.5 V 9.5 A 1.5 1.5 0 0 1 -16 8 Z"),
    SceneryShape(SceneryPaint.Roof300, null, 0f, "M -16 9.5 H -11 V 28.5 H -16 Z"),
    SceneryShape(null, SceneryPaint.Roof700, 1f, "M -9.5 9 v 20 M -5.5 9 v 20"),
    SceneryShape(SceneryPaint.Denim500, SceneryPaint.Outline, 1.8f, "M 3 8 H 16 A 1.5 1.5 0 0 1 17.5 9.5 V 28.5 A 1.5 1.5 0 0 1 16 30 H 3 A 1.5 1.5 0 0 1 1.5 28.5 V 9.5 A 1.5 1.5 0 0 1 3 8 Z"),
    SceneryShape(SceneryPaint.Denim300, null, 0f, "M 3 9.5 H 8 V 28.5 H 3 Z"),
    SceneryShape(null, SceneryPaint.Water700, 1f, "M 9.5 9 v 20 M 13.5 9 v 20"),
    SceneryShape(SceneryPaint.Wall100, SceneryPaint.Outline, 2.2f, "M -13 38 H 13 A 3 3 0 0 1 16 41 V 57 A 3 3 0 0 1 13 60 H -13 A 3 3 0 0 1 -16 57 V 41 A 3 3 0 0 1 -13 38 Z"),
    SceneryShape(SceneryPaint.Wall300, null, 0f, "M 8 39.5 H 14.5 V 58.5 H 8 Z"),
    SceneryShape(SceneryPaint.Glass, SceneryPaint.Glass700, 1.1f, "M -10.5 41 H 10.5 A 1.5 1.5 0 0 1 12 42.5 V 44.5 A 1.5 1.5 0 0 1 10.5 46 H -10.5 A 1.5 1.5 0 0 1 -12 44.5 V 42.5 A 1.5 1.5 0 0 1 -10.5 41 Z"),
    SceneryShape(SceneryPaint.Roof500, SceneryPaint.Outline, 1.6f, "M -3.5 50 H 3.5 A 1.5 1.5 0 0 1 5 51.5 V 55.5 A 1.5 1.5 0 0 1 3.5 57 H -3.5 A 1.5 1.5 0 0 1 -5 55.5 V 51.5 A 1.5 1.5 0 0 1 -3.5 50 Z"),
))

private fun artJetty() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Wood500, SceneryPaint.Outline, 2.2f, "M -7 -44 H 7 A 2 2 0 0 1 9 -42 V 42 A 2 2 0 0 1 7 44 H -7 A 2 2 0 0 1 -9 42 V -42 A 2 2 0 0 1 -7 -44 Z"),
    SceneryGroup("M -7 -44 H 7 A 2 2 0 0 1 9 -42 V 42 A 2 2 0 0 1 7 44 H -7 A 2 2 0 0 1 -9 42 V -42 A 2 2 0 0 1 -7 -44 Z", null, listOf(
        SceneryShape(SceneryPaint.Wood300, null, 0f, "M -9 -44 H -2 V 44 H -9 Z"),
        SceneryShape(SceneryPaint.Wood700, null, 0f, "M 5 -44 H 9 V 44 H 5 Z"),
        SceneryShape(null, SceneryPaint.Wood900, 1.1f, "M -9 -33 h 18 M -9 -22 h 18 M -9 -11 h 18 M -9 0 h 18 M -9 11 h 18 M -9 22 h 18 M -9 33 h 18"),
    )),
    SceneryShape(null, SceneryPaint.Outline, 2.2f, "M -7 -44 H 7 A 2 2 0 0 1 9 -42 V 42 A 2 2 0 0 1 7 44 H -7 A 2 2 0 0 1 -9 42 V -42 A 2 2 0 0 1 -7 -44 Z"),
    SceneryShape(SceneryPaint.Wood700, SceneryPaint.Outline, 1.6f, "M -12 -40 A 3 3 0 1 0 -6 -40 A 3 3 0 1 0 -12 -40 Z"),
    SceneryShape(SceneryPaint.Wood700, SceneryPaint.Outline, 1.6f, "M 6 -40 A 3 3 0 1 0 12 -40 A 3 3 0 1 0 6 -40 Z"),
    SceneryShape(SceneryPaint.Wood700, SceneryPaint.Outline, 1.6f, "M -12 -8 A 3 3 0 1 0 -6 -8 A 3 3 0 1 0 -12 -8 Z"),
    SceneryShape(SceneryPaint.Wood700, SceneryPaint.Outline, 1.6f, "M 6 -8 A 3 3 0 1 0 12 -8 A 3 3 0 1 0 6 -8 Z"),
    SceneryShape(SceneryPaint.Wood700, SceneryPaint.Outline, 1.6f, "M -12 24 A 3 3 0 1 0 -6 24 A 3 3 0 1 0 -12 24 Z"),
    SceneryShape(SceneryPaint.Wood700, SceneryPaint.Outline, 1.6f, "M 6 24 A 3 3 0 1 0 12 24 A 3 3 0 1 0 6 24 Z"),
))

private fun artWave() = SceneryArt(listOf(
    SceneryShape(null, SceneryPaint.Foam, 2f, "M -14 2 q 7 -6 14 0 t 14 0"),
))

private fun artWake() = SceneryArt(listOf(
    SceneryShape(null, SceneryPaint.Foam, 2f, "M 0 -22 q -10 16 -14 40 M 0 -22 q 10 16 14 40"),
    SceneryShape(null, SceneryPaint.Foam, 1.5f, "M 0 -10 q -4 10 -5 22 M 0 -10 q 4 10 5 22", opacity = 0.7f),
))

private fun artCropField() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Grass700, null, 0f, "M -24 -28 H 24 A 12 12 0 0 1 36 -16 V 16 A 12 12 0 0 1 24 28 H -24 A 12 12 0 0 1 -36 16 V -16 A 12 12 0 0 1 -24 -28 Z"),
    SceneryShape(SceneryPaint.Soil500, null, 0f, "M -23 -24 H 23 A 9 9 0 0 1 32 -15 V 15 A 9 9 0 0 1 23 24 H -23 A 9 9 0 0 1 -32 15 V -15 A 9 9 0 0 1 -23 -24 Z"),
    SceneryGroup("M -23 -24 H 23 A 9 9 0 0 1 32 -15 V 15 A 9 9 0 0 1 23 24 H -23 A 9 9 0 0 1 -32 15 V -15 A 9 9 0 0 1 -23 -24 Z", null, listOf(
        SceneryShape(null, SceneryPaint.Leaf300, 4f, "M -32 -19 H 32 M -32 -10 H 32 M -32 -1 H 32 M -32 8 H 32 M -32 17 H 32"),
    )),
    SceneryShape(null, SceneryPaint.Soil700, 2f, "M -23 -24 H 23 A 9 9 0 0 1 32 -15 V 15 A 9 9 0 0 1 23 24 H -23 A 9 9 0 0 1 -32 15 V -15 A 9 9 0 0 1 -23 -24 Z"),
))

private fun artCropWheat() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Grass700, null, 0f, "M -24 -28 H 24 A 12 12 0 0 1 36 -16 V 16 A 12 12 0 0 1 24 28 H -24 A 12 12 0 0 1 -36 16 V -16 A 12 12 0 0 1 -24 -28 Z"),
    SceneryShape(SceneryPaint.Straw300, null, 0f, "M -23 -24 H 23 A 9 9 0 0 1 32 -15 V 15 A 9 9 0 0 1 23 24 H -23 A 9 9 0 0 1 -32 15 V -15 A 9 9 0 0 1 -23 -24 Z"),
    SceneryGroup("M -23 -24 H 23 A 9 9 0 0 1 32 -15 V 15 A 9 9 0 0 1 23 24 H -23 A 9 9 0 0 1 -32 15 V -15 A 9 9 0 0 1 -23 -24 Z", null, listOf(
        SceneryShape(null, SceneryPaint.Straw500, 4f, "M -32 -19 H 32 M -32 -10 H 32 M -32 -1 H 32 M -32 8 H 32 M -32 17 H 32"),
    )),
    SceneryShape(null, SceneryPaint.Straw700, 2f, "M -23 -24 H 23 A 9 9 0 0 1 32 -15 V 15 A 9 9 0 0 1 23 24 H -23 A 9 9 0 0 1 -32 15 V -15 A 9 9 0 0 1 -23 -24 Z"),
))

private fun artCropPlough() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Grass700, null, 0f, "M -24 -28 H 24 A 12 12 0 0 1 36 -16 V 16 A 12 12 0 0 1 24 28 H -24 A 12 12 0 0 1 -36 16 V -16 A 12 12 0 0 1 -24 -28 Z"),
    SceneryShape(SceneryPaint.Soil500, null, 0f, "M -23 -24 H 23 A 9 9 0 0 1 32 -15 V 15 A 9 9 0 0 1 23 24 H -23 A 9 9 0 0 1 -32 15 V -15 A 9 9 0 0 1 -23 -24 Z"),
    SceneryGroup("M -23 -24 H 23 A 9 9 0 0 1 32 -15 V 15 A 9 9 0 0 1 23 24 H -23 A 9 9 0 0 1 -32 15 V -15 A 9 9 0 0 1 -23 -24 Z", null, listOf(
        SceneryShape(null, SceneryPaint.Soil700, 2.2f, "M -32 -19 H 32 M -32 -10 H 32 M -32 -1 H 32 M -32 8 H 32 M -32 17 H 32"),
    )),
    SceneryShape(null, SceneryPaint.Soil700, 2f, "M -23 -24 H 23 A 9 9 0 0 1 32 -15 V 15 A 9 9 0 0 1 23 24 H -23 A 9 9 0 0 1 -32 15 V -15 A 9 9 0 0 1 -23 -24 Z"),
))

private fun artDetailTuft() = SceneryArt(listOf(
    SceneryShape(null, SceneryPaint.Grass700, 1.7f, "M 0 0 l -2.5 -6 M 0 0 l 0.3 -8.5 M 0 0 l 3 -6"),
))

private fun artDetailTuftLight() = SceneryArt(listOf(
    SceneryShape(null, SceneryPaint.Grass300, 1.7f, "M 0 0 l -2.5 -6 M 0 0 l 0.3 -8.5 M 0 0 l 3 -6"),
))

private fun artDetailFlower() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Wall100, null, 0f, "M -2.3 0 A 2.3 2.3 0 1 0 2.3 0 A 2.3 2.3 0 1 0 -2.3 0 Z"),
    SceneryShape(SceneryPaint.Straw500, null, 0f, "M -0.9 0 A 0.9 0.9 0 1 0 0.9 0 A 0.9 0.9 0 1 0 -0.9 0 Z"),
))

private fun artDetailFlowerPink() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Muzzle, null, 0f, "M -2.3 0 A 2.3 2.3 0 1 0 2.3 0 A 2.3 2.3 0 1 0 -2.3 0 Z"),
    SceneryShape(SceneryPaint.Wall100, null, 0f, "M -0.9 0 A 0.9 0.9 0 1 0 0.9 0 A 0.9 0.9 0 1 0 -0.9 0 Z"),
))

private fun artDetailReed() = SceneryArt(listOf(
    SceneryShape(null, SceneryPaint.Leaf700, 1.6f, "M 0 0 l -2 -11 M 0 0 l 1 -13 M 0 0 l 4 -10"),
    SceneryShape(null, SceneryPaint.Wood700, 2.6f, "M -2 -11 l -0.4 -3 M 1 -13 l 0.2 -3.5 M 4 -10 l 0.6 -3"),
))

private fun artDetailLily() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Leaf500, SceneryPaint.Leaf900, 1f, "M 0 0 L 6 -2 A 6.3 6.3 0 1 1 5 -4 Z"),
    SceneryShape(SceneryPaint.Muzzle, null, 0f, "M -3.3 1.5 A 1.8 1.8 0 1 0 0.3 1.5 A 1.8 1.8 0 1 0 -3.3 1.5 Z"),
))

private fun artDetailDuck() = SceneryArt(listOf(
    SceneryShape(null, SceneryPaint.Foam, 1.5f, "M -9 6 q 9 5 18 0 M -6 11 q 6 3 12 0"),
    SceneryShape(SceneryPaint.Wall100, SceneryPaint.Wall500, 1.5f, "M -4.2 1 A 4.2 6 0 1 0 4.2 1 A 4.2 6 0 1 0 -4.2 1 Z"),
    SceneryShape(SceneryPaint.Pine500, SceneryPaint.Pine900, 1.5f, "M -3 -5 A 3 3 0 1 0 3 -5 A 3 3 0 1 0 -3 -5 Z"),
    SceneryShape(SceneryPaint.Straw500, SceneryPaint.Straw700, 1f, "M -1.3 -8.5 h 2.6 l -1.3 -2.6 z"),
))

private fun artCloud() = SceneryArt(listOf(
    SceneryShape(SceneryPaint.Cloud, null, 0f, "M -58 20 a 22 22 0 0 1 4 -43 a 34 34 0 0 1 62 -12 a 26 26 0 0 1 44 16 a 20 20 0 0 1 4 39 z"),
    SceneryGroup("M -58 20 a 22 22 0 0 1 4 -43 a 34 34 0 0 1 62 -12 a 26 26 0 0 1 44 16 a 20 20 0 0 1 4 39 z", null, listOf(
        SceneryShape(SceneryPaint.CloudShade, null, 0f, "M -90 8 q 30 10 62 2 q 30 -8 56 2 q 24 8 50 -2 v 30 h -168 z"),
        SceneryShape(null, SceneryPaint.GlassShine, 5f, "M -30 -34 q 14 -14 30 -8"),
    )),
    SceneryShape(null, SceneryPaint.Outline, 3f, "M -58 20 a 22 22 0 0 1 4 -43 a 34 34 0 0 1 62 -12 a 26 26 0 0 1 44 16 a 20 20 0 0 1 4 39 z"),
    SceneryShape(null, SceneryPaint.CloudShade, 2.2f, "M -36 -8 q 8 -8 18 -4 M 14 -18 q 8 -6 16 -1"),
))
