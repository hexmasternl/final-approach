package nl.hexmaster.finalapproach.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.hexmaster.finalapproach.domain.ReduceMotionChoice
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

private object ControlPreviewData {
    const val WIDTH = 320
    const val HEIGHT = 240
    val choices = ReduceMotionChoice.entries
}

@Composable
private fun ControlsPreview(night: Boolean) {
    FaTheme(night = night, reduceMotion = true) {
        Column(
            Modifier.background(FaTheme.colors.surface).padding(FaDimens.space5),
            verticalArrangement = Arrangement.spacedBy(FaDimens.space4),
        ) {
            FaToggle(checked = false, onCheckedChange = {})
            FaToggle(checked = true, onCheckedChange = {})
            ControlPreviewData.choices.forEach { choice ->
                FaSegmentedControl(ControlPreviewData.choices, choice, {}, { it.name })
            }
        }
    }
}

@Preview(widthDp = ControlPreviewData.WIDTH, heightDp = ControlPreviewData.HEIGHT * 2)
@Composable
private fun ControlsDayPreview() = ControlsPreview(night = false)

@Preview(widthDp = ControlPreviewData.WIDTH, heightDp = ControlPreviewData.HEIGHT * 2)
@Composable
private fun ControlsNightPreview() = ControlsPreview(night = true)
