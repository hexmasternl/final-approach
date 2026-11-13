package nl.hexmaster.finalapproach.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

private object CoachPreviewData {
    const val WIDTH = 400
    const val HEIGHT = 520
}

@Preview(widthDp = CoachPreviewData.WIDTH, heightDp = CoachPreviewData.HEIGHT)
@Composable
private fun CoachMarkVariantsPreview() {
    FaTheme(reduceMotion = true) {
        Column(
            Modifier.background(FaTheme.colors.bgField).padding(FaDimens.space5),
            verticalArrangement = Arrangement.spacedBy(FaDimens.space5),
        ) {
            FaCoachMark("Step 3 of 6", "Draw its path", "Drag from the plane to the runway.", icon = FaIcon.Finger)
            FaCoachMark("Step 3 of 6", "Light prop inbound", "It needs to land.", tail = FaCoachTail.Up, icon = FaIcon.Plane)
            FaCoachMark(
                "Step 2 of 6",
                "This is runway 27",
                "Airplanes land here. Come in from the right, lined up with the runway.",
                gotItLabel = "Got it",
                onGotIt = {},
            )
        }
    }
}
