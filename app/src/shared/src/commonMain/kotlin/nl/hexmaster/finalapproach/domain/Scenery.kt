package nl.hexmaster.finalapproach.domain

/**
 * What a piece of decoration is; the renderer picks its look from this. A [ground] kind lies
 * flat (asphalt, soil, water, planks): it is drawn under the runways and may lie under the HUD
 * band. Every other kind stands on the ground and must stay out of that band.
 */
enum class SceneryKind(val ground: Boolean = false) {
    Taxiway(ground = true),
    Apron(ground = true),
    Field(ground = true),
    Road(ground = true),
    Pond(ground = true),
    Waterhole(ground = true),
    Jetty(ground = true),
    Terminal,
    Tower,
    Hangar,
    Windsock,
    Car,
    FuelTruck,
    BaggageCart,
    ParkedAircraft,
    Tree,
    ForestEdge,
    Hedge,
    Fence,
    Farmhouse,
    Barn,
    Palm,
    Acacia,
    Bush,
    Rock,
    Zebra,
    Elephant,
}

/**
 * Decoration on the field. It has no effect on play and must stay clear of runways, helipads
 * and their landing zones.
 *
 * @param position centre, in world units
 * @param width size across, in world units
 * @param height size along [rotation], in world units
 * @param rotation radians, clockwise from up; the front of the item points this way
 */
data class SceneryItem(
    val kind: SceneryKind,
    val position: Vec2,
    val width: Double,
    val height: Double,
    val rotation: Double = 0.0,
) {
    val shape: OrientedRect get() = OrientedRect(position, rotation, height, width)

    /** True for scenery that lies flat on the ground and is drawn under the runway. */
    val isGround: Boolean get() = kind.ground
}

/**
 * Sizes of scenery, in world units. Scenery belongs to the airfield, so none of these follow the
 * world zoom, except the parked aircraft, which is measured against a flying one.
 */
object SceneryDefaults {
    /** A parked aircraft is this fraction of the flying light prop, so it is never taken for one in play. */
    const val PARKED_AIRCRAFT_SCALE = 0.75

    /** Width and height of a parked aircraft. */
    val PARKED_AIRCRAFT_SIZE: Double = DefaultAircraftSpecs.lightProp.spriteSize * PARKED_AIRCRAFT_SCALE

    /** Distance between two crop rows of a field. */
    const val FIELD_ROW_SPACING = 12.0

    /** Length of a dash of a road's centre line, and of the gap after it. */
    const val ROAD_DASH = 22.0
    const val ROAD_GAP = 18.0

    /** How far the ruts of a dirt road lie from its centre, as a fraction of its width. */
    const val ROAD_RUT_FRACTION = 0.22

    /** How far inside an apron its edge line is painted. */
    const val APRON_LINE_INSET = 8.0

    /** Width of the bank around a waterhole. */
    const val WATERHOLE_BANK = 14.0

    /** Width of the beach around an island. */
    const val BEACH_WIDTH = 26.0

    /** Size of a wave, how far off the beach waves lie, and how far apart along the shore. */
    const val WAVE_WIDTH = 44.0
    const val WAVE_OFFSET = 26.0
    const val WAVE_SPACING = 150.0

    /** The trees of a forest edge overlap by this fraction of their size, and alternate sides by that one. */
    const val FOREST_OVERLAP = 0.3
    const val FOREST_STAGGER = 0.18
}
