package nl.hexmaster.finalapproach.domain

/** The invisible area a path must enter to clear an aircraft to land. */
sealed interface LandingZone {
    val id: String

    /** The aircraft kind this zone accepts. */
    val accepts: AircraftKind

    /** A round zone centred on a helipad. */
    data class Helipad(override val id: String, val area: Circle) : LandingZone {
        override val accepts: AircraftKind get() = AircraftKind.Helicopter
    }

    /**
     * A zone at the threshold of a runway: [area] starts at the threshold and runs [area].length
     * into the runway along [direction], the landing heading. [rolloutDistance] is how far an
     * airplane rolls after touchdown.
     */
    data class Runway(
        override val id: String,
        val runwayId: String,
        val area: OrientedRect,
        val direction: Double,
        val rolloutDistance: Double,
    ) : LandingZone {
        override val accepts: AircraftKind get() = AircraftKind.Airplane

        /** The unit vector along the centre line in the landing direction. */
        val along: Vec2 get() = Angles.direction(direction)

        /** Where the runway begins for this landing direction, on the centre line. */
        val threshold: Vec2 get() = area.center - along * (area.length / 2.0)

        /** The point on the centre line [distance] past the threshold. */
        fun centreLinePoint(distance: Double): Vec2 = threshold + along * distance

        /** How far past the threshold [point] lies, measured along the centre line. */
        fun distanceAlong(point: Vec2): Double = (point - threshold) dot along
    }
}

/** Builds the landing zones of a level from its runways and helipads. */
object LandingZones {

    fun of(config: LevelConfig): List<LandingZone> {
        val landing = config.landing
        val pads = config.helipads.map { pad ->
            LandingZone.Helipad(pad.id, Circle(pad.position, pad.zoneRadius ?: landing.helipadZoneRadius))
        }
        val runways = config.runways.flatMap { runway ->
            runway.landingHeadings.map { heading ->
                val along = Angles.direction(heading)
                val threshold = runway.position - along * (runway.length / 2.0)
                val center = threshold + along * (landing.runwayZoneDepth / 2.0)
                LandingZone.Runway(
                    id = "${runway.id}/${runway.numberFor(heading)}",
                    runwayId = runway.id,
                    area = OrientedRect(center, heading, landing.runwayZoneDepth, landing.runwayZoneWidth),
                    direction = heading,
                    rolloutDistance = runway.length * landing.rolloutFraction,
                )
            }
        }
        return runways + pads
    }
}
