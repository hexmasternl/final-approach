package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.RunwayConfig
import nl.hexmaster.finalapproach.domain.Vec2
import kotlin.test.Test
import kotlin.test.assertEquals
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

    @Test
    fun aLandingZoneWiderThanTheWorldsShortSideShowsAsMuchAsFits() {
        val base = Levels.all.first()
        val centre = Vec2(base.world.width / 2.0, base.world.height / 2.0)
        val level = base.copy(runways = listOf(RunwayConfig("long", centre, 0.0, length = 2.0 * base.world.height)), helipads = emptyList())
        val crop = miniatureCrop(level)
        val shortSide = minOf(level.world.width, level.world.height)
        assertEquals(shortSide, crop.side, epsilon)
        assertTrue(crop.left >= -epsilon && crop.top >= -epsilon, "the crop starts inside the world")
        assertTrue(crop.right <= level.world.width + epsilon && crop.bottom <= level.world.height + epsilon, "the crop ends inside the world")
    }

    @Test
    fun theViewMapsTheCropsTopLeftToPixelZeroAndItsSideToTheWholeTile() {
        val tile = 168f
        Levels.all.forEach { level ->
            val crop = miniatureCrop(level)
            val view = miniatureView(crop, tile)
            val topLeft = view.toTile(Vec2(crop.left, crop.top))
            val bottomRight = view.toTile(Vec2(crop.right, crop.bottom))
            assertEquals(0f, topLeft.x, 1e-3f, level.id)
            assertEquals(0f, topLeft.y, 1e-3f, level.id)
            assertEquals(tile, bottomRight.x, 1e-2f, level.id)
            assertEquals(tile, bottomRight.y, 1e-2f, level.id)
        }
    }
}
