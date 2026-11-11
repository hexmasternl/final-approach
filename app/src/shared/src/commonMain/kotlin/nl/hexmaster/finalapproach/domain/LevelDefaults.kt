package nl.hexmaster.finalapproach.domain

/**
 * Global defaults for level tunables. A level config uses these unless it sets its own value,
 * so no such number appears as a literal in domain or engine logic.
 */
object LevelDefaults {
    /** Closest two aircraft may be when one of them spawns, in world units. */
    const val MINIMUM_SEPARATION = 80.0

    /** How far outside the viewport aircraft appear, in world units. */
    const val SPAWN_MARGIN = 60.0

    /** How far an approach heading may differ from a runway heading, in radians (30 degrees). */
    const val LANDING_TOLERANCE = 0.5235987755982988

    /** Fraction of each viewport axis, centred, that spawn trajectories aim at. */
    const val SAFE_REGION_FRACTION = 0.5

    /** Largest random turn added to a spawn heading, in radians (15 degrees). */
    const val ANGLE_JITTER = 0.2617993877991494

    /** Shortest time a spawned aircraft flying straight on stays in the viewport, in seconds. */
    const val MINIMUM_TIME_ON_SCREEN = 4.0

    /** Shortest straight path through the viewport, as a fraction of its smaller side. */
    const val MINIMUM_CHORD_FRACTION = 0.5

    /** How many entries are rolled before a spawn is delayed. */
    const val MAX_ENTRY_REROLLS = 8

    /** How long a delayed spawn waits before it tries again, in seconds. */
    const val SPAWN_RETRY_DELAY = 0.5

    /** Time from the start of a run to the first spawn, in seconds. */
    const val FIRST_SPAWN_DELAY = 1.0

    /** Radius of a helipad, in world units. Matches the 112-unit pad of the design system. */
    const val HELIPAD_RADIUS = 56.0

    /** Width of a runway, in world units. Matches the 64-unit strip of the design system. */
    const val RUNWAY_WIDTH = 64.0

    const val LIVES = 3
    const val POINTS_MULTIPLIER = 1.0
    const val SPEED_MULTIPLIER = 1.0
}
