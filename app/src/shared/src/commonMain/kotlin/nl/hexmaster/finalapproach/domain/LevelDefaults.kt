package nl.hexmaster.finalapproach.domain

/**
 * Global defaults for level tunables. A level config uses these unless it sets its own value,
 * so no such number appears as a literal in domain or engine logic.
 */
object LevelDefaults {
    /** The default set of aircraft a runway accepts: every airplane. */
    val ALL_AIRPLANES: Set<AircraftType> = AircraftType.entries.filter { it.kind == AircraftKind.Airplane }.toSet()

    /**
     * How far every level is zoomed out: a level's world is [BASE_WORLD_WIDTH] by
     * [BASE_WORLD_HEIGHT] times this. Runways, helipads, landing zones and scenery keep their
     * world sizes, so the airfield takes a smaller share of the screen. Every world-unit value
     * that belongs to an aircraft or to the player's finger is multiplied by it, so aircraft
     * look, move, warn and spawn on screen as they did before the zoom-out. At 2.5 the airfield
     * is half as large on screen as it was at the former zoom of 1.25.
     */
    const val WORLD_ZOOM = 2.5

    /** Size of the world before the zoom-out, in world units. */
    const val BASE_WORLD_WIDTH = 1280.0
    const val BASE_WORLD_HEIGHT = 720.0

    /** Size of every level's world, in world units. */
    const val WORLD_WIDTH = BASE_WORLD_WIDTH * WORLD_ZOOM
    const val WORLD_HEIGHT = BASE_WORLD_HEIGHT * WORLD_ZOOM

    /**
     * The height at the top of the world kept clear of zones for the HUD, in world units. Covers
     * the HUD on the smallest supported phone, which `HudFitTest` checks. Follows the zoom, so it
     * is as tall on screen as before.
     */
    const val HUD_BAND = 160.0 * WORLD_ZOOM

    /**
     * Closest a new aircraft may spawn to any other aircraft, centre to centre, in world units.
     * Larger than the warning distance, so no aircraft arrives already warned.
     */
    const val SPAWN_SEPARATION = 160.0 * WORLD_ZOOM

    /** Aircraft whose centres are closer than this get the proximity warning, in world units. */
    const val WARNING_DISTANCE = 150.0 * WORLD_ZOOM

    /** Longest simulation step; a longer frame is split into steps this long, in seconds. */
    const val MAX_STEP_SECONDS = 0.02

    /** How far outside the viewport aircraft appear, in world units. */
    const val SPAWN_MARGIN = 60.0 * WORLD_ZOOM

    /** How far beyond the viewport an aircraft that was inside it may fly before it has left. */
    const val LEAVE_MARGIN = 60.0 * WORLD_ZOOM

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

    /** Radius of the invisible landing zone on a helipad, in world units. */
    const val HELIPAD_ZONE_RADIUS = 44.0

    /** Depth of a runway landing zone along the runway, from the threshold, in world units. */
    const val RUNWAY_ZONE_DEPTH = 140.0

    /** Width of a runway landing zone across the runway, in world units. */
    const val RUNWAY_ZONE_WIDTH = 96.0

    /** Rollout distance after touchdown, as a fraction of the runway length. */
    const val ROLLOUT_FRACTION = 2.0 / 3.0

    /** How long a rolled-out airplane takes to fade away, in seconds. */
    const val LANDED_FADE_SECONDS = 0.5

    /** How long a helicopter takes to descend and land on its helipad, in seconds. */
    const val HELICOPTER_LANDING_SECONDS = 3.0

    /**
     * Distance from the threshold to the touchdown point on the centre line, in world units. The
     * lead-in to it is flown on the aircraft's turning circle, so it follows the zoom.
     */
    const val TOUCHDOWN_DISTANCE = 32.0 * WORLD_ZOOM

    /** How far back along the drawn path the runway lead-in starts, in turning radii. */
    const val LEAD_IN_TURNING_RADII = 2.0

    /** Length of the lead-in's Bezier handles, as a fraction of its chord. */
    const val LEAD_IN_HANDLE_FRACTION = 0.4

    /** Seconds an airplane takes after touchdown to change from its flight speed to the landing speed. */
    const val ROLLOUT_DECELERATION_SECONDS = 1.0

    /** The aircraft type whose flight speed, times [LANDING_SPEED_FACTOR], is the landing speed every airplane rolls out at. */
    val LANDING_SPEED_REFERENCE = AircraftType.LightProp

    /** The landing speed as a fraction of the reference type's flight speed. 1 keeps the full reference speed. */
    const val LANDING_SPEED_FACTOR = 0.5
}
