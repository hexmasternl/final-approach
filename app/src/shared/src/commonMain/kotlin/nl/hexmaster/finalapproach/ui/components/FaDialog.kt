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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import nl.hexmaster.finalapproach.ui.sprites.FaIcon
import nl.hexmaster.finalapproach.ui.theme.FaColors
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import nl.hexmaster.finalapproach.ui.theme.FaMotion
import nl.hexmaster.finalapproach.ui.theme.FaOpacity
import nl.hexmaster.finalapproach.ui.theme.FaTheme

/** Geometry of the Dialog component that is not a token of its own. */
private object FaDialogDefaults {
    /** The dialog drops in from this far above its resting place. */
    val dropDistance = 40.dp
    const val DROP_START_SCALE = 0.9f

    /** Share of the compact pass's width left of the perforation: the header and the body. */
    const val COMPACT_LEAD_WEIGHT = 1f

    /** Share of the compact pass's width taken by the stub: the stats and the buttons. */
    const val COMPACT_STUB_WEIGHT = 1.3f

    /** Stats stand in this many columns. */
    const val STAT_COLUMNS = 2
}

/** One button of a [FaDialog]. The dialog places and sizes it for its layout. */
class FaDialogAction(
    val text: String,
    val onClick: () -> Unit,
    val variant: FaButtonVariant = FaButtonVariant.Primary,
    val icon: FaIcon? = null,
)

/**
 * What the header band of a [FaDialog] is tinted for. The title and the body say what happened;
 * the tint only supports them.
 */
enum class FaDialogTone { Neutral, Failure, Success }

/**
 * A modal on a scrim, drawn as a boarding pass: a tinted header band with the [eyebrow] and the
 * [title], the [body], a dashed perforation, and a stub with the stats and the buttons.
 * Everything behind it is dimmed and cannot be touched, but keeps drawing.
 *
 * The dialog picks its layout from the space inside the system bars ([dialogLayoutFor]). On a
 * short landscape screen the perforation is vertical, with the stub on the right. [content] can
 * read the layout from [LocalFaDialogLayout].
 *
 * @param title one or two words
 * @param body one sentence
 * @param actions the buttons, primary first
 * @param eyebrow a few words above the title, shown in capitals
 * @param tone the tint of the header band
 * @param stamp optional two or three words stamped under the body, in the success tone on a
 *   [FaDialogTone.Success] dialog; they repeat the body, so screen readers skip them
 * @param content optional stats on the stub, above the buttons; see [FaDialogStats]
 * @param below optional full-width content under the dialog, such as an ad banner; sits on the scrim
 */
@Composable
fun FaDialog(
    title: String,
    body: String,
    actions: List<FaDialogAction>,
    eyebrow: String,
    modifier: Modifier = Modifier,
    tone: FaDialogTone = FaDialogTone.Neutral,
    stamp: String? = null,
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
                            // The contact shadow stays inside the dialog's own bounds, so it fades in with it.
                            .padding(bottom = FaDimens.contactOffsetOverlay)
                            .faMouldedSurface(
                                tone = colors.tonePaper,
                                outline = colors.outline,
                                radius = FaDimens.radiusDialog,
                                lip = FaDimens.lipDialog,
                                contact = colors.contactOverlay,
                                contactOffset = FaDimens.contactOffsetOverlay,
                            )
                            .padding(
                                start = FaDimens.lineUi,
                                top = FaDimens.lineUi,
                                end = FaDimens.lineUi,
                                bottom = FaDimens.lineUi + FaDimens.lipDialog,
                            )
                            .clip(faFaceShape(FaDimens.radiusDialog, FaDimens.lipDialog))
                            // Last resort: on a screen smaller than common phones, or at a large font
                            // size, the dialog scrolls instead of being cut off.
                            .verticalScroll(rememberScrollState()),
                    ) {
                        CompositionLocalProvider(LocalFaDialogLayout provides layout) {
                            if (compact) {
                                CompactPass(title, body, actions, eyebrow, tone, stamp, content)
                            } else {
                                PortraitPass(title, body, actions, eyebrow, tone, stamp, content)
                            }
                        }
                    }
                }
                below()
            }
        }
    }
}

/** The pass upright: header, body, a horizontal perforation, then the stub with stats and stacked buttons. */
@Composable
private fun PortraitPass(
    title: String,
    body: String,
    actions: List<FaDialogAction>,
    eyebrow: String,
    tone: FaDialogTone,
    stamp: String?,
    content: (@Composable ColumnScope.() -> Unit)?,
) {
    val colors = FaTheme.colors
    PassHeader(eyebrow, title, tone)
    PassBody(body, stamp, tone)
    Box(
        Modifier.fillMaxWidth().height(FaDimens.lineUiSm).drawWithCache {
            val clip = Path()
            onDrawBehind {
                val y = size.height / 2f
                val margin = FaDimens.perforationDot.toPx()
                drawDashedRule(colors.paper500, Offset(margin, y), Offset(size.width - margin, y), size.height)
                drawPunch(Offset(0f, y), colors, clip)
                drawPunch(Offset(size.width, y), colors, clip)
            }
        },
    )
    PassStub(actions, content)
}

/** The pass on its side: header and body on the left, a vertical perforation, the stub on the right. */
@Composable
private fun CompactPass(
    title: String,
    body: String,
    actions: List<FaDialogAction>,
    eyebrow: String,
    tone: FaDialogTone,
    stamp: String?,
    content: (@Composable ColumnScope.() -> Unit)?,
) {
    val colors = FaTheme.colors
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(
            Modifier
                .weight(FaDialogDefaults.COMPACT_LEAD_WEIGHT)
                .fillMaxHeight()
                .drawWithCache {
                    val clip = Path()
                    onDrawWithContent {
                        drawContent()
                        val line = FaDimens.lineUiSm.toPx()
                        val x = size.width - line / 2f
                        val margin = FaDimens.perforationDot.toPx()
                        drawDashedRule(colors.paper500, Offset(x, margin), Offset(x, size.height - margin), line)
                        drawPunch(Offset(x, 0f), colors, clip)
                        drawPunch(Offset(x, size.height), colors, clip)
                    }
                },
        ) {
            PassHeader(eyebrow, title, tone)
            PassBody(body, stamp, tone)
        }
        Column(
            Modifier.weight(FaDialogDefaults.COMPACT_STUB_WEIGHT).fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
        ) {
            PassStub(actions, content)
        }
    }
}

/** The tinted band at the top of the pass: the eyebrow in capitals above the title. */
@Composable
private fun PassHeader(eyebrow: String, title: String, tone: FaDialogTone) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    val compact = LocalFaDialogLayout.current == FaDialogLayout.Compact
    val (fill, rule) = when (tone) {
        FaDialogTone.Neutral -> colors.skyBand to colors.sky500
        FaDialogTone.Failure -> colors.cherry200 to colors.cherry500
        FaDialogTone.Success -> colors.mint200 to colors.mint500
    }
    Column(
        Modifier
            .fillMaxWidth()
            .background(fill)
            .drawBehind {
                val line = FaDimens.lineDetail.toPx()
                drawRect(rule, Offset(0f, size.height - line), Size(size.width, line))
            }
            .padding(
                horizontal = if (compact) FaDimens.space4 else FaDimens.space5,
                vertical = if (compact) FaDimens.space3 else FaDimens.space4,
            ),
        verticalArrangement = Arrangement.spacedBy(FaDimens.space1),
    ) {
        BasicText(eyebrow.uppercase(), style = type.labelSm.copy(color = colors.ink.copy(alpha = FaOpacity.eyebrow)))
        BasicText(title, style = (if (compact) type.dialogTitleCompact else type.dialogTitle).copy(color = colors.ink))
    }
}

/** What happened, in one sentence, with the optional stamp under it. */
@Composable
private fun PassBody(body: String, stamp: String?, tone: FaDialogTone) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    val compact = LocalFaDialogLayout.current == FaDialogLayout.Compact
    Column(
        Modifier.fillMaxWidth().padding(
            horizontal = if (compact) FaDimens.space4 else FaDimens.space5,
            vertical = if (compact) FaDimens.space3 else FaDimens.space4,
        ),
        verticalArrangement = Arrangement.spacedBy(FaDimens.space3),
    ) {
        BasicText(body, style = type.body.copy(color = colors.inkMuted))
        // The stamp repeats the body in fewer words.
        if (stamp != null) {
            val stampTone = if (tone == FaDialogTone.Success) FaStampTone.Success else FaStampTone.Danger
            FaStamp(stamp, Modifier.padding(start = FaDimens.space1).clearAndSetSemantics { }, stampTone)
        }
    }
}

/** The stub of the pass: the stats, then the buttons. */
@Composable
private fun PassStub(actions: List<FaDialogAction>, content: (@Composable ColumnScope.() -> Unit)?) {
    val layout = LocalFaDialogLayout.current
    val compact = layout == FaDialogLayout.Compact
    if (content != null) Column(Modifier.fillMaxWidth(), content = content)
    val padding = Modifier.fillMaxWidth().padding(
        horizontal = if (compact) FaDimens.space3 else FaDimens.space5,
        vertical = if (compact) FaDimens.space2 else FaDimens.space4,
    )
    when (dialogActionFlowFor(layout, hasStats = content != null)) {
        FaDialogActionFlow.SideBySide -> Row(padding, horizontalArrangement = Arrangement.spacedBy(FaDimens.space3)) {
            // Small buttons keep the full 48dp touch target.
            actions.forEach { DialogButton(it, Modifier.weight(1f), FaButtonSize.Small) }
        }

        FaDialogActionFlow.Stacked -> Column(padding, verticalArrangement = Arrangement.spacedBy(FaDimens.space3)) {
            actions.forEach { DialogButton(it, Modifier.fillMaxWidth(), FaButtonSize.Medium) }
        }
    }
}

@Composable
private fun DialogButton(action: FaDialogAction, modifier: Modifier, size: FaButtonSize) {
    FaButton(action.text, action.onClick, modifier, action.variant, size, action.icon)
}

/**
 * A half-hole punched out of the pass where the perforation meets its edge; the face clips the
 * other half. [clip] is a reused path; its content is replaced.
 */
private fun DrawScope.drawPunch(center: Offset, colors: FaColors, clip: Path) {
    val radius = FaDimens.perforationDot.toPx() / 2f
    drawCircle(colors.paper500, radius, center)
    clip.rewind()
    clip.addOval(Rect(center, radius))
    clipPath(clip) {
        drawCircle(colors.paper400, radius, center + Offset(0f, FaDimens.lineUi.toPx()))
    }
}

/**
 * One statistic on the stub of a [FaDialog].
 *
 * @param label what is counted; shown in capitals
 * @param value the count as shown
 * @param leading optional picture before the label, such as the aircraft the count is for
 */
class FaDialogStat(
    val label: String,
    val value: String,
    val leading: (@Composable () -> Unit)? = null,
)

/**
 * The stats of a [FaDialog] as cells in two columns, separated by keylines. Upright, a cell has
 * its label above its value; in the compact layout label and value share one line, so four rows
 * fit a landscape phone.
 */
@Composable
fun FaDialogStats(stats: List<FaDialogStat>, modifier: Modifier = Modifier) {
    val keyline = FaTheme.colors.paper300
    Column(modifier.fillMaxWidth()) {
        stats.chunked(FaDialogDefaults.STAT_COLUMNS).forEach { row ->
            Row(
                Modifier.fillMaxWidth().height(IntrinsicSize.Min).drawBehind {
                    val line = FaDimens.lineDetail.toPx()
                    drawRect(keyline, Offset(0f, size.height - line), Size(size.width, line))
                },
            ) {
                row.forEachIndexed { index, stat ->
                    StatCell(stat, if (index < FaDialogDefaults.STAT_COLUMNS - 1) keyline else Color.Transparent, Modifier.weight(1f).fillMaxHeight())
                }
                repeat(FaDialogDefaults.STAT_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun StatCell(stat: FaDialogStat, endRule: Color, modifier: Modifier) {
    val colors = FaTheme.colors
    val type = FaTheme.type
    val compact = LocalFaDialogLayout.current == FaDialogLayout.Compact
    val label = stat.label.uppercase()
    Row(
        modifier
            .semantics(mergeDescendants = true) { contentDescription = "${stat.label} ${stat.value}" }
            .drawBehind {
                val line = FaDimens.lineDetail.toPx()
                drawRect(endRule, Offset(size.width - line, 0f), Size(line, size.height))
            }
            .padding(
                horizontal = if (compact) FaDimens.space3 else FaDimens.space5,
                vertical = if (compact) FaDimens.space1 else FaDimens.space2,
            ),
        horizontalArrangement = Arrangement.spacedBy(if (compact) FaDimens.space2 else FaDimens.space3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        stat.leading?.invoke()
        if (compact) {
            BasicText(label, Modifier.weight(1f), style = type.labelSm.copy(color = colors.inkMuted))
            BasicText(stat.value, style = type.stripValue.copy(color = colors.ink), maxLines = 1)
        } else {
            Column(Modifier.weight(1f)) {
                BasicText(label, style = type.labelSm.copy(color = colors.inkMuted))
                BasicText(stat.value, style = type.stripValue.copy(color = colors.ink), maxLines = 1)
            }
        }
    }
}
