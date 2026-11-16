package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.domain.Levels
import kotlin.test.Test
import kotlin.test.assertTrue

class LevelMiniatureTest {
    private val epsilon = 1e-9

    @Test
    fun theCropIsASquareInsideTheWorld() {
        Levels.all.forEach { level ->
            val crop = miniatureCrop(level)
            assertTrue(crop.side > 0.0, level.id)
            assertTrue(crop.left >= -epsilon && crop.top >= -epsilon, "${level.id}: ${crop.left}, ${crop.top}")
            assertTrue(crop.right <= level.world.width + epsilon && crop.bottom <= level.world.height + epsilon, "${level.id}: ends at ${crop.right}, ${crop.bottom}")
        }
    }

    @Test
    fun theCropHoldsEveryRunwayAndHelipadWithItsFringe() {
        Levels.all.forEach { level ->
            val crop = miniatureCrop(level)
            landingZoneCorners(level).forEach { corner ->
                assertTrue(crop.contains(corner), "${level.id}: $corner lies outside the crop ${crop.left}, ${crop.top}, ${crop.side}")
            }
            // The centres, which must be in view, are inside as well.
            level.runways.forEach { assertTrue(crop.contains(it.position), "${level.id} ${it.id}") }
            level.helipads.forEach { assertTrue(crop.contains(it.position), "${level.id} ${it.id}") }
        }
    }

    @Test
    fun theCropIsTheSmallestSquareWithMarginAroundTheLandingZones() {
        Levels.all.forEach { level ->
            val corners = landingZoneCorners(level)
            val wide = maxOf(corners.maxOf { it.x } - corners.minOf { it.x }, corners.maxOf { it.y } - corners.minOf { it.y })
            val crop = miniatureCrop(level)
            val world = minOf(level.world.width, level.world.height)
            assertTrue(crop.side <= wide + 2.0 * MiniatureDefaults.MARGIN + epsilon || crop.side <= world + epsilon, level.id)
            assertTrue(crop.side >= minOf(wide, world) - epsilon, "${level.id}: a smaller square would not hold the zones")
        }
    }
}
