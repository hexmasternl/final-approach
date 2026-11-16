package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.domain.LevelConfig
import nl.hexmaster.finalapproach.domain.LevelDefaults
import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.Polygons
import nl.hexmaster.finalapproach.domain.SceneryKind
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.ui.sprites.SceneryDetail
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LevelDecorationTest {
    private val fields = setOf(SceneryKind.FieldWheat, SceneryKind.FieldGreen, SceneryKind.FieldPlough)

    private fun everyPoint(level: LevelConfig): List<Vec2> {
        val d = levelDecoration(level)
        return d.details.map { it.position } + d.trees.map { it.position }
    }

    @Test
    fun theSameLevelGivesTheSameDecorationTwice() {
        Levels.all.forEach { assertEquals(levelDecoration(it), levelDecoration(it), it.id) }
    }

    @Test
    fun aDifferentSeedGivesDifferentDecoration() {
        val level = Levels.meadowField
        assertTrue(levelDecoration(level) != levelDecoration(level.copy(decorationSeed = level.decorationSeed + 1)))
    }

    @Test
    fun everyLevelHasDecoration() {
        Levels.all.forEach {
            val d = levelDecoration(it)
            assertTrue(d.details.isNotEmpty(), "${it.id} details")
            assertTrue(d.trees.isNotEmpty(), "${it.id} trees")
        }
        assertTrue(levelDecoration(Levels.meadowField).patches.isNotEmpty())
        assertTrue(levelDecoration(Levels.islandAirport).patches.isNotEmpty(), "sea patches")
    }

    @Test
    fun nothingLiesInsideTheAirfield() {
        Levels.all.forEach { level ->
            everyPoint(level).forEach { assertFalse(Polygons.contains(level.airfield, it), "${level.id}: $it is in the airfield") }
            levelDecoration(level).patches.forEach { patch ->
                patch.outline.forEach { assertFalse(Polygons.contains(level.airfield, it), "${level.id}: a patch reaches into the airfield") }
            }
        }
    }

    @Test
    fun nothingLiesOnARoadFieldPondRunwayOrHelipad() {
        Levels.all.forEach { level ->
            everyPoint(level).forEach { point ->
                level.scenery.filter { it.kind == SceneryKind.Road || it.kind in fields }.forEach {
                    assertFalse(it.shape.contains(point), "${level.id}: $point on ${it.kind}")
                }
                level.scenery.filter { it.kind == SceneryKind.Pond || it.kind == SceneryKind.Waterhole }.forEach {
                    val local = it.shape.toLocal(point)
                    val dx = local.y / (it.width / 2.0)
                    val dy = local.x / (it.height / 2.0)
                    assertTrue(dx * dx + dy * dy >= 1.0, "${level.id}: $point on the water")
                }
                level.runways.forEach { assertFalse(it.shape.contains(point), "${level.id}: $point on runway ${it.id}") }
                level.helipads.forEach { assertTrue(it.position.distanceTo(point) > it.radius, "${level.id}: $point on helipad ${it.id}") }
            }
        }
    }

    @Test
    fun nothingThatStandsLiesInTheHudBand() {
        Levels.all.forEach { level ->
            levelDecoration(level).trees.forEach {
                assertTrue(it.position.y - it.radius >= LevelDefaults.HUD_BAND, "${level.id}: ${it.sprite} at ${it.position}")
            }
        }
    }

    @Test
    fun theSavannaHasTuftsAndNoFlowers() {
        val details = levelDecoration(Levels.savannaDawn).details.map { it.detail }.toSet()
        assertTrue(SceneryDetail.Tuft in details)
        assertFalse(SceneryDetail.Flower in details || SceneryDetail.FlowerPink in details)
    }

    @Test
    fun theMeadowHasFlowers() {
        val details = levelDecoration(Levels.meadowField).details.map { it.detail }.toSet()
        assertTrue(SceneryDetail.Flower in details || SceneryDetail.FlowerPink in details)
    }

    @Test
    fun theIslandDecorationStaysOnTheIslandAndTheSeaPatchesOffIt() {
        val level = Levels.islandAirport
        val island = requireNotNull(level.island)
        everyPoint(level).forEach { assertTrue(island.contains(it), "$it is off the island") }
        levelDecoration(level).patches.forEach { patch ->
            patch.outline.forEach { assertFalse(island.contains(it), "a sea patch reaches onto the island") }
        }
    }

    @Test
    fun theDecorationStaysOutOfThePaddock() {
        val level = Levels.meadowField
        val paddock = requireNotNull(level.paddock)
        everyPoint(level).forEach { assertFalse(paddock.contains(it), "$it is in the paddock") }
    }
}
