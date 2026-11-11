package nl.hexmaster.finalapproach.input

import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.min

/**
 * Maps the world onto a screen: scaled uniformly so the whole world fits, and centred.
 * Screen coordinates are pixels with y pointing down, like world coordinates.
 */
data class WorldViewport(
    val worldWidth: Double,
    val worldHeight: Double,
    val screenWidth: Double,
    val screenHeight: Double,
) {
    /** Pixels per world unit. */
    val scale: Double = min(screenWidth / worldWidth, screenHeight / worldHeight)

    /** Screen position of the world origin. */
    val offset: Vec2 = Vec2(
        (screenWidth - worldWidth * scale) / 2.0,
        (screenHeight - worldHeight * scale) / 2.0,
    )

    fun toScreen(world: Vec2): Vec2 = offset + world * scale

    fun toWorld(screen: Vec2): Vec2 = (screen - offset) * (1.0 / scale)

    fun toScreenLength(worldLength: Double): Double = worldLength * scale

    fun toWorldLength(screenLength: Double): Double = screenLength / scale
}
