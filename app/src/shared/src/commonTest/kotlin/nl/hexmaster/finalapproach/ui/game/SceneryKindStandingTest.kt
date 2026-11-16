package nl.hexmaster.finalapproach.ui.game

import nl.hexmaster.finalapproach.domain.Levels
import nl.hexmaster.finalapproach.domain.SceneryKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The domain flag `standing` and the design system's `rotates` must not drift apart. */
class SceneryKindStandingTest {
    @Test
    fun standingIsTheOppositeOfRotatesForEveryKindWithASprite() {
        scenerySprites.forEach { (kind, sprite) ->
            assertEquals(!sprite.rotates, kind.standing, "$kind and sprite ${sprite.name}")
        }
        assertTrue(scenerySprites.keys.any { it.standing } && scenerySprites.keys.any { !it.standing })
    }

    @Test
    fun noStandingItemOfAnyLevelRotates() {
        Levels.all.forEach { level ->
            level.scenery.filter { it.kind == SceneryKind.Terminal || it.kind.standing }
                .forEach { assertEquals(0.0, it.rotation, "${level.id} ${it.kind}") }
            assertEquals(emptyList(), level.validate(), level.id)
        }
    }
}
