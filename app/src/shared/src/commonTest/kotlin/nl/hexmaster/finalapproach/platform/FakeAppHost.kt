package nl.hexmaster.finalapproach.platform

/** An [AppHost] for tests: records whether the app was asked to quit. */
class FakeAppHost(
    override val versionName: String = "1.2.0",
    override val reduceMotion: Boolean = false,
) : AppHost {
    override val back = BackDispatcher()

    override val highScores = InMemoryHighScoreStore()

    var quitCalls = 0
        private set

    override fun quit() {
        quitCalls++
    }

    var landscapeLocked = false
        private set

    override fun lockLandscape(locked: Boolean) {
        landscapeLocked = locked
    }
}
