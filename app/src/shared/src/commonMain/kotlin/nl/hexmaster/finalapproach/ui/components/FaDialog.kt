package nl.hexmaster.finalapproach.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion
import nl.hexmaster.finalapproach.ui.theme.FaShadows
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Geometry of the Dialog component that is not a token of its own. */
private object FaDialogDefaults {
    val maxWidth = 400.dp

    /** The dialog drops in from this far above its resting place. */
    val dropDistance = 40.dp
    const val DROP_START_SCALE = 0.9f
}

/**
 * A modal on a scrim. Everything behind it is dimmed and cannot be touched, but keeps drawing.
 *
 * @param title one or two words
 * @param body one sentence
 * @param content optional extra content between the body and the buttons, such as a stats grid
 * @param actions the buttons, primary first
 */
@Composable
fun FaDialog(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {},
    actions: @Composable ColumnScope.() -> Unit,
) {
    val colors = FaTheme.colors
    val type = FaTheme.type
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
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(FaDimens.space5),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .widthIn(max = FaDialogDefaults.maxWidth)
                .fillMaxWidth()
                .graphicsLayer {
                    val progress = arrival.value
                    translationY = (progress - 1f) * FaDialogDefaults.dropDistance.toPx()
                    val scale = FaDialogDefaults.DROP_START_SCALE + (1f - FaDialogDefaults.DROP_START_SCALE) * progress
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
                // A tall dialog on a short landscape screen scrolls instead of being cut off.
                .verticalScroll(rememberScrollState())
                .padding(
                    start = FaDimens.space6,
                    top = FaDimens.space6,
                    end = FaDimens.space6,
                    bottom = FaDimens.space6 + FaShadows.softLip,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BasicText(title, style = type.displayLg.copy(color = colors.ink, textAlign = TextAlign.Center))
            BasicText(
                body,
                modifier = Modifier.padding(top = FaDimens.space3),
                style = type.bodyLg.copy(color = colors.inkMuted, textAlign = TextAlign.Center),
            )
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(FaDimens.space2),
                content = content,
            )
            Column(
                Modifier.fillMaxWidth().padding(top = FaDimens.space5),
                verticalArrangement = Arrangement.spacedBy(FaDimens.space4),
                content = actions,
            )
        }
    }
}
