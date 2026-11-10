package nl.hexmaster.finalapproach.platform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BackDispatcherTest {
    @Test
    fun backIsUnhandledWithoutAHandler() {
        assertFalse(BackDispatcher().dispatch())
    }

    @Test
    fun theLastRegisteredHandlerReceivesTheBackPress() {
        val dispatcher = BackDispatcher()
        val calls = mutableListOf<String>()
        dispatcher.push { calls += "page" }
        dispatcher.push { calls += "dialog" }

        assertTrue(dispatcher.dispatch())

        assertEquals(listOf("dialog"), calls)
    }

    @Test
    fun aRemovedHandlerNoLongerReceivesBackPresses() {
        val dispatcher = BackDispatcher()
        val calls = mutableListOf<String>()
        dispatcher.push { calls += "page" }
        val remove = dispatcher.push { calls += "dialog" }

        remove()
        dispatcher.dispatch()

        assertEquals(listOf("page"), calls)
    }

    @Test
    fun theFakeHostRecordsQuit() {
        val host = FakeAppHost()
        host.quit()
        assertEquals(1, host.quitCalls)
    }
}
