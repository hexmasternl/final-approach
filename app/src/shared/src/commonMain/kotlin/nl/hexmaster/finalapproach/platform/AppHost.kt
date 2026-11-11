package nl.hexmaster.finalapproach.platform

/**
 * Everything the shared code needs from the platform it runs on. The host app implements it,
 * so shared code never touches a platform API.
 */
interface AppHost {
    /** Version of the installed build, for example "1.2.0". */
    val versionName: String

    /** True when the device asks apps to reduce or remove animation. */
    val reduceMotion: Boolean

    /** Receives back presses from the host and hands them to the current screen. */
    val back: BackDispatcher

    /** Closes the app. */
    fun quit()

    /** Locks the app in landscape while [locked] is true; false releases the lock. */
    fun lockLandscape(locked: Boolean)
}

/**
 * Delivers back presses to the screen that is currently on top. The host calls [dispatch];
 * screens register with [push]. Handlers form a stack, so the last one registered wins.
 */
class BackDispatcher {
    private val handlers = mutableListOf<() -> Unit>()

    /** Registers [handler] on top of the stack and returns a function that removes it again. */
    fun push(handler: () -> Unit): () -> Unit {
        handlers += handler
        return { handlers -= handler }
    }

    /** Hands a back press to the top handler. Returns false when nothing handled it. */
    fun dispatch(): Boolean {
        val handler = handlers.lastOrNull() ?: return false
        handler()
        return true
    }
}
