package nl.hexmaster.finalapproach.ui.menu

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class MainMenuArrangementTest {
    @Test
    fun fitsExactly() = assertEquals(MainMenuArrangement.Stacked, chooseMainMenuArrangement(400.dp, 400.dp))

    @Test
    fun fitsWithRoom() = assertEquals(MainMenuArrangement.Stacked, chooseMainMenuArrangement(600.dp, 400.dp))

    @Test
    fun oneDpShort() = assertEquals(MainMenuArrangement.Beside, chooseMainMenuArrangement(399.dp, 400.dp))

    @Test
    fun zeroHeight() = assertEquals(MainMenuArrangement.Beside, chooseMainMenuArrangement(0.dp, 400.dp))
}
