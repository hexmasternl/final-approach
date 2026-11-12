package nl.hexmaster.finalapproach.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion
import nl.hexmaster.finalapproach.ui.theme.FaShadows
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Geometry of the Dialog component that is not a token of its own. */
private object FaDialogDefaults {
    /** The dialog drops in from this far above its resting place. */
    val dropDistance = 40.dp
    const val DROP_START_SCALE = 0.9f

    /** Share of the compact card's width taken by the stats pane; the copy and actions get the rest. */
    const val COMPACT_LEAD_WEIGHT = 1f
    const val COMPACT_STATS_WEIGHT = 2f
}

/** One button of a [FaDialog]. The dialog places and sizes it for its layout. */
class FaDialogAction(
    val text: String,
    val onClick: () -> Unit,
    val variant: FaButtonVariant = FaButtonVariant.Primary,
    val icon: FaIcon? = null,
)

/**
 * A modal on a scrim. Everything behind it is dimmed and cannot be touched, but keeps drawing.
 *
 * The dialog picks its layout from the space inside the system bars ([dialogLayoutFor]): the
 * design-system Dialog, or on a short landscape screen its compact variant. [content] can read the
 * layout from [LocalFaDialogLayout].
 *
 * @param title one or two words
 * @param body one sentence
 * @param actions the buttons, primary first
 * @param content optional stats between the body and the buttons; in the compact layout they sit beside them
 * @param below optional full-width content under the dialog, such as an ad banner; sits on the scrim
 */
@Composable
fun FaDialog(
    title: String,
    body: String,
    actions: List<FaDialogAction>,
    modifier: Modifier = Modifier,
    content: (@Composable ColumnScope.() -> Unit)? = null,
    below: @Composable () -> Unit = {},
) {
    val colors = FaTheme.colors
    // With reduced motion the dialog is in place at once.
    val animate = !FaTheme.reduceMotion
    val arrival = remember { Animatable(if (animate) 0f else 1f) }
    LaunchedEffect(Unit) {
        if (animate) arrival.animateTo(1f, tween(FaMotion.durDialogMs, easing = FaMotion.easeBounce))
    }

    Box(
        modifier
            .fillMaxSize()
            .background(colors.scrim)
            // Swallow every touch, so nothing behind the scrim can be operated.
            .pointerInput(Unit) { detectTapGestures { } }
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val layout = dialogLayoutFor(maxWidth, maxHeight)
            val compact = layout == FaDialogLayout.Compact
            val padding = if (compact) FaDimens.space4 else FaDimens.space6
            Column(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(if (compact) FaDimens.space2 else FaDimens.space5),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        Modifier
                            .widthIn(max = if (compact) FaDimens.dialogCompactMaxWidth else FaDimens.dialogMaxWidth)
                            .fillMaxWidth()
                            .graphicsLayer {
                                val progress = arrival.value
                                translationY = (progress - 1f) * FaDialogDefaults.dropDistance.toPx()
                                val scale = FaDialogDefaults.DROP_START_SCALE +
                                    (1f - FaDialogDefaults.DROP_START_SCALE) * progress
                                scaleX = scale
                                scaleY = scale
                                alpha = progress.coerceIn(0f, 1f)
                            }
                            .semantics { paneTitle = title }
                            .faBlock(
                                fill = colors.surface,
                                outline = colors.outline,
                                border = FaDimens.line4,
                                radius = FaDimens.radiusXl,
                                lip = FaShadows.lipLg,
                                softLip = FaShadows.softLip,
                                softLipColor = FaTheme.softLip,
                            )
                            // Last resort: on a screen smaller than common phones, or at a large font
                            // size, the dialog scrolls instead of being cut off.
                            .verticalScroll(rememberScrollState())
                            .padding(start = padding, top = padding, end = padding, bottom = padding + FaShadows.softLip),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CompositionLocalProvider(LocalFaDialogLayout provides layout) {
                            when {
                                !compact -> PortraitDialog(title, body, actions, content)
                                content == null -> CompactDialog(title, body, actions)
                                else -> CompactStatsDialog(title, body, actions, content)
                            }
                        }
                    }
                }
                below()
            }
        }
    }
}

/** The design-system Dialog: copy, stats, then full-width buttons stacked. */
@Composable
private fun PortraitDialog(
    title: String,
    body: String,
    actions: List<FaDialogAction>,
    content: (@Composable ColumnScope.() -> Unit)?,
) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    BasicText(title, style = type.displayLg.copy(color = colors.ink, textAlign = TextAlign.Center))
    BasicText(
        body,
        modifier = Modifier.padding(top = FaDimens.space3),
        style = type.bodyLg.copy(color = colors.inkMuted, textAlign = TextAlign.Center),
    )
    if (content != null) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(FaDimens.space2),
            content = content,
        )
    }
    Column(
        Modifier.fillMaxWidth().padding(top = FaDimens.space5),
        verticalArrangement = Arrangement.spacedBy(FaDimens.space4),
    ) {
        actions.forEach { DialogButton(it, Modifier.fillMaxWidth(), FaButtonSize.Medium) }
    }
}

/** Compact without stats: copy, then the buttons side by side, primary at the start. */
@Composable
private fun CompactDialog(title: String, body: String, actions: List<FaDialogAction>) {
    CompactCopy(title, body)
    Row(
        Modifier.fillMaxWidth().padding(top = FaDimens.space4),
        horizontalArrangement = Arrangement.spacedBy(FaDimens.space3),
    ) {
        actions.forEach { DialogButton(it, Modifier.weight(1f), FaButtonSize.Medium) }
    }
}

/** Compact with stats: copy and stacked small buttons at the start, the stats at the end. */
@Composable
private fun CompactStatsDialog(
    title: String,
    body: String,
    actions: List<FaDialogAction>,
    content: @Composable ColumnScope.() -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FaDimens.space4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(FaDialogDefaults.COMPACT_LEAD_WEIGHT), horizontalAlignment = Alignment.CenterHorizontally) {
            CompactCopy(title, body)
            Column(
                Modifier.fillMaxWidth().padding(top = FaDimens.space3),
                verticalArrangement = Arrangement.spacedBy(FaDimens.space2),
            ) {
                // Small buttons keep the full 48dp touch target.
                actions.forEach { DialogButton(it, Modifier.fillMaxWidth(), FaButtonSize.Small) }
            }
        }
        Column(
            Modifier.weight(FaDialogDefaults.COMPACT_STATS_WEIGHT),
            verticalArrangement = Arrangement.spacedBy(FaDimens.space1),
            content = content,
        )
    }
}

@Composable
private fun CompactCopy(title: String, body: String) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    BasicText(title, style = type.titleLg.copy(color = colors.ink, textAlign = TextAlign.Center))
    BasicText(
        body,
        modifier = Modifier.padding(top = FaDimens.space1),
        style = type.body.copy(color = colors.inkMuted, textAlign = TextAlign.Center),
    )
}

@Composable
private fun DialogButton(action: FaDialogAction, modifier: Modifier, size: FaButtonSize) {
    FaButton(action.text, action.onClick, modifier, action.variant, size, action.icon)
}
