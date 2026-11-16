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
    /** Where the middle of the shadow lands minus where the middle of the sprite lands, in pixels. */
    private fun throwOf(sprite: ScenerySprite, scale: Float, degrees: Float): Offset {
        val boundsCenter = Offset(7f, -3f)
        val center = Offset(300f, 200f)
        val matrix = shadowMatrix(sprite.shadowX, sprite.shadowY, sprite.rotates, scale, boundsCenter, center, degrees)
        return matrix.map(boundsCenter) - center
    }

    @Test
    fun theShadowIsThrownTheSameWayWhateverTheRotation() {
        // The sun stands still: a turned car casts its shadow to the same side.
        val car = ScenerySprite.Car
        assertTrue(car.rotates)
        listOf(0f, 90f, 210f).forEach { degrees ->
            val thrown = throwOf(car, scale = 2f, degrees = degrees)
            assertEquals(car.shadowX * 2f, thrown.x, 1e-3f, "x at $degrees")
            assertEquals(car.shadowY * 2f, thrown.y, 1e-3f, "y at $degrees")
            assertTrue(thrown.x > 0f && thrown.y > 0f, "down and to the right at $degrees")
        }
    }

    @Test
    fun theShadowOfAnAmbientSpriteFallsToItsLowerRightWhateverWayItIsTurned() {
        // A cow that has turned, a farmer, a boat sailing left and the flock all throw down and to the right.
        val ambient = listOf(ScenerySprite.Cow, ScenerySprite.Farmer, ScenerySprite.Sailboat, ScenerySprite.Yacht, ScenerySprite.ContainerVessel, ScenerySprite.BirdUp, ScenerySprite.BirdDown)
        ambient.forEach { sprite ->
            listOf(0f, 37f, 90f, 135f, 210f, 270f).forEach { degrees ->
                val thrown = throwOf(sprite, scale = 1f, degrees = degrees)
                assertEquals(sprite.shadowX, thrown.x, 1e-3f, "$sprite at $degrees")
                assertEquals(sprite.shadowY, thrown.y, 1e-3f, "$sprite at $degrees")
                assertTrue(thrown.x > 0f && thrown.y > 0f, "$sprite: down and to the right at $degrees")
            }
        }
    }

    @Test
    fun theThrowGrowsWithTheScaleOfTheSprite() {
        val tree = ScenerySprite.Tree
        val one = throwOf(tree, 1f, 0f)
        val three = throwOf(tree, 3f, 0f)
        assertEquals(one.x * 3f, three.x, 1e-3f)
        assertEquals(one.y * 3f, three.y, 1e-3f)
    }

    @Test
    fun aShadowThatTurnsWithItsSpriteWouldBeCaught() {
        // The silhouette itself turns with the sprite: a point off the middle moves round the middle.
        val matrix = shadowMatrix(0f, 0f, rotates = true, scale = 1f, boundsCenter = Offset.Zero, center = Offset.Zero, rotationDegrees = 90f)
        val mapped = matrix.map(Offset(10f, 0f))
        assertEquals(0f, mapped.x, 1e-3f)
        assertEquals(10f, mapped.y, 1e-3f)
        // A sprite that does not rotate keeps its silhouette upright.
        val upright = shadowMatrix(0f, 0f, rotates = false, scale = 1f, boundsCenter = Offset.Zero, center = Offset.Zero, rotationDegrees = 90f)
        assertEquals(10f, upright.map(Offset(10f, 0f)).x, 1e-3f)
        assertEquals(0f, upright.map(Offset(10f, 0f)).y, 1e-3f)
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
        listOf(SceneryKind.Taxiway, SceneryKind.Apron, SceneryKind.Road, SceneryKind.Pond)
            .forEach { assertTrue(item(it).drawsOnGround, "$it") }
        listOf(SceneryKind.Tree, SceneryKind.Terminal, SceneryKind.Car, SceneryKind.Hedge, SceneryKind.ForestEdge, SceneryKind.Jetty)
            .forEach { assertFalse(item(it).drawsOnGround, "$it") }
        // A flat sprite casts none; a sprite with height always has a shadow.
        scenerySprites.values.forEach { sprite ->
            assertEquals(sprite.tier == SceneryTier.Flat, sprite.shadow.isEmpty(), sprite.name)
        }
    }
}
