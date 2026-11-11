package nl.hexmaster.finalapproach.domain

import kotlin.test.Test
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
    fun pointsIncreaseFromHelicopterToJumbo() {
        order.zipWithNext().forEach { (low, high) ->
            assertTrue(specs.getValue(low).landingPoints < specs.getValue(high).landingPoints, "$low < $high")
        }
    }
}
