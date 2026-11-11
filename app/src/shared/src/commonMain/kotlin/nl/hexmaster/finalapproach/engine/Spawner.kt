package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.Angles
import nl.hexmaster.finalapproach.domain.DifficultyPhase
import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.SpawnEdge
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.min
import kotlin.random.Random

/** A new aircraft the spawner asks the engine to add, placed outside the viewport. */
data class Spawn(
    val type: AircraftType,
    val position: Vec2,
    val heading: Double,
    val edge: SpawnEdge,
)

/**
 * Decides when, what and where aircraft enter a level. Deterministic for a [seed]: it draws every
 * random number from one `Random(seed)` and never reads a clock. [speedOf] gives each type's
 * effective speed in world units per second, used by the fair entry rule.
 */
class Spawner(
    private val config: LevelConfig,
    seed: Long,
    private val speedOf: (AircraftType) -> Double,
) {
    private val random = Random(seed)
    private val rules = config.spawn
    private val viewport = config.world.viewport
    private val edges = SpawnEdge.entries.filter { it in rules.edges }

    /** Seconds until the next spawn is due; zero or below means due now. */
    var timeUntilNextSpawn: Double = rules.firstSpawnDelay
        private set

    /** The type picked for the spawn that is due, kept while that spawn waits. */
    private var pendingType: AircraftType? = null

    /**
     * Advances the spawn clock by [deltaSeconds] at level time [levelTime] and returns a spawn
     * when one is due and possible. [aircraftPositions] holds every aircraft in the level,
     * including those still outside the viewport.
     */
    fun update(deltaSeconds: Double, levelTime: Double, aircraftPositions: List<Vec2>): Spawn? {
        timeUntilNextSpawn -= deltaSeconds
        if (timeUntilNextSpawn > 0.0) return null
        val phase = config.phaseAt(levelTime)
        if (aircraftPositions.size >= phase.maxAircraft) return null

        val type = pendingType?.takeIf { it in phase.allowedTypes } ?: pickType(phase)
        val spawn = findEntry(type, aircraftPositions)
        if (spawn == null) {
            pendingType = type
            timeUntilNextSpawn = rules.retryDelay
            return null
        }
        pendingType = null
        timeUntilNextSpawn = nextInterval(phase)
        return spawn
    }

    private fun nextInterval(phase: DifficultyPhase): Double =
        if (phase.spawnIntervalMax > phase.spawnIntervalMin) {
            random.nextDouble(phase.spawnIntervalMin, phase.spawnIntervalMax)
        } else {
            phase.spawnIntervalMin
        }

    private fun pickType(phase: DifficultyPhase): AircraftType {
        val entries = AircraftType.entries.mapNotNull { type -> phase.weights[type]?.let { type to it } }
        val total = entries.sumOf { it.second }
        var roll = random.nextInt(total)
        for ((type, weight) in entries) {
            if (roll < weight) return type
            roll -= weight
        }
        return entries.last().first
    }

    private fun findEntry(type: AircraftType, aircraftPositions: List<Vec2>): Spawn? {
        repeat(rules.maxEntryRerolls + 1) {
            val candidate = candidate(type)
            if (isFair(candidate, speedOf(type)) && isSeparated(candidate.position, aircraftPositions)) {
                return candidate
            }
        }
        return null
    }

    private fun candidate(type: AircraftType): Spawn {
        val edge = edges[random.nextInt(edges.size)]
        val margin = config.world.spawnMargin
        val along = random.nextDouble()
        val position = when (edge) {
            SpawnEdge.Left -> Vec2(viewport.left - margin, viewport.top + along * viewport.height)
            SpawnEdge.Right -> Vec2(viewport.right + margin, viewport.top + along * viewport.height)
            SpawnEdge.Top -> Vec2(viewport.left + along * viewport.width, viewport.top - margin)
            SpawnEdge.Bottom -> Vec2(viewport.left + along * viewport.width, viewport.bottom + margin)
        }
        val fraction = rules.safeRegionFraction
        val center = Vec2(viewport.left + viewport.width / 2.0, viewport.top + viewport.height / 2.0)
        val target = center + Vec2(
            (random.nextDouble() - 0.5) * fraction * viewport.width,
            (random.nextDouble() - 0.5) * fraction * viewport.height,
        )
        val jitter = (random.nextDouble() * 2.0 - 1.0) * rules.angleJitter
        val heading = Angles.normalize(Angles.heading(target - position) + jitter)
        return Spawn(type, position, heading, edge)
    }

    /** The fair entry rule: a long enough chord through the viewport, crossed slowly enough. */
    internal fun isFair(spawn: Spawn, speed: Double): Boolean {
        val crossing = viewport.rayCrossing(spawn.position, Angles.direction(spawn.heading)) ?: return false
        val chord = crossing.second - crossing.first
        val minimumChord = rules.minimumChordFraction * min(viewport.width, viewport.height)
        return chord >= minimumChord && chord / speed >= rules.minimumTimeOnScreen
    }

    private fun isSeparated(position: Vec2, aircraftPositions: List<Vec2>): Boolean =
        aircraftPositions.all { it.distanceTo(position) >= config.world.spawnSeparation }
}
