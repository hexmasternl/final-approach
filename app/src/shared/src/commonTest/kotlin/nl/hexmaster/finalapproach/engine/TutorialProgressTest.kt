package nl.hexmaster.finalapproach.engine

import nl.hexmaster.finalapproach.platform.InMemoryTutorialStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TutorialProgressTest {

    @Test
    fun otherEventsDoNotComplete() {
        val store = InMemoryTutorialStore()
        val progress = TutorialProgress(store, "meadow-field")
        progress.onEvents(listOf(GameEvent.LeftArea(1)))
        assertFalse(progress.completed)
        assertFalse(store.isCompleted("meadow-field"))
    }

    @Test
    fun completionIsWrittenOnceForTheLevel() {
        val store = InMemoryTutorialStore()
        val progress = TutorialProgress(store, "meadow-field")
        progress.onEvents(listOf(GameEvent.TutorialCompleted))
        progress.onEvents(listOf(GameEvent.TutorialCompleted))
        assertTrue(store.isCompleted("meadow-field"))
        assertEquals(1, store.markCalls)
    }

    @Test
    fun aTutorialCompletedBeforeIsNotWrittenAgain() {
        val store = InMemoryTutorialStore().also { it.markCompleted("meadow-field") }
        TutorialProgress(store, "meadow-field").onEvents(listOf(GameEvent.TutorialCompleted))
        assertEquals(1, store.markCalls)
    }
}
