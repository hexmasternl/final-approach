package nl.hexmaster.finalapproach.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import nl.hexmaster.finalapproach.domain.PlayerSettings
import nl.hexmaster.finalapproach.domain.ReduceMotionChoice
import nl.hexmaster.finalapproach.ui.PreviewAppHost
import nl.hexmaster.finalapproach.ui.theme.FaTheme

private object SettingsPreviewData {
    const val PORTRAIT_WIDTH = 360
    const val PORTRAIT_HEIGHT = 720
    const val LANDSCAPE_WIDTH = 720
    const val LANDSCAPE_HEIGHT = 320
}

@Composable
private fun SettingsPreview(night: Boolean = false) {
    FaTheme(night = night, reduceMotion = true) {
        SettingsPage(
            host = PreviewAppHost,
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

@Preview(widthDp = SettingsPreviewData.PORTRAIT_WIDTH, heightDp = SettingsPreviewData.PORTRAIT_HEIGHT)
@Composable
private fun SettingsPortraitNightPreview() = SettingsPreview(night = true)

@Preview(widthDp = SettingsPreviewData.LANDSCAPE_WIDTH, heightDp = SettingsPreviewData.LANDSCAPE_HEIGHT)
@Composable
private fun SettingsShortLandscapePreview() = SettingsPreview()
