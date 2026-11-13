package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.LevelDefaults

/**
 * Engine tunables that are not level data. Distances are in world units; the ones that follow
 * the player's finger are multiplied by the world zoom, so they are the same on screen as before
 * the zoom-out.
 *
 * @param minPointSpacing a drag point closer than this to the last path point is dropped; it is
 *   also the spacing of the smoothed path
 * @param smoothing how drawn paths are smoothed
 */
data class EngineConfig(
    val minPointSpacing: Double = MIN_POINT_SPACING,
    val smoothing: PathSmoothingConfig = PathSmoothingConfig(),
) {
    private companion object {
        const val MIN_POINT_SPACING = 12.0 * LevelDefaults.WORLD_ZOOM
    }
}

/**
 * Tunables for turning a drawn path into a smooth one. The aircraft follows the result exactly;
 * there is no turn limit.
 *
 * @param tolerance finger jitter within this many world units of the drawn line is removed
 *   first; a gentle path stays about this close to what was drawn
 * @param chaikinIterations corner-cutting passes over the resampled path
 */
data class PathSmoothingConfig(
    val tolerance: Double = TOLERANCE,
    val chaikinIterations: Int = CHAIKIN_ITERATIONS,
) {
    private companion object {
        const val TOLERANCE = 4.0 * LevelDefaults.WORLD_ZOOM
        const val CHAIKIN_ITERATIONS = 2
    }
}
