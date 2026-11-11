package nl.hexmaster.finalapproach.domain

/**
 * The shape two aircraft collide with: a few circles in the aircraft's local frame, in world
 * units. The local frame has the nose pointing up (negative y) at heading 0; [at] turns it to a
 * heading and moves it to a position.
 */
data class CollisionShape(val circles: List<Circle>) {
    init {
        require(circles.isNotEmpty()) { "A collision shape needs at least one circle" }
    }

    /** Radius around the aircraft position that holds every circle; used for the broad phase. */
    val boundingRadius: Double = circles.maxOf { it.center.length + it.radius }

    /** The circles in world space for an aircraft at [position] flying on [heading]. */
    fun at(position: Vec2, heading: Double): List<Circle> =
        circles.map { Circle(position + it.center.rotated(heading), it.radius) }

    companion object {
        /** Side of the square the aircraft sprites are drawn on, in sprite units. */
        const val SPRITE_UNITS = 96.0

        /** One circle of [radius] around the aircraft position. */
        fun disc(radius: Double) = CollisionShape(listOf(Circle(Vec2.Zero, radius)))

        /**
         * A shape traced on a sprite. [circles] are in sprite units, relative to the sprite centre
         * (nose up); they are scaled to a sprite drawn [spriteSize] world units wide.
         */
        fun fromSprite(spriteSize: Double, circles: List<Circle>): CollisionShape {
            val scale = spriteSize / SPRITE_UNITS
            return CollisionShape(circles.map { Circle(it.center * scale, it.radius * scale) })
        }

        /** A circle on the centre line and, when [x] is not zero, its mirror image. */
        internal fun mirrored(x: Double, y: Double, radius: Double): List<Circle> =
            if (x == 0.0) {
                listOf(Circle(Vec2(0.0, y), radius))
            } else {
                listOf(Circle(Vec2(-x, y), radius), Circle(Vec2(x, y), radius))
            }
    }
}
