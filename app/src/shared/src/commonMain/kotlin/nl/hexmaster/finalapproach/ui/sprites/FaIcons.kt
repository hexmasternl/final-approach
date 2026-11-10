// Generated from the design system icons (components/bundle.js). Do not edit by hand.
package nl.hexmaster.finalapproach.ui.sprites

/** A filled shape of an icon; strokeWidth > 0 adds a round-joined outline in the same colour. */
internal class IconFill(val pathData: String, val strokeWidth: Float = 0f)

/** An icon on the 24-unit grid: filled shapes plus one 3-unit round stroke. */
internal class IconDef(val fills: List<IconFill> = emptyList(), val stroke: String? = null)

internal enum class FaIcon(val def: IconDef) {
    Play(IconDef(fills = listOf(IconFill("M 8.5 5.5 v 13 l 10.5 -6.5 z", 3f)))),
    Pause(IconDef(fills = listOf(IconFill("M 7.5 5 H 9 A 1.5 1.5 0 0 1 10.5 6.5 V 17.5 A 1.5 1.5 0 0 1 9 19 H 7.5 A 1.5 1.5 0 0 1 6 17.5 V 6.5 A 1.5 1.5 0 0 1 7.5 5 Z"), IconFill("M 15 5 H 16.5 A 1.5 1.5 0 0 1 18 6.5 V 17.5 A 1.5 1.5 0 0 1 16.5 19 H 15 A 1.5 1.5 0 0 1 13.5 17.5 V 6.5 A 1.5 1.5 0 0 1 15 5 Z")))),
    Restart(IconDef(stroke = "M 19 12 a 7 7 0 1 1 -2.6 -5.4 M 17.5 2.8 v 4.7 h -4.7")),
    Home(IconDef(stroke = "M 3.5 11.5 L 12 4 l 8.5 7.5 M 6 10 v 9.5 h 12 V 10")),
    SoundOn(IconDef(fills = listOf(IconFill("M 4 9.5 h 3.5 L 12 6 v 12 l -4.5 -3.5 H 4 z", 2f)), stroke = "M 15.5 9.5 a 4 4 0 0 1 0 5 M 18.2 7 a 7.5 7.5 0 0 1 0 10")),
    SoundOff(IconDef(fills = listOf(IconFill("M 4 9.5 h 3.5 L 12 6 v 12 l -4.5 -3.5 H 4 z", 2f)), stroke = "M 16 9.5 l 5 5 M 21 9.5 l -5 5")),
    Music(IconDef(fills = listOf(IconFill("M 3.7 18 A 2.8 2.8 0 1 0 9.3 18 A 2.8 2.8 0 1 0 3.7 18 Z"), IconFill("M 13.7 16 A 2.8 2.8 0 1 0 19.3 16 A 2.8 2.8 0 1 0 13.7 16 Z")), stroke = "M 9.3 18 V 6.5 l 10 -2 V 16")),
    Settings(IconDef(fills = listOf(IconFill("M 12.2 7 A 2.8 2.8 0 1 0 17.8 7 A 2.8 2.8 0 1 0 12.2 7 Z"), IconFill("M 6.2 17 A 2.8 2.8 0 1 0 11.8 17 A 2.8 2.8 0 1 0 6.2 17 Z")), stroke = "M 3.5 7 h 8 M 18.5 7 h 2 M 3.5 17 h 2 M 12.5 17 h 8")),
    Heart(IconDef(fills = listOf(IconFill("M 12 20 S 4 14.5 4 9 a 4.2 4.2 0 0 1 8 -1.8 A 4.2 4.2 0 0 1 20 9 c 0 5.5 -8 11 -8 11 z", 2f)))),
    Star(IconDef(fills = listOf(IconFill("M 12 3.5 l 2.6 5.4 5.9 0.8 -4.3 4.1 1 5.9 L 12 16.9 l -5.2 2.8 1 -5.9 -4.3 -4.1 5.9 -0.8 z", 2f)))),
    Bolt(IconDef(fills = listOf(IconFill("M 13 2.5 L 5 13.5 h 5.5 l -1 8 9 -12 H 13 z", 2f)))),
    Lock(IconDef(fills = listOf(IconFill("M 7.5 10.5 H 16.5 A 2.5 2.5 0 0 1 19 13 V 17.5 A 2.5 2.5 0 0 1 16.5 20 H 7.5 A 2.5 2.5 0 0 1 5 17.5 V 13 A 2.5 2.5 0 0 1 7.5 10.5 Z")), stroke = "M 8.5 10.5 V 8 a 3.5 3.5 0 0 1 7 0 v 2.5")),
    Check(IconDef(stroke = "M 5 12.5 l 4.5 4.5 L 19 7.5")),
    Close(IconDef(stroke = "M 6 6 l 12 12 M 18 6 L 6 18")),
    ArrowLeft(IconDef(stroke = "M 11 5 l -7 7 7 7 M 4.5 12 H 20")),
    ArrowRight(IconDef(stroke = "M 13 5 l 7 7 -7 7 M 19.5 12 H 4")),
    Plane(IconDef(fills = listOf(IconFill("M 12 2.5 c 1.2 0 1.8 1.6 1.8 3.5 v 3 l 7.2 4.5 v 2.2 l -7.2 -2 v 4 l 2.2 1.8 V 21 L 12 20 l -4 1 v -1.5 l 2.2 -1.8 v -4 l -7.2 2 v -2.2 L 10.2 9 V 6 c 0 -1.9 0.6 -3.5 1.8 -3.5 z", 1.5f)))),
    Helicopter(IconDef(fills = listOf(IconFill("M 8.2 11.5 A 3.8 4.8 0 1 0 15.8 11.5 A 3.8 4.8 0 1 0 8.2 11.5 Z")), stroke = "M 12 16 v 5 M 9.5 21 h 5 M 4 7.5 l 16 8 M 20 7.5 l -16 8")),
    Warning(IconDef(fills = listOf(IconFill("M 10.7 16.3 A 1.3 1.3 0 1 0 13.3 16.3 A 1.3 1.3 0 1 0 10.7 16.3 Z")), stroke = "M 12 4 l 9 15.5 H 3 z M 12 10 v 3")),
    Tap(IconDef(fills = listOf(IconFill("M 9.2 10 A 2.8 2.8 0 1 0 14.8 10 A 2.8 2.8 0 1 0 9.2 10 Z")), stroke = "M 7.5 10 a 4.5 4.5 0 0 1 9 0 M 4 10 a 8 8 0 0 1 16 0 M 12 13 v 8")),
    Wind(IconDef(stroke = "M 3 9 h 10 a 3 3 0 1 0 -3 -3 M 3 14.5 h 14 a 3 3 0 1 1 -3 3")),
}
