package nl.hexmaster.finalapproach.domain

import kotlin.math.PI
import kotlin.math.abs

/** Day is the default; Night is for night levels. */
enum class LevelTheme { Day, Night }

/** Optional atmosphere of a level. */
data class Ambience(val clouds: Boolean = true)

/** The edges of the viewport aircraft can enter from. */
enum class SpawnEdge { Left, Top, Right, Bottom }

/** Whether a runway can be landed on from one end only or from both. */
sealed interface RunwayMode {
    /** Only landings on [heading] (radians) are accepted. */
    data class OneWay(val heading: Double) : RunwayMode

    /** Landings from either end are accepted; the direction follows the approach. */
    data object TwoWay : RunwayMode
}

/**
 * A runway. [orientation] is the heading of its axis in radians; for a one-way runway the mode
 * carries the landing heading, which lies along that axis.
 */
data class RunwayConfig(
    val id: String,
    val position: Vec2,
    val orientation: Double,
    val length: Double,
    val width: Double = LevelDefaults.RUNWAY_WIDTH,
    val mode: RunwayMode = RunwayMode.TwoWay,
) {
    val shape: OrientedRect get() = OrientedRect(position, orientation, length, width)

    /** The headings an airplane may land on. */
    val landingHeadings: List<Double>
        get() = when (mode) {
            is RunwayMode.OneWay -> listOf(Angles.normalize(mode.heading))
            RunwayMode.TwoWay -> listOf(Angles.normalize(orientation), Angles.normalize(orientation + PI))
        }

    /**
     * True when an approach on [approachHeading] is within [tolerance] of a landing heading.
     * A two-way runway accepts either end.
     */
    fun acceptsHeading(approachHeading: Double, tolerance: Double = LevelDefaults.LANDING_TOLERANCE): Boolean =
        landingHeading(approachHeading, tolerance) != null

    /** The landing heading an approach matches, or null. */
    fun landingHeading(approachHeading: Double, tolerance: Double = LevelDefaults.LANDING_TOLERANCE): Double? =
        landingHeadings.firstOrNull { abs(Angles.difference(it, approachHeading)) <= tolerance }

    /** Runway number painted at the end an airplane on [heading] lands from: heading / 10 degrees. */
    fun numberFor(heading: Double): String {
        val degrees = Math360.degreesOf(heading)
        val number = ((degrees + 5.0) / 10.0).toInt().let { if (it == 0) 36 else it }
        return number.toString().padStart(2, '0')
    }
}

/** Compass degrees, 0 to 360. */
internal object Math360 {
    fun degreesOf(heading: Double): Double {
        val d = heading * 180.0 / PI % 360.0
        return if (d < 0) d + 360.0 else d
    }
}

/** A round landing spot for helicopters. [zoneRadius] overrides the level's landing zone radius. */
data class HelipadConfig(
    val id: String,
    val position: Vec2,
    val radius: Double = LevelDefaults.HELIPAD_RADIUS,
    val zoneRadius: Double? = null,
) {
    val shape: Circle get() = Circle(position, radius)
}

/**
 * How landings work in a level. Zones are invisible; [alignmentTolerance] is in radians,
 * [rolloutFraction] is a fraction of the runway length, [fadeSeconds] is how long a landed
 * airplane takes to fade out.
 */
data class LandingConfig(
    val helipadZoneRadius: Double = LevelDefaults.HELIPAD_ZONE_RADIUS,
    val runwayZoneDepth: Double = LevelDefaults.RUNWAY_ZONE_DEPTH,
    val runwayZoneWidth: Double = LevelDefaults.RUNWAY_ZONE_WIDTH,
    val alignmentTolerance: Double = LevelDefaults.LANDING_TOLERANCE,
    val rolloutFraction: Double = LevelDefaults.ROLLOUT_FRACTION,
    val fadeSeconds: Double = LevelDefaults.LANDED_FADE_SECONDS,
)

/** The world a level is played in. The viewport is the rectangle from 0,0 to [width],[height]. */
data class WorldConfig(
    val width: Double,
    val height: Double,
    val spawnMargin: Double = LevelDefaults.SPAWN_MARGIN,
    val minimumSeparation: Double = LevelDefaults.MINIMUM_SEPARATION,
) {
    val viewport: Bounds get() = Bounds(0.0, 0.0, width, height)
}

/**
 * How a run of a level goes. A null [seed] means a fresh one per run; a null [durationSeconds]
 * means endless.
 */
data class RunRules(
    val seed: Long? = null,
    val durationSeconds: Double? = null,
    val goalLandings: Int? = null,
) {
    val endless: Boolean get() = durationSeconds == null
}

/**
 * One step of the difficulty curve, active from [startSeconds] until the next phase starts.
 * Spawn intervals are in seconds; [weights] say how often each allowed type is picked.
 */
data class DifficultyPhase(
    val startSeconds: Double,
    val spawnIntervalMin: Double,
    val spawnIntervalMax: Double,
    val weights: Map<AircraftType, Int>,
    val maxAircraft: Int,
) {
    val allowedTypes: Set<AircraftType> get() = weights.keys
}

/** How and where aircraft enter. */
data class SpawnRules(
    val edges: Set<SpawnEdge> = SpawnEdge.entries.toSet(),
    val safeRegionFraction: Double = LevelDefaults.SAFE_REGION_FRACTION,
    val angleJitter: Double = LevelDefaults.ANGLE_JITTER,
    val minimumTimeOnScreen: Double = LevelDefaults.MINIMUM_TIME_ON_SCREEN,
    val minimumChordFraction: Double = LevelDefaults.MINIMUM_CHORD_FRACTION,
    val maxEntryRerolls: Int = LevelDefaults.MAX_ENTRY_REROLLS,
    val retryDelay: Double = LevelDefaults.SPAWN_RETRY_DELAY,
    val firstSpawnDelay: Double = LevelDefaults.FIRST_SPAWN_DELAY,
)

/** Lives and the factor every landing's points are multiplied by. */
data class ScoringConfig(
    val lives: Int = LevelDefaults.LIVES,
    val pointsMultiplier: Double = LevelDefaults.POINTS_MULTIPLIER,
)

/** Wind blowing towards [direction] (radians) with [strength] in world units per second. */
data class Wind(val direction: Double, val strength: Double)

/** A coach mark shown at [atSeconds] into the level. */
data class TutorialHint(val atSeconds: Double, val text: String)

/** Everything the engine, the spawner and the UI need to run one level. */
data class LevelConfig(
    val id: String,
    val name: String,
    val order: Int,
    val world: WorldConfig,
    val phases: List<DifficultyPhase>,
    val theme: LevelTheme = LevelTheme.Day,
    val ambience: Ambience = Ambience(),
    val runways: List<RunwayConfig> = emptyList(),
    val helipads: List<HelipadConfig> = emptyList(),
    val run: RunRules = RunRules(),
    val spawn: SpawnRules = SpawnRules(),
    val speedMultiplier: Double = LevelDefaults.SPEED_MULTIPLIER,
    val speedOverrides: Map<AircraftType, Double> = emptyMap(),
    val pointsOverrides: Map<AircraftType, Int> = emptyMap(),
    val scoring: ScoringConfig = ScoringConfig(),
    val landing: LandingConfig = LandingConfig(),
    val wind: Wind? = null,
    val emergencyAircraft: Boolean = false,
    val hints: List<TutorialHint> = emptyList(),
) {
    /** The phase active at [levelTime] seconds: the last one that has started. */
    fun phaseAt(levelTime: Double): DifficultyPhase =
        phases.lastOrNull { it.startSeconds <= levelTime } ?: phases.first()

    /** The level's aircraft speed and points overrides; resolve once at level start. */
    val aircraftTuning: AircraftTuning
        get() = AircraftTuning(
            speedMultiplier = speedMultiplier,
            speedOverrides = speedOverrides,
            pointsMultiplier = scoring.pointsMultiplier,
            pointsOverrides = pointsOverrides,
        )

    /** Problems with this config; empty when it is valid. */
    fun validate(): List<String> = LevelConfigValidator.validate(this)

    /** Throws [IllegalArgumentException] naming every problem when this config is invalid. */
    fun requireValid(): LevelConfig {
        val problems = validate()
        require(problems.isEmpty()) { "Level '$id' is invalid: ${problems.joinToString("; ")}" }
        return this
    }
}
