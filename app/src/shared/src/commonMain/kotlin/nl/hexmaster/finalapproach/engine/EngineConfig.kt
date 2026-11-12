package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.LevelDefaults

/**
 * Engine tunables that are not level data. Distances are in world units; the ones that follow
 * the player's finger are multiplied by the world zoom, so they are the same on screen as before
 * the zoom-out.
 *
 * @param minReachRadius a path point closer than this is reached
 * @param minPointSpacing a drag point closer than this to the last path point is dropped
 * @param smoothing how drawn paths are smoothed and followed
 */
data class EngineConfig(
    val minReachRadius: Double = MIN_REACH_RADIUS,
    val minPointSpacing: Double = MIN_POINT_SPACING,
    val smoothing: PathSmoothingConfig = PathSmoothingConfig(),
) {
    private companion object {
        const val MIN_REACH_RADIUS = 6.0 * LevelDefaults.WORLD_ZOOM
        const val MIN_POINT_SPACING = 12.0 * LevelDefaults.WORLD_ZOOM
    }
}

/**
 * Tunables for turning a drawn path into a flyable one, and for following it. Lengths that scale
 * with an aircraft type are fractions of its turning radius (speed / turn rate).
 *
 * @param tolerance finger jitter within this many world units of the drawn line is removed
 *   first; a gentle path stays about this close to what was drawn
 * @param spacingTurnFraction the smoothed path has a point every this fraction of the turning
 *   radius, but never closer than the engine's minimum point spacing
 * @param chaikinIterations corner-cutting passes over the resampled path
 * @param minRadiusFactor no corner of the smoothed path is tighter than this many turning radii
 * @param flightSubsteps the smoothed path is traced by a virtual aircraft taking this many steps
 *   per point spacing
 * @param flightLengthFactor the virtual flight stops after this many times the drawn length plus
 *   one full circle; bounds the work for a path the aircraft cannot finish
 * @param lookaheadTurnFraction the aircraft steers at the point this fraction of its turning
 *   radius further along the path
 */
data class PathSmoothingConfig(
    val tolerance: Double = TOLERANCE,
    val spacingTurnFraction: Double = SPACING_TURN_FRACTION,
    val chaikinIterations: Int = CHAIKIN_ITERATIONS,
    val minRadiusFactor: Double = MIN_RADIUS_FACTOR,
    val flightSubsteps: Int = FLIGHT_SUBSTEPS,
    val flightLengthFactor: Double = FLIGHT_LENGTH_FACTOR,
    val lookaheadTurnFraction: Double = LOOKAHEAD_TURN_FRACTION,
) {
    private companion object {
        const val TOLERANCE = 4.0 * LevelDefaults.WORLD_ZOOM
        const val SPACING_TURN_FRACTION = 0.1
        const val CHAIKIN_ITERATIONS = 2
        const val MIN_RADIUS_FACTOR = 1.1
        const val FLIGHT_SUBSTEPS = 4
        const val FLIGHT_LENGTH_FACTOR = 2.0
        const val LOOKAHEAD_TURN_FRACTION = 0.25
    }
}
