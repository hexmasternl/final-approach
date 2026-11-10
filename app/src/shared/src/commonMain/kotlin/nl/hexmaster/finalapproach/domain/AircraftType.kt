package nl.hexmaster.finalapproach.domain

/** Whether an aircraft lands on a runway or on a helipad. */
enum class AircraftKind { Airplane, Helicopter }

/** The five aircraft of the game. */
enum class AircraftType(val kind: AircraftKind) {
    LightProp(AircraftKind.Airplane),
    Turboprop(AircraftKind.Airplane),
    Jet(AircraftKind.Airplane),
    Jumbo(AircraftKind.Airplane),
    Helicopter(AircraftKind.Helicopter),
}
