package nl.hexmaster.finalapproach.domain

import kotlin.math.max
import kotlin.math.roundToInt

/**
 * How a level bends the default aircraft specs. A per-type override replaces the base value;
 * the multiplier then applies to the result.
 */
data class AircraftTuning(
    val speedMultiplier: Double = LevelDefaults.SPEED_MULTIPLIER,
    val speedOverrides: Map<AircraftType, Double> = emptyMap(),
    val pointsMultiplier: Double = LevelDefaults.POINTS_MULTIPLIER,
    val pointsOverrides: Map<AircraftType, Int> = emptyMap(),
) {
    /** Problems with these values, each naming the field; empty when valid. */
    fun problems(): List<String> = buildList {
        if (speedMultiplier <= 0.0) add("speedMultiplier must be greater than zero")
        if (pointsMultiplier <= 0.0) add("pointsMultiplier must be greater than zero")
        speedOverrides.filterValues { it <= 0.0 }.keys.forEach { add("speedOverrides[$it] must be greater than zero") }
        pointsOverrides.filterValues { it <= 0 }.keys.forEach { add("pointsOverrides[$it] must be greater than zero") }
    }

    /** Resolves the effective spec of every type once. Throws when the tuning is invalid. */
    fun resolve(base: Map<AircraftType, AircraftTypeSpec> = DefaultAircraftSpecs.all): EffectiveAircraftSpecs {
        val problems = problems()
        require(problems.isEmpty()) { problems.joinToString("; ") }
        val resolved = AircraftType.entries.associateWith { type ->
            val spec = base.getValue(type)
            val speed = (speedOverrides[type] ?: spec.speed) * speedMultiplier
            val points = ((pointsOverrides[type] ?: spec.landingPoints) * pointsMultiplier).roundToInt()
            spec.copy(speed = speed, landingPoints = max(MIN_POINTS, points))
        }
        return EffectiveAircraftSpecs(resolved)
    }

    private companion object {
        const val MIN_POINTS = 1
    }
}

/** The specs a run uses, resolved once at level start. Immutable for the whole run. */
class EffectiveAircraftSpecs internal constructor(private val specs: Map<AircraftType, AircraftTypeSpec>) {
    operator fun get(type: AircraftType): AircraftTypeSpec = specs.getValue(type)

    fun speedOf(type: AircraftType): Double = get(type).speed

    fun pointsOf(type: AircraftType): Int = get(type).landingPoints

    override fun equals(other: Any?): Boolean = other is EffectiveAircraftSpecs && other.specs == specs

    override fun hashCode(): Int = specs.hashCode()

    override fun toString(): String = "EffectiveAircraftSpecs($specs)"

    companion object {
        val Defaults: EffectiveAircraftSpecs = AircraftTuning().resolve()
    }
}
