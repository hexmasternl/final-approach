package nl.hexmaster.finalapproach.ui.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import nl.hexmaster.finalapproach.domain.PlayerSettings
import nl.hexmaster.finalapproach.domain.ReduceMotionChoice
import nl.hexmaster.finalapproach.platform.AppHost
import nl.hexmaster.finalapproach.platform.AppLinks
import nl.hexmaster.finalapproach.ui.BackHandler
import nl.hexmaster.finalapproach.ui.components.FaButton
import nl.hexmaster.finalapproach.ui.components.FaButtonVariant
import nl.hexmaster.finalapproach.ui.components.FaIconButton
import nl.hexmaster.finalapproach.ui.components.FaPanel
import nl.hexmaster.finalapproach.ui.components.FaSegmentedControl
import nl.hexmaster.finalapproach.ui.components.FaToggle
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.sprites.drawSunburst
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Every string of the settings page. */
private object SettingsText {
    const val TITLE = "Settings"
    const val BACK = "Back"
    const val SOUND = "Sound"
    const val SOUND_EFFECTS = "Sound effects"
    const val SOUND_EFFECTS_BODY = "Engines, dings and boings"
    const val GAMEPLAY = "Gameplay"
    const val KEEP_SCREEN_ON = "Keep screen on"
    const val KEEP_SCREEN_ON_BODY = "While a level is playing"
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
    onKeepScreenOn: (Boolean) -> Unit,
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
            FaPanel(
                Modifier.widthIn(max = FaDimens.dialogMaxWidth).fillMaxWidth(),
                header = {
                    FaIconButton(FaIcon.ArrowLeft, SettingsText.BACK, onBack)
                    BasicText(SettingsText.TITLE, style = type.titleLg.copy(color = colors.ink))
                },
            ) {
                GroupLabel(SettingsText.SOUND, first = true)
                ToggleRow(SettingsText.SOUND_EFFECTS, SettingsText.SOUND_EFFECTS_BODY, settings.soundEffects, onSoundEffects)

                GroupLabel(SettingsText.GAMEPLAY)
                ToggleRow(SettingsText.KEEP_SCREEN_ON, SettingsText.KEEP_SCREEN_ON_BODY, settings.keepScreenOn, onKeepScreenOn)

                GroupLabel(SettingsText.ACCESSIBILITY)
                SettingRow(SettingsText.REDUCE_MOTION, null) {
                    FaSegmentedControl(
                        options = ReduceMotionChoice.entries,
                        selected = settings.reduceMotion,
                        onSelect = onReduceMotion,
                        label = SettingsText::choice,
                        // The group carries the name of its row, so a screen reader says what is chosen here.
                        modifier = Modifier.semantics { contentDescription = SettingsText.REDUCE_MOTION },
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

/** The small uppercase label above a group of settings; the [first] one stands directly under the header. */
@Composable
private fun GroupLabel(text: String, first: Boolean = false) {
    BasicText(
        text.uppercase(),
        modifier = if (first) Modifier.padding(bottom = FaDimens.space2) else Modifier.padding(top = FaDimens.space5, bottom = FaDimens.space2),
        style = FaTheme.type.labelSm.copy(color = FaTheme.colors.inkMuted),
    )
}

/**
 * A setting that is on or off. The whole row is the switch: a screen reader reads the title, the
 * description and the state as one element, and a tap anywhere on the row flips it. The toggle
 * is only drawn, and shows the focus ring of the row.
 */
@Composable
private fun ToggleRow(title: String, description: String?, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    SettingRow(
        title,
        description,
        Modifier.toggleable(
            value = checked,
            interactionSource = interaction,
            indication = null,
            role = Role.Switch,
            onValueChange = onCheckedChange,
        ),
    ) {
        FaToggle(checked, onCheckedChange = null, interactionSource = interaction)
    }
}

/** One setting: label in `row` and optional description in `body-sm` on the left, the control on the right. */
@Composable
private fun SettingRow(title: String, description: String?, modifier: Modifier = Modifier, control: @Composable () -> Unit) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FaDimens.space5),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            BasicText(title, style = type.row.copy(color = colors.ink))
            if (description != null) BasicText(description, style = type.bodySm.copy(color = colors.inkMuted))
        }
        control()
    }
}
