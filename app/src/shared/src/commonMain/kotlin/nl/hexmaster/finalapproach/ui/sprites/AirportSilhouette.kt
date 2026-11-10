// Generated from the design system (components/bundle.js). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.sprites

/**
 * The airport silhouette of the splash page, as SVG path data on a
 * 1000 by 336 grid. The buildings stand on the line y = BASE_Y; the ground
 * fills everything below it.
 */
internal object AirportSilhouette {
    const val WIDTH = 1000f
    const val HEIGHT = 336f
    const val BASE_Y = 290f

    /** Buildings, parked aircraft and trees. The control tower is the tallest shape. */
    val shapes: List<String> = listOf(
        "M 30 290 V 276 q 0 -8 10 -8 h 88 q 10 0 10 8 v 14 z",
        "M 60 290 l 24 -58 h 16 l 12 58 z",
        "M 150 290 v -52 q 75 -52 150 0 v 52 z",
        "M 330 290 v -70 h 290 v 70 z",
        "M 360 220 v -16 h 230 v 16 z",
        "M 640 290 V 100 h 32 v 190 z",
        "M 624 100 L 614 54 h 84 l -10 46 z",
        "M 620 54 V 44 h 72 v 10 z",
        "M 653.5 44 V 10 h 5 v 34 z",
        "M 740 290 v -16 q 0 -8 10 -8 h 140 q 10 0 10 8 v 16 z",
        "M 770 290 l 20 -62 h 18 l 16 62 z",
        "M 914 268 a 26 26 0 1 0 52 0 a 26 26 0 1 0 -52 0 z",
        "M 952 276 a 20 20 0 1 0 40 0 a 20 20 0 1 0 -40 0 z",
    )

    /** The lit window of the tower cab. */
    val lights: List<String> = listOf(
        "M 630 92 l -7 -30 h 66 l -7 30 z",
    )
}
