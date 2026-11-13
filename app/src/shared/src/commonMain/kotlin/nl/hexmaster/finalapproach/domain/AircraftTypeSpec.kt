package nl.hexmaster.finalapproach.domain

import nl.hexmaster.finalapproach.domain.CollisionShape.Companion.mirrored

/**
 * What every aircraft of one type shares. [speed] is in world units per second, [turnRate] in
 * radians per second, [radius] in world units (the sprite is drawn [spriteSize] wide). [turnRate]
 * only sets the length of the runway lead-in; it does not limit any turn.
 * [shape] is what the aircraft collides with.
 */
data class AircraftTypeSpec(
    val speed: Double,
    val turnRate: Double,
    val landingPoints: Int,
    val radius: Double,
    val shape: CollisionShape = CollisionShape.disc(radius),
) {
    /** Width and height of the drawn sprite, in world units. */
    val spriteSize: Double get() = radius * SPRITE_SIZE_PER_RADIUS

    companion object {
        /** Sprite size as a multiple of the type radius. */
        const val SPRITE_SIZE_PER_RADIUS = 2.4
    }
}

/**
 * The default spec of every aircraft type. Speeds and radii are written as their value before
 * the world zoom-out and multiplied by [LevelDefaults.WORLD_ZOOM], so every type is as large and
 * as fast on screen as before. Turn rates stay, so turning circles grow with the zoom too.
 */
object DefaultAircraftSpecs {
    val helicopter = spec(baseSpeed = 40.0, turnRate = 3.0, landingPoints = 10, baseRadius = 26.0) {
        // Rotor disc and tail rotor.
        mirrored(0.0, -4.0, 42.0) + mirrored(0.0, 38.0, 6.0)
    }
    val lightProp = spec(baseSpeed = 55.0, turnRate = 1.6, landingPoints = 20, baseRadius = 24.0) {
        // Propeller, fuselage, straight wing, tailplane.
        mirrored(0.0, -38.0, 5.0) + mirrored(0.0, -22.0, 11.0) + mirrored(0.0, 0.0, 11.0) +
            mirrored(0.0, 22.0, 9.0) + mirrored(18.0, -6.5, 8.5) + mirrored(33.5, -6.5, 8.5) +
            mirrored(12.0, 29.5, 5.5)
    }
    val turboprop = spec(baseSpeed = 75.0, turnRate = 1.2, landingPoints = 30, baseRadius = 28.0) {
        // Fuselage, long straight wing with engines, tailplane.
        mirrored(0.0, -30.0, 9.5) + mirrored(0.0, -10.0, 9.5) + mirrored(0.0, 10.0, 9.0) +
            mirrored(0.0, 30.0, 8.0) + mirrored(18.0, -2.5, 7.5) + mirrored(32.0, -2.5, 7.5) +
            mirrored(41.0, -2.5, 5.0) + mirrored(11.0, 33.0, 5.0)
    }
    val jet = spec(baseSpeed = 100.0, turnRate = 0.9, landingPoints = 50, baseRadius = 30.0) {
        // Fuselage, swept wing, tailplane.
        mirrored(0.0, -32.0, 9.0) + mirrored(0.0, -14.0, 9.0) + mirrored(0.0, 4.0, 9.0) +
            mirrored(0.0, 22.0, 8.0) + mirrored(0.0, 38.0, 7.0) + mirrored(18.0, 6.0, 8.0) +
            mirrored(32.0, 13.0, 6.0) + mirrored(12.0, 38.0, 5.0)
    }
    val jumbo = spec(baseSpeed = 130.0, turnRate = 0.6, landingPoints = 80, baseRadius = 44.0) {
        // Wide fuselage, swept wing with four engines, tailplane.
        mirrored(0.0, -32.0, 12.0) + mirrored(0.0, -10.0, 12.0) + mirrored(0.0, 12.0, 12.0) +
            mirrored(0.0, 32.0, 10.0) + mirrored(20.0, 4.0, 10.0) + mirrored(36.0, 12.0, 7.0) +
            mirrored(14.0, 39.0, 6.0)
    }

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

    /**
     * A spec whose collision shape is traced on the sprite, in sprite units around its centre.
     * [baseSpeed] and [baseRadius] are the values before the world zoom-out.
     */
    private fun spec(
        baseSpeed: Double,
        turnRate: Double,
        landingPoints: Int,
        baseRadius: Double,
        spriteCircles: () -> List<Circle>,
    ): AircraftTypeSpec {
        val speed = baseSpeed * LevelDefaults.WORLD_ZOOM
        val radius = baseRadius * LevelDefaults.WORLD_ZOOM
        val spriteSize = radius * AircraftTypeSpec.SPRITE_SIZE_PER_RADIUS
        return AircraftTypeSpec(
            speed = speed,
            turnRate = turnRate,
            landingPoints = landingPoints,
            radius = radius,
            shape = CollisionShape.fromSprite(spriteSize, spriteCircles()),
        )
    }
}