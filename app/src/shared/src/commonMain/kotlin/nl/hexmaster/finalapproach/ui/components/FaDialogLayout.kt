package nl.hexmaster.finalapproach.ui.components

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import nl.hexmaster.finalapproach.ui.theme.FaDimens

/** How a [FaDialog] is laid out: the design-system Dialog, or its compact variant for a short landscape screen. */
enum class FaDialogLayout { Portrait, Compact }

/**
 * Compact when the space inside the system bars is wider than it is tall and lower than the
 * `dialog-compact-breakpoint` token. The layout follows the measured space, not the device orientation.
 */
fun dialogLayoutFor(availableWidth: Dp, availableHeight: Dp): FaDialogLayout =
    if (availableWidth > availableHeight && availableHeight < FaDimens.dialogCompactBreakpoint) {
        FaDialogLayout.Compact
    } else {
        FaDialogLayout.Portrait
    }

/** How the buttons of a [FaDialog] are placed on its stub. */
enum class FaDialogActionFlow { Stacked, SideBySide }

/**
 * The buttons stand side by side only where height is short and the stub also holds stats: the
 * compact layout with stats. Everywhere else they are stacked, primary on top.
 */
fun dialogActionFlowFor(layout: FaDialogLayout, hasStats: Boolean): FaDialogActionFlow =
    if (layout == FaDialogLayout.Compact && hasStats) FaDialogActionFlow.SideBySide else FaDialogActionFlow.Stacked

/** The layout of the [FaDialog] around this content, so the content can follow it without measuring. */
val LocalFaDialogLayout = staticCompositionLocalOf { FaDialogLayout.Portrait }
