package nl.hexmaster.finalapproach.ui.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import nl.hexmaster.finalapproach.ui.PreviewAppHost
import nl.hexmaster.finalapproach.ui.theme.FaTheme

private object MainPagePreviewData {
    const val PORTRAIT_WIDTH = 360
    const val PORTRAIT_HEIGHT = 720
    const val LANDSCAPE_WIDTH = 720
    const val LANDSCAPE_HEIGHT = 320
}

/** The main page standing still; press back in interactive mode to see the quit dialog. */
@Composable
private fun MainPagePreview(night: Boolean) {
    FaTheme(night = night, reduceMotion = true) {
        MainPage(host = PreviewAppHost, onTakeOff = {}, onSettings = {})
    }
}

@Preview(widthDp = MainPagePreviewData.PORTRAIT_WIDTH, heightDp = MainPagePreviewData.PORTRAIT_HEIGHT)
@Composable
private fun MainPagePortraitPreview() = MainPagePreview(night = false)

@Preview(widthDp = MainPagePreviewData.PORTRAIT_WIDTH, heightDp = MainPagePreviewData.PORTRAIT_HEIGHT)
@Composable
private fun MainPagePortraitNightPreview() = MainPagePreview(night = true)

@Preview(widthDp = MainPagePreviewData.LANDSCAPE_WIDTH, heightDp = MainPagePreviewData.LANDSCAPE_HEIGHT)
@Composable
private fun MainPageShortLandscapePreview() = MainPagePreview(night = false)
