package nl.hexmaster.finalapproach.ui.sprites

import androidx.compose.ui.geometry.Offset
import nl.hexmaster.finalapproach.domain.SceneryItem
import nl.hexmaster.finalapproach.domain.SceneryKind
import nl.hexmaster.finalapproach.domain.Vec2
import nl.hexmaster.finalapproach.ui.game.drawsOnGround
import nl.hexmaster.finalapproach.ui.game.scenerySprites
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ShadowPlacementTest {
    @Test
    fun aShadowIsThrownTheSameWayWhateverTheRotation() {
        // The sun stands still: a car turned 0, 90 or 180 degrees casts its shadow to the same side.
        val car = ScenerySprite.Car
        val offsets = listOf(0f, 90f, 180f).map { shadowOffset(car, scale = 2f, rotationDegrees = it) }
        assertEquals(listOf(offsets[0], offsets[0], offsets[0]), offsets)
        assertEquals(Offset(car.shadowX * 2f, car.shadowY * 2f), offsets[0])
        assertTrue(offsets[0].x > 0f && offsets[0].y > 0f, "down and to the right")
    }

    @Test
    fun theThrowGrowsWithTheScaleOfTheSprite() {
        val tree = ScenerySprite.Tree
        assertEquals(shadowOffset(tree, 1f, 0f) * 3f, shadowOffset(tree, 3f, 0f))
    }

    @Test
    fun aSpriteFitsItsOwnBoundsIntoTheRoom() {
        assertEquals(1f, fitScale(56f, 72f, 56f, 72f), 1e-4f)
        assertEquals(2f, fitScale(56f, 72f, 112f, 200f), 1e-4f)
        assertEquals(0f, fitScale(0f, 72f, 56f, 72f))
    }

    @Test
    fun theRowOfAStripIsTurnedAboutItsMiddleAndItsPiecesKeepCount() {
        val middle = Offset(500f, 500f)
        val flat = rowPieces(44f, 44f, middle, 28f, 480f, 0f)
        val turned = rowPieces(44f, 44f, middle, 28f, 480f, 90f)
        assertEquals(flat.size, turned.size)
        assertTrue(flat.size > 1)
        // Along the strip at 0 degrees is the y axis; turned a quarter it is the x axis.
        assertEquals(flat.first().center.y - middle.y, -(turned.first().center.x - middle.x), 0.01f)
        assertEquals(0f, turned.first().center.y - middle.y, 0.01f)
    }

    @Test
    fun flatSpritesAndGroundKindsAreDrawnOnTheGroundAndTheRestStandsUp() {
        fun item(kind: SceneryKind) = SceneryItem(kind, Vec2(0.0, 0.0), 10.0, 10.0)
        listOf(SceneryKind.Taxiway, SceneryKind.Apron, SceneryKind.Field, SceneryKind.Road, SceneryKind.Pond)
            .forEach { assertTrue(item(it).drawsOnGround, "$it") }
        listOf(SceneryKind.Tree, SceneryKind.Terminal, SceneryKind.Car, SceneryKind.Hedge, SceneryKind.ForestEdge, SceneryKind.Jetty)
            .forEach { assertFalse(item(it).drawsOnGround, "$it") }
        // A flat sprite casts none; a sprite with height always has a shadow.
        scenerySprites.values.forEach { sprite ->
            assertEquals(sprite.tier == SceneryTier.Flat, sprite.shadow.isEmpty(), sprite.name)
        }
    }
}
