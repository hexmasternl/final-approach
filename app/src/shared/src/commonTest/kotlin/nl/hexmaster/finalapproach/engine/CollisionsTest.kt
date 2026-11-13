package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.domain.Aircraft
import nl.hexmaster.finalapproach.domain.AircraftStatus
import nl.hexmaster.finalapproach.domain.AircraftType
import nl.hexmaster.finalapproach.domain.AircraftTypeSpec
import nl.hexmaster.finalapproach.domain.AltitudeState
import nl.hexmaster.finalapproach.domain.Circle
import nl.hexmaster.finalapproach.domain.CollisionShape
import nl.hexmaster.finalapproach.domain.DefaultAircraftSpecs
import nl.hexmaster.finalapproach.domain.EffectiveAircraftSpecs
import nl.hexmaster.finalapproach.domain.LevelDefaults
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

    // Before the world zoom a default jet's wing tip circle sat 24 units out with radius 4.5, so
    // its half span was 28.5. Shapes follow the zoom, and so do the gaps these tests leave.

    /** The x of a second jet [gap] pre-zoom units to the right of one at x = 100. */
    private fun beside(gap: Double) = 100.0 + gap * LevelDefaults.WORLD_ZOOM

    @Test
    fun closeButNotTouchingIsNoCollision() {
        assertTrue(Collisions.find(listOf(jet(1, 100.0), jet(2, beside(60.0))), specs).isEmpty())
        assertFalse(Collisions.overlap(jet(1, 100.0), jet(2, beside(60.0)), specs))
    }

    @Test
    fun touchingShapesCollide() {
        assertEquals(listOf(1 to 2), Collisions.find(listOf(jet(1, 100.0), jet(2, beside(57.0))), specs))
    }

    @Test
    fun overlappingShapesCollide() {
        assertEquals(listOf(1 to 2), Collisions.find(listOf(jet(2, 100.0), jet(1, beside(50.0))), specs))
    }

    @Test
    fun onlyEqualAltitudeStatesCollide() {
        val landed = { id: Int, x: Double -> jet(id, x).copy(altitude = AltitudeState.Landed) }
        assertEquals(listOf(1 to 2), Collisions.find(listOf(landed(1, 100.0), landed(2, 150.0)), specs))
        assertTrue(Collisions.find(listOf(landed(1, 100.0), jet(2, 150.0)), specs).isEmpty())
        assertTrue(Collisions.find(listOf(jet(1, 100.0), landed(2, 150.0)), specs).isEmpty())
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
        val y = 300.0 + 80.0 * LevelDefaults.WORLD_ZOOM
        val b = jet(2, 400.0, y = y, heading = PI / 2)
        val previous = mapOf(1 to Vec2(200.0, 300.0), 2 to Vec2(200.0, y))
        assertTrue(Collisions.find(listOf(a, b), specs, previous).isEmpty())
    }

    @Test
    fun rollingOutAircraftCollide() {
        val rolling = jet(1, 100.0, heading = PI / 2).copy(status = AircraftStatus.RollingOut(PI / 2, 100.0, "r"))
        val other = jet(2, 150.0, heading = -PI / 2).copy(status = AircraftStatus.RollingOut(-PI / 2, 100.0, "r"))
        assertEquals(listOf(1 to 2), Collisions.find(listOf(rolling, other), specs))
    }

    private fun rolling(id: Int, x: Double, direction: Double, runway: String = "r", fade: Double = 0.0) =
        jet(id, x, heading = direction).copy(
            altitude = AltitudeState.Landed,
            status = AircraftStatus.RollingOut(direction, if (fade > 0.0) 0.0 else 100.0, runway, fadeElapsed = fade),
        )

    @Test
    fun sameRunwaySameDirectionRolloutsDoNotCollide() {
        assertTrue(Collisions.find(listOf(rolling(1, 100.0, PI / 2), rolling(2, 120.0, PI / 2)), specs).isEmpty())
        assertTrue(Collisions.find(listOf(rolling(1, 100.0, PI / 2), rolling(2, 120.0, PI / 2 + 0.05, fade = 0.2)), specs).isEmpty())
    }

    @Test
    fun sameRunwayOppositeDirectionRolloutsCollide() {
        assertEquals(listOf(1 to 2), Collisions.find(listOf(rolling(1, 100.0, PI / 2), rolling(2, 120.0, -PI / 2)), specs))
    }

    @Test
    fun rolloutsOnDifferentRunwaysCollide() {
        assertEquals(listOf(1 to 2), Collisions.find(listOf(rolling(1, 100.0, PI / 2), rolling(2, 120.0, PI / 2, runway = "s")), specs))
    }

    @Test
    fun aDefaultSpecTypeWithoutATracedShapeIsADisc() {
        val spec = AircraftTypeSpec(speed = 1.0, turnRate = 1.0, landingPoints = 1, radius = 10.0)
        assertEquals(10.0, spec.shape.boundingRadius)
    }
}
