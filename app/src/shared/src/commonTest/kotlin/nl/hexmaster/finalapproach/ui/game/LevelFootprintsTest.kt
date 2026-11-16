package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.SceneryKind
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Level scenery has the natural size of its sprite, so a sprite is drawn at scale 1 and its lines keep their token width. */
class LevelFootprintsTest {
    private val rounding = 0.5f

    @Test
    fun theFootprintTableEqualsTheSizeOfEachSpritesOwnShapes() {
        scenerySprites.forEach { (kind, sprite) ->
            val (width, height) = Levels.SIZES[kind] ?: return@forEach
            assertTrue(abs(width - sprite.bodyWidth) <= rounding, "$kind: $width across, the sprite is ${sprite.bodyWidth}")
            assertTrue(abs(height - sprite.bodyHeight) <= rounding, "$kind: $height along, the sprite is ${sprite.bodyHeight}")
        }
    }

    @Test
    fun everyKindWithASpriteAndAFootprintIsPlacedAtThatFootprint() {
        Levels.all.forEach { level ->
            level.scenery.filter { it.kind in scenerySprites && it.kind in Levels.SIZES }.forEach { item ->
                val (width, height) = Levels.SIZES.getValue(item.kind)
                assertEquals(width, item.width, "${level.id}: ${item.kind} at ${item.position}")
                assertEquals(height, item.height, "${level.id}: ${item.kind} at ${item.position}")
            }
        }
    }

    @Test
    fun everyStandingAndSmallKindThatLevelsPlaceHasAFootprint() {
        val placed = Levels.all.flatMap { level -> level.scenery.map { it.kind } }.toSet()
        val rows = setOf(SceneryKind.Hedge, SceneryKind.Fence, SceneryKind.ForestEdge)
        placed.filter { it in scenerySprites && it !in rows }.forEach { kind ->
            assertTrue(kind in Levels.SIZES, "$kind has a sprite but no footprint")
        }
    }
}
