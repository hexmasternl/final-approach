package nl.hexmaster.finalapproach.ui.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import nl.hexmaster.finalapproach.domain.PlayerSettings
import nl.hexmaster.finalapproach.domain.ReduceMotionChoice
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.platform.AppLinks
import nl.hexmaster.finalapproach.ui.BackHandler
import nl.hexmaster.finalapproach.ui.components.FaButton
import nl.hexmaster.finalapproach.ui.components.FaButtonVariant
import nl.hexmaster.finalapproach.ui.components.FaIconButton
import nl.hexmaster.finalapproach.ui.components.FaSegmentedControl
import nl.hexmaster.finalapproach.ui.components.FaToggle
import nl.hexmaster.finalapproach.ui.components.faBlock
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.sprites.drawSunburst
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaShadows
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Every string of the settings page. */
private object SettingsText {
    const val TITLE = "Settings"
    const val BACK = "Back"
    const val SOUND = "Sound"
    const val SOUND_EFFECTS = "Sound effects"
    const val SOUND_EFFECTS_BODY = "Engines, dings and boings"
    const val ACCESSIBILITY = "Accessibility"
    const val REDUCE_MOTION = "Reduce motion"
    const val DEVICE = "Device"
    const val ON = "On"
    const val OFF = "Off"
    const val ABOUT = "About"
    const val PRIVACY_POLICY = "Privacy policy"
    const val APP_NAME = "Final Approach"

    fun version(versionName: String) = "$APP_NAME $versionName"

    fun choice(choice: ReduceMotionChoice) = when (choice) {
        ReduceMotionChoice.Device -> DEVICE
        ReduceMotionChoice.On -> ON
        ReduceMotionChoice.Off -> OFF
    }
}

/**
 * The settings page: sound effects, reduce motion, the privacy policy and the version. Every
 * change applies at once through [onSoundEffects] and [onReduceMotion]; there is no Save button.
 * Back, and the system back button, return through [onBack].
 */
@Composable
fun SettingsPage(
    host: AppHost,
    settings: PlayerSettings,
    onSoundEffects: (Boolean) -> Unit,
    onReduceMotion: (ReduceMotionChoice) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    BackHandler(host, onBack)

    Box(modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) { drawSunburst(center, 0f, colors) }
        Box(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(FaDimens.space5),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                Modifier
                    .widthIn(max = FaDimens.dialogMaxWidth)
                    .fillMaxWidth()
                    .faBlock(colors.surface, colors.outline, FaDimens.line3, FaDimens.radiusLg, FaShadows.lipLg)
                    .padding(
                        start = FaDimens.space5,
                        top = FaDimens.space5,
                        end = FaDimens.space5,
                        bottom = FaDimens.space5 + FaShadows.lipLg,
                    ),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(FaDimens.space4)) {
                    FaIconButton(FaIcon.ArrowLeft, SettingsText.BACK, onBack)
                    BasicText(SettingsText.TITLE, style = type.titleLg.copy(color = colors.ink))
                }

                GroupLabel(SettingsText.SOUND)
                SettingRow(SettingsText.SOUND_EFFECTS, SettingsText.SOUND_EFFECTS_BODY) {
                    FaToggle(settings.soundEffects, onSoundEffects)
                }

                GroupLabel(SettingsText.ACCESSIBILITY)
                SettingRow(SettingsText.REDUCE_MOTION, null) {
                    FaSegmentedControl(
                        options = ReduceMotionChoice.entries,
                        selected = settings.reduceMotion,
                        onSelect = onReduceMotion,
                        label = SettingsText::choice,
                    )
                }

                GroupLabel(SettingsText.ABOUT)
                FaButton(
                    text = SettingsText.PRIVACY_POLICY,
                    onClick = { host.openUrl(AppLinks.PRIVACY_POLICY_URL) },
                    modifier = Modifier.fillMaxWidth(),
                    variant = FaButtonVariant.Ghost,
                )
                BasicText(
                    SettingsText.version(host.versionName),
                    modifier = Modifier.padding(top = FaDimens.space4),
                    style = type.caption.copy(color = colors.inkMuted),
                )
            }
        }
    }
}

/** The small uppercase label above a group of settings. */
@Composable
private fun GroupLabel(text: String) {
    BasicText(
        text.uppercase(),
        modifier = Modifier.padding(top = FaDimens.space5, bottom = FaDimens.space2),
        style = FaTheme.type.label.copy(color = FaTheme.colors.inkMuted),
    )
}

/** One `fa-setting` row: label and optional description on the left, the control on the right. */
@Composable
private fun SettingRow(title: String, description: String?, control: @Composable () -> Unit) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FaDimens.space5),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            BasicText(title, style = type.titleSm.copy(color = colors.ink))
            if (description != null) BasicText(description, style = type.bodySm.copy(color = colors.inkMuted))
        }
        control()
    }
}
