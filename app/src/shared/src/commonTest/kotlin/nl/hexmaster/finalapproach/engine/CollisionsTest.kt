package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.AircraftTypeSpec
import nl.hexmaster.finalapproach.domain.Circle
import nl.hexmaster.finalapproach.domain.CollisionShape
import nl.hexmaster.finalapproach.domain.DefaultAircraftSpecs
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CollisionsTest {

    private val specs = EffectiveAircraftSpecs.Defaults

    /** Every type is a stick: three circles of radius 5 along the heading, 20 apart. */
    private val sticks = EffectiveAircraftSpecs(
        DefaultAircraftSpecs.all.mapValues { (_, spec) ->
            spec.copy(shape = CollisionShape(listOf(-20.0, 0.0, 20.0).map { Circle(Vec2(0.0, it), 5.0) }))
        },
    )

    private fun jet(id: Int, x: Double, y: Double = 300.0, heading: Double = 0.0) =
        Aircraft(id, AircraftType.Jet, Vec2(x, y), heading)

    // A default jet's wing tip circle sits 24 units out with radius 4.5, so its half span is 28.5.

    @Test
    fun closeButNotTouchingIsNoCollision() {
        assertTrue(Collisions.find(listOf(jet(1, 100.0), jet(2, 160.0)), specs).isEmpty())
        assertFalse(Collisions.overlap(jet(1, 100.0), jet(2, 160.0), specs))
    }

    @Test
    fun touchingShapesCollide() {
        assertEquals(listOf(1 to 2), Collisions.find(listOf(jet(1, 100.0), jet(2, 157.0)), specs))
    }

    @Test
    fun overlappingShapesCollide() {
        assertEquals(listOf(1 to 2), Collisions.find(listOf(jet(2, 100.0), jet(1, 150.0)), specs))
    }

    @Test
    fun rotationChangesTheOutcome() {
        // Two sticks 15 apart: a gap of 5. Turned, the first reaches 20 into the other's side.
        val side = jet(2, 115.0)
        assertFalse(Collisions.overlap(jet(1, 100.0), side, sticks))
        assertTrue(Collisions.overlap(jet(1, 100.0, heading = PI / 2), side, sticks))
    }

    @Test
    fun headOnPassWithinOneStepIsDetected() {
        val west = jet(1, 400.0, heading = PI / 2)
        val east = jet(2, 200.0, heading = -PI / 2)
        val previous = mapOf(1 to Vec2(200.0, 300.0), 2 to Vec2(400.0, 300.0))
        assertFalse(Collisions.overlap(west, east, specs), "the shapes are apart at the end of the step")
        assertEquals(listOf(1 to 2), Collisions.find(listOf(west, east), specs, previous))
    }

    @Test
    fun parallelFlightInOneStepIsNoCollision() {
        val a = jet(1, 400.0, heading = PI / 2)
        val b = jet(2, 400.0, y = 400.0, heading = PI / 2)
        val previous = mapOf(1 to Vec2(200.0, 300.0), 2 to Vec2(200.0, 400.0))
        assertTrue(Collisions.find(listOf(a, b), specs, previous).isEmpty())
    }

    @Test
    fun rollingOutAircraftCollide() {
        val rolling = jet(1, 100.0, heading = PI / 2).copy(status = AircraftStatus.RollingOut(PI / 2, 100.0))
        val other = jet(2, 150.0, heading = -PI / 2).copy(status = AircraftStatus.RollingOut(-PI / 2, 100.0))
        assertEquals(listOf(1 to 2), Collisions.find(listOf(rolling, other), specs))
    }

    @Test
    fun aDefaultSpecTypeWithoutATracedShapeIsADisc() {
        val spec = AircraftTypeSpec(speed = 1.0, turnRate = 1.0, landingPoints = 1, radius = 10.0)
        assertEquals(10.0, spec.shape.boundingRadius)
    }
}
