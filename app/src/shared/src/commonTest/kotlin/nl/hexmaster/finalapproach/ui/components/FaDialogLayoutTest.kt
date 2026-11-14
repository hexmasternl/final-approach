package nl.hexmaster.finalapproach.ui.components

import androidx.compose.ui.unit.dp
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import kotlin.test.Test
import kotlin.test.assertEquals

class FaDialogLayoutTest {
    @Test
    fun portraitPhonesUseThePortraitDialog() {
        assertEquals(FaDialogLayout.Portrait, dialogLayoutFor(360.dp, 592.dp))
        assertEquals(FaDialogLayout.Portrait, dialogLayoutFor(412.dp, 867.dp))
    }

    @Test
    fun smallPhoneInLandscapeIsCompact() {
        // 640×360 with the status bar and the navigation bar.
        assertEquals(FaDialogLayout.Compact, dialogLayoutFor(640.dp, 312.dp))
        assertEquals(FaDialogLayout.Compact, dialogLayoutFor(592.dp, 336.dp))
    }

    @Test
    fun largePhoneInLandscapeIsCompact() {
        // 915×412 with the status bar and the navigation bar.
        assertEquals(FaDialogLayout.Compact, dialogLayoutFor(915.dp, 364.dp))
        assertEquals(FaDialogLayout.Compact, dialogLayoutFor(867.dp, 388.dp))
    }

    @Test
    fun tallLandscapeKeepsThePortraitDialog() {
        assertEquals(FaDialogLayout.Portrait, dialogLayoutFor(1280.dp, 752.dp))
        assertEquals(FaDialogLayout.Portrait, dialogLayoutFor(1000.dp, FaDimens.dialogCompactBreakpoint))
    }

    @Test
    fun justBelowTheBreakpointIsCompact() {
        assertEquals(FaDialogLayout.Compact, dialogLayoutFor(1000.dp, FaDimens.dialogCompactBreakpoint - 1.dp))
    }

    @Test
    fun aSquareSpaceIsNotLandscape() {
        assertEquals(FaDialogLayout.Portrait, dialogLayoutFor(400.dp, 400.dp))
    }

    @Test
    fun buttonsStandSideBySideOnlyOnACompactStubWithStats() {
        assertEquals(FaDialogActionFlow.SideBySide, dialogActionFlowFor(FaDialogLayout.Compact, hasStats = true))
        assertEquals(FaDialogActionFlow.Stacked, dialogActionFlowFor(FaDialogLayout.Compact, hasStats = false))
        assertEquals(FaDialogActionFlow.Stacked, dialogActionFlowFor(FaDialogLayout.Portrait, hasStats = true))
        assertEquals(FaDialogActionFlow.Stacked, dialogActionFlowFor(FaDialogLayout.Portrait, hasStats = false))
    }
}
