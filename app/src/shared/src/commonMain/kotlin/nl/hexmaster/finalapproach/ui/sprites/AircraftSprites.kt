// Generated from the design system sprites (components/bundle.js). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.sprites

import nl.hexmaster.finalapproach.domain.AircraftType

/** How a sprite part is painted. Colours come from the theme at draw time. */
internal enum class SpritePaint { Body, Livery, Shade, Glass, Shine, Dark, Blur, Line }

/** How a sprite part moves while the aircraft flies. */
internal enum class SpriteMotion { None, Propeller, Rotor }

/** One shape of a sprite, as SVG path data on the 96-unit sprite square (nose up). */
internal class SpritePart(
    val paint: SpritePaint,
    val pathData: String,
    val motion: SpriteMotion = SpriteMotion.None,
    val pivotX: Float = 0f,
    val pivotY: Float = 0f,
)

/** Side length of the square every sprite is drawn on. */
internal const val SPRITE_UNITS = 96f

internal val aircraftSprites: Map<AircraftType, List<SpritePart>> = mapOf(
    AircraftType.LightProp to listOf(
        SpritePart(SpritePaint.Livery, "M 35.5 72 H 60.5 A 5.5 5.5 0 0 1 66 77.5 V 77.5 A 5.5 5.5 0 0 1 60.5 83 H 35.5 A 5.5 5.5 0 0 1 30 77.5 V 77.5 A 5.5 5.5 0 0 1 35.5 72 Z"),
        SpritePart(SpritePaint.Body, "M 48 13 c 9 0 12 14 11 32 -1 16 -5 34 -11 41 -6 -7 -10 -25 -11 -41 -1 -18 2 -32 11 -32 z"),
        SpritePart(SpritePaint.Livery, "M 14.5 33 H 81.5 A 8.5 8.5 0 0 1 90 41.5 V 41.5 A 8.5 8.5 0 0 1 81.5 50 H 14.5 A 8.5 8.5 0 0 1 6 41.5 V 41.5 A 8.5 8.5 0 0 1 14.5 33 Z"),
        SpritePart(SpritePaint.Glass, "M 39.5 31 q 8.5 -9 17 0 v -6 q -8.5 -8 -17 0 z"),
        SpritePart(SpritePaint.Shine, "M 43 25.5 l 3 -2.5"),
        SpritePart(SpritePaint.Dark, "M 32 6.5 H 64 A 3 3 0 0 1 67 9.5 V 9.5 A 3 3 0 0 1 64 12.5 H 32 A 3 3 0 0 1 29 9.5 V 9.5 A 3 3 0 0 1 32 6.5 Z", SpriteMotion.Propeller, 48f, 9.5f),
        SpritePart(SpritePaint.Dark, "M 43.5 11 A 4.5 4.5 0 1 0 52.5 11 A 4.5 4.5 0 1 0 43.5 11 Z"),
    ),
    AircraftType.Turboprop to listOf(
        SpritePart(SpritePaint.Livery, "M 36 76 H 60 A 5 5 0 0 1 65 81 V 81 A 5 5 0 0 1 60 86 H 36 A 5 5 0 0 1 31 81 V 81 A 5 5 0 0 1 36 76 Z"),
        SpritePart(SpritePaint.Body, "M 10.5 38 H 85.5 A 7.5 7.5 0 0 1 93 45.5 V 45.5 A 7.5 7.5 0 0 1 85.5 53 H 10.5 A 7.5 7.5 0 0 1 3 45.5 V 45.5 A 7.5 7.5 0 0 1 10.5 38 Z"),
        SpritePart(SpritePaint.Shade, "M 24.5 27 H 24.5 A 5.5 5.5 0 0 1 30 32.5 V 45.5 A 5.5 5.5 0 0 1 24.5 51 H 24.5 A 5.5 5.5 0 0 1 19 45.5 V 32.5 A 5.5 5.5 0 0 1 24.5 27 Z"),
        SpritePart(SpritePaint.Shade, "M 71.5 27 H 71.5 A 5.5 5.5 0 0 1 77 32.5 V 45.5 A 5.5 5.5 0 0 1 71.5 51 H 71.5 A 5.5 5.5 0 0 1 66 45.5 V 32.5 A 5.5 5.5 0 0 1 71.5 27 Z"),
        SpritePart(SpritePaint.Body, "M 48 5 c 7.5 0 9.5 12 9.5 25 l -1 35 c -1 12 -4.5 21 -8.5 27 -4 -6 -7.5 -15 -8.5 -27 l -1 -35 c 0 -13 2 -25 9.5 -25 z"),
        SpritePart(SpritePaint.Livery, "M 48 30 H 48 A 3 3 0 0 1 51 33 V 67 A 3 3 0 0 1 48 70 H 48 A 3 3 0 0 1 45 67 V 33 A 3 3 0 0 1 48 30 Z"),
        SpritePart(SpritePaint.Glass, "M 41 19 q 7 -8.5 14 0 v -5.5 q -7 -7.5 -14 0 z"),
        SpritePart(SpritePaint.Shine, "M 44 14.5 l 2.5 -2"),
        SpritePart(SpritePaint.Dark, "M 15.25 23 H 33.75 A 2.75 2.75 0 0 1 36.5 25.75 V 25.75 A 2.75 2.75 0 0 1 33.75 28.5 H 15.25 A 2.75 2.75 0 0 1 12.5 25.75 V 25.75 A 2.75 2.75 0 0 1 15.25 23 Z", SpriteMotion.Propeller, 24.5f, 25.75f),
        SpritePart(SpritePaint.Dark, "M 62.25 23 H 80.75 A 2.75 2.75 0 0 1 83.5 25.75 V 25.75 A 2.75 2.75 0 0 1 80.75 28.5 H 62.25 A 2.75 2.75 0 0 1 59.5 25.75 V 25.75 A 2.75 2.75 0 0 1 62.25 23 Z", SpriteMotion.Propeller, 71.5f, 25.75f),
    ),
    AircraftType.Jet to listOf(
        SpritePart(SpritePaint.Livery, "M 48 72 l 17 13 q 2 5 -3 5 l -14 -4 -14 4 q -5 0 -3 -5 z"),
        SpritePart(SpritePaint.Livery, "M 48 32 l 39 26 q 3 7 -4 7 l -27 -7 h -16 l -27 7 q -7 0 -4 -7 z"),
        SpritePart(SpritePaint.Shade, "M 36.5 60 H 36.5 A 4.5 4.5 0 0 1 41 64.5 V 71.5 A 4.5 4.5 0 0 1 36.5 76 H 36.5 A 4.5 4.5 0 0 1 32 71.5 V 64.5 A 4.5 4.5 0 0 1 36.5 60 Z"),
        SpritePart(SpritePaint.Shade, "M 59.5 60 H 59.5 A 4.5 4.5 0 0 1 64 64.5 V 71.5 A 4.5 4.5 0 0 1 59.5 76 H 59.5 A 4.5 4.5 0 0 1 55 71.5 V 64.5 A 4.5 4.5 0 0 1 59.5 60 Z"),
        SpritePart(SpritePaint.Body, "M 48 3 c 6.5 0 8.5 12 8.5 25 l -1 42 c -1 10 -4 17 -7.5 22 -3.5 -5 -6.5 -12 -7.5 -22 l -1 -42 c 0 -13 2 -25 8.5 -25 z"),
        SpritePart(SpritePaint.Glass, "M 41.5 18 q 6.5 -8.5 13 0 v -6 q -6.5 -7.5 -13 0 z"),
        SpritePart(SpritePaint.Shine, "M 44.5 13.5 l 2.5 -2"),
    ),
    AircraftType.Jumbo to listOf(
        SpritePart(SpritePaint.Livery, "M 48 74 l 21 13 q 2 5.5 -3.5 5.5 l -17.5 -4.5 -17.5 4.5 q -5.5 0 -3.5 -5.5 z"),
        SpritePart(SpritePaint.Shade, "M 17 46 H 17 A 4 4 0 0 1 21 50 V 57 A 4 4 0 0 1 17 61 H 17 A 4 4 0 0 1 13 57 V 50 A 4 4 0 0 1 17 46 Z"),
        SpritePart(SpritePaint.Shade, "M 30 38 H 30 A 4 4 0 0 1 34 42 V 49 A 4 4 0 0 1 30 53 H 30 A 4 4 0 0 1 26 49 V 42 A 4 4 0 0 1 30 38 Z"),
        SpritePart(SpritePaint.Shade, "M 66 38 H 66 A 4 4 0 0 1 70 42 V 49 A 4 4 0 0 1 66 53 H 66 A 4 4 0 0 1 62 49 V 42 A 4 4 0 0 1 66 38 Z"),
        SpritePart(SpritePaint.Shade, "M 79 46 H 79 A 4 4 0 0 1 83 50 V 57 A 4 4 0 0 1 79 61 H 79 A 4 4 0 0 1 75 57 V 50 A 4 4 0 0 1 79 46 Z"),
        SpritePart(SpritePaint.Body, "M 48 30 l 44 27 q 3 7.5 -4 7.5 l -31 -8 h -18 l -31 8 q -7 0 -4 -7.5 z"),
        SpritePart(SpritePaint.Body, "M 48 2 c 9.5 0 12 12 12 26 l -1 42 c -1 11 -6 19 -11 24 -5 -5 -10 -13 -11 -24 l -1 -42 c 0 -14 2.5 -26 12 -26 z"),
        SpritePart(SpritePaint.Livery, "M 48 26 H 48 A 3.5 3.5 0 0 1 51.5 29.5 V 68.5 A 3.5 3.5 0 0 1 48 72 H 48 A 3.5 3.5 0 0 1 44.5 68.5 V 29.5 A 3.5 3.5 0 0 1 48 26 Z"),
        SpritePart(SpritePaint.Glass, "M 39.5 17 q 8.5 -9.5 17 0 v -6 q -8.5 -8.5 -17 0 z"),
        SpritePart(SpritePaint.Shine, "M 43 12 l 3 -2.5"),
    ),
    AircraftType.Helicopter to listOf(
        SpritePart(SpritePaint.Line, "M 31 30 v 30 M 65 30 v 30 M 31 38 h 8 M 65 38 h -8 M 31 52 h 8 M 65 52 h -8"),
        SpritePart(SpritePaint.Livery, "M 48 56 H 48 A 3.5 3.5 0 0 1 51.5 59.5 V 84.5 A 3.5 3.5 0 0 1 48 88 H 48 A 3.5 3.5 0 0 1 44.5 84.5 V 59.5 A 3.5 3.5 0 0 1 48 56 Z"),
        SpritePart(SpritePaint.Dark, "M 40.75 84 H 55.25 A 2.75 2.75 0 0 1 58 86.75 V 86.75 A 2.75 2.75 0 0 1 55.25 89.5 H 40.75 A 2.75 2.75 0 0 1 38 86.75 V 86.75 A 2.75 2.75 0 0 1 40.75 84 Z"),
        SpritePart(SpritePaint.Livery, "M 31 44 A 17 21 0 1 0 65 44 A 17 21 0 1 0 31 44 Z"),
        SpritePart(SpritePaint.Glass, "M 35 41 q -1 -15.5 13 -15.5 t 13 15.5 q -13 -6.5 -26 0 z"),
        SpritePart(SpritePaint.Shine, "M 41.5 33.5 l 3 -3"),
        SpritePart(SpritePaint.Blur, "M 6 44 A 42 42 0 1 0 90 44 A 42 42 0 1 0 6 44 Z"),
        SpritePart(SpritePaint.Dark, "M 9.5 41.5 H 86.5 A 2.5 2.5 0 0 1 89 44 V 44 A 2.5 2.5 0 0 1 86.5 46.5 H 9.5 A 2.5 2.5 0 0 1 7 44 V 44 A 2.5 2.5 0 0 1 9.5 41.5 Z", SpriteMotion.Rotor, 48f, 44f),
        SpritePart(SpritePaint.Dark, "M 48 3 H 48 A 2.5 2.5 0 0 1 50.5 5.5 V 82.5 A 2.5 2.5 0 0 1 48 85 H 48 A 2.5 2.5 0 0 1 45.5 82.5 V 5.5 A 2.5 2.5 0 0 1 48 3 Z", SpriteMotion.Rotor, 48f, 44f),
        SpritePart(SpritePaint.Dark, "M 43 44 A 5 5 0 1 0 53 44 A 5 5 0 1 0 43 44 Z"),
    ),
)
