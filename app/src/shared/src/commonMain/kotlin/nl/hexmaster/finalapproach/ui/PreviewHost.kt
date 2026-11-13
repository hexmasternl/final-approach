package nl.hexmaster.finalapproach.ui

import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.platform.BackDispatcher
import nl.hexmaster.finalapproach.platform.InMemoryHighScoreStore
import nl.hexmaster.finalapproach.platform.InMemorySettingsStore
import nl.hexmaster.finalapproach.platform.InMemoryTutorialStore

/** A host that does nothing, for the previews of whole screens. Not used by the game. */
internal object PreviewAppHost : AppHost {
    override val versionName = "1.0.0"
    override val reduceMotion = false
    override val back = BackDispatcher()
    override val highScores = InMemoryHighScoreStore()
    override val settings = InMemorySettingsStore()
    override val tutorials = InMemoryTutorialStore()
    override fun quit() = Unit
    override fun keepScreenOn(on: Boolean) = Unit
    override fun openUrl(url: String) = Unit
    override fun lockLandscape(locked: Boolean) = Unit
}
