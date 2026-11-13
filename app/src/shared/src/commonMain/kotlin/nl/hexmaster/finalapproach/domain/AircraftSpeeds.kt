package nl.hexmaster.finalapproach.domain

/**
 * The one place that holds the default flight speed of every aircraft type, in world units per
 * second at zoom factor 1. Each is written as the former speed times a fraction, so the change is
 * easy to read and to tweak. [DefaultAircraftSpecs] multiplies them by [LevelDefaults.WORLD_ZOOM].
 */
object AircraftSpeeds {
    const val HELICOPTER = 40.0 * (1.0 / 2.0)
    const val LIGHT_PROP = 55.0 * (2.0 / 3.0)
    const val TURBOPROP = 75.0 * (2.0 / 3.0)
    const val JET = 100.0 * (2.0 / 3.0)
    const val JUMBO = 130.0 * (1.0 / 2.0)
}
