package nl.hexmaster.finalapproach.domain

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AircraftTypeSpecTest {

    private val specs = DefaultAircraftSpecs.all
    private val order = DefaultAircraftSpecs.speedOrder

    @Test
    fun everyTypeHasPositiveSpeedAndPoints() {
        AircraftType.entries.forEach { type ->
            val spec = specs.getValue(type)
            assertTrue(spec.speed > 0.0, "$type speed")
            assertTrue(spec.landingPoints > 0, "$type points")
        }
    }

    @Test
    fun speedIncreasesFromHelicopterToJumbo() {
        order.zipWithNext().forEach { (slow, fast) ->
            assertTrue(specs.getValue(slow).speed < specs.getValue(fast).speed, "$slow < $fast")
        }
    }

    @Test
    fun neighbouringSpeedsDifferByAtLeastFifteenPercent() {
        order.zipWithNext().forEach { (slow, fast) ->
            val ratio = specs.getValue(fast).speed / specs.getValue(slow).speed
            assertTrue(ratio >= 1.15, "$fast / $slow = $ratio")
        }
    }

    @Test
    fun speedsAreTheFormerSpeedsTimesTheWorldZoom() {
        val former = listOf(40.0, 55.0, 75.0, 100.0, 130.0)
        assertEquals(former.map { it * LevelDefaults.WORLD_ZOOM }, order.map { specs.getValue(it).speed })
        assertEquals(listOf(100.0, 137.5, 187.5, 250.0, 325.0), order.map { specs.getValue(it).speed })
    }

    @Test
    fun spritesAreTheFormerSizeTimesTheWorldZoom() {
        val formerRadius = listOf(26.0, 24.0, 28.0, 30.0, 44.0)
        order.zip(formerRadius).forEach { (type, radius) ->
            val spec = specs.getValue(type)
            assertEquals(radius * LevelDefaults.WORLD_ZOOM, spec.radius, "$type radius")
            assertEquals(radius * AircraftTypeSpec.SPRITE_SIZE_PER_RADIUS * LevelDefaults.WORLD_ZOOM, spec.spriteSize, 1e-9, "$type sprite")
        }
    }

    @Test
    fun collisionShapesGrewWithTheSprites() {
        // The light prop's wing tip circle: 33.5 sprite units out, 8.5 in radius, on a 96-unit sprite.
        val spec = specs.getValue(AircraftType.LightProp)
        val scale = spec.spriteSize / CollisionShape.SPRITE_UNITS
        assertEquals(1.5, scale, 1e-12)
        assertTrue(spec.shape.circles.any { abs(it.center.x - 33.5 * scale) < 1e-9 && abs(it.radius - 8.5 * scale) < 1e-9 })
    }

    @Test
    fun everyTypeCrossesTheWorldInTheSameTimeAsBefore() {
        val former = listOf(40.0, 55.0, 75.0, 100.0, 130.0)
        order.zip(former).forEach { (type, speed) ->
            val before = LevelDefaults.BASE_WORLD_WIDTH / speed
            val now = LevelDefaults.WORLD_WIDTH / specs.getValue(type).speed
            assertEquals(before, now, 1e-9, "$type")
        }
    }

    @Test
    fun pointsIncreaseFromHelicopterToJumbo() {
        order.zipWithNext().forEach { (low, high) ->
            assertTrue(specs.getValue(low).landingPoints < specs.getValue(high).landingPoints, "$low < $high")
        }
    }
}
