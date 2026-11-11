package nl.hexmaster.finalapproach.domain

/** What a piece of airfield decoration is; the renderer picks its look from this. */
enum class SceneryKind { Taxiway, Apron, Terminal, Tower, Hangar, Windsock, Tree }

/**
 * Decoration on the field. It has no effect on play and must stay clear of runways, helipads
 * and their landing zones.
 *
 * @param position centre, in world units
 * @param width size across, in world units
 * @param height size along [rotation], in world units
 * @param rotation radians, clockwise from up
 */
data class SceneryItem(
    val kind: SceneryKind,
    val position: Vec2,
    val width: Double,
    val height: Double,
    val rotation: Double = 0.0,
) {
    val shape: OrientedRect get() = OrientedRect(position, rotation, height, width)
}
