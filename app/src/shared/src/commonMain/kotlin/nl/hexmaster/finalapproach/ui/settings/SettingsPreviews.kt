package nl.hexmaster.finalapproach.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import nl.hexmaster.finalapproach.domain.PlayerSettings
import nl.hexmaster.finalapproach.domain.ReduceMotionChoice
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.platform.BackDispatcher
import nl.hexmaster.finalapproach.platform.InMemoryHighScoreStore
import nl.hexmaster.finalapproach.platform.InMemorySettingsStore
import nl.hexmaster.finalapproach.platform.InMemoryTutorialStore
import nl.hexmaster.finalapproach.ui.theme.FaTheme

private object SettingsPreviewData {
    const val PORTRAIT_WIDTH = 360
    const val PORTRAIT_HEIGHT = 720
    const val LANDSCAPE_WIDTH = 720
    const val LANDSCAPE_HEIGHT = 320
    const val VERSION = "1.0.0"

    val host = object : AppHost {
        override val versionName = VERSION
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
}

@Composable
private fun SettingsPreview() {
    FaTheme(reduceMotion = true) {
        SettingsPage(
            host = SettingsPreviewData.host,
            settings = PlayerSettings(reduceMotion = ReduceMotionChoice.On),
            onSoundEffects = {},
            onKeepScreenOn = {},
            onReduceMotion = {},
            onBack = {},
        )
    }
}

@Preview(widthDp = SettingsPreviewData.PORTRAIT_WIDTH, heightDp = SettingsPreviewData.PORTRAIT_HEIGHT)
@Composable
private fun SettingsPortraitPreview() = SettingsPreview()

@Preview(widthDp = SettingsPreviewData.LANDSCAPE_WIDTH, heightDp = SettingsPreviewData.LANDSCAPE_HEIGHT)
@Composable
private fun SettingsShortLandscapePreview() = SettingsPreview()
