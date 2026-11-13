package nl.hexmaster.finalapproach.domain

/** Where the meadow's runway centre lay when the positions in the engine tests were written. */
private val WRITTEN_FOR = Vec2(640.0, 400.0)

/**
 * A point on Meadow Field given relative to its runway: [x], [y] are coordinates in the layout
 * the tests were written for, moved to where the level puts the runway now. The helipad lies at
 * the same offset from the runway as it did then.
 */
internal fun meadow(x: Double, y: Double): Vec2 =
    Vec2(x, y) + (Levels.meadowField.runways.single().position - WRITTEN_FOR)
