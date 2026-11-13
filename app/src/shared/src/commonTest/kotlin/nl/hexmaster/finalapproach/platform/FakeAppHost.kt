package nl.hexmaster.finalapproach.platform

/** An [AppHost] for tests: records whether the app was asked to quit. */
class FakeAppHost(
    override val versionName: String = "1.2.0",
    override val reduceMotion: Boolean = false,
) : AppHost {
    override val back = BackDispatcher()

    override val highScores = InMemoryHighScoreStore()

    override val settings = InMemorySettingsStore()

    override val tutorials = InMemoryTutorialStore()

    override val sounds = RecordingSoundPlayer()

    var quitCalls = 0
        private set

    override fun quit() {
        quitCalls++
    }

    val openedUrls = mutableListOf<String>()

    override fun openUrl(url: String) {
        openedUrls += url
    }

    /** The last value passed to [keepScreenOn]. */
    var screenKeptOn = false
        private set

    override fun keepScreenOn(on: Boolean) {
        screenKeptOn = on
    }

    var landscapeLocked = false
        private set

    override fun lockLandscape(locked: Boolean) {
        landscapeLocked = locked
    }
}

/** A [SoundPlayer] that lists what was played. */
class RecordingSoundPlayer : SoundPlayer {
    val played = mutableListOf<SoundEffect>()

    override fun play(effect: SoundEffect) {
        played += effect
    }
}
