package nl.hexmaster.finalapproach.platform

/** An [AppHost] for tests: records whether the app was asked to quit. */
class FakeAppHost(
    override val versionName: String = "1.2.0",
    override val reduceMotion: Boolean = false,
) : AppHost {
    override val back = BackDispatcher()

    var quitCalls = 0
        private set

    override fun quit() {
        quitCalls++
    }
}
