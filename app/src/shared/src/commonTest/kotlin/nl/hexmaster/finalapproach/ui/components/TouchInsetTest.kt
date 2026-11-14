package nl.hexmaster.finalapproach.ui.components

import androidx.compose.ui.unit.dp
import nl.hexmaster.finalapproach.ui.theme.FaDimens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** A control drawn lower than a touch target still takes touches over a full one. */
class TouchInsetTest {
    @Test
    fun aControlLowerThanATouchTargetGetsHalfTheDifferenceOnEachSide() {
        assertEquals(4.dp, touchInset(FaDimens.buttonSm))
        assertEquals(4.dp, touchInset(FaDimens.stripHeight))
    }

    @Test
    fun aControlThatIsATouchTargetGetsNoInset() {
        assertEquals(0.dp, touchInset(FaDimens.buttonMd))
        assertEquals(0.dp, touchInset(FaDimens.buttonLg))
        assertEquals(0.dp, touchInset(FaDimens.touchMin))
    }

    @Test
    fun everyControlSizeReachesTheMinimumTouchTarget() {
        listOf(FaDimens.buttonSm, FaDimens.buttonMd, FaDimens.buttonLg, FaDimens.stripHeight).forEach { size ->
            assertTrue(size + touchInset(size) * 2 >= FaDimens.touchMin, "$size is smaller than a touch target")
        }
    }
}
