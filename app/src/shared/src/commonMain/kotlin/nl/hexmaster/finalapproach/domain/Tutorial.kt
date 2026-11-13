package nl.hexmaster.finalapproach.domain

/** Where and how a scripted tutorial aircraft enters: a point outside the viewport and a heading in radians. */
data class ScriptedEntry(val position: Vec2, val heading: Double)

/**
 * The traffic of a level's first-time tutorial: the light prop, the turboprop and the helicopter,
 * one entry each. An aircraft is well in view when its centre is at least [wellInViewInset] world
 * units inside every viewport edge.
 */
data class TutorialScript(
    val lightProp: ScriptedEntry,
    val turboprop: ScriptedEntry,
    val helicopter: ScriptedEntry,
    val wellInViewInset: Double = LevelDefaults.TUTORIAL_WELL_IN_VIEW_INSET,
) {
    /** The entries with the aircraft type each one is for. */
    val entries: List<Pair<AircraftType, ScriptedEntry>>
        get() = listOf(
            AircraftType.LightProp to lightProp,
            AircraftType.Turboprop to turboprop,
            AircraftType.Helicopter to helicopter,
        )
}
