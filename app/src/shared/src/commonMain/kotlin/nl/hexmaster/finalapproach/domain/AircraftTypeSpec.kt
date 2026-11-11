package nl.hexmaster.finalapproach.domain

/**
 * What every aircraft of one type shares. [speed] is in world units per second, [turnRate] in
 * radians per second, [radius] in world units (roughly half the sprite). [rolloutSpeedFraction]
 * is the speed on the runway after touchdown, as a fraction of [speed].
 */
data class AircraftTypeSpec(
    val speed: Double,
    val turnRate: Double,
    val landingPoints: Int,
    val radius: Double,
    val rolloutSpeedFraction: Double = LevelDefaults.ROLLOUT_SPEED_FRACTION,
)

/** The default spec of every aircraft type. */
object DefaultAircraftSpecs {
    val helicopter = AircraftTypeSpec(speed = 40.0, turnRate = 3.0, landingPoints = 10, radius = 26.0)
    val lightProp = AircraftTypeSpec(speed = 55.0, turnRate = 1.6, landingPoints = 20, radius = 24.0)
    val turboprop = AircraftTypeSpec(speed = 75.0, turnRate = 1.2, landingPoints = 30, radius = 28.0)
    val jet = AircraftTypeSpec(speed = 100.0, turnRate = 0.9, landingPoints = 50, radius = 30.0)
    val jumbo = AircraftTypeSpec(speed = 130.0, turnRate = 0.6, landingPoints = 80, radius = 44.0)

    val all: Map<AircraftType, AircraftTypeSpec> = mapOf(
        AircraftType.Helicopter to helicopter,
        AircraftType.LightProp to lightProp,
        AircraftType.Turboprop to turboprop,
        AircraftType.Jet to jet,
        AircraftType.Jumbo to jumbo,
    )

    /** Types from slowest to fastest, the order the defaults keep. */
    val speedOrder: List<AircraftType> = listOf(
        AircraftType.Helicopter,
        AircraftType.LightProp,
        AircraftType.Turboprop,
        AircraftType.Jet,
        AircraftType.Jumbo,
    )
}
