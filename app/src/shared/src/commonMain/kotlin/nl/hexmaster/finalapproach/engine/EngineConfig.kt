package nl.hexmaster.finalapproach.engine

/**
 * Engine tunables that are not level data. Distances are in world units.
 *
 * @param minReachRadius a path point closer than this is reached
 * @param reachTurnFraction intermediate path points within this fraction of the turning radius
 *   (speed / turn rate) are reached, so an aircraft never orbits a point it cannot turn onto
 * @param minPointSpacing a drag point closer than this to the last path point is dropped
 */
data class EngineConfig(
    val minReachRadius: Double = MIN_REACH_RADIUS,
    val reachTurnFraction: Double = REACH_TURN_FRACTION,
    val minPointSpacing: Double = MIN_POINT_SPACING,
) {
    private companion object {
        const val MIN_REACH_RADIUS = 6.0
        const val REACH_TURN_FRACTION = 0.5
        const val MIN_POINT_SPACING = 12.0
    }
}
