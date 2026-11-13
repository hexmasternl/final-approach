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

    /** Opens [url] in the device's browser. Does nothing when no app can open it. */
    fun openUrl(url: String)

    /** Where the personal best score of each level is kept. */
    val highScores: HighScoreStore

    /** Where it is kept that the first-time tutorial of a level was completed. */
    val tutorials: TutorialStore

    /** Where the player's settings are kept. */
    val settings: SettingsStore

    /** Ads; [NoOpAds] on a platform that has none. */
    val ads: Ads get() = NoOpAds

    /** Sound effects; [SilentSoundPlayer] on a platform that has no player. */
    val sounds: SoundPlayer get() = SilentSoundPlayer

    /**
     * Keeps the screen from timing out while [on] is true; false restores the normal timeout.
     * Only the game screen calls it, while a level is running.
     */
    fun keepScreenOn(on: Boolean)

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
