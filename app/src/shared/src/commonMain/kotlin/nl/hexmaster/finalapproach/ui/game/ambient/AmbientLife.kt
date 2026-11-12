package nl.hexmaster.finalapproach.ui.game.ambient

import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.Bounds
import nl.hexmaster.finalapproach.domain.Island
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.LevelDefaults
import nl.hexmaster.finalapproach.domain.SceneryDefaults
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.ui.theme.FaLights
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

/**
 * Tunables of the scenery's own life. None of it touches the game engine. Sizes are in world
 * units and are at least the minimum drawn sizes of the design system (`world.md`): 50 units
 * for an animal, a person or a boat, 40 for a bird's wingspan.
 */
data class LevelAmbienceConfig(
    val cowCount: Int = 3,
    /** Walking speed of a cow, in world units per second. */
    val cowSpeed: Double = 8.0,
    val cowPauseMin: Double = 2.0,
    val cowPauseMax: Double = 6.0,
    /** Nose to tail, and across, of a cow. */
    val cowLength: Double = 60.0,
    val cowWidth: Double = 36.0,
    /** A cow's centre stays this far inside the fence, so the whole cow does. */
    val cowFenceInset: Double = 36.0,
    /** Shoulder to shoulder, and front to back with the pitchfork, of the farmer. */
    val farmerWidth: Double = 50.0,
    val farmerLength: Double = 55.0,
    /** The farmer stands this far inside the paddock's top-left corner. */
    val farmerInset: Double = 44.0,
    val birdGapMin: Double = 12.0,
    val birdGapMax: Double = 30.0,
    /** Flight speed of the flock; follows the world zoom, so a crossing takes as long as before. */
    val birdSpeed: Double = 90.0 * LevelDefaults.WORLD_ZOOM,
    /** Wing tip to wing tip, and beak to tail, of a bird. */
    val birdSpan: Double = 44.0,
    val birdLength: Double = 30.0,
    /** Where the two followers fly, relative to the leader: this far behind and this far to each side. */
    val flockTrail: Double = 40.0,
    val flockSpread: Double = 34.0,
    /** How long the wings stay out, and then in. Never shorter than [MIN_WING_SECONDS]. */
    val wingSeconds: Double = 0.3,
    val boatGapMin: Double = 6.0,
    val boatGapMax: Double = 20.0,
    /** Width of a wake as a multiple of the boat's beam, and its length as a multiple of that width. */
    val wakeBeams: Double = 1.5,
    val wakeLengthPerWidth: Double = 4.0 / 3.0,
    val lightOpacity: Float = FaLights.MAX_OPACITY,
    val beaconCycleSeconds: Double = FaLights.BEACON_CYCLE_SECONDS,
    val runningLightCycleSeconds: Double = FaLights.RUNNING_LIGHT_CYCLE_SECONDS,
) {
    /** Problems with the values; empty when gentle enough. */
    fun problems(): List<String> = buildList {
        if (beaconCycleSeconds < FaLights.MIN_CYCLE_SECONDS) add("The beacon cycle is faster than allowed")
        if (runningLightCycleSeconds < FaLights.MIN_CYCLE_SECONDS) add("The running light cycle is faster than allowed")
        if (lightOpacity > FaLights.MAX_OPACITY) add("Lights are brighter than the light opacity token")
        if (wingSeconds < MIN_WING_SECONDS) add("Wings flap faster than allowed")
    }

    /** Width of the wake behind a boat of [kind], and how far it trails, in world units. */
    fun wakeWidth(kind: BoatKind): Double = kind.beam * wakeBeams

    fun wakeLength(kind: BoatKind): Double = wakeWidth(kind) * wakeLengthPerWidth

    companion object {
        /** A wing frame is shown at least this long, in seconds. */
        const val MIN_WING_SECONDS = 0.25
    }
}

/**
 * The boats that sail past the island; [length] and [beam] are in world units, the grid of each
 * boat's sprite. [speed] is in world units per second and follows the world zoom, so a boat
 * crosses the screen in the same time as before.
 */
enum class BoatKind(val length: Double, val beam: Double, val speed: Double) {
    Sailboat(76.0, 38.0, 14.0 * LevelDefaults.WORLD_ZOOM),
    Yacht(92.0, 32.0, 18.0 * LevelDefaults.WORLD_ZOOM),
    ContainerVessel(176.0, 48.0, 10.0 * LevelDefaults.WORLD_ZOOM),
}

/** A cow in the paddock; [heading] is the way it last walked, in radians. */
class Cow(var position: Vec2, var target: Vec2, var pause: Double, var heading: Double)

/** The leader of the flock. The other birds follow it in formation. */
class Bird(var position: Vec2, val velocity: Vec2)

class Boat(val kind: BoatKind, var position: Vec2, val direction: Double)

/**
 * The life around a level's airfield: cows and a farmer in the paddock, a flock of three birds,
 * a boat, the running light along the runway and the tower beacon. A pure simulation driven by
 * [update]; with [reduceMotion] nothing moves, no bird or boat appears, no wing flaps, the
 * running lights are all lit and the beacon is steady.
 */
class AmbientLife(
    private val level: LevelConfig,
    val config: LevelAmbienceConfig = LevelAmbienceConfig(),
    seed: Long = 0L,
    private val reduceMotion: Boolean = false,
) {
    private val random = Random(seed)
    private val world = level.world.viewport
    private var birdCountdown = nextGap(config.birdGapMin, config.birdGapMax)
    private var boatCountdown = nextGap(config.boatGapMin, config.boatGapMax)
    private var lightClock = 0.0
    private var wingClock = 0.0

    val cows: List<Cow> = level.paddock?.let { paddock ->
        List(config.cowCount) {
            val start = pointIn(paddock)
            Cow(start, start, 0.0, random.nextDouble() * Angles.TWO_PI)
        }
    }.orEmpty()

    /** Where the farmer stands, or null in a level without a paddock. */
    val farmer: Vec2? = level.paddock?.let { Vec2(it.left + config.farmerInset, it.top + config.farmerInset) }

    /** The leader of the flock that is crossing, or null between crossings. */
    var bird: Bird? = null
        private set

    /** Every bird of the flock that is crossing: the leader first, then its two followers behind it. */
    val flock: List<Vec2>
        get() {
            val leader = bird ?: return emptyList()
            val ahead = leader.velocity * (1.0 / leader.velocity.length)
            val aside = Vec2(-ahead.y, ahead.x)
            val behind = leader.position - ahead * config.flockTrail
            return listOf(leader.position, behind - aside * config.flockSpread, behind + aside * config.flockSpread)
        }

    /** The way the flock flies, in radians; 0 between crossings. */
    val flockHeading: Double get() = bird?.let { Angles.heading(it.velocity) } ?: 0.0

    /**
     * True while the wings of the flock are out, false while they are in. The two frames take
     * turns, each for [LevelAmbienceConfig.wingSeconds]; with reduced motion the wings stay out.
     */
    val wingsOut: Boolean
        get() = reduceMotion || (wingClock / config.wingSeconds).toInt() % 2 == 0

    var boat: Boat? = null
        private set

    /** A point inside [paddock], kept [LevelAmbienceConfig.cowFenceInset] from the fence. */
    private fun pointIn(paddock: Bounds): Vec2 {
        val inset = config.cowFenceInset
        return Vec2(
            paddock.left + inset + random.nextDouble() * (paddock.right - paddock.left - 2 * inset),
            paddock.top + inset + random.nextDouble() * (paddock.bottom - paddock.top - 2 * inset),
        )
    }

    private fun nextGap(min: Double, max: Double) = min + random.nextDouble() * (max - min)

    fun update(deltaSeconds: Double) {
        if (reduceMotion) return
        lightClock += deltaSeconds
        wingClock += deltaSeconds
        updateCows(deltaSeconds)
        updateBird(deltaSeconds)
        updateBoat(deltaSeconds)
    }

    private fun updateCows(dt: Double) {
        val paddock = level.paddock ?: return
        for (cow in cows) {
            if (cow.pause > 0.0) {
                cow.pause -= dt
                continue
            }
            val distance = cow.position.distanceTo(cow.target)
            val step = config.cowSpeed * dt
            if (distance <= step) {
                cow.position = cow.target
                cow.target = pointIn(paddock)
                cow.pause = nextGap(config.cowPauseMin, config.cowPauseMax)
            } else {
                cow.heading = Angles.heading(cow.target - cow.position)
                cow.position = cow.position + (cow.target - cow.position) * (step / distance)
            }
        }
    }

    private fun updateBird(dt: Double) {
        val current = bird
        if (current == null) {
            birdCountdown -= dt
            if (birdCountdown <= 0.0) {
                // The whole flock stays below the HUD band and inside the world.
                val reach = config.flockSpread + config.birdSpan / 2
                val top = world.top + LevelDefaults.HUD_BAND + reach
                val y = top + random.nextDouble() * (world.bottom - reach - top)
                bird = Bird(Vec2(world.left - birdMargin, y), Vec2(config.birdSpeed, 0.0))
                birdCountdown = nextGap(config.birdGapMin, config.birdGapMax)
            }
        } else {
            current.position = current.position + current.velocity * dt
            if (current.position.x > world.right + birdMargin + config.flockTrail) bird = null
        }
    }

    /** How far outside the world the leader appears, so no bird pops into view. */
    private val birdMargin: Double get() = config.birdLength

    private fun updateBoat(dt: Double) {
        val island = level.island ?: return
        val current = boat
        if (current == null) {
            boatCountdown -= dt
            if (boatCountdown <= 0.0) {
                boat = spawnBoat(island)
                boatCountdown = nextGap(config.boatGapMin, config.boatGapMax)
            }
        } else {
            current.position = current.position + Vec2(current.direction * current.kind.speed * dt, 0.0)
            // Its wake trails behind it, so the boat is gone only when the wake has left too.
            val reach = current.kind.length + config.wakeLength(current.kind)
            val gone = current.position.x < world.left - reach || current.position.x > world.right + reach
            if (gone) boat = null
        }
    }

    /** A boat sailing across on a lane in the water, or null when the kind has no room. */
    private fun spawnBoat(island: Island): Boat? {
        val kind = BoatKind.entries[random.nextInt(BoatKind.entries.size)]
        val lane = boatLane(island, kind) ?: return null
        val rightwards = random.nextBoolean()
        val startX = if (rightwards) world.left - kind.length else world.right + kind.length
        return Boat(kind, Vec2(startX, lane), if (rightwards) 1.0 else -1.0)
    }

    /**
     * A y for a boat of [kind] that clears the island and its beach over the whole width and
     * stays below the HUD band, or null.
     */
    private fun boatLane(island: Island, kind: BoatKind): Double? {
        val half = config.wakeWidth(kind) / 2
        val shore = island.radiusY + SceneryDefaults.BEACH_WIDTH
        val topMin = world.top + LevelDefaults.HUD_BAND + half
        val topMax = island.center.y - shore - half
        val bottomMin = island.center.y + shore + half
        val bottomMax = world.bottom - half
        val lanes = buildList {
            if (topMax >= topMin) add(topMin + random.nextDouble() * (topMax - topMin))
            if (bottomMax >= bottomMin) add(bottomMin + random.nextDouble() * (bottomMax - bottomMin))
        }
        return lanes.randomOrNull(random)
    }

    /** Brightness of the beacon, 0 to the light opacity token; steady when motion is reduced. */
    fun beaconOpacity(): Float {
        if (reduceMotion) return config.lightOpacity
        val phase = (lightClock / config.beaconCycleSeconds) % 1.0
        val pulse = if (phase < BEACON_ON_FRACTION) 1.0 - abs(phase / BEACON_ON_FRACTION * 2 - 1) else 0.0
        return (pulse * config.lightOpacity).toFloat()
    }

    /**
     * The index (0 until [count]) of the running light that is lit now: the sequence moves along
     * the runway and then repeats. Null means all lights are lit, which is the case under
     * reduced motion.
     */
    fun runningLightHead(count: Int): Int? {
        if (reduceMotion || count <= 0) return null
        val phase = (lightClock / config.runningLightCycleSeconds) % 1.0
        return min(count - 1, (phase * count).toInt())
    }

    val lightOpacity: Float get() = config.lightOpacity

    private companion object {
        const val BEACON_ON_FRACTION = 0.5
    }
}
