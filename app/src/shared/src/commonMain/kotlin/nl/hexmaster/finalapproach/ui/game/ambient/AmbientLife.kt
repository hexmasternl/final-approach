package nl.hexmaster.finalapproach.ui.game.ambient

import nl.hexmaster.finalapproach.domain.Bounds
import nl.hexmaster.finalapproach.domain.Island
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.ui.theme.FaLights
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

/** Tunables of the scenery's own life. None of it touches the game engine. */
data class LevelAmbienceConfig(
    val cowCount: Int = 2,
    /** Walking speed of a cow, in world units per second. */
    val cowSpeed: Double = 8.0,
    val cowPauseMin: Double = 2.0,
    val cowPauseMax: Double = 6.0,
    /** A cow stays this far inside the fence, in world units. */
    val cowFenceInset: Double = 16.0,
    val birdGapMin: Double = 12.0,
    val birdGapMax: Double = 30.0,
    val birdSpeed: Double = 90.0,
    val boatGapMin: Double = 6.0,
    val boatGapMax: Double = 20.0,
    val lightOpacity: Float = FaLights.MAX_OPACITY,
    val beaconCycleSeconds: Double = FaLights.BEACON_CYCLE_SECONDS,
    val runningLightCycleSeconds: Double = FaLights.RUNNING_LIGHT_CYCLE_SECONDS,
) {
    /** Problems with the values; empty when gentle enough. */
    fun problems(): List<String> = buildList {
        if (beaconCycleSeconds < FaLights.MIN_CYCLE_SECONDS) add("The beacon cycle is faster than allowed")
        if (runningLightCycleSeconds < FaLights.MIN_CYCLE_SECONDS) add("The running light cycle is faster than allowed")
        if (lightOpacity > FaLights.MAX_OPACITY) add("Lights are brighter than the light opacity token")
    }
}

/** The boats that sail past the island; [length] and [beam] are in world units, [speed] per second. */
enum class BoatKind(val length: Double, val beam: Double, val speed: Double) {
    Sailboat(70.0, 24.0, 14.0),
    Yacht(90.0, 28.0, 18.0),
    ContainerVessel(170.0, 40.0, 10.0),
}

class Cow(var position: Vec2, var target: Vec2, var pause: Double)

class Bird(var position: Vec2, val velocity: Vec2)

class Boat(val kind: BoatKind, var position: Vec2, val direction: Double)

/**
 * The life around a level's airfield: cows in the paddock, a bird, a boat, the running light along
 * the runway and the tower beacon. A pure simulation driven by [update]; with [reduceMotion] nothing
 * moves, no bird or boat appears, the running lights are all lit and the beacon is steady.
 */
class AmbientLife(
    private val level: LevelConfig,
    private val config: LevelAmbienceConfig = LevelAmbienceConfig(),
    seed: Long = 0L,
    private val reduceMotion: Boolean = false,
) {
    private val random = Random(seed)
    private val world = level.world.viewport
    private var birdCountdown = nextGap(config.birdGapMin, config.birdGapMax)
    private var boatCountdown = nextGap(config.boatGapMin, config.boatGapMax)
    private var lightClock = 0.0

    val cows: List<Cow> = level.paddock?.let { paddock ->
        List(config.cowCount) {
            val start = pointIn(paddock)
            Cow(start, start, 0.0)
        }
    }.orEmpty()

    var bird: Bird? = null
        private set

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
                cow.position = cow.position + (cow.target - cow.position) * (step / distance)
            }
        }
    }

    private fun updateBird(dt: Double) {
        val current = bird
        if (current == null) {
            birdCountdown -= dt
            if (birdCountdown <= 0.0) {
                val y = world.top + random.nextDouble() * (world.bottom - world.top)
                bird = Bird(Vec2(world.left - BIRD_MARGIN, y), Vec2(config.birdSpeed, 0.0))
                birdCountdown = nextGap(config.birdGapMin, config.birdGapMax)
            }
        } else {
            current.position = current.position + current.velocity * dt
            if (current.position.x > world.right + BIRD_MARGIN) bird = null
        }
    }

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
            val gone = current.position.x < world.left - current.kind.length ||
                current.position.x > world.right + current.kind.length
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

    /** A y for a boat of [kind] that clears the island over the whole width, or null. */
    private fun boatLane(island: Island, kind: BoatKind): Double? {
        val half = kind.beam / 2
        val topMax = island.center.y - island.radiusY - half - LANE_MARGIN
        val bottomMin = island.center.y + island.radiusY + half + LANE_MARGIN
        val lanes = buildList {
            if (topMax >= world.top + half) add(world.top + half + random.nextDouble() * (topMax - world.top - half))
            if (bottomMin <= world.bottom - half) add(bottomMin + random.nextDouble() * (world.bottom - half - bottomMin))
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
        const val BIRD_MARGIN = 40.0
        const val LANE_MARGIN = 4.0
        const val BEACON_ON_FRACTION = 0.5
    }
}
