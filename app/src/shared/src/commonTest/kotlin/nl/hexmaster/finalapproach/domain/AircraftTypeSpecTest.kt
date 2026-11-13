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
    fun speedIncreasesFromHelicopterToJet() {
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
    fun speedsAreTheTunedSpeedsTimesTheWorldZoom() {
        val zoom = LevelDefaults.WORLD_ZOOM
        val all = order + AircraftType.Jumbo
        val expected = listOf(20.0, 55.0 * 2.0 / 3.0, 50.0, 100.0 * 2.0 / 3.0, 65.0)
        expected.zip(all).forEach { (speed, type) -> assertEquals(speed * zoom, specs.getValue(type).speed, 1e-9, "$type") }
    }

    @Test
    fun spritesAreTheFormerSizeTimesTheWorldZoomAndTheJumboIsHalf() {
        val radius = listOf(26.0, 24.0, 28.0, 30.0, 22.0)
        (order + AircraftType.Jumbo).zip(radius).forEach { (type, radius) ->
            val spec = specs.getValue(type)
            assertEquals(radius * LevelDefaults.WORLD_ZOOM, spec.radius, "$type radius")
            assertEquals(radius * AircraftTypeSpec.SPRITE_SIZE_PER_RADIUS * LevelDefaults.WORLD_ZOOM, spec.spriteSize, 1e-9, "$type sprite")
        }
    }

    @Test
    fun theJumboCollisionShapeIsHalfTheFormerOne() {
        val jumbo = specs.getValue(AircraftType.Jumbo)
        val scale = jumbo.spriteSize / CollisionShape.SPRITE_UNITS
        // The former wing tip engine circle: 36 sprite units out, 7 in radius, on the former 44-radius sprite.
        val formerScale = 44.0 * AircraftTypeSpec.SPRITE_SIZE_PER_RADIUS * LevelDefaults.WORLD_ZOOM / CollisionShape.SPRITE_UNITS
        assertEquals(0.5, scale / formerScale, 1e-12)
        assertTrue(jumbo.shape.circles.any { abs(it.center.x - 36.0 * scale) < 1e-9 && abs(it.radius - 7.0 * scale) < 1e-9 })
    }

    @Test
    fun collisionShapesGrewWithTheSprites() {
        // The light prop's wing tip circle: 33.5 sprite units out, 8.5 in radius, on a 96-unit sprite.
        val spec = specs.getValue(AircraftType.LightProp)
        val scale = spec.spriteSize / CollisionShape.SPRITE_UNITS
        assertEquals(LevelDefaults.WORLD_ZOOM * 0.6, scale, 1e-12)
        assertTrue(spec.shape.circles.any { abs(it.center.x - 33.5 * scale) < 1e-9 && abs(it.radius - 8.5 * scale) < 1e-9 })
    }

    @Test
    fun everyTypeCrossesTheWorldInTheSameTimeAsAtZoomOne() {
        val former = listOf(20.0, 55.0 * 2.0 / 3.0, 50.0, 100.0 * 2.0 / 3.0, 65.0)
        (order + AircraftType.Jumbo).zip(former).forEach { (type, speed) ->
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
