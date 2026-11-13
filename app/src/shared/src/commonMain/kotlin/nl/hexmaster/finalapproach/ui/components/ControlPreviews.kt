package nl.hexmaster.finalapproach.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.hexmaster.finalapproach.domain.ReduceMotionChoice
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Sample data for the component previews. Not used by the game. */
private object ControlPreviewData {
    const val WIDTH = 420
    const val CONTROLS_HEIGHT = 420
    const val BUTTONS_HEIGHT = 520
    const val READOUTS_HEIGHT = 360
    const val SCORE = 12_450
    const val BEST = 18_200
    val choices = ReduceMotionChoice.entries
}

/** Toggles and segmented controls in a panel: off, on, disabled, and every choice selected once. */
@Composable
private fun ControlsPreview(night: Boolean) {
    FaTheme(night = night, reduceMotion = true) {
        Column(Modifier.background(FaTheme.colors.bgSky).padding(FaDimens.space5)) {
            FaPanel(
                Modifier.fillMaxWidth(),
                header = { BasicText("Controls", style = FaTheme.type.titleSm.copy(color = FaTheme.colors.ink)) },
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(FaDimens.space4)) {
                    FaToggle(checked = false, onCheckedChange = {})
                    FaToggle(checked = true, onCheckedChange = {})
                    FaToggle(checked = false, onCheckedChange = {}, enabled = false)
                }
                Column(verticalArrangement = Arrangement.spacedBy(FaDimens.space3)) {
                    ControlPreviewData.choices.forEach { choice ->
                        FaSegmentedControl(ControlPreviewData.choices, choice, {}, { it.name })
                    }
                }
            }
        }
    }
}

@Preview(widthDp = ControlPreviewData.WIDTH, heightDp = ControlPreviewData.CONTROLS_HEIGHT)
@Composable
private fun ControlsDayPreview() = ControlsPreview(night = false)

@Preview(widthDp = ControlPreviewData.WIDTH, heightDp = ControlPreviewData.CONTROLS_HEIGHT)
@Composable
private fun ControlsNightPreview() = ControlsPreview(night = true)

/** Every button variant and size, the icon buttons and the disabled state. */
@Composable
private fun ButtonsPreview(night: Boolean) {
    FaTheme(night = night, reduceMotion = true) {
        Column(
            Modifier.background(FaTheme.colors.bgSky).padding(FaDimens.space5),
            verticalArrangement = Arrangement.spacedBy(FaDimens.space4),
        ) {
            FaButton("Take off", {}, icon = FaIcon.Play)
            FaButton("Choose airport", {}, variant = FaButtonVariant.Secondary)
            FaButton("Settings", {}, variant = FaButtonVariant.Ghost, icon = FaIcon.Settings)
            FaButton("Quit flight", {}, variant = FaButtonVariant.Danger)
            Row(horizontalArrangement = Arrangement.spacedBy(FaDimens.space4), verticalAlignment = Alignment.CenterVertically) {
                FaButton("Retry", {}, size = FaButtonSize.Small, icon = FaIcon.Restart)
                FaButton("Resume", {})
                FaButton("Play", {}, size = FaButtonSize.Large, icon = FaIcon.Play)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(FaDimens.space4), verticalAlignment = Alignment.CenterVertically) {
                FaButton("Disabled", {}, enabled = false)
                FaIconButton(FaIcon.ArrowLeft, "Back", {})
                FaIconButton(FaIcon.Settings, "Options", {}, compact = true)
                FaIconButton(FaIcon.Close, "Close", {}, enabled = false)
            }
        }
    }
}

@Preview(widthDp = ControlPreviewData.WIDTH, heightDp = ControlPreviewData.BUTTONS_HEIGHT)
@Composable
private fun ButtonsDayPreview() = ButtonsPreview(night = false)

@Preview(widthDp = ControlPreviewData.WIDTH, heightDp = ControlPreviewData.BUTTONS_HEIGHT)
@Composable
private fun ButtonsNightPreview() = ButtonsPreview(night = true)

/** The flat things on the field: flight strips, stickers, chips and the stamp. */
@Composable
private fun ReadoutsPreview(night: Boolean) {
    FaTheme(night = night, reduceMotion = true) {
        Column(
            Modifier.background(FaTheme.colors.bgField).padding(FaDimens.space5),
            verticalArrangement = Arrangement.spacedBy(FaDimens.space5),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(FaDimens.space3)) {
                FaScoreCounter("Best", ControlPreviewData.BEST, icon = FaIcon.Star)
                FaScoreCounter("Score", ControlPreviewData.SCORE, accent = true)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(FaDimens.space5)) {
                FaSticker("x3", icon = FaIcon.Bolt)
                FaSticker("+250", tone = FaStickerTone.Sun, caption = "Streak x3")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(FaDimens.space2)) {
                FaChip("1 runway")
                FaChip("Best 18 200", tone = FaChipTone.Sun, icon = FaIcon.Star)
                FaChip("Landed", tone = FaChipTone.Mint, icon = FaIcon.Check)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(FaDimens.space2)) {
                FaChip("Too close", tone = FaChipTone.Cherry, icon = FaIcon.Warning)
                FaChip("Locked", tone = FaChipTone.Plain, icon = FaIcon.Lock)
            }
            Row(Modifier.background(FaTheme.colors.paper100).padding(FaDimens.space4)) { FaStamp("Too close") }
        }
    }
}

@Preview(widthDp = ControlPreviewData.WIDTH, heightDp = ControlPreviewData.READOUTS_HEIGHT)
@Composable
private fun ReadoutsDayPreview() = ReadoutsPreview(night = false)

@Preview(widthDp = ControlPreviewData.WIDTH, heightDp = ControlPreviewData.READOUTS_HEIGHT)
@Composable
private fun ReadoutsNightPreview() = ReadoutsPreview(night = true)
