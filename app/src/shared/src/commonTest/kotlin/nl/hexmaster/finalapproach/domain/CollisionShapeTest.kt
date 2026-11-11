package nl.hexmaster.finalapproach.domain

import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CollisionShapeTest {

    private fun assertNear(expected: Vec2, actual: Vec2) =
        assertTrue(expected.distanceTo(actual) < 1e-9, "expected $expected, was $actual")

    @Test
    fun rotatedShapeCirclesMatchHandComputedPositions() {
        val shape = CollisionShape(listOf(Circle(Vec2(0.0, -10.0), 3.0), Circle(Vec2(5.0, 0.0), 2.0)))
        val position = Vec2(100.0, 50.0)

        val north = shape.at(position, 0.0)
        assertNear(Vec2(100.0, 40.0), north[0].center)
        assertNear(Vec2(105.0, 50.0), north[1].center)

        // Heading east: the nose points to +x, the right wing to +y (down on screen).
        val east = shape.at(position, PI / 2)
        assertNear(Vec2(110.0, 50.0), east[0].center)
        assertNear(Vec2(100.0, 55.0), east[1].center)
        assertEquals(listOf(3.0, 2.0), east.map { it.radius })

        val south = shape.at(position, PI)
        assertNear(Vec2(100.0, 60.0), south[0].center)
        assertNear(Vec2(95.0, 50.0), south[1].center)
    }

    @Test
    fun boundingRadiusHoldsEveryCircle() {
        val shape = CollisionShape(listOf(Circle(Vec2(0.0, -10.0), 3.0), Circle(Vec2(5.0, 0.0), 2.0)))
        assertEquals(13.0, shape.boundingRadius)
    }

    @Test
    fun defaultShapesStayWithinTheSprite() {
        DefaultAircraftSpecs.all.forEach { (type, spec) ->
            val halfDiagonal = spec.spriteSize / 2 * kotlin.math.sqrt(2.0)
            assertTrue(spec.shape.boundingRadius <= halfDiagonal, "$type shape is larger than its sprite")
            assertTrue(spec.shape.boundingRadius >= spec.spriteSize / 2 * 0.8, "$type shape is much smaller than its sprite")
        }
    }

    @Test
    fun spriteShapesAreSymmetric() {
        DefaultAircraftSpecs.all.forEach { (type, spec) ->
            val circles = spec.shape.circles
            circles.forEach { c ->
                assertTrue(
                    circles.any { abs(it.center.x + c.center.x) < 1e-9 && abs(it.center.y - c.center.y) < 1e-9 },
                    "$type has no mirror image of $c",
                )
            }
        }
    }
}
