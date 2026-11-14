package nl.hexmaster.finalapproach.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class FaColorsTest {
    private val palettes = listOf(DayColors, NightColors, DawnColors)

    /**
     * Reading a palette loads the generated theme classes. A method that takes more than 255
     * argument slots (a `Color` takes two) is rejected when its class loads, on the JVM and on
     * Android, so a palette that cannot be built fails here instead of at app start.
     */
    @Test
    fun everyPaletteLoadsWithItsColoursSet() {
        for (palette in palettes) {
            assertNotEquals(Color.Unspecified, palette.outline)
            assertNotEquals(Color.Unspecified, palette.blackout)
        }
    }

    @Test
    fun pathReturnsTheLiveryColourOfItsIndex() {
        for (palette in palettes) {
            assertEquals(palette.path1, palette.path(1))
            assertEquals(palette.path2, palette.path(2))
            assertEquals(palette.path3, palette.path(3))
            assertEquals(palette.path4, palette.path(4))
            assertEquals(palette.path5, palette.path(5))
            assertEquals(palette.path6, palette.path(6))
        }
    }
}
