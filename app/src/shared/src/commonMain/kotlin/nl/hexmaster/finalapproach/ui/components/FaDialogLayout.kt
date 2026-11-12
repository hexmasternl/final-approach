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

/** The layout of the [FaDialog] around this content, so the content can follow it without measuring. */
val LocalFaDialogLayout = staticCompositionLocalOf { FaDialogLayout.Portrait }
