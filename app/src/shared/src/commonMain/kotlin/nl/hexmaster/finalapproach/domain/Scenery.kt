package nl.hexmaster.finalapproach.domain

/**
 * What a piece of decoration is; the renderer picks its look from this. A [ground] kind lies
 * flat (asphalt, soil, water, planks): it is drawn under the runways and may lie under the HUD
 * band. Every other kind stands on the ground and must stay out of that band.
 * A [standing] kind (a building, a tree, the windsock) shows a south wall and never rotates: its
 * sprite has `rotates` off in the design system, and the level validator rejects a rotation on it.
 */
enum class SceneryKind(val ground: Boolean = false, val standing: Boolean = false) {
    Taxiway(ground = true),
    Apron(ground = true),
    FieldWheat(ground = true),
    FieldGreen(ground = true),
    FieldPlough(ground = true),
    Track(ground = true),
    CarPark(ground = true),
    Road(ground = true),
    Pond(ground = true),
    Waterhole(ground = true),
    Jetty(ground = true),
    Terminal(standing = true),
    Tower(standing = true),
    Hangar(standing = true),
    Windsock(standing = true),
    Car,
    FuelTruck,
    BaggageCart,
    ParkedAircraft,
    Tree(standing = true),
    Oak(standing = true),
    Pine(standing = true),
    Silo(standing = true),
    HayBale,
    ForestEdge,
    Hedge,
    Fence,
    Farmhouse(standing = true),
    Barn(standing = true),
    Palm(standing = true),
    Acacia(standing = true),
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

/** Sizes of scenery, in world units. Scenery belongs to the airfield, so none of these follow the world zoom. */
object SceneryDefaults {
    /**
     * Width and height of a parked aircraft. It belongs to the airfield and shrinks with it, so it
     * fits its apron and is always much smaller than any aircraft in play, which is never taken
     * for one of them.
     */
    const val PARKED_AIRCRAFT_SIZE = 60.0

    /** Width of the beach around an island. */
    const val BEACH_WIDTH = 26.0

    /** Size of a wave, how far off the beach waves lie, and how far apart along the shore. */
    const val WAVE_WIDTH = 44.0
    const val WAVE_OFFSET = 56.0
    const val WAVE_SPACING = 150.0

    /** The trees of a forest edge overlap by this fraction of their size, and alternate sides by that one. */
    const val FOREST_OVERLAP = 0.3
    const val FOREST_STAGGER = 0.18
}
